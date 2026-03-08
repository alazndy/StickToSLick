package com.sticktoslick.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

/**
 * Central helper class for reading/writing the custom weapon NBT data
 * attached to every Stick to Slick weapon.
 */
public final class WeaponNBTHelper {
    // NBT Keys
    private static final String TAG_ROOT = "StickToSlick";
    private static final String TAG_LEVEL = "WeaponLevel";
    private static final String TAG_XP = "WeaponXP";
    private static final String TAG_CLASS = "WeaponClass";
    private static final String TAG_STAT_DAMAGE = "Stat_Damage";
    private static final String TAG_STAT_ATTACK_SPEED = "Stat_AttackSpeed";
    private static final String TAG_STAT_MOVE_SPEED = "Stat_MoveSpeed";
    private static final String TAG_STAT_KNOCKBACK = "Stat_Knockback";
    private static final String TAG_ENCHANT_CAPACITY_BONUS = "BonusEnchantCapacity";
    private static final String TAG_TRAIT_LEVEL = "TraitLevel";
    private static final String TAG_TOTAL_KILLS = "TotalKills";
    private static final String TAG_TOTAL_DAMAGE = "TotalDamage";
    private static final String TAG_BESTIARY = "Bestiary";
    private static final String TAG_EXTRA_SLOTS = "ExtraSlots";
    private static final String TAG_QUEST_ENCHANT = "QuestEnchant";
    private static final String TAG_QUEST_PROGRESS = "QuestProgress";
    private static final String TAG_QUEST_GOAL = "QuestGoal";
    private static final String TAG_QUEST_TYPE = "QuestType";
    private static final String TAG_QUEST_TARGET_LEVEL = "QuestTargetLevel";

    private WeaponNBTHelper() {
    }

    // ─── Root Tag ────────────────────────────────────────────

    public static CompoundTag getOrCreateTag(ItemStack stack) {
        CompoundTag root = stack.getOrCreateTag();
        if (!root.contains(TAG_ROOT)) {
            root.put(TAG_ROOT, new CompoundTag());
        }
        return root.getCompound(TAG_ROOT);
    }

    private static void save(ItemStack stack, CompoundTag data) {
        stack.getOrCreateTag().put(TAG_ROOT, data);
    }

    public static boolean hasWeaponData(ItemStack stack) {
        return stack.hasTag() && stack.getTag().contains(TAG_ROOT);
    }

    // ─── Level & XP ─────────────────────────────────────────

    public static int getLevel(ItemStack stack) {
        return getOrCreateTag(stack).getInt(TAG_LEVEL);
    }

    public static void setLevel(ItemStack stack, int level) {
        CompoundTag data = getOrCreateTag(stack);
        data.putInt(TAG_LEVEL, Math.min(level, 30));
        save(stack, data);
    }

    public static int getXP(ItemStack stack) {
        return getOrCreateTag(stack).getInt(TAG_XP);
    }

    public static void setXP(ItemStack stack, int xp) {
        CompoundTag data = getOrCreateTag(stack);
        data.putInt(TAG_XP, xp);
        save(stack, data);
    }

    public static void addXP(ItemStack stack, int amount) {
        setXP(stack, getXP(stack) + amount);
    }

    // ─── Weapon Class ───────────────────────────────────────

    public static String getWeaponClass(ItemStack stack) {
        CompoundTag data = getOrCreateTag(stack);
        return data.contains(TAG_CLASS) ? data.getString(TAG_CLASS) : "starter";
    }

    public static void setWeaponClass(ItemStack stack, String weaponClass) {
        CompoundTag data = getOrCreateTag(stack);
        data.putString(TAG_CLASS, weaponClass);
        save(stack, data);
    }

    // ─── Stats ──────────────────────────────────────────────

    public static int getStatDamage(ItemStack stack) {
        return getOrCreateTag(stack).getInt(TAG_STAT_DAMAGE);
    }

    public static void addStatDamage(ItemStack stack, int amount) {
        CompoundTag data = getOrCreateTag(stack);
        data.putInt(TAG_STAT_DAMAGE, data.getInt(TAG_STAT_DAMAGE) + amount);
        save(stack, data);
    }

    public static void setStatDamage(ItemStack stack, int value) {
        CompoundTag data = getOrCreateTag(stack);
        data.putInt(TAG_STAT_DAMAGE, value);
        save(stack, data);
    }

    public static int getStatAttackSpeed(ItemStack stack) {
        return getOrCreateTag(stack).getInt(TAG_STAT_ATTACK_SPEED);
    }

    public static void addStatAttackSpeed(ItemStack stack, int amount) {
        CompoundTag data = getOrCreateTag(stack);
        data.putInt(TAG_STAT_ATTACK_SPEED, data.getInt(TAG_STAT_ATTACK_SPEED) + amount);
        save(stack, data);
    }

