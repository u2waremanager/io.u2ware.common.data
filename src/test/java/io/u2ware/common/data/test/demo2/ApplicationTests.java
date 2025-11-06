package io.u2ware.common.data.test.demo2;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static io.u2ware.common.docs.MockMvcRestDocs.*;




@SpringBootTest
@AutoConfigureMockMvc
public class ApplicationTests {



	protected Log logger = LogFactory.getLog(getClass());

	@Autowired
	private MockMvc m;

	@Autowired
	private FooDocs d;

	@Test
    public void contextLoads() throws Exception{
		m.perform(get("/api/profile/foos")).andExpect(is2xx()).andDo(print());
		m.perform(post("/api/foos").content(d::newFoo, "bar1")).andExpect(is2xx()).andDo(print());
		m.perform(post("/api/foos/search").content(d::searchFoo, "bar1")).andExpect(is2xx()).andDo(print());

    }
}
