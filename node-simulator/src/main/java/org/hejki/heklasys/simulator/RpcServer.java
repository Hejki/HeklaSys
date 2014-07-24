package org.hejki.heklasys.simulator;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class RpcServer extends Thread {
    private boolean runnig;
    private int port;

    public RpcServer(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        runnig = true;

        try (DatagramChannel channel = DatagramChannel.open()) {

            channel.socket().bind(new InetSocketAddress(port));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
