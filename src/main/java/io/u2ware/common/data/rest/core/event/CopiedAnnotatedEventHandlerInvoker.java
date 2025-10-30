package io.u2ware.common.data.rest.core.event;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.data.rest.core.event.AnnotatedEventHandlerInvoker;
import org.springframework.data.rest.core.event.LinkedEntityEvent;
import org.springframework.data.rest.core.event.RepositoryEvent;
import org.springframework.data.util.ProxyUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ReflectionUtils;

/**
 * Component to discover annotated repository event handlers and trigger them on {@link ApplicationEvent}s.
 *
 * @author Jon Brisbin
 * @author Oliver Gierke
 */
public abstract class CopiedAnnotatedEventHandlerInvoker implements ApplicationListener<RepositoryEvent>, BeanPostProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(AnnotatedEventHandlerInvoker.class);
    private static final String PARAMETER_MISSING = "Invalid event handler method %s! At least a single argument is required to determine the domain type for which you are interested in events.";

    private final MultiValueMap<Class<? extends RepositoryEvent>, EventHandlerMethod> handlerMethods = new LinkedMultiValueMap<Class<? extends RepositoryEvent>, EventHandlerMethod>();

    /*
     * (non-Javadoc)
     * @see org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
     */
    @Override
    public void onApplicationEvent(RepositoryEvent event) {

        Class<? extends RepositoryEvent> eventType = event.getClass();

        if (!handlerMethods.containsKey(eventType)) {
            return;
        }

        for (EventHandlerMethod handlerMethod : handlerMethods.get(eventType)) {

            Object src = event.getSource();

            if (!ClassUtils.isAssignable(handlerMethod.targetType, src.getClass())) {
                continue;
            }

            List<Object> parameters = new ArrayList<Object>();
            parameters.add(src);

            if (event instanceof LinkedEntityEvent) {
                parameters.add(((LinkedEntityEvent) event).getLinked());
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("Invoking {} handler for {}.", event.getClass().getSimpleName(), event.getSource());
            }

            ReflectionUtils.invokeMethod(handlerMethod.method, handlerMethod.handler, parameters.toArray());
        }
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.beans.factory.config.BeanPostProcessor#postProcessBeforeInitialization(java.lang.Object, java.lang.String)
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.beans.factory.config.BeanPostProcessor#postProcessAfterInitialization(java.lang.Object, java.lang.String)
     */
    @Override
    public Object postProcessAfterInitialization(final Object bean, String beanName) throws BeansException {

        Class<?> beanType = ProxyUtils.getUserClass(bean);
        RepositoryEventHandler typeAnno = AnnotationUtils.findAnnotation(beanType, RepositoryEventHandler.class);

        if (typeAnno == null) {
            return bean;
        }

        for (Method method : ReflectionUtils.getUniqueDeclaredMethods(beanType)) {
            ////////////////////////////////
            // Chang Start !!!
            ////////////////////////////////
//            inspect(bean, method, HandleBeforeCreate.class, BeforeCreateEvent.class);
//            inspect(bean, method, HandleAfterCreate.class, AfterCreateEvent.class);
//            inspect(bean, method, HandleBeforeSave.class, BeforeSaveEvent.class);
//            inspect(bean, method, HandleAfterSave.class, AfterSaveEvent.class);
//            inspect(bean, method, HandleBeforeLinkSave.class, BeforeLinkSaveEvent.class);
//            inspect(bean, method, HandleAfterLinkSave.class, AfterLinkSaveEvent.class);
//            inspect(bean, method, HandleBeforeDelete.class, BeforeDeleteEvent.class);
//            inspect(bean, method, HandleAfterDelete.class, AfterDeleteEvent.class);
//            inspect(bean, method, HandleBeforeLinkDelete.class, BeforeLinkDeleteEvent.class);
//            inspect(bean, method, HandleAfterLinkDelete.class, AfterLinkDeleteEvent.class);
            inspecAll(bean, method);
        }

        return bean;
    }

    protected abstract <T extends Annotation> void inspecAll(Object bean, Method method);

    protected <T extends Annotation> void inspecAll(Object handler, Method method, Class<T> annotationType,
                                                    Class<? extends RepositoryEvent> eventType) {
        this.inspect(handler,method,annotationType,eventType);
    }
    ////////////////////////////////
    // Change End!!!
    ////////////////////////////////


    /**
     * Inspects the given handler method for an annotation of the given type. If the annotation present an
     * {@link EventHandlerMethod} is registered for the given {@link RepositoryEvent} type.
     *
     * @param handler must not be {@literal null}.
     * @param method must not be {@literal null}.
     * @param annotationType must not be {@literal null}.
     * @param eventType must not be {@literal null}.
     */
    private <T extends Annotation> void inspect(Object handler, Method method, Class<T> annotationType,
                                                Class<? extends RepositoryEvent> eventType) {

        T annotation = AnnotationUtils.findAnnotation(method, annotationType);

        if (annotation == null) {
            return;
        }

        if (method.getParameterCount() == 0) {
            throw new IllegalStateException(String.format(PARAMETER_MISSING, method));
        }

        ResolvableType parameter = ResolvableType.forMethodParameter(method, 0, handler.getClass());
        EventHandlerMethod handlerMethod = EventHandlerMethod.of(parameter.resolve(), handler, method);

        if (LOG.isDebugEnabled()) {
            LOG.debug("Annotated handler method found: {}", handlerMethod);
        }

        List<EventHandlerMethod> events = handlerMethods.get(eventType);

        if (events == null) {
            events = new ArrayList<EventHandlerMethod>();
        }

        if (events.isEmpty()) {
            handlerMethods.add(eventType, handlerMethod);
            return;
        }

        events.add(handlerMethod);
        Collections.sort(events);
        handlerMethods.put(eventType, events);
    }

    static class EventHandlerMethod implements Comparable<EventHandlerMethod> {

        final Class<?> targetType;
        final Method method;
        final Object handler;

        public EventHandlerMethod(Class<?> targetType, Method method, Object handler) {

            Assert.notNull(targetType, "Target type must not be null!");
            Assert.notNull(method, "Method must not be null!");
            Assert.notNull(handler, "Handler must not be null!");

            this.targetType = targetType;
            this.method = method;
            this.handler = handler;
        }

        public static EventHandlerMethod of(Class<?> targetType, Object handler, Method method) {

            ReflectionUtils.makeAccessible(method);

            return new EventHandlerMethod(targetType, method, handler);
        }

        /*
         * (non-Javadoc)
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        @Override
        public int compareTo(EventHandlerMethod o) {
            return AnnotationAwareOrderComparator.INSTANCE.compare(this.method, o.method);
        }

        /*
         * (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(final Object o) {

            if (o == this) {
                return true;
            }

            if (!(o instanceof EventHandlerMethod)) {
                return false;
            }

            EventHandlerMethod other = (EventHandlerMethod) o;

            return Objects.equals(targetType, other.targetType) //
                    && Objects.equals(method, other.method) //
                    && Objects.equals(handler, other.handler);
        }

        /*
         * (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            return Objects.hash(targetType, method, handler);
        }

        /*
         * (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "AnnotatedEventHandlerInvoker.EventHandlerMethod(targetType=" + this.targetType + ", method=" + this.method
                    + ", handler=" + this.handler + ")";
        }
    }
}
