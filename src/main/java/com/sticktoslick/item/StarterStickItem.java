package com.sticktoslick.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.sticktoslick.data.WeaponClassData;
import com.sticktoslick.data.WeaponNBTHelper;
import com.sticktoslick.data.WeaponTierData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

/**
 * The Starter Stick — the seed from which all legendary weapons grow.
 */
public class StarterStickItem extends SwordItem {
    private static final Tier STARTER_TIER = new Tier() {
        @Override
        public int getUses() {
            return 100;
        }

        @Override
        public float getSpeed() {
            return 0.0f;
        }

        @Override
        public float getAttackDamageBonus() {
            return 0.0f;
        }

        @Override
        public int getLevel() {
            return 0;
        }

        @Override
        public int getEnchantmentValue() {
            return 0;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.EMPTY;
        }
    };

    private static final UUID BASE_ATTACK_DAMAGE_UUID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
    private static final UUID BASE_ATTACK_SPEED_UUID = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");

    public StarterStickItem() {
        super(STARTER_TIER, 0, -2.4f, new Item.Properties().fireResistant());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip,
            TooltipFlag isAdvanced) {
        if (!WeaponNBTHelper.hasWeaponData(stack))
            return;

        int weaponLevel = WeaponNBTHelper.getLevel(stack);
        int xp = WeaponNBTHelper.getXP(stack);
        String weaponClass = WeaponNBTHelper.getWeaponClass(stack);
        WeaponTierData tier = WeaponTierData.getFromLevel(weaponLevel);

        // --- 1. Header & Rarity ---
        tooltip.add(Component.literal("§l" + tier.name.toUpperCase())
                .withStyle(tier.formatting, ChatFormatting.BOLD));
        tooltip.add(Component.literal(" Class: " + formatClassName(weaponClass))
                .withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.literal(" Level: " + weaponLevel + " / 30")
                .withStyle(ChatFormatting.GREEN));

        // --- 2. XP Progress Bar ---
        if (weaponLevel < 30) {
            int nextXP = com.sticktoslick.data.WeaponLevelConfig.getXPForNextLevel(weaponLevel);
            float progress = (float) xp / nextXP;
            tooltip.add(buildXpBar(progress, xp, nextXP));
        } else {
            tooltip.add(Component.literal(" [")
                    .append(Component.literal("▎▎▎▎▎▎▎▎▎▎").withStyle(ChatFormatting.GOLD))
                    .append(Component.literal("] MAX").withStyle(ChatFormatting.LIGHT_PURPLE)));
        }

        // --- 3. Enchantment Capacity ---
        int maxSlots = 3 + WeaponNBTHelper.getExtraSlots(stack);
        int currentEnchants = stack.getEnchantmentTags().size();
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("◈ Enchants: ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal(currentEnchants + "/" + maxSlots)
                        .withStyle(currentEnchants >= maxSlots ? ChatFormatting.RED : ChatFormatting.AQUA)));

        // --- 4. Active Quest ---
        if (WeaponNBTHelper.hasActiveQuest(stack)) {
            tooltip.add(Component.literal("📜 GÖREV: ").withStyle(ChatFormatting.GOLD)
                    .append(Component.literal(com.sticktoslick.data.EnchantmentQuestManager.getQuestDescription(
                            WeaponNBTHelper.getQuestType(stack), WeaponNBTHelper.getQuestGoal(stack)))
                            .withStyle(ChatFormatting.YELLOW)));

            float qProgress = (float) WeaponNBTHelper.getQuestProgress(stack) / WeaponNBTHelper.getQuestGoal(stack);
            tooltip.add(Component.literal("  ")
                    .append(buildMiniBar(qProgress, ChatFormatting.GOLD))
                    .append(Component
                            .literal(" " + WeaponNBTHelper.getQuestProgress(stack) + "/"
                                    + WeaponNBTHelper.getQuestGoal(stack))
                            .withStyle(ChatFormatting.WHITE)));
        }

        // --- 5. Bonus Stats ---
        int statDmg = WeaponNBTHelper.getStatDamage(stack);
        int statSpd = WeaponNBTHelper.getStatAttackSpeed(stack);
        int statMov = WeaponNBTHelper.getStatMoveSpeed(stack);
        int statKb = WeaponNBTHelper.getStatKnockback(stack);

        if (statDmg > 0 || statSpd > 0 || statMov > 0 || statKb > 0) {
            tooltip.add(Component.empty());
            tooltip.add(Component.literal("--- Bonuslar ---").withStyle(ChatFormatting.DARK_GRAY));
            if (statDmg > 0)
                tooltip.add(Component.literal(" ⚔ +").append(String.valueOf(statDmg)).append(" Hasar")
                        .withStyle(ChatFormatting.BLUE));
            if (statSpd > 0)
                tooltip.add(Component.literal(" ⚡ +").append(String.valueOf(statSpd)).append(" Hız")
                        .withStyle(ChatFormatting.RED));
            if (statMov > 0)
                tooltip.add(Component.literal(" 👟 +").append(String.valueOf(statMov)).append(" Hareket")
                        .withStyle(ChatFormatting.WHITE));
            if (statKb > 0)
                tooltip.add(Component.literal(" 🛡 +").append(String.valueOf(statKb)).append(" Geri Tepme")
                        .withStyle(ChatFormatting.GREEN));
        }

