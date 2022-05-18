package mod.crend.autohud.component;

import java.util.function.Supplier;

public class ValueComponentState<T> extends ComponentState {
    T oldValue;
    Supplier<T> newValueSupplier;

    public ValueComponentState(Component component, Supplier<T> newValueSupplier) {
        super(component);
        this.newValueSupplier = newValueSupplier;
        this.oldValue = newValueSupplier.get();
    }

    @Override
    public void tick() {
        T newValue = newValueSupplier.get();
        if (doReveal(newValue)) {
            component.revealCombined();
        }
        oldValue = newValue;
    }

    protected boolean doReveal(T newValue) {
        return !component.config.active() || newValue != oldValue;
    }
}

