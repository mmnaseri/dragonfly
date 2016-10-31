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

package com.mmnaseri.dragonfly.fluent.tools;

import com.mmnaseri.couteau.context.error.RegistryException;
import com.mmnaseri.couteau.context.value.ValueReaderContext;
import com.mmnaseri.couteau.context.value.impl.*;
import com.mmnaseri.couteau.reflection.beans.BeanAccessor;
import com.mmnaseri.couteau.reflection.beans.BeanWrapper;
import com.mmnaseri.couteau.reflection.beans.impl.MethodBeanAccessor;
import com.mmnaseri.couteau.reflection.beans.impl.MethodBeanWrapper;
import com.mmnaseri.dragonfly.entity.impl.DefaultMapEntityCreator;
import com.mmnaseri.dragonfly.fluent.error.ValueAccessFailureException;
import com.mmnaseri.dragonfly.tools.MapTools;

import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (14/8/9 AD, 3:39)
 */
public abstract class QueryBuilderTools {

    public static final AtomicLong COUNTER = new AtomicLong(0);
    private static final boolean MAKE_ACCESSIBLE = true;

    /**
     * Given a data type, initiates it and creates a value for it which can be later used to compare references
     * @param clazz    the type of object to be instantiated
     * @param <T>      type parameter for the object
     * @return instantiated and initialized object
     * This method is courtesy of the `iciql` project
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T> T newObject(Class<T> clazz) {
        // must create new instances
        if (clazz == int.class || clazz == Integer.class) {
            return (T) new Integer((int) COUNTER.getAndIncrement());
        } else if (clazz == String.class) {
            return (T) ("" + COUNTER.getAndIncrement());
        } else if (clazz == long.class || clazz == Long.class) {
            return (T) new Long(COUNTER.getAndIncrement());
        } else if (clazz == short.class || clazz == Short.class) {
            return (T) new Short((short) COUNTER.getAndIncrement());
        } else if (clazz == byte.class || clazz == Byte.class) {
            return (T) new Byte((byte) COUNTER.getAndIncrement());
        } else if (clazz == float.class || clazz == Float.class) {
            return (T) new Float(COUNTER.getAndIncrement());
        } else if (clazz == double.class || clazz == Double.class) {
            return (T) new Double(COUNTER.getAndIncrement());
        } else if (clazz == boolean.class || clazz == Boolean.class) {
            COUNTER.getAndIncrement();
            //noinspection BooleanConstructorCall
            return (T) new Boolean(false); //we have to make a new boolean object so that the reference is comparable.
            //the value is not so much important
        } else if (clazz == BigDecimal.class) {
            return (T) new BigDecimal(COUNTER.getAndIncrement());
        } else if (clazz == BigInteger.class) {
            return (T) new BigInteger("" + COUNTER.getAndIncrement());
        } else if (clazz == java.sql.Date.class) {
            return (T) new java.sql.Date(COUNTER.getAndIncrement());
        } else if (clazz == java.sql.Time.class) {
            return (T) new java.sql.Time(COUNTER.getAndIncrement());
        } else if (clazz == java.sql.Timestamp.class) {
            return (T) new java.sql.Timestamp(COUNTER.getAndIncrement());
        } else if (clazz == java.util.Date.class) {
            return (T) new java.util.Date(COUNTER.getAndIncrement());
        } else if (clazz == byte[].class) {
            COUNTER.getAndIncrement();
            return (T) new byte[0];
        } else if (clazz.isEnum()) {
            COUNTER.getAndIncrement();
            // enums can not be instantiated reflectively
            // return first constant as reference
            return clazz.getEnumConstants()[0];
        } else if (clazz == UUID.class) {
            COUNTER.getAndIncrement();
            return (T) UUID.randomUUID();
        }
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            if (MAKE_ACCESSIBLE) {
                Constructor[] constructors = clazz.getDeclaredConstructors();
                // try 0 length constructors
                for (Constructor c : constructors) {
                    if (c.getParameterTypes().length == 0) {
                        c.setAccessible(true);
                        try {
                            return clazz.newInstance();
                        } catch (Exception e2) {
                            // ignore
                        }
                    }
                }
                // try 1 length constructors
                for (Constructor c : constructors) {
                    if (c.getParameterTypes().length == 1) {
                        c.setAccessible(true);
                        try {
                            return (T) c.newInstance(new Object[1]);
                        } catch (Exception e2) {
                            // ignore
                        }
                    }
                }
            }
            throw new RuntimeException("Missing default constructor?! Exception trying to instantiate " + clazz.getCanonicalName() + ": " + e.getMessage(), e);
        }
    }

    /**
     * @return a value reader context for use with a value reader
     */
    public static ValueReaderContext getValueReaderContext() {
        try {
            final ClassValueReader item = new ClassValueReader();
            item.setClassLoader(DefaultMapEntityCreator.class.getClassLoader());
            return (ValueReaderContext)
                    new DefaultValueReaderContext()
                            .register(item)
                            .register(new DateValueReader())
                            .register(new EnumValueReader())
                            .register(new FileValueReader())
                            .register(new PrimitiveValueReader())
                            .register(new UrlValueReader());
        } catch (RegistryException e) {
            return null;
        }
    }


    public static Map<String, Object> unwrap(Object binding) {
        if (binding instanceof Map) {
            //noinspection unchecked
            return MapTools.copy((Map<String, Object>) binding);
        } else if (binding instanceof List) {
            List list = (List) binding;
            final HashMap<String, Object> map = new HashMap<String, Object>();
            for (Integer i = 0; i < list.size(); i++) {
                Object o = list.get(i);
                map.put(i.toString(), o);
            }
            return map;
        }
        final BeanAccessor<Object> accessor = new MethodBeanAccessor<Object>(binding);
        final HashMap<String, Object> map = new HashMap<String, Object>();
        for (String propertyName : accessor.getPropertyNames()) {
            try {
                map.put(propertyName, accessor.getPropertyValue(propertyName));
            } catch (Exception e) {
                throw new ValueAccessFailureException("Failed to access property value on provided binding " + propertyName, e);
            }
        }
        return map;
    }

    public static Map<String, Object> unwrap(Object binding, Object fallback) {
        if (binding instanceof Map || binding instanceof List) {
            return unwrap(fallback);
        }
        final BeanAccessor<Object> accessor = new MethodBeanAccessor<Object>(binding);
        final HashMap<String, Object> map = new HashMap<String, Object>();
        for (String propertyName : accessor.getPropertyNames()) {
            try {
                map.put(propertyName, accessor.getPropertyValue(propertyName));
            } catch (Exception e) {
                throw new ValueAccessFailureException("Failed to access property value on provided binding " + propertyName, e);
            }
        }
        return map;
    }

    public static void setValue(Object target, String propertyName, Object value) {
        if (target instanceof Map) {
            //noinspection unchecked
            ((Map) target).put(propertyName, value);
        } else if (target instanceof List) {
            //noinspection unchecked
            ((List) target).set(Integer.parseInt(propertyName), value);
        } else {
            final BeanWrapper<Object> wrapper = new MethodBeanWrapper<Object>(target);
            //noinspection EmptyCatchBlock
            try {
                if (wrapper.hasProperty(propertyName) && wrapper.isWritable(propertyName)) {
                    wrapper.setPropertyValue(propertyName, value);
                }
            } catch (Exception e) {}
        }
    }

}
