package com.willr27.blocklings.client.gui.control.controls;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.willr27.blocklings.client.gui.control.BaseControl;
import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.control.event.events.FocusChangedEvent;
import com.willr27.blocklings.client.gui.control.event.events.TryDragEvent;
import com.willr27.blocklings.client.gui.control.event.events.TryHoverEvent;
import com.willr27.blocklings.client.gui.control.event.events.input.*;
import com.willr27.blocklings.client.gui.screen.BlocklingsScreen;
import com.willr27.blocklings.client.gui.util.ScissorStack;
import com.willr27.blocklings.client.gui3.RenderArgs;
import com.willr27.blocklings.client.gui3.util.GuiUtil;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Used as the root control for a {@link BlocklingsScreen} and {@link BlocklingsContainerScreen}.
 */
@OnlyIn(Dist.CLIENT)
public class ScreenControl extends Control
{
    private final List<BaseControl> measureList = new ArrayList<>();

    private final List<BaseControl> arrangeList = new ArrayList<>();

    @Nullable
    private BaseControl hoveredControl;

    @Nullable
    private BaseControl pressedControl;

    /**
     * The pixel x coordinate that the pressed control was pressed at.
     */
    private double pressedStartPixelX = 0.0;

    /**
     * The pixel y coordinate that the pressed control was pressed at.
     */
    private double pressedStartPixelY = 0.0;

    @Nullable
    private BaseControl focusedControl;

    @Nullable
    private BaseControl draggedControl;

    public void measureAndArrange()
    {
        measureList.removeIf(control -> control.getScreen() != this);
        arrangeList.removeIf(control -> control.getScreen() != this);

        while (!measureList.isEmpty() || !arrangeList.isEmpty())
        {
            while (!measureList.isEmpty())
            {
                int minDepth = Integer.MAX_VALUE;
                BaseControl minDepthControl = null;

                for (BaseControl control : measureList)
                {
                    if (control.getTreeDepth() < minDepth)
                    {
                        minDepth = control.getTreeDepth();
                        minDepthControl = control;
                    }
                }

                if (minDepthControl == this)
                {
                    minDepthControl.doMeasure(getWidth(), getHeight());
                }
                else if (minDepthControl != null && minDepthControl.getParent() != null)
                {
                    minDepthControl.getParent().measureChildren();
                }
            }

            while (!arrangeList.isEmpty())
            {
                int minDepth = Integer.MAX_VALUE;
                BaseControl minDepthControl = null;

                for (BaseControl control : arrangeList)
                {
                    if (control.getTreeDepth() < minDepth)
                    {
                        minDepth = control.getTreeDepth();
                        minDepthControl = control;
                    }
                }

                if (minDepthControl != null)
                {
                    minDepthControl.doArrange();
                }
            }
        }
    }

    @Override
    public void measureSelf(double availableWidth, double availableHeight)
    {
        setDesiredWidth(availableWidth);
        setDesiredHeight(availableHeight);
    }

    @Override
    public void arrange()
    {
        setSize(getDesiredSize());

        super.arrange();
    }

    public void addToMeasureQueue(@Nonnull BaseControl control)
    {
        if (!measureList.contains(control))
        {
            measureList.add(control);
        }
    }

    public void removeFromMeasureQueue(@Nullable BaseControl control)
    {
        measureList.remove(control);
    }

    public void addToArrangeQueue(@Nonnull BaseControl control)
    {
        if (!arrangeList.contains(control))
        {
            arrangeList.add(control);
        }
    }

    public void removeFromArrangeQueue(@Nullable BaseControl control)
    {
        arrangeList.remove(control);
    }

    /**
     * Mimics {@link Screen#render(MatrixStack, int, int, float)}.
     */
    public void render(@Nonnull MatrixStack matrixStack, int screenMouseX, int screenMouseY, float partialTicks)
    {
        float guiScale = GuiUtil.getInstance().getGuiScale();
        double mouseX = GuiUtil.getInstance().getPixelMouseX();
        double mouseY = GuiUtil.getInstance().getPixelMouseY();

        TryHoverEvent e = new TryHoverEvent(mouseX, mouseY);

        forwardHover(e);

        if (!e.isHandled())
        {
            setHoveredControl(null);
        }

        forwardTryDrag(new TryDragEvent(mouseX, mouseY));

        if (getDraggedControl() != null)
        {
            getDraggedControl().onDrag(mouseX, mouseY, partialTicks);
        }

        measureAndArrange();

        matrixStack.pushPose();
        matrixStack.scale(1.0f / guiScale, 1.0f / guiScale, 1.0f);

        forwardRender(matrixStack, new ScissorStack(), mouseX, mouseY, partialTicks);

        matrixStack.popPose();

        if (getHoveredControl() != null && getDraggedControl() == null)
        {
            matrixStack.pushPose();
            matrixStack.scale((float) getHoveredControl().getScaleX(), (float) getHoveredControl().getScaleY(), 1.0f);

            RenderSystem.enableDepthTest();
            getHoveredControl().onRenderTooltip(matrixStack, mouseX, mouseY, partialTicks);

            matrixStack.popPose();
        }
    }

