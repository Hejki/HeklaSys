package org.hejki.heklasys.msg.repository;

import org.hejki.heklasys.msg.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

/**
 * TODO Document me.
 *
 * @author Petr Hejkal
 */
public interface MessageRepository extends PagingAndSortingRepository<Message, Integer> {
    Page<Message> findByDeviceId(int deviceId, Pageable pageable);

    Optional<Message> findLastByIpAndIdentifier(String ipAddress, int identifier);
}
