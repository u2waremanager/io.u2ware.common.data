package io.u2ware.common.data.rest.core.event;

import org.springframework.data.rest.core.event.RepositoryEvent;

public class RepositoryAdditionalEventListener<T> extends CopiedAbstractRepositoryEventListener<T> {

    @Override
    @SuppressWarnings({ "unchecked" })
    protected void onApplicationEvents(RepositoryEvent event) {
        if(event instanceof AfterReadEvent) {
            onAfterRead((T) event.getSource(), ((AfterReadEvent) event).getLinked());
        } else if (event instanceof BeforeReadEvent) {
            onBeforeRead((T) event.getSource(), ((BeforeReadEvent) event).getLinked());

        } else if (event instanceof AfterLoadEvent) {
            onAfterLoad((T) event.getSource());

        } else if (event instanceof BeforeLoadEvent) {
            onBeforeLoad((T) event.getSource());
        }
    }
    protected <X> void onAfterRead(T entity, X linked) {}
    protected <X> void onBeforeRead(T entity, X linked) {}

    protected void onAfterLoad(T entity) {}
    protected void onBeforeLoad(T entity) {}

}
