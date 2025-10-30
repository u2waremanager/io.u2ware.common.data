
package io.u2ware.common.data.rest.core.event;


import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import io.u2ware.common.data.rest.core.annotation.HandleAfterLoad;
import io.u2ware.common.data.rest.core.annotation.HandleAfterRead;
import io.u2ware.common.data.rest.core.annotation.HandleBeforeLoad;
import io.u2ware.common.data.rest.core.annotation.HandleBeforeRead;


public class RepositoryAdditionalEventHandlerInvoker extends CopiedAnnotatedEventHandlerInvoker{

    @Override
    protected <T extends Annotation> void inspecAll(Object bean, Method method) {
        inspecAll(bean, method, HandleBeforeRead.class, BeforeReadEvent.class);
        inspecAll(bean, method, HandleAfterRead.class, AfterReadEvent.class);


        inspecAll(bean, method, HandleBeforeLoad.class, BeforeLoadEvent.class);
        inspecAll(bean, method, HandleAfterLoad.class, AfterLoadEvent.class);


    }
}
