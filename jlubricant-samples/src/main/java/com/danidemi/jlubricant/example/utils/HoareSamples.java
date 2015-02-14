package com.danidemi.jlubricant.example.utils;

import com.danidemi.jlubricant.utils.hoare.Preconditions;

public class HoareSamples {

	public static void main(String[] args) {
		
		createPerson("john", null);
		
	}

	private static void createPerson(String name, String surname) {
		Preconditions.paramNotNull(name);
		Preconditions.condition("should be longer", surname!=null && surname.length() >= 2);
	}
	
}
