package mod.crend.autohud.config;

public enum RevealType {
    Individual,
    Stacked,
    Grouped,
    HideCombined,
    Combined;

    @Override
    public String toString() {
        return "text.autohud.option.revealType." + name();
    }
}
