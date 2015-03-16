//package org.hejki.heklasys.service;
//
//import lombok.extern.slf4j.Slf4j;
//import org.hejki.heklasys.config.AppSettings;
//import org.hejki.heklasys.model.Message;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import reactor.core.Environment;
//import reactor.core.Reactor;
//import reactor.event.Event;
//import reactor.function.Consumer;
//import reactor.function.Function;
//import reactor.io.Buffer;
//import reactor.net.netty.udp.NettyDatagramServer;
//import reactor.net.udp.DatagramServer;
//import reactor.net.udp.spec.DatagramServerSpec;
//
//import javax.annotation.PostConstruct;
//import javax.annotation.PreDestroy;
//
///**
// * TODO Document me.
// *
// * @author Petr Hejkal
// */
//@Slf4j
//@Service
//public class DatagramListenerServiceImpl {
//
//    @Autowired
//    private Reactor reactor;
//
//    @Autowired
//    private AppSettings appSettings;
//
//    private DatagramServer<Message, Message> server;
//
//    @PostConstruct
//    public void init() {
//        DatagramServerSpec<Message, Message> spec = new DatagramServerSpec<>(NettyDatagramServer.class);
//        spec.env(new Environment());
//        spec.codec(new MessageCodec());
//        spec.listen(appSettings.getUdpPort());
//        spec.consume(connection -> {
//            connection.in().consume(message -> {
//
//                Event<Message> event = Event.wrap(message);
//
//                //                event.getHeaders().setOrigin(connection);
//                System.out.println(connection);
//                log.debug("Publish message={}", message);
//                reactor.notify(message.getClass(), event);
//            });
//        });
//
//        server = spec.get();
//        server.start();
//        log.info("UDP server started. Listen on port={}", appSettings.getUdpPort());
//    }
//
//    @PreDestroy
//    public void destroy() {
//        server.shutdown();
//    }
//}
//
//class MessageCodec implements reactor.io.encoding.Codec<Buffer, Message, Message> {
//    private static final Logger log = LoggerFactory.getLogger(MessageCodec.class);
//
//    @Override
//    public Function<Buffer, Message> decoder(Consumer<Message> next) {
//        return buffer -> {
//            try {
//                Message message = Message.from(buffer.byteBuffer());
//                if (null != next) {
//                    next.accept(message);
//                    return null;
//                }
//                return message;
//            } catch (Exception e) {
//                log.error("Cannot convert message from buffer, skip={} bytes.", buffer.remaining(), e);
//                buffer.skip(buffer.remaining());
//            }
//            return null;
//        };
//    }
//
//    @Override
//    public Function<Message, Buffer> encoder() {
//        return null;
//    }
//}