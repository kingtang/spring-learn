package org.springframework.domain.cycle;
//—≠ª∑“¿¿µ∂‘œÛB
public class CycleB {
	
	private CycleA a;

	public CycleA getA() {
		return a;
	}

	public void setA(CycleA a) {
		this.a = a;
	}
}
