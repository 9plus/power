/*
 * Copyright (c) 2011, 2012, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package com.apple.laf;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;

import apple.laf.JRSUIConstants.*;

import com.apple.laf.AquaUtilControlSize.*;
import com.apple.laf.AquaUtils.*;

public class AquaButtonRadioUI extends AquaButtonLabeledUI {
    private static final RecyclableSingleton<AquaButtonRadioUI> instance = new RecyclableSingletonFromDefaultConstructor<AquaButtonRadioUI>(AquaButtonRadioUI.class);
    private static final RecyclableSingleton<ImageIcon> sizingIcon = new RecyclableSingleton<ImageIcon>() {
        protected ImageIcon getInstance() {
            return new ImageIcon(AquaNativeResources.getRadioButtonSizerImage());
        }
    };

    public static ComponentUI createUI(final JComponent b) {
        return instance.get();
    }

    public static Icon getSizingRadioButtonIcon(){
        return sizingIcon.get();
    }

    protected String getPropertyPrefix() {
        return "RadioButton" + ".";
    }

    protected AquaButtonBorder getPainter() {
        return new RadioButtonBorder();
    }

    public static class RadioButtonBorder extends LabeledButtonBorder {
        public RadioButtonBorder() {
            super(new SizeDescriptor(new SizeVariant().replaceMargins("RadioButton.margin")));
            painter.state.set(Widget.BUTTON_RADIO);
        }

        public RadioButtonBorder(final RadioButtonBorder other) {
            super(other);
        }
    }
}
