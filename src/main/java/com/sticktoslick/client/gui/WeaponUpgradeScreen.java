package com.sticktoslick.client.gui;

import com.sticktoslick.data.*;
import com.sticktoslick.network.C2SUpgradeWeaponPacket;
import com.sticktoslick.network.ModMessages;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.client.Minecraft;
import java.util.List;
import java.util.ArrayList;

public class WeaponUpgradeScreen extends Screen {
    private final ItemStack weaponStack;
    private final int level;
    private final String weaponClass;
    private final WeaponClassData.WeaponStats stats;
    private final WeaponTraitData.TraitInfo trait;

    // Animation state
    private boolean animating = false;
    private float animationProgress = 0f;
    private ItemStack animatingItem = ItemStack.EMPTY;
    private String pendingUpgrade = "";
    private String pendingExtraData = "";
    private int startX, startY;

    public WeaponUpgradeScreen(ItemStack stack) {
        super(Component.literal("Weapon Upgrades"));
        this.weaponStack = stack;
        this.level = WeaponNBTHelper.getLevel(stack);
        this.weaponClass = WeaponNBTHelper.getWeaponClass(stack);
        this.stats = WeaponClassData.get(weaponClass);
        this.trait = WeaponTraitData.get(weaponClass);
    }

    @Override
    protected void init() {
        int cx = this.width / 2;
        int cy = this.height / 2;

        // --- Layout Parameters ---
        int upgradeBtnX = cx + 55;
        int upgradeBtnY = cy + 5;
        int btnSpacing = 22;

        // 1. Upgrade Buttons (Right Column)
        this.addRenderableWidget(Button.builder(Component.literal("⚔ +Damage"),
                b -> startAnimation("damage", "", Items.LAPIS_LAZULI, b.getX(), b.getY()))
                .bounds(upgradeBtnX, upgradeBtnY, 100, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("⚡ +Speed"),
                b -> startAnimation("speed", "", Items.REDSTONE, b.getX(), b.getY()))
                .bounds(upgradeBtnX, upgradeBtnY + btnSpacing, 100, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("👟 +Move"),
                b -> startAnimation("move_speed", "", Items.FEATHER, b.getX(), b.getY()))
                .bounds(upgradeBtnX, upgradeBtnY + btnSpacing * 2, 100, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("🛡 +Knock"),
                b -> startAnimation("knockback", "", Items.PISTON, b.getX(), b.getY()))
                .bounds(upgradeBtnX, upgradeBtnY + btnSpacing * 3, 100, 20).build());

        // 2. Evolution Tree Button (Top Center)
        this.addRenderableWidget(Button.builder(Component.literal("🌳 EVOLUTION TREE"), b -> {
            Minecraft.getInstance().setScreen(new EvolutionTreeScreen(this, weaponStack, weaponClass));
        }).bounds(cx - 75, cy - 110, 150, 20).build());

        // 3. Evolution Buttons (Bottom Center)
        if (EvolutionPath.hasPendingEvolution(weaponClass, level)) {
            List<EvolutionPath.Evolution> evolutions = EvolutionPath.getEvolutions(weaponClass);
            int evoY = cy + 115;
            for (int i = 0; i < evolutions.size(); i++) {
                EvolutionPath.Evolution evo = evolutions.get(i);
                if (level >= evo.requiredLevel()) {
                    this.addRenderableWidget(Button
                            .builder(Component.literal("★ EVOLVE: " + evo.targetClass().toUpperCase()),
                                    b -> startAnimation("evolve", evo.targetClass(), evo.catalyst(), b.getX(),
                                            b.getY()))
                            .bounds(cx - 75, evoY + (i * 22), 150, 20).build());
                }
            }
        }
    }

    private void startAnimation(String type, String extraData, net.minecraft.world.item.Item item, int bx, int by) {
        if (animating)
            return;
        this.pendingUpgrade = type;
        this.pendingExtraData = extraData;
        this.animatingItem = new ItemStack(item);
        this.startX = bx + 50;
        this.startY = by + 10;
        this.animating = true;
        this.animationProgress = 0f;
    }

