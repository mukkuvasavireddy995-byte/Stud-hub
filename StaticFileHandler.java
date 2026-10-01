package com.studenthub.handler;

import com.sun.net.httpserver.*;
import java.io.*;
import java.nio.file.*;

public class StaticFileHandler implements HttpHandler {
    private final Path root=Path.of("src/main/resources/frontend").toAbsolutePath().normalize();

    public void handle(HttpExchange e)throws IOException{
        String p=e.getRequestURI().getPath();
        if(p.equals("/"))p="/index.html";
        Path file=root.resolve(p.substring(1)).normalize();
        if(!file.startsWith(root)||!Files.exists(file)||Files.isDirectory(file)){
            byte[] b="Not Found".getBytes();
            e.sendResponseHeaders(404,b.length); e.getResponseBody().write(b); e.close(); return;
        }
        byte[] b=Files.readAllBytes(file);
        e.getResponseHeaders().set("Content-Type",type(file));
        e.sendResponseHeaders(200,b.length);
        try(OutputStream out=e.getResponseBody()){out.write(b);}
    }

    private String type(Path p){
        String n=p.toString().toLowerCase();
        if(n.endsWith(".html"))return"text/html; charset=UTF-8";
        if(n.endsWith(".css"))return"text/css; charset=UTF-8";
        if(n.endsWith(".js"))return"application/javascript; charset=UTF-8";
        return"application/octet-stream";
    }
}
