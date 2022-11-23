package com.willr27.blocklings.client.gui.control;

import com.willr27.blocklings.client.gui.IScreen;
import com.willr27.blocklings.client.gui.control.event.events.DragEndEvent;
import com.willr27.blocklings.client.gui.control.event.events.DragStartEvent;
import com.willr27.blocklings.client.gui.control.event.events.input.MouseButtonEvent;
import com.willr27.blocklings.client.gui.control.event.events.input.MousePosEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * The root control for each screen.
 */
@OnlyIn(Dist.CLIENT)
public class ScreenControl extends Control implements IScreen
{
    /**
     * The currently hovered control.
     */
    @Nullable
    private Control hoveredControl;

    /**
     * The currently pressed control.
     */
    @Nullable
    private Control pressedControl;

    /**
     * The pixel x coordinate that the pressed control was pressed at.
     */
    private int pressedStartPixelX = 0;

    /**
     * The pixel y coordinate that the pressed control was pressed at.
     */
    private int pressedStartPixelY = 0;

    /**
     * The currently focused control.
     */
    @Nullable
    private Control focusedControl;

    /**
     * The currently dragged control.
     */
    @Nullable
    private Control draggedControl;

    /**
     */
    public ScreenControl()
    {
        this.screen = this;
    }

    @Override
    public void forwardMouseReleased(@Nonnull MouseButtonEvent mouseButtonEvent)
    {
        super.forwardMouseReleased(mouseButtonEvent);

        setPressedControl(null, mouseButtonEvent);
        setDraggedControl(null, mouseButtonEvent);
    }

    @Override
    @Nullable
    public Control getHoveredControl()
    {
        return hoveredControl;
    }

    @Override
    public void setHoveredControl(@Nullable Control control, @Nonnull MousePosEvent mousePosEvent)
    {
        if (hoveredControl != control)
        {
            if (hoveredControl != null)
            {
                mousePosEvent.mouseX = Math.round(hoveredControl.toLocalX(mousePosEvent.mousePixelX));
                mousePosEvent.mouseY = Math.round(hoveredControl.toLocalY(mousePosEvent.mousePixelY));

                hoveredControl.onHoverExit(mousePosEvent);
            }

            if (control != null)
            {
                mousePosEvent.mouseX = Math.round(control.toLocalX(mousePosEvent.mousePixelX));
                mousePosEvent.mouseY = Math.round(control.toLocalY(mousePosEvent.mousePixelY));

                control.onHoverEnter(mousePosEvent);
            }
        }

        hoveredControl = control;
    }

    @Nullable
    @Override
    public Control getPressedControl()
    {
        return pressedControl;
    }

    @Override
    public void setPressedControl(@Nullable Control control, @Nonnull MouseButtonEvent mouseButtonEvent)
    {
        if (pressedControl != control)
        {
            if (pressedControl != null)
            {
                mouseButtonEvent.mouseX = Math.round(pressedControl.toLocalX(mouseButtonEvent.mousePixelX));
                mouseButtonEvent.mouseY = Math.round(pressedControl.toLocalY(mouseButtonEvent.mousePixelY));

                pressedControl.onReleased(mouseButtonEvent);
            }

            if (control != null)
            {
                pressedStartPixelX = mouseButtonEvent.mousePixelX;
                pressedStartPixelY = mouseButtonEvent.mousePixelY;

                mouseButtonEvent.mouseX = Math.round(control.toLocalX(mouseButtonEvent.mousePixelX));
                mouseButtonEvent.mouseY = Math.round(control.toLocalY(mouseButtonEvent.mousePixelY));

                control.onPressed(mouseButtonEvent);
            }
        }

        pressedControl = control;
    }

    @Override
    public int getPressedStartPixelX()
    {
        return pressedStartPixelX;
    }

    @Override
    public int getPressedStartPixelY()
    {
        return pressedStartPixelY;
    }

    @Nullable
    @Override
    public Control getFocusedControl()
    {
        return focusedControl;
    }

    @Override
    public void setFocusedControl(@Nullable Control control, @Nonnull MouseButtonEvent mouseButtonEvent)
    {
        if (focusedControl != control)
        {
            if (focusedControl != null)
            {
                mouseButtonEvent.mouseX = Math.round(focusedControl.toLocalX(mouseButtonEvent.mousePixelX));
                mouseButtonEvent.mouseY = Math.round(focusedControl.toLocalY(mouseButtonEvent.mousePixelY));

                focusedControl.onUnfocused(mouseButtonEvent);
            }

            if (control != null)
            {
                mouseButtonEvent.mouseX = Math.round(control.toLocalX(mouseButtonEvent.mousePixelX));
                mouseButtonEvent.mouseY = Math.round(control.toLocalY(mouseButtonEvent.mousePixelY));

                control.onFocused(mouseButtonEvent);
            }
        }

        focusedControl = control;
    }

    @Nullable
    @Override
    public Control getDraggedControl()
    {
        return draggedControl;
    }

    @Override
    public void setDraggedControl(@Nullable Control control, @Nonnull MousePosEvent mousePosEvent)
    {
        if (draggedControl != control)
        {
            if (draggedControl != null)
            {
                mousePosEvent.mouseX = Math.round(draggedControl.toLocalX(mousePosEvent.mousePixelX));
                mousePosEvent.mouseY = Math.round(draggedControl.toLocalY(mousePosEvent.mousePixelY));

                draggedControl.onDragEnd.handle(new DragEndEvent(draggedControl));
                draggedControl.onDragEnd(mousePosEvent);
            }

            if (control != null)
            {
                mousePosEvent.mouseX = Math.round(control.toLocalX(mousePosEvent.mousePixelX));
                mousePosEvent.mouseY = Math.round(control.toLocalY(mousePosEvent.mousePixelY));

                control.onDragStart.handle(new DragStartEvent(control));
                control.onDragStart(mousePosEvent);
            }
        }

        draggedControl = control;
    }
}
