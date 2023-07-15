package mod.crend.autohud.config;

public enum ScrollDirection {
    Up,
    Down,
    Left,
    Right;

    public String toString() {
        return "text.autohud.option.scrollDirection." + name();
    }
}
