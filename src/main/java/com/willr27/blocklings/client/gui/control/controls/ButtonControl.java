package com.willr27.blocklings.client.gui.control.controls;

import com.mojang.blaze3d.vertex.PoseStack;
import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.texture.Textures;
import com.willr27.blocklings.client.gui.util.ScissorStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * A vanilla button control.
 */
@OnlyIn(Dist.CLIENT)
public class ButtonControl extends Control
{
    /**
     * The text block control containing the button's text.
     */
    @Nonnull
    public final TextBlockControl textBlock;

    /**
     */
    public ButtonControl()
    {
        super();

        textBlock = new TextBlockControl();
        textBlock.setParent(this);
        textBlock.setFitWidthToContent(true);
        textBlock.setHorizontalAlignment(0.5);
        textBlock.setHorizontalContentAlignment(0.5);
        textBlock.setVerticalAlignment(0.5);
    }

    @Override
    protected void onRender(@Nonnull PoseStack poseStack, @Nonnull ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks)
    {
        super.onRender(poseStack, scissorStack, mouseX, mouseY, partialTicks);

        if (isHovered())
        {
            renderTextureAsBackground(poseStack, Textures.Common.BUTTON_HOVERED.width((int) (getWidth() - 2)));
            renderTextureAsBackground(poseStack, Textures.Common.BUTTON_HOVERED.width(2).dx(Textures.Common.BUTTON_HOVERED.width - 2), getWidth() - 2, 0);
        }
        else
        {
            renderTextureAsBackground(poseStack, Textures.Common.BUTTON.width((int) (getWidth() - 2)));
            renderTextureAsBackground(poseStack, Textures.Common.BUTTON.width(2).dx(Textures.Common.BUTTON.width - 2), getWidth() - 2, 0);
        }
    }
}
