package mod.crend.autohud.config;

public enum RevealType {
    Individual,
    Stacked,
    Grouped,
    HideCombined,
    Combined;

    public String toString() {
        return "text.autohud.option.revealType." + name();
    }
}
