package org.springframework.domain;

public class Student {
	
	private int id;
	
	private User user;
	public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

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

	@Override
	public String toString() {
		return "Student [id=" + id + "]";
	}
}
