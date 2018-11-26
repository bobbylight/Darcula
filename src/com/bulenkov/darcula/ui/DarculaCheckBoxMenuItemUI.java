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
import com.bulenkov.iconloader.util.GraphicsConfig;
import com.bulenkov.iconloader.util.GraphicsUtil;
import com.bulenkov.iconloader.util.Gray;
import com.bulenkov.iconloader.util.UIUtil;
import sun.swing.MenuItemLayoutHelper;
import sun.swing.SwingUtilities2;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.View;
import java.awt.*;

import static com.bulenkov.darcula.ui.DarculaCheckBoxUI.getColor;

/**
 * @author Konstantin Bulenkov
 */
public class DarculaCheckBoxMenuItemUI extends DarculaMenuItemUIBase {

  @SuppressWarnings({"MethodOverridesStaticMethodOfSuperclass", "UnusedDeclaration"})
  public static ComponentUI createUI(JComponent c) {
    return new DarculaCheckBoxMenuItemUI();
  }

  protected String getPropertyPrefix() {
      return "CheckBoxMenuItem";
  }

  @Override
  protected void paintCheckIcon(Graphics g, MenuItemLayoutHelper lh, MenuItemLayoutHelper.LayoutResult lr, Color holdc, Color foreground) {

    final int x = 0;
    final int y = 0;
    final int w = 13;
    final int h = 13;
    AbstractButton b = lh.getMenuItem();
    AbstractButton c = b;
    Graphics2D g2 = (Graphics2D)g;

    g.translate(lr.getCheckRect().x+1, lr.getCheckRect().y+1);

    g2.translate(x, y);
    final Paint paint = new GradientPaint(w / 2, 0, b.getBackground().brighter(),
            w / 2, h, b.getBackground());
    g2.setPaint(paint);
    g2.fillRect(1, 1, w - 2, h - 2);

    //setup AA for lines
    final GraphicsConfig config = GraphicsUtil.setupAAPainting(g);

    g2.setColor(getColor("checkBoxBackgroundColor", null));

    if (c.hasFocus()) {
      g2.fillRoundRect(0, 0, w - 2, h - 2, 4, 4);
      DarculaUIUtil.paintFocusRing(g, 1, 1, w - 2, h - 2);
    } else {
      g2.fillRoundRect(0, 0, w, h - 1 , 4, 4);

      g2.setColor(UIManager.getColor("CheckBox.darcula.checkBoxBorderColor"));
      g2.drawRoundRect(0, 0, w, h - 1, 4, 4);
    }

    if (b.getModel().isSelected()) {
      g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
      g2.setStroke(new BasicStroke(1 *2.0f, BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
      g2.setPaint(getCheckSignColor(b.isEnabled()));
      g2.drawLine(4, 7, 6, 10);
      g2.drawLine(6, 10, w - 3, 3);
    }

    g2.translate(-x, -y);
    g.translate(-lr.getCheckRect().x-1, -lr.getCheckRect().y-1);
    config.restore();
  }

  protected Color getCheckSignColor(boolean enabled) {
    return enabled ? getColor("checkSignColor", Gray._170)
            : getColor("checkSignColorDisabled", Gray._120);
  }
}
