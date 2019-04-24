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

package com.bulenkov.darcula.util;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Utility methods dealing with images. A lot of this code was stolen from Substance.
 */
public class ImageUtil {

    public static BufferedImage getBlankImage(int w, int h) {
        return new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
    }

    public static Icon getCloseIcon(int size) {

        BufferedImage image = getBlankImage(size, size);
        Graphics2D graphics = image.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        int start = size / 4;
        int end = size - start;

        Stroke stroke = new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

        graphics.setStroke(stroke);
        Color color = UIManager.getColor("text");
        graphics.setColor(color);
        graphics.drawLine(start, start, end, end);
        graphics.drawLine(start, end, end, start);
        graphics.dispose();

        return new ImageIcon(image);
    }

    /**
     * Returns <code>maximize</code> icon.
     *
     * @param size Icon dimension.
     * @return <code>Maximize</code> icon.
     */
    public static Icon getMaximizeIcon(int size) {

        BufferedImage image = getBlankImage(size, size);
        Graphics2D graphics = image.createGraphics();
        int start = size / 4 - 1;
        int end = size - start;
        graphics.setColor(UIManager.getColor("text"));
        // top (thicker)
        graphics.fillRect(start, start, end - start, 2);
        // left
        graphics.fillRect(start, start, 1, end - start);
        // right
        graphics.fillRect(end - 1, start, 1, end - start);
        // bottom
        graphics.fillRect(start, end - 1, end - start, 1);
        graphics.dispose();

        return new ImageIcon(image);
    }

    /**
     * Returns <code>minimize</code> icon.
     *
     * @param size Icon dimension.
     * @return <code>Minimize</code> icon.
     */
    public static Icon getMinimizeIcon(int size) {
        BufferedImage image = getBlankImage(size, size);
        Graphics2D graphics = image.createGraphics();
        int start = size / 4 - 2;
        int end = 3 * size / 4;
        size = end - start - 3;
        Color color = UIManager.getColor("text");
        graphics.setColor(color);
        graphics.fillRect(start + 2, end - 1, size, 3);
        graphics.dispose();
        return new ImageIcon(image);
    }

    /**
     * Returns <code>restore</code> icon.
     *
     * @param size Icon dimension.
     * @return <code>Restore</code> icon.
     */
    public static Icon getRestoreIcon(int size) {

        BufferedImage image = getBlankImage(size, size);
        Graphics2D graphics = image.createGraphics();
        int start = size / 4 - 1;
        int end = size - start;
        int smallSquareSize = end - start - 3;
        graphics.setColor(UIManager.getColor("text"));

        // "Main" rectangle
        int mainStartY = end - smallSquareSize;
        // top (thicker)
        graphics.fillRect(start, mainStartY, smallSquareSize, 2);
        // left
        graphics.fillRect(start, mainStartY, 1, smallSquareSize);
        // right
        graphics.fillRect(start + smallSquareSize - 1, mainStartY, 1, smallSquareSize);
        // bottom
        graphics.fillRect(start, mainStartY + smallSquareSize - 1, smallSquareSize, 1);

        // "Secondary rectangle"
        int secondaryStartX = start + 3;
        int secondaryStartY = mainStartY - 3;
        // top (thicker)
        graphics.fillRect(secondaryStartX, secondaryStartY, smallSquareSize, 2);
        // right
        graphics.fillRect(secondaryStartX + smallSquareSize - 1, secondaryStartY, 1,
                smallSquareSize);
        // bottom (partial)
        graphics.fillRect(start + smallSquareSize + 1, secondaryStartY + smallSquareSize - 1,
                2, 1);

        graphics.dispose();

        return new ImageIcon(image);
    }

    /**
     * Scans {@code imageList} for best-looking image of specified dimensions. Image can be scaled
     * and/or padded with transparency.
     */
    public static BufferedImage getScaledIconImage(java.util.List<Image> imageList, int width, int height) {
        if (width == 0 || height == 0) {
            return null;
        }
        Image bestImage = null;
        int bestWidth = 0;
        int bestHeight = 0;
        double bestSimilarity = 3; // Impossibly high value

        for (Image im : imageList) {
            // Iterate imageList looking for best matching image.
            // 'Similarity' measure is defined as good scale factor and small
            // insets.
            // Best possible similarity is 0 (no scale, no insets).
            // It's found while the experiments that good-looking result is
            // achieved with scale factors x1, x3/4, x2/3, xN, x1/N.
            if (im == null) {
                continue;
            }
            int iw;
            int ih;
            try {
                iw = im.getWidth(null);
                ih = im.getHeight(null);
            } catch (Exception e) {
                continue;
            }
            if (iw > 0 && ih > 0) {
                // Calc scale factor
                double scaleFactor = Math.min((double)width / (double)iw,
                        (double)height / (double)ih);
                // Calculate scaled image dimensions
                // adjusting scale factor to nearest "good" value
                int adjw;
                int adjh;
                double scaleMeasure; // 0 - best (no) scale, 1 - impossibly
                // bad
                if (scaleFactor >= 2) {
                    // Need to enlarge image more than twice
                    // Round down scale factor to multiply by integer value
                    scaleFactor = Math.floor(scaleFactor);
                    adjw = iw * (int)scaleFactor;
                    adjh = ih * (int)scaleFactor;
                    scaleMeasure = 1.0 - 0.5 / scaleFactor;
                }
                else if (scaleFactor >= 1) {
                    // Don't scale
                    scaleFactor = 1.0;
                    adjw = iw;
                    adjh = ih;
                    scaleMeasure = 0;
                }
                else if (scaleFactor >= 0.75) {
                    // Multiply by 3/4
                    scaleFactor = 0.75;
                    adjw = iw * 3 / 4;
                    adjh = ih * 3 / 4;
                    scaleMeasure = 0.3;
                }
                else if (scaleFactor >= 0.6666) {
                    // Multiply by 2/3
                    scaleFactor = 0.6666;
                    adjw = iw * 2 / 3;
                    adjh = ih * 2 / 3;
                    scaleMeasure = 0.33;
                }
                else {
                    // Multiply size by 1/scaleDivider
                    // where scaleDivider is minimum possible integer
                    // larger than 1/scaleFactor
                    double scaleDivider = Math.ceil(1.0 / scaleFactor);
                    scaleFactor = 1.0 / scaleDivider;
                    adjw = (int)Math.round(iw / scaleDivider);
                    adjh = (int)Math.round(ih / scaleDivider);
                    scaleMeasure = 1.0 - 1.0 / scaleDivider;
                }
                double similarity = ((double)width - (double)adjw) / width
                        + ((double)height - (double)adjh) / height + // Large
                        // padding
                        // is
                        // bad
                        scaleMeasure; // Large rescale is bad
                if (similarity < bestSimilarity) {
                    bestSimilarity = similarity;
                    bestImage = im;
                    bestWidth = adjw;
                    bestHeight = adjh;
                }
                if (similarity == 0)
                    break;
            }
        }
        if (bestImage == null) {
            // No images were found, possibly all are broken
            return null;
        }
        BufferedImage bimage = getBlankImage(width, height);
        Graphics2D g = bimage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        int x = (width - bestWidth) / 2;
        int y = (height - bestHeight) / 2;
        g.drawImage(bestImage, x, y, bestWidth, bestHeight, null);
        g.dispose();
        return bimage;
    }
}
