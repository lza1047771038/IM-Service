package org.im.service.common;

import org.im.service.log.LoggerImplKt;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceFacade {

    private static final Map<Class<?>, Object> serviceImplementationMap = new ConcurrentHashMap<>();

    public static <T, R extends T> void put(final Class<T> interfaceKey, final R impl) {
        if (serviceImplementationMap.containsKey(interfaceKey)) {
            final Object existImpl = serviceImplementationMap.get(interfaceKey);
            if (existImpl == null) {
                serviceImplementationMap.put(interfaceKey, impl);
            } else {
                LoggerImplKt.getLogger().log("ServiceFacade", "current impl: " + existImpl.getClass().getName() + " in ServiceFacade will be replaced with " + impl.getClass().getName() + ".");
                serviceImplementationMap.put(interfaceKey, impl);
            }
        } else {
            serviceImplementationMap.put(interfaceKey, impl);
        }
    }

    @NotNull
    public static <T> T get(final Class<T> interfaceKey) {
        final Object nowExistImpl = serviceImplementationMap.get(interfaceKey);
        if (nowExistImpl == null) {
            throw new NullPointerException("impl for interface: " + interfaceKey.getName() + " is not initialized, please call ServiceFacade#put to add your own implementation.");
        }
        return (T) nowExistImpl;
    }
}
