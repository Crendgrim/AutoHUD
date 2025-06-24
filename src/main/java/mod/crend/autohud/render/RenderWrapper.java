//? if >=1.21.6 {
/*package mod.crend.autohud.render;

import com.mojang.blaze3d.systems.RenderSystem;
import mod.crend.autohud.mixin.DrawContextAccessor;
import mod.crend.libbamboo.mixin.MinecraftClientAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.SpecialGuiElementRenderer;
import net.minecraft.client.gui.render.state.special.SpecialGuiElementRenderState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

public class RenderWrapper extends SpecialGuiElementRenderer<RenderWrapper.RenderWrapperState> {
	public static Framebuffer framebuffer = null;
	public static Framebuffer builtinFramebuffer;
	public RenderWrapper(VertexConsumerProvider.Immediate vertexConsumers) {
		super(vertexConsumers);
	}

	public static void init() {
		if (framebuffer == null) {
			Window window = MinecraftClient.getInstance().getWindow();
			framebuffer = new SimpleFramebuffer("autohud-fb", window.getFramebufferWidth(), window.getFramebufferHeight(), true);
			builtinFramebuffer = MinecraftClient.getInstance().getFramebuffer();
		}

		framebuffer.delete();
		((MinecraftClientAccessor) MinecraftClient.getInstance()).setFramebuffer(framebuffer);
	}

	public static void create(DrawContext context) {
		((DrawContextAccessor) context).getState().addSpecialElement(new RenderWrapperState(0, context.getScaledWindowWidth(), 0, context.getScaledWindowHeight()));
		init();
	}

	public static void cleanup() {
		//((MinecraftClientAccessor) MinecraftClient.getInstance()).setFramebuffer(builtinFramebuffer);
	}

	@Override
	public Class<RenderWrapperState> getElementClass() {
		return RenderWrapperState.class;
	}

	@Override
	protected void render(RenderWrapperState state, MatrixStack matrixStack) {
		((MinecraftClientAccessor)MinecraftClient.getInstance()).setFramebuffer(builtinFramebuffer);
		var texture = RenderSystem.getShaderTexture(0);
		RenderSystem.setShaderTexture(0, RenderSystem.getDevice().createTextureView(framebuffer.getColorAttachment()));
		VertexConsumer vertexConsumer = this.vertexConsumers.getBuffer(RenderLayer.getLineStrip());
		Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
		int color = AutoHudRenderer.getArgb();
		vertexConsumer.vertex(matrix4f, state.x1(), state.y1(), 0.0F).texture(0F, 1F).color(color);
		vertexConsumer.vertex(matrix4f, state.x2(), state.y1(), 0.0F).texture(0F, 0F).color(color);
		vertexConsumer.vertex(matrix4f, state.x2(), state.y2(), 0.0F).texture(1F, 0F).color(color);
		vertexConsumer.vertex(matrix4f, state.x1(), state.y2(), 0.0F).texture(1F, 1F).color(color);
		RenderSystem.setShaderTexture(0, texture);
	}

	@Override
	protected String getName() {
		return "autohud wrapper";
	}

	public record RenderWrapperState(int x1, int x2, int y1, int y2) implements SpecialGuiElementRenderState {
		@Override
		public float scale() {
			return 1.0f;
		}

		@Nullable
		@Override
		public ScreenRect scissorArea() {
			return null;
		}

		@Nullable
		@Override
		public ScreenRect bounds() {
			return null;
		}
	}
}
*///?}
