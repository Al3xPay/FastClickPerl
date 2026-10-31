package Al3xPay;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class PearlThrower {
	private static final int HOTBAR_SIZE = 9;
	private static final int INVENTORY_SIZE = 36;

	private PearlThrower() {
	}

	public static boolean tryThrow(Minecraft client, PearlConfig config) {
		if (!config.isEnabled()) {
			return false;
		}

		LocalPlayer player = client.player;

		if (player == null || client.level == null || client.gameMode == null) {
			return false;
		}

		if (client.screen != null || player.isSpectator()) {
			return false;
		}

		Inventory inventory = player.getInventory();
		int slot = findPearl(inventory);

		if (slot == Inventory.SLOT_OFFHAND) {
			ItemStack offhand = player.getOffhandItem();

			if (!offhand.is(Items.ENDER_PEARL) || player.getCooldowns().isOnCooldown(offhand)) {
				return false;
			}

			return client.gameMode.useItem(player, InteractionHand.OFF_HAND).consumesAction();
		}

		if (slot < 0) {
			return false;
		}

		ItemStack stack = inventory.getItem(slot);

		if (!stack.is(Items.ENDER_PEARL) || player.getCooldowns().isOnCooldown(stack)) {
			return false;
		}

		int selected = inventory.getSelectedSlot();

		if (slot == selected) {
			return client.gameMode.useItem(player, InteractionHand.MAIN_HAND).consumesAction();
		}

		if (Inventory.isHotbarSlot(slot)) {
			inventory.setSelectedSlot(slot);
			boolean used = client.gameMode.useItem(player, InteractionHand.MAIN_HAND).consumesAction();
			inventory.setSelectedSlot(selected);
			return used;
		}

		AbstractContainerMenu menu = player.containerMenu;

		if (!(menu instanceof InventoryMenu)) {
			return false;
		}

		client.gameMode.handleInventoryMouseClick(menu.containerId, slot, selected, ClickType.SWAP, player);
		boolean used = client.gameMode.useItem(player, InteractionHand.MAIN_HAND).consumesAction();
		client.gameMode.handleInventoryMouseClick(menu.containerId, slot, selected, ClickType.SWAP, player);
		return used;
	}

	private static int findPearl(Inventory inventory) {
		for (int slot = 0; slot < HOTBAR_SIZE; slot++) {
			if (inventory.getItem(slot).is(Items.ENDER_PEARL)) {
				return slot;
			}
		}

		for (int slot = HOTBAR_SIZE; slot < INVENTORY_SIZE; slot++) {
			if (inventory.getItem(slot).is(Items.ENDER_PEARL)) {
				return slot;
			}
		}

		if (inventory.player.getOffhandItem().is(Items.ENDER_PEARL)) {
			return Inventory.SLOT_OFFHAND;
		}

		return -1;
	}
}
