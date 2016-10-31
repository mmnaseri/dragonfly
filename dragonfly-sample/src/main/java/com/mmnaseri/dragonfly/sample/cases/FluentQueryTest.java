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

import com.mmnaseri.couteau.basics.api.Processor;
import com.mmnaseri.dragonfly.annotations.Ordering;
import com.mmnaseri.dragonfly.data.FluentDataAccess;
import com.mmnaseri.dragonfly.fluent.SelectQueryExecution;
import com.mmnaseri.dragonfly.fluent.generation.FunctionInvocation;
import com.mmnaseri.dragonfly.fluent.generation.impl.ImmutableFunction;
import com.mmnaseri.dragonfly.fluent.generation.impl.ImmutableFunctionInvocation;
import com.mmnaseri.dragonfly.sample.entities.Group;
import com.mmnaseri.dragonfly.sample.entities.LibraryCard;
import com.mmnaseri.dragonfly.sample.entities.Person;
import com.mmnaseri.dragonfly.fluent.tools.Functions;
import com.mmnaseri.dragonfly.fluent.tools.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static com.mmnaseri.couteau.basics.collections.CollectionWrapper.with;

/**
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (14/8/7 AD, 13:46)
 */
@Service
public class FluentQueryTest extends BaseTestCase {

    @Autowired
    private FluentDataAccess dataAccess;

    private static final long ONE_SECOND = 1000;
    private static final long ONE_MINUTE = 60 * ONE_SECOND;
    private static final long ONE_HOUR = 60 * ONE_MINUTE;
    private static final long ONE_DAY = 24 * ONE_HOUR;
    private static final long ONE_MONTH = 30 * ONE_DAY;

    private void testPagination() {
        expect(dataAccess.from(new Person()).limit(10, 1).select()).toHaveSize(10);
    }

    private void testCustomFunction() {
        final Person person = new Person();
        final SelectQueryExecution<Person, ImmutableFunctionInvocation<Long>> selection = dataAccess.from(person).selection(new ImmutableFunctionInvocation<Long>(new ImmutableFunction<Long>(Long.class, "MY_FUNC", 1), person.getGroup(), person.getBirthday()));
        final String sql = selection.getSql();
        final String normalized = sql.replaceAll("t\\d+", "t").replaceAll("MY_FUNC\\d", "MY_FUNC_x").replaceAll("`", "").toLowerCase();
        expect(normalized).toEqual("select my_func(t.group, t.birthday) as my_func_x from test.people as t");
    }

    private void selectFunction() {
        final Person person = new Person();
        final List<? extends FunctionInvocation<Long>> list = dataAccess.from(person).select(Functions.count(person));
        expect(list).toHaveSize(1);
        expect(list.get(0).getResult()).toEqual(100L);
    }

    private void testSelectAlias() {
        final Person person = new Person();
        final List<? extends Person> people = dataAccess.from(person).select(person);
        expect(people).toHaveSize(100);
        for (Person found : people) {
            expect(found.getBirthday()).not().toBeNull();
            expect(found.getKey()).not().toBeNull();
            expect(found.getName()).not().toBeNull();
        }
    }

    private void testSelectSingleProperty() {
        final Person person = new Person();
        final List<? extends Date> dates = dataAccess.from(person).orderBy(person.getBirthday()).select(person.getBirthday());
        Date max = new Date(0);
        expect(dates).toHaveSize(100);
        for (Date date : dates) {
            expect(date).not().toBeNull();
            expect(date.getTime() > max.getTime()).toBeTrue();
            max = date;
        }
    }

    private void testFetchingLists() {
        final Person person = new Person();
        final List<? extends List<Object>> list = dataAccess.from(person).select(Query.projection(Functions.count(person)));
        expect(list).toHaveSize(1);
        final List<Object> objects = list.get(0);
        expect(objects).toHaveSize(1);
        expect(objects.get(0)).toBe(100L);
    }

    private void testUnionAll() {
        final Person person = new Person();
        final Person anotherPerson = new Person();
        final List<? extends Person> people = dataAccess
                .from(person)
                .unionAll(dataAccess.from(anotherPerson).selection())
                .select();
        expect(people).toHaveSize(200);
    }

