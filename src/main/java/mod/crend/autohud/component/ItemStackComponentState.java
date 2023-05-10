package mod.crend.autohud.component;

import net.minecraft.item.ItemStack;

import java.util.function.Supplier;

public class ItemStackComponentState extends ValueComponentState<ItemStack> {
	public ItemStackComponentState(Component component, Supplier<ItemStack> newValueSupplier, boolean updateEveryTick) {
		super(component, () -> newValueSupplier.get().copy(), updateEveryTick);
	}
	public ItemStackComponentState(Component component, Supplier<ItemStack> newValueSupplier) {
		super(component, newValueSupplier, false);
	}

	@Override
	protected boolean doReveal(ItemStack newValue) {
		return !component.config.active() || (component.config.onChange() && !ItemStack.areEqual(oldValue, newValue));
	}
}
