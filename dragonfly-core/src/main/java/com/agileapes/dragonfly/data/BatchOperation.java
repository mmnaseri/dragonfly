package com.agileapes.dragonfly.data;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/25, 0:16)
 */
public interface BatchOperation {

    void execute(DataAccess dataAccess);

}
