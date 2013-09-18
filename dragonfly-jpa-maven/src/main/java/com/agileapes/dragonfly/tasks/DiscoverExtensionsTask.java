package com.agileapes.dragonfly.tasks;

import com.agileapes.couteau.basics.api.Processor;
import com.agileapes.couteau.maven.resource.ProjectClassResourceTransformer;
import com.agileapes.couteau.maven.resource.ProjectResource;
import com.agileapes.couteau.maven.resource.ProjectResourceType;
import com.agileapes.couteau.maven.resource.ProjectResourceTypeFilter;
import com.agileapes.couteau.maven.task.PluginTask;
import com.agileapes.couteau.reflection.util.assets.AnnotatedElementFilter;
import com.agileapes.dragonfly.annotations.Extension;
import com.agileapes.dragonfly.entity.EntityDefinitionContext;
import com.agileapes.dragonfly.ext.ExtensionManager;
import com.agileapes.dragonfly.mojo.PluginExecutor;
import org.apache.maven.plugin.MojoFailureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/17, 14:47)
 */
@Component("findExtensions")
public class DiscoverExtensionsTask extends PluginTask<PluginExecutor> {

    @Autowired
    private ExtensionManager extensionManager;

    @Override
    protected String getIntro() {
        return "Discovering extensions ...";
    }

    @Override
    public void execute(PluginExecutor executor) throws MojoFailureException {
        final Collection<ProjectResource> projectResources = executor.getProjectResources();
        //noinspection unchecked
        with(projectResources)
        .keep(new ProjectResourceTypeFilter(ProjectResourceType.CLASS))
        .transform(new ProjectClassResourceTransformer())
        .keep(new AnnotatedElementFilter(Extension.class))
        .each(new Processor<Class<?>>() {
            @Override
            public void process(Class<?> input) {
                extensionManager.addExtension(input);
            }
        });
    }

}
