package com.agileapes.dragonfly.analysis.impl;

import com.agileapes.couteau.concurrency.error.TaskFailureException;
import com.agileapes.couteau.concurrency.task.Task;
import com.agileapes.dragonfly.analysis.ApplicationDesignAnalyzer;
import com.agileapes.dragonfly.analysis.DesignIssue;
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

    public AnalyzerTask(ApplicationDesignAnalyzer analyzer, Collection<DesignIssue> issues) {
        this.analyzer = analyzer;
        this.issues = issues;
    }

    @Override
    public void perform() throws TaskFailureException {
        issues.addAll(analyzer.analyze());
    }
    
}
