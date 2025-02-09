package mod.crend.autohud.compat;

//? if dehydration {

import mod.crend.autohud.AutoHud;
import mod.crend.autohud.api.AutoHudApi;
import mod.crend.autohud.component.Component;
import mod.crend.autohud.component.Components;
import mod.crend.autohud.component.state.EnhancedPolicyComponentState;
import mod.crend.autohud.render.ComponentRenderer;
import net.dehydration.access.ThirstManagerAccess;
import net.dehydration.init.EffectInit;
import net.dehydration.misc.ThirstTooltipData;
import net.dehydration.thirst.ThirstManager;
import net.minecraft.item.ItemStack;

public class DehydrationCompat implements AutoHudApi {
    public static final String DEHYDRATION_MOD_ID = "dehydration";

    @Override
    public String modId() {
        return DEHYDRATION_MOD_ID;
    }

    // We bind this to the hunger config, as that is the most closely related one.
    public static Component Thirst = Component.builder(DEHYDRATION_MOD_ID, "thirst")
            .config(AutoHud.config.hunger())
            .stackComponents(Components.Air)
            .inMainHud()
            .state(player -> {
                ThirstManager thirstManager = ((ThirstManagerAccess) player).getThirstManager();
                if (thirstManager.hasThirst()) {
                    return new EnhancedPolicyComponentState(DehydrationCompat.Thirst,
                            thirstManager::getThirstLevel,
                            20,
                            () -> player.hasStatusEffect(EffectInit.THIRST)
                                    || (
                                            AutoHud.config.revealHungerWhenHoldingFoodItem()
                                            && thirstManager.isNotFull()
                                            && (itemStillsThirst(player.getMainHandStack()) || itemStillsThirst(player.getOffHandStack()))
                                    )
                    );
                }
                return null;
            })
            .build();
    public static ComponentRenderer THIRST_WRAPPER = ComponentRenderer.of(Thirst);
    static {
        // Fake this API being inserted via entry point
        AutoHud.addApi(new DehydrationCompat());
    }

    @Override
    public void init() {
        Components.Hunger.addStackComponent(Thirst);
    }

    private static boolean itemStillsThirst(ItemStack itemStack) {
        return !itemStack.isEmpty() && itemStack.getTooltipData().isPresent() && itemStack.getTooltipData().get() instanceof ThirstTooltipData;
    }
}
//?} else {
/*public class DehydrationCompat { }
*///?}
