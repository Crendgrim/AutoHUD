package mod.crend.autohud.api;

import net.minecraft.client.network.ClientPlayerEntity;

public interface AutoHudApi {
    /**
     * Implement this method to initialize a component
     * @param player currently active player
     */
    default void initState(ClientPlayerEntity player) { }

    /**
     * Implement this method to add special component handling on ticks
     * @param player currently active player
     */
    default void tickState(ClientPlayerEntity player) { }
}
