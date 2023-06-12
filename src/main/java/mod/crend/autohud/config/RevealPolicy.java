package mod.crend.autohud.config;

import mod.crend.yaclx.type.NameableEnum;
import net.minecraft.text.Text;

public enum RevealPolicy implements NameableEnum {
    Always,
    Disabled,
    Changing,
    Increasing,
    Decreasing,
    NotFull,
    Low;

    @Override
    public Text getDisplayName() {
        return switch (this) {
            case Always -> Text.translatable("autohud.revealPolicy.Always");
            case Disabled -> Text.translatable("autohud.revealPolicy.Disabled");
            case Changing -> Text.translatable("autohud.revealPolicy.Changing");
            case Increasing -> Text.translatable("autohud.revealPolicy.Increasing");
            case Decreasing -> Text.translatable("autohud.revealPolicy.Decreasing");
            case NotFull -> Text.translatable("autohud.revealPolicy.NotFull");
            case Low -> Text.translatable("autohud.revealPolicy.Low");
        };
    }
}
