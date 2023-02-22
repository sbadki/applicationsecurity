package com.example.owasptop10;

import java.io.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class InsecureDeserialization {

    static Set<String> whitelist = new HashSet<>(Arrays.asList("Employee"));

    public static void main(String[] args) throws IOException, ClassNotFoundException {

        Employee employee = new Employee("Jacob", "Jacob@example.com", "11 Street, USA");
        serializeObject(employee,"employee.ser");
        System.out.println("Deserializing an Employee object." );
        deserializeObject("employee.ser");

        System.out.println("---------------------------------------------------------------------");

        VulnerableObj vulnerableObj = new VulnerableObj("calc.exe");
        serializeObject(vulnerableObj, "vulnerable.ser");
        System.out.println("Deserializing a Vulnerable object." );
        deserializeObject("vulnerable.ser");
    }

    public static Employee deserializeObject(String fileName) throws IOException, ClassNotFoundException {
        Employee employee;
        try(FileInputStream fis = new FileInputStream(fileName);
            ObjectInputStream ois = new ObjectInputStream(fis)) {
            //Prevention 2: Whitelist approach
            //ObjectInputStream ois = new LookAheadObjectInputStream(fis,whitelist)) {
            employee = (Employee) ois.readObject();
            System.out.println(employee);
        }
        return employee;
    }

    public static void serializeObject(Object obj, String fileName) throws IOException {
        System.out.println("Serializing a " +obj.getClass().getName());
        try(FileOutputStream fos = new FileOutputStream(fileName);
            ObjectOutputStream oos = new ObjectOutputStream(fos))  {
            oos.writeObject(obj);
            System.out.println(obj.toString());
        }
    }
}

class Employee implements Serializable {
    private String name;
    private String email;
    private String address;

    public Employee(String name, String email, String address) {
        this.name = name;
        this.email = email;
        this.address = address;
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
    }

    //Prevention - 1
    //We can override deserialize method by throwing an exception.
//    private final void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
//      throw new IOException(("Can't be deserialized");
//    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Employee{");
        sb.append("name='").append(name).append('\'');
        sb.append(", email='").append(email).append('\'');
        sb.append(", address='").append(address).append('\'');
        sb.append('}');
        return sb.toString();
    }
}

class VulnerableObj implements Serializable {
    public String command;

    public VulnerableObj(String command) {
        this.command = command;
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        String line;
        Process process = Runtime.getRuntime().exec(this.command);
        BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
        while ((line = input.readLine()) != null) {
            System.out.println(line);
        }
    }
}