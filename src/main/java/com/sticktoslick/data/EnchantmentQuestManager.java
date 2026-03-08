package com.sticktoslick.data;

import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages the "Quests" required to unlock enchantments.
 * Maps enchantment types to specific tasks and goals.
 */
public final class EnchantmentQuestManager {
    private EnchantmentQuestManager() {
    }

    public enum QuestType {
        KILL_ANY, // Kill any mob
        KILL_UNDEAD, // Kill undead mobs (zombies, skeletons, etc.)
        KILL_ARTHROPOD, // Kill spiders, silverfish, etc.
        KILL_ILLAGER, // Kill illagers (vindicators, pillagers, etc.)
        KILL_NETHER, // Kill nether mobs (blazes, ghasts, piglins, etc.)
        DEAL_DAMAGE, // Deal damage to enemies
        BLOCK_DAMAGE, // Block damage with shield
        TAKE_DAMAGE, // Take damage while holding weapon
        CRITICAL_HIT // Land critical hits
    }

    public static class QuestInfo {
        public final QuestType type;
        public final int goal;

        public QuestInfo(QuestType type, int goal) {
            this.type = type;
            this.goal = goal;
        }
    }

    private static final Map<Enchantment, QuestInfo> QUESTS = new HashMap<>();

    static {
        // ──── Damage Enchantments ────
        QUESTS.put(Enchantments.SHARPNESS, new QuestInfo(QuestType.KILL_ANY, 100)); // Genel savaş ustalığı
        QUESTS.put(Enchantments.SMITE, new QuestInfo(QuestType.KILL_UNDEAD, 75)); // Ölümsüz avcısı
        QUESTS.put(Enchantments.BANE_OF_ARTHROPODS, new QuestInfo(QuestType.KILL_ARTHROPOD, 75)); // Böcek avcısı

        // ──── Utility Combat Enchantments ────
        QUESTS.put(Enchantments.FIRE_ASPECT, new QuestInfo(QuestType.KILL_NETHER, 50)); // Nether moblarını öldür
        QUESTS.put(Enchantments.KNOCKBACK, new QuestInfo(QuestType.CRITICAL_HIT, 60)); // Kritik vuruş ustası
        QUESTS.put(Enchantments.SWEEPING_EDGE, new QuestInfo(QuestType.KILL_ANY, 150)); // Kalabalık temizleyici

        // ──── Loot & Resource Enchantments ────
        QUESTS.put(Enchantments.MOB_LOOTING, new QuestInfo(QuestType.KILL_ILLAGER, 40)); // Yağmacıları avla

        // ──── Durability Enchantments ────
        QUESTS.put(Enchantments.UNBREAKING, new QuestInfo(QuestType.TAKE_DAMAGE, 500)); // Dayak ye ama ayakta kal
        QUESTS.put(Enchantments.MENDING, new QuestInfo(QuestType.DEAL_DAMAGE, 8000)); // Savaşarak iyileş

        // ──── Curse (still earnable but painful) ────
        QUESTS.put(Enchantments.VANISHING_CURSE, new QuestInfo(QuestType.TAKE_DAMAGE, 1000)); // Acıya dayan
    }

    /**
     * Returns the base quest information for the specified enchantment.
     */
    public static QuestInfo getInfoFor(Enchantment enchantment) {
        return QUESTS.getOrDefault(enchantment, new QuestInfo(QuestType.KILL_ANY, 100));
    }

    /**
     * Returns quest info scaled for a specific enchantment level.
     * Each level increases the base goal by 50%.
     * Level 1 = 1.0x, Level 2 = 1.5x, Level 3 = 2.0x, Level 4 = 2.5x, Level 5 =
     * 3.0x
     */
    public static QuestInfo getInfoForLevel(Enchantment enchantment, int targetLevel) {
        QuestInfo base = getInfoFor(enchantment);
        double multiplier = 1.0 + (targetLevel - 1) * 0.5;
        int scaledGoal = (int) Math.ceil(base.goal * multiplier);
        return new QuestInfo(base.type, scaledGoal);
    }

    /**
     * Returns a human-readable description of the quest goal.
     */
    public static String getQuestDescription(String questTypeStr, int goal) {
        QuestType type = QuestType.KILL_ANY;
        try {
            if (questTypeStr != null && !questTypeStr.isEmpty()) {
                type = QuestType.valueOf(questTypeStr);
            }
        } catch (IllegalArgumentException e) {
            // fallback
        }

        return switch (type) {
            case KILL_ANY -> goal + " mob öldür";
            case KILL_UNDEAD -> goal + " ölümsüz (zombie/iskelet) öldür";
            case KILL_ARTHROPOD -> goal + " örümcek/gümüşbalığı öldür";
            case KILL_ILLAGER -> goal + " yağmacı (illager) öldür";
            case KILL_NETHER -> goal + " nether yaratığı öldür";
            case DEAL_DAMAGE -> goal + " hasar ver";
            case BLOCK_DAMAGE -> goal + " hasar engelle (kalkan)";
            case TAKE_DAMAGE -> goal + " hasar ye ve ayakta kal";
            case CRITICAL_HIT -> goal + " kritik vuruş yap";
            default -> "Görev tamamla (" + goal + ")";
        };
    }
}
