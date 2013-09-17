package com.agileapes.dragonfly.tasks;

import com.agileapes.couteau.basics.api.Processor;
import com.agileapes.couteau.basics.api.Transformer;
import com.agileapes.couteau.maven.resource.ProjectClassResourceTransformer;
import com.agileapes.couteau.maven.resource.ProjectResourceType;
import com.agileapes.couteau.maven.resource.ProjectResourceTypeFilter;
import com.agileapes.couteau.maven.task.PluginTask;
import com.agileapes.couteau.reflection.util.assets.AnnotatedElementFilter;
import com.agileapes.dragonfly.entity.EntityDefinition;
import com.agileapes.dragonfly.entity.EntityDefinitionContext;
import com.agileapes.dragonfly.entity.impl.EntityProxy;
import com.agileapes.dragonfly.entity.impl.ImmutableEntityDefinition;
import com.agileapes.dragonfly.mojo.PluginExecutor;
import org.apache.maven.plugin.MojoFailureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/17, 15:13)
 */
@Component("defineEntities")
public class EntityDefinitionCollectorTask extends PluginTask<PluginExecutor> {

    @Autowired
    private EntityDefinitionContext definitionContext;

    @Override
    protected String getIntro() {
        return "Collecting metadata about entity definitions ...";
    }

    @Override
    public void execute(PluginExecutor executor) throws MojoFailureException {
        //noinspection unchecked
        with(executor.getProjectResources())
        .keep(new ProjectResourceTypeFilter(ProjectResourceType.CLASS))
        .transform(new ProjectClassResourceTransformer())
        .keep(new AnnotatedElementFilter(Entity.class))
        .transform(new Transformer<Class<?>, EntityDefinition<?>>() {
            @Override
            public EntityDefinition<?> map(Class<?> input) {
                final List<Class<?>> interfaces = new ArrayList<Class<?>>();
                Collections.addAll(interfaces, EntityProxy.class.getInterfaces());
                Collections.addAll(interfaces, input.getInterfaces());
                //noinspection unchecked
                return new ImmutableEntityDefinition<Object>((Class<Object>) input, interfaces.toArray(new Class[interfaces.size()]));
            }
        }).each(new Processor<EntityDefinition<?>>() {
            @Override
            public void process(EntityDefinition<?> input) {
                definitionContext.addDefinition(input);
            }
        });
    }

}
