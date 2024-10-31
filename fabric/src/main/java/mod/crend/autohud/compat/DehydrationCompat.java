package mod.crend.autohud.compat;

//? if 1.20.6 {
/*public class DehydrationCompat { }
*///?} else {

import mod.crend.autohud.AutoHud;
import mod.crend.autohud.api.AutoHudApi;
import mod.crend.autohud.component.Component;
import mod.crend.autohud.component.state.PolicyComponentState;
import net.dehydration.access.ThirstManagerAccess;
import net.dehydration.effect.ThirstEffect;
import net.dehydration.init.EffectInit;
import net.dehydration.misc.ThirstTooltipData;
import net.dehydration.thirst.ThirstManager;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.entry.RegistryEntry;

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

        ThirstManager thirstManager = ((ThirstManagerAccess) player).getThirstManager();
        if (thirstManager.hasThirst()) {
            Thirst.state = new PolicyComponentState(Thirst, thirstManager::getThirstLevel, 20);
        }
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
//?}