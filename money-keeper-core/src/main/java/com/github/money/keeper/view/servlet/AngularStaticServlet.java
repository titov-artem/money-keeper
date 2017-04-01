package com.github.money.keeper.view.servlet;

import org.eclipse.jetty.servlet.DefaultServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AngularStaticServlet extends DefaultServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpServletRequestWrapper wrapper = new HttpServletRequestWrapper(request) {

            @Override public String getPathInfo() {
                String pathInfo = super.getPathInfo();
                if (pathInfo.contains(".") || pathInfo.endsWith("/")) {
                    // for files return
                    return pathInfo;
                }
                return "/";
            }
        };
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
        super.doGet(wrapper, response);
    }
}
