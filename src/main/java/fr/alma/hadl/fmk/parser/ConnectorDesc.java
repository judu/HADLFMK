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
   public String name;
   public String to;
   public String from;

   public ConnectorDesc(String name, String from, String to) {
      this.name = name;
      this.from = from;
      this.to = to;
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
      if ((this.to == null) ? (other.to != null) : !this.to.equals(other.to)) {
         return false;
      }
      if ((this.from == null) ? (other.from != null) : !this.from.equals(other.from)) {
         return false;
      }
      return true;
   }

   @Override
   public int hashCode() {
      int hash = 7;
      hash = 73 * hash + (this.name != null ? this.name.hashCode() : 0);
      hash = 73 * hash + (this.to != null ? this.to.hashCode() : 0);
      hash = 73 * hash + (this.from != null ? this.from.hashCode() : 0);
      return hash;
   }

   @Override
   public String name() {
      return name;
   }

   @Override
   public String toString() {
      return "Connector " + name + " {" +"from= " + from +", to= " + to + '}';
   }

   public void print(String prefix) {
      System.out.println(prefix + toString());
   }
}
