package org.hejki.heklasys.utils;

import org.springframework.util.Assert;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * TODO Document me.
 *
 * @author Petr Hejkal
 */
public class NetworkUtils {

    public static String socketAddressToString(SocketAddress address) {
        Assert.notNull(address);

        if (address instanceof InetSocketAddress) {
            return ((InetSocketAddress) address).getAddress().getHostAddress();
        }
        throw new IllegalArgumentException("Given address type is not supported. Type is " + address.getClass());
    }
}
