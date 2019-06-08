/*
 * Copyright 2000-2014 JetBrains s.r.o.
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

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;

/**
 * @author Konstantin Bulenkov
 */
public class DarculaTabbedPaneUI extends BasicTabbedPaneUI {

  @SuppressWarnings({"MethodOverridesStaticMethodOfSuperclass", "UnusedDeclaration"})
  public static ComponentUI createUI(JComponent c) {
    return new DarculaTabbedPaneUI();
  }

  @Override
  protected JButton createScrollButton(int direction) {
    if (direction != SOUTH && direction != NORTH && direction != EAST &&
            direction != WEST) {
      throw new IllegalArgumentException("Direction must be one of: " +
              "SOUTH, NORTH, EAST or WEST");
    }
    return new ScrollableTabButton(direction);
  }

  @Override
  protected void paintTabBorder(Graphics g, int tabPlacement,
                                int tabIndex,
                                int x, int y, int w, int h,
                                boolean isSelected ) {
    g.setColor(lightHighlight);

    switch (tabPlacement) {
      case LEFT:
      case RIGHT:
        super.paintTabBorder(g, tabPlacement, tabIndex, x, y, w, h, isSelected);
        break;
      case BOTTOM:
        if (tabIndex == 0) {
          g.drawLine(x, y, x, y+h-1);
        }
        g.drawLine(x, y+h-1, x+w-1, y+h-1);
        g.drawLine(x+w-1, y+h-1, x+w-1, y);
        break;
      case TOP:
      default:
        if (tabIndex == 0) {
          g.drawLine(x, y, x, y+h-1);
        }
        g.drawLine(x, y, x+w-1, y);
        g.drawLine(x+w-1, y, x+w-1, y+h-1);
        break;
    }
  }

  protected void paintTabBackground(Graphics g, int tabPlacement,
                                    int tabIndex,
                                    int x, int y, int w, int h,
                                    boolean isSelected ) {
    Color selectedColor = UIManager.getColor("TabbedPane.selected");
    g.setColor(!isSelected || selectedColor == null?
            tabPane.getBackgroundAt(tabIndex) : selectedColor);
    switch(tabPlacement) {
      case LEFT:
      case RIGHT:
        super.paintTabBackground(g, tabPlacement, tabIndex, x, y, w, h, isSelected);
        break;
      case BOTTOM:
        g.fillRect(x, y, w-1, h-1);
        break;
      case TOP:
      default:
        g.fillRect(x, y+1, w-1, h-1);
    }
  }

  /**
   * Overridden to make the break in border for selected tab not quite so wide.
   */
  @Override
  protected void paintContentBorderTopEdge(Graphics g, int tabPlacement,
                                           int selectedIndex,
                                           int x, int y, int w, int h) {
    Rectangle selRect = selectedIndex < 0? null :
            getTabBounds(selectedIndex, calcRect);

    g.setColor(lightHighlight);

    // Draw unbroken line if tabs are not on TOP, OR
    // selected tab is not in run adjacent to content, OR
    // selected tab is not visible (SCROLL_TAB_LAYOUT)
    //
    if (tabPlacement != TOP || selectedIndex < 0 ||
            (selRect.y + selRect.height + 1 < y) ||
            (selRect.x < x || selRect.x > x + w)) {
      g.drawLine(x, y, x+w-2, y);
    } else {
      // Break line to show visual connection to selected tab
      g.drawLine(x, y, selRect.x-1, y);
      if (selRect.x + selRect.width < x + w - 1) {
        g.drawLine(selRect.x + selRect.width-1, y, x+w-1, y);
      } else {
        g.setColor(shadow);
        g.drawLine(x+w-2, y, x+w-2, y);
      }
    }
  }

