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

package com.agileapes.dragonfly.runtime.analysis;

import com.agileapes.couteau.basics.api.Processor;
import com.agileapes.couteau.concurrency.manager.TaskManager;
import com.agileapes.couteau.concurrency.manager.impl.ThreadPoolTaskManager;
import com.agileapes.dragonfly.runtime.analysis.impl.AnalyzerTask;
import com.agileapes.dragonfly.runtime.session.SessionPreparator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.util.StopWatch;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (14/3/17 AD, 0:40)
 */
@SuppressWarnings("unchecked")
public abstract class ApplicationDesignAdvisor implements ApplicationContextAware, Ordered, ApplicationDesignAnalyzer, BeanNameAware {

    private static final Log log = LogFactory.getLog(ApplicationDesignAdvisor.class);
    private String beanName;
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        reportIssues(analyze());
        final BeanDefinitionRegistry definitionRegistry = (BeanDefinitionRegistry) applicationContext.getAutowireCapableBeanFactory();
        definitionRegistry.removeBeanDefinition(beanName);
    }

    private void reportIssues(List<DesignIssue> issues) {
        with(issues).sort().each(new Processor<DesignIssue>() {
            @Override
            public void process(DesignIssue designIssue) {
                System.out.flush();
                System.err.println(designIssue.toString());
                System.err.flush();
            }
        });
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }

    @Override
    public List<DesignIssue> analyze() {
        final List<DesignIssue> issues = new CopyOnWriteArrayList<DesignIssue>();
        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        log.info("Starting design advisor on " + applicationContext.getDisplayName());
        final TaskManager taskManager = new ThreadPoolTaskManager(Runtime.getRuntime().availableProcessors(), true);
        final Thread analyzerThread = new Thread(taskManager);
        final SessionPreparator sessionPreparator = applicationContext.getBean(SessionPreparator.class);
        with(getAnalyzers(sessionPreparator)).each(new Processor<ApplicationDesignAnalyzer>() {
            @Override
            public void process(ApplicationDesignAnalyzer applicationDesignAnalyzer) {
                taskManager.schedule(new AnalyzerTask(applicationDesignAnalyzer, issues));
            }
        });
        try {
            analyzerThread.start();
            analyzerThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        stopWatch.stop();
        log.info("Finished analysis of " + applicationContext.getDisplayName() + " in " + stopWatch.getLastTaskTimeMillis() + "ms");
        return issues;
    }

    protected abstract Collection<ApplicationDesignAnalyzer> getAnalyzers(SessionPreparator sessionPreparator);

    @Override
    public void setBeanName(String name) {
        beanName = name;
    }

}
