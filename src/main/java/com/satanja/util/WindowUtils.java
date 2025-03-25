package com.satanja.util;

import com.satanja.rangetree.Window;
import com.satanja.rangetree.Point;

public class WindowUtils {

    public static boolean windowContainsPoint(final Window window, final Point point) {
        if (window.getXMin() <= point.getX() && point.getX() <= window.getXMax() &&
                window.getYMin() <= point.getY() && point.getY() <= window.getYMax()) {
            return true;
        }
        return false;
    }
}
