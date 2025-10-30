package io.u2ware.common.data.jpa.repository.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.CopiedPredicateBuilder;
import org.springframework.data.repository.query.parser.Part;
import org.springframework.data.repository.query.parser.PartTree;
import org.springframework.data.util.DirectFieldAccessFallbackBeanWrapper;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class PartTreeSpecification<T> implements Specification<T> {

    private final String partTreeSource;
    private Collection<Object> parameters;
    // private T example;

    public PartTreeSpecification(String partTreeSource, Collection<Object> parameters){
        this.partTreeSource = partTreeSource;
        this.parameters = parameters;
    }

    public PartTreeSpecification(String partTreeSource, Object... parameters){
        this.partTreeSource = partTreeSource;
        this.parameters = Arrays.asList(parameters);
    }

    public PartTreeSpecification(String partTreeSource, T entity){
        this.partTreeSource = partTreeSource;

        ArrayList<Object> params = new ArrayList<>();
        DirectFieldAccessFallbackBeanWrapper beanWrapper = new DirectFieldAccessFallbackBeanWrapper(entity);
        PartTree tree = new PartTree(partTreeSource, entity.getClass());
        for (PartTree.OrPart node : tree) {
            Iterator<Part> parts = node.iterator();
            while (parts.hasNext()) {
                Part part = parts.next();
                String name = part.getProperty().getSegment();
                Object value = beanWrapper.getPropertyValue(name);
                params.add(value);
            }
        }
        this.parameters = params;
    }


    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

        PartTree tree = new PartTree(partTreeSource, root.getJavaType());

        Predicate base = null;

        for (PartTree.OrPart node : tree) {

            Iterator<Part> parts = node.iterator();

            if (!parts.hasNext()) {
                throw new IllegalStateException(String.format("No part found in PartTree %s", tree));
            }

            Predicate criteria = create(root, query, builder, parts.next());

            while (parts.hasNext()) {
                criteria = and(root, query, builder, parts.next(), criteria);
            }
            base = base == null ? criteria : or(root, query, builder, base, criteria);
        }

        return base;
    }

    protected Predicate create(Root<T> r, CriteriaQuery<?> q, CriteriaBuilder b,  Part part) {
        return toPredicate(r,q,b, part);
    }

    protected Predicate and(Root<T> r, CriteriaQuery<?> q, CriteriaBuilder b, Part part, Predicate base) {
        return b.and(base, this.toPredicate(r,q,b, part));
    }

    protected Predicate or(Root<T> r, CriteriaQuery<?> q, CriteriaBuilder b, Predicate base, Predicate criteria) {
        return b.or(base, criteria);
    }

    protected Predicate toPredicate(Root<T> r, CriteriaQuery<?> q, CriteriaBuilder b, Part part) {
        return new CopiedPredicateBuilder(r,q,b, part, parameters.iterator()).build();
    }

}


