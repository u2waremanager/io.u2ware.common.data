
# Installation (POM)

Please Setting [Github Packages](https://docs.github.com/ko/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry) and..

```xml
		<dependency>
			<groupId>io.u2ware</groupId>
			<artifactId>common.data</artifactId>
			<version>3.2.7</version>
		</dependency>
```

# Spring Data JPA 

[Spring Data JPA](https://docs.spring.io/spring-data/jpa/docs/2.3.6.RELEASE/reference/html) 는 도메인 객체에 대한 operation 을 제공 합니다.


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

[Spring Data REST](https://docs.spring.io/spring-data/rest/docs/3.3.6.RELEASE/reference/html/) 는 도메인 객체에 대한  리소스 Endpoints 를 제공합니다.    
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
먼저, `@EnableRestfulJpaRepositories` 선언이 필요합니다.

```java
@SpringBootApplication
@EnableRestfulJpaRepositories
public class Application {

}
```


추가로, JPA 를 사용하는 `Spring Data Repository` 에  [QuerydslPredicateExecutor<T>](https://docs.spring.io/spring-data/commons/docs/2.3.6.RELEASE/api/index.html?org/springframework/data/querydsl/QuerydslPredicateExecutor.html) 또는  [JpaSpecificationExecutor<T>](https://docs.spring.io/spring-data/jpa/docs/2.3.6.RELEASE/api/index.html?org/springframework/data/jpa/repository/JpaSpecificationExecutor.html) 확장이 필요합니다.

```java
public interface FooRepository extends JpaRepository<Foo, Long>
					,JpaSpecificationExecutor<Foo>  //--
					,QuerydslPredicateExecutor<Foo>  //--
{  
}
```

[u2ware-data-rest](https://github.com/u2ware/u2ware-data-rest/) 는 REST API 와 Event Handler 를 추가로 제공합니다.   

|request |   before event |  operations | after event | response  |
|---|---|---|---|---|
|curl '/foos/1' -X POST -d '{}'                     |  [@HandleAfterRead](#handleafterread)  | read |   | json |
|curl '/foos/search'   -X POST -d '{"name" : ...}' |  | search |   [@HandleBeforeRead](#handlebeforeread) | json |



# @HandleAfterRead 

 [@HandleAfterRead](src/main/java/io/github/u2ware/data/rest/core/annotation/HandleAfterRead.java) 는 단일 자원에 대한 읽기 이벤트 이며, 이를 통해 확장 포인트를 제공합니다.
다음은 Entity 의 읽기 카운트를 1씩 증가 시키는 예시 입니다.

```java
import io.github.u2ware.data.rest.core.annotation.HandleAfterRead;

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

(1) `Spring Data Repository` 가 [QuerydslPredicateExecutor<T>](https://docs.spring.io/spring-data/commons/docs/2.3.6.RELEASE/api/index.html?org/springframework/data/querydsl/QuerydslPredicateExecutor.html) 를 확장한 경우, 이벤트 핸들러에  [Predicate](http://www.querydsl.com/static/querydsl/4.2.1/apidocs/index.html?com/querydsl/core/types/Predicate.html) 객체가 전달됩니다.

```java
public interface HelloRepository extends JpaRepository<Hello, Long>
					,QuerydslPredicateExecutor<Hello> //-- (1)
{
}
```
```java
import io.github.u2ware.data.rest.core.annotation.HandleBeforeRead;

import com.querydsl.core.types.Predicate;

@Component
@RepositoryEventHandler
public class HelloHandler {
	
	@HandleBeforeRead
	public void onBeforeRead(Hello entity, Predicate predicate) { //-- (1)

	}
}
```

(2) `Spring Data Repository` 가 [JpaSpecificationExecutor<T>](https://docs.spring.io/spring-data/jpa/docs/2.3.6.RELEASE/api/index.html?org/springframework/data/jpa/repository/JpaSpecificationExecutor.html)  를 확장한 경우, 이벤트 핸들러에   [Specification](https://docs.spring.io/spring-data/jpa/docs/2.3.6.RELEASE/api/index.html?org/springframework/data/jpa/domain/Specification.html) 객체가 전달됩니다.

```java
public interface WorldRepository extends JpaRepository<World, Long>
					,JpaSpecificationExecutor<World> //-- (2)
{
}
```
```java
import io.github.u2ware.data.rest.core.annotation.HandleBeforeRead;

import org.springframework.data.jpa.domain.Specification;

@Component
@RepositoryEventHandler
public class WorldHandler {
	
	@HandleBeforeRead
	public void onBeforeRead(World entity, Specification<World> specification) {  //-- (2)
	
	}
}
```

# QueryDSL Predicate Builder 

[u2ware-data-rest](https://github.com/u2ware/u2ware-data-rest/) 는 method chain style 의 
[QuerydslPredicateBuilder](src/main/java/io/github/u2ware/data/jpa/repository/support/QuerydslPredicateBuilder.java)
를 제공합니다.

```java
import io.github.u2ware.data.rest.core.annotation.HandleBeforeRead;
import io.github.u2ware.data.jpa.repository.support.QuerydslPredicateBuilder;

@Component
@RepositoryEventHandler
public class FooHandler {
	
	@HandleBeforeRead
	public void onBeforeRead(Foo entity, Predicate predicate) {

		// method chain style 의  검색 조건 생성 
		QuerydslPredicateBuilder.of(Foo.class)   
			.where()
			.and().eq("name", entity.getName())
			.andStart()
				.andStart()
					.and().eq("age", entity.getAge())
					.or().eq("name", entity.getName())
				.andEnd()
			.andEnd()
		.build(predicate);

	}
}
```


# JPA Specification Builder 

[u2ware-data-rest](https://github.com/u2ware/u2ware-data-rest/) 는 method chain style 의
[JpaSpecificationBuilder](src/main/java/io/github/u2ware/data/jpa/repository/query/JpaSpecificationBuilder.java) 를 제공합니다.

```java
import io.github.u2ware.data.rest.core.annotation.HandleBeforeRead;
import io.github.u2ware.data.jpa.repository.query.JpaSpecificationBuilder;

@Component
@RepositoryEventHandler
public class FooHandler {
	
	@HandleBeforeRead
	public void onBeforeRead(Foo entity, Specification<Foo> specification) {

		// method chain style 의  검색 조건 생성 
		JpaSpecificationBuilder.of(Foo.class)   
			.where()
			.and().eq("name", entity.getName())
			.andStart()
				.andStart()
					.and().eq("age", entity.getAge())
					.or().eq("name", entity.getName())
				.andEnd()
			.andEnd()
		.build(specification);
	}
}
```


# License

[u2ware-common-data](https://github.com/u2waremanager/io.u2ware.common.data) is Open Source software released under the Apache 2.0 license.

