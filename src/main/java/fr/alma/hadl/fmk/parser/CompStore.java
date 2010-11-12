/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.alma.hadl.fmk.parser;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author judu
 */
public class CompStore<T extends DescElem> implements Iterable<T>{

   private Set<T> store = new HashSet<T>();

   public CompStore() {
   }

   public List<T> getByCompName(String comp) {
      if(comp == null) return null;
      List<T> li = new LinkedList<T>();
      for(T desc:store) {
         if(desc.name() != null && desc.name().equals(comp)) {
            li.add(desc);
         }
      }
      return li;
   }

   public boolean add(T e) {
      return store.add(e);
   }

   public boolean contains(T e) {
      return store.contains(e);
   }

   @Override
   public Iterator<T> iterator() {
      return store.iterator();
   }
}
