package fr.alma.hadl.fmk;

import java.io.IOException;

/**
 * Hello world!
 *
 */
public class App {

   public static void main(String[] args) throws IOException, ClassNotFoundException {
      if (args.length == 1) {
         SystemManager manager = SystemManager.getInstance();
         manager.init(args[0]);
         manager.run();
      }
   }
}
