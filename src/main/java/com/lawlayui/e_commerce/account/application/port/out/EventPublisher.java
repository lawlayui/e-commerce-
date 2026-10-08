package com.lawlayui.e_commerce.account.application.port.out;

public interface EventPublisher {
    void publish(Object event);
}
