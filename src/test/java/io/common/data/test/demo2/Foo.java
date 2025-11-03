package io.common.data.test.demo2;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Foo {
    
    @Id
    @GeneratedValue
    private Long seq;

    private String name;
}