  /**
   * Overridden to make the break in border for selected tab not quite so wide.
   */
  @Override
  protected void paintContentBorderBottomEdge(Graphics g, int tabPlacement,
                                              int selectedIndex,
                                              int x, int y, int w, int h) {
    Rectangle selRect = selectedIndex < 0? null :
            getTabBounds(selectedIndex, calcRect);

    g.setColor(lightHighlight);

    // Draw unbroken line if tabs are not on BOTTOM, OR
    // selected tab is not in run adjacent to content, OR
    // selected tab is not visible (SCROLL_TAB_LAYOUT)
    //
    if (tabPlacement != BOTTOM || selectedIndex < 0 ||
            (selRect.y - 1 > h) ||
            (selRect.x < x || selRect.x > x + w)) {
      g.drawLine(x+1, y+h-1, x+w-2, y+h-1);
    } else {
      // Break line to show visual connection to selected tab
      g.drawLine(x, y+h-1, selRect.x - 1, y+h-1);
      if (selRect.x + selRect.width < x + w - 1) {
        g.drawLine(selRect.x + selRect.width-1, y+h-1, x+w-1, y+h-1);
      }
    }

  }
  protected void paintContentBorderRightEdge(Graphics g, int tabPlacement,
                                             int selectedIndex,
                                             int x, int y, int w, int h) {
    Rectangle selRect = selectedIndex < 0? null :
            getTabBounds(selectedIndex, calcRect);

    g.setColor(lightHighlight);

    // Draw unbroken line if tabs are not on RIGHT, OR
    // selected tab is not in run adjacent to content, OR
    // selected tab is not visible (SCROLL_TAB_LAYOUT)
    //
    if (tabPlacement != RIGHT || selectedIndex < 0 ||
            (selRect.x - 1 > w) ||
            (selRect.y < y || selRect.y > y + h)) {
      g.drawLine(x+w-1, y, x+w-1, y+h-1);
    } else {
      // Break line to show visual connection to selected tab
      g.drawLine(x+w-1, y, x+w-1, selRect.y - 1);

      if (selRect.y + selRect.height < y + h - 2) {
        g.drawLine(x+w-1, selRect.y + selRect.height,
                x+w-1, y+h-2);
      }
    }
  }

  /**
   * Overridden to flip "top" and "bottom" values when tabs are bottom-aligned (BasicLookAndFeel only really provides
   * a means of specifying a single value for these insets (e.g. the size of the visible part of the tabbed pane
   * surrounding tabs), whereas the API allows for different values returned depending on the tab position, which
   * makes sense.
   */
  @Override
  protected Insets getContentBorderInsets(int tabPlacement) {

    Insets insets = contentBorderInsets; // Specified for TOP

    if (tabPlacement == BOTTOM) { // Make copy as "contentBorderInsets" is shared amongst all TabbedPanUIs
      insets = new Insets(insets.bottom, insets.left, insets.top, insets.right);
    }

    return insets;
  }

//  @Override
//  protected int calculateTabAreaWidth(int tabPlacement, int vertRunCount, int maxTabWidth) {
//    return calculateTabAreaWidth(tabPlacement, vertRunCount, maxTabWidth) + 1;
//  }

  private class ScrollableTabButton extends BasicArrowButton implements UIResource,
          SwingConstants {

    public ScrollableTabButton(int direction) {
      super(direction,
              UIManager.getColor("TabbedPane.selected"),
              shadow,
              shadow,
              UIManager.getColor("TabbedPane.highlight"));
    }

    public void paint(Graphics g) {
      Color origColor;
      boolean isPressed, isEnabled;
      int w, h, size;

      w = getSize().width;
      h = getSize().height;
      origColor = g.getColor();
      isPressed = getModel().isPressed();
      isEnabled = isEnabled();

      g.setColor(getBackground());
      g.fillRect(1, 1, w-2, h-2);

      /// Draw the proper Border
      if (getBorder() != null && !(getBorder() instanceof UIResource)) {
        paintBorder(g);
      } else if (isPressed) {
        g.setColor(shadow);
        g.drawRect(0, 0, w-1, h-1);
      } else {
        // Using the background color set above
        g.drawLine(0, 0, 0, h-1);
        g.drawLine(1, 0, w-2, 0);

        g.setColor(lightHighlight);
        g.drawLine(1, 1, 1, h-3);
        g.drawLine(2, 1, w-3, 1);

        g.drawLine(1, h-2, w-2, h-2);
        g.drawLine(w-2, 1, w-2, h-3);

        g.drawLine(0, h-1, w-1, h-1);
        g.drawLine(w-1, h-1, w-1, 0);
      }

      // If there's no room to draw arrow, bail
      if(h < 5 || w < 5)      {
        g.setColor(origColor);
        return;
      }

      if (isPressed) {
        g.translate(1, 1);
      }

      // Draw the arrow
      size = Math.min((h - 4) / 3, (w - 4) / 3);
      size = Math.max(size, 2);
      g.setColor(darkShadow);
      paintTriangle(g, (w - size) / 2, (h - size) / 2,
              size, direction, isEnabled);

      // Reset the Graphics back to it's original settings
      if (isPressed) {
        g.translate(-1, -1);
      }
      g.setColor(origColor);

    }
  }
}
