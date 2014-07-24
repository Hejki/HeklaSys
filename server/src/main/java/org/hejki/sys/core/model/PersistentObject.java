package org.hejki.sys.core.model;

import org.springframework.data.domain.Persistable;

/**
 * TODO Document me.
 *
 * @author Petr Hejkal
 */
public class PersistentObject implements Persistable<Integer> {
    private Integer id;

    protected PersistentObject() {
    }

    protected PersistentObject(int id) {
        this.id = id;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return id == null;
    }
}
