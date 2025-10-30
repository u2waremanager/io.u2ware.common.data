package org.springframework.data.jpa.repository.query;

import java.util.Collection;
import java.util.Iterator;

import org.springframework.data.mapping.PropertyPath;
import org.springframework.data.repository.query.parser.Part;
import org.springframework.util.Assert;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class CopiedPredicateBuilder {

    private final Root<?> root;
    private final CriteriaQuery<?> query;
    private final CriteriaBuilder builder;
    private final Part part;
    private final Iterator<Object> iterator;

    public CopiedPredicateBuilder(Root<?> root, CriteriaQuery<?> query, CriteriaBuilder builder, Part part, Iterator<Object> iterator ) {
        Assert.notNull(part, "Part must not be null");
        Assert.notNull(root, "Root must not be null");
        this.root = root;
        this.query = query;
        this.builder = builder;
        this.part = part;
        this.iterator = iterator;
    }

    public Predicate build() {
        PropertyPath property = this.part.getProperty();
        Part.Type type = this.part.getType();
        Expression propertyExpressionx;
        switch (type) {
            case BETWEEN:
//                ParameterMetadataProvider.ParameterMetadata<Comparable> first = JpaQueryCreator.this.provider.next(this.part);
//                ParameterMetadataProvider.ParameterMetadata<Comparable> second = JpaQueryCreator.this.provider.next(this.part);
//                return JpaQueryCreator.this.builder.between(this.getComparablePath(this.root, this.part), first.getExpression(), second.getExpression());
                return builder.between(this.getComparablePath(this.root, this.part), next(), next());
            case IS_NOT_NULL:
                return this.getTypedPath(this.root, this.part).isNotNull();
            case IS_NULL:
                return this.getTypedPath(this.root, this.part).isNull();
            case LESS_THAN:
            case BEFORE:
//                return JpaQueryCreator.this.builder.lessThan(this.getComparablePath(this.root, this.part), JpaQueryCreator.this.provider.next(this.part, Comparable.class).getExpression());
                return builder.lessThan(this.getComparablePath(this.root, this.part), next());
            case LESS_THAN_EQUAL:
//                return JpaQueryCreator.this.builder.lessThanOrEqualTo(this.getComparablePath(this.root, this.part), JpaQueryCreator.this.provider.next(this.part, Comparable.class).getExpression());
                return builder.lessThanOrEqualTo(this.getComparablePath(this.root, this.part), next());
            case GREATER_THAN:
            case AFTER:
//                return JpaQueryCreator.this.builder.greaterThan(this.getComparablePath(this.root, this.part), JpaQueryCreator.this.provider.next(this.part, Comparable.class).getExpression());
                return builder.greaterThan(this.getComparablePath(this.root, this.part), next());
            case GREATER_THAN_EQUAL:
//                return JpaQueryCreator.this.builder.greaterThanOrEqualTo(this.getComparablePath(this.root, this.part), JpaQueryCreator.this.provider.next(this.part, Comparable.class).getExpression());
                return builder.greaterThanOrEqualTo(this.getComparablePath(this.root, this.part), next());
            case NOT_LIKE:
            case LIKE:
                break;
            case STARTING_WITH:
            case ENDING_WITH:
            case NOT_CONTAINING:
            case CONTAINING:
                if (property.getLeafProperty().isCollection()) {
                    propertyExpressionx = this.traversePath(this.root, property);
//                    ParameterExpression<Object> parameterExpression = JpaQueryCreator.this.provider.next(this.part).getExpression();
//                    return type.equals(Part.Type.NOT_CONTAINING) ? this.isNotMember(JpaQueryCreator.this.builder, parameterExpression, propertyExpressionx) : this.isMember(JpaQueryCreator.this.builder, parameterExpression, propertyExpressionx);
                    return type.equals(Part.Type.NOT_CONTAINING) ? this.isNotMember(builder, next(), propertyExpressionx) : this.isMember(builder, next(), propertyExpressionx);
                }
                break;
            case IS_NOT_EMPTY:
            case IS_EMPTY:
                if (!property.getLeafProperty().isCollection()) {
                    throw new IllegalArgumentException("IsEmpty / IsNotEmpty can only be used on collection properties");
                }

//                Expression<Collection<Object>> collectionPath = this.traversePath(this.root, property);
//                return type.equals(Part.Type.IS_NOT_EMPTY) ? JpaQueryCreator.this.builder.isNotEmpty(collectionPath) : JpaQueryCreator.this.builder.isEmpty(collectionPath);
                return type.equals(Part.Type.IS_NOT_EMPTY) ? builder.isNotEmpty(this.traversePath(this.root, property)) : builder.isEmpty(this.traversePath(this.root, property));
            case NOT_IN:
//                return this.upperIfIgnoreCase(this.getTypedPath(this.root, this.part)).in(JpaQueryCreator.this.provider.next(this.part, Collection.class).getExpression()).not();
                return this.upperIfIgnoreCase(this.getTypedPath(this.root, this.part)).in((Collection<?>) next(true)).not();
            case IN:
//                return this.upperIfIgnoreCase(this.getTypedPath(this.root, this.part)).in(JpaQueryCreator.this.provider.next(this.part, Collection.class).getExpression());
                return this.upperIfIgnoreCase(this.getTypedPath(this.root, this.part)).in((Collection<?>)next(true));
            case NEAR:
            case WITHIN:
            case REGEX:
            case EXISTS:
            default:
                throw new IllegalArgumentException("Unsupported keyword " + String.valueOf(type));
            case TRUE:
//                Expression<Boolean> truePath = this.getTypedPath(this.root, this.part);
//                return JpaQueryCreator.this.builder.isTrue(truePath);
                return builder.isTrue(this.getTypedPath(this.root, this.part));
            case FALSE:
//                Expression<Boolean> falsePath = this.getTypedPath(this.root, this.part);
//                return JpaQueryCreator.this.builder.isFalse(falsePath);
                return builder.isFalse(this.getTypedPath(this.root, this.part));
            case NEGATING_SIMPLE_PROPERTY:
//                return JpaQueryCreator.this.builder.notEqual(this.upperIfIgnoreCase(this.getTypedPath(this.root, this.part)), this.upperIfIgnoreCase(JpaQueryCreator.this.provider.next(this.part).getExpression()));
                return builder.notEqual(this.upperIfIgnoreCase(this.getTypedPath(this.root, this.part)), this.upperIfIgnoreCase(next()));
            case SIMPLE_PROPERTY:
//                ParameterMetadataProvider.ParameterMetadata<Object> expression = JpaQueryCreator.this.provider.next(this.part);
//                Expression<Object> path = this.getTypedPath(this.root, this.part);
//                return expression.isIsNullParameter() ? path.isNull() : JpaQueryCreator.this.builder.equal(this.upperIfIgnoreCase(path), this.upperIfIgnoreCase(expression.getExpression()));
                return builder.equal(this.upperIfIgnoreCase(this.getTypedPath(this.root, this.part)), this.upperIfIgnoreCase(next()));
        }

        propertyExpressionx = this.getTypedPath(this.root, this.part);
//        Expression<String> propertyExpression = this.upperIfIgnoreCase(propertyExpressionx);
//        Expression<String> parameterExpressionx = this.upperIfIgnoreCase(JpaQueryCreator.this.provider.next(this.part, String.class).getExpression());
//        Predicate like = JpaQueryCreator.this.builder.like(propertyExpression, parameterExpressionx, JpaQueryCreator.this.escape.getEscapeCharacter());

        Expression<String> propertyExpression = (Expression<String>) this.upperIfIgnoreCase(propertyExpressionx);
        Expression<String> parameterExpressionx = (Expression<String>)this.upperIfIgnoreCase(next());
        Predicate like = builder.like(propertyExpression, parameterExpressionx, EscapeCharacter.DEFAULT.getEscapeCharacter());
        return !type.equals(Part.Type.NOT_LIKE) && !type.equals(Part.Type.NOT_CONTAINING) ? like : like.not();
    }

    private <T> Predicate isMember(CriteriaBuilder builder, Expression<T> parameter, Expression<Collection<T>> property) {
        return builder.isMember(parameter, property);
    }

    private <T> Predicate isNotMember(CriteriaBuilder builder, Expression<T> parameter, Expression<Collection<T>> property) {
        return builder.isNotMember(parameter, property);
    }

    private Expression<?> upperIfIgnoreCase(Expression<?> expression) {

        switch (this.part.shouldIgnoreCase()) {
            case ALWAYS:
                boolean var10000 = this.canUpperCase(expression);
                String var10001 = expression.getJavaType().getName();
                Assert.state(var10000, "Unable to ignore case of " + var10001 + " types, the property '" + this.part.getProperty().getSegment() + "' must reference a String");
                return builder.upper((Expression<String>)expression);
            case WHEN_POSSIBLE:
                if (this.canUpperCase(expression)) {
                    return builder.upper((Expression<String>) expression);
                }
            case NEVER:
            default:
                return expression;
        }
    }

    private boolean canUpperCase(Expression<?> expression) {
        return String.class.equals(expression.getJavaType());
    }

    private Expression<? extends Comparable> getComparablePath(Root<?> root, Part part) {
        return this.getTypedPath(root, part);
    }

    private <T> Expression<T> getTypedPath(Root<?> root, Part part) {
        return QueryUtils.toExpressionRecursively(root, part.getProperty());
    }

    public static <T> Expression<T> toExpressionRecursively(Root<?> root, Part part){
        return QueryUtils.toExpressionRecursively(root, part.getProperty());
    }

    private <T> Expression<T> traversePath(Path<?> root, PropertyPath path) {
        Path<Object> result = root.get(path.getSegment());
        return (Expression)(path.hasNext() ? this.traversePath(result, path.next()) : result);
    }


    private Object next(boolean raw){
        return iterator.next();
    }

    private <X> Expression<X> next(){

        Object value = iterator.next();
        if (value == null) {
            return null;
        }
        switch (part.getType()) {
            case STARTING_WITH:
                return (Expression<X>) builder.literal(String.format("%s%%", value.toString()));
            case ENDING_WITH:
                return (Expression<X>) builder.literal(String.format("%%%s", value.toString()));
            case CONTAINING:
            case NOT_CONTAINING:
                return (Expression<X>) builder.literal(String.format("%%%s%%", value.toString()));
            default:
                return (Expression<X>) builder.literal(value);
        }
    }
}
