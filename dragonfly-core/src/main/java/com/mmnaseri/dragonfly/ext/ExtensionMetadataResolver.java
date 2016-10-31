/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 Milad Naseri.
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

package com.mmnaseri.dragonfly.ext;

import com.mmnaseri.couteau.basics.api.Filter;

/**
 * Resolves extension metadata from sources of the given type parameter
 *
 * @param <S> the type of the input from which extension metadata must be resolved
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (2013/11/11, 19:30)
 */
public interface ExtensionMetadataResolver<S> extends Filter<S> {

    /**
     * Called whenever we need to resolved extension metadata from an input object of the
     * given type
     * @param source    the source containing the description of the metadata
     * @return the metadata for the extension
     */
    ExtensionMetadata resolve(S source);

}
