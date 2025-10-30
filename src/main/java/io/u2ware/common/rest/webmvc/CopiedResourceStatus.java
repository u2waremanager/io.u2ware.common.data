package io.u2ware.common.rest.webmvc;

import java.util.List;
import java.util.function.Supplier;

import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.rest.webmvc.HttpHeadersPreparer;
import org.springframework.data.rest.webmvc.PersistentEntityResource;
import org.springframework.data.rest.webmvc.RootResourceInformation;
import org.springframework.data.rest.webmvc.support.ETag;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;

public class CopiedResourceStatus {
    private static final String INVALID_DOMAIN_OBJECT = "Domain object %s is not an instance of the given PersistentEntity of type %s!";

    private final HttpHeadersPreparer preparer;

    public CopiedResourceStatus(HttpHeadersPreparer preparer) {

        Assert.notNull(preparer, "HttpHeadersPreparer must not be null!");

        this.preparer = preparer;
    }

    public static CopiedResourceStatus of(HttpHeadersPreparer preparer) {
        return new CopiedResourceStatus(preparer);
    }

    /**
     * Returns the {@link CopiedResourceStatus} calculated from the given {@link HttpHeaders}, domain object and
     * {@link RootResourceInformation}
     *
     * @param requestHeaders must not be {@literal null}.
     * @param domainObject must not be {@literal null}.
     * @param entity must not be {@literal null}.
     * @return
     */
    public StatusAndHeaders getStatusAndHeaders(HttpHeaders requestHeaders, Object domainObject,
                                                               PersistentEntity<?, ?> entity) {

        Assert.notNull(requestHeaders, "Request headers must not be null!");
        Assert.notNull(domainObject, "Domain object must not be null!");
        Assert.notNull(entity, "PersistentEntity must not be null!");
        Assert.isTrue(entity.getType().isInstance(domainObject),
                () -> String.format(INVALID_DOMAIN_OBJECT, domainObject, entity.getType()));

        // Check ETag for If-Non-Match

        List<String> ifNoneMatch = requestHeaders.getIfNoneMatch();
        ETag eTag = ifNoneMatch.isEmpty() ? ETag.NO_ETAG : ETag.from(ifNoneMatch.get(0));
        HttpHeaders responseHeaders = preparer.prepareHeaders(entity, domainObject);

        // Check last modification for If-Modified-Since

        return eTag.matches(entity, domainObject) || preparer.isObjectStillValid(domainObject, requestHeaders)
                ? StatusAndHeaders.notModified(responseHeaders)
                : StatusAndHeaders.modified(responseHeaders);
    }

    public static class StatusAndHeaders {

        private final HttpHeaders headers;
        private final boolean modified;

        private StatusAndHeaders(HttpHeaders headers, boolean modified) {

            Assert.notNull(headers, "HttpHeaders must not be null!");

            this.headers = headers;
            this.modified = modified;
        }

        boolean isModified() {
            return this.modified;
        }

        private static StatusAndHeaders notModified(HttpHeaders headers) {
            return new StatusAndHeaders(headers, false);
        }

        private static StatusAndHeaders modified(HttpHeaders headers) {
            return new StatusAndHeaders(headers, true);
        }

        /**
         * Creates a {@link ResponseEntity} based on the given {@link PersistentEntityResource}.
         *
         * @param supplier a {@link Supplier} to provide a {@link PersistentEntityResource} eventually, must not be
         *          {@literal null}.
         * @return
         */
        public ResponseEntity<EntityModel<?>> toResponseEntity(Supplier<PersistentEntityResource> supplier) {

            return modified //
                    ? new ResponseEntity<EntityModel<?>>(supplier.get(), headers, HttpStatus.OK) //
                    : new ResponseEntity<EntityModel<?>>(headers, HttpStatus.NOT_MODIFIED);
        }
    }
}
