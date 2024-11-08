package mod.crend.autohud.compat;

//? if dehydration {

import mod.crend.autohud.AutoHud;
import mod.crend.autohud.api.AutoHudApi;
import mod.crend.autohud.component.Component;
import mod.crend.autohud.component.Components;
import mod.crend.autohud.component.state.PolicyComponentState;
import mod.crend.autohud.render.ComponentRenderer;
import net.dehydration.access.ThirstManagerAccess;
import net.dehydration.init.EffectInit;
import net.dehydration.misc.ThirstTooltipData;
import net.dehydration.thirst.ThirstManager;
import net.minecraft.client.network.ClientPlayerEntity;

public class DehydrationCompat implements AutoHudApi {

    @Override
    public String modId() {
        return "dehydration";
    }

    // We bind this to the hunger config, as that is the most closely related one.
    public static Component Thirst = Component.builder("Thirst")
            .config(AutoHud.config.hunger())
            .stackComponents(Components.Air)
            .inMainHud()
            .state(player -> {
                ThirstManager thirstManager = ((ThirstManagerAccess) player).getThirstManager();
                if (thirstManager.hasThirst()) {
                    return new PolicyComponentState(DehydrationCompat.Thirst, thirstManager::getThirstLevel, 20);
                }
                return null;
            })
            .build();
    public static ComponentRenderer THIRST_WRAPPER = ComponentRenderer.builder(Thirst)
            .move()
            .fade()
            .withCustomFramebuffer(false)
            .build();
    static {
        // Fake this API being inserted via entry point
        AutoHud.addApi(new DehydrationCompat());
    }

    @Override
    public void initState(ClientPlayerEntity player) {
        Components.Hunger.addStackComponent(Thirst);
    }

    @Override
    public void tickState(ClientPlayerEntity player) {
        if (player.hasStatusEffect(EffectInit.THIRST)) {
            Thirst.reveal();
        } else if (((ThirstManagerAccess) player).getThirstManager().isNotFull()) {
            if (!player.getMainHandStack().isEmpty() && player.getMainHandStack().getTooltipData().isPresent() && player.getMainHandStack().getTooltipData().get() instanceof ThirstTooltipData) {
                Thirst.reveal();
            } else if (!player.getOffHandStack().isEmpty() && player.getOffHandStack().getTooltipData().isPresent() && player.getOffHandStack().getTooltipData().get() instanceof ThirstTooltipData) {
                Thirst.reveal();
            }
        }
    }
}
//?} else {
/*public class DehydrationCompat { }
*///?}
