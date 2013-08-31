package com.agileapes.dragonfly.entity;

import com.agileapes.dragonfly.metadata.TableMetadata;

import java.util.Map;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/8/31, 17:13)
 */
public interface EntityMapCreator {

    <E> Map<String, Object> toMap(TableMetadata<E> tableMetadata, E entity);

}
