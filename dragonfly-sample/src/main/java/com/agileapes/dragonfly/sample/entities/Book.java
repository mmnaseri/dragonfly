package com.agileapes.dragonfly.sample.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.Collection;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/10/2, 22:22)
 */
@Entity
@Table(
        name = "books",
        schema = "test"
)
public class Book {

    private String title;
    private Collection<Author> authors;
    private Collection<Author> editors;

    @Column
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @ManyToMany(mappedBy = "books")
    public Collection<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(Collection<Author> authors) {
        this.authors = authors;
    }

    @ManyToMany(mappedBy = "editedBooks")
    public Collection<Author> getEditors() {
        return editors;
    }

    public void setEditors(Collection<Author> editors) {
        this.editors = editors;
    }
}
