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

package com.agileapes.dragonfly.metadata;

/**
 * This enum is used to decide whether or not ambiguity in choosing metadata resolver
 * for an entity should be resolved.
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/8, 19:46)
 */
public enum MetadataResolveStrategy {

    /**
     * If this option is chosen, in case of ambiguity, an exception will be thrown
     */
    UNAMBIGUOUS,
    /**
     * This option denotes that in case of multiple metadata resolvers being available
     * for an entity type, the first one sorted by the priority declared through the
     * {@link com.agileapes.couteau.context.contract.OrderedBean#getOrder()} method will
     * be chosen
     */
    ORDERED
    
}
