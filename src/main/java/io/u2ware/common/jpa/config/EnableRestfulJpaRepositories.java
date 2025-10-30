package io.u2ware.common.jpa.config;

import io.u2ware.common.jpa.webmvc.RestfulJpaRepositoryController;
import io.u2ware.common.jpa.webmvc.RestfulJpaRepositoryEventHandlerInvoker;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({RestfulJpaRepositoryEventHandlerInvoker.class, RestfulJpaRepositoryController.class})
public @interface EnableRestfulJpaRepositories {


}
