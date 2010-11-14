/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.alma.hadl.fmk;

import java.lang.reflect.Method;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author judu
 */
public class ValidatorTest {

   public ValidatorTest() {
   }

   @BeforeClass
   public static void setUpClass() throws Exception {
   }

   @AfterClass
   public static void tearDownClass() throws Exception {
   }

   @Before
   public void setUp() {
   }

   @After
   public void tearDown() {
   }

   /**
    * Test of checkRequired method, of class Validator.
    */
   @Test
   public void testCheckRequired() {
      System.out.println("checkRequired");
      Method m = null;
      try {
         m = TestAnnot.class.getMethod("testRequired", String.class);
      } catch (NoSuchMethodException ex) {
         fail("No such method : " + ex.getMessage());
      } catch (SecurityException ex) {
         fail("Not public : " + ex.getMessage());
      }

      Boolean expResult = Boolean.TRUE;
      Boolean result = Validator.checkRequired(m);
      assertEquals(expResult, result);
   }

   /**
    * Test of checkProvided method, of class Validator.
    */
   @Test
   public void testCheckProvided() {
      System.out.println("checkProvided");
      Method m = null;
      try {
         m = TestAnnot.class.getMethod("testProvided");
      } catch (NoSuchMethodException ex) {
         fail("No such method : " + ex.getMessage());
      } catch (SecurityException ex) {
         fail("Not public : " + ex.getMessage());
      }
      Boolean expResult = Boolean.TRUE;
      Boolean result = Validator.checkProvided(m);
      assertEquals(expResult, result);
   }
}
