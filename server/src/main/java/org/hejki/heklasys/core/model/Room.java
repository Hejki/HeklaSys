package org.hejki.heklasys.core.model;

import lombok.Getter;
import lombok.Setter;
import org.hejki.sys.core.model.PersistentObject;

/**
 * TODO Document me.
 *
 * @author Petr Hejkal
 */
@Getter
@Setter
public class Room extends PersistentObject {
    private String name;
    private int floor;

    public Room() {
    }

    public Room(int id, String name) {
        super(id);
        this.name = name;
    }
}
