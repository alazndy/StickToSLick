package com.sticktoslick.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ModClientConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.BooleanValue ENABLE_SCREENSHAKE;
    public static final ForgeConfigSpec.BooleanValue ENABLE_VIGNETTE;

    static {
        BUILDER.push("Visuals");

        ENABLE_SCREENSHAKE = BUILDER
                .comment("Enable camera shake effect when hitting mobs")
                .define("enableScreenshake", true);

        ENABLE_VIGNETTE = BUILDER
                .comment("Enable red screen flash (vignette) when landing a critical or powerful hit")
                .define("enableVignette", true);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
