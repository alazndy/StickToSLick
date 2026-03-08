package com.sticktoslick.client.gui;

import com.sticktoslick.data.EvolutionPath;
import com.sticktoslick.data.WeaponClassData;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import java.util.*;

public class EvolutionTreeScreen extends Screen {
    private final Screen parentScreen;
    private final ItemStack currentWeapon;
    private final String currentClass;

    // Panning
    private double scrollX = 0;
    private double scrollY = 0;
    private boolean isDragging = false;
    private double lastMouseX = 0;
    private double lastMouseY = 0;

    // Tree Nodes
    private final List<TreeNode> nodes = new ArrayList<>();
    private final List<TreeConnection> connections = new ArrayList<>();

    public EvolutionTreeScreen(Screen parent, ItemStack currentWeapon, String currentClass) {
        super(Component.literal("Evrim Ağacı"));
        this.parentScreen = parent;
        this.currentWeapon = currentWeapon;
        this.currentClass = currentClass;
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

        // 1. Group all reachable weapons by their shortest distance from root
        Map<String, Integer> weaponDistances = new HashMap<>();
        Map<String, TreeNode> createdNodes = new HashMap<>();

        Queue<String> queue = new LinkedList<>();
        queue.add("weapon_wooden_stick");
        weaponDistances.put("weapon_wooden_stick", 0);

        // BFS to find all nodes and their layers (distances)
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

        // 2. Assign positions based on layer (X) and index within layer (Y)
        Map<Integer, List<String>> layers = new HashMap<>();
        int maxLayer = 0;
        for (Map.Entry<String, Integer> entry : weaponDistances.entrySet()) {
            layers.computeIfAbsent(entry.getValue(), k -> new ArrayList<>()).add(entry.getKey());
            maxLayer = Math.max(maxLayer, entry.getValue());
        }

        int xOffset = 140; // Horizontal distance between tiers
        int ySpacing = 60; // Vertical distance between weapons in same tier
        int startX = 50;

        // Sort items within layers for consistent ordering (alphabetical or by internal
        // logic)
        for (int i = 0; i <= maxLayer; i++) {
            List<String> layerNodes = layers.getOrDefault(i, Collections.emptyList());
            layerNodes.sort(String::compareTo); // Sort alphabetically to maintain consistent Y axis

            int totalHeight = (layerNodes.size() - 1) * ySpacing;
            int startY = -(totalHeight / 2);

            for (int j = 0; j < layerNodes.size(); j++) {
                String weaponClass = layerNodes.get(j);
                int nx = startX + (i * xOffset);
                int ny = startY + (j * ySpacing);

                TreeNode node = new TreeNode(weaponClass, nx, ny, new ItemStack(getMockItem(weaponClass)));
                nodes.add(node);
                createdNodes.put(weaponClass, node);
            }
        }

        // 3. Build Connections between pre-existing nodes
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

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        this.renderDirtBackground(g); // Use vanilla dark dirt background as base

        g.pose().pushPose();
        g.pose().translate(scrollX, scrollY, 0);

        // 1. Draw Connections
        for (TreeConnection conn : connections) {
            drawConnection(g, conn);
        }

        // 2. Draw Nodes
        TreeNode hoveredNode = null;
        for (TreeNode node : nodes) {
            boolean isCurrent = node.weaponClass.equals(currentClass);
            drawNode(g, node, isCurrent);

            // Hitbox for tooltip
            int nx = (int) (node.x + scrollX);
            int ny = (int) (node.y + scrollY);
            if (mouseX >= nx - 12 && mouseX <= nx + 12 && mouseY >= ny - 12 && mouseY <= ny + 12) {
                hoveredNode = node;
            }
        }

        g.pose().popPose();

        // 3. Draw Title Overlay
        g.drawCenteredString(this.font, "§6§lSİLAH EVRİM AĞACI", this.width / 2, 10, 0xFFFFFF);
        g.drawCenteredString(this.font, "§7Kaydırmak için sürükle", this.width / 2, 25, 0xAAAAAA);

        // 4. Draw Tooltip Action
        if (hoveredNode != null) {
            renderNodeTooltip(g, hoveredNode, mouseX, mouseY);
        }

        super.render(g, mouseX, mouseY, partialTick);
    }

    private void drawConnection(GuiGraphics g, TreeConnection conn) {
        int color = 0xFFAAAAAA; // Gray line

        // Draw zigzag line (Horizontal -> Vertical -> Horizontal)
        int midX1 = conn.parent.x + 40;

        g.hLine(conn.parent.x, midX1, conn.parent.y, color); // Out from parent
        g.vLine(midX1, conn.parent.y, conn.child.y, color); // Vertical shift
        g.hLine(midX1, conn.child.x, conn.child.y, color); // In to child

        // Draw catalyst icon midway along the vertical segment for visibility
        int iconX = midX1;
        int iconY = (conn.parent.y + conn.child.y) / 2;

        g.pose().pushPose();
        g.pose().translate(iconX, iconY, 10);
        g.pose().scale(0.5f, 0.5f, 0.5f);
        g.renderFakeItem(conn.catalyst, -8, -8);
        g.pose().popPose();
    }

