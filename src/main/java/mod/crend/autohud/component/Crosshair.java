package mod.crend.autohud.component;

import mod.crend.autohud.AutoHud;
import mod.crend.autohud.config.BlockCrosshairPolicy;
import mod.crend.autohud.config.CrosshairPolicy;
import mod.crend.autohud.config.InteractableCrosshairPolicy;
import mod.crend.autohud.mixin.IBlockItemMixin;
import mod.crend.autohud.mixin.IBucketItemMixin;
import mod.crend.autohud.mixin.IItemMixin;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.*;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.RaycastContext;

public class Crosshair {

    private static boolean policyMatches(CrosshairPolicy policy, HitResult hitResult) {
        return (policy == CrosshairPolicy.Always || (policy == CrosshairPolicy.IfTargeting && hitResult.getType() != HitResult.Type.MISS));
    }
    private static boolean policyMatches(BlockCrosshairPolicy policy, HitResult hitResult) {
        return (policy == BlockCrosshairPolicy.Always || (policy != BlockCrosshairPolicy.Disabled && hitResult.getType() == HitResult.Type.BLOCK));
    }

    private static boolean checkHand(ClientPlayerEntity player, ItemStack handItemStack, HitResult hitResult, boolean offhand) {
        if (handItemStack.isEmpty()) return false;
        Item handItem = handItemStack.getItem();

        if (policyMatches(AutoHud.config.dynamicCrosshairHoldingTool(), hitResult)) {
            // Tools & Melee Weapons
            if (handItem instanceof ToolItem && !offhand) return true;
            if (handItem instanceof FlintAndSteelItem) return true;
            if (handItem instanceof ShearsItem) return true;
            if (handItem instanceof FishingRodItem) return true;
        }
        if (policyMatches(AutoHud.config.dynamicCrosshairHoldingRangedWeapon(), hitResult)) {
            if (handItem instanceof RangedWeaponItem) return true;
            if (handItem instanceof TridentItem) return true;
        }
        if (policyMatches(AutoHud.config.dynamicCrosshairHoldingThrowable(), hitResult)) {
            if (handItem instanceof EggItem) return true;
            if (handItem instanceof SnowballItem) return true;
            if (handItem instanceof ThrowablePotionItem) return true;
            if (handItem instanceof ExperienceBottleItem) return true;
            if (handItem instanceof EnderPearlItem) return true;
        }
        if (policyMatches(AutoHud.config.dynamicCrosshairHoldingBlock(), hitResult)) {
            if (handItem instanceof BlockItem) {
                if (AutoHud.config.dynamicCrosshairHoldingBlock() == BlockCrosshairPolicy.IfInteractable) {
                    IBlockItemMixin blockItem = (IBlockItemMixin) handItem;
                    ItemPlacementContext itemPlacementContext = new ItemPlacementContext(player, player.getActiveHand(), handItemStack, (BlockHitResult) hitResult);
                    BlockState blockState = blockItem.invokeGetPlacementState(itemPlacementContext);
                    if (blockState != null && blockItem.invokeCanPlace(itemPlacementContext, blockState)) return true;
                } else return true;
            }
            if (handItem instanceof ArmorStandItem) return true;
            if (handItem instanceof MinecartItem) return true;
            if (handItem instanceof BoatItem) return true;
        }

        if (AutoHud.config.dynamicCrosshairHoldingUsableItem() == BlockCrosshairPolicy.IfInteractable) {
            // Enable crosshair on food and drinks also when not targetting if "when interactable" is chosen
            if (handItem.isFood() && player.getHungerManager().isNotFull()) return true;
            if (handItem.getUseAction(handItemStack) == UseAction.DRINK) return true;

            if (handItem instanceof SpawnEggItem) return true;
            if (handItem instanceof FireChargeItem) return true;

            if (hitResult.getType() == HitResult.Type.BLOCK) {
                Block block = MinecraftClient.getInstance().world.getBlockState(((BlockHitResult) hitResult).getBlockPos()).getBlock();
                if (handItem instanceof MusicDiscItem && block.equals(Blocks.JUKEBOX)) return true;
                if (handItem instanceof HoneycombItem && HoneycombItem.UNWAXED_TO_WAXED_BLOCKS.get().get(block) != null)
                    return true;
                if (handItem instanceof EnderEyeItem && block.equals(Blocks.END_PORTAL_FRAME)) return true;
                if (handItem instanceof GlassBottleItem) {
                    if (block.equals(Blocks.WATER_CAULDRON)) return true;
                    if (block.equals(Blocks.BEE_NEST)) return true;
                    if (block.equals(Blocks.BEEHIVE)) return true;
                }
                if (handItem instanceof PotionItem && PotionUtil.getPotion(handItemStack) == Potions.WATER) {
                    if (block.equals(Blocks.CAULDRON)) return true;
                }
                if (handItem instanceof BucketItem) {
                    boolean isFilled = ((IBucketItemMixin) handItem).getFluid() != Fluids.EMPTY;
                    if (isFilled) return true;
                    if (block.equals(Blocks.WATER_CAULDRON)) return true;
                    if (block.equals(Blocks.LAVA_CAULDRON)) return true;
                    if (block.equals(Blocks.POWDER_SNOW_CAULDRON)) return true;
                }
            }

            // Liquid interactions, ignores block targeting state and casts extra rays
            if (handItem instanceof GlassBottleItem) {
                BlockHitResult blockHitResult = IItemMixin.invokeRaycast(MinecraftClient.getInstance().world, MinecraftClient.getInstance().player, RaycastContext.FluidHandling.ANY);
                if (MinecraftClient.getInstance().world.getFluidState(blockHitResult.getBlockPos()).isIn(FluidTags.WATER))
                    return true;
            }
            if (handItem instanceof BucketItem) {
                BlockHitResult blockHitResult = IItemMixin.invokeRaycast(MinecraftClient.getInstance().world, MinecraftClient.getInstance().player, RaycastContext.FluidHandling.SOURCE_ONLY);
                if (!MinecraftClient.getInstance().world.getFluidState(blockHitResult.getBlockPos()).isEmpty())
                    return true;
            }
        } else if (AutoHud.config.dynamicCrosshairHoldingUsableItem() == BlockCrosshairPolicy.Always
                || (AutoHud.config.dynamicCrosshairHoldingUsableItem() == BlockCrosshairPolicy.IfTargeting && hitResult.getType() == HitResult.Type.BLOCK)) {
            if (handItem.isFood()) return true;
            if (handItem.getUseAction(handItemStack) == UseAction.DRINK) return true;

            if (handItem instanceof SpawnEggItem) return true;
            if (handItem instanceof FireChargeItem) return true;
            if (handItem instanceof MusicDiscItem) return true;
            if (handItem instanceof HoneycombItem) return true;
            if (handItem instanceof EnderEyeItem) return true;
            if (handItem instanceof GlassBottleItem) return true;
            if (handItem instanceof PotionItem) return true;
            if (handItem instanceof BucketItem) return true;
        }

        if (AutoHud.config.dynamicCrosshairOnBlock() == InteractableCrosshairPolicy.IfInteractable && hitResult.getType() == HitResult.Type.BLOCK) {
            BlockState blockState = MinecraftClient.getInstance().world.getBlockState(((BlockHitResult) hitResult).getBlockPos());
            Block block = blockState.getBlock();
            if (block instanceof BlockWithEntity) {
                if (block instanceof AbstractSignBlock) {
                    if (handItem instanceof DyeItem || handItem.equals(Items.GLOW_INK_SAC)) return true;
                }
                if (block instanceof LecternBlock) {
                    if (handItem.equals(Items.WRITTEN_BOOK) || handItem.equals(Items.WRITABLE_BOOK) || blockState.get(LecternBlock.HAS_BOOK))
                        return true;
                }
                if (block instanceof CampfireBlock) {
                    BlockEntity blockEntity = MinecraftClient.getInstance().world.getBlockEntity(((BlockHitResult) hitResult).getBlockPos());
                    if (blockEntity instanceof CampfireBlockEntity && (((CampfireBlockEntity) blockEntity).getRecipeFor(handItemStack)).isPresent())
                        return true;
                }
            }
        }

        return false;
    }

