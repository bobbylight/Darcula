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

import com.bulenkov.darcula.util.ImageUtil;
import com.bulenkov.iconloader.util.ColorUtil;
import sun.swing.SwingUtilities2;

import javax.accessibility.AccessibleContext;
import javax.swing.*;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

/**
 * The title bar used when system decorations are enabled.
 *
 * @author Konstantin Bulenkov
 */
public class DarculaTitlePane extends JComponent {
  private static final int IMAGE_HEIGHT = 16;
  private static final int IMAGE_WIDTH = 16;

  private PropertyChangeListener propertyChangeListener;
  private JMenuBar menuBar;
  private Action closeAction;
  private Action iconifyAction;
  private Action restoreAction;
  private Action maximizeAction;
  private JButton toggleButton;
  private JButton iconifyButton;
  private JButton closeButton;
  private Icon maximizeIcon;
  private Icon minimizeIcon;
  private Image appIcon;
  private WindowListener windowListener;
  private Window window;
  private JRootPane rootPane;
  private int state;
  private DarculaRootPaneUI rootPaneUI;

  private Color activeBackground = null;
  private Color activeForeground = null;

  public DarculaTitlePane(JRootPane root, DarculaRootPaneUI ui) {
    this.rootPane = root;
    rootPaneUI = ui;

    state = -1;

    installSubcomponents();
    determineColors();
    installDefaults();

    setLayout(createLayout());
  }

  private void uninstall() {
    uninstallListeners();
    window = null;
    removeAll();
  }

  private void installListeners() {
    if (window != null) {
      windowListener = createWindowListener();
      window.addWindowListener(windowListener);
      propertyChangeListener = createWindowPropertyChangeListener();
      window.addPropertyChangeListener(propertyChangeListener);
    }
  }

  private void uninstallListeners() {
    if (window != null) {
      window.removeWindowListener(windowListener);
      window.removePropertyChangeListener(propertyChangeListener);
    }
  }

  private WindowListener createWindowListener() {
    return new WindowHandler();
  }

  private PropertyChangeListener createWindowPropertyChangeListener() {
    return new PropertyChangeHandler();
  }

  public JRootPane getRootPane() {
    return rootPane;
  }

  private int getWindowDecorationStyle() {
    return getRootPane().getWindowDecorationStyle();
  }

  public void addNotify() {
    super.addNotify();

    uninstallListeners();

    window = SwingUtilities.getWindowAncestor(this);
    if (window != null) {
      if (window instanceof Frame) {
        setState(((Frame)window).getExtendedState());
      }
      else {
        setState(0);
      }
      setActive(window.isActive());
      installListeners();
      updateSystemIcon();
    }
  }

  public void removeNotify() {
    super.removeNotify();

    uninstallListeners();
    window = null;
  }

  private void installSubcomponents() {
    int decorationStyle = getWindowDecorationStyle();
    if (decorationStyle == JRootPane.FRAME) {
      createActions();
      menuBar = createMenuBar();
      add(menuBar);
      createButtons();
      add(iconifyButton);
      add(toggleButton);
      add(closeButton);
    }
    else if (decorationStyle == JRootPane.PLAIN_DIALOG ||
             decorationStyle == JRootPane.INFORMATION_DIALOG ||
             decorationStyle == JRootPane.ERROR_DIALOG ||
             decorationStyle == JRootPane.COLOR_CHOOSER_DIALOG ||
             decorationStyle == JRootPane.FILE_CHOOSER_DIALOG ||
             decorationStyle == JRootPane.QUESTION_DIALOG ||
             decorationStyle == JRootPane.WARNING_DIALOG) {
      createActions();
      createButtons();
      add(closeButton);
    }
  }

