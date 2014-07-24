package org.hejki.heklasys.core.model;

import org.hejki.heklasys.core.model.devices.Light;
import org.hejki.heklasys.core.model.devices.NodeDevice;
import org.hejki.sys.core.model.PersistentObject;
import org.hejki.sys.core.utils.ObjectUtils;
import org.springframework.beans.BeanUtils;

/**
 * TODO Document me.
 *
 * @author Petr Hejkal
 */
public enum DeviceType {
    NODE(NodeDevice.class),
    LIGHT(Light.class),
    TEMPERATURE_SENSOR(null),
    SWITCH(null),
    RELAY(null),;

    private Class<? extends Device> deviceClass;

    DeviceType(Class<? extends Device> deviceClass) {
        this.deviceClass = deviceClass;
    }

    public static DeviceType valueOf(int index) {
        return DeviceType.values()[index];
    }

    public Device newInstance(Integer id) {
        Device device = BeanUtils.instantiateClass(deviceClass);

        ObjectUtils.setFieldValue(PersistentObject.class, "id", device, id);
        return device;
    }
}
