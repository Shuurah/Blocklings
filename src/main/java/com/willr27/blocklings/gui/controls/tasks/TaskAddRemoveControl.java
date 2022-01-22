package com.willr27.blocklings.gui.controls.tasks;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.entity.entities.blockling.BlocklingTasks;
import com.willr27.blocklings.gui.Control;
import com.willr27.blocklings.gui.GuiTexture;
import com.willr27.blocklings.gui.GuiTextures;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * A control to add or remove a task to tasks list.
 */
@OnlyIn(Dist.CLIENT)
public class TaskAddRemoveControl extends Control
{
    /**
     * The width of the icon.
     */
    public static final int TASK_ADD_REMOVE_WIDTH = 20;

    /**
     * The height of the icon.
     */
    public static final int TASK_ADD_REMOVE_HEIGHT = 20;

    /**
     * The add task icon texture.
     */
    @Nonnull
    private static final GuiTexture ADD_TEXTURE = new GuiTexture(GuiTextures.TASKS, 136, 166, TASK_ADD_REMOVE_WIDTH, TASK_ADD_REMOVE_HEIGHT);

    /**
     * The remove task icon texture.
     */
    @Nonnull
    private static final GuiTexture REMOVE_TEXTURE = new GuiTexture(GuiTextures.TASKS, 156, 166, TASK_ADD_REMOVE_WIDTH, TASK_ADD_REMOVE_HEIGHT);

    /**
     * The parent task control.
     */
    @Nonnull
    public final TaskControl taskControl;

    /**
     * Whether the control is add or remove.
     */
    public final boolean isAddControl;

    /**
     * @param parentTaskControl the parent task control.
     * @param isAddControl whether the control is add or remove.
     */
    public TaskAddRemoveControl(@Nonnull TaskControl parentTaskControl, boolean isAddControl)
    {
        super(parentTaskControl, parentTaskControl.width - 20, 0, TASK_ADD_REMOVE_WIDTH, TASK_ADD_REMOVE_HEIGHT);
        this.taskControl = parentTaskControl;
        this.isAddControl = isAddControl;
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY)
    {
        if (isAddControl)
        {
            renderTexture(matrixStack, ADD_TEXTURE);
        }
        else
        {
            renderTexture(matrixStack, REMOVE_TEXTURE);
        }
    }

    @Override
    public boolean mouseReleased(int mouseX, int mouseY, int button)
    {
        if (isPressed() && isMouseOver(mouseX, mouseY))
        {
            if (isAddControl)
            {
                taskControl.task.blockling.getTasks().createTask(BlocklingTasks.NULL);
            }
            else
            {
                taskControl.task.blockling.getTasks().removeTask(taskControl.task);
            }
        }

        return super.mouseReleased(mouseX, mouseY, button);
    }
}