        tooltip.add(Component.empty());
        if (isBroken(stack)) {
            tooltip.add(Component.literal("⚠ KIRIK ⚠").withStyle(ChatFormatting.RED, ChatFormatting.BOLD,
                    ChatFormatting.STRIKETHROUGH));
        }
    }

    private Component buildXpBar(float progress, int current, int max) {
        int bars = 10;
        int filled = Math.round(progress * bars);
        MutableComponent bar = Component.literal(" [");

        for (int i = 0; i < bars; i++) {
            if (i < filled) {
                bar.append(Component.literal("▎").withStyle(ChatFormatting.YELLOW));
            } else {
                bar.append(Component.literal("▎").withStyle(ChatFormatting.DARK_GRAY));
            }
        }

        return bar.append(Component.literal("] ").withStyle(ChatFormatting.WHITE))
                .append(Component.literal(current + "/" + max + " XP").withStyle(ChatFormatting.GRAY));
    }

    private Component buildMiniBar(float progress, ChatFormatting color) {
        int bars = 5;
        int filled = Math.round(progress * bars);
        MutableComponent bar = Component.literal("[");
        for (int i = 0; i < bars; i++) {
            bar.append(Component.literal("▎").withStyle(i < filled ? color : ChatFormatting.DARK_GRAY));
        }
        return bar.append("]");
    }

    private String formatClassName(String weaponClass) {
        if (weaponClass == null || weaponClass.isEmpty())
            return "Unknown";
        String[] parts = weaponClass.split("_");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (!part.isEmpty()) {
                sb.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1)).append(" ");
            }
        }
        return sb.toString().trim();
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        if (slot == EquipmentSlot.MAINHAND && WeaponNBTHelper.hasWeaponData(stack)) {
            String weaponClass = WeaponNBTHelper.getWeaponClass(stack);
            WeaponClassData.WeaponStats stats = WeaponClassData.get(weaponClass);

            float bonusDamage = WeaponNBTHelper.getStatDamage(stack) * 0.5f;
            float bonusSpeed = WeaponNBTHelper.getStatAttackSpeed(stack) * 0.05f;

            ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
            builder.put(Attributes.ATTACK_DAMAGE,
                    new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier",
                            stats.baseDamage() + bonusDamage - 1.0, AttributeModifier.Operation.ADDITION));
            builder.put(Attributes.ATTACK_SPEED,
                    new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier",
                            stats.baseAttackSpeed() + bonusSpeed - 4.0, AttributeModifier.Operation.ADDITION));

            // Movement speed bonus
            float moveBonus = WeaponNBTHelper.getStatMoveSpeed(stack) * 0.01f;
            if (moveBonus > 0) {
                builder.put(Attributes.MOVEMENT_SPEED,
                        new AttributeModifier(UUID.fromString("7e4b2c1a-3f5d-4e6a-8b9c-0d1e2f3a4b5c"),
                                "Weapon speed bonus", moveBonus, AttributeModifier.Operation.MULTIPLY_TOTAL));
            }

            // If broken, completely negate all damage and attack speed
            if (isBroken(stack)) {
                builder.put(Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier",
                                -100.0, AttributeModifier.Operation.ADDITION));
                builder.put(Attributes.ATTACK_SPEED,
                        new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier",
                                -100.0, AttributeModifier.Operation.ADDITION));
                return builder.build();
            }

            return builder.build();
        }
        return super.getAttributeModifiers(slot, stack);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, net.minecraft.world.level.block.state.BlockState state) {
        if (isBroken(stack)) {
            return 0.0f; // Cannot mine when broken
        }
        return super.getDestroySpeed(stack, state);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        if (WeaponNBTHelper.hasWeaponData(stack)) {
            String weaponClass = WeaponNBTHelper.getWeaponClass(stack);
            return WeaponClassData.get(weaponClass).maxDurability();
        }
        return super.getMaxDamage(stack);
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        int maxDmg = getMaxDamage(stack);
        super.setDamage(stack, Math.min(damage, maxDmg - 1));
    }

    @Override
    public <T extends net.minecraft.world.entity.LivingEntity> int damageItem(ItemStack stack, int amount, T entity,
            java.util.function.Consumer<T> onBroken) {
        int currentDamage = stack.getDamageValue();
        int maxDamage = stack.getMaxDamage();
        int newDamage = Math.min(currentDamage + amount, maxDamage - 1);
        stack.setDamageValue(newDamage);
        return 0;
    }

    public static boolean isBroken(ItemStack stack) {
        return stack.getDamageValue() >= stack.getMaxDamage() - 1;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, net.minecraft.world.entity.LivingEntity target,
            net.minecraft.world.entity.LivingEntity attacker) {
        if (isBroken(stack)) {
            if (attacker instanceof Player player) {
                player.displayClientMessage(
                        Component.literal("⚠ Silahın kırık! Tamir et!").withStyle(ChatFormatting.RED), true);
            }
            return false;
        }
        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false; // Disable vanilla enchanting
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return false; // Disable anvil enchanting
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack repairCandidate) {
        return false;
    }

    @Override
    public boolean isRepairable(ItemStack stack) {
        return false;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        return InteractionResultHolder.pass(itemstack);
    }
}
