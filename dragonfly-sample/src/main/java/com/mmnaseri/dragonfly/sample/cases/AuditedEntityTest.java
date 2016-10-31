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

package com.mmnaseri.dragonfly.sample.cases;

import com.mmnaseri.dragonfly.data.DataAccess;
import com.mmnaseri.dragonfly.runtime.ext.audit.api.Auditable;
import com.mmnaseri.dragonfly.runtime.ext.identity.api.Identifiable;
import com.mmnaseri.dragonfly.sample.entities.SampleAuditedEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (2015/2/16 AD, 7:12)
 */
@Service
public class AuditedEntityTest extends BaseTestCase {

    @Autowired
    private DataAccess dataAccess;

    @Override
    public void run() {
        final SampleAuditedEntity entity = new SampleAuditedEntity();
        entity.setName("A");
        final Long key = ((Identifiable) dataAccess.save(entity)).getUniqueKey();
        final SampleAuditedEntity first = dataAccess.find(SampleAuditedEntity.class, key);
        expect(first.getName()).toBe("A");
        expect(((Auditable) first).getUpdateCount()).toBe(0L);
        first.setName("B");
        dataAccess.save(first);
        final SampleAuditedEntity second = dataAccess.find(SampleAuditedEntity.class, key);
        expect(second.getName()).toBe("B");
        expect(((Auditable) second).getUpdateCount()).toBe(1L);
    }

}
