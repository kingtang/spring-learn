package org.springframework.domain.override;

import java.lang.reflect.Method;

import org.springframework.beans.factory.support.MethodReplacer;

public class MethodReplace implements MethodReplacer {

	private Object target;
	
	public Object reimplement(Object obj, Method method, Object[] args) throws Throwable {
		System.out.println("before method");
		//method.invoke(target, args);
		System.out.println("after method");
		return "aaa";
	}

	public Object getTarget() {
		return target;
	}

	public void setTarget(Object target) {
		this.target = target;
	}

}
