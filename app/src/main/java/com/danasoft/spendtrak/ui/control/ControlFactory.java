package com.danasoft.spendtrak.ui.control;

public class ControlFactory {
    public static final String BUTTONS = "buttons";
    public static final String SEARCH = "search";
    public static final String VTOTAL = "vtotal";
    public static final String VMERCHANT = "vmerchant";
    public static final String VSEARCH = "vsearch";
    public static final String SCANCEL = "scancel";
    public static final String TRESULTS = "tresults";
    public static final int GREATER    = 0x00;
    public static final int LESS       = 0x07;
    public static final int BETWEEN    = 0x0F;
    public static final int X_AMOUNT    = 0x7F;
    public static final int BEFORE     = 0xF0;
    public static final int AFTER      = 0xF7;
    public static final int ONTHEDAY   = 0xFF;
    public static final int D_BETWEEN    = 0xAF;

    public static VisualizeButtonsControl getVisualizeButtons() {
        return VisualizeButtonsControl.newInstance();
    }

    public static SearchControl getSearchControl() {
        return SearchControl.newInstance();
    }
}
