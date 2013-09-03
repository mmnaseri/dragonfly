package com.agileapes.dragonfly.dialect;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/1, 1:33)
 */
public interface DatabaseDialect {

    Character getIdentifierEscapeCharacter();

    Character getSchemaSeparator();

    Character getStringEscapeCharacter();

}
