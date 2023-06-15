package mod.crend.autohud.component;

import mod.crend.autohud.config.ConfigHandler;

import java.util.function.Supplier;

public class PolicyComponentState extends ValueComponentState<Integer> {
    Supplier<Integer> maxValueSupplier;

    public PolicyComponentState(Component component, Supplier<Integer> newValueSupplier, Supplier<Integer> maxValueSupplier, boolean updateEveryTick) {
        super(component, newValueSupplier, updateEveryTick);
        this.maxValueSupplier = maxValueSupplier;
    }
    public PolicyComponentState(Component component, Supplier<Integer> newValueSupplier, Supplier<Integer> maxValueSupplier) {
        this(component, newValueSupplier, maxValueSupplier, false);
    }
    public PolicyComponentState(Component component, Supplier<Integer> newValueSupplier, Integer maxValueSupplier, boolean updateEveryTick) {
        this(component, newValueSupplier, () -> maxValueSupplier, updateEveryTick);
    }
    public PolicyComponentState(Component component, Supplier<Integer> newValueSupplier, Integer maxValueSupplier) {
        this(component, newValueSupplier, maxValueSupplier, false);
    }

    @Override
    protected boolean doReveal(Integer newValue) {
        return switch (((ConfigHandler.PolicyComponent) component.config).policy()) {
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
