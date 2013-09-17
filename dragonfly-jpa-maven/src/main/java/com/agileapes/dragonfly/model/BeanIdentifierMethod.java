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
