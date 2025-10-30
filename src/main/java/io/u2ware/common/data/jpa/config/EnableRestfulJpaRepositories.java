package io.u2ware.common.data.jpa.config;

import org.springframework.context.annotation.Import;

import io.u2ware.common.data.jpa.webmvc.RestfulJpaRepositoryController;
import io.u2ware.common.data.jpa.webmvc.RestfulJpaRepositoryEventHandlerInvoker;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({RestfulJpaRepositoryEventHandlerInvoker.class, RestfulJpaRepositoryController.class})
public @interface EnableRestfulJpaRepositories {


}