  private void determineColors() {
    switch (getWindowDecorationStyle()) {
      case JRootPane.FRAME:
      case JRootPane.PLAIN_DIALOG:
      case JRootPane.INFORMATION_DIALOG:
      default:
        activeBackground = UIManager.getColor("darcula.background");
        activeForeground = UIManager.getColor("OptionPane.foreground");
        break;
      case JRootPane.ERROR_DIALOG:
        activeBackground = UIManager.getColor("OptionPane.errorDialog.titlePane.background");
        activeForeground = UIManager.getColor("OptionPane.errorDialog.titlePane.foreground");
        break;
      case JRootPane.QUESTION_DIALOG:
      case JRootPane.COLOR_CHOOSER_DIALOG:
      case JRootPane.FILE_CHOOSER_DIALOG:
        activeBackground = UIManager.getColor("OptionPane.questionDialog.titlePane.background");
        activeForeground = UIManager.getColor("OptionPane.questionDialog.titlePane.foreground");
        break;
      case JRootPane.WARNING_DIALOG:
        activeBackground = UIManager.getColor("OptionPane.warningDialog.titlePane.background");
        activeForeground = UIManager.getColor("OptionPane.warningDialog.titlePane.foreground");
        break;
    }
  }

  private void installDefaults() {
    setFont(UIManager.getFont("InternalFrame.titleFont", getLocale()));
  }


  protected JMenuBar createMenuBar() {
    menuBar = new SystemMenuBar();
    menuBar.setFocusable(false);
    menuBar.setBorderPainted(true);
    menuBar.add(createMenu());
    return menuBar;
  }

  private void close() {
    Window window = getWindow();

    if (window != null) {
      window.dispatchEvent(new WindowEvent(
        window, WindowEvent.WINDOW_CLOSING));
    }
  }

  private void iconify() {
    Frame frame = getFrame();
    if (frame != null) {
      frame.setExtendedState(state | Frame.ICONIFIED);
    }
  }

  private void maximize() {
    Frame frame = getFrame();
    if (frame != null) {
      frame.setExtendedState(state | Frame.MAXIMIZED_BOTH);
    }
  }

  private void restore() {
    Frame frame = getFrame();

    if (frame == null) {
      return;
    }

    if ((state & Frame.ICONIFIED) != 0) {
      frame.setExtendedState(state & ~Frame.ICONIFIED);
    }
    else {
      frame.setExtendedState(state & ~Frame.MAXIMIZED_BOTH);
    }
  }

  private void createActions() {
    closeAction = new CloseAction();
    if (getWindowDecorationStyle() == JRootPane.FRAME) {
      iconifyAction = new IconifyAction();
      restoreAction = new RestoreAction();
      maximizeAction = new MaximizeAction();
    }
  }

  private JMenu createMenu() {

    JMenu systemMenu = new JMenu("");
    systemMenu.setOpaque(false);
    if (appIcon != null) {
        systemMenu.setIcon(new ImageIcon(appIcon));
    }
    if (getWindowDecorationStyle() == JRootPane.FRAME) {
      addMenuItems(systemMenu);
    }
    return systemMenu;
  }

  private void addMenuItems(JMenu menu) {
    menu.add(restoreAction);
    menu.add(iconifyAction);
    if (Toolkit.getDefaultToolkit().isFrameStateSupported(Frame.MAXIMIZED_BOTH)) {
      menu.add(maximizeAction);
    }

    menu.add(new JSeparator());

    menu.add(closeAction);
  }

  private JButton createButton(String accessibleName, Icon icon, Action action) {
    JButton button = new JButton();
    button.setUI(new DarculaRootPaneButtonUI());
    button.setFocusPainted(false);
    button.setFocusable(false);
    button.setOpaque(true);
    button.setContentAreaFilled(false);
    button.putClientProperty("paintActive", Boolean.TRUE);
    button.putClientProperty(AccessibleContext.ACCESSIBLE_NAME_PROPERTY, accessibleName);
    button.setBorder(BorderFactory.createEmptyBorder());
    button.setAction(action);
    button.setToolTipText((String)action.getValue(Action.NAME));
    button.setText(null);
    button.setIcon(icon);
    button.setRolloverEnabled(true);
    return button;
  }

  private void createButtons() {

    closeButton = createButton("Close", UIManager.getIcon("InternalFrame.closeIcon"), closeAction);

    if (getWindowDecorationStyle() == JRootPane.FRAME) {
      maximizeIcon = UIManager.getIcon("InternalFrame.maximizeIcon");
      minimizeIcon = UIManager.getIcon("InternalFrame.minimizeIcon");

      iconifyButton = createButton("Iconify", UIManager.getIcon("InternalFrame.iconifyIcon"), iconifyAction);
      toggleButton = createButton("Maximize", maximizeIcon, restoreAction);
    }
  }

