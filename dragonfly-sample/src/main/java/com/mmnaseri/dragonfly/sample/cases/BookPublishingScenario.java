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

package com.mmnaseri.dragonfly.sample.cases;

import com.mmnaseri.dragonfly.data.DataAccess;
import com.mmnaseri.dragonfly.sample.assets.LogContainer;
import com.mmnaseri.dragonfly.sample.assets.LogEntry;
import com.mmnaseri.dragonfly.sample.entities.Author;
import com.mmnaseri.dragonfly.sample.entities.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (2013/10/2, 22:36)
 */
@Service
public class BookPublishingScenario extends BaseTestCase {

    @Autowired
    private DataAccess dataAccess;

    @Autowired
    private LogContainer logContainer;

    private Book getBook(String title) {
        final Book book = new Book();
        book.setTitle(title);
        return book;
    }

    private Author getAuthor(String name) {
        final Author author = new Author();
        author.setName(name);
        return author;
    }

    @Override
    public void run() {
        final Set<Thread> threads = new HashSet<Thread>();
        final int benchmarkSize = 50;
        for (int i = 0; i < benchmarkSize; i ++) {
            threads.add(new Thread(new Runnable() {
                @Override
                public void run() {
                    final Book bookA = dataAccess.save(getBook("Book A"));
                    final Book bookB = dataAccess.save(getBook("Book B"));
                    final Author authorA = dataAccess.save(getAuthor("Author A"));
                    final Author authorB = dataAccess.save(getAuthor("Author B"));
                    final Author authorC = dataAccess.save(getAuthor("Author C"));
                    bookA.setAuthors(Arrays.asList(authorA, authorB));
                    bookA.setEditors(Arrays.asList(authorB, authorC));
                    bookB.setAuthors(Arrays.asList(authorB, authorC));
                    bookB.setEditors(Arrays.asList(authorA, authorC));
                    authorA.setBooks(Arrays.asList(bookA));
                    authorA.setEditedBooks(Arrays.asList(bookB));
                    authorB.setBooks(Arrays.asList(bookA, bookB));
                    authorB.setEditedBooks(Arrays.asList(bookA));
                    authorC.setBooks(Arrays.asList(bookB));
                    authorC.setEditedBooks(Arrays.asList(bookA, bookB));
                    dataAccess.save(bookA);
                    dataAccess.save(bookB);
                    final List<Author> authors = dataAccess.find(authorA);
                    for (Author author : authors) {
                        System.out.println("Author: " + author.getName());
                        System.out.println("Written books:");
                        for (Book book : author.getBooks()) {
                            System.out.println(book.getTitle());
                        }
                        System.out.println("Edited books:");
                        for (Book book : author.getEditedBooks()) {
                            System.out.println(book.getTitle());
                        }
                    }
                    dataAccess.delete(authors.get(0));
                    for (LogEntry logEntry : logContainer.getEntries()) {
                        expect(logEntry).not().toBeNull();
                    }
                }
            }, "Publisher-" + i));
        }
        final Set<Thread> started = new HashSet<Thread>();
        final int operationSize = threads.size();
        while (!threads.isEmpty()) {
            final Thread thread = threads.iterator().next();
            threads.remove(thread);
            started.add(thread);
            thread.start();
            if (started.size() == operationSize || threads.isEmpty()) {
                for (Thread startedThread : started) {
                    try {
                        startedThread.join();
                    } catch (InterruptedException e) {
                        throw new Error(e);
                    }
                }
                started.clear();
            }
        }
        dataAccess.deleteAll(Author.class);
        dataAccess.deleteAll(Book.class);
    }

}
