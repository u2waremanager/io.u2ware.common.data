
# Installation (POM)

Please Setting [Github Packages](https://docs.github.com/ko/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry).

```xml
<dependency>
	<groupId>io.u2ware</groupId>
	<artifactId>common-data</artifactId>
	<version>3.4.11</version>
</dependency>
```

# Spring Data JPA 

[Spring Data JPA](https://spring.io/projects/spring-data-jpa) 는 도메인 객체에 대한 operation 을 제공 합니다.


```java
@Entity 
public class Foo{
    @Id @GeneratedValue
    private Long seq;
    private String name;
}
```
```java
public interface FooRepository extends JpaRepository<Foo, Long>{
    Iterable<Foo> findByName(String name);
}
```
```java
Foo f = new Foo("a");
f = fooRepository.save(f); //create
fooRepository.findById(1); //read
fooRepository.save(f);     //update
fooRepository.delete(f);   //delete

fooRepository.findAll();       //search all
fooRepository.findByName("b"); //search 
```

# Spring Data REST

[Spring Data REST](https://spring.io/projects/spring-data-rest) 는 도메인 객체에 대한 리소스 Endpoints 를 제공합니다.    
예를 들어, FooRepository 를 기반으로 Foo에 대한 REST API 와 Event Handler 를 아래와 같이 제공합니다.

|request |   before event |  operations | after event | response  |
|---|---|---|---|---|
|curl '/foos'   -X POST -d '{"name" : ...}'          |  @HandleBeforeCreate | create     | @HandleAfterCreate |json|
|curl '/foos/1' -X GET                               |                      | read       |                    |json|
|curl '/foos/1' -X PATCH(or PUT) -d '{"name" : ...}' |  @HandleBeforeSave   | update     | @HandleAfterSave   |json|
|curl '/foos/1' -X DELETE                            |  @HandleBeforeDelete | delete     | @HandleAfterDelete |json|
|curl '/foos/'  -X GET                               |                      | search all |                    |json|  
|curl '/foos/search/findByName?name=hello' -X GET    |                      | search     |                    |json|   

```java
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
```

# U2ware Common Data

먼저, `u2ware-common-data` 가 제공하는  [@EnableRestfulJpaRepositories](src/main/java/io/u2ware/common/data/jpa/config/EnableRestfulJpaRepositories.java) 선언이 필요합니다.

```java
import io.u2ware.common.data.jpa.config.EnableRestfulJpaRepositories;

@SpringBootApplication
@EnableRestfulJpaRepositories //
public class Application {

}
```

추가로, JPA 를 사용하는 Spring Data Repository 에  [JpaSpecificationExecutor<T>](https://spring.io/projects/spring-data-jpa) 확장이 필요합니다.

```java
public interface FooRepository extends JpaRepository<Foo, Long>
					,JpaSpecificationExecutor<Foo>  
{  
}
```

`u2ware-common-data` 가 제공하는 [RestfulJpaRepositor<T,ID>](src/main/java/io/u2ware/common/data/jpa/repository/RestfulJpaRepository.java) 을 사용 할 수도 있습니다.
```java
import io.u2ware.common.data.jpa.repository.RestfulJpaRepository;

public interface FooRepository extends RestfulJpaRepositor<Foo, Long>
{  
}
```


`u2ware-common-data` 는 도매인 객체에 대해 REST API 와 Event Handler 를 추가로 제공합니다.   

|request |   before event |  operations | after event | response  |
|---|---|---|---|---|
|curl '/foos/1' -X POST -d '{}'                     |  [@HandleAfterRead](#handleafterread)  | read |   | json |
|curl '/foos/search'   -X POST -d '{"name" : ...}' |  | search |   [@HandleBeforeRead](#handlebeforeread) | json |



# @HandleAfterRead 

 `u2ware-common-data` 의 [@HandleAfterRead](src/main/java/io/u2ware/common/data/rest/core/annotation/HandleAfterRead.java) 는 단일 자원에 대한 읽기 이벤트 이며, 이를 통해 확장 포인트를 제공합니다.

다음은 Entity 의 읽기 카운트를 1씩 증가 시키는 예시 입니다.

```java
import io.u2ware.common.data.rest.core.annotation.HandleAfterRead;

@Component
@RepositoryEventHandler
public class ArticleHandler {
	
	private ArticleRepository articleRepository;

	@HandleAfterRead
	public void onAfterRead(Article article) { 
		article.setReadCount(article.getReadCount()+1);
		articleRepository.save(article);
	}	
}
```

# @HandleBeforeRead 

`u2ware-common-data` 의 [@HandleBeforeRead](src/main/java/io/u2ware/common/data/rest/core/annotation/HandleBeforeRead.java) 의 경우, 
이벤트 핸들러에  [Specification](https://spring.io/projects/spring-data-jpa) 객체가 전달됩니다.

```java
import io.u2ware.common.data.rest.core.annotation.HandleBeforeRead;

import org.springframework.data.jpa.domain.Specification;

@Component
@RepositoryEventHandler
public class FooHandler {
	
	@HandleBeforeRead
	public void onBeforeRead(Foo entity, Specification<Foo> specification) {  //
	
	}
}
```


# JPA Specification Builder 

`u2ware-common-data` 는 method chain style 의
[JpaSpecificationBuilder](src/main/java/io/u2ware/common/data/jpa/repository/query/JpaSpecificationBuilder.java) 를 제공합니다.

```java
import io.u2ware.common.data.jpa.repository.query.JpaSpecificationBuilder;

@Component
@RepositoryEventHandler
public class FooHandler {
	
	@HandleBeforeRead
	public void onBeforeRead(Foo entity, Specification<Foo> specification) {

		// method chain style 의  검색 조건 생성 
		JpaSpecificationBuilder.of(Foo.class)   
			.where()
			.and().eq("name", entity.getName())

		.build(specification);
	}
}
```


# License

`u2ware-common-data` is Open Source software released under the Apache 2.0 license.

