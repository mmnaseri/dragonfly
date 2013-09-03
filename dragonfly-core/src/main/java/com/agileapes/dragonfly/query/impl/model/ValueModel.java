package com.agileapes.dragonfly.query.impl.model;

import com.agileapes.dragonfly.query.impl.functions.QuoteMethod;
import freemarker.ext.beans.BeanModel;
import freemarker.template.*;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/3, 13:57)
 */
@Deprecated
public class ValueModel implements TemplateHashModel {

    private final TemplateModel model;
    private final QuoteMethod quote;

    public ValueModel(TemplateModel model, Character escape) {
        this.model = model;
        this.quote = new QuoteMethod(escape);
    }

    @Override
    public TemplateModel get(String key) throws TemplateModelException {
        TemplateModel value;
        if (model instanceof BeanModel) {
            BeanModel beanModel = (BeanModel) model;
            value = beanModel.get(key);
            if (value instanceof TemplateDateModel) {
                final Date date = ((TemplateDateModel) value).getAsDate();
                final String pattern;
                if (date instanceof Time) {
                    pattern = "HH:mm:ss";
                } else if (date instanceof Timestamp) {
                    pattern = "yyyy-MM-dd HH:mm:ss";
                } else {
                    pattern = "yyyy-MM-dd";
                }
                value = new SimpleScalar(new SimpleDateFormat(pattern).format(date));
            }
            if (value instanceof TemplateScalarModel) {
                value = new SimpleScalar((String) quote.quote(((TemplateScalarModel) value).getAsString()));
            }
        } else if (model instanceof TemplateHashModel) {
            value = ((TemplateHashModel) model).get(key);
        } else {
            value = null;
        }
        return value;
    }

    @Override
    public boolean isEmpty() throws TemplateModelException {
        return !(model instanceof TemplateHashModel) || ((TemplateHashModel) model).isEmpty();
    }
}
