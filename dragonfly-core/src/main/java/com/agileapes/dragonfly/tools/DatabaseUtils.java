package com.agileapes.dragonfly.tools;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/8/29, 14:13)
 */
public abstract class DatabaseUtils {

    public static String shorten(final String name) {
        final int length = 12;
        String identifier = name;
        while (identifier.length() > length) {
            String original = identifier;
            identifier = identifier.replaceFirst("[aeiou]", "");
            if (identifier.equals(original)) {
                break;
            }
        }
        while (identifier.length() > length) {
            String original = identifier;
            identifier = identifier.replaceFirst("([a-z])(.*?)\\1", "$1$2");
            if (identifier.equals(original)) {
                break;
            }
        }
        if (identifier.length() > length) {
            identifier = identifier.substring(0, length);
        }
        return identifier;
    }

    public static String unify(final String name) {
        return shorten(name).toLowerCase();
    }

}
