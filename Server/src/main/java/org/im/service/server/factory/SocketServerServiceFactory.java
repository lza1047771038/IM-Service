package org.im.service.server.factory;

import org.im.service.interfaces.RequestHandler;
import org.im.service.interfaces.SocketChannelDispatcher;
import org.im.service.message.queue.interfaces.MessageQueue;
import org.im.service.server.controller.NonBlockingSocketServerServiceImpl;
import org.im.service.interfaces.IEncryptor;
import org.im.service.interfaces.SocketServerService;

public class SocketServerServiceFactory {
    public static SocketServerService create(final String address, final int port, final SocketChannelDispatcher dispatcher) {
        return new NonBlockingSocketServerServiceImpl(address, port, dispatcher);
    }
}
