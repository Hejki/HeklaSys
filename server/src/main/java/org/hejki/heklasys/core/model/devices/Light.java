package org.hejki.heklasys.core.model.devices;

import org.hejki.heklasys.core.model.Device;
import org.hejki.heklasys.core.model.DeviceType;

/**
 * TODO Document me.
 *
 * @author Petr Hejkal
 */
public class Light extends Device {
    public Light() {
        super(DeviceType.LIGHT);
    }

    public Light(int id) {
        super(id, DeviceType.LIGHT);
    }
}
