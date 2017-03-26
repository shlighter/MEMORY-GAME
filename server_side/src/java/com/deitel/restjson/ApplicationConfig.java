
package com.deitel.restjson;

import java.util.Set;
import javax.ws.rs.core.Application;

/**
 *
 * @author Shai
 */
@javax.ws.rs.ApplicationPath("webresources")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method.
     * It is automatically populated with
     * all resources defined in the project.
     * If required, comment out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        
        resources.add(com.deitel.restjson.getScores.class);
        resources.add(com.deitel.restjson.jsonResource.class);
        resources.add(com.deitel.restjson.login.class);
        resources.add(com.deitel.restjson.register.class);
        resources.add(com.deitel.restjson.updateScores.class);
    }
    
}
