package one.yuqas.overlay;

import com.google.gson.GsonBuilder;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import java.awt.Color;

public class OverlayConfig implements ModMenuApi {


    @SerialEntry public static boolean enabled = true;
    @SerialEntry public static Color fillColor = new Color(195, 234, 243, 50);
    @SerialEntry public static Color lineColor = new Color(255, 255, 255, 255);
    @SerialEntry public static boolean showLine = true;
    @SerialEntry public static float lineWidth = 1.0f;
    @SerialEntry public static boolean throughWallsFill = false;
    @SerialEntry public static boolean throughWallsLine = false;
    @SerialEntry public static boolean animEnabled = false;
    @SerialEntry public static float animSpeed = 8.0f;
    @SerialEntry public static boolean onlyCrystalBase = false;
    @SerialEntry public static OverlayAnimations.Mode animationMode = OverlayAnimations.Mode.EASE_OUT_CUBIC;

    public static ConfigClassHandler<OverlayConfig> HANDLER = ConfigClassHandler.createBuilder(OverlayConfig.class)
            .id(Identifier.fromNamespaceAndPath("config", "blockaccent"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("block_accent.json"))
                    .appendGsonBuilder(GsonBuilder::setPrettyPrinting)
                    .setJson5(true)
                    .build())
            .build();

    static {
        HANDLER.load();
    }

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return OverlayConfig::create;
    }

    public static Screen create(Screen parent) {
        return YetAnotherConfigLib.createBuilder()
                .title(Component.translatable("blockaccent.config.title"))
                .save(HANDLER::save)
                .category(ConfigCategory.createBuilder()
                        .name(Component.translatable("blockaccent.config.group.general"))
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.translatable("blockaccent.config.enabled"))
                                .description(OptionDescription.of(Component.translatable("blockaccent.config.enabled.desc")))
                                .binding(true, () -> enabled, v -> enabled = v)
                                .controller(BooleanControllerBuilder::create)
                                .build())
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.translatable("blockaccent.config.onlycrystal"))
                                .description(OptionDescription.of(Component.translatable("blockaccent.config.onlycrystal.desc")))
                                .binding(false, () -> onlyCrystalBase, v -> onlyCrystalBase = v)
                                .controller(BooleanControllerBuilder::create)
                                .build())
                        .build())
                .category(ConfigCategory.createBuilder()
                        .name(Component.translatable("blockaccent.config.group.fill"))
                        .option(Option.<Color>createBuilder()
                                .name(Component.translatable("blockaccent.config.fillcolor"))
                                .description(OptionDescription.of(Component.translatable("blockaccent.config.fillcolor.desc")))
                                .binding(new Color(195, 234, 243, 50), () -> fillColor, v -> fillColor = v)
                                .controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(true))
                                .build())
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.translatable("blockaccent.config.throughwalls.fill"))
                                .description(OptionDescription.of(Component.translatable("blockaccent.config.throughwalls.fill.desc")))
                                .binding(false, () -> throughWallsFill, v -> throughWallsFill = v)
                                .controller(BooleanControllerBuilder::create)
                                .build())
                        .build())
                .category(ConfigCategory.createBuilder()
                        .name(Component.translatable("blockaccent.config.group.outline"))
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.translatable("blockaccent.config.showline"))
                                .description(OptionDescription.of(Component.translatable("blockaccent.config.showline.desc")))
                                .binding(true, () -> showLine, v -> showLine = v)
                                .controller(BooleanControllerBuilder::create)
                                .build())
                        .option(Option.<Color>createBuilder()
                                .name(Component.translatable("blockaccent.config.linecolor"))
                                .description(OptionDescription.of(Component.translatable("blockaccent.config.linecolor.desc")))
                                .binding(new Color(255, 255, 255, 255), () -> lineColor, v -> lineColor = v)
                                .controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(true))
                                .build())
                        .option(Option.<Float>createBuilder()
                                .name(Component.translatable("blockaccent.config.linewidth"))
                                .description(OptionDescription.of(Component.translatable("blockaccent.config.linewidth.desc")))
                                .binding(1.0f, () -> lineWidth, v -> lineWidth = v)
                                .controller(opt -> FloatSliderControllerBuilder.create(opt).range(0.5f, 5.0f).step(0.5f))
                                .build())
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.translatable("blockaccent.config.throughwalls.line"))
                                .description(OptionDescription.of(Component.translatable("blockaccent.config.throughwalls.line.desc")))
                                .binding(false, () -> throughWallsLine, v -> throughWallsLine = v)
                                .controller(BooleanControllerBuilder::create)
                                .build())
                        .build())
                .category(ConfigCategory.createBuilder()
                        .name(Component.translatable("blockaccent.config.group.anim"))
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.translatable("blockaccent.config.anim"))
                                .description(OptionDescription.of(Component.translatable("blockaccent.config.anim.desc")))
                                .binding(false, () -> animEnabled, v -> animEnabled = v)
                                .controller(BooleanControllerBuilder::create)
                                .build())
                        .option(Option.<Float>createBuilder()
                                .name(Component.translatable("blockaccent.config.animspeed"))
                                .description(OptionDescription.of(Component.translatable("blockaccent.config.animspeed.desc")))
                                .binding(8.0f, () -> animSpeed, v -> animSpeed = v)
                                .controller(opt -> FloatSliderControllerBuilder.create(opt).range(1.0f, 20.0f).step(0.5f))
                                .build())
                        .option(Option.<OverlayAnimations.Mode>createBuilder()
                                .name(Component.translatable("blockaccent.config.animmode"))
                                .description(OptionDescription.of(Component.translatable("blockaccent.config.animmode.desc")))
                                .binding(OverlayAnimations.Mode.EASE_OUT_CUBIC, () -> animationMode, v -> animationMode = v)
                                .controller(opt -> EnumControllerBuilder.create(opt).enumClass(OverlayAnimations.Mode.class))
                                .build())
                        .build())
                .build()
                .generateScreen(parent);
    }
}