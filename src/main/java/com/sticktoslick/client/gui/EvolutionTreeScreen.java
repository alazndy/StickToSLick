package com.sticktoslick.client.gui;

import com.sticktoslick.data.EvolutionPath;
import com.sticktoslick.data.WeaponClassData;
import com.sticktoslick.data.WeaponNBTHelper;
import com.sticktoslick.item.WeaponItemRegistry;
import com.sticktoslick.network.C2SUpgradeWeaponPacket;
import com.sticktoslick.network.ModMessages;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import java.util.*;

public class EvolutionTreeScreen extends Screen {
    private final Screen parentScreen;
    private final ItemStack currentWeapon;
    private final String currentClass;
    private final int currentLevel;

    // Panning
    private double scrollX = 0;
    private double scrollY = 0;
    private boolean isDragging = false;
    private double lastMouseX = 0;
    private double lastMouseY = 0;

    // Tree Nodes
    private final List<TreeNode> nodes = new ArrayList<>();
    private final List<TreeConnection> connections = new ArrayList<>();

    // Tier labels for the left side
    private static final String[] TIER_LABELS = {
            "Lv.1 Başlangıç", "Lv.5 Temel", "Lv.10 Gelişmiş", "Lv.15 Hibrit",
            "Lv.20 Uzman", "Lv.25 Ağır", "Lv.30 Zirve",
            "Lv.40 Efsane", "Lv.50 Tanrısal"
    };

    // Track which classes the player has passed through (for "completed" state)
    private final Set<String> reachableFromCurrent = new HashSet<>();

    public EvolutionTreeScreen(Screen parent, ItemStack currentWeapon, String currentClass) {
        super(Component.literal("Evrim Ağacı"));
        this.parentScreen = parent;
        this.currentWeapon = currentWeapon;
        this.currentClass = currentClass;
        this.currentLevel = WeaponNBTHelper.getLevel(currentWeapon);

        // Build set of classes reachable from current class (for "available"
        // highlighting)
        buildReachable();
    }

    private void buildReachable() {
        Queue<String> queue = new LinkedList<>();
        queue.add(currentClass);
        reachableFromCurrent.add(currentClass);
        while (!queue.isEmpty()) {
            String cls = queue.poll();
            for (EvolutionPath.Evolution evo : EvolutionPath.getEvolutions(cls)) {
                if (!reachableFromCurrent.contains(evo.targetClass())) {
                    reachableFromCurrent.add(evo.targetClass());
                    queue.add(evo.targetClass());
                }
            }
        }
    }

    @Override
    protected void init() {
        super.init();
        buildTree();

        // Center the view on the current weapon node
        for (TreeNode node : nodes) {
            if (node.weaponClass.equals(currentClass)) {
                scrollX = this.width / 2.0 - node.x;
                scrollY = this.height / 2.0 - node.y;
                break;
            }
        }

        // Add Back Button
        this.addRenderableWidget(
                net.minecraft.client.gui.components.Button.builder(Component.literal("⬅ Geri Dön"), b -> {
                    Minecraft.getInstance().setScreen(parentScreen);
                }).bounds(10, 10, 80, 20).build());
    }

