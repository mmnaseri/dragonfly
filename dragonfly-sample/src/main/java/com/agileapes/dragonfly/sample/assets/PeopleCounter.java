/*
 * Copyright (c) 2013 AgileApes, Ltd.
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall
 * be included in all copies or substantial portions of the
 * Software.
 */

package com.agileapes.dragonfly.sample.assets;

import com.agileapes.dragonfly.annotations.MappedColumn;
import com.agileapes.dragonfly.annotations.Partial;
import com.agileapes.dragonfly.sample.entities.Person;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/7, 13:44)
 */
@Partial(targetEntity = Person.class, query = "countPeople")
public class PeopleCounter {

    private Long count;

    @MappedColumn(column = "cnt")
    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

}
