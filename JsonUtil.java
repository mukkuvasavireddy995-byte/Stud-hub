package com.studenthub.util;

public final class JsonUtil {
    private JsonUtil(){}
    public static String quote(String s){
        if(s==null)s="";
        return """ + s.replace("\\","\\\\").replace(""","\\"")
            .replace("\n","\\n").replace("\r","\\r") + """;
    }
}
