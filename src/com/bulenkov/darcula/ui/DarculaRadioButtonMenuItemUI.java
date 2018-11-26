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

import com.bulenkov.darcula.DarculaUIUtil;
import com.bulenkov.iconloader.util.*;
import sun.swing.MenuItemLayoutHelper;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.*;

/**
 * @author Konstantin Bulenkov
 */
public class DarculaRadioButtonMenuItemUI extends DarculaMenuItemUIBase {
  @SuppressWarnings({"MethodOverridesStaticMethodOfSuperclass", "UnusedDeclaration"})
  public static ComponentUI createUI(JComponent c) {
    return new DarculaRadioButtonMenuItemUI();
  }

  protected String getPropertyPrefix() {
      return "RadioButtonMenuItem";
  }

  @Override
  protected void paintCheckIcon(Graphics g2, MenuItemLayoutHelper lh, MenuItemLayoutHelper.LayoutResult lr, Color holdc, Color foreground) {
    Graphics2D g = (Graphics2D) g2;
    AbstractButton b = lh.getMenuItem();
    final GraphicsConfig config = GraphicsUtil.setupAAPainting(g);

    g.translate(lr.getCheckRect().x+1, lr.getCheckRect().y+1);

    int rad = 5;

    final int x = 0;
    final int y = 0;
    final int w = 13;
    final int h = 13;

    g.translate(x, y);

    //setup AA for lines
    final boolean focus = b.hasFocus();
    g.setColor(UIManager.getColor("CheckBox.darcula.checkBoxBackgroundColor"));
    if (focus) {
      g.fillOval(0, 1, w, h);
    } else {
      g.fillOval(0, 1, w - 1, h - 1);
    }

    if (focus) {
      if (UIUtil.isRetina()) {
        DarculaUIUtil.paintFocusOval(g, 1, 2, w - 2, h - 2);
      } else {
        DarculaUIUtil.paintFocusOval(g, 0, 1, w, h);
      }
    } else {
      g.setColor(UIManager.getColor("CheckBox.darcula.checkBoxBorderColor"));
      g.drawOval(0, 1, w - 1, h - 1);
    }

    if (b.isSelected()) {
      final boolean enabled = b.isEnabled();
      g.setColor(UIManager.getColor(enabled ? "RadioButton.darcula.selectionEnabledColor" : "RadioButton.darcula.selectionDisabledColor")); //Gray._170 : Gray._120);
      g.fillOval(w/2 - rad/2, h/2 - 1, rad, rad);
    }
    config.restore();
    g.translate(-x, -y);

    g.translate(-lr.getCheckRect().x-1, -lr.getCheckRect().y-1);
    config.restore();
  }
}
