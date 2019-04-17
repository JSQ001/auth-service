package com.hand.hcf.app.core.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hcf.app.core.web.exception.ExceptionDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AuthExceptionEntryPoint implements AuthenticationEntryPoint {


    @Autowired
    ObjectMapper mapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException)
            throws ServletException {

        ExceptionDetail exp=new ExceptionDetail();
        exp.setError(HttpStatus.UNAUTHORIZED.getReasonPhrase());
        exp.setMessage(authException.getMessage());
        exp.setPath(request.getServletPath());
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        try {
             mapper = new ObjectMapper();
            mapper.writeValue(response.getOutputStream(), exp);
        } catch (Exception e) {
            throw new ServletException();
        }
    }

}
