package org.springframework.handler;

import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.xml.BeanDefinitionDecorator;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class CustomBeanDefinitionDecorator implements BeanDefinitionDecorator
{

    public BeanDefinitionHolder decorate(Node node, BeanDefinitionHolder definition, ParserContext parserContext)
    {
        NamedNodeMap attributes = node.getAttributes();
        Node namedItem = attributes.getNamedItem("customAttr");
        System.out.println("解析自定义属性");
        return definition;
    }
    
}
