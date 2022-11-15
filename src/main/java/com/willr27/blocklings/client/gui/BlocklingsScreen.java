package com.willr27.blocklings.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui2.GuiUtil;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * A base screen used to support using {@link Control} objects.
 */
@OnlyIn(Dist.CLIENT)
public abstract class BlocklingsScreen extends Screen
{
    /**
     * The root control that contains all the sub controls on the screen.
     */
    @Nonnull
    protected final Control rootControl = new Control();

    /**
     * Default constructor.
     */
    protected BlocklingsScreen()
    {
        super(new StringTextComponent(""));
    }

    @Override
    protected void init()
    {
        super.init();

        rootControl.getChildrenCopy().forEach(control -> rootControl.removeChild(control));
        rootControl.setWidth(width);
        rootControl.setHeight(height);
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int screenMouseX, int screenMouseY, float partialTicks)
    {
        matrixStack.pushPose();
        matrixStack.scale(1.0f / GuiUtil.getGuiScale(), 1.0f / GuiUtil.getGuiScale(), 1.0f);

        rootControl.forwardRender(new RenderArgs(matrixStack, GuiUtil.getPixelMouseX(), GuiUtil.getPixelMouseY(), partialTicks));

        matrixStack.popPose();
    }
}
