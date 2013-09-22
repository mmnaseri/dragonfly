package com.agileapes.dragonfly.sample;

import com.agileapes.dragonfly.sample.service.*;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/13, 1:30)
 */
public class Main {

    public static void main(String[] args) throws Exception {
        final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("/sample.xml");
        context.getBean(DeletePeopleService.class).execute();
        context.getBean(CreatePersonService.class).execute();
        context.getBean(ListPeopleService.class).execute();
        context.getBean(InstantiationService.class).execute();
        context.getBean(CountPeopleService.class).execute();
        context.getBean(CountPeopleProcedureService.class).execute();
        context.getBean(ListPeopleProcedureService.class).execute();
    }

}
