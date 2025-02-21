package org.example;

import java.net.*;
import java.io.*;

public class Facade {
    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String GET_URL = "http://localhost:37000/compreflex?comando=";
    //localhost3600/cliente
    public static void main(String[] args) throws IOException, URISyntaxException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(36000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }

        Socket clientSocket = null;
        boolean running = true;
        while(running){
            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
                handleConnection(clientSocket);
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }
        }
        serverSocket.close();
    }

    public static void handleConnection(Socket client) throws IOException, URISyntaxException{
        PrintWriter out = new PrintWriter(
                client.getOutputStream(), true);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(client.getInputStream()));
        String inputLine, outputLine, query;
        String fLine = "";
        while ((inputLine = in.readLine()) != null) {
            if(fLine.isEmpty()){
                fLine = inputLine.split(" ")[1];
            }
            if (!in.ready()) {break; }
        }
        System.out.println(fLine);

        if (fLine.equals("/cliente")){
            outputLine = getIndexPage();
        } else if(fLine.startsWith("/consulta")){
            URI uri = new URI(fLine);
            query = uri.getQuery();
            query = query.split("=")[1];
            outputLine = "HTTP/1.1 200 OK\r\n"
                    + "Content-Type: application/json\r\n"
                    + "\r\n";
            outputLine += callToChatAndRespond(query);
        }else{
            outputLine = getErrorPage();
        }

        out.println(outputLine);
        out.close();
        in.close();
        client.close();
    }

    public static String getIndexPage(){
        return "HTTP/1.1 200 OK\r\n"
                + "Content-Type: text/html\r\n"
                + "\r\n"+ "<!DOCTYPE html>\r\n" + //
                "<html>\r\n" + //
                "    <head>\r\n" + //
                "        <title>Form Example</title>\r\n" + //
                "        <meta charset=\"UTF-8\">\r\n" + //
                "        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\r\n" + //
                "    </head>\r\n" + //
                "    <body>\r\n" + //
                "        <h1>ChatGPT</h1>\r\n" + //
                "        <form>\r\n" + //
                "            <input type=\"text\" id=\"instruction\" name=\"name\"><br><br>\r\n" + //
                "            <input type=\"button\" value=\"Submit\" onclick=\"query(instruction)\">\r\n" + //
                "        </form>\r\n" + //
                "        \r\n" + //
                "        <div id=\"resp\"></div>\r\n" + //
                "        \r\n" + //
                "        <script>\r\n" + //
                "            function query(instruction){\r\n" + //
                "                let url = \"/consulta?comando=\" + instruction.value;\r\n" + //
                "\r\n" + //
                "                fetch (url, {method: 'GET'})\r\n" + //
                "                    .then(x => x.json())\r\n" + //
                "                    .then(y => {\r\n" + //
                "                        let p = document.createElement('p');\r\n" + //
                "                        for(let i in y){\r\n" + //
                "                            p.textContent += \"{\" + i + \": \" + y[i] + \"}\";\r\n" + //
                "                        }\r\n" + //
                "                        document.getElementById(\"resp\").appendChild(p);\r\n" + //
                "                    });\r\n" + //
                "            }\r\n" + //
                "        </script>\r\n" + //
                "    </body>\r\n" + //
                "</html>";
    }

    public static String getErrorPage(){
        return "HTTP/1.1 403 Bad request\r\n"
                + "Content-Type: text/html\r\n"
                + "\r\n"+"<!DOCTYPE html>\r\n" + //
                "<html>\r\n" + //
                "    <head>\r\n" + //
                "        <title>Form Example</title>\r\n" + //
                "        <meta charset=\"UTF-8\">\r\n" + //
                "        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\r\n" + //
                "    </head>\r\n" + //
                "    <h1>Error, your request failed :c</h1>\r\n" + //
                "</html>";
    }

    public static String callToChatAndRespond(String query) throws IOException{
        URL obj = new URL(GET_URL + query);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);

        //The following invocation perform the connection implicitly before getting the code
        int responseCode = con.getResponseCode();
        System.out.println("GET Response Code :: " + responseCode);
        StringBuffer response = new StringBuffer();

        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            System.out.println(response.toString());
        } else {
            System.out.println("GET request not worked");
            response.append(getErrorPage());
        }
        System.out.println("GET DONE");
        return response.toString();
    }
}