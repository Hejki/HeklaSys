package org.hejki.heklasys.msg.service;

import org.hejki.heklasys.core.model.devices.NodeDevice;
import org.hejki.heklasys.msg.impl.MessageFrame;
import org.hejki.heklasys.msg.model.Message;
import org.hejki.heklasys.msg.model.MessageType;
import org.hejki.heklasys.msg.repository.MessageRepository;
import org.hejki.sys.core.utils.ByteUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

import static org.hejki.heklasys.msg.impl.MessageFrame.MESSAGE_MIN_LEN;
import static org.hejki.heklasys.msg.impl.MessageFrame.calculateCrc;
import static org.hejki.sys.core.utils.ByteUtils.toInt;
import static org.hejki.sys.core.utils.NetworkUtils.socketAddressToString;

/**
 * TODO Document me.
 *
 * @author Petr Hejkal
 */
public class MessageCommunicationServiceImpl implements MessageCommunicationService, InitializingBean, DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(MessageCommunicationServiceImpl.class);
    private static final byte[] EMPTY_DATA = new byte[0];

    @Value("${udp.port}")
    private int serverUdpPort;

    @Autowired
    private ExecutorService threadPoolExecutor;

    @Autowired
    private MessageRepository messageRepository;

    private DatagramChannel datagramChannel;
    private DatagramReceiveThread datagramReceiveThread;

    @Override
    public void send(MessageFrame messageFrame, NodeDevice target) {
        ByteBuffer request = messageFrame.createRequest();
        InetSocketAddress targetAddress = new InetSocketAddress(target.getIp(), target.getPort());

        // store message to db
        messageRepository.save(Message.messageFromFrame(messageFrame, target));

        threadPoolExecutor.execute(() -> {
            try {
                request.position(0);
                datagramChannel.send(request, targetAddress);
            } catch (IOException e) {
                log.error("Cannot send message '{}' to node '{} ({})'.", messageFrame.getType(), target.getIdentifier(), target.getId(), e);
            }
        });
    }

    @Override
    public void receive(SocketAddress source, ByteBuffer buffer) {
        log.debug("Message from {}, buffer {}", source, buffer);
        buffer.position(0);

        MessageType msgType = MessageType.fromByte(buffer.get());
        int msgLength = toInt(buffer.get());
        int msgId = toInt(buffer.get());
        log.debug("Received message of type {} with id {} and data length {}", msgType, msgId, msgLength);

        byte[] msgData = EMPTY_DATA;
        if (msgLength > 0) {
            msgData = new byte[msgLength];
            buffer.get(msgData);
            log.debug("Received data: {}", ByteUtils.toHexString(msgData));
        }

        int msgCrc = toInt(buffer.get());
        int calculatedCrc = toInt(calculateCrc(msgType, msgId, msgData));
        if (msgCrc != calculatedCrc) {
            log.error("Received message has invalid checksum. Value declared in message was {} but calculated crc is {}.", msgCrc, calculatedCrc);
            return;
        }

        // retrieve msg from db and update it
        Optional<Message> messageOptional = messageRepository.findLastByIpAndIdentifier(socketAddressToString(source), msgId);
        if (messageOptional.isPresent()) {
            Message message = messageOptional.get();

            message.setResponseReceive(LocalDateTime.now());
            message.setResponseType(msgType);
            message.setResponseData(msgData);
            messageRepository.save(message);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        datagramChannel = DatagramChannel.open();
        datagramChannel.socket().bind(new InetSocketAddress(serverUdpPort));

        datagramReceiveThread = new DatagramReceiveThread();
        datagramReceiveThread.start();
    }

    @Override
    public void destroy() {
        try {
            datagramChannel.close();
        } catch (IOException e) {
            log.error("Cannot close datagram channel.", e);
        }

        datagramReceiveThread.doStop();
    }

    private class DatagramReceiveThread extends Thread {
        private boolean isRunning = true;

        @Override
        public void run() {
            ByteBuffer buffer = ByteBuffer.allocateDirect(255 + MESSAGE_MIN_LEN);

            while (isRunning) {
                try {
                    buffer.position(0);
                    SocketAddress address = datagramChannel.receive(buffer);

                    threadPoolExecutor.execute(() -> receive(address, buffer));
                } catch (IOException e) {
                    log.error("Error: ", e);
                }
            }

        }

        public void doStop() {
            isRunning = false;

            try {
                join(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            if (isAlive()) {
                interrupt();
            }
        }
    }
}
