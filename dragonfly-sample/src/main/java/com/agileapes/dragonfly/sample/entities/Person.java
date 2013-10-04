package com.agileapes.dragonfly.sample.entities;

import com.agileapes.dragonfly.annotations.ParameterMode;
import com.agileapes.dragonfly.annotations.StoredProcedure;
import com.agileapes.dragonfly.annotations.StoredProcedureParameter;
import com.agileapes.dragonfly.annotations.StoredProcedures;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;
import java.util.List;

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
    private Date birthday;
    private Collection<Thing> things;
    private LibraryCard libraryCard;
    private Group group;
    private Long key;

    @Column
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column
    @Temporal(TemporalType.DATE)
    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "owner", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    public Collection<Thing> getThings() {
        return things;
    }

    public void setThings(List<Thing> things) {
        this.things = things;
    }

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    public LibraryCard getLibraryCard() {
        return libraryCard;
    }

    public void setLibraryCard(LibraryCard libraryCard) {
        this.libraryCard = libraryCard;
    }

    @JoinColumn
    @ManyToOne(cascade = CascadeType.ALL)
    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    @Column
    @GeneratedValue(strategy = GenerationType.TABLE)
    public Long getKey() {
        return key;
    }

    public void setKey(Long key) {
        this.key = key;
    }
}
