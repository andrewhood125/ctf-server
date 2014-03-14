ctf-server
=========

The server to support capture the flag.

Author
------
Andrew Hood <andrewhood125@gmail.com>


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