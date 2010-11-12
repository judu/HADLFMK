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
public class ConfDesc extends CompDesc {
   public Set<CompDesc> children;

   public CompStore<ConnectorDesc> connectors;
   public CompStore<BindingDesc> bindings;

   public ConfDesc(String name) {
      super(name);
      connectors = new CompStore<ConnectorDesc>();
      bindings = new CompStore<BindingDesc>();
      children = new HashSet<CompDesc>();
   }


   public CompDesc getChild(String cName) {
      for(CompDesc child: children) {
         if(child.name().equals(cName)) {
            return child;
         }
      }
      return null;
   }

   @Override
   public void print(String prefix) {
      super.print(prefix);
      System.out.println(prefix + " children : {");
      for(CompDesc c:children) {
         c.print("   ");
      }
      System.out.println(prefix + " }");
      System.out.println(prefix + " connectors : {");
      for(ConnectorDesc cd: connectors) {
         cd.print("  ");
      }
      System.out.println(prefix + " }");
      System.out.println(prefix + " bindings : {");
      for(BindingDesc bd:bindings) {
         bd.print("  ");
      }
      System.out.println(prefix + " }");
      System.out.println(prefix + "}");
   }
}
