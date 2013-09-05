package com.agileapes.dragonfly.metadata.impl.sample;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/5, 12:59)
 */
@Entity
@Table(
        name = "people",
        schema = "test",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"name", "birthday"}
        )
)
public class Person {

    private int id;
    private String name;
    private Person father;
    private Person mother;
    private Date birthday;
    private Person friend;

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JoinColumn
    @OneToOne
    public Person getFather() {
        return father;
    }

    public void setFather(Person father) {
        this.father = father;
    }

    @JoinColumn
    @OneToOne
    public Person getMother() {
        return mother;
    }

    public void setMother(Person mother) {
        this.mother = mother;
    }

    @Column
    @Temporal(TemporalType.DATE)
    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    @JoinColumn
    @OneToOne
    public Person getFriend() {
        return friend;
    }

    public void setFriend(Person friend) {
        this.friend = friend;
    }
}
