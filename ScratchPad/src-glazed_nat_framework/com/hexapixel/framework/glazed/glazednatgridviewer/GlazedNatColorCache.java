package com.hexapixel.framework.glazed.glazednatgridviewer;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public final class GlazedNatColorCache {

    public static final RGB        BLACK = new RGB(0, 0, 0);
    public static final RGB        WHITE = new RGB(255, 255, 255);

    private static Map<RGB, Color> colorTable;
    private static GlazedNatColorCache      instance;

    static {
        colorTable = new HashMap<RGB, Color>();
        new GlazedNatColorCache();
    }

    private GlazedNatColorCache() {
        instance = this;
    }

    public static GlazedNatColorCache getInstance() {
        return instance;
    }

    /**
     * Disposes of all colors. DO ONLY CALL THIS WHEN YOU ARE SHUTTING DOWN YOUR
     * APPLICATION!
     */
    public static void disposeColors() {
        Iterator<Color> e = colorTable.values().iterator();
        while (e.hasNext())
            e.next().dispose();

        colorTable.clear();
    }

    public static Color getWhite() {
        return getColorFromRGB(new RGB(255, 255, 255));
    }

    public static Color getBlack() {
        return getColorFromRGB(new RGB(0, 0, 0));
    }

    public static Color getColorFromRGB(RGB rgb) {
        Color color = colorTable.get(rgb);

        if (color == null) {
            color = new Color(Display.getCurrent(), rgb);
            colorTable.put(rgb, color);
        }

        return color;
    }

    public Color getColor(RGB rgb) {
        return GlazedNatColorCache.getColorFromRGB(rgb);
    }

    public static Color getColor(int r, int g, int b) {
        RGB rgb = new RGB(r, g, b);
        Color color = colorTable.get(rgb);

        if (color == null) {
            color = new Color(Display.getCurrent(), rgb);
            colorTable.put(rgb, color);
        }

        return color;
    }

    public void dispose() {
        GlazedNatColorCache.disposeColors();
    }

}
