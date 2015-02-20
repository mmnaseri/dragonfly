/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 AgileApes, Ltd.
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

package com.agileapes.dragonfly.runtime.repo.impl;

import com.agileapes.couteau.basics.api.Filter;
import com.agileapes.couteau.reflection.util.ClassUtils;
import com.agileapes.dragonfly.runtime.repo.CrudRepository;
import com.agileapes.dragonfly.runtime.repo.EntityRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.core.Ordered;

import java.util.List;

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (14/8/14 AD, 12:23)
 */
public class CrudRepositoryContext implements BeanFactoryPostProcessor, Ordered {

    private static final Log log = LogFactory.getLog(CrudRepositoryContext.class);
    private final List<Class> repositories;

    public CrudRepositoryContext(ClassLoader classLoader, String... basePackages) {
        //noinspection unchecked
        this.repositories = with(new EntityRepositoryLookupSource(classLoader).getClasses(basePackages)).keep(new Filter<Class>() {
            @Override
            public boolean accepts(Class item) {
                return !item.equals(EntityRepository.class) && !item.equals(CrudRepository.class) && item.isInterface();
            }
        }).list();
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        for (int i = 0; i < repositories.size(); i++) {
            Class repository = repositories.get(i);
            final Class[] typeArguments = ClassUtils.resolveTypeArguments(repository, EntityRepository.class);
            if (Object.class.equals(typeArguments[0])) {
                log.info("Discarding repository " + repository.getCanonicalName() + " because it is not bound tightly enough");
                continue;
            }
            log.info("Registering repository " + repository.getSimpleName() + " for entities of type " + typeArguments[0].getCanonicalName());
            beanFactory.registerSingleton("_repo" + repository.getSimpleName() + i, Enhancer.create(Object.class, new Class[]{repository}, new CrudRepositoryInterceptor(beanFactory, repository, typeArguments[0], typeArguments[1])));
        }
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }

    public List<Class> getRepositories() {
        return repositories;
    }
}
