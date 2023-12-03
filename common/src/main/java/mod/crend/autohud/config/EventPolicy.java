package mod.crend.autohud.config;

import mod.crend.yaclx.type.NameableEnum;
import net.minecraft.text.Text;

public enum EventPolicy implements NameableEnum {
	Reveal,
	Hide,
	Nothing;

	@Override
	public Text getDisplayName() {
		return switch (this) {
			case Reveal -> Text.translatable("autohud.eventPolicy.Reveal");
			case Hide -> Text.translatable("autohud.eventPolicy.Hide");
			case Nothing -> Text.translatable("autohud.eventPolicy.Nothing");
		};
	}
}
