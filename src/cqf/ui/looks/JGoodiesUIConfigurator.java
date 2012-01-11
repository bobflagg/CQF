/*
 * Copyright (c) 2001-2004 JGoodies Karsten Lentzsch. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 *  o Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer. 
 *     
 *  o Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution. 
 *     
 *  o Neither the name of JGoodies Karsten Lentzsch nor the names of 
 *    its contributors may be used to endorse or promote products derived 
 *    from this software without specific prior written permission. 
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE 
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 */

package cqf.ui.looks;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.*;


import com.jgoodies.clearlook.ClearLookManager;
import com.jgoodies.clearlook.ClearLookMode;

import javax.swing.plaf.UIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;



import com.jgoodies.plaf.BorderStyle;
import com.jgoodies.plaf.FontSizeHints;
import com.jgoodies.plaf.HeaderStyle;
import com.jgoodies.plaf.plastic.PlasticLookAndFeel;
import com.jgoodies.plaf.plastic.PlasticTheme;
import com.jgoodies.plaf.plastic.PlasticXPLookAndFeel;

import com.jgoodies.forms.factories.Borders;
import com.jgoodies.plaf.LookUtils;
import com.jgoodies.plaf.Options;
import com.jgoodies.plaf.windows.ExtWindowsLookAndFeel;
import cqf.configuration.ConfigurationException;
import cqf.ui.UIConfigurator;



import java.util.Properties;

/**
 * Describes most of the optional settings of the JGoodies Looks.
 * Used by the <code>DemoFrame</code> to configure the UI.
 *
 * @author  Karsten Lentzsch
 * @version $Revision: 1.1.1.1 $
 *
 * @see     com.jgoodies.plaf.BorderStyle
 * @see     com.jgoodies.plaf.FontSizeHints
 * @see     com.jgoodies.plaf.HeaderStyle
 * @see     com.jgoodies.plaf.Options
 */
public final class JGoodiesUIConfigurator implements UIConfigurator {
    public static final Dimension PREFERRED_SIZE =
        LookUtils.IS_LOW_RESOLUTION ? new Dimension(500, 400) : new Dimension(500, 400);
//        LookUtils.IS_LOW_RESOLUTION ? new Dimension(800, 495) : new Dimension(1000, 618);

    private LookAndFeel selectedLookAndFeel;

    private PlasticTheme selectedTheme;

    private Boolean useSystemFonts;

    private FontSizeHints fontSizeHints;

    private boolean useNarrowButtons;

    private boolean tabIconsEnabled;

    private Boolean popupDropShadowEnabled;

    private String plasticTabStyle;

    private boolean plasticHighContrastFocusEnabled;

    private HeaderStyle menuBarHeaderStyle;

    private BorderStyle menuBarPlasticBorderStyle;

    private BorderStyle menuBarWindowsBorderStyle;

    private Boolean menuBar3DHint;

    private HeaderStyle toolBarHeaderStyle;

    private BorderStyle toolBarPlasticBorderStyle;

    private BorderStyle toolBarWindowsBorderStyle;

    private Boolean toolBar3DHint;

    private ClearLookMode clearLookMode;

    private String clearLookPolicyName;


    // Instance Creation ******************************************************

    public JGoodiesUIConfigurator() {
        setSelectedLookAndFeel(new PlasticXPLookAndFeel());
        setSelectedTheme(PlasticLookAndFeel.createMyDefaultTheme());
        setUseSystemFonts(Boolean.TRUE);
        setFontSizeHints(FontSizeHints.MIXED);
        setUseNarrowButtons(false);
        setTabIconsEnabled(true);
        setPlasticTabStyle(PlasticLookAndFeel.TAB_STYLE_DEFAULT_VALUE);
        setPlasticHighContrastFocusEnabled(false);
        setMenuBarHeaderStyle(null);
        setMenuBarPlasticBorderStyle(null);
        setMenuBarWindowsBorderStyle(null);
        setMenuBar3DHint(null);
        setToolBarHeaderStyle(null);
        setToolBarPlasticBorderStyle(null);
        setToolBarWindowsBorderStyle(null);
        setToolBar3DHint(null);
        setClearLookMode(ClearLookMode.OFF);
        setClearLookPolicyName(ClearLookManager.getPolicy().getClass().getName());
    }


    // Accessors **************************************************************