  private LayoutManager createLayout() {
    return new TitlePaneLayout();
  }

  private void setActive(boolean active) {
    closeButton.putClientProperty("paintActive", active);

    if (getWindowDecorationStyle() == JRootPane.FRAME) {
      iconifyButton.putClientProperty("paintActive", active);
      toggleButton.putClientProperty("paintActive", active);
    }

    getRootPane().repaint();
  }

  private void setState(int state) {
    setState(state, false);
  }

  private void setState(int state, boolean updateRegardless) {
    Window wnd = getWindow();

    if (wnd != null && getWindowDecorationStyle() == JRootPane.FRAME) {
      if (this.state == state && !updateRegardless) {
        return;
      }
      Frame frame = getFrame();

      if (frame != null) {
        JRootPane rootPane = getRootPane();

        if (((state & Frame.MAXIMIZED_BOTH) != 0) &&
            (rootPane.getBorder() == null ||
             (rootPane.getBorder() instanceof UIResource)) &&
            frame.isShowing()) {
          rootPane.setBorder(null);
        }
        else if ((state & Frame.MAXIMIZED_BOTH) == 0) {
          // This is a croak, if state becomes bound, this can
          // be nuked.
          rootPaneUI.installBorder(rootPane);
        }
        if (frame.isResizable()) {
          if ((state & Frame.MAXIMIZED_BOTH) != 0) {
            updateToggleButton(restoreAction, minimizeIcon);
            maximizeAction.setEnabled(false);
            restoreAction.setEnabled(true);
          }
          else {
            updateToggleButton(maximizeAction, maximizeIcon);
            maximizeAction.setEnabled(true);
            restoreAction.setEnabled(false);
          }
          if (toggleButton.getParent() == null ||
              iconifyButton.getParent() == null) {
            add(toggleButton);
            add(iconifyButton);
            revalidate();
            repaint();
          }
          toggleButton.setText(null);
        }
        else {
          maximizeAction.setEnabled(false);
          restoreAction.setEnabled(false);
          if (toggleButton.getParent() != null) {
            remove(toggleButton);
            revalidate();
            repaint();
          }
        }
      }
      else {
        // Not contained in a Frame
        maximizeAction.setEnabled(false);
        restoreAction.setEnabled(false);
        iconifyAction.setEnabled(false);
        remove(toggleButton);
        remove(iconifyButton);
        revalidate();
        repaint();
      }
      closeAction.setEnabled(true);
      this.state = state;
    }
  }

  private void updateToggleButton(Action action, Icon icon) {
    toggleButton.setAction(action);
    toggleButton.setIcon(icon);
    toggleButton.setText(null);
  }

  private Frame getFrame() {
    Window window = getWindow();

    if (window instanceof Frame) {
      return (Frame)window;
    }
    return null;
  }

  private Window getWindow() {
    return window;
  }

  private String getTitle() {
    Window w = getWindow();

    if (w instanceof Frame) {
      return ((Frame)w).getTitle();
    }
    else if (w instanceof Dialog) {
      return ((Dialog)w).getTitle();
    }
    return null;
  }

  public void paintComponent(Graphics g) {
    if (getFrame() != null) {
      setState(getFrame().getExtendedState());
    }
    JRootPane rootPane = getRootPane();
    Window window = getWindow();
    boolean leftToRight = (window == null) ?
                          rootPane.getComponentOrientation().isLeftToRight() :
                          window.getComponentOrientation().isLeftToRight();
    int width = getWidth();
    int height = getHeight();

    Color background = activeBackground;
    Color foreground = activeForeground;

    g.setColor(background);
    g.fillRect(0, 0, width, height);

    int xOffset = leftToRight ? 5 : width - 5;

    if (getWindowDecorationStyle() == JRootPane.FRAME) {
      xOffset += leftToRight ? IMAGE_WIDTH + 5 : -IMAGE_WIDTH - 5;
    }

    String theTitle = getTitle();
    if (theTitle != null) {
      FontMetrics fm = SwingUtilities2.getFontMetrics(rootPane, g);

      g.setColor(foreground);

      int yOffset = ((height - fm.getHeight()) / 2) + fm.getAscent();

      Rectangle rect = new Rectangle(0, 0, 0, 0);
      if (iconifyButton != null && iconifyButton.getParent() != null) {
        rect = iconifyButton.getBounds();
      }
      int titleW;

      if (leftToRight) {
        if (rect.x == 0) {
          rect.x = window.getWidth() - window.getInsets().right - 2;
        }
        titleW = rect.x - xOffset - 4;
        theTitle = SwingUtilities2.clipStringIfNecessary(
          rootPane, fm, theTitle, titleW);
      }
      else {
        titleW = xOffset - rect.x - rect.width - 4;
        theTitle = SwingUtilities2.clipStringIfNecessary(
          rootPane, fm, theTitle, titleW);
        xOffset -= SwingUtilities2.stringWidth(rootPane, fm, theTitle);
      }
      int titleLength = SwingUtilities2.stringWidth(rootPane, fm, theTitle);
      SwingUtilities2.drawString(rootPane, g, theTitle, xOffset, yOffset);
      xOffset += leftToRight ? titleLength + 5 : -5;
    }
  }

