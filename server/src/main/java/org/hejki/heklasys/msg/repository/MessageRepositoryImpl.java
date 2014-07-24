package org.hejki.heklasys.msg.repository;

import org.hejki.heklasys.msg.model.Message;
import org.hejki.sys.core.repository.PersistentObjectJdbcRepository;
import org.hejki.sys.core.repository.PersistentObjectRowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * TODO Document me.
 *
 * @author Petr Hejkal
 */
public class MessageRepositoryImpl extends PersistentObjectJdbcRepository<Message> implements MessageRepository {
    private static final Logger log = LoggerFactory.getLogger(MessageRepositoryImpl.class);
    private static final PersistentObjectRowMapper<Message> ROW_MAPPER = new PersistentObjectRowMapper<>(Message.class);

    private static final String SQL_GET_LAST = "SELECT * FROM messages" +
            " WHERE node_id = (SELECT node_id FROM device_properties WHERE name = 'ip' AND value = ?)" +
            " AND response_receive IS NULL" +
            " AND identifier = ? ORDER BY id DESC LIMIT 1";


    public MessageRepositoryImpl() {
        super(ROW_MAPPER, ROW_MAPPER, "messages");
    }

    @Override
    public Page<Message> findByDeviceId(int deviceId, Pageable pageable) {
        int count = getJdbcOperations().queryForObject("SELECT count(*) FROM messages WHERE node_id = ?", Integer.class, deviceId);
        List<Message> content = getJdbcOperations().query("SELECT * FROM messages WHERE node_id = ?", ROW_MAPPER, deviceId);

        return new PageImpl<>(content, pageable, count);
    }

    @Override
    public Optional<Message> findLastByIpAndIdentifier(String ipAddress, int identifier) {
        try {
            return Optional.of(getJdbcOperations().queryForObject(SQL_GET_LAST, ROW_MAPPER, ipAddress, identifier));
        } catch (EmptyResultDataAccessException e) {
            log.warn("Cannot found last message for ip {} with identifier {}.", ipAddress, identifier);
        }
        return Optional.empty();
    }
}
