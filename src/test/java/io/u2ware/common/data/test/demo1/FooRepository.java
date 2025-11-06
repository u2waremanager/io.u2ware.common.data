package io.u2ware.common.data.test.demo1;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FooRepository extends JpaRepository<Foo, Long> {
    

    Iterable<Foo> findByName(String name);
}