    private void buildTree() {
        nodes.clear();
        connections.clear();

        Map<String, Integer> weaponDistances = new HashMap<>();
        Map<String, TreeNode> createdNodes = new HashMap<>();

        Queue<String> queue = new LinkedList<>();
        queue.add("weapon_wooden_stick");
        weaponDistances.put("weapon_wooden_stick", 0);

        // BFS to find all nodes and their layers
        while (!queue.isEmpty()) {
            String current = queue.poll();
            int currentDist = weaponDistances.get(current);

            List<EvolutionPath.Evolution> evolutions = EvolutionPath.getEvolutions(current);
            for (EvolutionPath.Evolution evo : evolutions) {
                if (!weaponDistances.containsKey(evo.targetClass())) {
                    weaponDistances.put(evo.targetClass(), currentDist + 1);
                    queue.add(evo.targetClass());
                }
            }
        }

        // Assign positions
        Map<Integer, List<String>> layers = new HashMap<>();
        int maxLayer = 0;
        for (Map.Entry<String, Integer> entry : weaponDistances.entrySet()) {
            layers.computeIfAbsent(entry.getValue(), k -> new ArrayList<>()).add(entry.getKey());
            maxLayer = Math.max(maxLayer, entry.getValue());
        }

        int xOffset = 160;
        int ySpacing = 70;
        int startX = 80;

        for (int i = 0; i <= maxLayer; i++) {
            List<String> layerNodes = layers.getOrDefault(i, Collections.emptyList());
            layerNodes.sort(String::compareTo);

            int totalHeight = (layerNodes.size() - 1) * ySpacing;
            int startY = -(totalHeight / 2);

            for (int j = 0; j < layerNodes.size(); j++) {
                String weaponClass = layerNodes.get(j);
                int nx = startX + (i * xOffset);
                int ny = startY + (j * ySpacing);

                // Use our actual weapon items for display!
                ItemStack displayItem = new ItemStack(WeaponItemRegistry.getItem(weaponClass));
                TreeNode node = new TreeNode(weaponClass, nx, ny, displayItem, getNodeState(weaponClass));
                nodes.add(node);
                createdNodes.put(weaponClass, node);
            }
        }

        // Build Connections
        for (TreeNode parentNode : nodes) {
            List<EvolutionPath.Evolution> evolutions = EvolutionPath.getEvolutions(parentNode.weaponClass);
            for (EvolutionPath.Evolution evo : evolutions) {
                TreeNode childNode = createdNodes.get(evo.targetClass());
                if (childNode != null) {
                    connections.add(new TreeConnection(parentNode, childNode, new ItemStack(evo.catalyst())));
                }
            }
        }
    }

    private NodeState getNodeState(String weaponClass) {
        if (weaponClass.equals(currentClass))
            return NodeState.CURRENT;
        // Check if this is a direct evolution from the current class that the player
        // can do right now
        List<EvolutionPath.Evolution> evos = EvolutionPath.getEvolutions(currentClass);
        for (EvolutionPath.Evolution evo : evos) {
            if (evo.targetClass().equals(weaponClass) && currentLevel >= evo.requiredLevel()) {
                return NodeState.AVAILABLE;
            }
        }
        // Check if the player has already passed this node (it's before the current
        // class in the tree)
        if (!reachableFromCurrent.contains(weaponClass)) {
            return NodeState.COMPLETED; // Already passed through this branch
        }
        return NodeState.LOCKED;
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        // Dark gradient background
        g.fillGradient(0, 0, this.width, this.height, 0xCC0A0A1A, 0xEE050510);

        g.pose().pushPose();
        g.pose().translate(scrollX, scrollY, 0);

        // 1. Draw Connections
        for (TreeConnection conn : connections) {
            drawConnection(g, conn);
        }

        // 2. Draw Nodes
        TreeNode hoveredNode = null;
        for (TreeNode node : nodes) {
            drawNode(g, node);

            // Hitbox for tooltip
            int nx = (int) (node.x + scrollX);
            int ny = (int) (node.y + scrollY);
            if (mouseX >= nx - 16 && mouseX <= nx + 16 && mouseY >= ny - 16 && mouseY <= ny + 16) {
                hoveredNode = node;
            }
        }

        // 3. Draw tier labels on the left
        for (int i = 0; i < TIER_LABELS.length && i <= 8; i++) {
            int labelX = 80 + (i * 160) - 20;
            int labelY = -140;
            g.drawString(this.font, "§7" + TIER_LABELS[i], labelX, labelY, 0x888888, false);
        }

        g.pose().popPose();

        // 4. Title Overlay
        g.fillGradient(0, 0, this.width, 50, 0xDD0A0A1A, 0x000A0A1A);
        g.drawCenteredString(this.font, "§6§l⚔ SİLAH EVRİM AĞACI ⚔", this.width / 2, 8, 0xFFFFFF);
        g.drawCenteredString(this.font, "§7Sürükle: Kaydır | Evrimleşebilen silaha tıkla", this.width / 2, 22,
                0xAAAAAA);

        // Legend
        int legendY = this.height - 25;
        g.drawString(this.font, "§a■ Şu Anki", 10, legendY, 0x00FF00, false);
        g.drawString(this.font, "§e■ Evrimleşebilir", 80, legendY, 0xFFFF00, false);
        g.drawString(this.font, "§8■ Kilitli", 180, legendY, 0x888888, false);

        // 5. Tooltip
        if (hoveredNode != null) {
            renderNodeTooltip(g, hoveredNode, mouseX, mouseY);
        }

        super.render(g, mouseX, mouseY, partialTick);
    }

