package mod.crend.autohud.api;

import net.minecraft.client.network.ClientPlayerEntity;

public interface AutoHudApi {
    /**
     * We want to load the compatibility module only if the targeted mod is present.
     * @return the targeted mod's ID, as defined in its fabric.mod.json
     */
    String modId();

    /**
     * Implement this method for a one-time initialisation on game start.
     */
    default void init() { }

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
