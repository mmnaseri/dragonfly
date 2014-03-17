package com.agileapes.dragonfly.analysis.impl;

import com.agileapes.dragonfly.analysis.IssueTarget;
import com.agileapes.dragonfly.ext.ExtensionMetadata;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (14/3/17 AD, 15:32)
 */
public class ExtensionIssueTarget implements IssueTarget<ExtensionMetadata> {

    private final ExtensionMetadata extensionMetadata;

    public ExtensionIssueTarget(ExtensionMetadata extensionMetadata) {
        this.extensionMetadata = extensionMetadata;
    }

    @Override
    public ExtensionMetadata getTarget() {
        return extensionMetadata;
    }

    @Override
    public String toString() {
        return "extension '" + extensionMetadata.getExtension().getCanonicalName() + "'";
    }
}
