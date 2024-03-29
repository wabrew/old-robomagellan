package com.builditboys.robots.infrastruture;

import com.builditboys.robots.infrastructure.ParameterInterface;
import com.builditboys.robots.infrastructure.ParameterServer;

public class ExampleParameter implements ParameterInterface {
	
	private int value1 = 42;
	private int value2 = 43;
	
	
	// having a getName makes you a parameter
	// do not have a setName since they you could change the name of a parameter 
	// and that would really mess things up
	String name;

	public String getName() {
		return name;
	}
	

	// --------------------------------------------------------------------------------

	public static ExampleParameter getParameter (String key) {
		return (ExampleParameter) ParameterServer.getParameter(key);
	}
	
	public static ExampleParameter maybeGetParameter (String key) {
		return (ExampleParameter) ParameterServer.maybeGetParameter(key);
	}
	
	// --------------------------------------------------------------------------------

	ExampleParameter (String name) {
		this.name = name;
	}
	
	
	public synchronized int getValue1() {
		return value1;
	}
	public synchronized void setValue1(int value1) {
		this.value1 = value1;
	}
	public synchronized int getValue2() {
		return value2;
	}
	public synchronized void setValue2(int value2) {
		this.value2 = value2;
	}

}
