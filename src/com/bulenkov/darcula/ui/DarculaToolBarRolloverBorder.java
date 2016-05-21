package com.bulenkov.darcula.ui;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;

/**
 * Rollover border for toolbar buttons that mimics those in IntelliJ 2016.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class DarculaToolBarRolloverBorder extends AbstractBorder {

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {

        AbstractButton b = (AbstractButton)c;

        if (b.getModel().isRollover() || b.getModel().isArmed()) {
            g.setColor(UIManager.getColor("ToolBar.rolloverBorder.color"));
            g.drawRoundRect(x, y, width - 1, height - 1, 2, 2);
        }
    }

    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        insets.top = insets.bottom = insets.left = insets.right = 3;
        return insets;
    }

    @Override
    public boolean isBorderOpaque() {
        return true;
    }
}