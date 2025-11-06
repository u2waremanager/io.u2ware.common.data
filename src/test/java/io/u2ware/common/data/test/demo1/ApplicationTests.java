package io.u2ware.common.data.test.demo1;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static io.u2ware.common.docs.MockMvcRestDocs.*;




@SpringBootTest
@AutoConfigureMockMvc
public class ApplicationTests {



	protected Log logger = LogFactory.getLog(getClass());

	@Autowired
	private MockMvc mvc;

	
	@Test
    public void contextLoads() throws Exception{

		mvc
			.perform(
					MockMvcRequestBuilders.get("/profile/foos")
			)
			.andExpect(
					MockMvcResultMatchers.status().is2xxSuccessful()
			)
			.andDo(
					MockMvcResultHandlers.print(System.err)
			);

    }

	@Autowired
	private FooDocs d;

	@Test
    public void fooTest() throws Exception{

		mvc
			.perform(
				get(d::uri, "/foos")
			)
			.andExpect(
				is2xx()
			)
			.andDo(
				print() 
			)
			.andDo(
				result(d::context, "r1")
			);

		logger.info(d.context("r1", "$._links.self.href"));

		mvc
			.perform(
				post(d::uri, "/foos").content(d::newFoo)
			)
			.andExpect(
				is2xx()
			)
			.andDo(
				print() 
			);
		


		mvc
			.perform(
				get("http://localhost/foos/1")
			)
			.andExpect(
				is2xx()
			)
			.andDo(
				print() 
			)
			.andDo(
				result(d::context, "r3")
			);
		logger.info(d.context("r3", "$._links.self.href"));


		mvc
			.perform(
				get("/foos/search/findByName").param("name", "name1")
			)
			.andExpect(
				is2xx()
			)
			.andDo(
				print() 
			)
			.andDo(
				result(d::context, "r3")
			);
		logger.info(d.context("r3", "$._links.self.href"));		

    }

}