    public static void setStatAttackSpeed(ItemStack stack, int value) {
        CompoundTag data = getOrCreateTag(stack);
        data.putInt(TAG_STAT_ATTACK_SPEED, value);
        save(stack, data);
    }

    public static int getStatMoveSpeed(ItemStack stack) {
        return getOrCreateTag(stack).getInt(TAG_STAT_MOVE_SPEED);
    }

    public static void addStatMoveSpeed(ItemStack stack, int amount) {
        CompoundTag data = getOrCreateTag(stack);
        data.putInt(TAG_STAT_MOVE_SPEED, data.getInt(TAG_STAT_MOVE_SPEED) + amount);
        save(stack, data);
    }

    public static void setStatMoveSpeed(ItemStack stack, int value) {
        CompoundTag data = getOrCreateTag(stack);
        data.putInt(TAG_STAT_MOVE_SPEED, value);
        save(stack, data);
    }

    public static int getStatKnockback(ItemStack stack) {
        return getOrCreateTag(stack).getInt(TAG_STAT_KNOCKBACK);
    }

    public static void addStatKnockback(ItemStack stack, int amount) {
        CompoundTag data = getOrCreateTag(stack);
        data.putInt(TAG_STAT_KNOCKBACK, data.getInt(TAG_STAT_KNOCKBACK) + amount);
        save(stack, data);
    }

    public static void setStatKnockback(ItemStack stack, int value) {
        CompoundTag data = getOrCreateTag(stack);
        data.putInt(TAG_STAT_KNOCKBACK, value);
        save(stack, data);
    }

    // ─── Stat Points Logic ─────────────────────────────────

    private static final String TAG_SPENT_STAT_POINTS = "SpentStatPoints";

    public static int getSpentStatPoints(ItemStack stack) {
        return getOrCreateTag(stack).getInt(TAG_SPENT_STAT_POINTS);
    }

    public static void addSpentStatPoint(ItemStack stack, int amount) {
        CompoundTag data = getOrCreateTag(stack);
        data.putInt(TAG_SPENT_STAT_POINTS, data.getInt(TAG_SPENT_STAT_POINTS) + amount);
        save(stack, data);
    }

    public static int getAvailableStatPoints(ItemStack stack) {
        int level = getLevel(stack);
        int spent = getSpentStatPoints(stack);
        // Assuming 1 point per level, minus level 1. So level 5 has 4 total points.
        return Math.max(0, (level - 1) - spent);
    }

    // ─── Enchantment Capacity ───────────────────────────────

    public static int getBonusEnchantCapacity(ItemStack stack) {
        return getOrCreateTag(stack).getInt(TAG_ENCHANT_CAPACITY_BONUS);
    }

    public static void addBonusEnchantCapacity(ItemStack stack, int amount) {
        CompoundTag data = getOrCreateTag(stack);
        data.putInt(TAG_ENCHANT_CAPACITY_BONUS, data.getInt(TAG_ENCHANT_CAPACITY_BONUS) + amount);
        save(stack, data);
    }

    public static int getExtraSlots(ItemStack stack) {
        return getOrCreateTag(stack).getInt(TAG_EXTRA_SLOTS);
    }

    public static void addExtraSlot(ItemStack stack) {
        CompoundTag data = getOrCreateTag(stack);
        data.putInt(TAG_EXTRA_SLOTS, data.getInt(TAG_EXTRA_SLOTS) + 1);
        save(stack, data);
    }

    // ─── Enchantment Quests ─────────────────────────────────

    public static String getQuestEnchantment(ItemStack stack) {
        return getOrCreateTag(stack).getString(TAG_QUEST_ENCHANT);
    }

    public static boolean hasActiveQuest(ItemStack stack) {
        return !getQuestEnchantment(stack).isEmpty();
    }

    public static void startQuest(ItemStack stack, String enchantId, int goal, String questType, int targetLevel) {
        CompoundTag data = getOrCreateTag(stack);
        data.putString(TAG_QUEST_ENCHANT, enchantId);
        data.putInt(TAG_QUEST_PROGRESS, 0);
        data.putInt(TAG_QUEST_GOAL, goal);
        data.putString(TAG_QUEST_TYPE, questType);
        data.putInt(TAG_QUEST_TARGET_LEVEL, targetLevel);
        save(stack, data);
    }

    public static int getQuestTargetLevel(ItemStack stack) {
        return getOrCreateTag(stack).getInt(TAG_QUEST_TARGET_LEVEL);
    }

    public static String getQuestType(ItemStack stack) {
        return getOrCreateTag(stack).getString(TAG_QUEST_TYPE);
    }

    public static void clearQuest(ItemStack stack) {
        CompoundTag data = getOrCreateTag(stack);
        data.remove(TAG_QUEST_ENCHANT);
        data.remove(TAG_QUEST_PROGRESS);
        data.remove(TAG_QUEST_GOAL);
        data.remove(TAG_QUEST_TYPE);
        data.remove(TAG_QUEST_TARGET_LEVEL);
        save(stack, data);
    }

