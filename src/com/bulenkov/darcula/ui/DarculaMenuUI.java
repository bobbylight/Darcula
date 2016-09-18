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
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

public class DarculaMenuUI extends BasicMenuUI {

    private Window parentWindow;
    private Listener listener;

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
}