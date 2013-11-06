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

package com.agileapes.dragonfly.model;

import com.agileapes.couteau.freemarker.api.Invokable;
import com.agileapes.couteau.freemarker.model.TypedMethodModel;
import com.agileapes.dragonfly.tools.SynchronizedIdentifierDispenser;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/17, 16:43)
 */
public class BeanIdentifierMethod extends TypedMethodModel {

    private SynchronizedIdentifierDispenser<BeanDefinitionModel> identifierDispenser = new SynchronizedIdentifierDispenser<BeanDefinitionModel>();
    private Map<BeanDefinitionModel, String> identifiers = new HashMap<BeanDefinitionModel, String>();

    @Invokable
    public String identify(BeanDefinitionModel bean) {
        if (identifiers.containsKey(bean)) {
            return identifiers.get(bean);
        }
        if (bean.getId() != null) {
            identifiers.put(bean, bean.getId());
            return bean.getId();
        }
        final String identifier = bean.getType() + "#" + identifierDispenser.getIdentifier(bean);
        identifiers.put(bean, identifier);
        return identifier;
    }

}
