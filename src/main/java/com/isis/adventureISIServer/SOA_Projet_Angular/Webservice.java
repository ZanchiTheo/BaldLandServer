package com.isis.adventureISIServer.SOA_Projet_Angular;

import com.google.gson.Gson;
import generated.PallierType;
import generated.ProductType;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("generic")
public class Webservice { 
 
    Services services; 
 
    public Webservice() {         
        services = new Services();     
    } 
 
    @GET 
    @Path("world") 
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON}) 
    public Response getXml(@Context HttpServletRequest request) {
        String username = request.getHeader("user");   
        System.out.println("---------- header username : " + request.getHeader("user"));
        System.out.println("---------- username : " + username);
        return Response.ok(services.readWorldFromXml(username)).build();        
    }
    
    @PUT 
    @Path("product") 
    @Consumes(MediaType.APPLICATION_JSON) 
    public Response putProduct(@Context HttpServletRequest request, ProductType product) {  
        //ProductType p = new Gson().fromJson(product, ProductType.class); 
        String username = request.getHeader("user");
        System.out.println("---------- username : " + username);
        return Response.ok(services.updateProduct(username, product)).build();          
    }
     
    @PUT 
    @Path("manager") 
    @Consumes(MediaType.APPLICATION_JSON) 
    public Response putManager(@Context HttpServletRequest request, PallierType manager) {  
        //PallierType m = new Gson().fromJson(manager, PallierType.class); 
        String username = request.getHeader("user");
        System.out.println("----- putManager : username : " + username);
        System.out.println("----- putManager : manager : " + manager.getName());
        return Response.ok(services.updateManager(username, manager)).build();          
    }
} 
