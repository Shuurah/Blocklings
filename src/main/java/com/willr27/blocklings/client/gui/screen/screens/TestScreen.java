package com.willr27.blocklings.client.gui.screen.screens;

import com.willr27.blocklings.client.gui.control.BaseControl;
import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.control.controls.panels.FlowPanel;
import com.willr27.blocklings.client.gui.control.event.events.input.MouseClickedEvent;
import com.willr27.blocklings.client.gui.control.event.events.input.MouseReleasedEvent;
import com.willr27.blocklings.client.gui.properties.Flow;
import com.willr27.blocklings.client.gui.screen.BlocklingsScreen;
import com.willr27.blocklings.entity.blockling.BlocklingEntity;

import javax.annotation.Nonnull;

public class TestScreen extends BlocklingsScreen
{
    /**
     * @param blockling the blockling associated with the screen.
     */
    public TestScreen(@Nonnull BlocklingEntity blockling)
    {
        super(blockling);

//        Control control = new Control();
//        control.setParent(screenControl);
//        control.setHorizontalAlignment(0.5);
//        control.setVerticalAlignment(0.5);
//        control.setWidth(100.0);
//        control.setHeight(50.0);
//        control.setCanScrollHorizontally(true);
//        control.setCanScrollVertically(true);
////        control.setMaxWidth(100.0);
////        control.setShouldFitWidthToContent(true);
////        control.setShouldFitHeightToContent(true);
////        control.setWidthPercentage(0.5);
////        control.setHeightPercentage(0.75);
//        control.setPadding(5, 10, 15, 20);
//        control.setBackgroundColour(control.randomColour());
//
//        Control control2 = new Control()
//        {
//            protected void mouseReleased(double mouseX, double mouseY, int button)
//            {
//                setWidth(163);
//                setHeight(20);
//                control.setInnerScale(0.5, 0.5);
////                setWidthPercentage(1.0);
////                setHeightPercentage(1.0);
//                control.setPadding(5, 10, 15, 20);
//                setHorizontalAlignment(0.5);
//                setVerticalAlignment(0.5);
////                setMargin(8);
//            }
//        };
//        control2.setParent(control);
//        control2.setBackgroundColour(control2.randomColour());
//
//        Control control3 = new Control();
//        control3.setParent(control);
//        control3.setWidth(20.0);
//        control3.setHeight(50.0);
//        control3.setHorizontalAlignment(0.5);
//        control3.setBackgroundColour(control3.randomColour());

        FlowPanel flowPanel = new FlowPanel();
        flowPanel.setParent(screenControl);
        flowPanel.setWidth(70.0);
        flowPanel.setHeight(70.0);
//        stackPanel.setMaxWidth(100.0);
//        stackPanel.setMaxHeight(100.0);
//        flowPanel.setInnerScale(0.5, 0.5);
//        stackPanel.setMinScroll(0.0, 0.0);
//        stackPanel.setMaxScroll(100.0, 100.0);
        flowPanel.setCanScrollHorizontally(true);
        flowPanel.setCanScrollVertically(true);
        flowPanel.setPadding(10, 10, 10, 10);
//        stackPanel.setShouldFitWidthToContent(true);
//        stackPanel.setShouldFitHeightToContent(true);
        flowPanel.setFlow(Flow.BOTTOM_RIGHT_BOTTOM_TO_TOP);
        flowPanel.setHorizontalSpacing(4.0);
        flowPanel.setVerticalSpacing(4.0);
        flowPanel.setHorizontalAlignment(0.5);
        flowPanel.setVerticalAlignment(0.5);
        flowPanel.setHorizontalContentAlignment(0.5);
        flowPanel.setVerticalContentAlignment(0.5);
        flowPanel.setLineAlignment(0.5);
        flowPanel.setBackgroundColour(flowPanel.randomColour());

        for (int i = 0; i < 15; i++)
        {
            Control stackPanelControl = new Control();
            stackPanelControl.setParent(flowPanel);
            stackPanelControl.setWidth(stackPanelControl.randomInt(10, 20));
            stackPanelControl.setHeight(stackPanelControl.randomInt(10, 50));
//            stackPanelControl.setMargin(stackPanelControl.randomInt(2, 4));
            stackPanelControl.setMargin(1);
            stackPanelControl.setBackgroundColour(0xffffff00 | (i*30 << 0));
            stackPanelControl.eventBus.subscribe((BaseControl control, MouseClickedEvent e) -> control.setBackgroundColour(0xff00ff00));
            stackPanelControl.eventBus.subscribe((BaseControl control, MouseReleasedEvent e) -> System.out.println("Released!"));
        }
    }
}
