package com.agileapes.dragonfly.analysis;

import com.agileapes.couteau.basics.api.Processor;
import com.agileapes.couteau.concurrency.manager.TaskManager;
import com.agileapes.couteau.concurrency.manager.impl.ThreadPoolTaskManager;
import com.agileapes.dragonfly.analysis.analyzers.*;
import com.agileapes.dragonfly.analysis.impl.AnalyzerTask;
import com.agileapes.dragonfly.entity.EntityDefinitionContext;
import com.agileapes.dragonfly.ext.ExtensionManager;
import com.agileapes.dragonfly.metadata.TableMetadataRegistry;
import com.agileapes.dragonfly.session.SessionPreparator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.util.StopWatch;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (14/3/17 AD, 0:40)
 */
@SuppressWarnings("unchecked")
public final class ApplicationDesignAdvisor implements ApplicationContextAware, Ordered, ApplicationDesignAnalyzer, BeanNameAware {

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
        final String[] basePackages = sessionPreparator.getBasePackages();
        final EntityDefinitionContext definitionContext = sessionPreparator.getDefinitionContext();
        final ExtensionManager extensionManager = sessionPreparator.getExtensionManager();
        final TableMetadataRegistry metadataRegistry = sessionPreparator.getTableMetadataRegistry();
        with(
                new ExtensionFilterApplicabilityAnalyzer(extensionManager, definitionContext),
                new ExtensionPropertyAccessibilityAnalyzer(extensionManager),
                new ScanPackageDefinitionEfficiencyAnalyzer(basePackages, definitionContext),
                new IdentityCollisionAnalyzer(extensionManager, definitionContext),
                new VersionEntityWithoutKeyAnalyzer(metadataRegistry),
                new PrimitiveColumnTypeAnalyzer(metadataRegistry)
        ).each(new Processor<ApplicationDesignAnalyzer>() {
            @Override
            public void process(ApplicationDesignAnalyzer applicationDesignAnalyzer) {
                taskManager.schedule(new AnalyzerTask(applicationDesignAnalyzer, issues));
            }
        });
        try {
            analyzerThread.start();
            analyzerThread.join();
        } catch (InterruptedException e) {
            throw new Error(e);
        }
        stopWatch.stop();
        log.info("Finished analysis of " + applicationContext.getDisplayName() + " in " + stopWatch.getLastTaskTimeMillis() + "ms");
        return issues;
    }

    @Override
    public void setBeanName(String name) {
        beanName = name;
    }

}
