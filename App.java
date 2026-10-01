package com.studenthub;

import com.sun.net.httpserver.HttpServer;
import com.studenthub.dao.StudentDao;
import com.studenthub.handler.StudentHandler;
import com.studenthub.handler.StaticFileHandler;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Executors;

public class App {
    public static void main(String[] args) throws Exception {
        if (!Files.exists(Path.of("src/main/resources/frontend/index.html"))) {
            throw new IllegalStateException("Frontend files are missing.");
        }
        StudentDao dao = new StudentDao();
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/api/students", new StudentHandler(dao));
        server.createContext("/", new StaticFileHandler());
        server.setExecutor(Executors.newFixedThreadPool(10));
        server.start();
        System.out.println("StudentHub running at http://localhost:8080");
    }
}
