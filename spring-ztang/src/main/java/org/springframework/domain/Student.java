package org.springframework.domain;

public class Student {
	
	private int id;
	
	public Student()
	{
		System.out.println("student constructor is invoked.");
		id = 3;
		Config.time = 1000;
	}
	
	public int getId()
	{
		return id;
	}
	
	public void setId(int id)
	{
		this.id = id;
	}
}