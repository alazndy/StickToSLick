package com.sticktoslick.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.sticktoslick.StickToSlick;
import com.sticktoslick.data.WeaponClassData;
import com.sticktoslick.data.WeaponNBTHelper;
import com.sticktoslick.item.ModItems;
import com.sticktoslick.item.StarterStickItem;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Debug and utility commands for Stick to Slick.
 * All commands are under /ss
 *
 * /ss gui - Open weapon upgrade GUI
 * /ss give <class> - Give a weapon of the specified class
 * /ss setclass <class> - Change held weapon's class
 * /ss setlevel <level> - Set held weapon's level
 * /ss setstat <stat> <amount> - Set a stat on the held weapon
 * /ss maxout - Max out all stats on held weapon
 * /ss list - List all weapon classes
 * /ss info - Show info about held weapon
 */
@Mod.EventBusSubscriber(modid = StickToSlick.MODID)
public class ModCommands {

        // Suggest all weapon class names
        private static final SuggestionProvider<CommandSourceStack> WEAPON_CLASS_SUGGESTIONS = (context,
                        builder) -> SharedSuggestionProvider.suggest(
                                        WeaponClassData.WEAPON_STATS.keySet(), builder);

        // Suggest stat names
        private static final SuggestionProvider<CommandSourceStack> STAT_SUGGESTIONS = (context,
                        builder) -> SharedSuggestionProvider.suggest(
                                        new String[] { "damage", "speed", "movement", "knockback" }, builder);

