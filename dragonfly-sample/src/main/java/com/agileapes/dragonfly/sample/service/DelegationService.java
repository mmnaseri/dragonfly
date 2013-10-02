package com.agileapes.dragonfly.sample.service;

import com.agileapes.dragonfly.data.DataAccess;
import com.agileapes.dragonfly.data.DataCallback;
import com.agileapes.dragonfly.data.DataOperation;
import com.agileapes.dragonfly.data.OperationType;
import com.agileapes.dragonfly.data.impl.DelegatingDataAccess;
import com.agileapes.dragonfly.data.impl.TypedDataCallback;
import com.agileapes.dragonfly.data.impl.op.IdentifiableDataOperation;
import com.agileapes.dragonfly.data.impl.op.SampledDataOperation;
import com.agileapes.dragonfly.data.impl.op.TypedDataOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/23, 12:55)
 */
@Service
public class DelegationService {

    public class Memorable {

        private Long id;
        private String name;

        public Memorable(Long id, String name) {
            setId(id);
            setName(name);
        }

        public Memorable(String name) {
            this(null, name);
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    private static interface LogEntry {

        long getTime();

        DataOperation getOperation();

    }

    @Autowired
    private DataAccess dataAccess;

    public void execute() {
        final ThreadLocal<List<LogEntry>> threadLocalLog = new ThreadLocal<List<LogEntry>>() {
            @Override
            protected List<LogEntry> initialValue() {
                return new ArrayList<LogEntry>();
            }
        };
        final Map<Long, Memorable> memory = new HashMap<Long, Memorable>();
        final DelegatingDataAccess dataAccess = new DelegatingDataAccess(this.dataAccess);
        dataAccess.addCallback(new DataCallback<DataOperation>() {
            @Override
            public Object execute(final DataOperation operation) {
                final StopWatch stopWatch = new StopWatch();
                stopWatch.start();
                final Object proceed = operation.proceed();
                stopWatch.stop();
                threadLocalLog.get().add(new LogEntry() {

                    @Override
                    public long getTime() {
                        return stopWatch.getTotalTimeMillis();
                    }

                    @Override
                    public DataOperation getOperation() {
                        return operation;
                    }

                    @Override
                    public String toString() {
                        return String.format("[%5dms] %s", getTime(), getOperation());
                    }

                });
                return proceed;
            }

            @Override
            public boolean accepts(DataOperation dataOperation) {
                return true;
            }
        });
        dataAccess.addCallback(new TypedDataCallback<DataOperation>(Memorable.class) {
            @Override
            public Object execute(DataOperation operation) {
                if (OperationType.SAVE.equals(operation.getOperationType()) && operation instanceof SampledDataOperation) {
                    SampledDataOperation dataOperation = (SampledDataOperation) operation;
                    final Object sample = dataOperation.getSample();
                    final Memorable memorable = (Memorable) sample;
                    if (memorable.getId() != null) {
                        memory.put(memorable.getId(), memorable);
                    } else {
                        memorable.setId((long) memory.size());
                        memory.put(memorable.getId(), memorable);
                    }
                    return memorable;
                } else if (OperationType.DELETE.equals(operation.getOperationType()) && operation instanceof SampledDataOperation) {
                    SampledDataOperation dataOperation = (SampledDataOperation) operation;
                    Memorable memorable = (Memorable) dataOperation.getSample();
                    if (memorable.getId() != null) {
                        memory.remove(memorable.getId());
                    } else {
                        Long key = null;
                        for (Map.Entry<Long, Memorable> entry : memory.entrySet()) {
                            if (entry.getValue().getName().equals(memorable.getName())) {
                                key = entry.getKey();
                            }
                        }
                        if (key != null) {
                            memory.remove(key);
                        }
                    }
                    return null;
                } else if (OperationType.FIND.equals(operation.getOperationType()) && operation instanceof IdentifiableDataOperation) {
                    IdentifiableDataOperation dataOperation = (IdentifiableDataOperation) operation;
                    //noinspection SuspiciousMethodCalls
                    final Memorable memorable = memory.get(dataOperation.getKey());
                    return new Memorable(memorable.getId(), memorable.getName());
                } else if (OperationType.COUNT.equals(operation.getOperationType()) && operation instanceof TypedDataOperation) {
                    return (long) memory.size();
                } else {
                    return operation.proceed();
                }
            }

        });
        System.out.println("first        : " + dataAccess.save(new Memorable("First")).getId());
        System.out.println("second       : " + dataAccess.save(new Memorable("Second")).getId());
        System.out.println("id (1)       : " + dataAccess.find(Memorable.class, 0L).getName());
        dataAccess.save(new Memorable(0L, "The First"));
        System.out.println("id (1)       : " + dataAccess.find(Memorable.class, 0L).getName());
        System.out.println("id (2)       : " + dataAccess.find(Memorable.class, 1L).getName());
        System.out.println("count all    : " + dataAccess.countAll(Memorable.class));
        System.out.println("delete (1)");
        dataAccess.delete(new Memorable("Second"));
        System.out.println("count all    : " + dataAccess.countAll(Memorable.class));
        final List<LogEntry> logEntries = threadLocalLog.get();
        for (LogEntry logEntry : logEntries) {
            System.out.println(logEntry);
        }

    }

}
