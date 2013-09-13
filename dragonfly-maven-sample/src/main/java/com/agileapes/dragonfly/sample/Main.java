package com.agileapes.dragonfly.sample;

import com.agileapes.dragonfly.sample.service.CreatePersonService;
import com.agileapes.dragonfly.sample.service.DeletePeopleService;
import com.agileapes.dragonfly.sample.service.ListPeopleService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/13, 1:30)
 */
public class Main {

    public static void main(String[] args) throws Exception {
        final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("/data/sample.xml");
        context.getBean(DeletePeopleService.class).execute();
        context.getBean(CreatePersonService.class).execute();
        context.getBean(ListPeopleService.class).execute();
    }

}