    public static boolean shouldShowCrosshair() {
        if (!AutoHud.config.dynamicCrosshair()) return true;
        if (!AutoHud.config.dynamicCrosshairEnableWithHud() && !Hud.isDynamic()) return true;

        HitResult hitResult = MinecraftClient.getInstance().crosshairTarget;
        if (AutoHud.config.dynamicCrosshairOnBlock() == InteractableCrosshairPolicy.IfTargeting && hitResult.getType() == HitResult.Type.BLOCK) return true;
        if (AutoHud.config.dynamicCrosshairOnEntity() && hitResult.getType() == HitResult.Type.ENTITY) return true;

        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        assert player != null;
        ItemStack mainHandStack = player.getMainHandStack();

        if (AutoHud.config.dynamicCrosshairOnBlock() == InteractableCrosshairPolicy.IfInteractable && hitResult.getType() == HitResult.Type.BLOCK) {
            BlockState blockState = MinecraftClient.getInstance().world.getBlockState(((BlockHitResult) hitResult).getBlockPos());
            Block block = blockState.getBlock();
            if (block instanceof BlockWithEntity) {
                if (!(block instanceof BeehiveBlock || block instanceof AbstractSignBlock || block instanceof LecternBlock || block instanceof CampfireBlock)) return true;
            }
            if (block instanceof StonecutterBlock) return true;
            if (block instanceof SmithingTableBlock) return true;
            if (block instanceof GrindstoneBlock) return true;
            if (block instanceof CartographyTableBlock) return true;
            if (block instanceof LoomBlock) return true;
            if (block instanceof BedBlock) return true;
            if (block instanceof CakeBlock) return true;
            if (block instanceof TrapdoorBlock) return true;
            if (block instanceof DoorBlock) return true;
            if (block instanceof FenceGateBlock) return true;
            if (block instanceof AbstractButtonBlock) return true;
            if (block instanceof NoteBlock) return true;
            if (block instanceof LeverBlock) return true;
            if (block instanceof AbstractRedstoneGateBlock) return true;
            if (block instanceof AnvilBlock) return true;
            if (block instanceof CraftingTableBlock) return true;
            if (block instanceof RedstoneOreBlock) return true;
            if (block instanceof AbstractCandleBlock && blockState.get(AbstractCandleBlock.LIT) && mainHandStack.isEmpty()) return true;
            if (block instanceof ComposterBlock && ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.containsKey(mainHandStack.getItem())) return true;
        }

        if (checkHand(player, mainHandStack, hitResult, false)) return true;
        if ((mainHandStack.getUseAction() == UseAction.NONE
                // skip offhand if we have food in our main hand that can override it
                || (mainHandStack.getUseAction() == UseAction.EAT && !player.getHungerManager().isNotFull())
        )
                // skip offhand if we have blocks in our main hand and are targeting a block
                && !(mainHandStack.getItem() instanceof BlockItem && hitResult.getType() == HitResult.Type.BLOCK)
        ) {
            return checkHand(player, player.getOffHandStack(), hitResult, true);
        }
        return false;
    }
}
