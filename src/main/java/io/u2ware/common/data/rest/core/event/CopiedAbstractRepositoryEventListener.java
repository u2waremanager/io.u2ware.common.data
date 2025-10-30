package io.u2ware.common.data.rest.core.event;

import static org.springframework.core.GenericTypeResolver.resolveTypeArgument;

import org.springframework.context.ApplicationListener;
import org.springframework.data.rest.core.event.AbstractRepositoryEventListener;
import org.springframework.data.rest.core.event.AfterCreateEvent;
import org.springframework.data.rest.core.event.AfterDeleteEvent;
import org.springframework.data.rest.core.event.AfterLinkDeleteEvent;
import org.springframework.data.rest.core.event.AfterLinkSaveEvent;
import org.springframework.data.rest.core.event.AfterSaveEvent;
import org.springframework.data.rest.core.event.BeforeCreateEvent;
import org.springframework.data.rest.core.event.BeforeDeleteEvent;
import org.springframework.data.rest.core.event.BeforeLinkDeleteEvent;
import org.springframework.data.rest.core.event.BeforeLinkSaveEvent;
import org.springframework.data.rest.core.event.BeforeSaveEvent;
import org.springframework.data.rest.core.event.RepositoryEvent;

public abstract class CopiedAbstractRepositoryEventListener<T> implements ApplicationListener<RepositoryEvent> {

    private final Class<?> INTERESTED_TYPE = resolveTypeArgument(getClass(), AbstractRepositoryEventListener.class);

    /*
     * (non-Javadoc)
     * @see org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
     */
    @Override
    @SuppressWarnings({ "unchecked" })
    public final void onApplicationEvent(RepositoryEvent event) {

        Class<?> srcType = event.getSource().getClass();

        if (null != INTERESTED_TYPE && !INTERESTED_TYPE.isAssignableFrom(srcType)) {
            return;
        }

        if (event instanceof BeforeSaveEvent) {
            onBeforeSave((T) event.getSource());
        } else if (event instanceof BeforeCreateEvent) {
            onBeforeCreate((T) event.getSource());
        } else if (event instanceof AfterCreateEvent) {
            onAfterCreate((T) event.getSource());
        } else if (event instanceof AfterSaveEvent) {
            onAfterSave((T) event.getSource());
        } else if (event instanceof BeforeLinkSaveEvent) {
            onBeforeLinkSave((T) event.getSource(), ((BeforeLinkSaveEvent) event).getLinked());
        } else if (event instanceof AfterLinkSaveEvent) {
            onAfterLinkSave((T) event.getSource(), ((AfterLinkSaveEvent) event).getLinked());
        } else if (event instanceof BeforeLinkDeleteEvent) {
            onBeforeLinkDelete((T) event.getSource(), ((BeforeLinkDeleteEvent) event).getLinked());
        } else if (event instanceof AfterLinkDeleteEvent) {
            onAfterLinkDelete((T) event.getSource(), ((AfterLinkDeleteEvent) event).getLinked());
        } else if (event instanceof BeforeDeleteEvent) {
            onBeforeDelete((T) event.getSource());
        } else if (event instanceof AfterDeleteEvent) {
            onAfterDelete((T) event.getSource());
        }

        ///////////////////////////
        // Changed
        ///////////////////////////
        onApplicationEvents(event);
    }

    protected abstract void onApplicationEvents(RepositoryEvent event);



    /**
     * Override this method if you are interested in {@literal beforeCreate} events.
     *
     * @param entity The entity being created.
     */
    protected void onBeforeCreate(T entity) {}

    /**
     * Override this method if you are interested in {@literal afterCreate} events.
     *
     * @param entity The entity that was created.
     */
    protected void onAfterCreate(T entity) {}

    /**
     * Override this method if you are interested in {@literal beforeSave} events.
     *
     * @param entity The entity being saved.
     */
    protected void onBeforeSave(T entity) {}

    /**
     * Override this method if you are interested in {@literal afterSave} events.
     *
     * @param entity The entity that was just saved.
     */
    protected void onAfterSave(T entity) {}

    /**
     * Override this method if you are interested in {@literal beforeLinkSave} events.
     *
     * @param parent The parent entity to which the child object is linked.
     * @param linked The linked, child entity.
     */
    protected void onBeforeLinkSave(T parent, Object linked) {}

    /**
     * Override this method if you are interested in {@literal afterLinkSave} events.
     *
     * @param parent The parent entity to which the child object is linked.
     * @param linked The linked, child entity.
     */
    protected void onAfterLinkSave(T parent, Object linked) {}

    /**
     * Override this method if you are interested in {@literal beforeLinkDelete} events.
     *
     * @param parent The parent entity to which the child object is linked.
     * @param linked The linked, child entity.
     */
    protected void onBeforeLinkDelete(T parent, Object linked) {}

    /**
     * Override this method if you are interested in {@literal afterLinkDelete} events.
     *
     * @param parent The parent entity to which the child object is linked.
     * @param linked The linked, child entity.
     */
    protected void onAfterLinkDelete(T parent, Object linked) {}

    /**
     * Override this method if you are interested in {@literal beforeDelete} events.
     *
     * @param entity The entity that is being deleted.
     */
    protected void onBeforeDelete(T entity) {}

    /**
     * Override this method if you are interested in {@literal afterDelete} events.
     *
     * @param entity The entity that was just deleted.
     */
    protected void onAfterDelete(T entity) {}

}