    private void drawConnection(GuiGraphics g, TreeConnection conn) {
        int parentState = getConnectionColor(conn.parent.state, conn.child.state);

        // Smooth bezier-style: horizontal out, vertical, horizontal in
        int midX = (conn.parent.x + conn.child.x) / 2;

        g.hLine(conn.parent.x + 16, midX, conn.parent.y, parentState);
        g.vLine(midX, Math.min(conn.parent.y, conn.child.y), Math.max(conn.parent.y, conn.child.y), parentState);
        g.hLine(midX, conn.child.x - 16, conn.child.y, parentState);

        // Draw catalyst icon at the midpoint
        int iconX = midX;
        int iconY = (conn.parent.y + conn.child.y) / 2;

        g.pose().pushPose();
        g.pose().translate(iconX, iconY, 10);
        g.pose().scale(0.5f, 0.5f, 0.5f);
        g.renderFakeItem(conn.catalyst, -8, -8);
        g.pose().popPose();
    }

    private int getConnectionColor(NodeState from, NodeState to) {
        if (from == NodeState.CURRENT && to == NodeState.AVAILABLE)
            return 0xFFFFFF00; // Yellow
        if (from == NodeState.CURRENT || to == NodeState.CURRENT)
            return 0xFF00FF00; // Green
        if (from == NodeState.COMPLETED || to == NodeState.COMPLETED)
            return 0xFF666666; // Gray
        return 0xFF444444;
    }

    private void drawNode(GuiGraphics g, TreeNode node) {
        int bgColor, borderColor;
        float scale = 1.5f;

        switch (node.state) {
            case CURRENT -> {
                bgColor = 0xCC003300;
                borderColor = 0xFF00FF00;
                scale = 2.0f;
            }
            case AVAILABLE -> {
                bgColor = 0xCC333300;
                borderColor = 0xFFFFFF00;
                // Pulse animation for available nodes
                float pulse = (float) Math.sin(System.currentTimeMillis() / 500.0) * 0.15f;
                scale = 1.8f + pulse;
            }
            case COMPLETED -> {
                bgColor = 0xCC222222;
                borderColor = 0xFF666666;
            }
            default -> { // LOCKED
                bgColor = 0xCC111111;
                borderColor = 0xFF333333;
            }
        }

        // Background
        g.fill(node.x - 16, node.y - 16, node.x + 16, node.y + 16, bgColor);
        // Border
        g.renderOutline(node.x - 16, node.y - 16, 32, 32, borderColor);

        // Render item
        g.pose().pushPose();
        g.pose().translate(node.x, node.y, 100);
        g.pose().scale(scale, scale, 1.0f);
        g.renderFakeItem(node.displayItem, -8, -8);
        g.pose().popPose();

        // Draw weapon name below
        String shortName = formatShortName(node.weaponClass);
        int nameColor = node.state == NodeState.LOCKED ? 0x555555 : 0xCCCCCC;
        g.drawCenteredString(this.font, shortName, node.x, node.y + 20, nameColor);
    }

