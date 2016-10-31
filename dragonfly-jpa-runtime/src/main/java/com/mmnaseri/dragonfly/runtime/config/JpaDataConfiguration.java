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

package com.mmnaseri.dragonfly.runtime.config;

import com.mmnaseri.dragonfly.events.CallbackResolver;
import com.mmnaseri.dragonfly.runtime.session.JpaSessionPreparator;
import com.mmnaseri.dragonfly.runtime.session.SessionPreparator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (14/8/13 AD, 15:35)
 */
@Configuration
@Import(RuntimeDataConfiguration.class)
public class JpaDataConfiguration {

    @Bean
    public CallbackResolver callbackResolver() {
        return new CallbackResolver();
    }

    @Bean
    public SessionPreparator sessionPreparator(@Value("${dragonfly.basePackages}") String basePackages, ApplicationContext applicationContext) {
        final JpaSessionPreparator preparator = new JpaSessionPreparator(applicationContext.getClassLoader());
        final List<String> packages = new ArrayList<String>(Arrays.asList(basePackages.trim().split("\\s*,\\s*")));
        packages.add("com.mmnaseri.dragonfly.runtime.ext");
        preparator.setBasePackages(packages.toArray(new String[packages.size()]));
        preparator.setInitializeSession(true);
        return preparator;
    }

}
