/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.alma.hadl.fmk;

import fr.alma.hadl.annotations.Component;
import fr.alma.hadl.annotations.Connector;
import fr.alma.hadl.annotations.ProvidedInterface;
import fr.alma.hadl.annotations.RequiredInterface;
import fr.alma.hadl.annotations.RunInterface;
import fr.alma.hadl.fmk.exceptions.ArchitectureException;
import fr.alma.hadl.fmk.exceptions.ParseException;
import fr.alma.hadl.fmk.parser.BindingDesc;
import fr.alma.hadl.fmk.parser.CompDesc;
import fr.alma.hadl.fmk.parser.ConfDesc;
import fr.alma.hadl.fmk.parser.ConnectorDesc;
import fr.alma.hadl.fmk.parser.EntryPointDesc;
import fr.alma.hadl.fmk.parser.Parser;
import fr.univnantes.alma.hadlm2.composant.Composant;
import fr.univnantes.alma.hadlm2.composant.Configuration;
import fr.univnantes.alma.hadlm2.connecteur.Connecteur;
import fr.univnantes.alma.hadlm2.connecteur.ConnecteurPP;
import fr.univnantes.alma.hadlm2.connecteur.ConnecteurPS;
import fr.univnantes.alma.hadlm2.connecteur.ConnecteurSP;
import fr.univnantes.alma.hadlm2.connecteur.ConnecteurSS;
import fr.univnantes.alma.hadlm2.exceptions.NoSuchComponentException;
import fr.univnantes.alma.hadlm2.exceptions.NoSuchInterfaceException;
import fr.univnantes.alma.hadlm2.exceptions.WrongTypeException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.scannotation.AnnotationDB;

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

   private ClassLoader loader;

   private SystemManager() {
   }

   public void init(String jarPath) throws IOException, ClassNotFoundException {
      jarBinks = new File(jarPath);
      try {
         loadDesc();
      } catch (ParseException ex) {
         Logger.getLogger(SystemManager.class.getName()).log(Level.SEVERE, "Error while parsing the xml file : {0}", ex);
         System.exit(1);
      }
      try {
         loadSystem();
         Logger.getAnonymousLogger().info("System loaded.");
      } catch (ArchitectureException ex) {
         Logger.getLogger(SystemManager.class.getName()).log(Level.SEVERE, "Error while loading the system : {0}", ex);
         System.exit(1);
      } catch (NoSuchMethodException ex) {
         Logger.getLogger(SystemManager.class.getName()).log(Level.SEVERE, "Error while loading the system : {0}", ex);
         System.exit(1);
      } catch (IllegalArgumentException ex) {
         Logger.getLogger(SystemManager.class.getName()).log(Level.SEVERE, "Error while loading the system : {0}", ex);
         System.exit(1);
      } catch (InvocationTargetException ex) {
         Logger.getLogger(SystemManager.class.getName()).log(Level.SEVERE, "Error while loading the system : {0}", ex);
         System.exit(1);
      } catch (NoSuchComponentException ex) {
         Logger.getLogger(SystemManager.class.getName()).log(Level.SEVERE, "Error while loading the system : {0}", ex);
         System.exit(1);
      } catch (NoSuchInterfaceException ex) {
         Logger.getLogger(SystemManager.class.getName()).log(Level.SEVERE, "Error while loading the system : {0}", ex);
         System.exit(1);
      } catch (WrongTypeException ex) {
         Logger.getLogger(SystemManager.class.getName()).log(Level.SEVERE, "Error while loading the system : {0}", ex);
         System.exit(1);
      }
   }

   public void run() {
      Method entryPoint = system.getEntryPoint();
      if(entryPoint != null) {
         Logger.getAnonymousLogger().log(Level.INFO, "EntryPoint : {0}", entryPoint.getName());
         system.call(entryPoint.getName());
      }
   }

   public void loadSystem() throws ArchitectureException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException, NoSuchComponentException, NoSuchInterfaceException, WrongTypeException, IOException, ClassNotFoundException {
      annotdb = new AnnotationDB();
      annotdb.setScanClassAnnotations(Boolean.TRUE);
      annotdb.setScanFieldAnnotations(Boolean.TRUE);
      annotdb.setScanMethodAnnotations(Boolean.TRUE);
      annotdb.setScanParameterAnnotations(Boolean.FALSE);
      annotdb.scanArchives(jarBinks.toURI().toURL());
      system = (Configuration) instanciate(architecture);
   }

   public void loadDesc() throws ParseException {
      URL jarURL;
      try {
         jarURL = new URL("jar", "", "file:" + jarBinks.getAbsolutePath() + "!/");
         Logger.getAnonymousLogger().info(jarURL.toString());

         loader = new URLClassLoader(new URL[]{jarURL});
         URL archiURL = loader.getResource("META-INF/architecture.xml");
         Parser parser = new Parser();
         parser.parse(archiURL);
         parser.printLoadedSystem();

         architecture = parser.getArchitecture();

      } catch (MalformedURLException ex) {
         Logger.getLogger(SystemManager.class.getName()).log(Level.SEVERE, null, ex);
      }
   }

   private Composant instanciate(CompDesc element) throws ClassNotFoundException, ArchitectureException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException, NoSuchComponentException, NoSuchInterfaceException, WrongTypeException {
      Logger.getAnonymousLogger().log(Level.INFO, ">>> Instanciate : {0}", element.name());

      String name = element.name();
      Set<String> classes = annotdb.getAnnotationIndex().get(Component.class.getName());

      for (String clName : classes) {
         Class compC = loader.loadClass(clName);
         if (Composant.class.isAssignableFrom(compC)) {
            try {
               Composant compTest = (Composant) compC.newInstance();
               String value = compTest.getClass().getAnnotation(Component.class).value();
               if (name.equals(value)) {
                  checkInterfaces(compTest, element); //DONE
                  // On a trouvé la classe qui va bien ! il faut l'ajouter au système, et passer à la suite.
                  if (element instanceof ConfDesc) {
                     if (compTest instanceof Configuration) {
                        for (CompDesc cm : ((ConfDesc) element).children) {
                           Logger.getAnonymousLogger().log(Level.INFO, "Adding component {0} to configuration {1}...", new Object[]{cm.name(), element.name()});
                           ((Configuration) compTest).addComposant(instanciate(cm));
                        }
                        addConnectors((Configuration) compTest, (ConfDesc) element); //DONE
                        addBindings((Configuration) compTest, (ConfDesc) element); //DONE
                     } else {
                        throw new ArchitectureException("The " + element.name() + " node is configuration and refers to a simplecomposant.");
                     }
                  } else if (compTest instanceof Configuration) {
                     throw new ArchitectureException("The " + element.name() + " node is simplecomposant and refers to a configuration.");
                  }
                  return compTest;
               }
            } catch (InstantiationException ex) {
               Logger.getLogger(SystemManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
               Logger.getLogger(SystemManager.class.getName()).log(Level.SEVERE, null, ex);
            }
         } else {
            throw new ArchitectureException("Only Composant class should be annotated with Component annotation.");
         }
      }
      throw new ArchitectureException("No component found with name " + name);
   }

   private void checkInterfaces(Composant comp, CompDesc element) throws InstantiationException, IllegalAccessException, ArchitectureException, NoSuchInterfaceException, ClassNotFoundException {

      Logger.getAnonymousLogger().log(Level.INFO, "Checking interfaces for component {0}...", element.name());

      for (String pName : element.ports) {
         Field f = (Field) comp.getInterfaceForName(pName, Boolean.TRUE);
         if (f == null) {
            f = (Field) comp.getInterfaceForName(pName, Boolean.FALSE);
            if (f == null) {
               throw new NoSuchInterfaceException("Cannot find port " + pName);
            }
         }
      }

      for (String sName : element.services) {
         Method m = (Method) comp.getInterfaceForName(sName, Boolean.TRUE);
         if (m == null) {
            m = (Method) comp.getInterfaceForName(sName, Boolean.FALSE);
            if (m == null) {
               throw new NoSuchInterfaceException("Cannot find service " + element.name() + "." + sName);
            }
         }
      }

   }

   private void addConnectors(Configuration conf, ConfDesc element) throws ClassNotFoundException, ArchitectureException, InstantiationException, IllegalAccessException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException, NoSuchComponentException {

      for (ConnectorDesc cd : element.connectors) {
         addConnector(conf, cd);
      }
   }

   private void addConnector(Configuration conf, ConnectorDesc cd) throws ClassNotFoundException, ArchitectureException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchComponentException {
      String name = cd.name();
      Set<String> index = annotdb.getAnnotationIndex().get(Connector.class.getName());
      for (String connectorClassName : index) {
         Class connectorClass = loader.loadClass(connectorClassName);
         Composant fromC = getComposant(conf, cd.fromC());
         Composant toC = getComposant(conf, cd.toC());
         AccessibleObject fromInt = getInterface(conf, cd.fromC(), cd.fromI(), Boolean.TRUE);
         AccessibleObject toInt = getInterface(conf, cd.toC(), cd.toI(), Boolean.FALSE);

         Connecteur newConn = null;

         String connName = ((Connector) connectorClass.getAnnotation(Connector.class)).value();

         if (connName.equals(name)) {
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
                  if (ConnecteurSP.class.isAssignableFrom(connectorClass)) {
                     newConn = (Connecteur) connectorClass.getConstructor(Composant.class, Method.class, Composant.class, Field.class).newInstance(fromC, (Field) fromInt, toC, (Field) toInt);
                  } else {
                     throw new ArchitectureException(name + " should be a ConnecteurSP");
                  }
               } else {
                  if (ConnecteurSS.class.isAssignableFrom(connectorClass)) {
                     newConn = (Connecteur) connectorClass.getConstructor(Composant.class, Method.class, Composant.class, Method.class).newInstance(fromC, fromInt, toC, toInt);
                  } else {
                     throw new ArchitectureException(name + " should be a ConnecteurSS");
                  }
               }
            }
            conf.addConnecteur(newConn);
            return;
         }
      }
      throw new ArchitectureException("No connecteur class found for " + name);
   }

   private void addBindings(Configuration conf, ConfDesc element) throws ArchitectureException, NoSuchInterfaceException, NoSuchComponentException, WrongTypeException {
      for (BindingDesc bd : element.bindings) {
         Boolean provided = Boolean.TRUE;
         AccessibleObject from = conf.getInterfaceForName(bd.name(), provided);
         if (from == null) {
            provided = Boolean.FALSE;
            from = conf.getInterfaceForName(bd.name(), provided);
         }
         Composant comp = getComposant(conf, bd.cRef());
         AccessibleObject interf = getInterface(conf, bd.cRef(), bd.iRef(), provided);

         if (from instanceof Field) {
            if (interf instanceof Field) {
               conf.addBinding((Field) from, comp, (Field) interf);
            } else {
               throw new ArchitectureException("Can't bind port " + bd.name() + "and service " + bd.ref() + ".");
            }
         } else if (interf instanceof Method) {
            conf.addBinding((Method) from, comp, (Method) interf);
         } else {
            throw new ArchitectureException("Can't bind service " + bd.name() + "and port " + bd.ref() + ".");
         }
      }

      EntryPointDesc epd = element.entrypoint;
      if (epd != null) {
         Method entryPoint = conf.getEntryPoint();
         if (entryPoint == null || !entryPoint.getAnnotation(RunInterface.class).value().equals(epd.name())) {
            throw new ArchitectureException("There should be an entry point '"+epd.name()+"' defined in the configuration " + element.name());
         } else {
            Method interf = getEntryPoint(conf, epd.cRef(), epd.iRef());
            conf.addBinding(entryPoint, getComposant(conf, epd.cRef()), interf);
         }
      }
   }

   /**
    * Vérifie qu'une interface est bien présente dans les interfaces d'un composant.
    * @param name nom de l'interface dans le xml et l'annotation
    * @param intNames noms des classes annotées
    * @param comp composant censé contenir l'interface
    * @return
    * @throws ClassNotFoundException
    * @throws InstantiationException
    * @throws IllegalAccessException
    */
   private boolean isOneOf(String name, Set<String> intNames, Composant comp) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
      for (String intName : intNames) {
         Class intC = loader.loadClass(intName);
         if (AccessibleObject.class.isAssignableFrom(intC) && Member.class.isAssignableFrom(intC)) {
            AccessibleObject member = (AccessibleObject) intC.newInstance();

            // Faut check qu'il est bien de la bonne classe
            if (((Member) member).getDeclaringClass().equals(comp.getClass())) {
               // Faut check que l'annotation a bien pName comme value.
               RequiredInterface annotation = member.getAnnotation(RequiredInterface.class);
               if (annotation == null) {
                  ProvidedInterface annotation1 = member.getAnnotation(ProvidedInterface.class);
                  if (annotation1.value().equals(name)) {
                     return true;
                  }
               } else {
                  if (annotation.value().equals(name)) {
                     return true;
                  }
               }

            }
         }

      }
      return false;
   }

   private AccessibleObject getInterface(Configuration conf, String cName, String iName, Boolean from) throws ArchitectureException {

      Composant cmp = getComposant(conf, cName);
      AccessibleObject ao = cmp.getInterfaceForName(iName, from);
      if (ao == null) {
         throw new ArchitectureException(
                 "No field or method for interface " + cName + "." + iName);
      }
      if (from) {
         if (ao instanceof Field) {
            if (Validator.checkProvided((Field) ao)) {
               return ao;
            } else {
               throw new ArchitectureException("The Field " + ((Field) ao).getName() + " should be public.");
            }
         } else {
            if (Validator.checkProvided((Method) ao)) {
               return ao;
            } else {
               throw new ArchitectureException("The Method " + ((Method) ao).getName() + " should be public.");
            }
         }
      } else {
         if (ao instanceof Field) {
            if (Validator.checkRequired((Field) ao)) {
               return ao;
            } else {
               throw new ArchitectureException("The Field " + ((Field) ao).getName() + " should be public.");
            }
         } else {
            if (Validator.checkRequired((Method) ao)) {
               return ao;
            } else {
               throw new ArchitectureException("The Method " + ((Method) ao).getName() + " should be public.");
            }
         }
      }
   }

   private Composant getComposant(Configuration conf, String cName) throws ArchitectureException {

      for (Composant cmp : conf.getComposants()) {
         if (cmp.getClass().getAnnotation(Component.class).value().equals(cName)) {
            return cmp;
         }
      }
      throw new ArchitectureException("No component found for name " + cName);
   }

   private Method getEntryPoint(Configuration conf, String cRef, String iRef) throws ArchitectureException {
      Composant cmp = getComposant(conf, cRef);
      Method ep = cmp.getEntryPoint();
      if(ep == null || !Validator.checkEntryPoint(ep)) {
         throw new ArchitectureException("The composant " + cRef + " should have entry point " + iRef + ".");
      } else {
         return ep;
      }
   }
}