    private void testInnerSelect() {
        final Person person = new Person();
        final Person otherPerson = new Person();
        final LibraryCard libraryCard = new LibraryCard();
        final Group group = new Group();
        final List<? extends Person> people = dataAccess.from(person)
                .innerJoin(libraryCard).when(libraryCard.getOwner()).isEqualTo(person)
                .innerJoin(group).when(person.getGroup()).isEqualTo(group)
                .where(person.getKey()).isIn(
                        dataAccess.from(otherPerson)
                                .where(otherPerson.getBirthday()).isGreaterThan(new Date(new Date().getTime() + 10 * ONE_MONTH))
                                .distinctSelection(new Person() {{
                                    setKey(otherPerson.getKey());
                                }}))
                .and(person.getName()).isLike("Person 1%")
                .select();
        expect(people).toHaveSize(9);
        for (Person found : people) {
            expect(found).not().toBeNull();
            expect(found.getBirthday()).not().toBeNull();
            expect(found.getKey()).not().toBeNull();
            expect(found.getGroup()).not().toBeNull();
            expect(found.getLibraryCard()).not().toBeNull();
            expect(found.getName()).not().toBeNull();
            expect(found.getGroup().getName()).not().toBeNull();
            expect(found.getLibraryCard().getCardNumber()).not().toBeNull();
        }
    }

    private void testAggregateFunctions() {
        final Person person = new Person();
        final List<? extends HashMap<String, Object>> list = dataAccess.from(person).selectDistinct(new HashMap<String, Object>() {{
            put("count", Functions.count(person.getKey()));
            put("average", Functions.average(person.getKey()));
            put("min", Functions.min(person.getKey()));
            put("max", Functions.max(person.getKey()));
            put("round", Functions.round(person.getKey()));
            put("length", Functions.length(person.getName()));
            put("sum", Functions.sum(person.getKey()));
        }});
        expect(list).toHaveSize(1);
        final HashMap<String, Object> metadata = list.get(0);
        with("count", "average", "min", "max", "round", "length", "sum").each(new Processor<String>() {
            @Override
            public void process(String input) {
                expect(metadata.keySet()).toContain(input);
            }
        });
    }

    private void testComplexSelection() {
        final Person person = new Person();
        final LibraryCard libraryCard = new LibraryCard();
        final Group group = new Group();
        final List<? extends Person> people = dataAccess.from(person)
                .innerJoin(libraryCard).when(libraryCard.getOwner()).isEqualTo(person)
                .innerJoin(group).when(person.getGroup()).isEqualTo(group)
                .where(person.getName()).isLike("Person 1%")
                .or(person.getName()).isLike("Person 2%")
                .orderBy(person.getName(), Ordering.DESCENDING)
                .select();
        expect(people).toHaveSize(22);
        for (Person found : people) {
            expect(found).not().toBeNull();
            expect(found.getBirthday()).not().toBeNull();
            expect(found.getKey()).not().toBeNull();
            expect(found.getGroup()).not().toBeNull();
            expect(found.getLibraryCard()).not().toBeNull();
            expect(found.getName()).not().toBeNull();
            expect(found.getGroup().getName()).not().toBeNull();
            expect(found.getLibraryCard().getCardNumber()).not().toBeNull();
        }
    }

    private void prepareEntities() {
        for (int i = 0; i < 100; i++) {
            dataAccess.save(getPerson(i));
        }
    }

    private Person getPerson(int index) {
        final Group group = new Group();
        group.setName("My Group " + index);
        final Person person = new Person();
        person.setGroup(group);
        person.setName("Person " + index);
        person.setBirthday(new Date(System.currentTimeMillis() + (index * ONE_MONTH)));
        final LibraryCard libraryCard = new LibraryCard();
        libraryCard.setOwner(person);
        libraryCard.setCardNumber(UUID.randomUUID().toString());
        person.setLibraryCard(libraryCard);
        return person;
    }

    @Override
    public void run() {
        prepareEntities();
        testComplexSelection();
        testAggregateFunctions();
        testInnerSelect();
        testUnionAll();
        testFetchingLists();
        testSelectSingleProperty();
        testSelectAlias();
        selectFunction();
        testCustomFunction();
        testPagination();
        cleanUp();
    }

    private void cleanUp() {
        dataAccess.deleteAll(LibraryCard.class);
        dataAccess.deleteAll(Person.class);
        dataAccess.deleteAll(Group.class);
    }

}
