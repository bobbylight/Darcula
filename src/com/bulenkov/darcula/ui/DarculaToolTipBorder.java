package com.bulenkov.darcula.ui;

import javax.swing.plaf.BorderUIResource;
import java.awt.*;

/**
 * The border to use for tool tips.
 *
 * @author Robert Futrell
 */
public class DarculaToolTipBorder extends BorderUIResource.LineBorderUIResource {

    public DarculaToolTipBorder() {
        super(new Color(0x777777));//UIManager.getColor("ToolTip.borderColor"));
    }
}
