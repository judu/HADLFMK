/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.alma.hadl.fmk.parser;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author judu
 */
public abstract class CompDesc {

   private String name;

   public Set<String> ports;

   public Set<String> services;

   public CompDesc(String name) {
      this.name = name;
      this.ports = new HashSet<String>();
      this.services = new HashSet<String>();
   }

   public final String name() {
      return name;
   }

   public String print(String prefix) {
      if(prefix == null) prefix = "";
      StringBuilder strb = new StringBuilder();
      strb.append(prefix).append("Component ").append(name).append(" {").append('\n');
      strb.append(prefix).append("\tports : ").append(ports).append('\n');
      strb.append(prefix).append("\tservices : ").append(services).append('\n');
      return strb.toString();
      
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      final CompDesc other = (CompDesc) obj;
      if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
         return false;
      }
      return true;
   }

   @Override
   public int hashCode() {
      int hash = 7;
      hash = 83 * hash + (this.name != null ? this.name.hashCode() : 0);
      return hash;
   }


   
}
