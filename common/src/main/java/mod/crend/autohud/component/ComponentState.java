package mod.crend.autohud.component;


public class ComponentState {
    Component component;

    boolean updateEveryTick = false;
    boolean updateNextTick = true;

    public ComponentState(Component component, boolean updateEveryTick) {
        this.component = component;
        this.updateEveryTick = updateEveryTick;
    }
    public ComponentState(Component component) {
        this(component, false);
    }

    public void tick() {
        if (updateNextTick || updateEveryTick) {
            update();
            updateNextTick = false;
        }
    }

    public void update() {
        if (!component.config.active()) {
            component.revealCombined();
        }
    }

    public void updateNextTick() {
        updateNextTick = true;
    }

    public boolean scheduledUpdate() {
        return updateNextTick;
    }
}
