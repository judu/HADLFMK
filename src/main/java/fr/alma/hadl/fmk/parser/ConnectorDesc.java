/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.alma.hadl.fmk.parser;

/**
 *
 * @author judu
 */
public class ConnectorDesc extends DescElem {
   private String name;
   private String toC;
   private String toI;
   private String fromC;
   private String fromI;

   public ConnectorDesc(String name, String from, String to) {
      this.name = name;
      this.fromC = from.substring(0, from.indexOf("."));
      this.fromI = from.substring(from.indexOf(".")+ 1);
      this.toC = to.substring(0, to.indexOf("."));
      this.toI = to.substring(to.indexOf(".")+ 1);
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      final ConnectorDesc other = (ConnectorDesc) obj;
      if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
         return false;
      }
      if ((this.toC == null) ? (other.toC != null) : !this.toC.equals(other.toC)) {
         return false;
      }
      if ((this.toI == null) ? (other.toI != null) : !this.toI.equals(other.toI)) {
         return false;
      }
      if ((this.fromC == null) ? (other.fromC != null) : !this.fromC.equals(other.fromC)) {
         return false;
      }
      if ((this.fromI == null) ? (other.fromI != null) : !this.fromI.equals(other.fromI)) {
         return false;
      }
      return true;
   }

   @Override
   public int hashCode() {
      int hash = 7;
      hash = 97 * hash + (this.name != null ? this.name.hashCode() : 0);
      hash = 97 * hash + (this.toC != null ? this.toC.hashCode() : 0);
      hash = 97 * hash + (this.toI != null ? this.toI.hashCode() : 0);
      hash = 97 * hash + (this.fromC != null ? this.fromC.hashCode() : 0);
      hash = 97 * hash + (this.fromI != null ? this.fromI.hashCode() : 0);
      return hash;
   }

   

   @Override
   public String name() {
      return name;
   }

   public String from() {
      return fromC + "."+ fromI;
   }

   public String to() {
      return toC + "." + toI;
   }

   public String fromC() {
      return fromC;
   }

   public String fromI(){
      return fromI;
   }

   public String toC() {
      return toC;
   }

   public String toI(){
      return toI;
   }


   @Override
   public String toString() {
      return "Connector " + name + " {" +"from= " + from() +", to= " + to() + '}';
   }

   public String print(String prefix) {
      return prefix + toString() + "\n";
   }
}
