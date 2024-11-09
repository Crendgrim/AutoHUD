package mod.crend.autohud.forge;

import mod.crend.autohud.AutoHud;
import mod.crend.autohud.ModKeyBindings;
import mod.crend.autohud.api.AutoHudApi;
import mod.crend.autohud.compat.HotbarSlotCyclingCompat;
import mod.crend.autohud.config.ConfigHandler;
import mod.crend.autohud.render.AutoHudRenderer;
import mod.crend.libbamboo.forge.ConfigScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
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
		ConfigScreen.register(ConfigHandler.CONFIG_STORE);
		MinecraftForge.EVENT_BUS.register(new AutoHudGui());
		if (ModList.get().isLoaded("hotbarslotcycling")) {
			HotbarSlotCyclingCompat.init();
		}
		if (ModList.get().isLoaded("raised")) {
			AutoHudForge.raisedCompat = true;
		}
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

	@SubscribeEvent
	static void onRegisterOverlaysEvent(RegisterGuiOverlaysEvent event) {
		event.registerAboveAll(NEW_CHAT_MESSAGE_INDICATOR, (forgeGui, context, f, i, j) -> AutoHudRenderer.renderChatMessageIndicator(context, f));
	}

}
