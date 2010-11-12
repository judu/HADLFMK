/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.alma.hadl.fmk;

import fr.alma.hadl.annotations.Component;
import fr.univnantes.alma.hadlm2.composant.Composant;
import java.io.File;
import java.io.IOException;
import java.net.URL;
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
   private List<Composant> composants;

   private SystemManager() {
      composants = new LinkedList<Composant>();
   }

   public void loadAnnotatedComposants(String packagePath) {
      AnnotationDB annotdb = new AnnotationDB();
      annotdb.setScanClassAnnotations(Boolean.TRUE);
      annotdb.setScanFieldAnnotations(Boolean.FALSE);
      annotdb.setScanMethodAnnotations(Boolean.FALSE);
      annotdb.setScanParameterAnnotations(Boolean.FALSE);
      URL[] urls = ClasspathUrlFinder.findClassPaths(packagePath);

      File jar = new File(packagePath);
      if(!jar.exists()) {
         return;
      }
      try {
         annotdb.scanArchives(jar.toURI().toURL());
      } catch (IOException ex) {
         Logger.getLogger(SystemManager.class.getName()).log(Level.SEVERE, null, ex);
      }


      Map<String, Set<String>> annotIndex = annotdb.getAnnotationIndex();
      Set<String> comps = annotIndex.get(Component.class.getName());
      if (comps != null) {
         for (String className : comps) {
            System.out.println(className);
            try {
               Class compC = ClassLoader.getSystemClassLoader().loadClass(className);
               if (Composant.class.isAssignableFrom(compC)) {
                  Composant comp = (Composant) compC.newInstance();
                  System.out.println("test : " + comp.getClass().getAnnotation(Component.class).value());
                  composants.add(comp);
               }
            } catch (InstantiationException ex) {
               Logger.getLogger(SystemManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
               Logger.getLogger(SystemManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
               Logger.getLogger(SystemManager.class.getName()).log(Level.SEVERE, null, ex);
            }
         }
      }
   }

   public List<Composant> getComposants() {
      return composants;
   }
}
