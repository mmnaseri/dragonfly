package com.agileapes.dragonfly.sample.entities;

import javax.persistence.*;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/5, 16:13)
 */
@Entity
@Table(
        schema = "test",
        name = "stuff"
)
public class Thing {

    private String name;
    private String description;
    private Person owner;

    @Column
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JoinColumn
    @ManyToOne
    public Person getOwner() {
        return owner;
    }

    public void setOwner(Person owner) {
        this.owner = owner;
    }

}
