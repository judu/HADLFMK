/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.alma.hadl.fmk.parser;

/**
 *
 * @author judu
 */
public class SimpleDesc extends CompDesc {

   public SimpleDesc(String name) {
      super(name);
   }

   @Override
   public String print(String prefix) {
      StringBuilder sb = new StringBuilder(super.print(prefix));
      return sb.append(prefix).append("}").toString();
   }
}
