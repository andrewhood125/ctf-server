ctf-server
=========

The server to support capture the flag.

Author
------
Andrew Hood <andrewhood125@gmail.com>

Usage
-----
- Compile the server. `javac CTFServer.java`
- Start the server. `java CTFServer 4444`
- Connect to the server from telnet or nc `nc localhost 4444`

Protocol
--------
The CTF Protocol is in JSON. An example exchange looks like this. 
```bash
nc localhost 4444
```
```javascript
{"ACTION":"HELLO","USERNAME":"andrewhood125","BLUETOOTH":"10:40:f3:97:28:9e"}
{"ACTION":"CREATE","LOCATION":"33,-90","SIZE":10}
{"ACTION":"LOG","LEVEL":"INFO","PAYLOAD":"You\u0027re now in lobby
2C52A"}
{"ACTION":"START"}
{"ACTION":"LOG","LEVEL":"ERROR","PAYLOAD":"Not enough players to start
game."}
```

Code Standards
--------------

- No tabs
- Indent 4 spaces
- Curly braces on new line except for stacked else if else and catch blocks.
```java 
if(someCondition)
{
    // This looks funny in markdown
} else {
    // Other statement
}```
- Constants at top of class in all caps.
- Class methods Constructors first then alphabetical.
- Imports should be alphabetized.
- Instance variables should be initialized only in the constructor;
- Constants and static variables should be initialized at declaration.
- All instance variables and non static methods  should always be referenced with this to  prevent confusion.
- If a class has getters or setters use them.
- Variables within a category {static, instance, constants} should be
  alphabetized.

License
-------
Copyright (c) Andrew Hood. All rights reserved.
