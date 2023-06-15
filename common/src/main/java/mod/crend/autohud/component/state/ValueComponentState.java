package mod.crend.autohud.component.state;

import mod.crend.autohud.component.Component;

import java.util.Objects;
import java.util.function.Supplier;

public class ValueComponentState<T> extends ComponentState {
    T oldValue;
    Supplier<T> newValueSupplier;

    public ValueComponentState(Component component, Supplier<T> newValueSupplier, boolean updateEveryTick) {
        super(component, updateEveryTick);
        this.newValueSupplier = newValueSupplier;
        this.oldValue = newValueSupplier.get();
    }
    public ValueComponentState(Component component, Supplier<T> newValueSupplier) {
        this(component, newValueSupplier, false);
    }

    @Override
    public void update() {
        T newValue = newValueSupplier.get();
        if (doReveal(newValue)) {
            this.onUpdateReveal(newValue);
            component.revealCombined();
        }
        oldValue = newValue;
    }

    protected boolean doReveal(T newValue) {
        return !component.config.active() || (component.config.onChange() && !Objects.equals(newValue, oldValue));
    }
    
    protected void onUpdateReveal(T newValue) {}
}

