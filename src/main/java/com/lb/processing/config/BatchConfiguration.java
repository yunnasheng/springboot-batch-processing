package com.lb.processing.config;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.lb.processing.entity.Person;
import com.lb.processing.processor.JobCompletionNotificationListener;
import com.lb.processing.processor.PersonProcessor;

/**
 * 1.设置为配置类 2.开启批处理配置
 * 
 * #@EnableBatchProcessing注解会自动装配好如下bean，来方便执行批处理程序，若您需要使用如下的bean，只需要使用@Autowired注入即可使用
 * 
 * JobRepository - bean name "jobRepository" JobLauncher - bean name
 * "jobLauncher" JobRegistry - bean name "jobRegistry"
 * PlatformTransactionManager - bean name "transactionManager" JobBuilderFactory
 * - bean name "jobBuilders" StepBuilderFactory - bean name "stepBuilders"
 * 
 * @author yunnasheng
 *
 */
@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	/**
	 * 读取器 reader()创建一个ItemReader 它会寻找一个文件名字为：sample-data.csv的文件，将其转换为Person。
	 * 
	 * @return
	 */
	@Bean
	public FlatFileItemReader<Person> reader() {
		return new FlatFileItemReaderBuilder<Person>().name("personItemReader")
				.resource(new ClassPathResource("sample-data.csv")).delimited()
				.names(new String[] { "firstName", "lastName" })
				.fieldSetMapper(new BeanWrapperFieldSetMapper<Person>() {
					{
						setTargetType(Person.class);
					}
				}).build();
	}

	/**
	 * 处理器，实现ItemProcessor接口的process方法，
	 * 用于处理业务逻辑，将数据转换为大写
	 * 
	 * @return
	 */
	@Bean
	public PersonProcessor processor() {
		return new PersonProcessor();
	}

	/**
	 * 写入器，writer(DataSource)创建一个ItemWriter
	 * 
	 * 这个只是针对JDBC业务场景的样例，若有其他业务场场景可查看ItemWriter的实现类来
	 * 
	 * 负责将person实体写入数据库
	 * 
	 * @param dataSource
	 * @return
	 */
	@Bean
	public JdbcBatchItemWriter<Person> writer(DataSource dataSource) {
		return new JdbcBatchItemWriterBuilder<Person>()
				.itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
				.sql("INSERT INTO people (first_name, last_name) VALUES (:firstName, :lastName)").dataSource(dataSource)
				.build();
	}

	/**
	 * 批次导入用户工作
	 * 
	 * @param listener
	 * @param step1
	 * @return
	 */
	@Bean
	public Job importUserJob(JobCompletionNotificationListener listener, Step step1) {

		JobBuilder builder = jobBuilderFactory.get("importUserJob");
		Job job = builder.incrementer(new RunIdIncrementer())
				.listener(listener)
				// 执行步骤1
				.start(step1)
				.build();

		return job;

//	  return jobBuilderFactory.get("importUserJob")
//	    .incrementer(new RunIdIncrementer())
//	    .listener(listener)
//	    .flow(step1)
//	    .end()
//	    .build();
	}

	/**
	 * 步骤1
	 * @param writer
	 * @return
	 */
	@Bean
	public Step step1(JdbcBatchItemWriter<Person> writer) {
		return stepBuilderFactory.get("step1")
				// 定义一次要写入多少数据,这里每个批次允许处理10条数据
				.<Person, Person>chunk(10)
				// 读取CSV
				.reader(reader())
				// 处理业务逻辑
				.processor(processor())
				// 写入数据库
				.writer(writer).build();
	}
}
