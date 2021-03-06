/*
 * Copyright 2000-2019 JetBrains s.r.o.
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
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.UIResource;
import java.awt.*;

/**
 * Border used by root panes when Darcula is providing window decorations.
 *
 * @author Konstantin Bulenkov
 */
public class DarculaRootPaneBorder extends AbstractBorder implements UIResource {

    private static final int SIZE = 2;
    private static final Stroke STROKE = new BasicStroke(SIZE);

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {

        g.setColor(UIManager.getColor("Separator.foreground"));

        Graphics2D g2d = (Graphics2D)g;
        Stroke origStroke = g2d.getStroke();
        g2d.setStroke(STROKE);
        g.drawRect(1, 1, width - 2, height - 2);
        g2d.setStroke(origStroke);
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new InsetsUIResource(SIZE, SIZE, SIZE, SIZE);
    }
}
