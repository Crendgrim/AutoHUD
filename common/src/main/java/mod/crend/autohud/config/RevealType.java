package mod.crend.autohud.config;

import net.minecraft.text.Text;

public enum RevealType {
    Individual,
    Stacked,
    Grouped,
    HideCombined,
    Combined;

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
