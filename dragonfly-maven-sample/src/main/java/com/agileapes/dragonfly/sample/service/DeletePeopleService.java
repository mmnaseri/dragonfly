package com.agileapes.dragonfly.sample.service;

import com.agileapes.dragonfly.data.DataAccess;
import com.agileapes.dragonfly.sample.entities.Person;
import com.agileapes.dragonfly.sample.entities.Thing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/14, 0:58)
 */
@Service
public class DeletePeopleService {

    @Autowired
    private DataAccess dataAccess;

    public void execute() {
        dataAccess.deleteAll(Thing.class);
        dataAccess.deleteAll(Person.class);
    }

}
