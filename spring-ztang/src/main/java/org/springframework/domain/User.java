package org.springframework.domain;

/**
 * Hello world!
 *
 */
public class User 
{
    private String name;
    
    public User()
    {
    	System.out.println(Config.time);
    }
    
    public String getName()
    {
    	return name;
    }
    
    public void setName(String name)
    {
    	this.name = name;
    }
    
}
