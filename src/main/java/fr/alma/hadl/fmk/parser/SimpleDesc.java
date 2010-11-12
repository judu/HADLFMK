/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.alma.hadl.fmk.parser;

import sun.net.idn.StringPrep;

/**
 *
 * @author judu
 */
public class SimpleDesc extends CompDesc {

   public SimpleDesc(String name) {
      super(name);
   }

   @Override
   public void print(String prefix) {
      super.print(prefix);
      System.out.println(prefix + "}");
   }
}
