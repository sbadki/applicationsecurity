---
layout: post
title:  "Insecure Deserialization"
date:   2023-01-30 12:28:45 +0800
categories: Application security blogs
---

**Insecure deserialization** is one of the top vulnerability in OWASP top 10 list for 2021. Insecure Deserialization is a vulnerability which occurs when untrusted data is used to abuse the logic of an application. Successful insecure deserialization attacks could allow an attacker to carry out denial-of-service (DoS) attacks, authentication bypasses and remote code execution attacks.

## What is Serialization & Deserialization? ##

**Serialization** refers to a process of converting an object into a format which can be persisted to disk (for example saved to a file or a datastore), sent through streams (for example stdout), or sent over a network. The format in which an object is serialized into, can either be binary or structured text (for example XML, JSON YAML…). JSON and XML are two of the most used serialization formats within web applications.

**Deserialization** on the other hand, is the opposite of serialization, that is, transforming serialized data coming from a file, stream or network socket into an object.
There are different libraries or classes for different languages for serialization/deserialization purposes such as in 
Python - Pickle,
PHP - Serialize/Unserialize,
Java - ObjectInputStream/ObjectOutputStream and in
JavaScript - JSON.stringfy()/JSON.parse()

![Portswigger]({{ "/assets/images/insecure-deserialization/serialization.png" | relative_url }})
_Photo by: [Portswigger.net]

Many programming languages support the serialization and deserialization of objects, including Java, PHP, Python, and Ruby. It’s important to understand that safe deserialization of objects is normal practice in software development. The trouble however, starts when deserializing untrusted user input


## What is In-Secure deserialization? ##

**Insecure deserialization** is a type of vulnerability that arises when an attacker can manipulate the serialized object and pass harmful data into the application code which cause unintended consequences in the program’s flow.

Example: We serialized an Employee object by implementing Serializable interface and deserialized it.


![JavaExample]({{ "/assets/images/insecure-deserialization/employee.png" | relative_url }})


Hex dump of the serialized employee object looks like below. It's easy to read and also can update it. 


![HexDump]({{ "/assets/images/insecure-deserialization/hexdump.png" | relative_url }})


Here we serialized Vulnerable Object and deserialized it, so whatever command we have passed to Vulnerable Object got executed.


![Vulnerable]({{ "/assets/images/insecure-deserialization/vulnerableObj.png" | relative_url }})


The point to make here is that, while deserialization the application is not verifying whether the input for deserialization is the one which we expect to deserialize? It's just deserializing whatever is being passed. With Vulnerable object we passed the command as calc.exe and it's just executed. What if the hacker pass some arbitrary code which may give a remote access to the hacker. Certainty its possible. That's why it's called as insecure deserialization.


## What is happening behind the scene? ##


This is because Deserialization doesn’t call constructor while re-creating an object from Stream of Bytes, it is using reflection to re-create an object. All the magic is happing because we can override the readObject method. This is the method which is provided by the Serialization mechanism. These methods are also called as magic methods. Magic methods are a special subset of methods that you do not have to explicitly invoke. Instead, they are invoked automatically whenever a particular event or scenario occurs. Magic methods are a common feature of object-oriented programming in various languages like Ruby, PHP, python, Java etc.
So, this shows that the De-Serialization is not secured. Attackers can customize deserialization protocol by overriding the readObject() function of the Java.

## Insecure Deserialization in action. ##

We take an example from Webgoat. It’s the deliberately insecure javas-based application. Which has OWASP top 10 challenges.
e.g. "Try to change this serialized object in order to delay the page response for exactly 5 seconds."


![webgoateg]({{ "/assets/images/insecure-deserialization/webgoateg.png" | relative_url }})


1. We need to know whether there is any object which is getting serialized and deserialized. From the page it looks like it does as the string shown is starting with r00.(Serialized objects can be identified with string starting with AC ED or r00 in hex and base64 editor respectively)
2. Intercept the request in Burp Suite and see what’s getting pass in input field. Token=deserialize.


![inspect]({{ "/assets/images/insecure-deserialization/inspect.png" | relative_url }})


3. Search for any magic methods like read Object() of ObjectInputStream. It's used by InsecureDeserializationTask. Its taking a token value from an input field directly without sanitizing it and deserializing it.
4. VulnerableTaskHolder Object is serializable object which is overriding readObject method which is executing command via.
      Process p = Runtime.getRuntime().exec(taskAction);
      taskAction is any string passed to its constructor. That means we can make use of this class to pass any command as string and serialized that object to perform remote code execution.
