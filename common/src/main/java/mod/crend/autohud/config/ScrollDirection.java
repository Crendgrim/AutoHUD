package mod.crend.autohud.config;

import net.minecraft.text.Text;

public enum ScrollDirection {
    Up,
    Down,
    Left,
    Right;

    public Text getDisplayName() {
        return switch (this) {
            case Up -> Text.translatable("autohud.scrollDirection.Up");
            case Down -> Text.translatable("autohud.scrollDirection.Down");
            case Left -> Text.translatable("autohud.scrollDirection.Left");
            case Right -> Text.translatable("autohud.scrollDirection.Right");
        };
    }
}
