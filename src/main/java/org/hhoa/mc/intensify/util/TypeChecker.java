package org.hhoa.mc.intensify.util;

import java.util.List;

public class TypeChecker {
    public static void checkAllElementsOfType(List<?> list, Class<?> type) {
        for (Object o : list) {
            if (!type.isInstance(o)) {
                throw new IllegalArgumentException("Element " + o + " is not of type " + type);
            }
        }
    }
}
