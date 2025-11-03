package io.common.data.test.demo1;

import org.springframework.stereotype.Component;

import io.u2ware.common.docs.MockMvcRestDocs;

@Component
public class FooDocs extends MockMvcRestDocs{



    public Foo newFoo(){
        Foo foo = new Foo();
        foo.setName("name1");
        return foo;
    }


    
}
