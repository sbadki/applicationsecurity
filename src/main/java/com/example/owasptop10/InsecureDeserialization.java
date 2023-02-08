package com.example.owasptop10;

import java.io.*;

public class InsecureDeserialization {

    public static void main(String[] args) throws IOException, ClassNotFoundException {

        System.out.println("Serialize and deserialization of an employee object");
        Employee employee = new Employee("Jacob", "Jacob@example.com", "11 Street, USA");
        serializeObject(employee,"employee.ser");
        deserializeObject("employee.ser");

        VulnerableObj vulnerableObj = new VulnerableObj("calc.exe");

        serializeObject(vulnerableObj, "vulnerable.ser");
        deserializeObject("vulnerable.ser");
    }

    private static Employee deserializeObject(String fileName) throws IOException, ClassNotFoundException {
        Employee employee;
        try(FileInputStream fis = new FileInputStream(fileName);
            ObjectInputStream ois = new ObjectInputStream(fis)) {
            employee = (Employee) ois.readObject();
            System.out.println(employee);
        }
        return employee;
    }

    private static void serializeObject(Object obj, String fileName) throws IOException {
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
        this.email = "attacker" + this.email;
    }

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
    private String command;

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