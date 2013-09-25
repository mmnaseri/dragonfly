package com.agileapes.dragonfly.sample.service;

import com.agileapes.dragonfly.data.BatchOperation;
import com.agileapes.dragonfly.data.DataAccess;
import com.agileapes.dragonfly.data.impl.Reference;
import com.agileapes.dragonfly.sample.audit.Identifiable;
import com.agileapes.dragonfly.sample.entities.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/25, 0:21)
 */
@Service
public class BatchService {

    @Autowired
    private DataAccess dataAccess;

    public void execute() {
        final Reference<Person> reference = new Reference<Person>();
        Person person = new Person();
        person.setName("One Person");
        reference.setValue(person);
        reference.setValue(dataAccess.save(reference.getValue()));
        final List<Integer> batchResult = dataAccess.update(new BatchOperation() {
            @Override
            public void execute(DataAccess dataAccess) {
                reference.getValue().setName("Another Person");
                dataAccess.save(reference.getValue());
                dataAccess.delete(reference.getValue());
                dataAccess.delete(reference.getValue());
                dataAccess.delete(reference.getValue());
            }
        });
        for (int i = 0; i < batchResult.size(); i++) {
            System.out.println("batch[" + i + "] = " + batchResult.get(i));
        }
        System.out.println("Batch key: " + ((Identifiable) reference.getValue()).getUniqueKey());
    }

}
