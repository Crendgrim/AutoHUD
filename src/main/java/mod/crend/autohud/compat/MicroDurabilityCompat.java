package mod.crend.autohud.compat;

import mod.crend.autohud.AutoHud;
import mod.crend.autohud.api.AutoHudApi;
import mod.crend.autohud.component.Component;
import mod.crend.autohud.component.ComponentState;
import net.minecraft.client.network.ClientPlayerEntity;

public class MicroDurabilityCompat implements AutoHudApi {
    // We bind this to the hotbar config, as that is the most closely related one.
    public static Component MicroDurabilityComponent = new Component("MicroDurability", AutoHud.config.hotbar(), true);
    static {
        Component.registerComponent(MicroDurabilityComponent);
        Component.ExperienceBar.addStackComponent(MicroDurabilityComponent);
        // Fake this API being inserted via entry point
        AutoHud.apis.add(new MicroDurabilityCompat());
    }

    @Override
    public void initState(ClientPlayerEntity player) {
        MicroDurabilityComponent.state = new ComponentState(MicroDurabilityComponent);
    }
}
