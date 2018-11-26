/*
 * Copyright 2000-2018 JetBrains s.r.o.
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

import javax.swing.border.Border;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.UIResource;
import java.awt.*;

public class DarculaScrollPaneBorder implements Border, UIResource {

  @Override
  public Insets getBorderInsets(Component c) {
    return new InsetsUIResource(2, 2, 2, 2);
  }

  @Override
  public boolean isBorderOpaque() {
    return false;
  }

  @Override
  public void paintBorder(Component c, Graphics g2, int x, int y, int width, int height) {

    Graphics2D g = ((Graphics2D)g2);
    final GraphicsConfig config = new GraphicsConfig(g);
    g.translate(x, y);

    if (c.hasFocus()) {
      DarculaUIUtil.paintFocusRing(g, 2, 2, width - 4, height - 4);
    }
    else {
       g.setColor(new Color(0x535353));
       g.drawRect(1, 1, width - 2, height - 2);
    }
    g.translate(-x, -y);
    config.restore();
  }
}