    public ClearLookMode getClearLookMode() {
        return clearLookMode;
    }

    public void setClearLookMode(ClearLookMode clearLookMode) {
        this.clearLookMode = clearLookMode;
    }

    public String getClearLookPolicyName() {
        return clearLookPolicyName;
    }

    public void setClearLookPolicyName(String clearLookPolicyName) {
        this.clearLookPolicyName = clearLookPolicyName;
    }

    public FontSizeHints getFontSizeHints() {
        return fontSizeHints;
    }

    public void setFontSizeHints(FontSizeHints fontSizeHints) {
        this.fontSizeHints = fontSizeHints;
    }

    public Boolean getMenuBar3DHint() {
        return menuBar3DHint;
    }

    public void setMenuBar3DHint(Boolean menuBar3DHint) {
        this.menuBar3DHint = menuBar3DHint;
    }

    public HeaderStyle getMenuBarHeaderStyle() {
        return menuBarHeaderStyle;
    }

    public void setMenuBarHeaderStyle(HeaderStyle menuBarHeaderStyle) {
        this.menuBarHeaderStyle = menuBarHeaderStyle;
    }

    public BorderStyle getMenuBarPlasticBorderStyle() {
        return menuBarPlasticBorderStyle;
    }

    public void setMenuBarPlasticBorderStyle(BorderStyle menuBarPlasticBorderStyle) {
        this.menuBarPlasticBorderStyle = menuBarPlasticBorderStyle;
    }

    public BorderStyle getMenuBarWindowsBorderStyle() {
        return menuBarWindowsBorderStyle;
    }

    public void setMenuBarWindowsBorderStyle(BorderStyle menuBarWindowsBorderStyle) {
        this.menuBarWindowsBorderStyle = menuBarWindowsBorderStyle;
    }

    public Boolean isPopupDropShadowEnabled() {
        return popupDropShadowEnabled;
    }

    public void setPopupDropShadowEnabled(Boolean popupDropShadowEnabled) {
        this.popupDropShadowEnabled = popupDropShadowEnabled;
    }

    public boolean isPlasticHighContrastFocusEnabled() {
        return plasticHighContrastFocusEnabled;
    }

    public void setPlasticHighContrastFocusEnabled(boolean plasticHighContrastFocusEnabled) {
        this.plasticHighContrastFocusEnabled = plasticHighContrastFocusEnabled;
    }

    public String getPlasticTabStyle() {
        return plasticTabStyle;
    }

    public void setPlasticTabStyle(String plasticTabStyle) {
        this.plasticTabStyle = plasticTabStyle;
    }

    public LookAndFeel getSelectedLookAndFeel() {
        return selectedLookAndFeel;
    }

    public void setSelectedLookAndFeel(LookAndFeel selectedLookAndFeel) {
        this.selectedLookAndFeel = selectedLookAndFeel;
    }

    public PlasticTheme getSelectedTheme() {
        return selectedTheme;
    }

    public void setSelectedTheme(PlasticTheme selectedTheme) {
        this.selectedTheme = selectedTheme;
    }

    public boolean isTabIconsEnabled() {
        return tabIconsEnabled;
    }

    public void setTabIconsEnabled(boolean tabIconsEnabled) {
        this.tabIconsEnabled = tabIconsEnabled;
    }

    public Boolean getToolBar3DHint() {
        return toolBar3DHint;
    }

    public void setToolBar3DHint(Boolean toolBar3DHint) {
        this.toolBar3DHint = toolBar3DHint;
    }

    public HeaderStyle getToolBarHeaderStyle() {
        return toolBarHeaderStyle;
    }

    public void setToolBarHeaderStyle(HeaderStyle toolBarHeaderStyle) {
        this.toolBarHeaderStyle = toolBarHeaderStyle;
    }

    public BorderStyle getToolBarPlasticBorderStyle() {
        return toolBarPlasticBorderStyle;
    }

    public void setToolBarPlasticBorderStyle(BorderStyle toolBarPlasticBorderStyle) {
        this.toolBarPlasticBorderStyle = toolBarPlasticBorderStyle;
    }

    public BorderStyle getToolBarWindowsBorderStyle() {
        return toolBarWindowsBorderStyle;
    }

    public void setToolBarWindowsBorderStyle(BorderStyle toolBarWindowsBorderStyle) {
        this.toolBarWindowsBorderStyle = toolBarWindowsBorderStyle;
    }

