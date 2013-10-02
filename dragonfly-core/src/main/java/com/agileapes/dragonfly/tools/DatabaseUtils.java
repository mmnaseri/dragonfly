package com.agileapes.dragonfly.tools;

import com.agileapes.couteau.basics.api.Transformer;
import com.agileapes.dragonfly.metadata.TableMetadata;

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/8/29, 14:13)
 */
public abstract class DatabaseUtils {

    public static String shorten(final String name, int length) {
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
        return shorten(name, 12).toLowerCase();
    }

    public static String getMiddleTableName(TableMetadata<?> first, TableMetadata<?> second, String firstProperty, String secondProperty) {
        final Transformer<String, String> transformer = new Transformer<String, String>() {
            @Override
            public String map(String input) {
                return shorten(input, 10);
            }
        };
        final String tableNames = with(first.getName(), second.getName()).sort().transform(transformer).join("_");
        final String finalName = with(tableNames).add(with(firstProperty, secondProperty).transform(transformer).sort().list()).join("_");
        return shorten(finalName, 16).toLowerCase();
    }

}
