package com.agileapes.dragonfly.assets;

import com.agileapes.dragonfly.session.SessionPreparator;
import org.springframework.context.ApplicationContext;

import java.util.List;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (14/3/17 AD, 1:46)
 */
public interface ApplicationDesignAnalyzer {

    List<DesignIssue> analyze();

}
