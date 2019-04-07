package com.isis.adventureISIServer.SOA_Projet_Angular;

import generated.PallierType;
import generated.ProductType;
import generated.World;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 *
 * @author Theo
 */
public class Services {
    
    public World readWorldFromXml(String username) {
        World world_return = null;
        try {   
            File world_xml = new File(username+"-world.xml");
            if(!world_xml.exists()) {
                try {
                    //System.out.println("-------------------------world" + username + "don't exist");
                    InputStream input_base = getClass().getClassLoader().getResourceAsStream("world.xml");
                    JAXBContext cont = JAXBContext.newInstance(World.class);
                    Unmarshaller u = cont.createUnmarshaller();
                    World world_base = (World) u.unmarshal(input_base);

                    Marshaller m = cont.createMarshaller();
                    m.marshal(world_base, new File(username+"-world.xml"));
                } catch (JAXBException e) {
                    return null;
                }              
                try {
                    File world_xml_new = new File(username+"-world.xml");
                    JAXBContext cont = JAXBContext.newInstance(World.class);
                    Unmarshaller u_new = cont.createUnmarshaller();
                    World world_new = (World) u_new.unmarshal(world_xml_new);
                    world_return = world_new;
                } catch (JAXBException e) {
                    return null;
                }
            } else {
                //System.out.println("-------------------------world" + username + " exist");
                JAXBContext cont = JAXBContext.newInstance(World.class);
                Unmarshaller u = cont.createUnmarshaller();
                World world = (World) u.unmarshal(world_xml);
                world_return = world;
            }
        } catch (JAXBException ex) {
            Logger.getLogger(Services.class.getName()).log(Level.SEVERE, null, ex);
        }    
        return world_return;
    }
    
    public void saveWorldToXml(World world, String username) {
        System.out.println("--------------------------------save world");
        try {         
            OutputStream  output = new FileOutputStream(username + "-world.xml");
            JAXBContext cont = JAXBContext.newInstance(World.class);
            Marshaller m = cont.createMarshaller();
            m.marshal(world, output);
        } catch (JAXBException | FileNotFoundException ex) {
            Logger.getLogger(Services.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    // prend en paramètre le pseudo du joueur et le produit 
// sur lequel une action a eu lieu (lancement manuel de production ou  
// achat d’une certaine quantité de produit) 
 
// renvoie false si l’action n’a pas pu être traitée   
    public Boolean updateProduct(String username, ProductType newproduct) { 
        // aller chercher le monde qui correspond au joueur         
        World world = getWorld(username); 
        // trouver dans ce monde, le produit équivalent à celui passé        
        // en paramètre         
        ProductType product = findProductById(world, newproduct.getId());         
        if (product == null) { return false;}                  
        // calculer la variation de quantité. Si elle est positive c'est         
        // que le joueur a acheté une certaine quantité de ce produit    
        // sinon c’est qu’il s’agit d’un lancement de production.         
        int qtchange = newproduct.getQuantite() - product.getQuantite();         
        if (qtchange > 0) {             
            // soustraire de l'argent du joueur le cout de la quantité             
            // achetée et mettre à jour la quantité de product 
            world.getProducts().getProduct().get(newproduct.getId() - 1).setQuantite(newproduct.getQuantite());
            world.setMoney(world.getMoney() - (product.getCout() * ((1-Math.pow(product.getCroissance(), newproduct.getQuantite()))/(1-product.getCroissance()))));
        } else {             
            // initialiser product.timeleft à product.vitesse             
            // pour lancer la production  
            world.getProducts().getProduct().get(newproduct.getId() - 1).setTimeleft(product.getVitesse());
            world.setLastupdate(System.currentTimeMillis());
        } 
        // sauvegarder les changements du monde         
        saveWorldToXml(world, username);
        System.out.println("----- upadate product : final world money : " + world.getMoney());
        return true;     
    }
    
    // prend en paramètre le pseudo du joueur et le manager acheté. 
    // renvoie false si l’action n’a pas pu être traitée   
    public Boolean updateManager(String username, PallierType newmanager) {      
        // aller chercher le monde qui correspond au joueur      
        World world = getWorld(username); 
        // trouver dans ce monde, le manager équivalent à celui passé      
        // en paramètre      
        PallierType manager = findManagerByName(world, newmanager.getName()); 
        if (manager == null) {   
            System.out.println("----- update manager : manager null");
            return false;
        }                     
        // débloquer ce manager            
        // trouver le produit correspondant au manager      
        ProductType product = findProductById(world, manager.getIdcible());      
        if (product == null) { 
            System.out.println("----- update manager : prodcut null");
            return false;
        }     
        // débloquer le manager de ce produit             
        // soustraire de l'argent du joueur le cout du manager 
        // sauvegarder les changements au monde  
        world = buyManager(world, newmanager);
        saveWorldToXml(world, username);      
        System.out.println("----- upadate manager : final world money : " + world.getMoney());
        return true;     
    } 

    private ProductType findProductById(World world, int id) {
        ProductType productTemp = null;
        for (ProductType product : world.getProducts().getProduct()) {
            if (product.getId() == id) 
                productTemp = product; 
        }
        return productTemp;
    }
    
    private PallierType findManagerByName(World world, String name) {
        System.out.println("----- findManagerByName : searching manager : " + name);
        for (PallierType manager : world.getManagers().getPallier()) {
            System.out.println("----- findManagerByName : for manager : " + manager.getName());
            if (manager.getName().equals(name)) 
                System.out.println("----- findManagerByName : manager found : " + manager.getName());
                return manager; 
        }
        return null;
    }

    private World buyManager(World world, PallierType newmanager) {
        World worldTemp = world;
        for (PallierType manager : worldTemp.getManagers().getPallier()) {
            if (manager.getName().equals(newmanager.getName())) {
                manager.setUnlocked(true);
                for (ProductType product : worldTemp.getProducts().getProduct()) {
                    if (product.getId() == manager.getIdcible()) {
                        product.setManagerUnlocked(true);
                    }
                }
                worldTemp.setMoney(worldTemp.getMoney() - manager.getSeuil());
            }
        }

        return worldTemp;    }

    private World getWorld(String username) {
        World world = readWorldFromXml(username);
        Long timePassed = System.currentTimeMillis() - world.getLastupdate();
        System.out.println("----- getWorld : time passed : " + timePassed);
        
        System.out.println("----- World name : " + world.getName());
        System.out.println("----- World money before : " + world.getMoney());
        
        for (ProductType product : world.getProducts().getProduct()) {
            if (product.isManagerUnlocked()) {
                world.setMoney(world.getMoney()+(0));
                world.setScore(world.getMoney()+(0));
            }
            else if ((product.getTimeleft() < timePassed) && (product.getTimeleft() != 0)) {
                world.setMoney(world.getMoney()+(product.getRevenu()*product.getQuantite()));
                world.setScore(world.getMoney()+(timePassed/product.getVitesse()*product.getRevenu()*product.getQuantite()));
            }
        }
        
        System.out.println("----- World money after : " + world.getMoney());
        
        world.setLastupdate(System.currentTimeMillis());
        saveWorldToXml(world, username);          
        return world;
    }

}


