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

package com.agileapes.dragonfly.sample.cases;

import com.agileapes.dragonfly.data.DataAccess;
import com.agileapes.dragonfly.runtime.ext.identity.api.Identifiable;
import com.agileapes.dragonfly.sample.entities.Switch;
import com.agileapes.dragonfly.sample.entities.SwitchType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (14/5/31 AD, 11:59)
 */
@Service
public class EnumVariationTest extends BaseTestCase {

    @Autowired
    private DataAccess dataAccess;

    @Override
    public void run() {
        final Switch aSwitch = new Switch();
        aSwitch.setOn(Boolean.TRUE);
        aSwitch.setType(SwitchType.SPECIAL);
        aSwitch.setDate(new Date());
        aSwitch.setNames(Arrays.asList("first","second","third"));
        dataAccess.save(aSwitch);
        final List<Switch> switches = dataAccess.findAll(Switch.class);
        expect(switches).toHaveSize(1);
        for (Switch loadedSwitch : switches) {
            expect(loadedSwitch.isOn()).toBeTrue();
            expect(loadedSwitch.getType()).toEqual(SwitchType.SPECIAL);
            expect(loadedSwitch.getNames()).toHaveSize(3);
            expect(loadedSwitch.getNames()).toContain("first");
            expect(loadedSwitch.getNames()).toContain("second");
            expect(loadedSwitch.getNames()).toContain("third");
            loadedSwitch.setOn(false);
            dataAccess.save(loadedSwitch);
            final Switch changedSwitch = dataAccess.find(Switch.class, ((Identifiable) loadedSwitch).getUniqueKey());
            expect(changedSwitch.isOn()).toBeFalse();
        }
        dataAccess.deleteAll(Switch.class);
    }

}
