/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 AgileApes, Ltd.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.agileapes.dragonfly.metadata.impl;

import com.agileapes.dragonfly.metadata.OrderMetadata;
import com.agileapes.dragonfly.metadata.PagedResultOrderMetadata;
import com.agileapes.dragonfly.metadata.ResultOrderMetadata;

import java.util.Collection;
import java.util.Collections;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (14/8/21 AD, 13:29)
 */
public class DefaultPagedResultOrderMetadata extends DefaultResultOrderMetadata implements PagedResultOrderMetadata {

    private final int pageSize;
    private final int pageNumber;

    public DefaultPagedResultOrderMetadata(int pageSize, int pageNumber) {
        this(Collections.<OrderMetadata>emptyList(), pageSize, pageNumber);
    }

    public DefaultPagedResultOrderMetadata(ResultOrderMetadata resultOrderMetadata, int pageSize, int pageNumber) {
        this((Collection<OrderMetadata>) resultOrderMetadata, pageSize, pageNumber);
    }

    public DefaultPagedResultOrderMetadata(Collection<OrderMetadata> ordering, int pageSize, int pageNumber) {
        super(ordering);
        this.pageSize = pageSize;
        this.pageNumber = pageNumber;
    }

    public DefaultPagedResultOrderMetadata(ResultOrderMetadata ordering) {
        this(ordering, ordering instanceof PagedResultOrderMetadata ? ((PagedResultOrderMetadata) ordering).getPageSize() : -1, ordering instanceof PagedResultOrderMetadata ? ((PagedResultOrderMetadata) ordering).getPageNumber() : -1);
    }

    @Override
    public int getPageSize() {
        return pageSize;
    }

    @Override
    public int getPageNumber() {
        return pageNumber;
    }

}
