package io.common.data.test.demo2;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import io.u2ware.common.data.jpa.repository.RestfulJpaRepository;


// public interface FooRepository extends JpaRepository<Foo, Long>, JpaSpecificationExecutor<Foo> {

public interface FooRepository extends RestfulJpaRepository<Foo, Long> {
    

}
