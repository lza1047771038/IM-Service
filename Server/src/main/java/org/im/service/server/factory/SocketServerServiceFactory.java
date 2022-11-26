package org.im.service.server.factory;

import org.im.service.interfaces.RequestHandler;
import org.im.service.message.queue.interfaces.MessageQueue;
import org.im.service.server.controller.NonBlockingSocketServerServiceImpl;
import org.im.service.interfaces.IEncryptor;
import org.im.service.interfaces.SocketServerService;

public class SocketServerServiceFactory {
    public static SocketServerService create(final int port, final IEncryptor encryptor, final RequestHandler requestHandler , final MessageQueue messageQueue) {
        return new NonBlockingSocketServerServiceImpl(port, encryptor, requestHandler, messageQueue);
    }
}
