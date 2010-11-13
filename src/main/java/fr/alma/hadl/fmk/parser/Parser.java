/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.alma.hadl.fmk.parser;

import fr.alma.hadl.fmk.exceptions.ParseException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

/**
 *
 * @author judu
 */
public class Parser {

   private final static String CONFIG = "configuration";

   private final static String COMPONENT = "composant";

   private final static String PORT = "port";

   private final static String SERV = "service";

   private final static String CONNECTOR = "connector";

   private final static String FROM = "from";

   private final static String TO = "to";

   private final static String REF = "ref";

   private final static String NAME = "name";

   private final static String ATTR_SYM = "@";

   private final static String NAME_ATTR = ATTR_SYM + NAME;

   private final static String FROM_ATTR = ATTR_SYM + FROM;

   private final static String TO_ATTR = ATTR_SYM + TO;

   private final static String REF_ATTR = ATTR_SYM + REF;

   private Document document;

   private ConfDesc root;


   public Parser() {
   }

   public void parse(URL url) throws ParseException {

      try {
         SAXReader reader = new SAXReader();
         document = reader.read(url);

         Node rootElem = document.getRootElement();
         if (!CONFIG.equals(rootElem.getName())) {
            throw new ParseException("Root element should be of Configuration type.");
         }

         // Put the rootElem in root
         root = (ConfDesc) digitalize(rootElem);

      } catch (DocumentException ex) {
         Logger.getLogger(Parser.class.getName()).log(Level.SEVERE, null, ex);
      }
   }

   private CompDesc digitalize(Node currentElem) throws ParseException {
      // First : make a CompDesc
      CompDesc currentComp;
      if (CONFIG.equals(currentElem.getName())) {
         currentComp = new ConfDesc(currentElem.valueOf(NAME_ATTR));
      } else if (COMPONENT.equals(currentElem.getName())) {
         currentComp = new SimpleDesc(currentElem.valueOf(NAME_ATTR));
      } else {
         throw new ParseException("I got a bad markup : " + currentElem.getName());
      }

      // Then fill it.
      List<Node> comps = currentElem.selectNodes("./child::*");
      for (Node elem : comps) {
         String eName = elem.getName();
         String name = elem.valueOf(NAME_ATTR);

         if (CONFIG.equals(eName) || COMPONENT.equals(eName)) {
            if (currentComp instanceof SimpleDesc) {
               throw new ParseException("There is a component defined in a simple component. You should use a configuration markup.");
            }
            CompDesc comp = digitalize(elem);

            if (!((ConfDesc) currentComp).children.add(comp)) {
               throw new ParseException("The configuration " + currentComp.name() + " already contains a component named " + comp.name());
            }
         } else if (CONNECTOR.equals(eName)) {
            addConnector(currentComp, elem);
         } else if (PORT.equals(eName)) {
            addPort(currentComp, name, elem.valueOf(REF_ATTR));
         } else if (SERV.equals(eName)) {
            addService(currentComp, name, elem.valueOf(REF_ATTR));
         }
      }
      return currentComp;
   }

   private void addPort(CompDesc currComp, String name, String ref) throws ParseException {
      if (currComp.services.contains(name) || !currComp.ports.add(name)) {
         throw new ParseException("There already is an interface with the name : " + name);
      }

      if (currComp instanceof ConfDesc) {
         // we split 'to' to get the component name and the interface name
         String toComp = ref.substring(0, ref.indexOf("."));
         String toInterface = ref.substring(ref.indexOf(".") + 1);
         CompDesc tgtCmp = ((ConfDesc) currComp).getChild(toComp);
         if (tgtCmp != null && tgtCmp.ports.contains(toInterface)) {
            ((ConfDesc) currComp).bindings.add(new BindingDesc(name, ref));
         } else {
            throw new ParseException("The binding does not bind to an existing Component's port");
         }
      }
   }

   private void addService(CompDesc currComp, String name, String ref) throws ParseException {
      if (currComp.ports.contains(name) || !currComp.services.add(name)) {
         throw new ParseException("There already is an interface with the name : " + name);
      }
      if (currComp instanceof ConfDesc) {
         // we split 'to' to get the component name and the interface name
         String toComp = ref.substring(0, ref.indexOf("."));
         String toInterface = ref.substring(ref.indexOf(".") + 1);
         CompDesc tgtCmp = ((ConfDesc) currComp).getChild(toComp);
         if (tgtCmp != null && tgtCmp.services.contains(toInterface)) {
            ((ConfDesc) currComp).bindings.add(new BindingDesc(name, ref));
         } else {
            throw new ParseException("The binding '" + new BindingDesc(name, ref).toString() + "' does not bind to an existing Component's service");
         }
      }
   }

   private void addConnector(CompDesc currentComp, Node elem) throws ParseException {
      if (currentComp instanceof SimpleDesc) {
         throw new ParseException("Simple component should not have a connector.");
      }
      String cname = elem.valueOf(NAME_ATTR);
      String to = elem.valueOf(TO_ATTR);
      String from = elem.valueOf(FROM_ATTR);

      assertExists(((ConfDesc) currentComp), to);
      assertExists(((ConfDesc) currentComp), from);

      ((ConfDesc) currentComp).connectors.add(new ConnectorDesc(cname, from, to));
   }

   private void assertExists(ConfDesc currComp, String role) throws ParseException {
      String toComp = role.substring(0, role.indexOf("."));
      String toInterface = role.substring(role.indexOf(".") + 1);
      CompDesc tgtCmp = currComp.getChild(toComp);
      if(tgtCmp == null || (!tgtCmp.ports.contains(toInterface) && !tgtCmp.services.contains(toInterface))) {
         throw new ParseException("The interface " + role + " does not exist.");
      }
   }

   public void printLoadedSystem() {
      System.out.println("Loaded system :");
      root.print("");
   }

   public ConfDesc getArchitecture() {
      return root;
   }
}
