package com.agileapes.dragonfly.assets.analysis;

import com.agileapes.couteau.concurrency.error.TaskFailureException;
import com.agileapes.couteau.concurrency.task.Task;
import com.agileapes.dragonfly.assets.ApplicationDesignAnalyzer;
import com.agileapes.dragonfly.assets.DesignIssue;
import com.agileapes.dragonfly.session.SessionPreparator;
import org.springframework.context.ApplicationContext;

import java.util.Collection;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (14/3/17 AD, 1:56)
 */
public class AnalyzerTask implements Task {
    
    private final ApplicationDesignAnalyzer analyzer;
    private final Collection<DesignIssue> issues;
    private final ApplicationContext applicationContext;
    private final SessionPreparator sessionPreparator;

    public AnalyzerTask(ApplicationDesignAnalyzer analyzer, Collection<DesignIssue> issues, ApplicationContext applicationContext, SessionPreparator sessionPreparator) {
        this.analyzer = analyzer;
        this.issues = issues;
        this.applicationContext = applicationContext;
        this.sessionPreparator = sessionPreparator;
    }

    @Override
    public void perform() throws TaskFailureException {
        System.err.println("Performing " + analyzer);
        issues.addAll(analyzer.analyze());
        System.err.println("Done with  " + analyzer);
    }
    
}
