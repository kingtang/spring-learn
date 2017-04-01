package org.springframework.handler;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class CustomNamespaceHandler extends NamespaceHandlerSupport
{

    public void init()
    {
        registerBeanDefinitionDecoratorForAttribute("customAttr", new CustomBeanDefinitionDecorator());
        System.out.println("初始化自定义Handler");
    }
    
}
