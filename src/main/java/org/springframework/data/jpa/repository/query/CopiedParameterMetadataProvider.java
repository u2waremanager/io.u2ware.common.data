package org.springframework.data.jpa.repository.query;

import org.springframework.data.repository.query.Parameters;
import org.springframework.data.repository.query.ParametersParameterAccessor;
import org.springframework.data.repository.query.parser.Part;

import jakarta.persistence.criteria.CriteriaBuilder;

public class CopiedParameterMetadataProvider extends ParameterMetadataProvider {


    public CopiedParameterMetadataProvider(CriteriaBuilder builder, ParametersParameterAccessor accessor, EscapeCharacter escape) {
        super(builder, accessor, escape);
    }

    public CopiedParameterMetadataProvider(CriteriaBuilder builder, Parameters<?, ?> parameters, EscapeCharacter escape) {
        super(builder, parameters, escape);
    }

    @Override
    public <T> ParameterMetadata<T> next(Part part) {

        return super.next(part);
    }
}
