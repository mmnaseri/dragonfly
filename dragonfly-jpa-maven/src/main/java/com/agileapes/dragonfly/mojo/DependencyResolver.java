package com.agileapes.dragonfly.mojo;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.artifact.resolver.filter.ScopeArtifactFilter;
import org.apache.maven.shared.dependency.tree.DependencyNode;
import org.apache.maven.shared.dependency.tree.DependencyTreeBuilderException;
import org.apache.maven.shared.dependency.tree.traversal.CollectingDependencyNodeVisitor;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/13, 0:24)
 */
public class DependencyResolver {

    public Collection<File> resolve(PluginExecutor executor) throws DependencyTreeBuilderException {
        ArtifactFilter artifactFilter = new ScopeArtifactFilter(null);
        final DependencyNode rootNode = executor.getTreeBuilder().buildDependencyTree(executor.getProject(),
                executor.getLocalRepository(), executor.getArtifactFactory(), executor.getArtifactMetadataSource(),
                artifactFilter, executor.getArtifactCollector());

        CollectingDependencyNodeVisitor visitor = new CollectingDependencyNodeVisitor();
        rootNode.accept(visitor);
        final List<DependencyNode> nodes = visitor.getNodes();
        final HashSet<File> files = new HashSet<File>();
        for (DependencyNode node : nodes) {
            final Artifact artifact = node.getArtifact();
            if (artifact.getFile() != null) {
                files.add(artifact.getFile());
            } else {
                final File file = new File(executor.getLocalRepository().getBasedir() + File.separator + executor.getLocalRepository().pathOf(artifact));
                if (file.exists()) {
                    files.add(file);
                }
            }
        }
        return files;
    }

}
