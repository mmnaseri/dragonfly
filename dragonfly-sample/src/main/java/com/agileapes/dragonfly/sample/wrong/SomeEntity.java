package com.agileapes.dragonfly.sample.wrong;

import com.agileapes.dragonfly.annotations.Ignored;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (14/3/17 AD, 2:23)
 */
@Entity
@Table(
        name = "some_entity",
        schema = "test"
)
@Ignored
public class SomeEntity {

    private String id;

    @Column
    @Id
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
