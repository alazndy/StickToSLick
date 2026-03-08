package com.sticktoslick.client.gui;

import com.sticktoslick.data.WeaponNBTHelper;
import com.sticktoslick.event.CombatEventHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.*;

/**
 * Bestiary screen: shows all mob types killed with this weapon,
 * their kill counts, and weapon statistics.
 */
public class BestiaryScreen extends Screen {
    private final ItemStack weaponStack;
    private final int totalKills;
    private final int totalDamage;
    private final List<Map.Entry<String, Integer>> entries;

    public BestiaryScreen(ItemStack stack) {
        super(Component.literal("Bestiary"));
        this.weaponStack = stack;
        this.totalKills = WeaponNBTHelper.getTotalKills(stack);
        this.totalDamage = WeaponNBTHelper.getTotalDamage(stack);

        CompoundTag bestiary = WeaponNBTHelper.getBestiary(stack);
        Map<String, Integer> map = new TreeMap<>();
        for (String key : bestiary.getAllKeys()) {
            map.put(key, bestiary.getInt(key));
        }
        // Sort by kill count descending
        entries = new ArrayList<>(map.entrySet());
        entries.sort((a, b) -> b.getValue() - a.getValue());
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(g);
        super.render(g, mouseX, mouseY, partialTick);

        int cx = this.width / 2;
        int y = 30;

        g.drawCenteredString(this.font, "§6§l━━━ BESTİARY ━━━", cx, y, 0xFFFFFF);
        y += 15;
        g.drawCenteredString(this.font, "§eToplam Öldürme: §f" + totalKills + "  §eToplam Hasar: §f" + totalDamage, cx,
                y, 0xFFFFFF);
        y += 20;

        // Column headers
        g.drawString(this.font, "§7Yaratık", cx - 100, y, 0xFFFFFF, false);
        g.drawString(this.font, "§7Sayı", cx + 70, y, 0xFFFFFF, false);
        y += 12;

        // Draw a separator
        g.fill(cx - 110, y, cx + 110, y + 1, 0x66FFFFFF);
        y += 4;

        int maxRows = Math.min(entries.size(), 20); // Show top 20
        for (int i = 0; i < maxRows; i++) {
            Map.Entry<String, Integer> entry = entries.get(i);
            String mobId = entry.getKey();
            int count = entry.getValue();

            // Clean up mob name: "minecraft:zombie" -> "Zombie"
            String cleanName = mobId.contains(":") ? mobId.split(":")[1] : mobId;
            cleanName = cleanName.substring(0, 1).toUpperCase() + cleanName.substring(1).replace("_", " ");

            // Color based on kill count
            String color = count >= 100 ? "§6" : count >= 50 ? "§e" : count >= 10 ? "§a" : "§f";

            g.drawString(this.font, color + cleanName, cx - 100, y, 0xFFFFFF, false);
            g.drawString(this.font, color + "x" + count, cx + 70, y, 0xFFFFFF, false);
            y += 11;
        }

        if (entries.isEmpty()) {
            g.drawCenteredString(this.font, "§7Henüz hiçbir yaratık öldürmediniz.", cx, y + 20, 0x999999);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
