package com.sticktoslick.event;

import com.sticktoslick.data.EvolutionPath;
import com.sticktoslick.data.WeaponClassData;
import com.sticktoslick.data.WeaponNBTHelper;
import com.sticktoslick.data.WeaponTraitData;
import com.sticktoslick.item.StarterStickItem;
import com.sticktoslick.item.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * Handles Shift + Right Click interactions, GUI-triggered upgrades,
 * and the special "stick rubbing" mechanic to obtain the first weapon.
 */
public class ItemInteractionHandler {

    /**
     * Called from the networking packet when a player clicks a button in the GUI.
     * Also used manually by Shift+Right-Click.
     */
    public static void handleUpgradeFromPacket(ServerPlayer player, ItemStack stack, String type, String extraData) {
        if (!(stack.getItem() instanceof StarterStickItem))
            return;
        if (!WeaponNBTHelper.hasWeaponData(stack))
            return;

        int level = WeaponNBTHelper.getLevel(stack);
        String currentClass = WeaponNBTHelper.getWeaponClass(stack);

        switch (type) {
            case "damage":
                if (consumeMaterial(player, Items.LAPIS_LAZULI)) {
                    WeaponNBTHelper.addStatDamage(stack, 1);
                    playUpgradeEffects(player, SoundEvents.ANVIL_USE, 1.5f);
                    player.sendSystemMessage(Component.literal("🔵 Hasar Yükseltildi!").withStyle(ChatFormatting.BLUE));
                }
                break;
            case "speed":
                if (consumeMaterial(player, Items.REDSTONE)) {
                    WeaponNBTHelper.addStatAttackSpeed(stack, 1);
                    playUpgradeEffects(player, SoundEvents.ANVIL_USE, 1.5f);
                    player.sendSystemMessage(
                            Component.literal("🔴 Saldırı Hızı Yükseltildi!").withStyle(ChatFormatting.RED));
                }
                break;
            case "movement":
                if (consumeMaterial(player, Items.SUGAR)) {
                    WeaponNBTHelper.addStatMoveSpeed(stack, 1);
                    playUpgradeEffects(player, SoundEvents.ANVIL_USE, 1.5f);
                    player.sendSystemMessage(
                            Component.literal("⚪ Koşma Hızı Yükseltildi!").withStyle(ChatFormatting.WHITE));
                }
                break;
            case "knockback":
                if (consumeMaterial(player, Items.SLIME_BALL)) {
                    WeaponNBTHelper.addStatKnockback(stack, 1);
                    playUpgradeEffects(player, SoundEvents.ANVIL_USE, 1.5f);
                    player.sendSystemMessage(
                            Component.literal("🟢 Geri İtme Yükseltildi!").withStyle(ChatFormatting.GREEN));
                }
                break;
            case "evolve":
                if (extraData == null || extraData.isEmpty()) {
                    Item catalyst = findEvolutionCatalyst(player, currentClass, level);
                    if (catalyst != null && consumeMaterial(player, catalyst)) {
                        String targetClass = EvolutionPath.getTargetClass(currentClass, catalyst);
                        WeaponNBTHelper.setWeaponClass(stack, targetClass);
                        stack.setDamageValue(0);
                        playUpgradeEffects(player, SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 1.0f);

                        String newName = WeaponClassData.get(targetClass).displayNameKey();
                        player.sendSystemMessage(Component.literal("★ SİLAHIN EVRİMLEŞTİ! → ")
                                .withStyle(ChatFormatting.GOLD)
                                .append(Component.translatable(newName).withStyle(ChatFormatting.AQUA)));
                    }
                } else {
                    java.util.Optional<EvolutionPath.Evolution> evoOpt = EvolutionPath.getEvolutions(currentClass)
                            .stream().filter(e -> e.targetClass().equals(extraData)).findFirst();
                    if (evoOpt.isPresent()) {
                        EvolutionPath.Evolution evo = evoOpt.get();
                        if (consumeMaterial(player, evo.catalyst())) {
                            WeaponNBTHelper.setWeaponClass(stack, evo.targetClass());
                            stack.setDamageValue(0);
                            playUpgradeEffects(player, SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 1.0f);

                            String newName = WeaponClassData.get(evo.targetClass()).displayNameKey();
                            player.sendSystemMessage(Component.literal("★ SİLAHIN EVRİMLEŞTİ! → ")
                                    .withStyle(ChatFormatting.GOLD)
                                    .append(Component.translatable(newName).withStyle(ChatFormatting.AQUA)));
                        }
                    }
                }
                break;
            case "trait":
                WeaponTraitData.TraitInfo traitInfo = WeaponTraitData.get(currentClass);
                if (consumeMaterial(player, traitInfo.upgradeMaterial())) {
                    WeaponNBTHelper.addTraitLevel(stack, 1);
                    int newTraitLvl = WeaponNBTHelper.getTraitLevel(stack);
                    playUpgradeEffects(player, SoundEvents.ENCHANTMENT_TABLE_USE, 1.2f);
                    player.sendSystemMessage(Component
                            .literal(
                                    "✦ " + traitInfo.traitName() + " Lv." + newTraitLvl + " → " + traitInfo.traitDesc())
                            .withStyle(ChatFormatting.LIGHT_PURPLE));
                }
                break;
            case "repair":
                Item requiredItem = getRepairMaterialForTier(level);
                if (consumeMaterial(player, requiredItem)) {
                    stack.setDamageValue(0);
                    playUpgradeEffects(player, SoundEvents.ANVIL_USE, 1.2f);
                    player.sendSystemMessage(
                            Component.literal("🛠 Silah başarıyla tamir edildi!").withStyle(ChatFormatting.GREEN));
                }
                break;
            case "extra_slot":
                if (consumeMaterial(player, Items.NETHER_STAR)) {
                    WeaponNBTHelper.addExtraSlot(stack);
                    playUpgradeEffects(player, SoundEvents.BEACON_ACTIVATE, 1.0f);
                    player.sendSystemMessage(
                            Component.literal("⭐ Büyü Kapasitesi Artırıldı!").withStyle(ChatFormatting.YELLOW));
                }
                break;
            case "start_quest":
                ItemStack offhand = player.getOffhandItem();
                if (offhand.is(Items.ENCHANTED_BOOK) && !WeaponNBTHelper.hasActiveQuest(stack)) {
                    var enchantments = net.minecraft.world.item.enchantment.EnchantmentHelper.getEnchantments(offhand);
                    if (!enchantments.isEmpty()) {
                        var entry = enchantments.entrySet().iterator().next();
                        net.minecraft.world.item.enchantment.Enchantment ench = entry.getKey();
                        int levelFromBook = entry.getValue();

                        int currentEnchants = stack.getEnchantmentTags().size();
                        int maxSlots = 3 + WeaponNBTHelper.getExtraSlots(stack);
                        if (currentEnchants >= maxSlots && net.minecraft.world.item.enchantment.EnchantmentHelper
                                .getItemEnchantmentLevel(ench, stack) == 0) {
                            player.sendSystemMessage(
                                    Component.literal("⚠ Maksimum Büyü Sınırına Ulaştın! Nether Yıldızı Kullan!")
                                            .withStyle(ChatFormatting.RED));
                            return;
                        }

                        int existingLvl = net.minecraft.world.item.enchantment.EnchantmentHelper
                                .getItemEnchantmentLevel(ench, stack);
                        int targetLevel = Math.max(levelFromBook, existingLvl + 1);

                        if (targetLevel > ench.getMaxLevel()) {
                            player.sendSystemMessage(Component.literal("⚠ Bu büyü zaten maksimum seviyede!")
                                    .withStyle(ChatFormatting.RED));
                            return;
                        }

                        consumeMaterial(player, Items.ENCHANTED_BOOK);

                        com.sticktoslick.data.EnchantmentQuestManager.QuestInfo questInfo = com.sticktoslick.data.EnchantmentQuestManager
                                .getInfoForLevel(ench, targetLevel);
                        net.minecraft.resources.ResourceLocation enchId = net.minecraftforge.registries.ForgeRegistries.ENCHANTMENTS
                                .getKey(ench);

                        if (enchId != null) {
                            WeaponNBTHelper.startQuest(stack, enchId.toString(), questInfo.goal, questInfo.type.name(),
                                    targetLevel);
                            playUpgradeEffects(player, SoundEvents.BOOK_PAGE_TURN, 1.0f);
                            player.sendSystemMessage(Component
                                    .literal("📜 Büyü Görevi Başladı: "
                                            + Component.translatable(ench.getDescriptionId()).getString())
                                    .withStyle(ChatFormatting.GOLD));
                        }
                    }
                }
                break;
        }
    }

