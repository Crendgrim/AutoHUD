//? if simplyskills {
package mod.crend.autohud.compat;

import mod.crend.autohud.api.AutoHudApi;
import mod.crend.autohud.component.Components;
import net.minecraft.client.network.ClientPlayerEntity;
import net.sweenus.simplyskills.SimplySkills;
import net.sweenus.simplyskills.client.SimplySkillsClient;

public class SimplySkillsCompat implements AutoHudApi {
	@Override
	public String modId() {
		return SimplySkills.MOD_ID;
	}

	private void tick(int cooldown, long lastUseTime, long currentTime) {
		if (lastUseTime + cooldown > currentTime) {
			Components.Hotbar.reveal();
		}
	}

	@Override
	public void tickState(ClientPlayerEntity player) {
		long currentTime = System.currentTimeMillis();
		tick(SimplySkillsClient.abilityCooldown, SimplySkillsClient.lastUseTime, currentTime);
		tick(SimplySkillsClient.abilityCooldown2, SimplySkillsClient.lastUseTime2, currentTime);
	}
}
//?}
