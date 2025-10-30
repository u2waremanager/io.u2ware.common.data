package org.springframework.data.jpa.repository.query;

import org.springframework.data.repository.query.ReturnedType;
import org.springframework.data.repository.query.parser.PartTree;

import jakarta.persistence.criteria.CriteriaBuilder;

public class CopiedJpaQueryCreator extends JpaQueryCreator {

    public CopiedJpaQueryCreator(PartTree tree, ReturnedType type, CriteriaBuilder builder, ParameterMetadataProvider provider) {
        super(tree, type, builder, provider);
    }
}
