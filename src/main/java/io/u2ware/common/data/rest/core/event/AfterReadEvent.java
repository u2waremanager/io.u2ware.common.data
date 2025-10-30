package io.u2ware.common.data.rest.core.event;

import org.springframework.data.rest.core.event.LinkedEntityEvent;

public class AfterReadEvent extends LinkedEntityEvent {

    private static final long serialVersionUID = -6090615345948638970L;

    public AfterReadEvent(Object source, Object linked) {
        super(source, linked);
    }
}