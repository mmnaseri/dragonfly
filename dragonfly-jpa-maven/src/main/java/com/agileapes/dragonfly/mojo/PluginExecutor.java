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

package com.agileapes.dragonfly.mojo;

import com.agileapes.couteau.maven.mojo.AbstractSpringPluginExecutor;
import com.agileapes.dragonfly.dialect.DatabaseDialect;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.DefaultArtifactRepository;
import org.apache.maven.artifact.repository.layout.DefaultRepositoryLayout;
import org.apache.maven.artifact.resolver.ArtifactCollector;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.dependency.tree.DependencyTreeBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Set;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @goal execute
 * @phase process-classes
 * @execute phase="process-classes"
 * @requiresDependencyResolution process-classes
 * @since 1.0 (2013/9/12, 12:41)
 */
public class PluginExecutor extends AbstractSpringPluginExecutor {

    /**
     * @parameter
     * @required
     */
    private String dialect;

    /**
     * @parameter expression='${project}'
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * @parameter
     * @required
     */
    private Set<String> packages;

    /**
     * @component
     * @required
     * @readonly
     */
    private ArtifactFactory artifactFactory;

    /**
     * @component
     * @required
     * @readonly
     */
    private ArtifactMetadataSource artifactMetadataSource;

    /**
     * @component
     * @required
     * @readonly
     */
    private ArtifactCollector artifactCollector;

    /**
     * @component
     * @required
     * @readonly
     */
    private DependencyTreeBuilder treeBuilder;

    private ArtifactRepository localRepository;

    public ArtifactFactory getArtifactFactory() {
        return artifactFactory;
    }

    public ArtifactMetadataSource getArtifactMetadataSource() {
        return artifactMetadataSource;
    }

    public ArtifactCollector getArtifactCollector() {
        return artifactCollector;
    }

    public DependencyTreeBuilder getTreeBuilder() {
        return treeBuilder;
    }

    public ArtifactRepository getLocalRepository() {
        return localRepository;
    }

    private DatabaseDialect databaseDialect = null;

    @Override
    public MavenProject getProject() {
        return project;
    }

    @Override
    public Set<String> getScanPackages() {
        return packages;
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        localRepository = getProject().getArtifact().getRepository();
        if (localRepository == null) {
            final String localRepoUrl = "file://" + System.getProperty("maven.repo.local", System.getProperty("user.home") + "/.m2/repository");
            localRepository = new DefaultArtifactRepository("central", localRepoUrl, new DefaultRepositoryLayout());
        }
        super.execute();
    }

    @Override
    protected ApplicationContext loadApplicationContext() {
        return new ClassPathXmlApplicationContext("/plugin.xml");
    }

    public DatabaseDialect getDialect() {
        if (databaseDialect == null) {
            try {
                databaseDialect = (DatabaseDialect) Class.forName(dialect).newInstance();
            } catch (Exception ignored) {
            }
        }
        return databaseDialect;
    }
}
