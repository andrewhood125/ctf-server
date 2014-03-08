/**
 * Author: Andrew Hood <andrewhood125@gmail.com>
 * Description: Store everything that a single player will need.
 * Copyright (c) 2014 Andrew Hood. All rights reserved.
 */
import java.net.Socket;

class Player extends Locate implements Runnable
{
  int id;
  String btMAC;
  String username;

  Player(Socket socket)
  {
    System.out.println("New player connected from IP: " + socket.getInetAddress());
  }

  public void run()
  {
    System.out.println(this.toString() + "'s thread was started.");
  }
}
