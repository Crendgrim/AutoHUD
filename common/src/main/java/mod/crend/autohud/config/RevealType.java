package mod.crend.autohud.config;

import mod.crend.yaclx.type.NameableEnum;
import net.minecraft.text.Text;

public enum RevealType implements NameableEnum {
    Individual,
    Stacked,
    Grouped,
    HideCombined,
    Combined;

    @Override
    public Text getDisplayName() {
        return switch (this) {
            case Individual -> Text.translatable("autohud.revealType.Individual");
            case Stacked -> Text.translatable("autohud.revealType.Stacked");
            case Grouped -> Text.translatable("autohud.revealType.Grouped");
            case HideCombined -> Text.translatable("autohud.revealType.HideCombined");
            case Combined -> Text.translatable("autohud.revealType.Combined");
        };
    }
}
