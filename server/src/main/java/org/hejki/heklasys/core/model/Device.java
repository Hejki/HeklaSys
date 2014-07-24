package org.hejki.heklasys.core.model;

import org.hejki.heklasys.core.model.devices.NodeDevice;
import org.hejki.sys.core.model.PersistentObject;

/**
 * TODO Document me.
 *
 * @author Petr Hejkal
 */
public abstract class Device extends PersistentObject {
    private DeviceType type;
    private String identifier;
    private NodeDevice node;
    private Room room;
    private RoomPosition roomPosition;

    protected Device(int id, DeviceType type) {
        super(id);
        this.type = type;
    }

    protected Device(DeviceType type) {
        this.type = type;
    }

    public DeviceType getType() {
        return type;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public NodeDevice getNode() {
        return node;
    }

    public void setNode(NodeDevice node) {
        this.node = node;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Device{");
        sb.append("type=").append(type);
        sb.append(", identifier='").append(getId()).append('\'');
        sb.append(", node=").append(node);
        sb.append(", room=").append(room);
        sb.append(", roomPosition=").append(roomPosition);
        sb.append('}');
        return sb.toString();
    }
}
