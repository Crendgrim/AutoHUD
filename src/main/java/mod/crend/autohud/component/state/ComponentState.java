package mod.crend.autohud.component.state;


import mod.crend.autohud.component.Component;
import mod.crend.autohud.component.Hud;

public class ComponentState {
    protected Component component;

    protected boolean updateEveryTick = false;
    protected boolean updateNextTick = true;

    public ComponentState(Component component, boolean updateEveryTick) {
        this.component = component;
        this.updateEveryTick = updateEveryTick;
    }
    public ComponentState(Component component) {
        this(component, false);
    }

    public void tick() {
        if (updateNextTick || (updateEveryTick && Hud.actDynamic())) {
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
