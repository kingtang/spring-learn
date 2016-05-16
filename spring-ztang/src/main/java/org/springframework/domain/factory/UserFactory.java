package org.springframework.domain.factory;

import org.springframework.domain.User;

public class UserFactory {
	
	private int id;
	
	public static User getInstance()
	{
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
