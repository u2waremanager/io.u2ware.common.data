package io.u2ware.common.data.test.demo1;

import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleAfterDelete;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeDelete;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

@Component
@RepositoryEventHandler
public class FooHandler {

	@HandleBeforeCreate
	public void onBeforeCreate(Foo entity) { /* Before [POST] /foos */  }

	@HandleAfterCreate
	public void onAfterCreate(Foo entity) { /* After [POST] /foos */ }
	
	@HandleBeforeSave
	public void onBeforeSave(Foo entity) { /* Before [PATCH or PUT] /foos/1 */	}

	@HandleAfterSave
	public void onAfterSave(Foo entity) { /* Before [PATCH or PUT] /foos/1 */ }
	
	@HandleBeforeDelete
	public void onBeforeDelete(Foo entity) {/* Before [DELETE] /foos/1 */ }

	@HandleAfterDelete
	public void onAfterDelete(Foo entity) { /* Before [DELETE] /foos/1 */}    

}
