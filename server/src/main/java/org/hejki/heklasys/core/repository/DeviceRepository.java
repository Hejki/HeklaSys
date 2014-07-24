package org.hejki.heklasys.core.repository;

import org.hejki.heklasys.core.model.Device;
import org.hejki.heklasys.core.model.devices.NodeDevice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;

/**
 * TODO Document me.
 *
 * @author Petr Hejkal
 */
public interface DeviceRepository extends Repository<Device, Integer> {

    Page<Device> findAll(Pageable pageable);

    /**
     * Find one device by it's id. This device will be loaded all
     * device properties.
     *
     * @param id device primary identifier
     * @return loaded device
     * @throws org.springframework.dao.DataAccessException if no device with requested id found
     */
    Device findOne(int id);

    Page<Device> findAllNodes(Pageable pageable);

    NodeDevice findNodeByIp(String ipAddress);

    Page<Device> findByNode(int nodeId, Pageable pageable);

    <T extends Device> T save(T device);
}
