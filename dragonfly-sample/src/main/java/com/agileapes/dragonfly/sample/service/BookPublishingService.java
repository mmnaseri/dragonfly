package com.agileapes.dragonfly.sample.service;

import com.agileapes.couteau.basics.api.Filter;
import com.agileapes.dragonfly.data.DataAccess;
import com.agileapes.dragonfly.sample.assets.LogContainer;
import com.agileapes.dragonfly.sample.assets.LogEntry;
import com.agileapes.dragonfly.sample.entities.Author;
import com.agileapes.dragonfly.sample.entities.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/10/2, 22:36)
 */
@Service
public class BookPublishingService {

    @Autowired
    private DataAccess dataAccess;

    @Autowired
    private LogContainer logContainer;

    public void execute() {
        final Set<Thread> threads = new HashSet<Thread>();
        for (int i = 0; i < 100; i ++) {
            threads.add(new Thread(new Runnable() {
                @Override
                public void run() {
                    final Book bookA = getBook("Book A");
                    final Book bookB = getBook("Book B");
                    Author authorA = getAuthor("Author A");
                    Author authorB = getAuthor("Author B");
                    Author authorC = getAuthor("Author C");
                    bookA.setAuthors(Arrays.asList(authorA, authorB));
                    bookA.setEditors(Arrays.asList(authorB, authorC));
                    dataAccess.save(bookA);
                    authorA = with(bookA.getAuthors()).find(new Filter<Author>() {
                        @Override
                        public boolean accepts(Author item) {
                            return item.getName().equals("Author A");
                        }
                    });
                    authorB = with(bookA.getAuthors()).find(new Filter<Author>() {
                        @Override
                        public boolean accepts(Author item) {
                            return item.getName().equals("Author B");
                        }
                    });
                    authorC = with(bookA.getEditors()).find(new Filter<Author>() {
                        @Override
                        public boolean accepts(Author item) {
                            return item.getName().equals("Author C");
                        }
                    });
                    bookB.setAuthors(Arrays.asList(authorB, authorC));
                    bookB.setEditors(Arrays.asList(authorA, authorC));
                    dataAccess.save(bookB);
                    dataAccess.save(getBook("Book C"));
                    final List<Author> authors = dataAccess.find(authorB);
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
//                    dataAccess.delete(authors.get(0));
                    for (LogEntry logEntry : logContainer.getEntries()) {
                        System.out.println(logEntry);
                    }
                }
            }));
        }
        final Set<Thread> started = new HashSet<Thread>();
        int count = 0;
        while (!threads.isEmpty()) {
            count ++;
            final Thread thread = threads.iterator().next();
            threads.remove(thread);
            started.add(thread);
            thread.start();
            if (count % 10 == 0) {
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
    }

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

}
