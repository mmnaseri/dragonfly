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

package com.mmnaseri.dragonfly.metadata.impl;

/**
 * This class is a many to many relation descriptor. It is used extensively by the table
 * metadata context to hold virtual table metadata for middle entities in a many-to-many
 * relation
 *
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (2013/10/3, 2:17)
 * @see DefaultTableMetadataContext
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
