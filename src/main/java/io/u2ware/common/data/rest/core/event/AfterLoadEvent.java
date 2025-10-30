package io.u2ware.common.data.rest.core.event;

import org.springframework.data.rest.core.event.RepositoryEvent;

public class AfterLoadEvent extends RepositoryEvent {

    private static final long serialVersionUID = -6090615345948638970L;

    public AfterLoadEvent(Object source) {
        super(source);
    }
}