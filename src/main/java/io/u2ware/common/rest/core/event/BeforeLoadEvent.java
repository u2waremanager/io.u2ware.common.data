package io.u2ware.common.rest.core.event;

import org.springframework.data.rest.core.event.RepositoryEvent;

public class BeforeLoadEvent extends RepositoryEvent {

    private static final long serialVersionUID = -6090615345948638970L;

    public BeforeLoadEvent(Object source) {
        super(source);
    }
}