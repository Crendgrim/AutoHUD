package mod.crend.autohud.neoforge;

import mod.crend.autohud.AutoHud;
import mod.crend.autohud.ModKeyBindings;
import mod.crend.autohud.api.AutoHudApi;
import mod.crend.autohud.config.ConfigHandler;
import mod.crend.autohud.neoforge.compat.HotbarSlotCyclingCompat;
import mod.crend.autohud.render.ChatMessageIndicator;
import mod.crend.libbamboo.neoforge.ConfigScreen;
import net.minecraft.util.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.ModList;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.InterModProcessEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;

import static net.neoforged.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = AutoHud.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class AutoHudModEvents {
	public static final String REGISTER_API = "register_api";
	public static final Identifier NEW_CHAT_MESSAGE_INDICATOR = new Identifier(AutoHud.MOD_ID, "new_chat_message_indicator");

	@SubscribeEvent
	static void onClientSetup(FMLClientSetupEvent event) {
		AutoHud.init();
		ConfigScreen.register(ConfigHandler.CONFIG_STORE);
		NeoForge.EVENT_BUS.register(new AutoHudGui());
		if (ModList.get().isLoaded("hotbarslotcycling")) {
			HotbarSlotCyclingCompat.init();
		}
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
	static void onRegisterOverlaysEvent(RegisterGuiLayersEvent event) {
		event.registerAboveAll(NEW_CHAT_MESSAGE_INDICATOR, (context, tickDelta) -> ChatMessageIndicator.render(context));
	}

}
