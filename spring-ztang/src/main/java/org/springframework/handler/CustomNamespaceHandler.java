package org.springframework.handler;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class CustomNamespaceHandler extends NamespaceHandlerSupport
{

    public void init()
    {
        registerBeanDefinitionDecoratorForAttribute("customAttr", new CustomBeanDefinitionDecorator());
        System.out.println("��ʼ���Զ���Handler");
    }
    
}
