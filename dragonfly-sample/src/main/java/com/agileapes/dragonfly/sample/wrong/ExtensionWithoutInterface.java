package com.agileapes.dragonfly.sample.wrong;

import com.agileapes.dragonfly.annotations.Extension;

import javax.persistence.Column;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (14/3/17 AD, 0:42)
 */
@Extension(filter = "NoClass")
public class ExtensionWithoutInterface {

    private String name;

    @Column
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
