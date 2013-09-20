package com.agileapes.dragonfly.sample.entities;

import javax.persistence.*;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/19, 0:08)
 */
@Entity
@Table(
        schema = "test",
        name = "library_card"
)
public class LibraryCard {

    private Person owner;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    public Person getOwner() {
        return owner;
    }

    public void setOwner(Person owner) {
        this.owner = owner;
    }

}
