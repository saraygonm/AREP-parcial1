package org.example;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.URISyntaxException;


public class ChatController {
    public void main(String[] args) throws IOException, URISyntaxException,ClassNotFoundException,
            NoSuchMethodException,SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
        //localhost:3600/cliente
        ServerSocket serverSocket =  new ServerSocket(37000);
        try {
            serverSocket = new ServerSocket(37000);
        }
        catch (IOException)


        }


    }

}
