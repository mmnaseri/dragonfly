package com.agileapes.dragonfly.tasks;

import com.agileapes.couteau.basics.api.Transformer;
import com.agileapes.couteau.freemarker.utils.FreemarkerUtils;
import com.agileapes.couteau.lang.compiler.DynamicClassCompiler;
import com.agileapes.couteau.lang.compiler.impl.DefaultDynamicClassCompiler;
import com.agileapes.couteau.lang.compiler.impl.MappedClassLoader;
import com.agileapes.couteau.lang.compiler.impl.SimpleJavaSourceCompiler;
import com.agileapes.couteau.lang.error.CompileException;
import com.agileapes.couteau.reflection.beans.impl.AbstractClassBeanDescriptor;
import com.agileapes.couteau.reflection.beans.impl.MethodClassBeanDescriptor;
import com.agileapes.couteau.reflection.error.NoSuchPropertyException;
import com.agileapes.couteau.reflection.property.ReadPropertyAccessor;
import com.agileapes.couteau.reflection.property.WritePropertyAccessor;
import com.agileapes.couteau.reflection.property.impl.MethodReadPropertyAccessor;
import com.agileapes.couteau.reflection.property.impl.MethodWritePropertyAccessor;
import com.agileapes.couteau.reflection.util.ReflectionUtils;
import com.agileapes.dragonfly.io.OutputManager;
import com.agileapes.dragonfly.metadata.ColumnMetadata;
import com.agileapes.dragonfly.metadata.MetadataRegistry;
import com.agileapes.dragonfly.metadata.TableMetadata;
import com.agileapes.dragonfly.model.BeanMapperModel;
import com.agileapes.dragonfly.model.PropertyAccessModel;
import com.agileapes.dragonfly.mojo.PluginExecutor;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.MojoFailureException;

import javax.persistence.TemporalType;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/14, 4:12)
 */
public class GenerateMapHandlersTask extends AbstractCodeGenerationTask {

    @Override
    public void execute(PluginExecutor executor) throws MojoFailureException {
        final Set<String> generatedClasses = new CopyOnWriteArraySet<String>();
        final MetadataRegistry metadataRegistry = applicationContext.getBean(MetadataCollectorTask.class).getRegistry();
        final OutputManager outputManager = applicationContext.getBean(OutputManager.class);
        final Configuration configuration = FreemarkerUtils.getConfiguration(getClass(), "/ftl/");
        final Template template;
        try {
            template = configuration.getTemplate("mapCreator.ftl");
        } catch (IOException e) {
            throw new MojoFailureException("Error retrieving template", e);
        }
        final List<TableMetadata<?>> metadataList = with(metadataRegistry.getEntityTypes()).transform(new Transformer<Class<?>, TableMetadata<?>>() {
            @Override
            public TableMetadata<?> map(Class<?> entityType) {
                return metadataRegistry.getTableMetadata(entityType);
            }
        }).list();
        for (TableMetadata<?> tableMetadata : metadataList) {
            final BeanMapperModel model = new BeanMapperModel();
            final Class<?> entityType = tableMetadata.getEntityType();
            model.setEntityType(entityType);
            final StringWriter out = new StringWriter();
            for (ColumnMetadata columnMetadata : tableMetadata.getColumns()) {
                model.addProperty(getPropertyAccessModel(columnMetadata));
            }
            try {
                template.process(model, out);
            } catch (TemplateException e) {
                throw new MojoFailureException("Error processing the template", e);
            } catch (IOException e) {
                throw new MojoFailureException("An I/O error prevented code generation", e);
            }
            final DynamicClassCompiler compiler = new DefaultDynamicClassCompiler(getClass().getClassLoader());
            try {
                compiler.setOption(SimpleJavaSourceCompiler.Option.CLASSPATH, getClassPath(executor));
            } catch (DependencyResolutionRequiredException e) {
                throw new MojoFailureException("Failed to prepare classpath", e);
            }
            final String className = entityType.getCanonicalName().substring(0, entityType.getCanonicalName().lastIndexOf('.')) + "." + entityType.getSimpleName() + "MapHandler";
            try {
                compiler.compile(className, new StringReader(out.toString()));
                generatedClasses.add(className);
                System.out.println(out);
            } catch (CompileException e) {
                throw new MojoFailureException("Failed to compile source code", e);
            }
            final byte[] bytes;
            try {
                bytes = ((MappedClassLoader) compiler.getClassLoader()).getBytes(className);
            } catch (ClassNotFoundException e) {
                throw new MojoFailureException("Class not found", e);
            }
            final String path = className.replace('.', File.separatorChar).concat(".class");
            try {
                outputManager.writeOutputFile(path, bytes);
            } catch (IOException e) {
                throw new MojoFailureException("Failed to write output", e);
            }
        }
        final Template handlersTemplate;
        try {
            handlersTemplate = configuration.getTemplate("mapHandlers.ftl");
        } catch (IOException e) {
            throw new MojoFailureException("Failed to locate template", e);
        }
        final StringWriter out = new StringWriter();
        try {
            final HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("beans", generatedClasses);
            handlersTemplate.process(map, out);
        } catch (TemplateException e) {
            throw new MojoFailureException("Failed to process template", e);
        } catch (IOException e) {
            throw new MojoFailureException("Failed to produce output", e);
        }
        try {
            outputManager.writeSourceFile("/src/main/resources/data/handlers.xml", out.toString());
        } catch (IOException e) {
            throw new MojoFailureException("Failed to write output file");
        }
    }

    private static PropertyAccessModel getPropertyAccessModel(ColumnMetadata columnMetadata) throws MojoFailureException {
        if (columnMetadata == null) {
            return null;
        }
        //noinspection unchecked
        final AbstractClassBeanDescriptor<?> beanDescriptor = new MethodClassBeanDescriptor<Object>((Class<Object>) columnMetadata.getDeclaringClass());
        final ReadPropertyAccessor<?> reader;
        final WritePropertyAccessor<?> writer;
        try {
            reader = beanDescriptor.getPropertyReader(columnMetadata.getPropertyName());
            writer = beanDescriptor.getPropertyWriter(columnMetadata.getPropertyName());
        } catch (NoSuchPropertyException e) {
            throw new MojoFailureException("Property definition not found: " + columnMetadata.getTable().getEntityType().getCanonicalName() + "." + columnMetadata.getName());
        }
        final String getterName = ((MethodReadPropertyAccessor) reader).getMethod().getName();
        final String setterName = ((MethodWritePropertyAccessor) writer).getMethod().getName();
        return new PropertyAccessModel(columnMetadata.getPropertyName(), columnMetadata.getName(), ReflectionUtils.mapType(columnMetadata.getPropertyType()), getterName, setterName, columnMetadata.getDeclaringClass(), getTemporalType(columnMetadata), getPropertyAccessModel(columnMetadata.getForeignReference()));
    }

    private static TemporalType getTemporalType(ColumnMetadata columnMetadata) {
        if (columnMetadata.getType() == Types.DATE) {
            return TemporalType.DATE;
        } else if (columnMetadata.getType() == Types.TIME) {
            return TemporalType.TIME;
        } else if (columnMetadata.getType() == Types.TIMESTAMP) {
            return TemporalType.TIMESTAMP;
        }
        return null;
    }

}
