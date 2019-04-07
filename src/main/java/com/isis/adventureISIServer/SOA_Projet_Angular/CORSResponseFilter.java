package com.isis.adventureISIServer.SOA_Projet_Angular;

import com.sun.net.httpserver.Headers;
import java.io.IOException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author Theo
 */
@Provider public class CORSResponseFilter implements ContainerResponseFilter { 
 
    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException { 
        MultivaluedMap<String, Object> headers = responseContext.getHeaders();
        headers.add("Access-Control-Allow-Origin", "*");  
        headers.add("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT, OPTIONS");         
        headers.add("Access-Control-Allow-Headers", "X-Requested-With, Content-Type, X-Codingpedia, authorization, user");     
    } 
} 
 
