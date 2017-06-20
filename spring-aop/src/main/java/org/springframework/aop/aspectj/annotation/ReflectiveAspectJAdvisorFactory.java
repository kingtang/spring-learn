/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.aop.aspectj.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.aopalliance.aop.Advice;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.DeclareParents;
import org.aspectj.lang.annotation.Pointcut;

import org.springframework.aop.Advisor;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.aop.aspectj.AbstractAspectJAdvice;
import org.springframework.aop.aspectj.AspectJAfterAdvice;
import org.springframework.aop.aspectj.AspectJAfterReturningAdvice;
import org.springframework.aop.aspectj.AspectJAfterThrowingAdvice;
import org.springframework.aop.aspectj.AspectJAroundAdvice;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.aspectj.AspectJMethodBeforeAdvice;
import org.springframework.aop.aspectj.DeclareParentsAdvisor;
import org.springframework.aop.framework.AopConfigException;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConvertingComparator;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.comparator.CompoundComparator;
import org.springframework.util.comparator.InstanceComparator;

/**
 * Factory that can create Spring AOP Advisors given AspectJ classes from
 * classes honoring the AspectJ 5 annotation syntax, using reflection to
 * invoke the corresponding advice methods.
 *
 * @author Rod Johnson
 * @author Adrian Colyer
 * @author Juergen Hoeller
 * @author Ramnivas Laddad
 * @author Phillip Webb
 * @since 2.0
 */
public class ReflectiveAspectJAdvisorFactory extends AbstractAspectJAdvisorFactory {

	private static final Comparator<Method> METHOD_COMPARATOR;

	static {
		CompoundComparator<Method> comparator = new CompoundComparator<Method>();
		comparator.addComparator(new ConvertingComparator<Method, Annotation>(
				new InstanceComparator<Annotation>(
						Around.class, Before.class, After.class, AfterReturning.class, AfterThrowing.class),
				new Converter<Method, Annotation>() {
					public Annotation convert(Method method) {
						AspectJAnnotation<?> annotation = AbstractAspectJAdvisorFactory.findAspectJAnnotationOnMethod(method);
						return annotation == null ? null : annotation.getAnnotation();
					}
				}));
		comparator.addComparator(new ConvertingComparator<Method, String>(
				new Converter<Method, String>() {
					public String convert(Method method) {
						return method.getName();
					}
				}));
		METHOD_COMPARATOR = comparator;
	}


	public List<Advisor> getAdvisors(MetadataAwareAspectInstanceFactory maaif) {
		//获取本次待处理的Aspect类
		final Class<?> aspectClass = maaif.getAspectMetadata().getAspectClass();
		final String aspectName = maaif.getAspectMetadata().getAspectName();
		validate(aspectClass);

		// We need to wrap the MetadataAwareAspectInstanceFactory with a decorator
		// so that it will only instantiate once.
		//同步保证单例
		final MetadataAwareAspectInstanceFactory lazySingletonAspectInstanceFactory =
				new LazySingletonAspectInstanceFactoryDecorator(maaif);

		final List<Advisor> advisors = new LinkedList<Advisor>();
		//获取要织入的逻辑，比如方法调用前，方法调用后等等
		for (Method method : getAdvisorMethods(aspectClass)) {
			//生成Advisor默认实现类为InstantiationModelAwarePointcutAdvisorImpl
			Advisor advisor = getAdvisor(method, lazySingletonAspectInstanceFactory, advisors.size(), aspectName);
			if (advisor != null) {
				advisors.add(advisor);
			}
		}

		// If it's a per target aspect, emit the dummy instantiating aspect.
		if (!advisors.isEmpty() && lazySingletonAspectInstanceFactory.getAspectMetadata().isLazilyInstantiated()) {
			Advisor instantiationAdvisor = new SyntheticInstantiationAdvisor(lazySingletonAspectInstanceFactory);
			advisors.add(0, instantiationAdvisor);
		}

		// Find introduction fields.
		for (Field field : aspectClass.getDeclaredFields()) {
			Advisor advisor = getDeclareParentsAdvisor(field);
			if (advisor != null) {
				advisors.add(advisor);
			}
		}

		return advisors;
	}

