package Al3xPay;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FastClickPearl implements ClientModInitializer {
	public static final String MOD_ID = "fastclickpearl";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(MOD_ID, "main"));

	private static KeyMapping throwKey;
	private static KeyMapping menuKey;
	private static PearlConfig config;

	@Override
	public void onInitializeClient() {
		config = PearlConfig.load(FabricLoader.getInstance().getConfigDir().resolve(MOD_ID + ".json"));

		throwKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
				"key.fastclickpearl.throw",
				InputConstants.Type.KEYSYM,
				GLFW.GLFW_KEY_V,
				CATEGORY));

		menuKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
				"key.fastclickpearl.menu",
				InputConstants.Type.KEYSYM,
				GLFW.GLFW_KEY_UNKNOWN,
				CATEGORY));

		ClientTickEvents.END_CLIENT_TICK.register(this::onEndTick);
		ClientLifecycleEvents.CLIENT_STOPPING.register(client -> config.save());
		ScreenEvents.AFTER_INIT.register(this::onScreenInit);

		LOGGER.info("FastClickPearl loaded");
	}

	private void onEndTick(Minecraft client) {
		while (throwKey.consumeClick()) {
			if (client.player != null && client.level != null && client.gui.screen() == null) {
				PearlThrower.tryThrow(client, config);
			}
		}

		while (menuKey.consumeClick()) {
			if (client.player != null && client.gui.screen() == null) {
				client.setScreenAndShow(new PearlConfigScreen(null));
			}
		}
	}

	private void onScreenInit(Minecraft client, Screen screen, int scaledWidth, int scaledHeight) {
		if (!(screen instanceof TitleScreen) && !(screen instanceof PauseScreen)) {
			return;
		}

		Button button = Button.builder(
				Component.translatable("screen.fastclickpearl.menu"),
				pressed -> client.setScreenAndShow(new PearlConfigScreen(screen)))
				.bounds(6, scaledHeight - 26, 120, 20)
				.build();

		Screens.getWidgets(screen).add(button);
	}

	public static PearlConfig getConfig() {
		return config;
	}

	public static KeyMapping getThrowKey() {
		return throwKey;
	}
}
