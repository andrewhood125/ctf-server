/**
 * Connect to the server for testing. 
 * 
 * @author Andrew Hood
 * @version 0.1
 */

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client
{   
    public static void main(String[] args)
    {
        try 
        {
            Scanner input = new Scanner(System.in);
            Socket socket = new Socket("localhost", 4444);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out.println("HELLO");
            out.println("12:34:56:67:89:91");
            out.println("Andrew");
            out.println("33.23423,-89.23423");
            
            String incomingCommunication;
            while(!(incomingCommunication = input.nextLine()).equals("QUIT"))
            {
                out.println(incomingCommunication.toUpperCase());
                System.out.println(in.readLine());
            }
        } catch(Exception ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        }   
    }
}
