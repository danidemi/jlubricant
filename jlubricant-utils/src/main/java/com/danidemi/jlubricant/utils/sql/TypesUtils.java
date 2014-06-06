package com.danidemi.jlubricant.utils.sql;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.Closure;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;

/**
 * Retrieve the SQL type name corresponding to the int constants contained in {@link java.sql.Types}.
 * @author danidemi
 */
public class TypesUtils {
	
	private static final Map<Integer, String> intToName;	
	
	static {
		intToName = new HashMap<Integer, String>( inspectTypes() );
	}

	/**
	 * Retrieve the SQL type name corresponding to the int constants contained in {@link java.sql.Types}.
	 * Return <code>null</code> if type is not found.
	 */
	public static String typeNameOf(int sqlType) {
		return intToName.get(sqlType);
	}

	private static Map<Integer, String> inspectTypes() {
		Collection<Field> fields = CollectionUtils.select(Arrays.asList( java.sql.Types.class.getFields() ), (Predicate<? super Field>) new Predicate<Field>() {
			@Override
			public boolean evaluate(Field f) {
				
						return Modifier.isStatic(f.getModifiers())
								&& f.getType().getName().equals("int");
			}
		});
				
		final HashMap<Integer, String> intToName = new HashMap<Integer, String>( 0 );
		
		CollectionUtils.forAllDo(fields, (Closure<? super Field>) new Closure<Field>() {

			@Override
			public void execute(Field arg0) {
				try {
					String name = arg0.getName();
					Integer val;
					val = (Integer)( arg0.get(java.sql.Types.class) );
					intToName.put(val, name );
				} catch (IllegalArgumentException | IllegalAccessException e) {
					// ok, we cannot access it
				}
			}
		});
		return intToName;
	}


	
}
