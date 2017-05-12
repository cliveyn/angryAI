/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package knowledge;

import ab.demo.other.ActionRobot;
import ab.vision.ABObject;
import ab.vision.Vision;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import physics.Physics;


/**
 *
 * @author lordminx
 */
/*public class LevelExtractor {
    
    
    
    
    
    public static void main(String[] args) {
        ActionRobot ab = new ActionRobot();
        Knowledge know = new Knowledge();
        Physics phys = new Physics();
        
        System.out.println("works");
        ab.GoFromMainMenuToLevelSelection();
        
        for (int i = 1; i <= 40; i++) {
            
            System.out.println("Loading Level " + i);
            ab.loadLevel(i);
            BufferedImage screenshot = ab.doScreenShot();
            Vision vision = new Vision(screenshot);
            List<List<ABObject>> objects = phys.extractFeaturesForScreenShot(screenshot);
            
            // Dump level objects to text file
            String blocktemp = "%s:\nCoordinates: %s, %s, %s, %s\nType: %s\n\n";
            List<String> blockstrings = new ArrayList<>();
            for (List<ABObject> list : objects) {
                for (ABObject ob : list) {
                    blockstrings.add(String.format(blocktemp, ob.globalID, ob.x, ob.y, ob.width, ob.height, ob.getType().toString()));
                    
                }
                
            }
            writeList(blockstrings, "level" + i + "-objects");
            
            try {
                
                Path foo = know.buildModell(objects, vision, "level" + i);
                System.out.println(foo.toString());
            } catch (BadWorldException e) {
                System.out.println("I done goofed:\n" + e.toString());
            }
            
            
        }
    }
    
    
    private static void writeList(List<String> results, String filename) {
		
            Path filepath = Paths.get("./" + filename + ".txt").toAbsolutePath().normalize();

            try {
                FileWriter fw;
                fw = new FileWriter(filepath.toFile());
                BufferedWriter bw = new BufferedWriter(fw);

                for(String result: results) {
                    bw.write(result);
                }
			
                bw.close();
                

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
    
    }
    
}*/