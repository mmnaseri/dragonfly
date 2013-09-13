package com.agileapes.dragonfly.sample;

import com.agileapes.dragonfly.data.DataAccess;
import com.agileapes.dragonfly.data.DataAccessObject;
import com.agileapes.dragonfly.sample.entities.Person;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/13, 1:30)
 */
public class Test {

    public static void main(String[] args) throws Exception {
        final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("/data/sample.xml");
        final DataAccess dataAccess = context.getBean(DataAccess.class);
        dataAccess.deleteAll(Person.class);
        Person person = dataAccess.getInstance(Person.class);
        person.setName("Milad");
        ((DataAccessObject) person).save();
        ((DataAccessObject) person).save();
        final List<Person> people = dataAccess.findAll(Person.class);
        for (Person someone : people) {
            System.out.println(someone.getName());
        }

    }

}
