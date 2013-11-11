package com.agileapes.dragonfly.ext;

import com.agileapes.couteau.basics.api.Filter;

/**
 * Resolves extension metadata from sources of the given type parameter
 *
 * @param <S> the type of the input from which extension metadata must be resolved
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
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
