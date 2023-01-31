package com.example.owasptop10;

import java.io.*;

public class InsecureDeserialization {
    private static final String fileName = "serial.ser";

    public static void main(String[] args) throws IOException, ClassNotFoundException {

//        System.out.println("Serialize an employee object");
//        Employee employee = new Employee("Jacob", "How are you?");
//        serializeObject(employee, filename);

        System.out.println("Serialize vulnerable object");
        VulnerableObj vulnerableObj = new VulnerableObj("calc.exe");
        serializeObject(vulnerableObj);

        System.out.println("Deserialize an object");
        deserializeObject();
    }

    private static void deserializeObject() throws IOException, ClassNotFoundException {
        Employee employee;
        try(FileInputStream fis = new FileInputStream(fileName);
            ObjectInputStream ois = new ObjectInputStream(fis)) {
            employee = (Employee) ois.readObject();
            System.out.println(employee);
        }
    }

    private static void serializeObject(Object obj) throws IOException {
        try(FileOutputStream fos = new FileOutputStream(fileName);
            ObjectOutputStream oos = new ObjectOutputStream(fos))  {
            oos.writeObject(obj);
            System.out.println(obj.toString());
        }
    }
}


class Employee implements Serializable {
    private String name;
    private String message;

    public Employee(String name, String message) {
        this.name = name;
        this.message = message;
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.name = this.name + "!";
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Employee{");
        sb.append("name='").append(name).append('\'');
        sb.append(", message='").append(message).append('\'');
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
        while((line = input.readLine()) != null) {
            System.out.println(line);
        }
    }
}