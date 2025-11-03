package io.common.data.test.demo2;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.rest.core.annotation.*;
import org.springframework.stereotype.Component;

import io.u2ware.common.data.jpa.repository.query.JpaSpecificationBuilder;
import io.u2ware.common.data.rest.core.annotation.HandleAfterRead;
import io.u2ware.common.data.rest.core.annotation.HandleBeforeRead;

@Component
@RepositoryEventHandler
public class FooHandler {
 

    protected Log logger = LogFactory.getLog(getClass());


    @HandleBeforeRead
    public void HandleBeforeRead(Foo bar, Specification<Foo> spec){
        logger.info("HandleBeforeRead: " + bar);

        JpaSpecificationBuilder.of(Foo.class)
            .where()
                .and()
                    .eq("name", bar.getName())
        .build(spec);
    }


    @HandleAfterRead
    public void HandleAfterCreate(Foo bar){
        logger.info("HandleAfterCreate: " + bar);
    }




}
