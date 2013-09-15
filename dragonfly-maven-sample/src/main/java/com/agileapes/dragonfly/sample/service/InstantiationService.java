package com.agileapes.dragonfly.sample.service;

import com.agileapes.dragonfly.entity.EntityContext;
import com.agileapes.dragonfly.sample.entities.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/15, 16:56)
 */
@Service
public class InstantiationService {

    @Autowired
    private EntityContext entityContext;

    public void execute() {
        long normalTime = System.nanoTime();
        for (int i = 0; i < 10000; i ++) {
            new Person();
        }
        normalTime = System.nanoTime() - normalTime;
        long contextTime = System.nanoTime();
        for (int i = 0; i < 10000; i ++) {
            entityContext.getInstance(Person.class);
        }
        contextTime = System.nanoTime() - contextTime;
        System.out.println("Normal time: " + normalTime);
        System.out.println("Context time: " + contextTime);
        System.out.println("[c/n] : " + (((double) contextTime) / normalTime));
    }

}
