package mod.crend.autohud.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.Window;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL30;

public class CustomFramebufferRenderer {
	static Framebuffer framebuffer = null;
	static int previousFramebuffer;
	public static void init() {
		// Setup extra framebuffer to draw into
		previousFramebuffer = GlStateManager.getBoundFramebuffer();
		if (framebuffer == null) {
			Window window = MinecraftClient.getInstance().getWindow();
			framebuffer = new SimpleFramebuffer(window.getFramebufferWidth(), window.getFramebufferHeight(), true, MinecraftClient.IS_SYSTEM_MAC);
			framebuffer.setClearColor(0, 0, 0, 0);
		}
		framebuffer.clear(MinecraftClient.IS_SYSTEM_MAC);
		framebuffer.beginWrite(false);
	}

	public static void draw(DrawContext context) {
		// Restore the original framebuffer
		GlStateManager._glBindFramebuffer(GL30.GL_FRAMEBUFFER, previousFramebuffer);

		// Render the custom framebuffer's contents with transparency into the main buffer
		Window window = MinecraftClient.getInstance().getWindow();
		int width = window.getScaledWidth();
		int height = window.getScaledHeight();

		RenderSystem.setShaderTexture(0, framebuffer.getColorAttachment());
		RenderSystem.setShader(GameRenderer::getPositionTexProgram);
		Matrix4f matrix4f = context.getMatrices().peek().getPositionMatrix();
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
		bufferBuilder.vertex(matrix4f, 0, 0, 0).texture(0, 1).next();
		bufferBuilder.vertex(matrix4f, 0, height, 0).texture(0, 0).next();
		bufferBuilder.vertex(matrix4f, width, height, 0).texture(1, 0).next();
		bufferBuilder.vertex(matrix4f, width, 0, 0).texture(1, 1).next();
		BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
	}

	public static void resizeFramebuffer() {
		if (framebuffer != null) {
			Window window = MinecraftClient.getInstance().getWindow();
			framebuffer.resize(window.getFramebufferWidth(), window.getFramebufferHeight(), MinecraftClient.IS_SYSTEM_MAC);
		}
	}
}
