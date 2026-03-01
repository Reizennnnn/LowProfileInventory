package org.swap.inventoryswap;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.screen.slot.SlotActionType;
import org.lwjgl.glfw.GLFW;

public class Inventoryswap implements ClientModInitializer {

    private static KeyBinding swapKey;
    private int cooldownTicks = 0;
    private static final int MAX_COOLDOWN = 10; // 0.5 sec op 20 tps

    @Override
    public void onInitializeClient() {
        // Registreer de toets (Standaard op 'R')
        swapKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.swapmod.swap",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                "category.swapmod"
        ));

        // Elke frame/tick checken of de knop is ingedrukt
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (cooldownTicks > 0) {
                cooldownTicks--;
            }

            // Gebruik 'isPressed' of 'wasPressed' afhankelijk van gewenste snelheid
            while (swapKey.wasPressed()) {
                if (cooldownTicks == 0) {
                    executeSwap(client);
                    cooldownTicks = MAX_COOLDOWN;
                }
            }
        });
    }

    private void executeSwap(MinecraftClient client) {
        if (client.player == null || client.interactionManager == null) return;

        // Gebruik de methode met haakjes ()
        int selectedSlot = client.player.getInventory().getSelectedSlot();
        int syncId = client.player.playerScreenHandler.syncId;

        // Slot IDs voor de PlayerScreenHandler (Hotbar = 36-44)
        int hotbarSlotId = selectedSlot + 36;
        int inventorySlotId = selectedSlot + 27;

        client.interactionManager.clickSlot(syncId, hotbarSlotId, 0, SlotActionType.PICKUP, client.player);
        client.interactionManager.clickSlot(syncId, inventorySlotId, 0, SlotActionType.PICKUP, client.player);
        client.interactionManager.clickSlot(syncId, hotbarSlotId, 0, SlotActionType.PICKUP, client.player);
    }
}
