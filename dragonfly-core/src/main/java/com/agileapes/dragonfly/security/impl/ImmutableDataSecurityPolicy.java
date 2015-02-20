/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 AgileApes, Ltd.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.agileapes.dragonfly.security.impl;

import com.agileapes.dragonfly.security.ActorFilter;
import com.agileapes.dragonfly.security.DataSecurityPolicy;
import com.agileapes.dragonfly.security.PolicyDecisionType;
import com.agileapes.dragonfly.security.SubjectFilter;

/**
 * This class allows for definition of immutable security policies.
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/9, 16:56)
 */
public class ImmutableDataSecurityPolicy implements DataSecurityPolicy {

    private final String name;
    private final ActorFilter actorFilter;
    private final SubjectFilter subjectFilter;
    private final PolicyDecisionType policyDecisionType;

    public ImmutableDataSecurityPolicy(String name, ActorFilter actorFilter, SubjectFilter subjectFilter, PolicyDecisionType policyDecisionType) {
        this.name = name;
        this.actorFilter = actorFilter;
        this.subjectFilter = subjectFilter;
        this.policyDecisionType = policyDecisionType;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ActorFilter getActorFilter() {
        return actorFilter;
    }

    @Override
    public SubjectFilter getSubjectFilter() {
        return subjectFilter;
    }

    @Override
    public PolicyDecisionType getDecisionType() {
        return policyDecisionType;
    }
}
