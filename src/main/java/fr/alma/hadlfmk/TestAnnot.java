/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.alma.hadlfmk;

import fr.alma.hadl.annotations.Component;
import fr.alma.hadl.annotations.ProvidedInterface;
import fr.alma.hadl.annotations.RequiredInterface;


/**
 *
 * @author judu
 */
@Component("youpi")
public class TestAnnot extends fr.univnantes.alma.hadlm2.composant.SimpleComposant {

   @RequiredInterface("parici")
   public void testRequired(String value) {
      System.out.println(value);
   }

   @ProvidedInterface("parla")
   public String testProvided() {
      return "lol";
   }
   

}
