package dev.customitem.util;

import javax.annotation.Nonnull;

/**
 * This utility provides methods to support {@link java.lang.Class} related operations.
 *
 * @author Alphaharrius
 */
public class ClassUtils {

    /**
     * This method returns the class name without package name.
     * @param classObject   {@link java.lang.Class}: The class object.
     * @return              {@link java.lang.String}: The class name without package name.
     */
    @Nonnull
    public static String getClassName(@Nonnull Class<?> classObject) {

        String fullClassName = classObject.getName();
        int substringIndex = fullClassName.lastIndexOf('.');

        if (substringIndex > 0) {

            return fullClassName.substring(substringIndex + 1);
        }

        return fullClassName;
    }

}