5. Create a test class which is serializing a VulnerableTaskHolder object by passing the taskName and taskAction as “TASK” and “Sleep 5” respectively which will delay the response of the page by 5 seconds.


![attack]({{ "/assets/images/insecure-deserialization/attack.png" | relative_url }})


6. And this is how we are able to delay the page response by 5 seconds.


![webgoatsuccess]({{ "/assets/images/insecure-deserialization/webgoatsuccess.png" | relative_url }})


## What is Gadgets and Chains?  ##


A gadget—as used by Lawrence & Frohoff in their talk Marschalling Pickle at AppSecCali 2015—is a class or function that exists in the application which helps attacker to achieve a particular goal but a gadget may not by itself do anything harmful with user input.
The exploitation strategy is to start with a “kick-off” gadget that’s executed after deserialization and build a chain of instances and method invocations to get to a “sink” gadget that’s able to execute arbitrary code or commands. Once attackers manage to get input to a sink gadget, they can do the maximum damage by performing an arbitrary code execution.


![gadgetchain]({{ "/assets/images/insecure-deserialization/gadgetchain.png" | relative_url }})

_Photo_by: https://brandur.org/fragments/gadgets-and-chains


Attacker needs access to the source code to identify such Gadgets, so it’s a tedious task to do it manually. Fortunately, There are tools like "ysoserial" and "gadget inspector" which helps us to identify such Gadgets.


## ysoserial


**ysoserial** is a tool for generating payloads that exploit unsafe Java object deserialization.


We could see all these java libraries has gadget chains and if any of this library is happened to found in our applications classpath our application is vulnerable to Insecure deserialization vulnerability. So, today will take an example of commons-collections library and try to run arbitrary command.


![Ysoserial]({{ "/assets/images/insecure-deserialization/ysoserial.png" | relative_url }})


"Usage: java -jar ysoserial.jar [payload] '[command]'"

The command will create a payload.ser file which we deserialize it.

**java.exe -jar ysoserial-all.jar CommonsCollections4 'calc.exe' > payload.ser**

With the help of ysoserial tool we created a payload and when we deserialized that payload the command got executed. Likewise, any remote code execution is possible if such classes are happened to found in our applications class path. Which is very dangerous. We might think that why anybody would keep such classes in our class path but many libraries like apache-commons, spring framework uses such classes. So it's always advisable that we keep our application dependencies updated with the latest libraries.


![GadgetChainTest]({{ "/assets/images/insecure-deserialization/gadgettest.png" | relative_url }})


## What are the mitigations we can apply? ##


•	If possible, try to avoid serialization, instead use data formats like JSON or XML if there is no language constraints.

•	If Serialization can't avoid, and we are forced to implement Serialization due to their hierarchy. We can override readObject method by throwing an exception.

![Overiride]({{ "/assets/images/insecure-deserialization/override.png" | relative_url }})

•	Do not accept serialized object from untrusted sources. Implement a check to prevent from tampering of an object by implementing check against the whitelist.


![Lookahead]({{ "/assets/images/insecure-deserialization/lookahead.png" | relative_url }})

![Safeuse]({{ "/assets/images/insecure-deserialization/safeuse.png" | relative_url }})

•	Sanitize user inputs which helps in reduce attack surface of an application. Attackers can use objects like cookies to insert malicious information to change user roles. In some cases, they can elevate their privileges to administrator rights by using a pre-existing or cached password hash from a previous session to launch DDOS, remote execution attacks.

To conclude, this vulnerability is very dangerous, and it’s very difficult to overcome it completely. So, apply the best practices wherever possible. OWASP team has provided cheat sheets for the same for various languages. Follow it for more details.



## References: ##

_https://github.com/frohoff/ysoserial
_https://cheatsheetseries.owasp.org/cheatsheets/Deserialization_Cheat_Sheet.html
_https://cheatsheetseries.owasp.org/cheatsheets/Deserialization_Cheat_Sheet.html
_https://foxglovesecurity.com/2015/11/06/what-do-weblogic-websphere-jboss-jenkins-opennms-and-your-application-have-in-common-this-vulnerability
_https://snyk.io/blog/serialization-and-deserialization-in-java

---