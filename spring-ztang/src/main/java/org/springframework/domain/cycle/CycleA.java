package org.springframework.domain.cycle;

//���ڲ���ѭ����������
public class CycleA {
	
	private CycleB b;

	public CycleB getB() {
		return b;
	}

	public void setB(CycleB b) {
		this.b = b;
	}
}
