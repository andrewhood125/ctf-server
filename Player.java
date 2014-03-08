/**
 * Author: Andrew Hood <andrewhood125@gmail.com>
 * Description: Store everything that a single player will need.
 * Copyright (c) 2014 Andrew Hood. All rights reserved.
 */
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;


class Player extends Locate implements Runnable
{
  int id;
  PrintWriter out;
  BufferedReader in;
  String btMAC;
  String username;

  Player(Socket socket)
  {
    try
    {
      out = new PrintWriter(socket.getOutputStream(), true);
      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      System.out.println("New player connected from IP: " + socket.getInetAddress());
      out.println("New player connected from IP: " + socket.getInetAddress());
    } catch(IOException ex) {
      System.err.println(ex.getMessage());
      System.exit(4);
    }
  }
  public void run()
  {
    System.out.println(this.toString() + "'s thread was started.");
    out.println(this.toString() + "'s thread was started.");
    // Listen for HELO
    try
    {
      System.out.println(in.readLine());
    } catch(IOException ex) {
      System.err.println(ex.getMessage());
      System.exit(5);
    }
    
  }
}