    private void sendUpgrade() {
        ModMessages.sendToServer(new C2SUpgradeWeaponPacket(pendingUpgrade, pendingExtraData));
        this.onClose();
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
        renderGlassBackground(g);
        super.render(g, mouseX, mouseY, delta);

        int cx = this.width / 2;
        int cy = this.height / 2;

        // 1. BOBBING SHOWCASE
        g.pose().pushPose();
        float bob = Mth.sin((System.currentTimeMillis() % 2000) / 1000f * Mth.PI) * 2.5f;
        g.pose().translate(cx, cy - 65 + bob, 200);

        float pulse = Mth.sin((System.currentTimeMillis() % 2000) / 1000f * Mth.PI) * 0.05f;
        g.pose().scale(6.0f + pulse, 6.0f + pulse, 6.0f + pulse);

        g.pose().translate(-8.0, -8.0, 0.0);

        g.renderFakeItem(weaponStack, 0, 0);
        g.pose().popPose();

        // 2. Headings
        WeaponTierData tier = WeaponTierData.getFromLevel(level);
        g.drawCenteredString(this.font, "§l" + tier.name.toUpperCase(), cx, cy - 135, tier.color);
        g.drawCenteredString(this.font, Component.translatable(stats.displayNameKey()).withStyle(ChatFormatting.AQUA),
                cx, cy - 124, 0xFFFFFF);

        // 3. Stats (Left Column)
        int sx = cx - 155;
        int sy = cy + 10;
        int bDmg = WeaponNBTHelper.getStatDamage(weaponStack);
        int bSpd = WeaponNBTHelper.getStatAttackSpeed(weaponStack);
        int bMov = WeaponNBTHelper.getStatMoveSpeed(weaponStack);
        int bKnk = WeaponNBTHelper.getStatKnockback(weaponStack);
        int availablePoints = WeaponNBTHelper.getAvailableStatPoints(weaponStack);

        g.drawString(this.font, "§b§l⚡ Nitelikler", sx, sy - 12, 0xFFFFFF, false);
        g.drawString(this.font, "Puan: " + availablePoints, sx + 70, sy - 12, 0xFFAA00, false);

        drawStat(g, "Hasar:", "§9+" + f(stats.baseDamage() + bDmg * 0.5f), sx, sy);
        drawStat(g, "Hız:", "§c+" + f(stats.baseAttackSpeed() + bSpd * 0.05f), sx, sy + 15);
        drawStat(g, "Hareket:", "§f+" + bMov + "%", sx, sy + 30);
        drawStat(g, "İtme:", "§a+" + bKnk, sx, sy + 45);

        // 4. Trait Description
        if (trait != null) {
            g.drawString(this.font, "§6§l✦ Trait: " + trait.traitName(), sx, sy + 65, 0xFFFFFF);
            String desc = trait.traitDesc();
            List<String> traitLines = wrapText(desc, 140);
            for (int i = 0; i < traitLines.size(); i++) {
                g.drawString(this.font, "§8" + traitLines.get(i), sx, sy + 76 + (i * 10), 0xAAAAAA);
            }
        }

        // 5. Active Quest Info (Middle-Bottom)
        if (WeaponNBTHelper.hasActiveQuest(weaponStack)) {
            int qy = cy + 60;
            String qDesc = EnchantmentQuestManager.getQuestDescription(
                    WeaponNBTHelper.getQuestType(weaponStack), WeaponNBTHelper.getQuestGoal(weaponStack));

            g.drawCenteredString(this.font, "§6§l📜 GÖREV", cx, qy, 0xFFFFFF);
            g.drawCenteredString(this.font, "§e" + qDesc, cx, qy + 11, 0xFFFFFF);

            int goal = WeaponNBTHelper.getQuestGoal(weaponStack);
            int prog = WeaponNBTHelper.getQuestProgress(weaponStack);
            float qProgress = (float) prog / goal;

            int qBarW = 100;
            int qBarX = cx - qBarW / 2;
            int qBarY = qy + 24;
            g.fill(qBarX - 1, qBarY - 1, qBarX + qBarW + 1, qBarY + 5, 0xAA000000);
            g.fill(qBarX, qBarY, qBarX + qBarW, qBarY + 4, 0x22FFFFFF);
            g.fill(qBarX, qBarY, qBarX + (int) (qBarW * qProgress), qBarY + 4, 0xFFFFAA00);
            g.drawCenteredString(this.font, prog + " / " + goal, cx, qBarY + 6, 0xCCCCCC);
        }

        // 6. XP Bar
        int curXP = WeaponNBTHelper.getXP(weaponStack);
        int nextXP = WeaponLevelConfig.getXPForNextLevel(level);
        float xpProg = level >= 30 ? 1.0f : (float) curXP / nextXP;
        int barW = 300;
        int barX = cx - barW / 2;
        int barY = cy + 105;

        g.fill(barX - 1, barY - 1, barX + barW + 1, barY + 5, 0xAA000000);
        g.fill(barX, barY, barX + barW, barY + 4, 0x11FFFFFF);
        g.fill(barX, barY, barX + (int) (barW * xpProg), barY + 4, tier.color | 0xFF000000);
        g.drawCenteredString(this.font, curXP + " / " + nextXP + " XP", cx, barY - 10, 0xCCCCCC);

        // 7. Upgrade Animation
        if (animating) {
            animationProgress += 0.05f;
            if (animationProgress >= 1.0f) {
                animating = false;
                sendUpgrade();
            } else {
                int ax = (int) Mth.lerp(animationProgress, startX, cx);
                int ay = (int) Mth.lerp(animationProgress, startY, cy - 65);
                g.pose().pushPose();
                g.pose().translate(ax, ay, 200);
                float s = 1.0f + (float) Math.sin(animationProgress * Math.PI) * 0.4f;
                g.pose().scale(s, s, 1.0f);
                g.renderFakeItem(animatingItem, -8, -8);
                g.pose().popPose();
            }
        }
    }

