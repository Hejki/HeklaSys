package org.hejki.heklasys.service
import org.hejki.heklasys.model.Node
import org.hejki.heklasys.model.RequestMessage
import org.hejki.heklasys.model.msg.response.OkResponseMessage
import spock.lang.Specification

import static java.util.concurrent.TimeUnit.SECONDS
/**
 * TODO Document me.
 *
 * @author Petr Hejkal <petr.hejkal@doxologic.com>
 */
class MessageSenderTest extends Specification {

    def "send and receive"() {
        def requestId = 0
        def node = new Node(address: "0.0.0.0", port: 12321)
        def request = Mock(RequestMessage)
        def response = Mock(OkResponseMessage)
        def server = Mock(DatagramServer)
        def service = new MessageSender()

        service.server = server

        when:
        def data = service.sendAndReceive(node, request, 10, SECONDS)

        then:
        data == "OK"
        1 * request.setIdentifier(_) >> { int id -> requestId = id }
        1 * response.getIdentifier() >> { requestId }
        1 * server.writeAndFlush(_, _) >> {
            service.processResponse(response, "0.0.0.0")
        }
        1 * request.parseOkResponse(response) >> "OK"
    }
}
