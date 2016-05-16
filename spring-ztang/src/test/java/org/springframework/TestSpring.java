package org.springframework;

import java.util.AbstractList;

import org.junit.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.domain.Student;
import org.springframework.domain.User;
import org.springframework.domain.cycle.CycleA;
import org.springframework.domain.factory.UserFactory;
import org.springframework.domain.override.HelloWorld;
import org.springframework.domain.parent.Child;

public class TestSpring {
	
	@Test
	public void classPathXml()
	{
		String configLocation = "classpath:/META-INF/app.xml";
		ApplicationContext context = new ClassPathXmlApplicationContext(configLocation);
		User user = (User)context.getBean("user");
		System.out.println(user.toString());
	}
	
	@Test
	public void xmlFactoryDependOn()
	{
		BeanFactory factory = new XmlBeanFactory(new ClassPathResource("META-INF/app.xml"));
		User user = (User)factory.getBean("user");
		System.out.println(user.toString());
	}
	
	@Test
	public void xmlFactoryCycle()
	{
		BeanFactory factory = new XmlBeanFactory(new ClassPathResource("META-INF/app_cycle.xml"));
		CycleA a = (CycleA)factory.getBean("cycle_a");
		System.out.println(a.toString());
	}
	
	@Test
	public void xmlFactoryParent()
	{
		BeanFactory factory = new XmlBeanFactory(new ClassPathResource("META-INF/app_parent.xml"));
		Child a = (Child)factory.getBean("child");
		System.out.println(a.toString());
	}
	
	@Test
	public void xmlFactoryOverride()
	{
		BeanFactory factory = new XmlBeanFactory(new ClassPathResource("META-INF/app_override.xml"));
		HelloWorld a = (HelloWorld)factory.getBean("hello");
		System.out.println(a.sayHello());
	}
	
	@Test
	public void xmlFactoryPostProcessor()
	{
		XmlBeanFactory factory = new XmlBeanFactory(new ClassPathResource("META-INF/app_post_processor.xml"));
		
		BeanPostProcessor post = factory.getBean(BeanPostProcessor.class);
		factory.addBeanPostProcessor(post);
		
		Student a = (Student)factory.getBean("student");
		System.out.println(a);
	}
	
	@Test
	public void factoryMethod()
	{
		
		BeanFactory factory = new XmlBeanFactory(new ClassPathResource("META-INF/app_factory_method.xml"));
		User user = (User) factory.getBean("userFactory");
		
		System.out.println(user);
	}
	
	@Test
	public void testBeanUtils()
	{
		AbstractList instance = BeanUtils.instantiateClass(AbstractList.class);
	}
}
