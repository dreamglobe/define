package com.kamomileware.define.match.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class IndexController {

    @RequestMapping(value = "/")
    public void index(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        try {
            RequestDispatcher dispatcher = request.getRequestDispatcher("index.html");
            dispatcher.forward(request, response);
        } catch (IOException e) {
            throw new ServletException("Error accessing index.html", e);
        }
    }
}