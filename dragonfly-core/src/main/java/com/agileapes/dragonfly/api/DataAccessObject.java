package com.agileapes.dragonfly.api;

import com.agileapes.dragonfly.metadata.TableMetadata;

import java.io.Serializable;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/5, 19:25)
 */
public interface DataAccessObject<E, K extends Serializable> {

    void refresh();

    void save();

    void delete();

    K accessKey();

    void changeKey(K key);

    boolean hasKey();

    String getQualifiedName();

    TableMetadata<E> getTableMetadata();

}
