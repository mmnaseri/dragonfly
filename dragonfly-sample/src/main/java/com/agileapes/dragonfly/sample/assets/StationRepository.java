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

package com.agileapes.dragonfly.sample.assets;

import com.agileapes.dragonfly.runtime.repo.CrudRepository;
import com.agileapes.dragonfly.runtime.repo.NativeQuery;
import com.agileapes.dragonfly.runtime.repo.Parameter;
import com.agileapes.dragonfly.runtime.repo.QueryAlias;
import com.agileapes.dragonfly.sample.entities.Station;

import java.util.List;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (14/8/14 AD, 12:39)
 */
public interface StationRepository extends CrudRepository<Station, Long> {

    Station findByNameAndVersion(String name, Integer version);

    List<Station> findByName(String name);

    void deleteByNameAndVersion(String name, Integer version);

    @NativeQuery("SELECT * FROM ${qualify(table)} WHERE ${qualify(column('version'))} > ${value.version};")
    List<Station> findNewerThan(@Parameter("version") Integer version);

    @QueryAlias("findNewerThan")
    List<Station> findFresherThan(@Parameter("version") Integer version);

    @NativeQuery
    void removeAll();

    @QueryAlias("deleteAll")
    void deleteEverything();

}