    public boolean isUseNarrowButtons() {
        return useNarrowButtons;
    }

    public void setUseNarrowButtons(boolean useNarrowButtons) {
        this.useNarrowButtons = useNarrowButtons;
    }

    public Boolean isUseSystemFonts() {
        return useSystemFonts;
    }

    public void setUseSystemFonts(Boolean useSystemFonts) {
        this.useSystemFonts = useSystemFonts;
    }

    // configuration  **************************************************************
    public void configure(Properties properties) throws ConfigurationException {}

    /**
     * Configures the user interface; requests Swing settings and
     * jGoodies Looks options from the launcher.
     */
    public void configureUI() {
        Options.setDefaultIconSize(new Dimension(18, 18));

        // Set font options
        UIManager.put(
            Options.USE_SYSTEM_FONTS_APP_KEY,
            isUseSystemFonts());
        Options.setGlobalFontSizeHints(getFontSizeHints());
        Options.setUseNarrowButtons(isUseNarrowButtons());

        // Global options
        Options.setTabIconsEnabled(isTabIconsEnabled());
        ClearLookManager.setMode(getClearLookMode());
        ClearLookManager.setPolicy(getClearLookPolicyName());
        UIManager.put(Options.POPUP_DROP_SHADOW_ENABLED_KEY,
                isPopupDropShadowEnabled());

        // Swing Settings
        LookAndFeel selectedLaf = getSelectedLookAndFeel();
        if (selectedLaf instanceof PlasticLookAndFeel) {
            PlasticLookAndFeel.setMyCurrentTheme(getSelectedTheme());
            PlasticLookAndFeel.setTabStyle(getPlasticTabStyle());
            PlasticLookAndFeel.setHighContrastFocusColorsEnabled(
                isPlasticHighContrastFocusEnabled());
        } else if (selectedLaf.getClass() == MetalLookAndFeel.class) {
            MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme());
        }

        // Work around caching in MetalRadioButtonUI
        JRadioButton radio = new JRadioButton();
        radio.getUI().uninstallUI(radio);
        JCheckBox checkBox = new JCheckBox();
        checkBox.getUI().uninstallUI(checkBox);

        try {
            UIManager.setLookAndFeel(selectedLaf);
        } catch (Exception e) {
            System.out.println("Can't change L&F: " + e);
        }

    }

    public void configureComponent(Component component) {
        if (component instanceof JToolBar) configure((JToolBar) component);
        else if (component instanceof JMenuBar) configure((JMenuBar) component);
        else if (component instanceof JMenuItem) configure((JMenuItem) component);
        else if (component instanceof JPanel) configure((JPanel) component);
    }

    private void configure(JToolBar toolBar) {
        toolBar.putClientProperty("JToolBar.isRollover", Boolean.TRUE);
        // Swing
        toolBar.putClientProperty(
            Options.HEADER_STYLE_KEY,
            getToolBarHeaderStyle());
        toolBar.putClientProperty(
            PlasticLookAndFeel.BORDER_STYLE_KEY,
            getToolBarPlasticBorderStyle());
        toolBar.putClientProperty(
            ExtWindowsLookAndFeel.BORDER_STYLE_KEY,
            getToolBarWindowsBorderStyle());
        toolBar.putClientProperty(
            PlasticLookAndFeel.IS_3D_KEY,
            getToolBar3DHint());

    }

    private void configure(JMenuBar menuBar) {
        menuBar.putClientProperty(Options.HEADER_STYLE_KEY,
        getMenuBarHeaderStyle());
        menuBar.putClientProperty(PlasticLookAndFeel.BORDER_STYLE_KEY,
        getMenuBarPlasticBorderStyle());
        menuBar.putClientProperty(ExtWindowsLookAndFeel.BORDER_STYLE_KEY,
        getMenuBarWindowsBorderStyle());
        menuBar.putClientProperty(PlasticLookAndFeel.IS_3D_KEY,
        getMenuBar3DHint());
    }

    private void configure(JPanel panel) {
       panel.setBorder(Borders.DIALOG_BORDER);
    }

    private void configure(JMenuItem menuItem) {
        menuItem.putClientProperty(Options.NO_ICONS_KEY, Boolean.TRUE);
    }

    public Dimension getPreferredSize() {
        return PREFERRED_SIZE;
    }


}