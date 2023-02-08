package com.example.owasptop10;

import org.apache.commons.collections4.Factory;
import org.apache.commons.collections4.map.LazyMap;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GadgetChain {
   public static void main(String[] args) throws Exception {
       
      Factory<Date> factory = () ->  new Date();

      Map<String, Date> map = new HashMap<>();

      Map<String, Date> lazy = LazyMap.lazyMap(map, factory);

      System.out.println(lazy.get("Before"));
      Thread.sleep(1000);
      System.out.println(lazy.get("After"));

      for (String key : lazy.keySet()) {
         System.out.println(key + " = " + lazy.get(key));
      }

      FileInputStream fis = new FileInputStream("payload.ser");
      ObjectInputStream ois = new ObjectInputStream(fis);
      Employee emp = (Employee) ois.readObject();
      System.out.println(emp);
   }
}