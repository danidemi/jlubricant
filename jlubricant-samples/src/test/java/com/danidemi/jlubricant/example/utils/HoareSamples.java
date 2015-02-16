package com.danidemi.jlubricant.example.utils;

import org.junit.Test;

import com.danidemi.jlubricant.utils.hoare.Preconditions;

public class HoareSamples {
	
	@Test(expected=IllegalArgumentException.class) public void howUseHoareAssertions(){
		createPerson("john", null);					
	}

	private static void createPerson(String name, String surname) {
		Preconditions.paramNotNull(name);
		Preconditions.condition("should be longer", surname!=null && surname.length() >= 2);
	}
	
}
