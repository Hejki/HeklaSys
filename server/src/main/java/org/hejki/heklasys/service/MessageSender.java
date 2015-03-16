package org.hejki.heklasys.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.hejki.heklasys.config.AppSettings;
import org.hejki.heklasys.exception.ErrorResponseException;
import org.hejki.heklasys.model.Node;
import org.hejki.heklasys.model.RequestMessage;
import org.hejki.heklasys.model.msg.response.ErrorResponseMessage;
import org.hejki.heklasys.model.msg.response.OkResponseMessage;
import org.hejki.heklasys.model.msg.response.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.*;

/**
 * TODO Document me.
 *
 * @author Petr Hejkal <petr.hejkal@doxologic.com>
 */
@Slf4j
@Service
public class MessageSender {
    private final Map<String, NodeRequestMap> messageRegistry = new HashMap<>();

    @Autowired
    private DatagramServer server;

    @Autowired
    private AppSettings appSettings;

    public <T> T sendAndReceive(Node node, RequestMessage<T> request) {
        return sendAndReceive(node, request, appSettings.getMessageDefaultTimeoutSeconds(), TimeUnit.SECONDS);
    }

    public <T> T sendAndReceive(Node node, RequestMessage<T> request, long timeout, TimeUnit unit) {
        String address = node.getAddress();
        NodeRequestMap map;

        synchronized (messageRegistry) {
            if (messageRegistry.containsKey(address) == false) {
                messageRegistry.put(address, new NodeRequestMap());
            }

            map = messageRegistry.get(address);
        }

        Future<T> future = map.addMessage(request);
        server.writeAndFlush(request, new InetSocketAddress(address, node.getPort()));

        try {
            return future.get(timeout, unit);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void processResponse(ResponseMessage response, String originAddress) {
        NodeRequestMap map;

        synchronized (messageRegistry) {
            map = messageRegistry.get(originAddress);
        }

        if (null != map) {
            map.receiveResponse(response);
        } else {
            log.warn("Receive response message from unknown node, node_address={}, response={}",
                    originAddress, response);
        }
    }

    @Data
    private static class NodeRequestMap {
        private byte messageIdCounter;
        private FutureResponse[] messages;

        private NodeRequestMap() {
            messageIdCounter = (byte) new Random(System.currentTimeMillis()).nextInt();
            messages = new FutureResponse[255];
        }

        public <T> Future<T> addMessage(RequestMessage<T> message) {
            int id = messageIdCounter++ & 0xFF;

            message.setIdentifier(id);
            if (messages[id] != null) {
                log.warn("Drop unresolved request message={}", messages[id]);
            }

            FutureResponse<T> future = new FutureResponse<>(message);
            messages[id] = future;
            return future;
        }

        public void receiveResponse(ResponseMessage response) {
            int id = response.getIdentifier();
            FutureResponse future = messages[id];

            if (null == future) {
                log.warn("Obtain response to not send request, message_id={}, response={}", id, response);
                return;
            }

            messages[id] = null;
            future.completeWithResponse(response);
        }
    }

    private static class FutureResponse<T> extends CompletableFuture<T> {
        private RequestMessage<T> request;

        private FutureResponse(RequestMessage<T> request) {
            this.request = request;
        }

        public void completeWithResponse(ResponseMessage response) {
            if (response instanceof OkResponseMessage) {
                complete(request.parseOkResponse((OkResponseMessage) response));
            } else if (response instanceof ErrorResponseMessage) {
                ErrorResponseMessage message = (ErrorResponseMessage) response;

                completeExceptionally(new ErrorResponseException(message.getError()));
            } else {
                completeExceptionally(new RuntimeException("Obtain not expected response message" + response));
            }
        }
    }
}
