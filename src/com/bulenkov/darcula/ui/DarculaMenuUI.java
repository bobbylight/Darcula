/*
 * Copyright 2000-2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bulenkov.darcula.ui;

import sun.swing.SwingUtilities2;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuUI;
import java.awt.*;
import java.awt.event.*;

public class DarculaMenuUI extends BasicMenuUI {

    private Window parentWindow;
    private Listener listener;

    /**
     * JMenuItems don't implement rollover OOTB, and indeed, DefaultButtonModel will only allow it to function on
     * enabled components, so because we want rollover on disabled menu items, we must do it ourselves.
     * Note: This could probably be shared with DarculaMenuItemUIBase.
     */
    private RolloverListener rolloverListener = new RolloverListener();

    /**
     * Again, we're implementing rollover ourselves since standard Swing code does not allow it to function on
     * disabled components.
     */
    private boolean rollover;

    private Color selectionInactiveBackground;

    public static ComponentUI createUI(JComponent c) {
        return new DarculaMenuUI();
    }

    public DarculaMenuUI() {
        listener = new Listener();
    }

    @Override
    public void installUI(final JComponent c) {

        super.installUI(c);

        c.addHierarchyListener(new HierarchyListener() {
            @Override
            public void hierarchyChanged(HierarchyEvent e) {

                if (DarculaMenuUI.this.parentWindow != null) {
                    DarculaMenuUI.this.parentWindow.removeWindowFocusListener(listener);
                }

                if (menuItem != null) {
                    Window parentWindow = SwingUtilities.getWindowAncestor(menuItem);
                    if (parentWindow != null) {
                        DarculaMenuUI.this.parentWindow = parentWindow;
                        parentWindow.addWindowFocusListener(listener);
                    }
                }
            }
        });
    }

    protected void installDefaults() {
        super.installDefaults();

        selectionInactiveBackground = UIManager.getColor("MenuItem.selectionInactiveBackground");

        menuItem.setRolloverEnabled(true);
        menuItem.addMouseListener(rolloverListener);
    }

    protected void uninstallDefaults() {
        menuItem.removeMouseListener(rolloverListener);
        super.uninstallDefaults();
    }

    @Override
    protected void paintText(Graphics g, JMenuItem menuItem, Rectangle textRect, String text) {

        // Render menu as inactive if parent window does not have focus
        if (((JMenu)menuItem).isTopLevelMenu()) {
            Window parentWindow = SwingUtilities.getWindowAncestor(menuItem);
            if (parentWindow != null && !parentWindow.isActive()) {
                FontMetrics fm = SwingUtilities2.getFontMetrics(menuItem, g);
                int mnemIndex = menuItem.getDisplayedMnemonicIndex();
                g.setColor(disabledForeground);
                SwingUtilities2.drawStringUnderlineCharAt(menuItem, g, text,
                        mnemIndex, textRect.x, textRect.y + fm.getAscent());
                return;
            }
        }

        super.paintText(g, menuItem, textRect, text);
    }

    @Override
    public void uninstallUI(JComponent c) {

        if (parentWindow != null) {
            parentWindow.removeWindowFocusListener(listener);
            parentWindow = null;
        }

        super.uninstallUI(c);
    }

    protected void paintBackground(Graphics g, JMenuItem menuItem, Color bgColor) {

        ButtonModel model = menuItem.getModel();
        Color oldColor = g.getColor();
        int menuWidth = menuItem.getWidth();
        int menuHeight = menuItem.getHeight();

        if(menuItem.isOpaque()) {
            if (model.isArmed()|| (menuItem instanceof JMenu && model.isSelected())) {
                g.setColor(bgColor);
            }
            else if (rollover) {
                g.setColor(model.isEnabled() ? bgColor : selectionInactiveBackground);
            }
            else {
                g.setColor(menuItem.getBackground());
            }
            g.fillRect(0,0, menuWidth, menuHeight);
            g.setColor(oldColor);
        }
        else if (rollover) {
            g.setColor(model.isEnabled() ? bgColor : selectionInactiveBackground);
            g.fillRect(0,0, menuWidth, menuHeight);
            g.setColor(oldColor);
        }
        else if (model.isArmed()) {
            g.setColor(bgColor);
            g.fillRect(0,0, menuWidth, menuHeight);
            g.setColor(oldColor);
        }
    }

    /**
     * Listens for the parent window losing focus, to trigger a repaint with our "inactive" color.
     */
    private class Listener implements WindowFocusListener {

        @Override
        public void windowGainedFocus(WindowEvent e) {
            menuItem.repaint();
        }

        @Override
        public void windowLostFocus(WindowEvent e) {
            menuItem.repaint();
        }
    }

    private class RolloverListener extends MouseAdapter {

        public void mouseEntered(MouseEvent e) {
            AbstractButton b = (AbstractButton) e.getSource();

            if (b.isRolloverEnabled() && !SwingUtilities.isLeftMouseButton(e)) {
                rollover = true;
                menuItem.repaint();
            }
        }

        public void mouseExited(MouseEvent e) {
            AbstractButton b = (AbstractButton) e.getSource();
            if (b.isRolloverEnabled()) {
                rollover = false;
                menuItem.repaint();
            }
        }

    }
}