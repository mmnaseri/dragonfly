package com.agileapes.dragonfly.ext;

import com.agileapes.couteau.basics.api.Filter;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/18, 15:50)
 */
public interface ExtensionExpressionParser {

    Filter<Class<?>> parse(String expression);

}
