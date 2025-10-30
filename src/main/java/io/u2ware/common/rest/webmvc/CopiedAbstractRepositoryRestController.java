package io.u2ware.common.rest.webmvc;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.support.Repositories;
import org.springframework.data.rest.core.mapping.ResourceMetadata;
import org.springframework.data.rest.webmvc.HttpHeadersPreparer;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.data.rest.webmvc.RootResourceInformation;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.core.EmbeddedWrappers;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * @author Jon Brisbin
 * @author Oliver Gierke
 * @author Thibaud Lepretre
 */
@SuppressWarnings({ "rawtypes" })
public class CopiedAbstractRepositoryRestController {

    private static final EmbeddedWrappers WRAPPERS = new EmbeddedWrappers(false);

    protected @Autowired ApplicationEventPublisher publisher;

    protected @Autowired PagedResourcesAssembler<Object> pagedResourcesAssembler;
    protected @Autowired HttpHeadersPreparer headersPreparer;
    protected @Autowired Repositories repositories;

    private CopiedResourceStatus resourceStatus;

    @SuppressWarnings("unchecked")
    protected <R> R getRepositoryFor(RootResourceInformation information) {
        return repositories.getRepositoryFor(information.getDomainType()).map(repository -> {
            if (repository == null) {
                throw new ResourceNotFoundException("repository is not Found: "+information);
            }
            return (R)repository;
        }).orElseThrow(() -> new ResourceNotFoundException("repository is not Found: "+information));
    }

    protected CopiedResourceStatus getResourceStatus(){
        if(resourceStatus == null){
            resourceStatus = new CopiedResourceStatus(headersPreparer);
        }
        return resourceStatus;
    }

//    /**
//     * Creates a new {@link org.springframework.data.rest.webmvc} for the given {@link PagedResourcesAssembler} and
//     * {@link AuditableBeanWrapperFactory}.
//     *
//     * @param pagedResourcesAssembler must not be {@literal null}.
//     */
//    public CopiedAbstractRepositoryRestController(PagedResourcesAssembler<Object> pagedResourcesAssembler) {
//
//        Assert.notNull(pagedResourcesAssembler, "PagedResourcesAssembler must not be null!");
//
//        this.pagedResourcesAssembler = pagedResourcesAssembler;
//    }

    protected Link resourceLink(RootResourceInformation resourceLink, EntityModel resource) {

        ResourceMetadata repoMapping = resourceLink.getResourceMetadata();

        Link selfLink = resource.getRequiredLink(IanaLinkRelations.SELF);
        LinkRelation rel = repoMapping.getItemResourceRel();

        return Link.of(selfLink.getHref(), rel);
    }

    @SuppressWarnings({ "unchecked" })
    protected CollectionModel<?> toCollectionModel(Iterable<?> source, PersistentEntityResourceAssembler assembler,
                                                   Class<?> domainType, Optional<Link> baseLink) {

        if (source instanceof Page) {
            Page<Object> page = (Page<Object>) source;
            return entitiesToResources(page, assembler, domainType, baseLink);
        } else if (source instanceof Iterable) {
            return entitiesToResources((Iterable<Object>) source, assembler, domainType);
        } else {
            return CollectionModel.empty();
        }
    }

    protected CollectionModel<?> entitiesToResources(Page<Object> page, PersistentEntityResourceAssembler assembler,
                                                     Class<?> domainType, Optional<Link> baseLink) {

        if (page.getContent().isEmpty()) {
            return baseLink.<PagedModel<?>> map(it -> pagedResourcesAssembler.toEmptyModel(page, domainType, it))//
                    .orElseGet(() -> pagedResourcesAssembler.toEmptyModel(page, domainType));
        }

        return baseLink.map(it -> pagedResourcesAssembler.toModel(page, assembler, it))//
                .orElseGet(() -> pagedResourcesAssembler.toModel(page, assembler));
    }

    protected CollectionModel<?> entitiesToResources(Iterable<Object> entities,
                                                     PersistentEntityResourceAssembler assembler, Class<?> domainType) {

        if (!entities.iterator().hasNext()) {

            List<Object> content = Arrays.<Object> asList(WRAPPERS.emptyCollectionOf(domainType));
            return CollectionModel.of(content, getDefaultSelfLink());
        }

        List<EntityModel<Object>> resources = new ArrayList<EntityModel<Object>>();

        for (Object obj : entities) {
            resources.add(obj == null ? null : assembler.toModel(obj));
        }

        return CollectionModel.of(resources, getDefaultSelfLink());
    }

    protected Link getDefaultSelfLink() {
        return Link.of(ServletUriComponentsBuilder.fromCurrentRequest().build().toUriString());
    }
}
