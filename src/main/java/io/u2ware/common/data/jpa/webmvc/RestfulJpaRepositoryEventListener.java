package io.u2ware.common.data.jpa.webmvc;

import org.springframework.data.rest.core.event.RepositoryEvent;

import io.u2ware.common.data.rest.core.event.AfterReadEvent;
import io.u2ware.common.data.rest.core.event.BeforeReadEvent;
import io.u2ware.common.data.rest.core.event.CopiedAbstractRepositoryEventListener;

public class  RestfulJpaRepositoryEventListener<T> extends CopiedAbstractRepositoryEventListener<T> {

    @Override
    @SuppressWarnings({ "unchecked" })
    protected void onApplicationEvents(RepositoryEvent event) {
        if(event instanceof AfterReadEvent) {
            onAfterRead((T) event.getSource(), ((AfterReadEvent) event).getLinked());
        } else if (event instanceof BeforeReadEvent) {
            onBeforeRead((T) event.getSource(), ((BeforeReadEvent) event).getLinked());
        }
    }
    protected <X> void onAfterRead(T entity, X linked) {}
    protected <X> void onBeforeRead(T entity, X linked) {}

}
