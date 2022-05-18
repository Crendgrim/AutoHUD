package mod.crend.autohud.component;


public class ComponentState {
    Component component;

    public ComponentState(Component component) {
        this.component = component;
    }

    public void tick() {
        if (!component.config.active()) {
            component.revealCombined();
        }
    }

}
