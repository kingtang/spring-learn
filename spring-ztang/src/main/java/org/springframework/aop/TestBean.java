package org.springframework.aop;

public class TestBean implements Test{
	public void test()
	{
		System.out.println("test bean");
	}
	
	public void test(String arg)
	{
		System.out.println("arg test");
	}
}
