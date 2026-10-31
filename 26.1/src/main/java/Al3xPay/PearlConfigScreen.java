package Al3xPay;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
import net.minecraft.network.chat.Component;

public class PearlConfigScreen extends Screen {
	private final Screen parent;
	private final PearlConfig config;

	public PearlConfigScreen(Screen parent) {
		super(Component.translatable("screen.fastclickpearl.title"));
		this.parent = parent;
		this.config = FastClickPearl.getConfig();
	}

	@Override
	protected void init() {
		int centerX = this.width / 2;
		int rowWidth = 200;
		int left = centerX - rowWidth / 2;
		int top = this.height / 2 - 34;

		Button keyButton = Button.builder(
				Component.translatable("screen.fastclickpearl.key", FastClickPearl.getThrowKey().getTranslatedKeyMessage()),
				button -> this.minecraft.setScreenAndShow(new KeyBindsScreen(this, this.minecraft.options)))
				.bounds(left, top, rowWidth, 20)
				.build();

		Button enabledButton = Button.builder(
				Component.translatable("screen.fastclickpearl.enabled", enabledText()),
				button -> {
					config.setEnabled(!config.isEnabled());
					config.save();
					this.rebuildWidgets();
				})
				.bounds(left, top + 24, rowWidth, 20)
				.build();

		Button doneButton = Button.builder(
				Component.translatable("gui.done"),
				button -> this.onClose())
				.bounds(left, top + 60, rowWidth, 20)
				.build();

		this.addRenderableWidget(keyButton);
		this.addRenderableWidget(enabledButton);
		this.addRenderableWidget(doneButton);
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
		super.extractRenderState(graphics, mouseX, mouseY, delta);

		graphics.centeredText(this.font, this.title, this.width / 2, 20, 0xFFFFFF);
		graphics.centeredText(this.font, Component.translatable("screen.fastclickpearl.hint"), this.width / 2, this.height / 2 + 42, 0xA0A0A0);
		graphics.centeredText(this.font, Component.translatable("screen.fastclickpearl.swap_hint"), this.width / 2, this.height / 2 + 56, 0xA0A0A0);
	}

	@Override
	public void onClose() {
		this.minecraft.setScreenAndShow(this.parent);
	}

	private Component enabledText() {
		return Component.translatable(config.isEnabled() ? "screen.fastclickpearl.on" : "screen.fastclickpearl.off");
	}
}