  private class CloseAction extends AbstractAction {
    public CloseAction() {
      super(UIManager.getString("DarculaTitlePane.closeTitle", getLocale()));
    }

    public void actionPerformed(ActionEvent e) {
      close();
    }
  }


  private class IconifyAction extends AbstractAction {
    public IconifyAction() {
      super(UIManager.getString("DarculaTitlePane.iconifyTitle", getLocale()));
    }

    public void actionPerformed(ActionEvent e) {
      iconify();
    }
  }


  private class RestoreAction extends AbstractAction {
    public RestoreAction() {
      super(UIManager.getString
        ("DarculaTitlePane.restoreTitle", getLocale()));
    }

    public void actionPerformed(ActionEvent e) {
      restore();
    }
  }


  private class MaximizeAction extends AbstractAction {
    public MaximizeAction() {
      super(UIManager.getString("DarculaTitlePane.maximizeTitle", getLocale()));
    }

    public void actionPerformed(ActionEvent e) {
      maximize();
    }
  }


  private class SystemMenuBar extends JMenuBar {
    public void paint(Graphics g) {
      if (isOpaque()) {
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
      }

      if (appIcon != null) {
        g.drawImage(appIcon, 0, 0, IMAGE_WIDTH, IMAGE_HEIGHT, null);
      }
      else {
        Icon icon = UIManager.getIcon("InternalFrame.icon");

        if (icon != null) {
          icon.paintIcon(this, g, 0, 0);
        }
      }
    }

    public Dimension getMinimumSize() {
      return getPreferredSize();
    }

    public Dimension getPreferredSize() {
      Dimension size = super.getPreferredSize();

      return new Dimension(Math.max(IMAGE_WIDTH, size.width),
                           Math.max(size.height, IMAGE_HEIGHT));
    }
  }

  private class TitlePaneLayout implements LayoutManager {
    public void addLayoutComponent(String name, Component c) {
    }

    public void removeLayoutComponent(Component c) {
    }

    public Dimension preferredLayoutSize(Container c) {
      int height = computeHeight();
      //noinspection SuspiciousNameCombination
      return new Dimension(height, height);
    }

    public Dimension minimumLayoutSize(Container c) {
      return preferredLayoutSize(c);
    }

    private int computeHeight() {
      FontMetrics fm = rootPane.getFontMetrics(getFont());
      int fontHeight = fm.getHeight();
      fontHeight += 7;
      int iconHeight = 0;
      if (getWindowDecorationStyle() == JRootPane.FRAME) {
        iconHeight = IMAGE_HEIGHT;
      }

      return Math.max(fontHeight, iconHeight);
    }

