package com.agileapes.dragonfly.sample.entities;

import com.agileapes.dragonfly.annotations.StoredProcedure;

import javax.persistence.*;
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
@StoredProcedure(
        name = "refresh"
)
public class Book {

    private String title;
    private Collection<Author> authors;
    private Collection<Author> editors;
    private Long key;

    @Column
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @ManyToMany(mappedBy = "books", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    public Collection<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(Collection<Author> authors) {
        this.authors = authors;
    }

    @ManyToMany(mappedBy = "editedBooks", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    public Collection<Author> getEditors() {
        return editors;
    }

    public void setEditors(Collection<Author> editors) {
        this.editors = editors;
    }

    @Column
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "book")
    public Long getKey() {
        return key;
    }

    public void setKey(Long key) {
        this.key = key;
    }

}
