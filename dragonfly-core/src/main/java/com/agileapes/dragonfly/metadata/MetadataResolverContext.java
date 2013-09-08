package com.agileapes.dragonfly.metadata;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/8, 19:41)
 */
public interface MetadataResolverContext extends MetadataResolver {

    void addMetadataResolver(MetadataResolver metadataResolver);

}
