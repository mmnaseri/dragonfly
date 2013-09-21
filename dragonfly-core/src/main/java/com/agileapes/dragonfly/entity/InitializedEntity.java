package com.agileapes.dragonfly.entity;

/**
 * This interface allows for the initialization of all data access objects with the given
 * initial state
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/5, 16:04)
 */
public interface InitializedEntity<E> {

    /**
     * Initializes the proxied entity
     * @param entityType    the type of the entity
     * @param entity        the actual entity
     * @param token           the context token for the object. This token is expected to be
     *                      the same throughout the entity's lifetime.
     */
    void initialize(Class<E> entityType, E entity, String token);

    /**
     * @return the token given to the entity by the initializing context
     */
    String getToken();

    /**
     * Sets the original copy of the entity
     * @param originalCopy    the original copy. This will be the reference for future updates.
     */
    void setOriginalCopy(E originalCopy);

    /**
     * @return the original copy of the entity, prior to being tampered with throughout the
     * application
     */
    E getOriginalCopy();

    /**
     * Determines whether or not the underlying entity has been changed in any way
     * @return {@code true} means that there as at least one apparent difference between this
     * entity and the original copy
     */
    boolean isDirtied();

    /**
     * Freezes the entity's state. This will prevent auto-loading of lazy-load properties,
     * dirty checking, etc.
     */
    void freeze();

    /**
     * Unfreezes the entity's state, telling the data access object that it should now behave
     * as though the entity is being used for application
     */
    void unfreeze();



}
