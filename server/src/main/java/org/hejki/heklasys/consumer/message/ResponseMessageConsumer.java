package org.hejki.heklasys.consumer.message;

import lombok.extern.slf4j.Slf4j;
import org.hejki.heklasys.model.Node;
import org.hejki.heklasys.model.msg.response.ResponseMessage;
import org.hejki.heklasys.service.MessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.spring.context.annotation.Consumer;

/**
 * TODO Document me.
 *
 * @author Petr Hejkal <petr.hejkal@doxologic.com>
 */
@Slf4j
@Consumer
public class ResponseMessageConsumer extends GenericMessageConsumer<ResponseMessage> {
    @Autowired
    private MessageSender messageSender;

    @Override
    protected void handleMessage(ResponseMessage message, Node node) {
        messageSender.processResponse(message, node.getAddress());
    }
}
