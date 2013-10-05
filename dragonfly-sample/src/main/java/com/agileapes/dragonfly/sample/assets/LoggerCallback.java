package com.agileapes.dragonfly.sample.assets;

import com.agileapes.couteau.context.contract.OrderedBean;
import com.agileapes.dragonfly.data.DataCallback;
import com.agileapes.dragonfly.data.DataOperation;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/10/5, 10:53)
 */
@Service
public class LoggerCallback implements DataCallback<DataOperation>, OrderedBean {

    @Resource
    private LogContainer logContainer;

    @Override
    public Object execute(DataOperation operation) {
        final DefaultLogEntry entry = new DefaultLogEntry(operation);
        final Object result = operation.proceed();
        entry.stop();
        logContainer.log(entry);
        return result;
    }

    @Override
    public boolean accepts(DataOperation item) {
        return true;
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }

}
