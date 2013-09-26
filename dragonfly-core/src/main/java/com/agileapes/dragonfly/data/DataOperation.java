package com.agileapes.dragonfly.data;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/26, 2:21)
 */
public interface DataOperation {

    OperationType getOperationType();

    DataAccess getDataAccess();

    Object proceed();

}