	private List<Method> getAdvisorMethods(Class<?> aspectClass) {
		final List<Method> methods = new LinkedList<Method>();
		//该方法获取Aspect类中所有符合要求的方法，具体规则参见方法内
		ReflectionUtils.doWithMethods(aspectClass, new ReflectionUtils.MethodCallback() {
			public void doWith(Method method) throws IllegalArgumentException {
				// Exclude pointcuts
				//某种意义上也是一种过滤逻辑，也即被Pointcut注解的方法暂不处理
				if (AnnotationUtils.getAnnotation(method, Pointcut.class) == null) {
					methods.add(method);
				}
			}
		});
		//此处包含父类和父接口的所有方法
		Collections.sort(methods, METHOD_COMPARATOR);
		return methods;
	}

	/**
	 * Build a {@link org.springframework.aop.aspectj.DeclareParentsAdvisor}
	 * for the given introduction field.
	 * <p>Resulting Advisors will need to be evaluated for targets.
	 * @param introductionField the field to introspect
	 * @return {@code null} if not an Advisor
	 */
	private Advisor getDeclareParentsAdvisor(Field introductionField) {
		DeclareParents declareParents = introductionField.getAnnotation(DeclareParents.class);
		if (declareParents == null) {
			// Not an introduction field
			return null;
		}

		if (DeclareParents.class.equals(declareParents.defaultImpl())) {
			// This is what comes back if it wasn't set. This seems bizarre...
			// TODO this restriction possibly should be relaxed
			throw new IllegalStateException("defaultImpl must be set on DeclareParents");
		}

		return new DeclareParentsAdvisor(
				introductionField.getType(), declareParents.value(), declareParents.defaultImpl());
	}


	public Advisor getAdvisor(Method candidateAdviceMethod, MetadataAwareAspectInstanceFactory aif,
			int declarationOrderInAspect, String aspectName) {

		validate(aif.getAspectMetadata().getAspectClass());

		//获取Pointcut描述,所谓Pointcut就是此方法需要编织到哪些地方，在spring只支持方法级别，因此这里描述一类方法
		AspectJExpressionPointcut ajexp =
				getPointcut(candidateAdviceMethod, aif.getAspectMetadata().getAspectClass());
		if (ajexp == null) {
			return null;
		}
		//构造Advisor实现类，构造函数中构造Advice
		return new InstantiationModelAwarePointcutAdvisorImpl(
				this, ajexp, aif, candidateAdviceMethod, declarationOrderInAspect, aspectName);
	}

	private AspectJExpressionPointcut getPointcut(Method candidateAdviceMethod, Class<?> candidateAspectClass) {
		//获取方法上的注解，主要有以下几种Before.class, Around.class, After.class, AfterReturning.class, AfterThrowing.class, Pointcut.class
		//且优先返回最前面的注解，此处的目的类似校验，如果AspectJ中没有以上advice，则不需要再生成代理类
		AspectJAnnotation<?> aspectJAnnotation =
				AbstractAspectJAdvisorFactory.findAspectJAnnotationOnMethod(candidateAdviceMethod);
		//如果未找到合适的注解则返回空
		if (aspectJAnnotation == null) {
			return null;
		}
		//构造pointpuct的Spring抽象
		AspectJExpressionPointcut ajexp =
				new AspectJExpressionPointcut(candidateAspectClass, new String[0], new Class[0]);
		//上面寻找Advice的时候会将Pointcut Expression解析出来
		ajexp.setExpression(aspectJAnnotation.getPointcutExpression());
		return ajexp;
	}


