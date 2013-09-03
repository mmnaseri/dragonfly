package com.agileapes.dragonfly.query.impl;

import com.agileapes.couteau.freemarker.utils.FreemarkerUtils;
import com.agileapes.dragonfly.dialect.DatabaseDialect;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/3, 18:04)
 */
public class InsertQueryBuilder extends AbstractFreemarkerQueryBuilder {

    public InsertQueryBuilder(DatabaseDialect dialect) {
        super(FreemarkerUtils.getConfiguration(InsertQueryBuilder.class, "/"), "insert.sql.ftl", dialect);
    }

}
