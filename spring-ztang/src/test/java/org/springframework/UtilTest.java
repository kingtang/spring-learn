package org.springframework;

import java.lang.reflect.Method;
import java.util.AbstractList;

import org.junit.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.domain.Student;
import org.springframework.domain.User;
import org.springframework.domain.cycle.CycleA;
import org.springframework.domain.factory.UserFactory;
import org.springframework.domain.override.HelloWorld;
import org.springframework.domain.parent.Child;
import org.springframework.generic.StudentBase;

@SuppressWarnings("deprecation")
public class UtilTest {
	
	private LocalVariableTableParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();
	
	@Test
	public void testBeanUtils()
	{
		try {
			Method method = UserFactory.class.getMethod("getInstance", new Class[]{String.class,int.class});
			System.out.println(method.getName());
			String[] parameterNames = discoverer.getParameterNames(method);
			for (String string : parameterNames) {
				System.out.println(string);
			}
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}
	@Test
	public void testGenericType()
	{
		StudentBase s = new StudentBase();
		System.out.println(s.getEntityClass());
	}
}