	public Advice getAdvice(Method candidateAdviceMethod, AspectJExpressionPointcut ajexp,
			MetadataAwareAspectInstanceFactory aif, int declarationOrderInAspect, String aspectName) {

		//获取aspect类
		Class<?> candidateAspectClass = aif.getAspectMetadata().getAspectClass();
		validate(candidateAspectClass);
		
		//获取advice方法上的aspectj注解，注解类型为Before.class, Around.class, After.class, AfterReturning.class, AfterThrowing.class, Pointcut.class
		AspectJAnnotation<?> aspectJAnnotation =
				AbstractAspectJAdvisorFactory.findAspectJAnnotationOnMethod(candidateAdviceMethod);
		if (aspectJAnnotation == null) {
			return null;
		}

		// If we get here, we know we have an AspectJ method.
		// Check that it's an AspectJ-annotated class
		if (!isAspect(candidateAspectClass)) {
			throw new AopConfigException("Advice must be declared inside an aspect type: " +
					"Offending method '" + candidateAdviceMethod + "' in class [" +
					candidateAspectClass.getName() + "]");
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Found AspectJ method: " + candidateAdviceMethod);
		}

		AbstractAspectJAdvice springAdvice;
		//分类处理advice类型，主要5种，方法前，方法后，方法返回后，异常抛出后，环绕
		switch (aspectJAnnotation.getAnnotationType()) {
			case AtBefore:
				springAdvice = new AspectJMethodBeforeAdvice(candidateAdviceMethod, ajexp, aif);
				break;
			case AtAfter:
				springAdvice = new AspectJAfterAdvice(candidateAdviceMethod, ajexp, aif);
				break;
			case AtAfterReturning:
				springAdvice = new AspectJAfterReturningAdvice(candidateAdviceMethod, ajexp, aif);
				AfterReturning afterReturningAnnotation = (AfterReturning) aspectJAnnotation.getAnnotation();
				if (StringUtils.hasText(afterReturningAnnotation.returning())) {
					springAdvice.setReturningName(afterReturningAnnotation.returning());
				}
				break;
			case AtAfterThrowing:
				springAdvice = new AspectJAfterThrowingAdvice(candidateAdviceMethod, ajexp, aif);
				AfterThrowing afterThrowingAnnotation = (AfterThrowing) aspectJAnnotation.getAnnotation();
				if (StringUtils.hasText(afterThrowingAnnotation.throwing())) {
					springAdvice.setThrowingName(afterThrowingAnnotation.throwing());
				}
				break;
			case AtAround:
				//三个参数，参数一：Advice方法，切面方法比如打日志，校验。参数二：切面表达式，切面方法应用到哪些原生方法中。参数三：？
				//该类底层继承了AspectJ的MethodInterceptor方法
				springAdvice = new AspectJAroundAdvice(candidateAdviceMethod, ajexp, aif);
				break;
			case AtPointcut:
				if (logger.isDebugEnabled()) {
					logger.debug("Processing pointcut '" + candidateAdviceMethod.getName() + "'");
				}
				return null;
			default:
				throw new UnsupportedOperationException(
						"Unsupported advice type on method " + candidateAdviceMethod);
		}

		// Now to configure the advice...
		springAdvice.setAspectName(aspectName);
		springAdvice.setDeclarationOrder(declarationOrderInAspect);
		String[] argNames = this.parameterNameDiscoverer.getParameterNames(candidateAdviceMethod);
		if (argNames != null) {
			springAdvice.setArgumentNamesFromStringArray(argNames);
		}
		springAdvice.calculateArgumentBindings();
		return springAdvice;
	}

	/**
	 * Synthetic advisor that instantiates the aspect.
	 * Triggered by per-clause pointcut on non-singleton aspect.
	 * The advice has no effect.
	 */
	@SuppressWarnings("serial")
	protected static class SyntheticInstantiationAdvisor extends DefaultPointcutAdvisor {

		public SyntheticInstantiationAdvisor(final MetadataAwareAspectInstanceFactory aif) {
			super(aif.getAspectMetadata().getPerClausePointcut(), new MethodBeforeAdvice() {
				public void before(Method method, Object[] args, Object target) {
					// Simply instantiate the aspect
					aif.getAspectInstance();
				}
			});
		}
	}

}