    @Override
    protected void onRender(@Nonnull MatrixStack matrixStack, @Nonnull ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks)
    {

    }

    @Override
    public void forwardMouseClicked(@Nonnull MouseClickedEvent e)
    {
        forwardGlobalMouseClicked(e);
        super.forwardMouseClicked(e);
    }

    @Override
    public void forwardMouseReleased(@Nonnull MouseReleasedEvent e)
    {
        forwardGlobalMouseReleased(e);
        super.forwardMouseReleased(e);

        setPressedControl(null);
        setDraggedControl(null);
    }

    @Override
    public void forwardMouseScrolled(@Nonnull MouseScrolledEvent e)
    {
        forwardGlobalMouseScrolled(e);
        super.forwardMouseScrolled(e);
    }

    @Override
    public void forwardGlobalKeyPressed(@Nonnull KeyPressedEvent e)
    {
        super.forwardGlobalKeyPressed(e);

        if (!e.isHandled() && getFocusedControl() != null)
        {
            getFocusedControl().onKeyPressed(e);
        }
    }

    @Override
    public void forwardGlobalKeyReleased(@Nonnull KeyReleasedEvent e)
    {
        super.forwardGlobalKeyReleased(e);

        if (!e.isHandled() && getFocusedControl() != null)
        {
            getFocusedControl().onKeyReleased(e);
        }
    }

    @Override
    public void forwardGlobalCharTyped(@Nonnull CharTypedEvent e)
    {
        super.forwardGlobalCharTyped(e);

        if (!e.isHandled() && getFocusedControl() != null)
        {
            getFocusedControl().onCharTyped(e);
        }
    }

    @Override
    public void setParent(BaseControl parent)
    {
        // The screen control should never have a parent.
    }

    @Nullable
    @Override
    public ScreenControl getScreen()
    {
        return this;
    }

    @Override
    @Nullable
    public BaseControl getHoveredControl()
    {
        return hoveredControl;
    }

    public void setHoveredControl(@Nullable BaseControl control)
    {
        if (hoveredControl != control)
        {
            if (hoveredControl != null)
            {
                hoveredControl.onHoverExit();
            }

            if (control != null)
            {
                control.onHoverEnter();
            }
        }

        hoveredControl = control;
    }

    @Override
    @Nullable
    public BaseControl getPressedControl()
    {
        return pressedControl;
    }

    public void setPressedControl(@Nullable BaseControl control)
    {
        if (pressedControl != control)
        {
            if (pressedControl != null)
            {
                pressedControl.onPressEnd();
            }

            if (control != null)
            {
                control.onPressStart();
            }
        }

        pressedControl = control;
    }

    @Override
    @Nullable
    public BaseControl getFocusedControl()
    {
        return focusedControl;
    }

    public void setFocusedControl(@Nullable BaseControl control)
    {
        boolean previousFocus = focusedControl == control;
        BaseControl previousFocusedControl = focusedControl;

        if (focusedControl != control)
        {
            if (focusedControl != null)
            {
                focusedControl.onUnfocused();
            }

            if (control != null)
            {
                control.onFocused();
            }
        }

        focusedControl = control;

        if (previousFocusedControl != null)
        {
            previousFocusedControl.eventBus.post(previousFocusedControl, new FocusChangedEvent(previousFocus));
        }
        else if (focusedControl != null)
        {
            focusedControl.eventBus.post(focusedControl, new FocusChangedEvent(previousFocus));
        }
    }

    @Override
    @Nullable
    public BaseControl getDraggedControl()
    {
        return draggedControl;
    }

    public void setDraggedControl(@Nullable BaseControl control)
    {
        if (draggedControl != control)
        {
            if (draggedControl != null)
            {
                draggedControl.onDragEnd();
            }

            if (control != null)
            {
                control.onDragStart();
            }
        }

        draggedControl = control;
    }

    public double getPressedStartPixelX()
    {
        return pressedStartPixelX;
    }

    public double getPressedStartPixelY()
    {
        return pressedStartPixelY;
    }
}
