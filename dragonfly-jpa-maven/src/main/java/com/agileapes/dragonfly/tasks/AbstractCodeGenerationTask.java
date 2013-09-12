package com.agileapes.dragonfly.tasks;

import com.agileapes.couteau.basics.api.Transformer;
import com.agileapes.couteau.maven.resource.ConfigurableClassLoader;
import com.agileapes.couteau.maven.task.PluginTask;
import com.agileapes.dragonfly.mojo.DependencyResolver;
import com.agileapes.dragonfly.mojo.PluginExecutor;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.shared.dependency.tree.DependencyTreeBuilderException;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/13, 2:35)
 */
public abstract class AbstractCodeGenerationTask extends PluginTask<PluginExecutor> implements ApplicationContextAware {

    protected ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * This method will determine the classpath argument passed to the JavaC compiler used by this
     * implementation
     *
     * @return the classpath
     * @throws org.apache.maven.artifact.DependencyResolutionRequiredException
     *          if the resolution of classpath requires a prior
     *          resolution of dependencies for the target project
     */
    protected String getClassPath(PluginExecutor executor) throws DependencyResolutionRequiredException {
        //Adding compile time dependencies for the project
        //noinspection unchecked
        final List<String> elements = executor.getProject().getCompileClasspathElements();
        final Set<File> classPathElements = new HashSet<File>();
        for (String element : elements) {
            final File file = new File(element);
            if (file.exists()) {
                classPathElements.add(file);
            }
        }
        //Adding runtime dependencies available to the target project
        final ClassLoader loader = executor.getProjectClassLoader();
        final URL[] urls;
        if (loader instanceof URLClassLoader) {
            final URLClassLoader classLoader = (URLClassLoader) loader;
            urls = classLoader.getURLs();
        } else if (loader instanceof ConfigurableClassLoader) {
            urls = ((ConfigurableClassLoader) loader).getUrls();
        } else {
            urls = new URL[0];
        }
        for (URL url : urls) {
            try {
                final File file = new File(url.toURI());
                if (file.exists()) {
                    classPathElements.add(file);
                }
            } catch (Throwable ignored) {
            }
        }
        final DependencyResolver dependencyResolver = applicationContext.getBean(DependencyResolver.class);
        try {
            classPathElements.addAll(dependencyResolver.resolve(executor));
        } catch (DependencyTreeBuilderException ignored) {
        }
        classPathElements.addAll(getClassPathElements());
        return StringUtils.collectionToDelimitedString(with(classPathElements).transform(new Transformer<File, String>() {
            @Override
            public String map(File input) {
                return input.getAbsolutePath();
            }
        }).list(), File.pathSeparator);
    }

    protected Collection<? extends File> getClassPathElements() {
        return Collections.emptyList();
    }

}
