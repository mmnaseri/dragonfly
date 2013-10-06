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
