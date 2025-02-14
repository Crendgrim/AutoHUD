package mod.crend.autohud.neoforge;

import mod.crend.autohud.AutoHud;
import mod.crend.autohud.ModKeyBindings;
import mod.crend.autohud.api.AutoHudApi;
import mod.crend.autohud.config.ConfigHandler;
import mod.crend.autohud.compat.HotbarSlotCyclingCompat;
import mod.crend.autohud.neoforge.compat.ColdSweatCompat;
import mod.crend.autohud.render.AutoHudRenderer;
import mod.crend.libbamboo.VersionUtils;
import mod.crend.libbamboo.neoforge.ConfigScreen;
import net.minecraft.util.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.ModList;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.InterModProcessEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;
//? if <1.20.5 {
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterGuiOverlaysEvent;
//?} else {
/*import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
*///?}

//? if <1.20.5 {
@Mod.EventBusSubscriber(modid = AutoHud.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
//?} else
/*@EventBusSubscriber(modid = AutoHud.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)*/
public class AutoHudModEvents {
	public static final String REGISTER_API = "register_api";
	public static final Identifier NEW_CHAT_MESSAGE_INDICATOR = VersionUtils.getIdentifier(AutoHud.MOD_ID, "new_chat_message_indicator");

	@SubscribeEvent
	static void onClientSetup(FMLClientSetupEvent event) {
		AutoHud.init();
		ConfigScreen.register(ConfigHandler.CONFIG_STORE);
		NeoForge.EVENT_BUS.register(new AutoHudGui());
		if (ModList.get().isLoaded("hotbarslotcycling")) {
			AutoHud.addApi(new HotbarSlotCyclingCompat());
		}
		if (ModList.get().isLoaded("raised")) {
			AutoHudNeoForge.raisedCompat = true;
		}
		if (ModList.get().isLoaded("coldsweat")) {
			AutoHud.addApi(new ColdSweatCompat());
		}
		// Delay initialising the client tick event, see that method.
		NeoForge.EVENT_BUS.addListener(AutoHudNeoForge::onClientTick);
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
	static void onRegisterOverlaysEvent(/*? if <1.20.5 {*/RegisterGuiOverlaysEvent/*?} else {*//*RegisterGuiLayersEvent*//*?}*/ event) {
		event.registerAboveAll(NEW_CHAT_MESSAGE_INDICATOR,
				//? if <1.20.5 {
				(neoforgeGui, context, f, i, j) -> AutoHudRenderer.renderChatMessageIndicator(context, f)
				//?} else
				/*AutoHudRenderer::renderChatMessageIndicator*/
		);
	}

}
