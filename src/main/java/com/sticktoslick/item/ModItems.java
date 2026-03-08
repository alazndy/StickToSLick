package com.sticktoslick.item;

import com.sticktoslick.StickToSlick;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Item registry for all Stick to Slick weapons and materials.
 */
public final class ModItems {
    private ModItems() {
    }

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS,
            StickToSlick.MODID);

    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister
            .create(Registries.CREATIVE_MODE_TAB, StickToSlick.MODID);

    // ─── Weapon Items ───────────────────────────────────────
    // Level 1: Root
    public static final RegistryObject<Item> WEAPON_WOODEN_STICK = ITEMS.register("weapon_wooden_stick",
            StarterStickItem::new);

    // Level 5: Base Branches
    public static final RegistryObject<Item> WEAPON_DAGGER = ITEMS.register("weapon_dagger", StarterStickItem::new);
    public static final RegistryObject<Item> WEAPON_SHORTSWORD = ITEMS.register("weapon_shortsword",
            StarterStickItem::new);
    public static final RegistryObject<Item> WEAPON_SPEAR = ITEMS.register("weapon_spear", StarterStickItem::new);
    public static final RegistryObject<Item> WEAPON_CLUB = ITEMS.register("weapon_club", StarterStickItem::new);

    // Level 10: Tier 1
    public static final RegistryObject<Item> WEAPON_DIRK = ITEMS.register("weapon_dirk", StarterStickItem::new);
    public static final RegistryObject<Item> WEAPON_ARMING_SWORD = ITEMS.register("weapon_arming_sword",
            StarterStickItem::new);
    public static final RegistryObject<Item> WEAPON_TRIDENT = ITEMS.register("weapon_trident", StarterStickItem::new);
    public static final RegistryObject<Item> WEAPON_MACE = ITEMS.register("weapon_mace", StarterStickItem::new);

    // Level 15: First Hybrids
    public static final RegistryObject<Item> WEAPON_SABER = ITEMS.register("weapon_saber", StarterStickItem::new);
    public static final RegistryObject<Item> WEAPON_LONGSWORD = ITEMS.register("weapon_longsword",
            StarterStickItem::new);
    public static final RegistryObject<Item> WEAPON_LUCERNE_HAMMER = ITEMS.register("weapon_lucerne_hammer",
            StarterStickItem::new);
    public static final RegistryObject<Item> WEAPON_MORNING_STAR = ITEMS.register("weapon_morning_star",
            StarterStickItem::new);

    // Level 20: Specialization
    public static final RegistryObject<Item> WEAPON_KATANA = ITEMS.register("weapon_katana", StarterStickItem::new);
    public static final RegistryObject<Item> WEAPON_BASTARD_SWORD = ITEMS.register("weapon_bastard_sword",
            StarterStickItem::new);
    public static final RegistryObject<Item> WEAPON_HALBERD = ITEMS.register("weapon_halberd", StarterStickItem::new);
    public static final RegistryObject<Item> WEAPON_WARHAMMER = ITEMS.register("weapon_warhammer",
            StarterStickItem::new);

    // Level 25: Heavy Hybrids
    public static final RegistryObject<Item> WEAPON_NODACHI = ITEMS.register("weapon_nodachi", StarterStickItem::new);
    public static final RegistryObject<Item> WEAPON_CLAYMORE = ITEMS.register("weapon_claymore", StarterStickItem::new);
    public static final RegistryObject<Item> WEAPON_PARTISAN = ITEMS.register("weapon_partisan", StarterStickItem::new);
    public static final RegistryObject<Item> WEAPON_GREAT_MAUL = ITEMS.register("weapon_great_maul",
            StarterStickItem::new);

    // Level 30: Historical Peak
    public static final RegistryObject<Item> WEAPON_ZWEIHANDER = ITEMS.register("weapon_zweihander",
            StarterStickItem::new);
    public static final RegistryObject<Item> WEAPON_WINGED_LANCE = ITEMS.register("weapon_winged_lance",
            StarterStickItem::new);
    public static final RegistryObject<Item> WEAPON_EXECUTIONERS_AXE = ITEMS.register("weapon_executioners_axe",
            StarterStickItem::new);

    // Level 40: Mythological
    public static final RegistryObject<Item> WEAPON_DRAGON_SLAYER = ITEMS.register("weapon_dragon_slayer",
            StarterStickItem::new);
    public static final RegistryObject<Item> WEAPON_GUNGNIR = ITEMS.register("weapon_gungnir", StarterStickItem::new);
    public static final RegistryObject<Item> WEAPON_VOID_CRUSHER = ITEMS.register("weapon_void_crusher",
            StarterStickItem::new);

    // Level 50: Godly Finales
    public static final RegistryObject<Item> WEAPON_GENESIS = ITEMS.register("weapon_genesis", StarterStickItem::new);
    public static final RegistryObject<Item> WEAPON_LONGINUS = ITEMS.register("weapon_longinus", StarterStickItem::new);
    public static final RegistryObject<Item> WEAPON_ATLAS = ITEMS.register("weapon_atlas", StarterStickItem::new);

    // ─── Creative Tab ───────────────────────────────────────
    public static final RegistryObject<CreativeModeTab> STICK_TO_SLICK_TAB = CREATIVE_TABS.register("sticktoslick_tab",
            () -> CreativeModeTab.builder()
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .title(Component.literal("Stick to Slick"))
                    .icon(() -> WEAPON_WOODEN_STICK.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        // Level 1
                        output.accept(WEAPON_WOODEN_STICK.get());

                        // Level 5
                        output.accept(WEAPON_DAGGER.get());
                        output.accept(WEAPON_SHORTSWORD.get());
                        output.accept(WEAPON_SPEAR.get());
                        output.accept(WEAPON_CLUB.get());

                        // Level 10
                        output.accept(WEAPON_DIRK.get());
                        output.accept(WEAPON_ARMING_SWORD.get());
                        output.accept(WEAPON_TRIDENT.get());
                        output.accept(WEAPON_MACE.get());

                        // Level 15
                        output.accept(WEAPON_SABER.get());
                        output.accept(WEAPON_LONGSWORD.get());
                        output.accept(WEAPON_LUCERNE_HAMMER.get());
                        output.accept(WEAPON_MORNING_STAR.get());

                        // Level 20
                        output.accept(WEAPON_KATANA.get());
                        output.accept(WEAPON_BASTARD_SWORD.get());
                        output.accept(WEAPON_HALBERD.get());
                        output.accept(WEAPON_WARHAMMER.get());

                        // Level 25
                        output.accept(WEAPON_NODACHI.get());
                        output.accept(WEAPON_CLAYMORE.get());
                        output.accept(WEAPON_PARTISAN.get());
                        output.accept(WEAPON_GREAT_MAUL.get());

                        // Level 30
                        output.accept(WEAPON_ZWEIHANDER.get());
                        output.accept(WEAPON_WINGED_LANCE.get());
                        output.accept(WEAPON_EXECUTIONERS_AXE.get());

                        // Level 40
                        output.accept(WEAPON_DRAGON_SLAYER.get());
                        output.accept(WEAPON_GUNGNIR.get());
                        output.accept(WEAPON_VOID_CRUSHER.get());

                        // Level 50
                        output.accept(WEAPON_GENESIS.get());
                        output.accept(WEAPON_LONGINUS.get());
                        output.accept(WEAPON_ATLAS.get());
                    })
                    .build());

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        CREATIVE_TABS.register(modEventBus);
    }
}
