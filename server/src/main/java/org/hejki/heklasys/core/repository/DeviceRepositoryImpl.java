package org.hejki.heklasys.core.repository;

import org.hejki.heklasys.core.model.Device;
import org.hejki.heklasys.core.model.DeviceType;
import org.hejki.heklasys.core.model.devices.NodeDevice;
import org.hejki.heklasys.core.model.Room;
import org.hejki.sys.core.beans.CorePropertyEditorRegistrar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.ConfigurablePropertyAccessor;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * TODO Document me.
 *
 * @author Petr Hejkal
 */
public class DeviceRepositoryImpl implements DeviceRepository {
    private static final Logger log = LoggerFactory.getLogger(DeviceRepositoryImpl.class);

    public static final String SQL_SELECT_COUNT = "SELECT count(*) FROM devices d";
    private static final String SQL_SELECT = "SELECT d.*, r.id roomId, r.name roomName, n.id nodeId, n.identifier nodeIdentifier " +
            " FROM devices d" +
            " JOIN rooms r ON r.id = d.room_id" +
            " LEFT JOIN devices n ON n.id = d.node_id";
    private static final String SQL_SELECT_BY_ID = SQL_SELECT + " WHERE d.id = ?";
    private static final String SQL_SELECT_PROPERTIES_BY_ID = "SELECT * FROM device_properties WHERE device_id = ?";
    private static final String SQL_SELECT_NODES = SQL_SELECT + " WHERE d.type = ?";
    private static final String SQL_SELECT_BY_PROPERTY = SQL_SELECT + " JOIN device_properties dp ON dp.device_id = d.id WHERE dp.name = ? AND dp.value = ?";
    private static final String SQL_SELECT_NODES_COUNT = SQL_SELECT_COUNT + " WHERE d.type = ?";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Page<Device> findAll(Pageable pageable) {
        StringBuilder sql = new StringBuilder();

        sql.append(SQL_SELECT);
        appendSelectSql(sql, null, pageable);

        int count = jdbcTemplate.queryForObject(SQL_SELECT_COUNT, Integer.class);
        List<Device> result = jdbcTemplate.query(sql.toString(), new DeviceRowMapper(), pageable.getPageSize(), pageable.getOffset());

        return new PageImpl<>(result, pageable, count);
    }

    @Override
    public Device findOne(int id) {
        Device device = jdbcTemplate.queryForObject(SQL_SELECT_BY_ID, new DeviceRowMapper(), id);

        jdbcTemplate.query(SQL_SELECT_PROPERTIES_BY_ID, new DevicePropertyRowHandler(device), id);
        return device;
    }

    @Override
    public Page<Device> findAllNodes(Pageable pageable) {
        StringBuilder sql = new StringBuilder();

        sql.append(SQL_SELECT_NODES);
        appendSelectSql(sql, null, pageable);

        int type = DeviceType.NODE.ordinal();
        int count = jdbcTemplate.queryForObject(SQL_SELECT_COUNT, Integer.class, type);
        List<Device> nodes = jdbcTemplate.query(sql.toString(), new DeviceRowMapper(), type, pageable.getPageSize(), pageable.getOffset());

        return new PageImpl<>(nodes, pageable, count);
    }

    @Override
    public NodeDevice findNodeByIp(String ipAddress) {
        NodeDevice node = (NodeDevice) jdbcTemplate.queryForObject(SQL_SELECT_BY_PROPERTY, new DeviceRowMapper(), "ip", ipAddress);

        jdbcTemplate.query(SQL_SELECT_PROPERTIES_BY_ID, new DevicePropertyRowHandler(node), node.getId());
        return node;
    }

    @Override
    public Page<Device> findByNode(int nodeId, Pageable pageable) {
        String where = " WHERE d.node_id = ?";
        Integer count = jdbcTemplate.queryForObject(SQL_SELECT_COUNT + where, Integer.class);

        StringBuilder sql = new StringBuilder();
        sql.append(SQL_SELECT);
        appendSelectSql(sql, where, pageable);

        List<Device> result = jdbcTemplate.query(sql.toString(), new DeviceRowMapper(), nodeId, pageable.getPageSize(), pageable.getOffset());
        return new PageImpl<>(result, pageable, count);
    }

    private void appendSelectSql(StringBuilder sql, String where, Pageable pageable) {
        if (null != where) {
            sql.append(where);
        }

        if (null != pageable) {
            if (null != pageable.getSort()) {
                sql.append(" ORDER BY");
                pageable.getSort().forEach(order -> {
                    sql.append(" ")
                       .append(order.getProperty())
                       .append(" ")
                       .append(order.getDirection());
                });
            }

            sql.append(" LIMIT ? OFFSET ?");
        }
    }

    @Override
    public <T extends Device> T save(T device) {
        return null;
    }

    private static class DeviceRowMapper implements RowMapper<Device> {
        @Override
        public Device mapRow(ResultSet rs, int rowNum) throws SQLException {
            Device device = DeviceType.valueOf(rs.getInt("type")).newInstance(rs.getInt("id"));

            device.setRoom(new Room(rs.getInt("roomId"), rs.getString("roomName")));
            device.setIdentifier(rs.getString("identifier"));

            Number nodeId = (Number) rs.getObject("nodeId");
            if (null != nodeId) {
                device.setNode(new NodeDevice(nodeId.intValue(), rs.getString("nodeIdentifier")));
            }
            return device;
        }
    }

    private static class DevicePropertyRowHandler implements RowCallbackHandler {
        private ConfigurablePropertyAccessor propertyAccessor;

        private DevicePropertyRowHandler(Device device) {
            this.propertyAccessor = PropertyAccessorFactory.forDirectFieldAccess(device);

            new CorePropertyEditorRegistrar().registerCustomEditors(propertyAccessor);
        }

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            propertyAccessor.setPropertyValue(rs.getString("name"), rs.getString("value"));
        }
    }
}
