package com.agileapes.dragonfly.statement.impl.model;

import com.agileapes.couteau.freemarker.model.UnresolvedMapModel;
import com.agileapes.dragonfly.dialect.DatabaseDialect;
import com.agileapes.dragonfly.error.DatabaseError;
import com.agileapes.dragonfly.metadata.TableMetadata;
import com.agileapes.dragonfly.statement.impl.model.functions.*;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/1, 1:29)
 */
public class FreemarkerStatementModel implements TemplateHashModel {

    private final Map<String, TemplateModel> items = new HashMap<String, TemplateModel>();
    private final BeansWrapper wrapper;

    public FreemarkerStatementModel(TableMetadata<?> tableMetadata, DatabaseDialect dialect) throws TemplateModelException {
        this(tableMetadata, dialect, new UnresolvedMapModel("value"), new UnresolvedMapModel("old"), new UnresolvedMapModel("new"));
    }

    public FreemarkerStatementModel(TableMetadata<?> tableMetadata, DatabaseDialect dialect, Object value) throws TemplateModelException {
        this(tableMetadata, dialect, value, new UnresolvedMapModel("old"), new UnresolvedMapModel("new"));
    }

    public FreemarkerStatementModel(TableMetadata<?> tableMetadata, DatabaseDialect dialect, Object value, Object oldValue, Object newValue) throws TemplateModelException {
        this.wrapper = BeansWrapper.getDefaultInstance();
        items.put("dialect", wrapper.wrap(dialect));
        items.put("table", wrapper.wrap(tableMetadata));
        items.put("value", wrapper.wrap(new UnresolvedMapModel("value")));
        items.put("old", wrapper.wrap(oldValue));
        items.put("new", wrapper.wrap(newValue));
        items.put("qualify", new DatabaseIdentifierQualifierMethod(dialect));
        items.put("notKey", new NonKeyColumnFilterMethod(tableMetadata));
        items.put("key", new KeyColumnFilterMethod(tableMetadata));
        items.put("escape", new EscapeMethod(dialect.getIdentifierEscapeCharacter()));
        items.put("isSet", new ValueColumnSelectorMethod(value instanceof TemplateModel ? null : value));
        items.put("quote", new QuoteMethod(dialect.getStringEscapeCharacter()));
        items.put("type", new TypeResolverMethod(dialect));
    }

    public void introduce(String name, Object value) {
        try {
            items.put(name, wrapper.wrap(value));
        } catch (TemplateModelException e) {
            throw new Error("Failed to introduce value: " + name);
        }
    }

    @Override
    public TemplateModel get(String key) throws TemplateModelException {
        return items.get(key);
    }

    @Override
    public boolean isEmpty() throws TemplateModelException {
        return items.isEmpty();
    }

}
