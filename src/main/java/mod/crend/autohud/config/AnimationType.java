package mod.crend.autohud.config;

public enum AnimationType {
    Move,
    Fade,
    None;

    @Override
    public String toString() {
        return "text.autohud.option.animationType." + name();
    }
}
