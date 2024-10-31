package mod.crend.autohud.component.state;

import mod.crend.autohud.component.Component;

import java.util.function.Supplier;

public class BooleanComponentState extends ComponentState {
	Supplier<Boolean> display;

	public BooleanComponentState(Component component, Supplier<Boolean> display, boolean updateEveryTick) {
		super(component, updateEveryTick);
		this.display = display;
	}
	public BooleanComponentState(Component component, Supplier<Boolean> display) {
		this(component, display, true);
	}

	@Override
	public void update() {
		if (!component.config.active() || (component.config.onChange() && display.get())) {
			component.revealCombined();
		} else {
			component.hide();
		}
	}
}
