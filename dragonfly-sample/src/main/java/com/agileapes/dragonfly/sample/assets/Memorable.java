package com.agileapes.dragonfly.sample.assets;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/10/5, 10:44)
 */
public class Memorable {

    private Long id;
    private String name;

    public Memorable(Long id, String name) {
        setId(id);
        setName(name);
    }

    public Memorable(String name) {
        this(null, name);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

