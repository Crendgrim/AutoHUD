package mod.crend.autohud.component;

import mod.crend.autohud.config.Config;

import java.util.function.Supplier;

public class PolicyComponentState extends ValueComponentState<Integer> {
    Supplier<Integer> maxValueSupplier;

    public PolicyComponentState(Component component, Supplier<Integer> newValueSupplier, Supplier<Integer> maxValueSupplier) {
        super(component, newValueSupplier);
        this.maxValueSupplier = maxValueSupplier;
    }
    public PolicyComponentState(Component component, Supplier<Integer> newValueSupplier, Integer maxValueSupplier) {
        this(component, newValueSupplier, () -> maxValueSupplier);
    }

    @Override
    protected boolean doReveal(Integer newValue) {
        return switch (((Config.PolicyComponent) component.config).policy()) {
            case NotFull -> (newValue < maxValueSupplier.get());
            case Low -> (newValue <= maxValueSupplier.get() / 3);
            case Increasing -> (newValue > oldValue);
            case Decreasing -> (newValue < oldValue);
            case Changing -> (!newValue.equals(oldValue));
            case Disabled -> !Hud.actDynamic();
            case Always -> true;
        };
    }
}
