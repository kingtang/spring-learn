package org.springframework.domain.cycle;
//ѭ����������B
public class CycleB {
	
	private CycleA a;

	public CycleA getA() {
		return a;
	}

	public void setA(CycleA a) {
		this.a = a;
	}
}
