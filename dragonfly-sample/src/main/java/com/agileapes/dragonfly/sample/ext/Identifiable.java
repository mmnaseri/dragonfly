package com.agileapes.dragonfly.sample.ext;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/18, 0:45)
 */
public interface Identifiable {

    Long getUniqueKey();

    void setUniqueKey(Long identifier);

}
