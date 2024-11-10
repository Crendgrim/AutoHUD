package mod.crend.autohud;

import mod.crend.autohud.api.AutoHudApi;
import mod.crend.autohud.component.EventHandler;
import mod.crend.autohud.component.Hud;
import mod.crend.autohud.config.ConfigHandler;
import mod.crend.libbamboo.PlatformUtils;
import mod.crend.libbamboo.event.GameEvent;

import java.util.ArrayList;
import java.util.List;

public class AutoHud {

    public static final String MOD_ID = "autohud";

    public static ConfigHandler config;
    public static final List<AutoHudApi> apis = new ArrayList<>();

    // These are global toggles that are usually always true.
    // Whole sections may be disabled using these flags to enhance mod compatibility.
    public static boolean targetHotbar = true;
    public static boolean targetExperienceBar = true;
    public static boolean targetStatusBars = true;
    public static boolean targetScoreboard = true;
    public static boolean targetStatusEffects = true;
    public static boolean targetCrosshair = true;
    public static boolean targetChat = true;

    public static void init() {
        config = new ConfigHandler();
        if (config.dynamicOnLoad()) {
            Hud.enableDynamic();
        }
        GameEvent.WORLD_LOAD.register(Hud::registerState);
        GameEvent.WORLD_TICK.register(Hud::tickState);
        GameEvent.WORLD_UNLOAD.register(Hud::unregisterState);
        GameEvent.PLAYER_RESPAWN.register(Hud::registerState);
        EventHandler.registerEvents();
    }

    public static void addApi(AutoHudApi api) {
        if (PlatformUtils.isModLoaded(api.modId())) {
            apis.add(api);
            api.init();
        }
    }
}
