package io.u2ware.common.data.jpa.webmvc;

import java.io.Serializable;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.support.RepositoryInvoker;
import org.springframework.data.rest.core.mapping.ResourceMetadata;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.data.rest.webmvc.RootResourceInformation;
import org.springframework.data.rest.webmvc.support.BackendId;
import org.springframework.data.rest.webmvc.support.DefaultedPageable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.u2ware.common.data.jpa.repository.RestfulJpaRepository;
import io.u2ware.common.data.jpa.repository.query.MutableSpecification;
import io.u2ware.common.data.jpa.repository.query.PartTreeSpecification;
import io.u2ware.common.data.rest.core.event.AfterReadEvent;
import io.u2ware.common.data.rest.core.event.BeforeReadEvent;
import io.u2ware.common.data.rest.webmvc.CopiedAbstractRepositoryRestController;

@Configuration
@RepositoryRestController
public class RestfulJpaRepositoryController extends CopiedAbstractRepositoryRestController {

    protected Log logger = LogFactory.getLog(getClass());

    protected static final String SEARCH = "/search";
    protected static final String BASE_MAPPING = "/{repository}";


    // org.springframework.data.jpa.

    @Autowired
    private ObjectMapper mapper;

    @RequestMapping(value = BASE_MAPPING + "/{id}", method = RequestMethod.POST)
    public ResponseEntity<EntityModel<?>> getItemResource(RootResourceInformation resourceInformation,
                                                          @BackendId Serializable id, 
                                                          final PersistentEntityResourceAssembler assembler, 
                                                          @RequestHeader HttpHeaders headers)
            throws HttpRequestMethodNotSupportedException {

        Repository<?,?> repository = getRepositoryFor(resourceInformation);
        verifySupportedMethod(repository);

        Optional<Object> resource = executeFindOne(resourceInformation.getInvoker(), id);

        return resource.map(it -> {
            PersistentEntity<?, ?> entity = resourceInformation.getPersistentEntity();
            return getResourceStatus().getStatusAndHeaders(headers, it, entity).toResponseEntity(//
                    () -> assembler.toFullResource(it));

        }).orElseThrow(() -> new ResourceNotFoundException());
    }


    @ResponseBody
    @RequestMapping(value = BASE_MAPPING+SEARCH, method = RequestMethod.POST)
    public <T> CollectionModel<?> getCollectionResource(@QuerydslPredicate RootResourceInformation resourceInformation,
                                                        @RequestHeader(name = "partTree", required = false) String partTree,
                                                        @RequestHeader(name = "unpaged", required = false) boolean unpaged,
                                                        DefaultedPageable pageable,
                                                        Sort sort,
                                                        @RequestBody Map<String, Object> payload,
                                                        PersistentEntityResourceAssembler assembler)
            throws ResourceNotFoundException, HttpRequestMethodNotSupportedException {


        Repository<?,?> repository = getRepositoryFor(resourceInformation);

        verifySupportedMethod(repository);

        Object content = mapper.convertValue(payload, resourceInformation.getDomainType());

        Iterable<?> results = executeFindAll(repository, content, unpaged, pageable.getPageable(), sort, partTree);

        ResourceMetadata metadata = resourceInformation.getResourceMetadata();
        Optional<Link> baseLink = Optional.of(getDefaultSelfLink());

        return super.toCollectionModel(results, assembler, metadata.getDomainType(), baseLink);
    }





    protected void verifySupportedMethod(Repository<?, ?> repository) {
        // if(ClassUtils.isAssignableValue(JpaSpecificationExecutor.class, repository)) return;
        // if(ClassUtils.isAssignableValue(QuerydslPredicateExecutor.class, repository)) return;
        if(ClassUtils.isAssignableValue(RestfulJpaRepository.class, repository)) return;
        
        throw new ResourceNotFoundException();
    }



    protected Optional<Object> executeFindOne(RepositoryInvoker repository, Serializable id) {
        Optional<Object> result = repository.invokeFindById(id);
        if(result.isPresent()) {
            publisher.publishEvent(new AfterReadEvent(result.get(), id));
        }
        return result;
    }


    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected Iterable<?> executeFindAll(Repository repository, Object content, boolean unpaged,
                                       Pageable pageable, Sort sort, String partTree) {

        if(ClassUtils.isAssignableValue(QuerydslPredicateExecutor.class, repository)) {

            QuerydslPredicateExecutor executor = (QuerydslPredicateExecutor)repository;


            com.querydsl.core.BooleanBuilder predicate = new com.querydsl.core.BooleanBuilder();
            publisher.publishEvent(new BeforeReadEvent(content, predicate));

            if(unpaged) {
                return executor.findAll(predicate, sort) ;
            }else {
                return executor.findAll(predicate, pageable) ;
            }


        }else if(ClassUtils.isAssignableValue(JpaSpecificationExecutor.class, repository)) {


            JpaSpecificationExecutor executor = (JpaSpecificationExecutor)repository;

            Specification specification = null;
            if(StringUtils.hasText(partTree)) {
                specification = new PartTreeSpecification(partTree, content);
            }else {
                specification = new MutableSpecification();
                publisher.publishEvent(new BeforeReadEvent(content, specification));
            }

            if(unpaged) {
                return executor.findAll(specification, sort) ;
            }else {
                return executor.findAll(specification, pageable) ;
            }

        }else {
            throw new ResourceNotFoundException();
        }
    }





}
