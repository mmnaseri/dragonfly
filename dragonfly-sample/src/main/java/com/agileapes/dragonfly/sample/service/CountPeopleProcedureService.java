package com.agileapes.dragonfly.sample.service;

import com.agileapes.dragonfly.data.DataAccess;
import com.agileapes.dragonfly.data.impl.Reference;
import com.agileapes.dragonfly.sample.entities.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/22, 23:51)
 */
@Service
public class CountPeopleProcedureService {

    @Autowired
    private DataAccess dataAccess;

    public void execute() {
        final Reference<Long> count = new Reference<Long>();
        dataAccess.call(Person.class, "countPeople", count);
        System.out.println("count.getValue() = " + count.getValue());
    }

}
