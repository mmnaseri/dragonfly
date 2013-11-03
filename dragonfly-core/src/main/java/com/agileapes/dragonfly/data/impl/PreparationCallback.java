package com.agileapes.dragonfly.data.impl;

import java.util.Map;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/11/3, 13:00)
 */
public interface PreparationCallback {

    void prepare(Object entity, Map<String, Object> values);

}
