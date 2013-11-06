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

package com.agileapes.dragonfly.tasks;

import com.agileapes.dragonfly.mojo.PluginExecutor;
import org.apache.maven.plugin.MojoFailureException;
import org.springframework.stereotype.Component;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/17, 15:42)
 */
@Component
public class GenerateEntitiesTask extends AbstractCodeGenerationTask {

    @Override
    protected String getIntro() {
        return "Generating enhanced entities ...";
    }

    @Override
    public void execute(PluginExecutor executor) throws MojoFailureException {
    }

}
