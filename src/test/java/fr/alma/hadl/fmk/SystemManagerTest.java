/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.alma.hadl.fmk;

import fr.alma.hadl.fmk.exceptions.ParseException;
import java.io.IOException;
import java.util.logging.Logger;
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
   public void testInit() throws ParseException, IOException, ClassNotFoundException {
      Logger.getAnonymousLogger().info("init");
      SystemManager instance = SystemManager.getInstance();
      instance.init("m1.jar");
   }

}