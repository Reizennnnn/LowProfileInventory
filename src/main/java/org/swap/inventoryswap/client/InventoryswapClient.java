package org.swap.inventoryswap.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class InventoryswapClient implements ClientModInitializer {

    private static KeyBinding keySwapRow1;
    private static KeyBinding keySwapRow2;
    private static KeyBinding keySwapRow3;
    private static KeyBinding keySwapFullRow;

    private int cooldown = 0;

    @Override
    public void onInitializeClient() {
        // De categorie naam in het menu
        String category = "Inventory Swap";

        // 1. Keybinds registreren met mooie namen
        keySwapRow1 = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Swap single on row 1", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R, category));

        keySwapRow2 = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Swap single on row 2", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_V, category));

        keySwapRow3 = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Swap single on row 3", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_X, category));

        keySwapFullRow = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Swap full row", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_B, category));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (cooldown > 0) cooldown--;
            if (client.player == null || cooldown > 0) return;

            if (keySwapRow1.wasPressed()) {
                executeSwap(client, 27); // Bovenste rij
                cooldown = 5;
            } else if (keySwapRow2.wasPressed()) {
                executeSwap(client, 18); // Middelste rij
                cooldown = 5;
            } else if (keySwapRow3.wasPressed()) {
                executeSwap(client, 9);  // Onderste rij (Rij 3)
                cooldown = 5;
            } else if (keySwapFullRow.wasPressed()) {
                executeFullRowSwap(client, 27); // Full swap met rij 1
                cooldown = 10;
            }
        });
    }

    private void executeSwap(MinecraftClient client, int rowOffset) {
        if (client.interactionManager == null || client.currentScreen != null) return;

        // Behoud van de veilige getter methode
        int selectedSlot = client.player.getInventory().getSelectedSlot();
        int syncId = client.player.playerScreenHandler.syncId;

        int hotbarSlotId = selectedSlot + 36;
        int inventorySlotId = selectedSlot + rowOffset;

        swapSlots(client, syncId, hotbarSlotId, inventorySlotId);
    }

    private void executeFullRowSwap(MinecraftClient client, int rowOffset) {
        if (client.interactionManager == null || client.currentScreen != null) return;
        int syncId = client.player.playerScreenHandler.syncId;

        for (int i = 0; i < 9; i++) {
            swapSlots(client, syncId, i + 36, i + rowOffset);
        }
    }

    private void swapSlots(MinecraftClient client, int syncId, int slotA, int slotB) {
        client.interactionManager.clickSlot(syncId, slotA, 0, SlotActionType.PICKUP, client.player);
        client.interactionManager.clickSlot(syncId, slotB, 0, SlotActionType.PICKUP, client.player);
        client.interactionManager.clickSlot(syncId, slotA, 0, SlotActionType.PICKUP, client.player);
    }
}
