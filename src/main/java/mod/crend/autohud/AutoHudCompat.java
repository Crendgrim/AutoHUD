package mod.crend.autohud;

import mod.crend.autohud.api.AutoHudApi;
import mod.crend.libbamboo.PlatformUtils;

import java.util.function.Supplier;

//? if appleskin && neoforge
/*import mod.crend.autohud.compat.AppleSkinCompat;*/
import mod.crend.autohud.compat.BetterMountHudCompat;
//? if coldsweat
/*import mod.crend.autohud.compat.ColdSweatCompat;*/
//? if dehydration
import mod.crend.autohud.compat.DehydrationCompat;
//? if drg_flares
import mod.crend.autohud.compat.DRGFlaresCompat;
//? if environmentz
import mod.crend.autohud.compat.EnvironmentZCompat;
//? if hotbarslotcycling
/*import mod.crend.autohud.compat.slotcycler.HotbarSlotCyclingCompat;*/
//? if legendary_survival_overhaul
/*import mod.crend.autohud.compat.legendarysurvivaloverhaul.LSOCompat;*/
//? if feathers
/*import mod.crend.autohud.compat.FeathersCompat;*/
import mod.crend.autohud.compat.MicroDurabilityCompat;
//? if onebar
import mod.crend.autohud.compat.OneBarCompat;
//? if overflowing_bars
import mod.crend.autohud.compat.OverflowingBarsCompat;
//? if quark
/*import mod.crend.autohud.compat.QuarkCompat;*/
//? if raised
import mod.crend.autohud.compat.RaisedCompat;
//? if simplyskills
import mod.crend.autohud.compat.SimplySkillsCompat;
//? if stamina
/*import mod.crend.autohud.compat.StaminaCompat;*/
//? if thirstwastaken
/*import mod.crend.autohud.compat.ThirstWasTakenCompat;*/

public class AutoHudCompat {
	public static void registerCompatibilityProviders() {
		//? if appleskin && neoforge
		/*register("appleskin", AppleSkinCompat::new);*/
		register("bettermounthud", BetterMountHudCompat::new);
		//? if coldsweat
		/*register("cold_sweat", ColdSweatCompat::new);*/
		//? if dehydration
		register("dehydration", DehydrationCompat::new);
		//? if drg_flares
		register("drg_flares", DRGFlaresCompat::new);
		//? if environmentz
		register("environmentz", EnvironmentZCompat::new);
		//? if feathers
		/*register("feathers", FeathersCompat::new);*/
		//? if hotbarslotcycling
		/*register("hotbarslotcycling", HotbarSlotCyclingCompat::new);*/
		//? if legendary_survival_overhaul
		/*register("legendarysurvivaloverhaul", LSOCompat::new);*/
		register("microdurability", MicroDurabilityCompat::new);
		//? if onebar
		register("onebar", OneBarCompat::new);
		//? if overflowing_bars
		register("overflowingbars", OverflowingBarsCompat::new);
		//? if quark
		/*register("quark", QuarkCompat::new);*/
		//? if raised
		register("raised", RaisedCompat::new);
		//? if simplyskills
		register("simplyskills", SimplySkillsCompat::new);
		//? if stamina
		/*register("stamina", StaminaCompat::new);*/
		//? if thirstwastaken
		/*register("thirst", ThirstWasTakenCompat::new);*/
	}

	private static void register(String modId, Supplier<AutoHudApi> apiSupplier) {
		if (PlatformUtils.isModPresent(modId)) {
			AutoHud.addApi(apiSupplier.get());
		}
	}
}
