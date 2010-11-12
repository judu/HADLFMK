/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.alma.hadl.fmk;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 *
 * @author judu
 */
public class Validator {

   public static Boolean checkRequired(Method m) {
      if(m == null) {
         return Boolean.FALSE;
      } else {
         return m.getParameterTypes().length == 1;
      }
   }


   public static Boolean checkRequired(Field f) {
      return f != null;
   }

   public static Boolean checkProvided(Method m) {
      if(m == null) {
         return Boolean.FALSE;
      } else {
         System.out.println(m.getParameterTypes().length);
         System.out.println(m.getReturnType().toString());
         return (m.getParameterTypes().length == 0) && (!m.getReturnType().equals(void.class));
      }
   }

   public static Boolean checkProvided(Field f) {
      if(f == null) {
         return Boolean.FALSE;
      } else if(f.isAccessible()) {
         return Boolean.TRUE;
      } else {
         return hasSetter(f);
      }
   }


   private static Boolean hasSetter(Field f) {
      Class c = f.getDeclaringClass();
      Method[] ms = c.getDeclaredMethods();
      String setterName = "set" + Character.toUpperCase(f.getName().charAt(0)) + f.getName().substring(1);


      for(Method m: ms) {
         if(m.isAccessible() && m.getName().equals(setterName)) {
            return Boolean.TRUE;
         }
      }
      return Boolean.FALSE;
   }
}
