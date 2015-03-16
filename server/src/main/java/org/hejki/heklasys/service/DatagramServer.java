package org.hejki.heklasys.service;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import lombok.extern.slf4j.Slf4j;
import org.hejki.heklasys.config.AppSettings;
import org.hejki.heklasys.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.Reactor;
import reactor.event.Event;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;

/**
 * TODO Document me.
 *
 * @author Petr Hejkal <petr.hejkal@doxologic.com>
 */
@Slf4j
@Service
public class DatagramServer {
    @Autowired
    private AppSettings appSettings;

    @Autowired
    private Reactor reactor;

    private NioEventLoopGroup eventLoopGroup;
    private Channel channel;

    public void writeAndFlush(Message message, InetSocketAddress address) {
        channel.writeAndFlush(new DatagramPacket(
                Unpooled.wrappedBuffer(message.toRaw()),
                address
        ));
    }

    @PostConstruct
    private void startServer() {
        eventLoopGroup = new  NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();

            bootstrap.group(eventLoopGroup)
                    .channel(NioDatagramChannel.class)
                    .handler(new DatagramChannelHandler());

            channel = bootstrap.bind(appSettings.getUdpPort()).sync().channel();
            log.debug("DatagramServer bind to port={}", appSettings.getUdpPort());
        } catch (Exception e) {
            log.error("DatagramServer channel error.", e);
        }
    }

    @PreDestroy
    private void stopServer() {
        log.debug("Shutdown DatagramServer.");
        eventLoopGroup.shutdownGracefully();
    }

    private class DatagramChannelHandler extends SimpleChannelInboundHandler<DatagramPacket> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
            try {
                log.trace("Receive datagram packet={}", msg);
                Message message = Message.from(msg.content().nioBuffer());
                Event<Message> event = Event.wrap(message);

                if (msg.sender() != null) {
                    event.getHeaders().setOrigin(msg.sender().getAddress().getHostAddress());
                }

                log.debug("Publish message={}", message);
                reactor.notify(message.getClass(), event);
            } catch (Exception e) {
                log.error("Cannot convert message from buffer.", e);
            }
        }
    }
}