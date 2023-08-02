package mod.crend.autohud.fabric.compat.mixin.overflowingbars;

//@Mixin(value = ArmorBarRenderer.class, remap = false)
public class ArmorBarRendererMixin {
   /* Not available for 1.18.2
   @Inject(method = "renderToughnessBar", at = @At("HEAD"))
    private static void autoHud$preRenderArmorBar(MatrixStack matrixStack, int posX, int posY, PlayerEntity player, Profiler profiler, boolean left, boolean unmodified, CallbackInfo ci) {
        Hud.postInject(matrixStack);
        Hud.preInject(matrixStack, Component.Armor);
    }*/
}