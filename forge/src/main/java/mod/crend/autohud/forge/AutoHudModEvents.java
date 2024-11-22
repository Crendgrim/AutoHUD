package mod.crend.autohud.forge;

import mod.crend.autohud.AutoHud;
import mod.crend.autohud.ModKeyBindings;
import mod.crend.autohud.api.AutoHudApi;
//? if hotbarslotcycling
/*import mod.crend.autohud.compat.HotbarSlotCyclingCompat;*/
//? if coldsweat
import mod.crend.autohud.forge.compat.ColdSweatCompat;
//? if quark
import mod.crend.autohud.forge.compat.QuarkCompat;
//? if legendary_survival_overhaul
import mod.crend.autohud.forge.compat.legendarysurvivaloverhaul.LSOCompat;
import net.minecraftforge.api.distmarker.Dist;
//? if <1.20.5 {
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import mod.crend.autohud.render.AutoHudRenderer;
//?}
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;

@Mod.EventBusSubscriber(modid = AutoHud.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class AutoHudModEvents {
	public static final String REGISTER_API = "register_api";
	public static final String NEW_CHAT_MESSAGE_INDICATOR = "new_chat_message_indicator";

	@SubscribeEvent
	static void onClientSetup(FMLClientSetupEvent event) {
		AutoHud.init();
		//? if <1.20.5
		MinecraftForge.EVENT_BUS.register(new AutoHudGui());
		ModList modList = ModList.get();
		//? if hotbarslotcycling {
		/*if (modList.isLoaded("hotbarslotcycling")) {
			AutoHud.addApi(new HotbarSlotCyclingCompat());
		}
		*///?}
		//? if raised {
		if (modList.isLoaded("raised")) {
			AutoHudForge.raisedCompat = true;
		}
		//?}
		//? if legendary_survival_overhaul {
		if (modList.isLoaded("legendarysurvivaloverhaul")) {
			AutoHud.addApi(new LSOCompat());
		}
		//?}
		//? if quark {
		if (modList.isLoaded("quark")) {
			AutoHud.addApi(new QuarkCompat());
		}
		//?}
		//? if coldsweat {
		if (modList.isLoaded("cold_sweat")) {
			AutoHud.addApi(new ColdSweatCompat());
		}
		//?}
		// Delay initialising the client tick event, see that method.
		MinecraftForge.EVENT_BUS.addListener(AutoHudForge::onClientTick);
	}

	@SubscribeEvent
	static void onInterModProcess(InterModProcessEvent event) {
		InterModComms.getMessages(AutoHud.MOD_ID, REGISTER_API::equals)
				.map(msg -> (AutoHudApi) msg.messageSupplier().get())
				.forEach(AutoHud::addApi);
	}

	@SubscribeEvent
	static void onKeyMappingsRegister(RegisterKeyMappingsEvent event) {
		ModKeyBindings.ALL.forEach(event::register);
	}

	//? if <1.20.5 {
	@SubscribeEvent
	static void onRegisterOverlaysEvent(RegisterGuiOverlaysEvent event) {
		event.registerAboveAll(NEW_CHAT_MESSAGE_INDICATOR, (forgeGui, context, f, i, j) -> AutoHudRenderer.renderChatMessageIndicator(context, f));
	}
	//?}

}