    public static int getQuestProgress(ItemStack stack) {
        return getOrCreateTag(stack).getInt(TAG_QUEST_PROGRESS);
    }

    public static int getQuestGoal(ItemStack stack) {
        return getOrCreateTag(stack).getInt(TAG_QUEST_GOAL);
    }

    public static void addQuestProgress(ItemStack stack, int amount) {
        CompoundTag data = getOrCreateTag(stack);
        data.putInt(TAG_QUEST_PROGRESS, data.getInt(TAG_QUEST_PROGRESS) + amount);
        save(stack, data);
    }

    // ─── Trait Level ────────────────────────────────────────

    public static int getTraitLevel(ItemStack stack) {
        return getOrCreateTag(stack).getInt(TAG_TRAIT_LEVEL);
    }

    public static void addTraitLevel(ItemStack stack, int amount) {
        CompoundTag data = getOrCreateTag(stack);
        data.putInt(TAG_TRAIT_LEVEL, data.getInt(TAG_TRAIT_LEVEL) + amount);
        save(stack, data);
    }

    // ─── Kill & Damage Statistics ──────────────────────────

    public static int getTotalKills(ItemStack stack) {
        return getOrCreateTag(stack).getInt(TAG_TOTAL_KILLS);
    }

    public static void addKill(ItemStack stack) {
        CompoundTag data = getOrCreateTag(stack);
        data.putInt(TAG_TOTAL_KILLS, data.getInt(TAG_TOTAL_KILLS) + 1);
        save(stack, data);
    }

    public static int getTotalDamage(ItemStack stack) {
        return getOrCreateTag(stack).getInt(TAG_TOTAL_DAMAGE);
    }

    public static void addDamageDealt(ItemStack stack, int amount) {
        CompoundTag data = getOrCreateTag(stack);
        data.putInt(TAG_TOTAL_DAMAGE, data.getInt(TAG_TOTAL_DAMAGE) + amount);
        save(stack, data);
    }

    // ─── Bestiary ─────────────────────────────────────

    public static void addBestiaryKill(ItemStack stack, String entityTypeId) {
        CompoundTag data = getOrCreateTag(stack);
        CompoundTag bestiary = data.contains(TAG_BESTIARY) ? data.getCompound(TAG_BESTIARY) : new CompoundTag();
        bestiary.putInt(entityTypeId, bestiary.getInt(entityTypeId) + 1);
        data.put(TAG_BESTIARY, bestiary);
        save(stack, data);
    }

    public static CompoundTag getBestiary(ItemStack stack) {
        CompoundTag data = getOrCreateTag(stack);
        return data.contains(TAG_BESTIARY) ? data.getCompound(TAG_BESTIARY) : new CompoundTag();
    }

    // ─── Initialization ─────────────────────────────────────

    /**
     * Writes default weapon data onto a fresh Starter Stick.
     */
    public static void initializeWeapon(ItemStack stack) {
        CompoundTag data = getOrCreateTag(stack);
        data.putInt(TAG_LEVEL, 1);
        data.putInt(TAG_XP, 0);
        data.putString(TAG_CLASS, "starter");
        data.putInt(TAG_STAT_DAMAGE, 0);
        data.putInt(TAG_STAT_ATTACK_SPEED, 0);
        data.putInt(TAG_STAT_MOVE_SPEED, 0);
        data.putInt(TAG_STAT_KNOCKBACK, 0);
        data.putInt(TAG_SPENT_STAT_POINTS, 0);
        data.putInt(TAG_ENCHANT_CAPACITY_BONUS, 0);
        data.putInt(TAG_TRAIT_LEVEL, 0);
        data.putInt(TAG_TOTAL_KILLS, 0);
        data.putInt(TAG_TOTAL_DAMAGE, 0);
        data.put(TAG_BESTIARY, new CompoundTag());
        data.putInt(TAG_EXTRA_SLOTS, 0);
        data.putString(TAG_QUEST_ENCHANT, "");
        data.putInt(TAG_QUEST_PROGRESS, 0);
        data.putInt(TAG_QUEST_GOAL, 0);
        data.putString(TAG_QUEST_TYPE, "");
        data.putInt(TAG_QUEST_TARGET_LEVEL, 0);
        save(stack, data);
    }

    // ─── Copy all weapon data to a new stack ────────────────

    /**
     * Copies ALL weapon NBT data from one stack to another,
     * preserving stats across evolution.
     */
    public static void copyWeaponData(ItemStack source, ItemStack target) {
        if (!hasWeaponData(source))
            return;
        CompoundTag sourceData = source.getTag().getCompound(TAG_ROOT).copy();
        target.getOrCreateTag().put(TAG_ROOT, sourceData);
    }
}
