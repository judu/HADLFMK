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

   public Set<ConnectorDesc> connectors;

   public Set<BindingDesc> bindings;

   public EntryPointDesc entrypoint;

   public ConfDesc(String name) {
      super(name);
      entrypoint = null;
      connectors = new HashSet<ConnectorDesc>();
      bindings = new HashSet<BindingDesc>();
      children = new HashSet<CompDesc>();
   }

   public CompDesc getChild(String cName) {
      for (CompDesc child : children) {
         if (child.name().equals(cName)) {
            return child;
         }
      }
      return null;
   }

   @Override
   public String print(String prefix) {
      String superPrint = super.print(prefix);
      StringBuilder sb = new StringBuilder(superPrint);
      sb.append(prefix).append("\tchildren : {\n");

      for (CompDesc c : children) {
         sb.append(c.print(prefix + "\t\t")).append('\n');
      }
      sb.append(prefix).append("\t}\n");
      sb.append(prefix).append("\tconnectors : {\n");
      for (ConnectorDesc cd : connectors) {
         sb.append(cd.print(prefix + "\t\t"));
      }
      sb.append(prefix).append("\t}\n");
      sb.append(prefix).append("\tbindings : {\n");
      for (BindingDesc bd : bindings) {
         sb.append(bd.print(prefix + "\t\t"));
      }

      sb.append(prefix).append("\t}\n");
      if (entrypoint != null) {
         sb.append(prefix).append("\tentrypoint : ").append(entrypoint.print(""));
      }
      return sb.append(prefix).append("}").toString();
   }
}
