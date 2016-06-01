package org.springframework.generic;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.springframework.domain.Student;

public abstract class AbstractBase<T> {
	private Class<T> entityClass;
	public AbstractBase()
	{
		Type genericSuperclass = this.getClass().getGenericSuperclass();
		Type type = ((ParameterizedType)genericSuperclass).getActualTypeArguments()[0];
		this.setEntityClass((Class)type);
	}
	public static void main(String[] args) {
		AbstractBase<Student> ab = new AbstractBase<Student>() {
		};
		Class<? extends AbstractBase> class1 = ab.getClass();
		System.out.println(class1.getName());
		System.out.println(ab.getEntityClass());
	}
	public Class<T> getEntityClass() {
		return entityClass;
	}
	public void setEntityClass(Class<T> entityClass) {
		this.entityClass = entityClass;
	}
}
