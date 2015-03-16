package org.hejki.heklasys.model

import org.hejki.heklasys.model.msg.general.PingRequestMessage
import reactor.io.Buffer
import spock.lang.Specification
import spock.lang.Unroll

import static org.hejki.heklasys.model.MessageType.PING_REQUEST

class MessageTest extends Specification {

    @Unroll
    def "create message from buffer, type=#type"() {
        when:
        def msg = Message.from(Buffer.wrap(input))

        then:
        type == msg.type
        size == msg.dataSize
        3 == msg.identifier
        clazz == msg.class

        where:
        input            | type         | size | clazz
        b(10, 0, 3, 243) | PING_REQUEST | 0    | PingRequestMessage
    }

    def b(int... bytes) {
        return bytes as byte[]
    }
}