    public void layoutContainer(Container c) {
      boolean leftToRight = (window == null) ?
                            getRootPane().getComponentOrientation().isLeftToRight() :
                            window.getComponentOrientation().isLeftToRight();

      int w = getWidth();
      int x;
      int y = 0; // our buttons touch the top of the window
      int spacing;
      int buttonHeight;
      int buttonWidth;

      if (closeButton != null && closeButton.getIcon() != null) {
        buttonHeight = closeButton.getIcon().getIconHeight();
        buttonWidth = closeButton.getIcon().getIconWidth();
      }
      else {
        buttonHeight = IMAGE_HEIGHT;
        buttonWidth = IMAGE_WIDTH;
      }
      buttonWidth += 16;
      buttonHeight += 8;



      x = leftToRight ? w : 0;

      spacing = 5;
      x = leftToRight ? spacing : w - IMAGE_WIDTH - spacing;
      if (menuBar != null) {
        int menuBarY = (getHeight() - menuBar.getPreferredSize().height) / 2;
        menuBar.setBounds(x, menuBarY, IMAGE_WIDTH, IMAGE_HEIGHT);
      }

      x = leftToRight ? w - buttonWidth : 0;
      if (closeButton != null) {
        closeButton.setBounds(x, y, buttonWidth, buttonHeight);
        closeButton.repaint();
      }

      if (!leftToRight) x += buttonWidth;
      spacing = 2;

      if (getWindowDecorationStyle() == JRootPane.FRAME) {
        if (Toolkit.getDefaultToolkit().isFrameStateSupported(
          Frame.MAXIMIZED_BOTH)) {
          if (toggleButton.getParent() != null) {
            x += leftToRight ? -spacing - buttonWidth : spacing;
            toggleButton.setBounds(x, y, buttonWidth, buttonHeight);
            if (!leftToRight) {
              x += buttonWidth;
            }
          }
        }

        if (iconifyButton != null && iconifyButton.getParent() != null) {
          x += leftToRight ? -spacing - buttonWidth : spacing;
          iconifyButton.setBounds(x, y, buttonWidth, buttonHeight);
          if (!leftToRight) {
            x += buttonWidth;
          }
        }
      }
    }
  }

  private class DarculaRootPaneButtonUI extends BasicButtonUI {

      private Color getMainButtonColor(AbstractButton b) {
          return UIManager.getColor(b == closeButton ? "DarculaTitlePane.hovered.closeButtonColor" :
                  "DarculaTitlePane.hovered.buttonColor");
      }

      @Override
      public void paint(Graphics g, JComponent c) {

          AbstractButton b = (AbstractButton)c;
          ButtonModel model = b.getModel();
          Color prevColor = g.getColor();

          if (model.isArmed()) {
              Color color = getMainButtonColor(b);
              color = ColorUtil.shift(color, b == closeButton ? 0.80 : 0.95);
              g.setColor(color);
              g.fillRect(0, 0, b.getWidth(), b.getHeight());
          }
          else if (model.isRollover() || model.isSelected()) {
              Color color = getMainButtonColor(b);
              g.setColor(color);
              g.fillRect(0, 0, b.getWidth(), b.getHeight());
          }

          g.setColor(prevColor);
          super.paint(g, c);
      }
  }

  private class PropertyChangeHandler implements PropertyChangeListener {
    public void propertyChange(PropertyChangeEvent pce) {
      String name = pce.getPropertyName();

      if ("resizable".equals(name) || "state".equals(name)) {
        Frame frame = getFrame();

        if (frame != null) {
          setState(frame.getExtendedState(), true);
        }
        if ("resizable".equals(name)) {
          getRootPane().repaint();
        }
      }
      else if ("title".equals(name)) {
        repaint();
      }
      else if ("componentOrientation" == name) {
        revalidate();
        repaint();
      }
      else if ("iconImage" == name) {
        updateSystemIcon();
        revalidate();
        repaint();
      }
    }
  }

  private void updateSystemIcon() {
    Window window = getWindow();
    if (window == null) {
      appIcon = null;
      return;
    }

    List<Image> icons = window.getIconImages();
    assert icons != null;

    if (icons.size() == 0) {
      appIcon = null;
    } else if (icons.size() == 1) {
      appIcon = icons.get(0);
    } else {
      appIcon = ImageUtil.getScaledIconImage(icons, IMAGE_WIDTH, IMAGE_HEIGHT);
    }
//
//    if (myMenuBar != null) {
//      if (appIcon != null) {
//        myMenuBar.getMenu(0).setIcon(new ImageIcon(appIcon));
//      }
//      else {
//        myMenuBar.getMenu(0).setIcon(EmptyIcon.create(18));
//      }
//    }
  }

  private class WindowHandler extends WindowAdapter {
    public void windowActivated(WindowEvent ev) {
      setActive(true);
    }

    public void windowDeactivated(WindowEvent ev) {
      setActive(false);
    }
  }
}
