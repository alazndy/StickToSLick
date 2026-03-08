package com.sticktoslick.event;

import com.sticktoslick.data.WeaponLevelConfig;
import com.sticktoslick.data.WeaponNBTHelper;
import com.sticktoslick.data.WeaponTraitData;
import com.sticktoslick.item.StarterStickItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Handles XP gain from mob kills and level-up logic.
 * Each mob type gives a unique XP amount to make hunting strategic.
 */
public class CombatEventHandler {

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player))
            return;

        ItemStack mainHand = player.getMainHandItem();
        if (!(mainHand.getItem() instanceof StarterStickItem))
            return;

        if (!WeaponNBTHelper.hasWeaponData(mainHand)) {
            WeaponNBTHelper.initializeWeapon(mainHand);
        }

        int currentLevel = WeaponNBTHelper.getLevel(mainHand);
        if (currentLevel >= WeaponLevelConfig.MAX_LEVEL)
            return;

        // Check if weapon is locked because it needs evolution
        String weaponClass = WeaponNBTHelper.getWeaponClass(mainHand);
        if (com.sticktoslick.data.EvolutionPath.hasPendingEvolution(weaponClass, currentLevel)) {
            player.displayClientMessage(
                    Component.literal("Bu silah evrimleşmeli! Daha fazla deneyim kazanamaz! (O tuşuna bas)")
                            .withStyle(ChatFormatting.RED, ChatFormatting.BOLD),
                    true);
            return;
        }

        LivingEntity victim = event.getEntity();
        int xpGain = getXPForEntity(victim);

        // Bonus XP from "Hırs" trait
        WeaponTraitData.TraitInfo traitInfo = WeaponTraitData.get(weaponClass);
        if ("bonus_xp".equals(traitInfo.effectType())) {
            int traitLvl = WeaponNBTHelper.getTraitLevel(mainHand);
            xpGain += (int) (xpGain * traitInfo.effectPerLevel() * traitLvl);
        }

        // Track statistics
        WeaponNBTHelper.addKill(mainHand);
        WeaponNBTHelper.addBestiaryKill(mainHand,
                net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(victim.getType()).toString());

        // Show XP gain in action bar
        player.displayClientMessage(Component.literal("+" + xpGain + " Silah XP")
                .withStyle(ChatFormatting.GREEN), true);

        WeaponNBTHelper.addXP(mainHand, xpGain);

        int xp = WeaponNBTHelper.getXP(mainHand);
        int threshold = WeaponLevelConfig.getXPForNextLevel(currentLevel);

        if (xp >= threshold) {
            int newLevel = currentLevel + 1;
            WeaponNBTHelper.setLevel(mainHand, newLevel);
            WeaponNBTHelper.setXP(mainHand, xp - threshold);
            onLevelUp(player, mainHand, newLevel);
        }

        // ──── Progress Enchantment Quest (Kill based) ────
        if (WeaponNBTHelper.hasActiveQuest(mainHand)) {
            String typeStr = WeaponNBTHelper.getQuestType(mainHand);
            boolean validKill = false;

            if ("KILL_ANY".equals(typeStr)) {
                validKill = true;
            } else if ("KILL_UNDEAD".equals(typeStr)) {
                validKill = victim.getMobType() == net.minecraft.world.entity.MobType.UNDEAD;
            } else if ("KILL_ARTHROPOD".equals(typeStr)) {
                validKill = victim.getMobType() == net.minecraft.world.entity.MobType.ARTHROPOD;
            } else if ("KILL_ILLAGER".equals(typeStr)) {
                validKill = victim.getMobType() == net.minecraft.world.entity.MobType.ILLAGER;
            } else if ("KILL_NETHER".equals(typeStr)) {
                // Nether mobs: Blaze, Ghast, Wither Skeleton, Piglin, Hoglin, Magma Cube, etc.
                validKill = victim instanceof net.minecraft.world.entity.monster.Blaze
                        || victim instanceof net.minecraft.world.entity.monster.Ghast
                        || victim instanceof net.minecraft.world.entity.monster.WitherSkeleton
                        || victim instanceof net.minecraft.world.entity.monster.MagmaCube
                        || victim instanceof net.minecraft.world.entity.monster.piglin.Piglin
                        || victim instanceof net.minecraft.world.entity.monster.hoglin.Hoglin;
            }

            if (validKill) {
                progressQuest(player, mainHand, 1);
            }
        }
    }

    private void onLevelUp(Player player, ItemStack stack, int newLevel) {
        player.sendSystemMessage(Component.literal("⭐ SEVİYE ATLADIN! → ")
                .withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD)
                .append(Component.literal("Lv. " + newLevel).withStyle(ChatFormatting.GOLD)));

        player.sendSystemMessage(Component.literal("[ ")
                .withStyle(ChatFormatting.GRAY)
                .append(Component.literal("TIKLA VE YÜKSELT")
                        .withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD, ChatFormatting.UNDERLINE)
                        .withStyle(
                                style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ss gui")))
                        .withStyle(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                Component.literal("Yükseltme ekranını açar")))))
                .append(Component.literal(" ]").withStyle(ChatFormatting.GRAY)));

        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0f, 1.2f);

        if (WeaponLevelConfig.isMilestone(newLevel)) {
            player.sendSystemMessage(Component.literal("✨ Bu seviye bir dönüm noktası! Silahını EVRİMLEŞTİREBİLİRSİN.")
                    .withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.ITALIC));
        }

        // Trigger Epic VFX on client
        com.sticktoslick.data.WeaponTierData tier = com.sticktoslick.data.WeaponTierData.getFromLevel(newLevel);
        com.sticktoslick.network.ModMessages.sendToPlayer(
                new com.sticktoslick.network.S2CLevelUpVfxPacket(newLevel, tier.color),
                (net.minecraft.server.level.ServerPlayer) player);
    }

    private void progressQuest(Player player, ItemStack weapon, int amount) {
        if (!WeaponNBTHelper.hasActiveQuest(weapon))
            return;

        WeaponNBTHelper.addQuestProgress(weapon, amount);
        int progress = WeaponNBTHelper.getQuestProgress(weapon);
        int goal = WeaponNBTHelper.getQuestGoal(weapon);

        if (progress >= goal) {
            String enchantId = WeaponNBTHelper.getQuestEnchantment(weapon);
            int targetLevel = WeaponNBTHelper.getQuestTargetLevel(weapon);
            if (targetLevel <= 0)
                targetLevel = 1; // fallback for old data

            net.minecraft.world.item.enchantment.Enchantment enchantment = net.minecraftforge.registries.ForgeRegistries.ENCHANTMENTS
                    .getValue(new net.minecraft.resources.ResourceLocation(enchantId));

            if (enchantment != null) {
                // Remove existing enchantment first (to upgrade level properly)
                java.util.Map<net.minecraft.world.item.enchantment.Enchantment, Integer> existing = net.minecraft.world.item.enchantment.EnchantmentHelper
                        .getEnchantments(weapon);
                existing.put(enchantment, targetLevel);
                net.minecraft.world.item.enchantment.EnchantmentHelper.setEnchantments(existing, weapon);

                player.sendSystemMessage(net.minecraft.network.chat.Component.literal("✨ GÖREV TAMAMLANDI! ")
                        .withStyle(net.minecraft.ChatFormatting.GOLD, net.minecraft.ChatFormatting.BOLD)
                        .append(net.minecraft.network.chat.Component
                                .literal(enchantment.getFullname(targetLevel).getString() + " silahına eklendi.")
                                .withStyle(net.minecraft.ChatFormatting.WHITE)));

                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                        net.minecraft.sounds.SoundEvents.UI_TOAST_CHALLENGE_COMPLETE,
                        net.minecraft.sounds.SoundSource.PLAYERS, 1.0f, 1.0f);
            }

            WeaponNBTHelper.clearQuest(weapon);
            weapon.resetHoverName();
        } else {
            // Adjust notification frequency based on goal size
            int notifyInterval = goal >= 1000 ? 500 : (goal >= 100 ? 50 : 10);
            if (progress % notifyInterval == 0 || goal - progress <= 5) {
                player.displayClientMessage(
                        net.minecraft.network.chat.Component.literal("Büyü Görevi: " + progress + "/" + goal)
                                .withStyle(net.minecraft.ChatFormatting.AQUA),
                        true);
            }
        }
    }

    @SubscribeEvent
    public void onLivingDamage(net.minecraftforge.event.entity.living.LivingDamageEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            ItemStack mainHand = player.getMainHandItem();
            if (mainHand.getItem() instanceof StarterStickItem && WeaponNBTHelper.hasActiveQuest(mainHand)) {
                String questType = WeaponNBTHelper.getQuestType(mainHand);
                if ("DEAL_DAMAGE".equals(questType)) {
                    progressQuest(player, mainHand, (int) event.getAmount());
                } else if ("CRITICAL_HIT".equals(questType)) {
                    // Critical hit: player is falling (not on ground) and attack is not a sweep
                    if (player.fallDistance > 0.0F && !player.onGround()
                            && !player.isInWater() && !player.onClimbable()) {
                        progressQuest(player, mainHand, 1);
                    }
                }
            }
        }
    }

    /**
     * Tracks TAKE_DAMAGE quests — player receives damage while holding the weapon.
     */
    @SubscribeEvent
    public void onPlayerHurt(net.minecraftforge.event.entity.living.LivingHurtEvent event) {
        if (event.getEntity() instanceof Player player) {
            ItemStack mainHand = player.getMainHandItem();
            if (mainHand.getItem() instanceof StarterStickItem && WeaponNBTHelper.hasActiveQuest(mainHand)) {
                if ("TAKE_DAMAGE".equals(WeaponNBTHelper.getQuestType(mainHand))) {
                    progressQuest(player, mainHand, (int) Math.max(1, event.getAmount()));
                }
            }
        }
    }

    @SubscribeEvent
    public void onShieldBlock(net.minecraftforge.event.entity.living.ShieldBlockEvent event) {
        if (event.getEntity() instanceof Player player) {
            ItemStack mainHand = player.getMainHandItem();
            if (mainHand.getItem() instanceof StarterStickItem && WeaponNBTHelper.hasActiveQuest(mainHand)) {
                if ("BLOCK_DAMAGE".equals(WeaponNBTHelper.getQuestType(mainHand))) {
                    progressQuest(player, mainHand, (int) event.getBlockedDamage());
                }
            }
        }
    }

    /**
     * Every mob has a unique XP value to make hunting strategic.
     */
    private int getXPForEntity(LivingEntity entity) {
        // ──── Legendary Bosses (50-100 XP) ────
        if (entity instanceof EnderDragon)
            return 100;
        if (entity instanceof WitherBoss)
            return 80;

        // ──── Mini-Bosses (25-40 XP) ────
        if (entity instanceof Warden)
            return 40;
        if (entity instanceof ElderGuardian)
            return 30;
        if (entity instanceof Ravager)
            return 25;

        // ──── Nether Mobs (8-20 XP) ────
        if (entity instanceof WitherSkeleton)
            return 20;
        if (entity instanceof Ghast)
            return 15;
        if (entity instanceof Blaze)
            return 12;
        if (entity instanceof PiglinBrute)
            return 12;
        if (entity instanceof Hoglin)
            return 10;
        if (entity instanceof MagmaCube)
            return 8;
        if (entity instanceof Piglin)
            return 8;
        if (entity instanceof Zoglin)
            return 10;

        // ──── End Mobs (10-15 XP) ────
        if (entity instanceof EnderMan)
            return 15;
        if (entity instanceof Shulker)
            return 12;
        if (entity instanceof Endermite)
            return 5;

        // ──── Overworld Elites (5-10 XP) ────
        if (entity instanceof Witch)
            return 10;
        if (entity instanceof Evoker)
            return 12;
        if (entity instanceof Vindicator)
            return 8;
        if (entity instanceof Pillager)
            return 6;
        if (entity instanceof Phantom)
            return 8;
        if (entity instanceof Guardian)
            return 7;

        // ──── Underground (3-6 XP) ────
        if (entity instanceof CaveSpider)
            return 5;
        if (entity instanceof Silverfish)
            return 3;

        // ──── Overworld Standards (2-5 XP) ────
        if (entity instanceof Creeper)
            return 5;
        if (entity instanceof Skeleton)
            return 4;
        if (entity instanceof Spider)
            return 3;
        if (entity instanceof Zombie)
            return 3;
        if (entity instanceof Drowned)
            return 4;
        if (entity instanceof Husk)
            return 4;
        if (entity instanceof Stray)
            return 4;
        if (entity instanceof Slime)
            return 2;

        // ──── Catch-all: any other Monster ────
        if (entity instanceof Monster)
            return 3;

        // ──── Passive mobs ────
        return 1;
    }
}
