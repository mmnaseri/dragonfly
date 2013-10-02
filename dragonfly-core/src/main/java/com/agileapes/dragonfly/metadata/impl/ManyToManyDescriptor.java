package com.agileapes.dragonfly.metadata.impl;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/10/3, 2:17)
 */
public class ManyToManyDescriptor {

    private final Class<?> here;
    private final Class<?> there;
    private final String localProperty;
    private final String targetProperty;

    public ManyToManyDescriptor(Class<?> here, String localProperty, Class<?> there, String targetProperty) {
        this.targetProperty = targetProperty;
        this.here = here;
        this.there = there;
        this.localProperty = localProperty;
    }

    public Class<?> getHere() {
        return here;
    }

    public Class<?> getThere() {
        return there;
    }

    public String getLocalProperty() {
        return localProperty;
    }

    public String getTargetProperty() {
        return targetProperty;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ManyToManyDescriptor that = (ManyToManyDescriptor) o;
        return this.here.equals(that.here) && this.localProperty.equals(that.localProperty)
                && this.there.equals(that.there) && this.targetProperty.equals(that.targetProperty)
                || this.there.equals(that.here) && this.targetProperty.equals(that.localProperty)
                && this.here.equals(that.there) && this.localProperty.equals(that.targetProperty);
    }

    @Override
    public int hashCode() {
        int result = here.hashCode();
        result = 31 * result + there.hashCode();
        result = 31 * result + localProperty.hashCode();
        result = 31 * result + targetProperty.hashCode();
        return result;
    }
}
