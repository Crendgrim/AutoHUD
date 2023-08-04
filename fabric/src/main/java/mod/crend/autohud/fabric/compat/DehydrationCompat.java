package mod.crend.autohud.fabric.compat;

import mod.crend.autohud.AutoHud;
import mod.crend.autohud.api.AutoHudApi;
import mod.crend.autohud.component.Component;
import mod.crend.autohud.component.PolicyComponentState;
import net.dehydration.access.ThirstManagerAccess;
import net.dehydration.thirst.ThirstManager;
import net.minecraft.client.network.ClientPlayerEntity;

import java.util.List;

public class DehydrationCompat implements AutoHudApi {

    @Override
    public String modId() {
        return "dehydration";
    }

    // We bind this to the hunger config, as that is the most closely related one.
    public static Component Thirst = new Component("Thirst", AutoHud.config.hunger(), List.of(Component.Air), true);
    static {
        // Fake this API being inserted via entry point
        AutoHud.addApi(new DehydrationCompat());
    }

    @Override
    public void initState(ClientPlayerEntity player) {
        Component.registerComponent(Thirst);
        Component.Hunger.addStackComponent(Thirst);

        ThirstManager thirstManager = ((ThirstManagerAccess) player).getThirstManager(player);
        if (thirstManager.hasThirst()) {
            Thirst.state = new PolicyComponentState(Thirst, thirstManager::getThirstLevel, 20);
        }
    }
}