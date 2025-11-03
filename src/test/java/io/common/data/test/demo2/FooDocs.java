package io.common.data.test.demo2;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import io.u2ware.common.docs.MockMvcRestDocs;

@Component
public class FooDocs extends MockMvcRestDocs{



    public Map<String, Object> newFoo(String name){
        Map<String, Object> e = new HashMap<>();
        e.put("name", name);
        return e;
    }

    public Map<String, Object> searchFoo(String name){
        Map<String, Object> e = new HashMap<>();
        e.put("name", name);
        return e;
    }


    

    
}
