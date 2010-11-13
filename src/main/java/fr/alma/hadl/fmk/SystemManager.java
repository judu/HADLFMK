/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.alma.hadl.fmk;

import fr.alma.hadl.annotations.Component;
import fr.alma.hadl.annotations.Connector;
import fr.alma.hadl.annotations.ProvidedInterface;
import fr.alma.hadl.annotations.RequiredInterface;
import fr.alma.hadl.fmk.exceptions.ArchitectureException;
import fr.alma.hadl.fmk.exceptions.ParseException;
import fr.alma.hadl.fmk.parser.CompDesc;
import fr.alma.hadl.fmk.parser.ConfDesc;
import fr.alma.hadl.fmk.parser.ConnectorDesc;
import fr.alma.hadl.fmk.parser.Parser;
import fr.univnantes.alma.hadlm2.composant.Composant;
import fr.univnantes.alma.hadlm2.composant.Configuration;
import fr.univnantes.alma.hadlm2.connecteur.Connecteur;
import fr.univnantes.alma.hadlm2.connecteur.ConnecteurPP;
import fr.univnantes.alma.hadlm2.connecteur.ConnecteurPS;
import fr.univnantes.alma.hadlm2.exceptions.NoSuchComponentException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.scannotation.AnnotationDB;
import org.scannotation.ClasspathUrlFinder;

/**
 *
 * @author judu
 */
public class SystemManager {

   private static SystemManager instance;

   public static SystemManager getInstance() {
      if (instance == null) {
         instance = new SystemManager();
      }
      return instance;
   }
   private File jarBinks;

   private Configuration system;

   private AnnotationDB annotdb;

   private ConfDesc architecture;

   private SystemManager() {
   }

   public void init(String jarPath) throws ParseException, ArchitectureException {
      jarBinks = new File(jarPath);
      loadDesc();
      loadSystem();
   }

   public void loadSystem() throws ArchitectureException {
      annotdb = new AnnotationDB();
      annotdb.setScanClassAnnotations(Boolean.TRUE);
      annotdb.setScanFieldAnnotations(Boolean.TRUE);
      annotdb.setScanMethodAnnotations(Boolean.TRUE);
      annotdb.setScanParameterAnnotations(Boolean.FALSE);
      try {
         annotdb.scanArchives(jarBinks.toURI().toURL());
      } catch (IOException ex) {
         Logger.getLogger(SystemManager.class.getName()).log(Level.SEVERE, null, ex);
      }
      try {
         system = (Configuration) instanciate(architecture);
      } catch (ClassNotFoundException ex) {
         Logger.getLogger(SystemManager.class.getName()).log(Level.SEVERE, null, ex);
      }

   }

   public void loadDesc() throws ParseException {
      URL jarURL;
      try {
         jarURL = new URL("jar", "", "file:" + jarBinks.getAbsolutePath() + "!/");
         System.out.println(jarURL.toString());


         URLClassLoader cl = new URLClassLoader(new URL[]{jarURL});
         URL archiURL = cl.findResource("META-INF/architecture.xml");
         Parser parser = new Parser();
         parser.parse(archiURL);
         parser.printLoadedSystem();

         architecture = parser.getArchitecture();

      } catch (MalformedURLException ex) {
         Logger.getLogger(SystemManager.class.getName()).log(Level.SEVERE, null, ex);
      }
   }

