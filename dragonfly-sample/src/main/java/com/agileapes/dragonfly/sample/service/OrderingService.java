package com.agileapes.dragonfly.sample.service;

import com.agileapes.dragonfly.data.DataAccess;
import com.agileapes.dragonfly.sample.entities.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/11/3, 17:03)
 */
@Service
public class OrderingService {

    @Autowired
    private DataAccess dataAccess;

    public void execute() {
        dataAccess.find(new Person(), "key DESC , name ASC  ");
    }
}
