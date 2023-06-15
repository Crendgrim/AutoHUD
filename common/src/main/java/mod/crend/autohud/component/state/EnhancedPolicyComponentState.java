package mod.crend.autohud.component.state;

import mod.crend.autohud.component.Component;

import java.util.function.Supplier;

public class EnhancedPolicyComponentState extends PolicyComponentState {
	Supplier<Boolean> test;

	public EnhancedPolicyComponentState(Component component, Supplier<Integer> newValueSupplier, Supplier<Integer> maxValueSupplier, Supplier<Boolean> test, boolean updateEveryTick) {
		super(component, newValueSupplier, maxValueSupplier, updateEveryTick);
		this.test = test;
	}

	public EnhancedPolicyComponentState(Component component, Supplier<Integer> newValueSupplier, Supplier<Integer> maxValueSupplier, Supplier<Boolean> test) {
		super(component, newValueSupplier, maxValueSupplier);
		this.test = test;
	}

	public EnhancedPolicyComponentState(Component component, Supplier<Integer> newValueSupplier, Integer maxValueSupplier, Supplier<Boolean> test, boolean updateEveryTick) {
		super(component, newValueSupplier, maxValueSupplier, updateEveryTick);
		this.test = test;
	}

	public EnhancedPolicyComponentState(Component component, Supplier<Integer> newValueSupplier, Integer maxValueSupplier, Supplier<Boolean> test) {
		super(component, newValueSupplier, maxValueSupplier);
		this.test = test;
	}

	@Override
	protected boolean doReveal(Integer newValue) {
		return test.get() || super.doReveal(newValue);
	}
}
