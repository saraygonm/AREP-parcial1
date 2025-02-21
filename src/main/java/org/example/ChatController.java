package org.example;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;


public class ChatController {
    public void main(String[] args) throws IOException, URISyntaxException, ClassNotFoundException,
            NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        //localhost:3600/cliente
        ServerSocket serverSocket = new ServerSocket(37000);
        try {
            serverSocket = new ServerSocket(37000);
        } catch (IOException e) {
            System.err.println("No se puede escuchar el puerto 37000");
            System.exit(1);
        }
        Socket clienteSock = null;
        boolean running = true;
        while (running) {
            try {
                System.out.println("Listo para Recibir...");
                clienteSock = serverSocket.accept();
                handConnection(clienteSock);
            } catch (IOException e) {
                System.err.println("Acept failted");
                System.exit(1);
            }
        }
        serverSocket.close();
    }

    public static void handConnection(Socket client) throws IOException, URISyntaxException, ClassNotFoundException,
            NoSuchMethodException, SecurityException, IllegalAccessException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        PrintWriter out = new PrintWriter(client.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        String inputLine, outputLine, query;
        String fLine = "";
        while ((inputLine = in.readLine()) != null) {
            if (fLine.isEmpty()) {
                fLine = inputLine.split("")[1];
            }
            if (!in.ready()) {
                break;
            }
        }

        if (fLine.startsWith("/compreflex")) {
            URI uri = new URI(fLine);
            query = uri.getQuery();
            query = query.split("=")[1];
            outputLine = "HTTP/1.1 200 OK\r\n"
                    + "Content-Type: application/json\r\n"
                    + "\r\n";
            outputLine += reflexClassProcedure(query);

        } else {
            outputLine = getErrorPage();
        }

        out.println(outputLine);
        out.close();
        in.close();
        client.close();
    }


    public static String getErrorPage() {
        return "HTTP/1.1 400 bad request\r\n"
                + "Content-Type: text/html\r\n"
                + "\r\n" + "<!DOCTYPE html>\n" +
                "<html>r\r\n"
                + "<head>\n"
                + "<title>Form Example</title>\r\n"
                + "<meta charset = \"UTF-8\">\r\n" +
                "<meta name =\"viewport\" content=\"width=device-width, initial-scale=1.0 \">\r\n" +

                "</head>\r\n"
                + "<h1>Error your request failed</h1>\r\n"
                + "</html>\n";
    }

    private static String reflexClassProcedure(String query) throws IOException, URISyntaxException, ClassNotFoundException,
            NoSuchMethodException, SecurityException, IllegalAccessException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        String funtion = query.split("\\(")[0];
        String values = query.split("\\(")[1];
        values = values.split("\\)")[0];
        String[] vals = values.split(",");
        String ans = "";
        Class<?> c = Class.forName(vals[0]);
        System.out.println(values);

        switch (funtion) {
            case "class":
                ans += "{\"fields\":[";
                for (Field f : c.getDeclaredFields()) {
                    ans += "\"" + f.getName() + "\",";
                }
                ans = ans.substring(0, ans.length() - 1);
                ans += "],\"methods\":[";

                for (Method m : c.getDeclaredMethods()) {
                    ans += "\"" + m.getName() + "\",";
                }
                ans = ans.substring(0, ans.length() - 1);
                ans += "]}";
                break;
            case "invoke":
                Method m1;
                try {
                    m1 = c.getDeclaredMethod(vals[1]);
                    ans += "{\"method\":\"" + m1.toString() + "\"}";
                } catch (Exception f) {
                    try {
                        m1 = c.getDeclaredMethod(vals[1], Integer.TYPE);
                        ans = "{\"method\":\"" + m1.toString() + "\"}";
                    } catch (Exception d) {
                        ans = "No such class defined with the parameters";
                    }
                }
                break;
            case "unary invoke":
                Method m2;
                System.out.println("Entre");
                if (vals[2].equals("String")) {
                    m2 = c.getMethod(vals[1], String.class);
                    String args = vals[3];
                    ans += "{\"answer\":\"" + m2.invoke(null, args) + "\"}";
                }
                if (vals[2].equals("int")) {
                    m2 = c.getMethod(vals[1], Integer.TYPE);
                    Integer args = Integer.parseInt(vals[3]);
                    ans += "{\"answer\":\"" + m2.invoke(null, args) + "\"}";
                }
                if (vals[2].equals("double")) {
                    m2 = c.getMethod(vals[1], Double.TYPE);
                    Double args = Double.parseDouble(vals[3]);
                    ans += "{\"answer\":\"" + m2.invoke(null, args) + "\"}";
                }
                break;

            case "binaryInvoke":
                Method m3;
                if (vals[2].equals("String")) {
                    m3 = c.getMethod(vals[1], String.class, String.class);
                    String arg1 = vals[3];
                    String arg2 = vals[5];
                    ans += "{\"answer\":\"" + m3.invoke(null, arg1, arg2) + "\"}";
                }
                if (vals[2].equals("int")) {
                    m2 = c.getMethod(vals[1], Integer.TYPE, Integer.TYPE);
                    Integer arg1 = Integer.parseInt(vals[3]);
                    Integer arg2 = Integer.parseInt(vals[5]);
                    ans += "{\"answer\":\"" + m2.invoke(null, arg1, arg2) + "\"}";
                }
                if (vals[2].equals("double")) {
                    m2 = c.getMethod(vals[1], Double.TYPE, Double.TYPE);
                    Double arg1 = Double.parseDouble(vals[3]);
                    Double arg2 = Double.parseDouble(vals[5]);
                    ans += "{\"answer\":\"" + m2.invoke(null, arg1, arg2) + "\"}";
                }
                break;
            default:
                break;

        }
        return ans;
    }
}















