/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 Milad Naseri.
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

package com.mmnaseri.dragonfly.security;

import com.mmnaseri.dragonfly.data.DataAccess;

/**
 * This interface abstracts everything that is a participant in making a decision regarding an action
 * taken about data through the {@link DataAccess} interface.
 *
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (2013/9/9, 16:52)
 */
public interface DataSecurityPolicy {

    /**
     * @return the name of the security policy, which should be human readable in case a log request is
     * placed regarding a policy failure
     */
    String getName();

    /**
     * @return the filter deciding which actors are included in this policy
     */
    ActorFilter getActorFilter();

    /**
     * @return the filter deciding to which subjects the policy needs apply
     */
    SubjectFilter getSubjectFilter();

    /**
     * @return the type of decision attributed with actions that match the policy
     */
    PolicyDecisionType getDecisionType();

}
