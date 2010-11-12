/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.alma.hadl.fmk.parser;

/**
 *
 * @author judu
 */
public class BindingDesc extends DescElem {

   public String name;
   public String ref;

   public BindingDesc(String name, String ref) {
      this.name = name;
      this.ref = ref;
   }

   @Override
   public String name() {
      return name;
   }

   @Override
   public String toString() {
      return "Bind " + name + " to " + ref;
   }

   

   public void print(String prefix) {
      System.out.println(prefix + toString());
   }

   

}
