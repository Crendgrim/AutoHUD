//? if >=1.21.6 {
/*package mod.crend.autohud.mixin;

import com.mojang.datafixers.util.Either;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.client.world.ClientWaypointHandler;
import net.minecraft.world.waypoint.TrackedWaypoint;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import java.util.UUID;

@Mixin(ClientWaypointHandler.class)
@MixinEnvironment(type = MixinEnvironment.Env.CLIENT)
public interface ClientWaypointHandlerAccessor {
	@Accessor
	Map<Either<UUID, String>, TrackedWaypoint> getWaypoints();
}
*///?}