   private Composant instanciate(CompDesc element) throws ClassNotFoundException, ArchitectureException {

      Composant currentComp = null;

      String name = element.name();
      Set<String> classes = annotdb.getAnnotationIndex().get(Component.class.getName());



      for (String clName : classes) {
         Class compC = ClassLoader.getSystemClassLoader().loadClass(clName);
         if (Composant.class.isAssignableFrom(compC)) {
            try {
               Composant comp = (Composant) compC.newInstance();
               String value = comp.getClass().getAnnotation(Component.class).value();
               if (name.equals(value)) {
                  checkInterfaces(comp, element); //DONE
                  // On a trouvé la classe qui va bien ! il faut l'ajouter au système, et passer à la suite.
                  if (element instanceof ConfDesc) {
                     if (comp instanceof Configuration) {
                        for (CompDesc cm : ((ConfDesc) element).children) {
                           ((Configuration) comp).addComposant(instanciate(cm));
                        }
                        addConnectors((Configuration) comp, (ConfDesc) element); //DONE
                        addBindings((Configuration) comp, (ConfDesc) element); //TODO
                     } else {
                        throw new ArchitectureException("The " + element.name() + " node is configuration and refers to a simplecomposant.");
                     }
                  } else {
                     throw new ArchitectureException("The " + element.name() + " node is simplecomposant and refers to a configuration.");
                  }
               }
            } catch (InstantiationException ex) {
               Logger.getLogger(SystemManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
               Logger.getLogger(SystemManager.class.getName()).log(Level.SEVERE, null, ex);
            }
         }


      }

      return currentComp;
   }

   private void checkInterfaces(Composant comp, CompDesc element) throws ClassNotFoundException, InstantiationException, IllegalAccessException, ArchitectureException {

      // Il faut vérifier chaque port de element. Pour chaque, il faut vérifier qu'on a bien un
      // Field dans la classe de comp, qui est soit required soit provided.

      Set<String> rIntNames = annotdb.getAnnotationIndex().get(RequiredInterface.class.getName());
      Set<String> pIntNames = annotdb.getAnnotationIndex().get(ProvidedInterface.class.getName());

      for (String pName : element.ports) {
         // Check in required
         if (!isOneOf(pName, rIntNames, comp)) {
            if (!isOneOf(pName, pIntNames, comp)) {
               throw new ArchitectureException("Cannot find port " + pName);
            }
         }
         // Check in provided
      }

      // Check methods
      for (String sName : element.services) {
         if (!isOneOf(sName, rIntNames, comp) && !isOneOf(sName, pIntNames, comp)) {
            throw new ArchitectureException("Cannot find service " + sName);
         }
      }

   }

   private void addConnectors(Configuration comp, ConfDesc element) throws ClassNotFoundException, ArchitectureException, InstantiationException, IllegalAccessException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException, NoSuchComponentException {

      for (ConnectorDesc cd : element.connectors) {
         String name = cd.name();
         Set<String> index = annotdb.getAnnotationIndex().get(Connector.class.getName());
         for (String connectorClassName : index) {
            Class connectorClass = ClassLoader.getSystemClassLoader().loadClass(connectorClassName);
            Composant fromC = getComposant(comp, cd.from());
            Composant toC = getComposant(comp, cd.to());
            AccessibleObject fromInt = getInterface(comp, cd.from(), Boolean.TRUE);
            AccessibleObject toInt = getInterface(comp, cd.to(), Boolean.FALSE);


            Connecteur newConn = null;

            if (fromInt instanceof Field) {
               if (toInt instanceof Field) {
                  if (ConnecteurPP.class.isAssignableFrom(connectorClass)) {
                     newConn = (Connecteur) connectorClass.getConstructor(Composant.class, Field.class, Composant.class, Field.class).newInstance(fromC, (Field) fromInt, toC, (Field) toInt);
                  } else {
                     throw new ArchitectureException(name + " should be a ConnecteurPP");
                  }
               } else {
                  if (ConnecteurPS.class.isAssignableFrom(connectorClass)) {
                     newConn = (Connecteur) connectorClass.getConstructor(Composant.class, Field.class, Composant.class, Method.class).newInstance(fromC, fromInt, toC, toInt);
                  } else {
                     throw new ArchitectureException(name + " should be a ConnecteurPS");
                  }
               }
            } else {
               if (toInt instanceof Field) {
                  if (ConnecteurPP.class.isAssignableFrom(connectorClass)) {
                     newConn = (Connecteur) connectorClass.getConstructor(Composant.class, Method.class, Composant.class, Field.class).newInstance(fromC, (Field) fromInt, toC, (Field) toInt);
                  } else {
                     throw new ArchitectureException(name + " should be a ConnecteurSP");
                  }
               } else {
                  if (ConnecteurPP.class.isAssignableFrom(connectorClass)) {
                     newConn = (Connecteur) connectorClass.getConstructor(Composant.class, Method.class, Composant.class, Method.class).newInstance(fromC, fromInt, toC, toInt);
                  } else {
                     throw new ArchitectureException(name + " should be a ConnecteurSS");
                  }
               }
            }
            comp.addConnecteur(newConn);

         }
      }


      throw new UnsupportedOperationException("Not yet implemented");
   }

   private void checkBindings(Composant comp, CompDesc element) {
      throw new UnsupportedOperationException("Not yet implemented");
   }

   private boolean isOneOf(String pName, Set<String> intNames, Composant comp) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
      for (String portName : intNames) {
         Class intC = ClassLoader.getSystemClassLoader().loadClass(portName);
         if (AccessibleObject.class.isAssignableFrom(intC) && Member.class.isAssignableFrom(intC)) {
            // It's a port.
            AccessibleObject rport = (AccessibleObject) intC.newInstance();

            // Faut check qu'il est bien de la bonne classe
            if (((Member) rport).getDeclaringClass().equals(comp.getClass())) {
               // Faut check que l'annotation a bien pName comme value.
               RequiredInterface annotation = rport.getAnnotation(RequiredInterface.class);
               if (annotation == null) {
                  ProvidedInterface annotation1 = rport.getAnnotation(ProvidedInterface.class);
                  if (annotation1.value().equals(pName)) {
                     return true;
                  }
               } else {
                  if (annotation.value().equals(pName)) {
                     return true;
                  }
               }

            }
         }

      }
      return false;
   }

