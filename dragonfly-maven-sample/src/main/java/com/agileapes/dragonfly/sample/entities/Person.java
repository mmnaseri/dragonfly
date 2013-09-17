package com.agileapes.dragonfly.sample.entities;

import com.agileapes.dragonfly.annotations.ParameterMode;
import com.agileapes.dragonfly.annotations.StoredProcedure;
import com.agileapes.dragonfly.annotations.StoredProcedureParameter;
import com.agileapes.dragonfly.annotations.StoredProcedures;

import javax.persistence.*;
import java.util.Collection;
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
@NamedNativeQueries(@NamedNativeQuery(
        name = "countPeople",
        query = "SELECT COUNT(*) AS ${escape('cnt')} FROM ${qualify(table)};"
))
@StoredProcedures({
        @StoredProcedure(
                name = "countPeople",
                parameters = @StoredProcedureParameter(
                        mode = ParameterMode.OUT,
                        type = Long.class
                )
        ),
        @StoredProcedure(
                name = "findPeopleByName",
                resultType = Person.class,
                parameters = {
                        @StoredProcedureParameter(
                                mode = ParameterMode.IN,
                                type = String.class
                        ),
                        @StoredProcedureParameter(
                                mode = ParameterMode.OUT,
                                type = Long.class
                        )
                }
        )
})
public class Person {

    private String name;
    private Person father;
    private Person mother;
    private Date birthday;
    private Person friend;
    private Collection<Thing> things;

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

    @JoinColumn
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "owner")
    public Collection<Thing> getThings() {
        return things;
    }

    public void setThings(Collection<Thing> things) {
        this.things = things;
    }

}
