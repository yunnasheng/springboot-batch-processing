# Spring Batch 快速入门


### 目标
> 将一个CSV文件的英文名，全部转换成大写字母，然后存储到数据库

### 环境
* 阅读耗时约15分钟 
* JDK 1.8 或更高版本
* Gradle 4+ or Maven 3.2+ 
* IDE工具 推荐Spring Tool Suite4（轻巧方便、占内存小）,ItellJ IDEA（更智能、也更占用内存）
* 此示例需要Spring Batch和HyperSQL 的依赖项

### 如何完成本指南
推荐大家从头开始每个步骤，当然如果想跳过从创建项目开始的流程，可以直接克隆我已经写好的工程文件

 [https://github.com/yunnasheng/springboot-batch-processing.git]

#### 准备业务数据


* 1.准备测试数据`/src/main/resources/sample-data.csv`

>该CSV在每行上包含一个姓氏和一个名字，用逗号分隔，内容如下：

		Jill,Doe
		Joe,Doe
		Justin,Doe
		Jane,Doe
		John,Doe

* 2.接下来需要编写一个SQL脚本`src/main/resources/schema-all.sql`来存储数据
	
	DROP TABLE people IF EXISTS;
	CREATE TABLE people  (
	    person_id BIGINT IDENTITY NOT NULL PRIMARY KEY,
	    first_name VARCHAR(20),
	    last_name VARCHAR(20)
	);

>springboot在启动后会自动执行 schema-all.sql
>

#### 创建springboot Maven项目
* pom.xml依赖如下：
	
	``` <dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-batch</artifactId>
		</dependency>
		<dependency>
			<groupId>org.hsqldb</groupId>
			<artifactId>hsqldb</artifactId>
			<scope>runtime</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
			<exclusions>
				<exclusion>
					<groupId>org.junit.vintage</groupId>
					<artifactId>junit-vintage-engine</artifactId>
				</exclusion>
			</exclusions>
		</dependency>
		<dependency>
			<groupId>org.springframework.batch</groupId>
			<artifactId>spring-batch-test</artifactId>
			<scope>test</scope>
		</dependency>
	</dependencies> 
```
	
* 1.创建一个实体类Person
* 2.创建PersonProcessor类处理大小写转换
* 3.创建BatchConfiguration类作为配置类
* 4.创建JobCompletionNotificationListener类来校验处理结果

#### 测试

* 1.运行项目：  ```java -jar springboot-batch-processing-1.0.0.jar```

* 2.测试结果： ![Alt text](/path/to/img.jpg "Click Me!")

