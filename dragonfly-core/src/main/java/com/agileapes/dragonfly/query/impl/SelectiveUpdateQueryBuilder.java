package com.agileapes.dragonfly.query.impl;

import com.agileapes.couteau.freemarker.utils.FreemarkerUtils;
import com.agileapes.dragonfly.dialect.DatabaseDialect;
import freemarker.template.Configuration;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/3, 18:08)
 */
public class SelectiveUpdateQueryBuilder extends AbstractFreemarkerQueryBuilder {

    public SelectiveUpdateQueryBuilder(DatabaseDialect dialect) {
        super(FreemarkerUtils.getConfiguration(InsertQueryBuilder.class, "/"), "selectiveUpdate.sql.ftl", dialect);
    }

}