    private void drawNode(GuiGraphics g, TreeNode node, boolean isCurrent) {
        int color = isCurrent ? 0xFF00FF00 : 0xFFFFFFFF; // Green if current
        // Draw border/bg
        g.fill(node.x - 12, node.y - 12, node.x + 12, node.y + 12, 0xAA000000); // Black bg
        if (isCurrent) {
            g.renderOutline(node.x - 12, node.y - 12, 24, 24, color);
        }

        // Draw Item
        g.renderFakeItem(node.displayItem, node.x - 8, node.y - 8);
    }

    private void renderNodeTooltip(GuiGraphics g, TreeNode node, int mouseX, int mouseY) {
        WeaponClassData.WeaponStats stats = WeaponClassData.get(node.weaponClass);
        List<Component> tooltip = new ArrayList<>();

        tooltip.add(Component.translatable(stats.displayNameKey()).withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
        tooltip.add(Component.literal("Sınıf: " + node.weaponClass).withStyle(ChatFormatting.DARK_GRAY));
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("⚔ Hasar: ").withStyle(ChatFormatting.BLUE)
                .append(Component.literal(String.valueOf(stats.baseDamage())).withStyle(ChatFormatting.WHITE)));
        tooltip.add(Component.literal("⚡ Hız: ").withStyle(ChatFormatting.RED)
                .append(Component.literal(String.valueOf(stats.baseAttackSpeed())).withStyle(ChatFormatting.WHITE)));
        tooltip.add(Component.literal("❤ Dayanıklılık: ").withStyle(ChatFormatting.DARK_RED)
                .append(Component.literal(String.valueOf(stats.maxDurability())).withStyle(ChatFormatting.WHITE)));
        tooltip.add(Component.literal("✧ Büyü Kapasitesi: ").withStyle(ChatFormatting.LIGHT_PURPLE)
                .append(Component.literal(String.valueOf(stats.enchantSlots())).withStyle(ChatFormatting.WHITE)));

        g.renderTooltip(this.font, tooltip, java.util.Optional.empty(), mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
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

    private static net.minecraft.world.item.Item getMockItem(String weaponClass) {
        return switch (weaponClass) {
            // Level 1: Root
            case "weapon_wooden_stick" -> Items.STICK;

            // Level 5: Base Branches
            case "weapon_dagger" -> Items.FLINT;
            case "weapon_shortsword" -> Items.IRON_SWORD;
            case "weapon_spear" -> Items.STICK;
            case "weapon_club" -> Items.OAK_LOG;

            // Level 10: Tier 1
            case "weapon_dirk" -> Items.IRON_NUGGET;
            case "weapon_arming_sword" -> Items.IRON_SWORD;
            case "weapon_trident" -> Items.TRIDENT;
            case "weapon_mace" -> Items.IRON_BLOCK;

            // Level 15: First Hybrids
            case "weapon_saber", "weapon_longsword" -> Items.GOLDEN_SWORD;
            case "weapon_lucerne_hammer" -> Items.IRON_HOE;
            case "weapon_morning_star" -> Items.CHAIN;

            // Level 20: Specialization
            case "weapon_katana" -> Items.DIAMOND_SWORD;
            case "weapon_bastard_sword" -> Items.DIAMOND_SWORD;
            case "weapon_halberd" -> Items.IRON_AXE;
            case "weapon_warhammer" -> Items.ANVIL;

            // Level 25: Heavy Hybrids
            case "weapon_nodachi", "weapon_claymore" -> Items.NETHERITE_SWORD;
            case "weapon_partisan" -> Items.DIAMOND_HOE;
            case "weapon_great_maul" -> Items.NETHERITE_BLOCK;

            // Level 30: Historical Peak
            case "weapon_zweihander" -> Items.NETHERITE_SWORD;
            case "weapon_winged_lance" -> Items.FEATHER;
            case "weapon_executioners_axe" -> Items.NETHERITE_AXE;

            // Level 40: Mythological
            case "weapon_dragon_slayer" -> Items.MAGMA_BLOCK;
            case "weapon_gungnir" -> Items.LIGHTNING_ROD;
            case "weapon_void_crusher" -> Items.CRYING_OBSIDIAN;

            // Level 50: Godly Finales
            case "weapon_genesis" -> Items.BEACON;
            case "weapon_longinus" -> Items.END_ROD;
            case "weapon_atlas" -> Items.BEDROCK;

            default -> Items.STICK;
        };
    }

    private static class TreeNode {
        String weaponClass;
        int x, y;
        ItemStack displayItem;

        TreeNode(String weaponClass, int x, int y, ItemStack displayItem) {
            this.weaponClass = weaponClass;
            this.x = x;
            this.y = y;
            this.displayItem = displayItem;
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
