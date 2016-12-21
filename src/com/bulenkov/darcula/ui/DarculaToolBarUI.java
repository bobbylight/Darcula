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
import javax.swing.plaf.metal.MetalToolBarUI;
import java.awt.*;

/**
 * @author Konstantin Bulenkov
 */
public class DarculaToolBarUI extends MetalToolBarUI {
  @SuppressWarnings({"MethodOverridesStaticMethodOfSuperclass", "UnusedDeclaration"})
  public static ComponentUI createUI(JComponent c) {
    return new DarculaToolBarUI();
  }

// Not sure why main Darcula overrides super.paint(), as this is effectively turning off
// the honoring of setOpaque(false)...
//  @Override
//  public void paint(Graphics g, JComponent c) {
//    g.setColor(UIManager.getColor("ToolBar.background"));
//    g.fillRect(0, 0, c.getWidth(), c.getHeight());
//  }

  @Override
  protected void setBorderToNonRollover(Component c) {
  }
}
