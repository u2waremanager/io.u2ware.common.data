# Installation (Maven)

```xml
<dependency>
  <groupId>io.u2ware</groupId>
  <artifactId>common.rest</artifactId>
  <version>2024.10</version>
</dependency>
```
# Background(Spring Data JPA)
[Spring Data JPA](https://docs.spring.io/spring-data/jpa/docs/2.3.6.RELEASE/reference/html) 는 JPA 를 사용하는 `Spring Data Repository` 를 제공하며, 자원에 대해 operation 이 가능합니다.

```java
@Entity 
public class Foo{
    @Id @GeneratedValue
    private Long seq;
    private String name;
}
```
```java
public interface FooRepository 
        extends PagingAndSortingRepository<Foo, Long>{
    
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

# Background(Spring Data REST)

[Spring Data REST](https://docs.spring.io/spring-data/rest/docs/3.3.6.RELEASE/reference/html/) 는 `Spring Data Repository` 의 리소스를 노출합니다.    
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
# Usage

### 1. Configuration

[U2ware Common REST JPA]()를 사용하기 위해 [@EnableJpaRepositoriesController]() 설정이 필요합니다.

```java
@SpringBootApplication
@EnableJpaRepositoriesController //Enable "U2ware Common REST JPA"
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
```

또한, JPA 를 사용하는 `Spring Data Repository`에 [JpaSpecificationExecutor<T>](https://docs.spring.io/spring-data/jpa/docs/2.3.6.RELEASE/api/index.html?org/springframework/data/jpa/repository/JpaSpecificationExecutor.html) 확장이 필요합니다.


```java
public interface FooRepository 
        extends PagingAndSortingRepository<Foo, Long>
        ,JpaSpecificationExecutor<Foo>  // requried
{  
}
```

### 1. Additional Handle

[U2ware Common REST JPA]() 는 다음과 같은 Endpoint 와 Event 가 추가로 제공합니다.

|request |   before event |  operations | after event | response  |
|---|---|---|---|---|
|curl '/foos/1' -X POST -d '{}'                     |  [@HandleAfterRead](#handleafterread)  | read |   | json |
|curl '/foos/search'   -X POST -d '{"name" : ...}' |  | search |   [@HandleBeforeRead](#handlebeforeread) | json |

### 2. @HandleAfterRead

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
### 3. @HandleBeforeRead
  [@HandleBeforeRead]() 는 자원에 대한 검색 이벤트 이며, 이를 통해 확장 포인트를 제공합니다.
  다음은 [Specification]() 를 이용한 검색 예시입니다.

```java
public interface HelloRepository extends PagingAndSortingRepository<Hello, Long>
					,QuerydslPredicateExecutor<Hello> //-- (1)
{
}
```
```java

@Component
@RepositoryEventHandler
public class WorldHandler {

    @HandleBeforeRead
    public void onBeforeRead(World entity, Specification<World> specification) {  //-- (2)
        specification.and((r,b,q)->{
            //....            
        });
    }
}
```

### 4. JPA Specification Builder 

[U2ware Common Rest JPA]() 는 method chain style 의
[JpaSpecificationBuilder](src/main/java/io/github/u2ware/data/jpa/repository/query/JpaSpecificationBuilder.java) 를 제공합니다.

```java
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