    private String formatShortName(String weaponClass) {
        String name = weaponClass.replace("weapon_", "");
        String[] parts = name.split("_");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (!part.isEmpty()) {
                sb.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1)).append(" ");
            }
        }
        return sb.toString().trim();
    }

    private void renderNodeTooltip(GuiGraphics g, TreeNode node, int mouseX, int mouseY) {
        WeaponClassData.WeaponStats stats = WeaponClassData.get(node.weaponClass);
        List<Component> tooltip = new ArrayList<>();

        tooltip.add(Component.translatable(stats.displayNameKey()).withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));

        // State indicator
        switch (node.state) {
            case CURRENT -> tooltip.add(Component.literal("★ Şu Anki Silahın").withStyle(ChatFormatting.GREEN));
            case AVAILABLE ->
                tooltip.add(Component.literal("⚡ Evrimleşebilir! (Tıkla)").withStyle(ChatFormatting.YELLOW));
            case LOCKED -> tooltip.add(Component.literal("🔒 Kilitli").withStyle(ChatFormatting.DARK_GRAY));
            case COMPLETED -> tooltip.add(Component.literal("✓ Geçilmiş").withStyle(ChatFormatting.GRAY));
        }

        tooltip.add(Component.empty());
        tooltip.add(Component.literal("⚔ Hasar: ").withStyle(ChatFormatting.BLUE)
                .append(Component.literal(String.valueOf(stats.baseDamage())).withStyle(ChatFormatting.WHITE)));
        tooltip.add(Component.literal("⚡ Hız: ").withStyle(ChatFormatting.RED)
                .append(Component.literal(String.valueOf(stats.baseAttackSpeed())).withStyle(ChatFormatting.WHITE)));
        tooltip.add(Component.literal("❤ Dayanıklılık: ").withStyle(ChatFormatting.DARK_RED)
                .append(Component.literal(String.valueOf(stats.maxDurability())).withStyle(ChatFormatting.WHITE)));
        tooltip.add(Component.literal("✧ Büyü Slotu: ").withStyle(ChatFormatting.LIGHT_PURPLE)
                .append(Component.literal(String.valueOf(stats.enchantSlots())).withStyle(ChatFormatting.WHITE)));

        // Show required catalyst if available
        if (node.state == NodeState.AVAILABLE) {
            EvolutionPath.getEvolutions(currentClass).stream()
                    .filter(e -> e.targetClass().equals(node.weaponClass))
                    .findFirst()
                    .ifPresent(evo -> {
                        tooltip.add(Component.empty());
                        tooltip.add(Component.literal("Gerekli: ").withStyle(ChatFormatting.GRAY)
                                .append(evo.catalyst().getDescription().copy().withStyle(ChatFormatting.AQUA)));
                    });
        }

        g.renderTooltip(this.font, tooltip, java.util.Optional.empty(), mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            // Check if clicked on an AVAILABLE node
            for (TreeNode node : nodes) {
                int nx = (int) (node.x + scrollX);
                int ny = (int) (node.y + scrollY);
                if (mouseX >= nx - 16 && mouseX <= nx + 16 && mouseY >= ny - 16 && mouseY <= ny + 16) {
                    if (node.state == NodeState.AVAILABLE) {
                        // Send evolution packet!
                        ModMessages.sendToServer(new C2SUpgradeWeaponPacket("evolve", node.weaponClass));
                        this.onClose();
                        return true;
                    }
                }
            }

            isDragging = true;
            lastMouseX = mouseX;
            lastMouseY = mouseY;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            isDragging = false;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (isDragging) {
            scrollX += (mouseX - lastMouseX);
            scrollY += (mouseY - lastMouseY);
            lastMouseX = mouseX;
            lastMouseY = mouseY;
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private enum NodeState {
        CURRENT, AVAILABLE, LOCKED, COMPLETED
    }

    private static class TreeNode {
        String weaponClass;
        int x, y;
        ItemStack displayItem;
        NodeState state;

        TreeNode(String weaponClass, int x, int y, ItemStack displayItem, NodeState state) {
            this.weaponClass = weaponClass;
            this.x = x;
            this.y = y;
            this.displayItem = displayItem;
            this.state = state;
        }
    }

    private static class TreeConnection {
        TreeNode parent;
        TreeNode child;
        ItemStack catalyst;

        TreeConnection(TreeNode parent, TreeNode child, ItemStack catalyst) {
            this.parent = parent;
            this.child = child;
            this.catalyst = catalyst;
        }
    }
}
