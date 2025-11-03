
package io.u2ware.common.data.jpa.webmvc;


import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import io.u2ware.common.data.rest.core.annotation.HandleAfterRead;
import io.u2ware.common.data.rest.core.annotation.HandleBeforeRead;
import io.u2ware.common.data.rest.core.event.AfterReadEvent;
import io.u2ware.common.data.rest.core.event.BeforeReadEvent;
import io.u2ware.common.data.rest.core.event.CopiedAnnotatedEventHandlerInvoker;


public class RestfulJpaRepositoryEventHandlerInvoker extends CopiedAnnotatedEventHandlerInvoker{

	protected Log logger = LogFactory.getLog(getClass());


    @Override
    protected <T extends Annotation> void inspecAll(Object bean, Method method) {
        inspecAll(bean, method, HandleBeforeRead.class, BeforeReadEvent.class);
        // logger.info("@EnableRestfulJpaRepositories @HandleBeforeRead inspected");

        inspecAll(bean, method, HandleAfterRead.class, AfterReadEvent.class);
        // logger.info("@EnableRestfulJpaRepositories @HandleAfterRead inspected");
    }
}
