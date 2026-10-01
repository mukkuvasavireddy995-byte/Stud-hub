package com.studenthub.handler;

import com.sun.net.httpserver.*;
import com.studenthub.dao.StudentDao;
import com.studenthub.model.Student;
import com.studenthub.util.JsonUtil;
import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.*;

public class StudentHandler implements HttpHandler {
    private final StudentDao dao;
    private static final Pattern FIELD=Pattern.compile("\"([^\"]+)\"\\s*:\\s*(\"(?:\\\\.|[^\"])*\"|[^,}]+)");

    public StudentHandler(StudentDao dao){this.dao=dao;}

    public void handle(HttpExchange ex) throws IOException {
        ex.getResponseHeaders().set("Content-Type","application/json; charset=UTF-8");
        ex.getResponseHeaders().set("Access-Control-Allow-Origin","*");
        try {
            String method=ex.getRequestMethod(), suffix=ex.getRequestURI().getPath().substring("/api/students".length());
            if("OPTIONS".equalsIgnoreCase(method)){send(ex,204,"");return;}
            if(suffix.isEmpty()||suffix.equals("/")){
                if("GET".equals(method)) list(ex);
                else if("POST".equals(method)) create(ex);
                else send(ex,405,error("Method not allowed"));
                return;
            }
            int id=Integer.parseInt(suffix.substring(1));
            if("GET".equals(method)) one(ex,id);
            else if("PUT".equals(method)) update(ex,id);
            else if("DELETE".equals(method)) delete(ex,id);
            else send(ex,405,error("Method not allowed"));
        } catch(NumberFormatException e){send(ex,400,error("Invalid student ID"));}
          catch(IllegalArgumentException e){send(ex,400,error(e.getMessage()));}
          catch(SQLException e){
              e.printStackTrace();
              send(ex,e.getErrorCode()==1062?409:500,
                   error(e.getErrorCode()==1062?"Email already exists":"Database error. Check MySQL credentials and server."));
          } catch(Exception e){e.printStackTrace();send(ex,500,error("Unexpected server error"));}
    }

    private void list(HttpExchange ex)throws Exception{
        String q=ex.getRequestURI().getRawQuery(), search="";
        if(q!=null) for(String p:q.split("&")){
            String[] a=p.split("=",2); if(a.length==2&&a[0].equals("search"))
                search=URLDecoder.decode(a[1],StandardCharsets.UTF_8);
        }
        List<String> out=new ArrayList<>();
        for(Student s:dao.findAll(search))out.add(json(s));
        send(ex,200,"["+String.join(",",out)+"]");
    }

    private void one(HttpExchange ex,int id)throws Exception{
        Student s=dao.findById(id);
        send(ex,s==null?404:200,s==null?error("Student not found"):json(s));
    }

    private void create(HttpExchange ex)throws Exception{
        Student s=parse(body(ex)); validate(s); int id=dao.create(s); s.setId(id); send(ex,201,json(s));
    }

    private void update(HttpExchange ex,int id)throws Exception{
        Student s=parse(body(ex)); validate(s);
        if(!dao.update(id,s)){send(ex,404,error("Student not found"));return;}
        s.setId(id); send(ex,200,json(s));
    }

    private void delete(HttpExchange ex,int id)throws Exception{
        boolean deleted = dao.delete(id);
        send(ex, deleted ? 200 : 404,
            deleted ? "{\"message\":\"Deleted\"}" : error("Student not found"));
    }

    private Student parse(String json){
        Map<String,String> m=new HashMap<>(); Matcher x=FIELD.matcher(json);
        while(x.find()){
            String v=x.group(2).trim();
            if(v.startsWith(""")&&v.endsWith("""))v=v.substring(1,v.length()-1)
                .replace("\"",""").replace("\\","\");
            m.put(x.group(1),v);
        }
        return new Student(m.getOrDefault("name","").trim(),m.getOrDefault("email","").trim(),
            m.getOrDefault("course","").trim(),Integer.parseInt(m.getOrDefault("yearLevel","0")),
            m.getOrDefault("phone","").trim());
    }

    private void validate(Student s){
        if(s.getName().isBlank()||s.getEmail().isBlank()||s.getCourse().isBlank())
            throw new IllegalArgumentException("Name, email and course are required");
        if(s.getYearLevel()<1||s.getYearLevel()>6)
            throw new IllegalArgumentException("Year must be between 1 and 6");
        if(!s.getEmail().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$"))
            throw new IllegalArgumentException("Invalid email");
    }

    private String body(HttpExchange e)throws IOException{
        try(InputStream in=e.getRequestBody()){return new String(in.readAllBytes(),StandardCharsets.UTF_8);}
    }

    private String json(Student s){
        return "{"id":"+s.getId()+","name":"+JsonUtil.quote(s.getName())+
            ","email":"+JsonUtil.quote(s.getEmail())+","course":"+JsonUtil.quote(s.getCourse())+
            ","yearLevel":"+s.getYearLevel()+","phone":"+JsonUtil.quote(s.getPhone())+"}";
    }

    private String error(String s){return "{"error":"+JsonUtil.quote(s)+"}";}

    private void send(HttpExchange e,int status,String text)throws IOException{
        byte[] b=text.getBytes(StandardCharsets.UTF_8); e.sendResponseHeaders(status,b.length);
        try(OutputStream out=e.getResponseBody()){out.write(b);}
    }
}
