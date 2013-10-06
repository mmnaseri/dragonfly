package com.agileapes.dragonfly.sample.entities;

import javax.persistence.*;
import java.util.Collection;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/24, 10:20)
 */
@Entity
@Table(
        schema = "test",
        name = "groups"
)
public class Group {

    private String name;
    private Collection<Person> people;

    @Column
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @OneToMany(cascade = CascadeType.ALL)
    public Collection<Person> getPeople() {
        return people;
    }

    public void setPeople(Collection<Person> people) {
        this.people = people;
    }
}