    private static Item getRepairMaterialForTier(int level) {
        if (level < 15)
            return Items.IRON_INGOT;
        if (level < 25)
            return Items.GOLD_INGOT;
        if (level < 40)
            return Items.DIAMOND;
        return Items.NETHERITE_SCRAP;
    }

    private static boolean consumeMaterial(Player player, Item material) {
        if (player.getOffhandItem().is(material)) {
            player.getOffhandItem().shrink(1);
            return true;
        }
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack invStack = player.getInventory().getItem(i);
            if (invStack.is(material)) {
                invStack.shrink(1);
                return true;
            }
        }
        return false;
    }

    private static Item findEvolutionCatalyst(Player player, String currentClass, int level) {
        ItemStack off = player.getOffhandItem();
        if (EvolutionPath.canEvolve(currentClass, level, off.getItem()))
            return off.getItem();

        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack invStack = player.getInventory().getItem(i);
            if (!invStack.isEmpty() && EvolutionPath.canEvolve(currentClass, level, invStack.getItem())) {
                return invStack.getItem();
            }
        }
        return null;
    }

    private static void playUpgradeEffects(Player player, net.minecraft.sounds.SoundEvent sound, float pitch) {
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                sound, SoundSource.PLAYERS, 1.0f, pitch);
    }

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        ItemStack stack = event.getItemStack();
        net.minecraft.world.level.Level level = event.getLevel();

        // 1. Check item: must be a vanilla stick
        if (stack.is(Items.STICK)) {
            BlockState state = level.getBlockState(event.getPos());

            // 2. Check block: must be stone-like (detect via sound type or tag)
            if (state.getSoundType() == SoundType.STONE || state.is(BlockTags.BASE_STONE_OVERWORLD)) {

                // --- Rubbing Animation & Effects ---
                player.swing(event.getHand());

                if (level.isClientSide) {
                    // Spawn stone particles at the click point
                    Vec3 hit = event.getHitVec().getLocation();
                    for (int i = 0; i < 5; i++) {
                        level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, state),
                                hit.x, hit.y, hit.z,
                                (Math.random() - 0.5) * 0.1, 0.1, (Math.random() - 0.5) * 0.1);
                    }
                } else {
                    // Play scraping/hit sound
                    level.playSound(null, event.getPos(), SoundEvents.GRINDSTONE_USE, SoundSource.PLAYERS, 0.4f,
                            1.5f + (float) Math.random() * 0.5f);

                    // Track progress using NBT on the stick
                    net.minecraft.nbt.CompoundTag tag = stack.getOrCreateTag();
                    int progress = tag.getInt("StickToSlick_Rubbing") + 1;
                    tag.putInt("StickToSlick_Rubbing", progress);

                    if (progress >= 10) {
                        // Transformation!
                        ItemStack newItem = new ItemStack(ModItems.WEAPON_WOODEN_STICK.get());
                        WeaponNBTHelper.initializeWeapon(newItem);

                        // Name the weapon after its creator
                        newItem.setHoverName(Component.literal(player.getName().getString() + "'ın Silahı")
                                .withStyle(ChatFormatting.AQUA, ChatFormatting.ITALIC));

                        // Consume one stick and give the new one
                        if (stack.getCount() > 1) {
                            stack.shrink(1);
                            if (!player.getInventory().add(newItem)) {
                                player.drop(newItem, false);
                            }
                        } else {
                            player.setItemInHand(event.getHand(), newItem);
                        }

                        // Success effects
                        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                                SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0f, 0.8f);
                        player.sendSystemMessage(Component.literal("✨ Çubuğu yontarak bir silah haline getirdin!")
                                .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
                    }
                }

                // Allow interaction without vanilla block behavior (like opening chest/door)
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        ItemStack stack = event.getItemStack();

        if (player.isShiftKeyDown() && stack.getItem() instanceof StarterStickItem
                && WeaponNBTHelper.hasWeaponData(stack)) {
            if (player instanceof ServerPlayer serverPlayer) {
                // Determine target via offhand for shift-click backwards compatibility (if
                // needed)
                String upgradeType = determineUpgradeTypeFromOffhand(player, stack);
                String extraData = "";
                if ("evolve".equals(upgradeType)) {
                    Item catalyst = findEvolutionCatalyst(player, WeaponNBTHelper.getWeaponClass(stack),
                            WeaponNBTHelper.getLevel(stack));
                    if (catalyst != null) {
                        extraData = EvolutionPath.getTargetClass(WeaponNBTHelper.getWeaponClass(stack), catalyst);
                    }
                }
                handleUpgradeFromPacket(serverPlayer, stack, upgradeType, extraData);
            }
            event.setCanceled(true);
        }
    }

    private String determineUpgradeTypeFromOffhand(Player player, ItemStack stack) {
        ItemStack off = player.getOffhandItem();
        if (off.is(Items.LAPIS_LAZULI))
            return "damage";
        if (off.is(Items.REDSTONE))
            return "speed";
        if (off.is(Items.SUGAR))
            return "movement";
        if (off.is(Items.SLIME_BALL))
            return "knockback";
        if (off.is(Items.NETHER_STAR))
            return "extra_slot";

        if (off.is(Items.ENCHANTED_BOOK) && !WeaponNBTHelper.hasActiveQuest(stack)) {
            if (!net.minecraft.world.item.enchantment.EnchantmentHelper.getEnchantments(off).isEmpty()) {
                return "start_quest";
            }
        }

        if (WeaponNBTHelper.hasWeaponData(stack)) {
            int level = WeaponNBTHelper.getLevel(stack);
            if (off.is(getRepairMaterialForTier(level)) && StarterStickItem.isBroken(stack)) {
                return "repair";
            }
        }

        return "evolve";
    }
}
