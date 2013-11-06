/*
 * Copyright (c) 2013 AgileApes, Ltd.
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall
 * be included in all copies or substantial portions of the
 * Software.
 */

package com.agileapes.dragonfly.ext;

import com.agileapes.dragonfly.entity.EntityDefinitionInterceptor;
import com.agileapes.dragonfly.metadata.TableMetadataInterceptor;

import java.util.Collection;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/18, 16:35)
 */
public interface ExtensionManager extends TableMetadataInterceptor, EntityDefinitionInterceptor {

    void addExtension(Class<?> extension);

    Collection<TableMetadataInterceptor> getMetadataInterceptors(Class<?> entityType);

    Collection<EntityDefinitionInterceptor> getDefinitionInterceptors(Class<?> entityType);

}
