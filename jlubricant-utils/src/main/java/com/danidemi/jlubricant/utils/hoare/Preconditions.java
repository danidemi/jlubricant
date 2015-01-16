/*
 * Copyright 2014 danidemi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.danidemi.jlubricant.utils.hoare;

/**
 *
 */
public final class Preconditions {

    public static void paramNotNull(Object shouldNotBeNull) {
        paramNotNull("Parameter cannot be null", shouldNotBeNull);
    }
    
    public static void paramNotNull(String exceptionText, Object shouldNotBeNull) {
        if(shouldNotBeNull == null){
            throw new IllegalArgumentException(exceptionText);
        }
    }
    
    /** Throw an IllegalArgumentException when the condition is false. */
    public static void condition(String exceptionText, boolean shouldBeTrueThat) {
    	if(shouldBeTrueThat == false) {
    		  throw new IllegalArgumentException(exceptionText);
    	}
    }
    
    private Preconditions(){
    	throw new IllegalStateException("Not meant to be instantiated");
    }
  
}
