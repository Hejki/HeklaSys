package org.hejki.heklasys.core.model.devices;

import org.hejki.heklasys.core.model.Device;
import org.hejki.heklasys.core.model.DeviceType;

import java.net.InetAddress;

/**
 * TODO Document me.
 *
 * @author Petr Hejkal
 */
public class NodeDevice extends Device {
    private InetAddress ip;
    private int port;

    public NodeDevice() {
        super(DeviceType.NODE);
    }

    public NodeDevice(int id, String identifier) {
        super(id, DeviceType.NODE);
        setIdentifier(identifier);
    }

    public InetAddress getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }
}
