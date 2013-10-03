package com.agileapes.dragonfly.sample.service;

import com.agileapes.dragonfly.data.DataAccess;
import com.agileapes.dragonfly.sample.entities.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/10/3, 11:08)
 */
@Service
public class CleanUpService {

    @Autowired
    private DataAccess dataAccess;

    public void execute() {
        dataAccess.call(Book.class, "refresh");
    }

}
