ctf-server
=========

The server to support capture the flag.

Author
------
Andrew Hood <andrewhood125@gmail.com>

Getting Started
---------------
- Clone ctf-server.

```
git clone http://git.nowaddhero.com/andrewhood125/ctf-server.git
```
- Compile and run the server.

```
cd ctf-server && make
```

Protocol
--------
The CTF Protocol is in JSON. 

#### What the server pushes
###### While a player is in a lobby the server can push...
When a player joins a lobby all other players will receive:

```
{"ACTION":"JOINED","LOCATION":"33.0,-90.0","PLAYER":"DORITO","TEAM":"Blue","BLUETOOTH":"12:12:34:56:78:90"}
```
###### While a player is in a game the server can push...
When the game is started

```
{"ACTION":"START"}
{"ACTION":"KILL","PLAYER":"andrewhood125"}
```

When a player updates their location

```
{"ACTION":"GPS","LOCATION":"-33.0,-90.0","PLAYER":"DORITO"}
```
#### Actions  
- Action
  - Attribute belonging to action
    - Acceptable values
    - Description
  - Another attribute
    - Acceptable values
    - Description
  - What this command does.
  - When this action can be used.
  - What this action will reply with.

```
 Example of this command being used
```
```
An example of a reply from the server
```

- HELLO
  - USERNAME
    - Alphanumeric
    - Used to identify a user in a lobby
  - BLUETOOTH
    - A valid bluetooth mac
    - Sets your users bluetooth mac on the server which is distributed to other players for the game logic.
  - Sets up your user. 
  - Upon establishing a connection to the server. Used only once. Greeting someone more than once is... awkward.
  - If you have not yet greeted you will not recieve a response. If you have already greeted than you will recieve info level log.   
  
```
{
  "ACTION":"HELLO",
  "USERNAME":"andrewhood125",
  "BLUETOOTH":"10:40:f3:97:2a:33"
}
```

- CREATE
  - LOCATION
    - DOUBLE,DOUBLE That is.. lattitude comma longitude.
    - Used to determine the arena boundaries for the lobby you're creating.
  - SIZE
    - Double
    - The distance to the North and South boundaries from your current location. The East and West boundaries will be SIZE*2 away to form the rectangle.
  - Creates a lobby.
  - After you have greeted while you are not in a lobby.
  - Replies with info level log.

```
{
  "ACTION":"CREATE",
  "LOCATION":"33.23432423,-89.34232232",
  "SIZE":"0.2"
}
```
```
{"ACTION":"LOG","LEVEL":"INFO","PAYLOAD":"You\u0027re now in lobby
2C52A"}
```
- START
  -  Starts the game. 
  - When you are in a lobby that you have created with more than 1 player. 
  - WHAT DOES START REPLY
  
```
{
  "ACTION":"START"
}
```
- GPS
  - LOCATION
    - DOUBLE,DOUBLE That is.. lattitude comma longitude.
    - Your updated location.
  - Updates your location.
  - When you are in a lobby.
  - No reply.

```
{
  "ACTION":"GPS",
  "LOCATION":"33.23432443,-89.34232232"
}

``` 
- JOIN
  - LOCATION
    - DOUBLE,DOUBLE That is.. lattitude comma longitude.
    - Your location at the time of joining the lobby.
  - ID
    - 5 Chars [0-9A-F]
    - The ID of the lobby you are trying to join.
  - Joins a lobby.
  - When you have greeted while you are not in a lobby.
  - WHAT DOES JOIN REPLY

```
{
  "ACTION":"JOIN",
  "LOCATION":"33.23432423,-89.34232232",
  "SIZE":"0.2"
}
```
- LEAVE
  - No attributes
  - Leave the lobby you are currently in.
  - When you are in a lobby.
  - No reply.

```
{
  "ACTION":"LEAVE"
}
```
  
- LOBBY
  - No attributes
  - Describe the lobby you are in.
  - When you are in a lobby.
  - Dependant on situation.

```
{
  "ACTION":"LOBBY"
}
```
```
{
  "ACTION":"LOBBIES",
  "LOBBIES": [
    {
      "LOBBY":"AB321",
      "BLUE":4,
      "RED",3",
      "STATE",1
    },
    {
      "LOBBY":"FFFFF",
      "BLUE":15,
      "RED",16",
      "STATE",0
    }
  ]
}
```
- DROP
  - BLUETOOTH
    - A bluetooth mac of a user on the opposite team
    - The mac of a user you heard.
  - Alert the server you are withing range of a user
  - When the game is in progress
  - No reply.

```
{
  "ACTION":"DROP",
  "BLUETOOTH":"10:40:f3:97:2a:33"
}
```
- QUIT
  - Quit is a special case it is just "QUIT"! At least for now. 
  

Code Standards
--------------

- No tabs
- Indent 4 spaces
- Curly braces on new line except for stacked else if else and catch blocks.

```
if(someCondition)  
{
    // This looks funny in markdown
} else {
    // Other statement
}

try
{
    // Something risky
} catch(RiskyException ex) {
    // Save the day
}
```
- Constants at top of class in all caps.
- Class methods Constructors first then alphabetical.
- Imports should be alphabetized.
- Instance variables should be initialized only in the constructor;
- Constants and static variables should be initialized at declaration.
- All instance variables and non static methods  should always be referenced with `this` to  prevent confusion.
- If a class has getters or setters use them.
- Variables within a category {static, instance, constants} should be
  alphabetized.
  
CopyPasta For Easy Server Debugging
-----------------------------------
## User 1
`{"ACTION":"HELLO","USERNAME":"andrewhood125","BLUETOOTH":"10:40:f3:97:28:9e"}`
`{"ACTION":"CREATE","LOCATION":"35.12109549,-89.93835153","SIZE":"0.0007", "ACCURACY":0.00026}`

## User 2
`{"ACTION":"HELLO","USERNAME":"spec-ops81","BLUETOOTH":"01:23:45:67:89:ab"}`
`{"ACTION":"LOBBY"}`  
`{"ACTION":"JOIN", "ID":"` `","LOCATION":"35.12113808,-89.93849768"}`

## User 1
`{"ACTION":"START"}`
`{"ACTION":"BASE"}`
`{"ACTION":"GPS",`
`{"ACTION":"FLAG"}`
`{"ACTION":"GPS",`
`{"ACTION":"GPS","LOCATION":"35.12113808,-89.93849768"}`
  
## User 3
`{"ACTION":"HELLO","USERNAME":"dorito","BLUETOOTH":"13:40:f3:97:28:9e"}`
`{"ACTION":"LOBBY"}` 

License
-------
Copyright (c) Andrew Hood 2014. All rights reserved.