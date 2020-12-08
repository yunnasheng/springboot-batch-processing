package com.lb.processing.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import com.lb.processing.entity.Person;


/**
 * 处理器，用于处理业务逻辑
 * 
 * 将源对象名字转换大写后输出
 * @author yunnasheng
 *
 */
public class PersonProcessor implements ItemProcessor<Person,Person>{
	
	private static final Logger log = LoggerFactory.getLogger(PersonProcessor.class);

	@Override
	public Person process(Person sourcePerson) throws Exception {
		
		final String firstName = sourcePerson.getFirstName().toUpperCase();
	    final String lastName = sourcePerson.getLastName().toUpperCase();

	    final Person transformedPerson = new Person(firstName, lastName);

	    log.info("Converting (" + sourcePerson + ") into (" + transformedPerson + ")");

	    return transformedPerson;
	}	

}