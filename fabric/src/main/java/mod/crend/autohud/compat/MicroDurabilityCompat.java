package mod.crend.autohud.compat;

import mod.crend.autohud.AutoHud;
import mod.crend.autohud.api.AutoHudApi;
import mod.crend.autohud.component.Component;
import mod.crend.autohud.component.Components;
import mod.crend.autohud.component.state.ComponentState;
import net.minecraft.client.network.ClientPlayerEntity;

public class MicroDurabilityCompat implements AutoHudApi {

    @Override
    public String modId() {
        return "microdurability";
    }

    // We bind this to the hotbar config, as that is the most closely related one.
    public static Component MicroDurabilityComponent = Component.builder("MicroDurability")
            .config(AutoHud.config.hotbar())
            .inMainHud()
            .state(player -> new ComponentState(MicroDurabilityCompat.MicroDurabilityComponent))
            .build();

    static {
        // Fake this API being inserted via entry point
        AutoHud.addApi(new MicroDurabilityCompat());
    }

    @Override
    public void initState(ClientPlayerEntity player) {
        Components.ExperienceBar.addStackComponent(MicroDurabilityComponent);
    }
}
