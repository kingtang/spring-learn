package org.springframework.generic;

import org.springframework.core.GenericTypeResolver;

public class Base<T> {
	private Class<T> entityClass;
	
	@SuppressWarnings("unchecked")
	public Base()
	{
		this.setEntityClass((Class<T>) GenericTypeResolver.resolveTypeArgument(getClass(), Base.class));
	}

	public Class<T> getEntityClass() {
		return entityClass;
	}

	public void setEntityClass(Class<T> entityClass) {
		this.entityClass = entityClass;
	}
	
}
