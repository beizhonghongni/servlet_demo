package com.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
@WebServlet(name = "MyServlet", urlPatterns = "/")
public class MyServlet extends javax.servlet.http.HttpServlet {
    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        this.doGet(request, response);
    }
    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        Enumeration<String> parameterNames = request.getParameterNames();
        Map<String, String> paramMap = new HashMap<>();
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            paramMap.put(paramName, request.getParameter(paramName));
        }
        response.setContentType("application/json;charset=UTF-8");
        DB db = DB.getInstance();
        String target = request.getRequestURI().split("/")[1];
        Object result = null;
        switch (target){
            case "order": result = db.order(paramMap); break;
            case "queryDate": result = db.queryDate(paramMap); break;
            case "queryCustomer": result = db.queryCustomer(paramMap); break;
            default: result = "path error";
        }
        response.getWriter().print(JSONObject.toJSONString(result));
    }

}
