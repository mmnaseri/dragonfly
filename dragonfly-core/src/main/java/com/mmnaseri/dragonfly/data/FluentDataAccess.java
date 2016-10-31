package com.mmnaseri.dragonfly.data;

import com.mmnaseri.dragonfly.fluent.SelectQueryInitiator;

/**
 * @author Milad Naseri (milad.naseri@cdk.com)
 * @since 1.0 (10/31/16, 1:11 PM)
 */
public interface FluentDataAccess extends DataAccess {

    <E> SelectQueryInitiator<E> from(E alias);

}
