/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 Milad Naseri.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.mmnaseri.dragonfly.sample.entities;

import com.mmnaseri.dragonfly.annotations.Order;
import com.mmnaseri.dragonfly.annotations.StoredProcedure;
import com.mmnaseri.dragonfly.runtime.ext.identity.api.Identified;

import javax.persistence.*;
import java.util.Collection;

/**
 * @author Milad Naseri (mmnaseri@programmer.net)
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
@Identified
public class Book {

    private String title;
    private Collection<Author> authors;
    private Collection<Author> editors;
    private Long key;

    @Column
    @Order
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @ManyToMany(mappedBy = "books", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @OrderBy("name")
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
