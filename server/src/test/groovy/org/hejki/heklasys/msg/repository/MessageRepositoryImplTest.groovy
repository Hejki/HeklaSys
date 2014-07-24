package org.hejki.heklasys.msg.repository

import org.hejki.heklasys.TestConfiguration
import org.hejki.heklasys.core.model.devices.NodeDevice
import org.hejki.heklasys.msg.impl.PingMessage
import org.hejki.heklasys.msg.model.Message
import org.hejki.heklasys.msg.model.MessageType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.data.domain.PageRequest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
@ContextConfiguration(classes = [MessageRepositoryImplTest, TestConfiguration])
class MessageRepositoryImplTest extends Specification {

    @Autowired
    MessageRepository repository

    @Autowired
    JdbcTemplate template

    def "findAll"() {
        Iterator<Message> it
        Message result

        when:
        it = repository.findAll().iterator()
        result = it.next()

        then:
        null != result
        1001 == result.id
        MessageType.PING_REQUEST == result.type
        43 == result.identifier
        null == result.requestData
        '2013-12-14T17:13:58.483' == result.requestSend.toString()
        '2013-12-14T17:14:01.321' == result.responseReceive.toString()
        MessageType.RESPONSE_OK == result.responseType
        [10,0,4] == result.responseData
        1002 == it.next().id
        false == it.hasNext()
    }

    def "count"() {
        expect:
        2 == repository.count()
    }

    @DirtiesContext
    def "delete"() {
        when:
        repository.delete(param)

        then:
        result == template.queryForObject("select count(*) from messages", Integer)

        where:
        param           | result
        1001            | 1
        1               | 2
        msgWithId(1001) | 1
        msgWithId(2)    | 2
    }

    static Message msgWithId(int id) {
        def message = new Message(type: MessageType.HEARTBEAT)
        def idField = message.class.superclass.getDeclaredField("id")

        idField.accessible = true
        idField.set(message, id)
        return message
    }

    @DirtiesContext
    def "delete all"() {
        when:
        repository.deleteAll()

        then:
        0 == template.queryForObject("select count(*) from messages", Integer)
    }

    def "exist with id: #id"() {
        expect:
        result == repository.exists(id)

        where:
        id   | result
        1    | false
        1001 | true
    }

    def "findOne"() {
        Message result

        when:
        result = repository.findOne(1001)

        then:
        null != result
        1001 == result.id
        MessageType.PING_REQUEST == result.type
        43 == result.identifier
        null == result.requestData
        Date.parse('yyyy-MM-dd HH:mm:ss.SSS', '2013-12-14 17:13:58.483').time == result.requestSend.time
        Date.parse('yyyy-MM-dd HH:mm:ss.SSS', '2013-12-14 17:14:01.321').time == result.responseReceive.time
        MessageType.RESPONSE_OK == result.responseType
        [10,0,4] == result.responseData
    }

    @DirtiesContext
    def "save new"() {
        def message = new Message(type: MessageType.HEARTBEAT, nodeId: 1000)

        when:
        repository.save(message)

        then:
        0 < message.id
        3 == repository.count()
    }

    @DirtiesContext
    def "save new from Frame"() {
        def message = Message.messageFromFrame(new PingMessage(32), new NodeDevice(1000, 'Hejkino L'))

        when:
        repository.save(message)

        then:
        1 == template.queryForInt("select count(1) from messages where type = ? " +
                "and identifier = ? and node_id = ?", MessageType.PING_REQUEST.ordinal(),
                32, 1000)
    }

    @DirtiesContext
    def "save old"() {
        def message = repository.findOne(1001)
        message.responseType = MessageType.RESERVED_6

        when:
        repository.save(message)
        message = repository.findOne(1001)

        then:
        1001 == message.id
        MessageType.RESERVED_6 == message.responseType
        2 == repository.count()
    }

    def "findByDeviceId"() {
        def list

        when:
        list = repository.findByDeviceId(1000, new PageRequest(0, 10)).content

        then:
        1 == list.size()
        1001 == list[0].id
    }

    @Bean
    MessageRepository messageRepository() {
        return new MessageRepositoryImpl()
    }
}
