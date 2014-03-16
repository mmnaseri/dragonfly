/*
 * Copyright (c) 2013 AgileApes, Ltd.
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall
 * be included in all copies or substantial portions of the
 * Software.
 */

package com.agileapes.dragonfly.sample;

import com.agileapes.dragonfly.sample.service.*;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.StopWatch;


/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/13, 1:30)
 */
public class Main {

    public static void main(String[] args) throws Exception {
        final StopWatch stopWatch = new StopWatch();
        stopWatch.start("startup");
        final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("/applicationContext.xml");
        stopWatch.stop();
        stopWatch.start("clean-up");
        context.getBean(CleanUpService.class).execute();
        stopWatch.stop();
        stopWatch.start("publishing");
        context.getBean(BookPublishingService.class).execute();
        stopWatch.stop();
        stopWatch.start("delete-people");
        context.getBean(DeletePeopleService.class).execute();
        stopWatch.stop();
        stopWatch.start("create-people");
        context.getBean(CreatePersonService.class).execute();
        stopWatch.stop();
        stopWatch.start("list-people");
        context.getBean(ListPeopleService.class).execute();
        stopWatch.stop();
        stopWatch.start("instantiation");
        context.getBean(InstantiationService.class).execute();
        stopWatch.stop();
        stopWatch.start("count");
        context.getBean(CountPeopleService.class).execute();
        stopWatch.stop();
        stopWatch.start("count-procedure");
        context.getBean(CountPeopleProcedureService.class).execute();
        stopWatch.stop();
        stopWatch.start("list-procedure");
        context.getBean(ListPeopleProcedureService.class).execute();
        stopWatch.stop();
        stopWatch.start("delegation");
        context.getBean(DelegationService.class).execute();
        stopWatch.stop();
        stopWatch.start("batch");
        context.getBean(BatchService.class).execute();
        stopWatch.stop();
        stopWatch.start("event-callback");
        context.getBean(EventCallbackService.class).execute();
        stopWatch.stop();
        stopWatch.start("ordering");
        context.getBean(OrderingService.class).execute();
        stopWatch.stop();
        System.out.println(stopWatch.prettyPrint());
    }

}
