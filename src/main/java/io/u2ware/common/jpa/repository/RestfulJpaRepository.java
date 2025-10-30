package io.u2ware.common.jpa.repository;

import java.io.Serializable;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RestResource;

public interface RestfulJpaRepository<T, ID extends Serializable>  extends
CrudRepository<T,ID>,
PagingAndSortingRepository<T,ID>,
JpaSpecificationExecutor<T> {


    @RestResource(exported=false) 
    public Page<T> findAll(Pageable pageable) ;


    @RestResource(exported=false) 
    public Optional<T> findById(ID id);


}
