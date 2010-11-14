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

   private String name;
   private String cRef;
   private String iRef;

   public BindingDesc(String name, String comp, String interf) {
      this.name = name;
      this.cRef = comp;
      this.iRef = interf;
   }

   @Override
   public String name() {
      return name;
   }

   public String cRef() {
      return cRef;
   }

   public String iRef(){
      return iRef;
   }

   public String ref() {
      return cRef +"." + iRef;
   }

   @Override
   public String toString() {
      return name + " -> " + ref();
   }

   public String print(String prefix) {
      return prefix + toString() + "\n";
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      final BindingDesc other = (BindingDesc) obj;
      if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
         return false;
      }
      if ((this.cRef == null) ? (other.cRef != null) : !this.cRef.equals(other.cRef)) {
         return false;
      }
      if ((this.iRef == null) ? (other.iRef != null) : !this.iRef.equals(other.iRef)) {
         return false;
      }
      return true;
   }

   @Override
   public int hashCode() {
      int hash = 7;
      hash = 13 * hash + (this.name != null ? this.name.hashCode() : 0);
      hash = 13 * hash + (this.cRef != null ? this.cRef.hashCode() : 0);
      hash = 13 * hash + (this.iRef != null ? this.iRef.hashCode() : 0);
      return hash;
   }

   

   

}
