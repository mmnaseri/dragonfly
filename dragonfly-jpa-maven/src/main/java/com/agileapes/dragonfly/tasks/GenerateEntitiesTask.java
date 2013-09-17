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
