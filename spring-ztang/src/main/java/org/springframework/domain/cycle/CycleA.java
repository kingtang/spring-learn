package org.springframework.domain.cycle;

//用于测试循环依赖场景
public class CycleA {
	
	private CycleB b;

	public CycleB getB() {
		return b;
	}

	public void setB(CycleB b) {
		this.b = b;
	}
}
