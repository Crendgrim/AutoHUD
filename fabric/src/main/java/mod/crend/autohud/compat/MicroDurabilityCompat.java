package mod.crend.autohud.compat;

import mod.crend.autohud.AutoHud;
import mod.crend.autohud.api.AutoHudApi;
import mod.crend.autohud.component.Component;
import mod.crend.autohud.component.Components;
import mod.crend.autohud.component.state.ComponentState;
import net.minecraft.client.network.ClientPlayerEntity;

public class MicroDurabilityCompat implements AutoHudApi {
    public static final String MICRODURABILITY_MOD_ID = "microdurability";

    @Override
    public String modId() {
        return MICRODURABILITY_MOD_ID;
    }

    // We bind this to the hotbar config, as that is the most closely related one.
    public static Component MicroDurabilityComponent = Component.builder(MICRODURABILITY_MOD_ID, "microdurability")
            .config(AutoHud.config.hotbar())
            .inMainHud()
            .state(player -> new ComponentState(MicroDurabilityCompat.MicroDurabilityComponent))
            .build();

    static {
        // Fake this API being inserted via entry point
        AutoHud.addApi(new MicroDurabilityCompat());
    }

    @Override
    public void init() {
        Components.ExperienceBar.addStackComponent(MicroDurabilityComponent);
    }
}
