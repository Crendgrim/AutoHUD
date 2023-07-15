package mod.crend.autohud.config;

public enum RevealPolicy {
    Always,
    Disabled,
    Changing,
    Increasing,
    Decreasing,
    NotFull,
    Low;

    public String toString() {
        return "text.autohud.option.revealPolicy." + name();
    }
}
