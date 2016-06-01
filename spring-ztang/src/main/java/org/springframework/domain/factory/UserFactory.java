package org.springframework.domain.factory;

import org.springframework.domain.User;

public class UserFactory {
	
	private int id;
	
	public static User getInstance()
	{
		return new User();
	}
	
	protected static User getInstance(String id)
	{
		return new User();
	}
	
	public static User getInstance(String id,int num)
	{
		System.out.println(id);
		System.out.println(num);
		return new User();
	}
	
	
	public void setId(int id)
	{
		this.id = id;
	}
	
	public int getId()
	{
		return id;
	}
	
}