        @SubscribeEvent
        public static void onRegisterCommands(RegisterCommandsEvent event) {
                CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

                dispatcher.register(Commands.literal("ss")
                                // /ss gui - Open weapon upgrade GUI
                                .then(Commands.literal("gui")
                                                .executes(context -> {
                                                        ServerPlayer player = context.getSource()
                                                                        .getPlayerOrException();
                                                        ItemStack stack = player.getMainHandItem();
                                                        if (stack.getItem() instanceof StarterStickItem) {
                                                                com.sticktoslick.network.ModMessages.sendToPlayer(
                                                                                new com.sticktoslick.network.S2COpenUpgradeScreenPacket(),
                                                                                player);
                                                                return 1;
                                                        }
                                                        context.getSource().sendFailure(
                                                                        Component.literal("Elinde silah yok!"));
                                                        return 0;
                                                }))

                                // /ss give <class> - Give a weapon with the specified class
                                .then(Commands.literal("give")
                                                .then(Commands.argument("class", StringArgumentType.string())
                                                                .suggests(WEAPON_CLASS_SUGGESTIONS)
                                                                .executes(context -> {
                                                                        ServerPlayer player = context.getSource()
                                                                                        .getPlayerOrException();
                                                                        String weaponClass = StringArgumentType
                                                                                        .getString(context, "class");

                                                                        if (!WeaponClassData.WEAPON_STATS
                                                                                        .containsKey(weaponClass)) {
                                                                                context.getSource().sendFailure(
                                                                                                Component.literal(
                                                                                                                "Geçersiz silah sınıfı: "
                                                                                                                                + weaponClass));
                                                                                return 0;
                                                                        }

                                                                        ItemStack newWeapon = new ItemStack(
                                                                                        ModItems.WEAPON_WOODEN_STICK
                                                                                                        .get());
                                                                        WeaponNBTHelper.initializeWeapon(newWeapon);
                                                                        WeaponNBTHelper.setWeaponClass(newWeapon,
                                                                                        weaponClass);

                                                                        // Set appropriate level based on tier
                                                                        int level = getDefaultLevelForClass(
                                                                                        weaponClass);
                                                                        WeaponNBTHelper.setLevel(newWeapon, level);

                                                                        // Name it
                                                                        newWeapon.setHoverName(Component
                                                                                        .literal("[DEBUG] "
                                                                                                        + weaponClass)
                                                                                        .withStyle(ChatFormatting.LIGHT_PURPLE));

                                                                        if (!player.getInventory().add(newWeapon)) {
                                                                                player.drop(newWeapon, false);
                                                                        }

                                                                        context.getSource()
                                                                                        .sendSuccess(() -> Component
                                                                                                        .literal("✔ " + weaponClass
                                                                                                                        + " (Lv."
                                                                                                                        + level
                                                                                                                        + ") verildi!")
                                                                                                        .withStyle(ChatFormatting.GREEN),
                                                                                                        false);
                                                                        return 1;
                                                                })))

                                // /ss setclass <class> - Change currently held weapon's class
                                .then(Commands.literal("setclass")
                                                .then(Commands.argument("class", StringArgumentType.string())
                                                                .suggests(WEAPON_CLASS_SUGGESTIONS)
                                                                .executes(context -> {
                                                                        ServerPlayer player = context.getSource()
                                                                                        .getPlayerOrException();
                                                                        ItemStack stack = player.getMainHandItem();

                                                                        if (!(stack.getItem() instanceof StarterStickItem)
                                                                                        || !WeaponNBTHelper
                                                                                                        .hasWeaponData(stack)) {
                                                                                context.getSource().sendFailure(
                                                                                                Component.literal(
                                                                                                                "Elinde silah yok!"));
                                                                                return 0;
                                                                        }

                                                                        String weaponClass = StringArgumentType
                                                                                        .getString(context, "class");
                                                                        if (!WeaponClassData.WEAPON_STATS
                                                                                        .containsKey(weaponClass)) {
                                                                                context.getSource().sendFailure(
                                                                                                Component.literal(
                                                                                                                "Geçersiz sınıf: "
                                                                                                                                + weaponClass));
                                                                                return 0;
                                                                        }

                                                                        WeaponNBTHelper.setWeaponClass(stack,
                                                                                        weaponClass);
                                                                        stack.setDamageValue(0); // Full repair on class
                                                                                                 // change

                                                                        context.getSource()
                                                                                        .sendSuccess(() -> Component
                                                                                                        .literal("✔ Silah sınıfı → "
                                                                                                                        + weaponClass)
                                                                                                        .withStyle(ChatFormatting.AQUA),
                                                                                                        false);
                                                                        return 1;
                                                                })))

                                // /ss setlevel <level> - Set held weapon's level
                                .then(Commands.literal("setlevel")
                                                .then(Commands.argument("level", IntegerArgumentType.integer(0, 50))
                                                                .executes(context -> {
                                                                        ServerPlayer player = context.getSource()
                                                                                        .getPlayerOrException();
                                                                        ItemStack stack = player.getMainHandItem();

                                                                        if (!(stack.getItem() instanceof StarterStickItem)
                                                                                        || !WeaponNBTHelper
                                                                                                        .hasWeaponData(stack)) {
                                                                                context.getSource().sendFailure(
                                                                                                Component.literal(
                                                                                                                "Elinde silah yok!"));
                                                                                return 0;
                                                                        }

                                                                        int level = IntegerArgumentType
                                                                                        .getInteger(context, "level");
                                                                        WeaponNBTHelper.setLevel(stack, level);
                                                                        WeaponNBTHelper.setXP(stack, 0);

                                                                        context.getSource().sendSuccess(() -> Component
                                                                                        .literal("✔ Seviye → " + level)
                                                                                        .withStyle(ChatFormatting.GREEN),
                                                                                        false);
                                                                        return 1;
                                                                })))

                                // /ss setstat <stat> <amount> - Set a specific stat
                                .then(Commands.literal("setstat")
                                                .then(Commands.argument("stat", StringArgumentType.string())
                                                                .suggests(STAT_SUGGESTIONS)
                                                                .then(Commands.argument("amount",
                                                                                IntegerArgumentType.integer(0, 100))
                                                                                .executes(context -> {
                                                                                        ServerPlayer player = context
                                                                                                        .getSource()
                                                                                                        .getPlayerOrException();
                                                                                        ItemStack stack = player
                                                                                                        .getMainHandItem();

                                                                                        if (!(stack.getItem() instanceof StarterStickItem)
                                                                                                        || !WeaponNBTHelper
                                                                                                                        .hasWeaponData(stack)) {
                                                                                                context.getSource()
                                                                                                                .sendFailure(
                                                                                                                                Component.literal(
                                                                                                                                                "Elinde silah yok!"));
                                                                                                return 0;
                                                                                        }

                                                                                        String stat = StringArgumentType
                                                                                                        .getString(context,
                                                                                                                        "stat");
                                                                                        int amount = IntegerArgumentType
                                                                                                        .getInteger(context,
                                                                                                                        "amount");

                                                                                        switch (stat) {
                                                                                                case "damage" ->
                                                                                                        WeaponNBTHelper.setStatDamage(
                                                                                                                        stack,
                                                                                                                        amount);
                                                                                                case "speed" ->
                                                                                                        WeaponNBTHelper.setStatAttackSpeed(
                                                                                                                        stack,
                                                                                                                        amount);
                                                                                                case "movement" ->
                                                                                                        WeaponNBTHelper.setStatMoveSpeed(
                                                                                                                        stack,
                                                                                                                        amount);
                                                                                                case "knockback" ->
                                                                                                        WeaponNBTHelper.setStatKnockback(
                                                                                                                        stack,
                                                                                                                        amount);
                                                                                                default -> {
                                                                                                        context.getSource()
                                                                                                                        .sendFailure(
                                                                                                                                        Component.literal(
                                                                                                                                                        "Geçersiz stat: "
                                                                                                                                                                        + stat
                                                                                                                                                                        + " (damage/speed/movement/knockback)"));
                                                                                                        return 0;
                                                                                                }
                                                                                        }

                                                                                        context.getSource()
                                                                                                        .sendSuccess(() -> Component
                                                                                                                        .literal("✔ " + stat
                                                                                                                                        + " → "
                                                                                                                                        + amount)
                                                                                                                        .withStyle(ChatFormatting.YELLOW),
                                                                                                                        false);
                                                                                        return 1;
                                                                                }))))

                                // /ss maxout - Max out everything on held weapon
                                .then(Commands.literal("maxout")
                                                .executes(context -> {
                                                        ServerPlayer player = context.getSource()
                                                                        .getPlayerOrException();
                                                        ItemStack stack = player.getMainHandItem();

                                                        if (!(stack.getItem() instanceof StarterStickItem)
                                                                        || !WeaponNBTHelper.hasWeaponData(stack)) {
                                                                context.getSource().sendFailure(
                                                                                Component.literal("Elinde silah yok!"));
                                                                return 0;
                                                        }

                                                        WeaponNBTHelper.setLevel(stack, 50);
                                                        WeaponNBTHelper.setXP(stack, 0);
                                                        WeaponNBTHelper.setStatDamage(stack, 20);
                                                        WeaponNBTHelper.setStatAttackSpeed(stack, 20);
                                                        WeaponNBTHelper.setStatMoveSpeed(stack, 10);
                                                        WeaponNBTHelper.setStatKnockback(stack, 10);
                                                        stack.setDamageValue(0);

                                                        context.getSource().sendSuccess(() -> Component
                                                                        .literal("✔ Tüm statlar MAXED!")
                                                                        .withStyle(ChatFormatting.GOLD,
                                                                                        ChatFormatting.BOLD),
                                                                        false);
                                                        return 1;
                                                }))

                                // /ss list - List all weapon classes
                                .then(Commands.literal("list")
                                                .executes(context -> {
                                                        StringBuilder sb = new StringBuilder(
                                                                        "§6─── Silah Sınıfları ───\n");

                                                        sb.append("§7Root (Lv1): §fweapon_wooden_stick\n");
                                                        sb.append("§7Base (Lv5): §fweapon_dagger, weapon_shortsword, weapon_spear, weapon_club\n");
                                                        sb.append("§7Tier 1 (Lv10): §fweapon_dirk, weapon_arming_sword, weapon_trident, weapon_mace\n");
                                                        sb.append("§7Hybrid 1 (Lv15): §fweapon_saber, weapon_longsword, weapon_lucerne_hammer, weapon_morning_star\n");
                                                        sb.append("§7Special (Lv20): §fweapon_katana, weapon_bastard_sword, weapon_halberd, weapon_warhammer\n");
                                                        sb.append("§7Heavy Hybrid (Lv25): §fweapon_nodachi, weapon_claymore, weapon_partisan, weapon_great_maul\n");
                                                        sb.append("§7Historical (Lv30): §fweapon_zweihander, weapon_winged_lance, weapon_executioners_axe\n");
                                                        sb.append("§7Mythological (Lv40): §fweapon_dragon_slayer, weapon_gungnir, weapon_void_crusher\n");
                                                        sb.append("§7Godly (Lv50): §fweapon_genesis, weapon_longinus, weapon_atlas");

                                                        context.getSource().sendSuccess(
                                                                        () -> Component.literal(sb.toString()), false);
                                                        return 1;
                                                }))

                                // /ss info - Show held weapon info
                                .then(Commands.literal("info")
                                                .executes(context -> {
                                                        ServerPlayer player = context.getSource()
                                                                        .getPlayerOrException();
                                                        ItemStack stack = player.getMainHandItem();

                                                        if (!(stack.getItem() instanceof StarterStickItem)
                                                                        || !WeaponNBTHelper.hasWeaponData(stack)) {
                                                                context.getSource().sendFailure(
                                                                                Component.literal("Elinde silah yok!"));
                                                                return 0;
                                                        }

                                                        String weaponClass = WeaponNBTHelper.getWeaponClass(stack);
                                                        int level = WeaponNBTHelper.getLevel(stack);
                                                        int xp = WeaponNBTHelper.getXP(stack);
                                                        WeaponClassData.WeaponStats stats = WeaponClassData
                                                                        .get(weaponClass);

                                                        StringBuilder sb = new StringBuilder();
                                                        sb.append("§6═══ Silah Bilgisi ═══\n");
                                                        sb.append("§7Sınıf: §b").append(weaponClass).append("\n");
                                                        sb.append("§7Seviye: §a").append(level).append("/50\n");
                                                        sb.append("§7XP: §e").append(xp).append("\n");
                                                        sb.append("§7Base Hasar: §9").append(stats.baseDamage())
                                                                        .append("\n");
                                                        sb.append("§7Base Hız: §c").append(stats.baseAttackSpeed())
                                                                        .append("\n");
                                                        sb.append("§7Dayanıklılık: §f").append(stats.maxDurability())
                                                                        .append("\n");
                                                        sb.append("§7Enchant Slot: §d").append(stats.enchantSlots())
                                                                        .append("\n");
                                                        sb.append("§7─── Bonus Stats ───\n");
                                                        sb.append("§7 +Damage: §9")
                                                                        .append(WeaponNBTHelper.getStatDamage(stack))
                                                                        .append("\n");
                                                        sb.append("§7 +AtkSpd: §c")
                                                                        .append(WeaponNBTHelper
                                                                                        .getStatAttackSpeed(stack))
                                                                        .append("\n");
                                                        sb.append("§7 +MoveSpd: §f")
                                                                        .append(WeaponNBTHelper.getStatMoveSpeed(stack))
                                                                        .append("\n");
                                                        sb.append("§7 +Knockback: §a").append(
                                                                        WeaponNBTHelper.getStatKnockback(stack));

                                                        context.getSource().sendSuccess(
                                                                        () -> Component.literal(sb.toString()), false);
                                                        return 1;
                                                })));
        }

        /**
         * Returns the minimum level a weapon class should have.
         */
        private static int getDefaultLevelForClass(String weaponClass) {
                return switch (weaponClass) {
                        case "weapon_wooden_stick" -> 1;
                        case "weapon_dagger", "weapon_shortsword", "weapon_spear", "weapon_club" -> 5;
                        case "weapon_dirk", "weapon_arming_sword", "weapon_trident", "weapon_mace" -> 10;
                        case "weapon_saber", "weapon_longsword", "weapon_lucerne_hammer", "weapon_morning_star" -> 15;
                        case "weapon_katana", "weapon_bastard_sword", "weapon_halberd", "weapon_warhammer" -> 20;
                        case "weapon_nodachi", "weapon_claymore", "weapon_partisan", "weapon_great_maul" -> 25;
                        case "weapon_zweihander", "weapon_winged_lance", "weapon_executioners_axe" -> 30;
                        case "weapon_dragon_slayer", "weapon_gungnir", "weapon_void_crusher" -> 40;
                        case "weapon_genesis", "weapon_longinus", "weapon_atlas" -> 50;
                        default -> 1;
                };
        }
}
