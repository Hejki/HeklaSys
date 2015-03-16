package org.hejki.heklasys.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;

import java.io.Serializable;

/**
 * TODO Document me.
 *
 * @author Petr Hejkal <petr.hejkal@doxologic.com>
 */
public abstract class AbstractPersistable<ID extends Serializable> implements Persistable<ID> {
    @Id
    private ID id;

    @Override
    public ID getId() {
        return id;
    }

    public void setId(ID id) {
        this.id = id;
    }

    @Override
    public boolean isNew() {
        return id == null;
    }
}
