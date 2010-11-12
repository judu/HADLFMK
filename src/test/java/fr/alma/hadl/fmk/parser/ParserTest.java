/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.alma.hadl.fmk.parser;

import fr.alma.hadl.fmk.exceptions.ParseException;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Assert.*;

/**
 *
 * @author judu
 */
public class ParserTest extends TestCase {

    public ParserTest() {
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
    * Test of parse method, of class Parser.
    */
   @Test
   public void testParse() throws Exception {
      System.out.println("parse");
      File f = new File("CSArch.xml");
      URL url = f.toURI().toURL();
      Parser instance = new Parser();
      instance.parse(url);

      instance.printLoadedSystem();
   }


   @Test
   public void testParseBindStoP() throws MalformedURLException {
      System.out.println("parse malformed file");
      File f = new File("CSArch-BSP.xml");
      URL url = f.toURI().toURL();
      Parser instance = new Parser();
      try {
         instance.parse(url);
         fail("Should have thrown exception");
      } catch (ParseException ex) {
      }
   }
   
   
   @Test
   public void testParseDoubleComp() throws MalformedURLException {
      System.out.println("parse conf with same comp name");
      File f = new File("CSArch-SCN.xml");
      URL url = f.toURI().toURL();
      Parser instance = new Parser();
      try {
         instance.parse(url);
         fail("Should have thrown exception");
      } catch (ParseException ex) {
         System.out.println(ex.getMessage());
      }
   }

   @Test
   public void testParseCompInComp() throws MalformedURLException {
      System.out.println("parse simple component with component in it");
      File f = new File("CSArch-SCC.xml");
      URL url = f.toURI().toURL();
      Parser instance = new Parser();
      try {
         instance.parse(url);
         fail("Should have thrown exception");
      } catch (ParseException ex) {
         System.out.println(ex.getMessage());
      }
   }

}