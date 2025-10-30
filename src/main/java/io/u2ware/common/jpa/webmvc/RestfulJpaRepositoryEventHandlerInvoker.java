
package io.u2ware.common.jpa.webmvc;


import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import io.u2ware.common.rest.core.annotation.HandleAfterRead;
import io.u2ware.common.rest.core.annotation.HandleBeforeRead;
import io.u2ware.common.rest.core.event.AfterReadEvent;
import io.u2ware.common.rest.core.event.BeforeReadEvent;
import io.u2ware.common.rest.core.event.CopiedAnnotatedEventHandlerInvoker;


public class RestfulJpaRepositoryEventHandlerInvoker extends CopiedAnnotatedEventHandlerInvoker{

    @Override
    protected <T extends Annotation> void inspecAll(Object bean, Method method) {
        inspecAll(bean, method, HandleBeforeRead.class, BeforeReadEvent.class);
        inspecAll(bean, method, HandleAfterRead.class, AfterReadEvent.class);
    }
}
