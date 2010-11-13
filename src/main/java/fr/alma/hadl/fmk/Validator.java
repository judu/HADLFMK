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
         return (m.isAccessible()) && (m.getParameterTypes().length == 1);
      }
   }


   public static Boolean checkRequired(Field f) {
      if(f == null) {
         return Boolean.FALSE;
      } else if(f.isAccessible()) {
         return Boolean.TRUE;
      } else {
         return Boolean.FALSE;
      }
   }

   public static Boolean checkProvided(Method m) {
      if(m == null) {
         return Boolean.FALSE;
      } else {
         return (m.isAccessible()) && (m.getParameterTypes().length == 0) && (!m.getReturnType().equals(void.class));
      }
   }

   public static Boolean checkProvided(Field f) {
      if(f == null) {
         return Boolean.FALSE;
      } else if(f.isAccessible()) {
         return Boolean.TRUE;
      } else {
         return Boolean.FALSE;
      }
   }
}