    private void renderGlassBackground(GuiGraphics g) {
        int w = 340;
        int h = 320;
        int cx = this.width / 2;
        int cy = this.height / 2;
        int x = cx - w / 2;
        int y = cy - h / 2;

        g.fillGradient(0, 0, this.width, this.height, 0xAA000000, 0xDD000000);
        g.fillGradient(x, y, x + w, y + h, 0x22FFFFFF, 0x11FFFFFF);
        g.fill(x, y, x + w, y + 1, 0x33FFFFFF);
        g.fill(x, y + h - 1, x + w, y + h, 0x33FFFFFF);
        g.fill(x, y, x + 1, y + h, 0x11FFFFFF);
        g.fill(x + w - 1, y, x + w, y + h, 0x11FFFFFF);
        g.fill(x + 10, cy - 40, x + w - 10, cy - 39, 0x11FFFFFF);
    }

    private void drawStat(GuiGraphics g, String label, String value, int x, int y) {
        g.drawString(this.font, label, x, y, 0xAAAAAA, false);
        g.drawString(this.font, value, x + 70, y, 0xFFFFFF, true);
    }

    private List<String> wrapText(String text, int width) {
        List<String> result = new ArrayList<>();
        String[] words = text.split(" ");
        String currentLine = "";
        for (String word : words) {
            if (this.font.width(currentLine + " " + word) < width) {
                currentLine += (currentLine.isEmpty() ? "" : " ") + word;
            } else {
                result.add(currentLine);
                currentLine = word;
            }
        }
        if (!currentLine.isEmpty())
            result.add(currentLine);
        return result;
    }

    private String f(double d) {
        return String.format("%.1f", d);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
