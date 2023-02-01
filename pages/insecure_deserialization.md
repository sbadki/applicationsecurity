---
layout: page
title: Insecure Deserialization
description: Personal blogs on security for self study
---

**Insecure deserialization** is one of the top vulnerability in OWASP top 10 list for 2021. Insecure Deserialization is a vulnerability
which occurs when untrusted data is used to abuse the logic of an application. Successful insecure deserialization attacks could
allow an attacker to carry out denial-of-service (DoS) attacks, authentication bypasses and remote code execution attacks.

## Serialization & Deserialization:

**Serialization** refers to a process of converting an object into a format which can be persisted to disk (for example saved to a file
or a datastore), sent through streams (for example stdout), or sent over a network. The format in which an object is serialized into,
can either be binary or structured text (for example XML, JSON YAML…). JSON and XML are two of the most commonly used serialization
formats within web applications.

**Deserialization** on the other hand, is the opposite of serialization, that is, transforming serialized data coming from a file, stream
or network socket into an object.

There are different libraries or classes for different languages for serialization/deserialization purposes such as in Python - Pickle,
PHP - Serialize/Unserialize, Java - ObjectInputStream/ObjectOutputStream and in Javascript - JSON.stringfy()/JSON.parse()

![serialization.png](serialization.png)


Many programming languages support the serialization and deserialization of objects, including Java, PHP, Python, and Ruby. It’s important
to understand that safe deserialization of objects is normal practice in software development. The trouble however, starts when
deserializing untrusted user input

## In-Secure deserialization in action:

**Insecure deserialization** is a type of vulnerability that arises when an attacker can manipulate the serialized object and pass harmful
data into the application code which cause unintended consequences in the program’s flow.

**Java example:**

We serialized an Employee object and deserialized it as shown below but how come the name is de-serialised as Jacob!? This is because we are
able to override the readObject method of the serialized class. These methods are also called as magic methods. Magic methods are a special
subset of methods that you do not have to explicitly invoke. Instead, they are invoked automatically whenever a particular event or scenario
occurs. **Magic methods** are a common feature of object-oriented programming in various languages.

![employee.png](employee.png)

![result.png](result.png)


Attackers can customize deserialization protocol for example, by overriding the readObject() function of the Java Serializable class as
shown in below snippet to achieve remote code execution. Deserialization doesn't call constructor while re-creating an object from Stream
of Bytes, it uses reflection to re-create an object.

## Vulnerability: ##

If we are able to serialize this object then we can execute any command remotely.  VulnerableObj class can run any command given to it.
Which causes any command to be executed by an application.

![Vulnerable.png](Vulnerable.png)

If we pass calc.exe as command, it runs while deserializing VulnerableObj. We could see that the ClassCastException has occurred but the
command we sent is executed that means the harm is already done. This is just sample example to show that we can execute any command by
passing a vulnerable object. In reality attacker might harm to the system severely by exploiting this kind of vulnerability.

![rce.png](rce.png)

## Prevention:

- Make sure to patch the systems and keep the dependencies updated.
- Monitoring the deserialization process.
- Encrypting serialization processes.
- Not accepting serialized objects from unknown or untrusted sources.
- Use simple data types, like strings and arrays instead of an objects.
- Running the deserialization code with limited access permissions.
- Using a firewall which can help detect insecure deserialization.
- Logging exceptions and failures encountered during deserialization

---