/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.alma.hadlfmk;

import java.io.File;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author judu
 */
public class SystemManagerTest {

    public SystemManagerTest() {
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
    * Test of getInstance method, of class SystemManager.
    */
   @Test
   public void testGetInstance() {
      System.out.println("getInstance");
      SystemManager result = SystemManager.getInstance();
      File f = new File("target/test.jar");
      System.out.println(f.exists());
      result.loadAnnotatedComposants("target/test.jar");
      
      System.out.println("Size = " + result.getComposants().size());
      
   }

}