   private AccessibleObject getInterface(Configuration conf, String intName, Boolean from) throws ArchitectureException {
      String cName = intName.substring(0, intName.indexOf("."));
      String iName = intName.substring(intName.indexOf(".") + 1);

      Composant cmp = getComposant(conf, intName);
      for (int i = 0; i < cmp.getClass().getFields().length; ++i) {
         String fieldName = null;
         if (from) {
            fieldName = cmp.getClass().getFields()[i].getAnnotation(ProvidedInterface.class).value();
            if (!Validator.checkProvided(cmp.getClass().getFields()[i])) {
               throw new ArchitectureException("The Field " + cmp.getClass().getFields()[i].getName() + " should be public.");
            }

         } else {
            fieldName = cmp.getClass().getFields()[i].getAnnotation(RequiredInterface.class).value();
            if (!Validator.checkRequired(cmp.getClass().getFields()[i])) {
               throw new ArchitectureException("The Field " + cmp.getClass().getFields()[i].getName() + " should be public.");
            }
         }

         if (fieldName.equals(iName)) {
            return cmp.getClass().getFields()[i];
         }
      }

      for (int i = 0; i < cmp.getClass().getMethods().length; ++i) {
         String methName = null;
         if (from) {
            methName = cmp.getClass().getMethods()[i].getAnnotation(ProvidedInterface.class).value();
            if (!Validator.checkProvided(cmp.getClass().getMethods()[i])) {
               throw new ArchitectureException("The Method " + cmp.getClass().getMethods()[i].getName() + " should be public.");
            }
         } else {
            methName = cmp.getClass().getMethods()[i].getAnnotation(RequiredInterface.class).value();
            if (!Validator.checkRequired(cmp.getClass().getMethods()[i])) {
               throw new ArchitectureException("The Method " + cmp.getClass().getMethods()[i].getName() + " should be public.");
            }
         }
         if (methName.equals(iName)) {
            return cmp.getClass().getMethods()[i];
         }
      }
      throw new ArchitectureException(
              "No field or method for interface " + intName);
   }

   private Composant getComposant(Configuration conf, String intName) throws ArchitectureException {
      String cName = intName.substring(0, intName.indexOf("."));

      for (Composant cmp : conf.getComposants()) {
         if (cmp.getClass().getAnnotation(Component.class).value().equals(cName)) {
            return cmp;
         }
      }
      throw new ArchitectureException("No component found for name " + cName);
   }
}
