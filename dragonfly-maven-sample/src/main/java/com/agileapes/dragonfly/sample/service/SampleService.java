package com.agileapes.dragonfly.sample.service;

import com.agileapes.dragonfly.data.DataAccess;
import com.agileapes.dragonfly.entity.EntityContext;
import com.agileapes.dragonfly.sample.audit.Identifiable;
import com.agileapes.dragonfly.sample.entities.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/21, 15:12)
 */
@Service
public class SampleService {

    @Autowired
    private DataAccess dataAccess;

    @Autowired
    private EntityContext entityContext;

    public void execute() {
        final Person person = new Person();
        person.setName("My Name");
        dataAccess.save(person);
        final Person instance = entityContext.getInstance(Person.class);
        instance.setName("Another name");
        dataAccess.save(instance);
        System.out.println("id: " + ((Identifiable) instance).getUniqueKey());
        dataAccess.save(instance);
        System.out.println("id: " + ((Identifiable) instance).getUniqueKey());
    }

}
