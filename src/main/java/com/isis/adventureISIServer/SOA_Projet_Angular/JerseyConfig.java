package com.isis.adventureISIServer.SOA_Projet_Angular;

import javax.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

/**
 *
 * @author Theo
 */
@Component 
@ApplicationPath("/adventureisis") public class JerseyConfig extends ResourceConfig { 
 
    public JerseyConfig() {                 
        register(Webservice.class);  
        register(CORSResponseFilter.class); 
    } 
} 
 
