package com.agileapes.dragonfly.sample.wrong;

import com.agileapes.dragonfly.sample.ext.ManualIdentity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (14/3/17 AD, 15:04)
 */
@Entity
@Table(
        name = "no_identity_version_entity",
        schema = "test"
)
@ManualIdentity
public class EntityWithVersionWithoutIdentity {

    private Long version;

    @Version
    @Column(nullable = false)
    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
