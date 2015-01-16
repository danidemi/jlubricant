This project offers a series of generic utilities that depend entirely on libraries available
in the JRE.

#EnvProperties

Like `java.util.Properties` but supports values in which you can reference an environment variable.

	Properties envProperties = new EnvProperties();
	envProperties.setProperty("pathdescription", "the path is ${PATH}");
	System.out.println( envProperties.getProperty("pathdescription") );
    
This prints outs...
    
	the path is /usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/games:/usr/local/games
    
#Preconditions

A series of static method that make easier to define preconditions on arguments.

	private static void createPerson(String name, String surname) {
		Preconditions.paramNotNull(name);
		Preconditions.condition("should be longer", surname!=null && surname.length() >= 2);
	}
	
If the surname is too short...

	Exception in thread "main" java.lang.IllegalArgumentException: should be longer
		at com.danidemi.jlubricant.utils.hoare.Preconditions.condition(Preconditions.java:37)
		at com.danidemi.jlubricant.samples.HoareSamples.createPerson(HoareSamples.java:15)
		at com.danidemi.jlubricant.samples.HoareSamples.main(HoareSamples.java:9)

#TypeUtils

Want to convert java SQL type numeric constants to their String counterparts ?

	System.out.println( TypesUtils.typeNameOf(java.sql.Types.BINARY) );
	
Produces...

	BINARY	
