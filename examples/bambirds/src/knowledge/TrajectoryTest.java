/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package knowledge;

//import ab.demo.other.ActionRobot;
import ab.planner.TrajectoryPlanner;
import ab.vision.ABObject;
import ab.vision.Vision;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import static knowledge.Knowledge.isReachable;
import physics.Physics;

import physics.calculation.Calculation;

/**
 *
 * @author lordminx
 */
/*public class TrajectoryTest {
    
    
    public static void main(String[] args) {
        
        
        ActionRobot ab = new ActionRobot();
        Knowledge know = new Knowledge();
        Physics phys = new Physics();
    
        
        System.out.println("works");
        ab.GoFromMainMenuToLevelSelection();
        
        for (int i = 1; i <= 20; i++) {
            TrajectoryPlanner tp = new TrajectoryPlanner();
        
            System.out.println("Loading Level " + i);
            ab.loadLevel(i);
            BufferedImage screenshot = ab.doScreenShot();
            Vision vision = new Vision(screenshot);
            List<List<ABObject>> objects = phys.extractFeaturesForVision(vision);
            
            Rectangle sling = vision.findSlingshotMBR();
            
            List<ABObject> blocks = new ArrayList<>();
            blocks.addAll(objects.get(0)); // get pigs
            blocks.addAll(objects.get(2)); // get blocks
            
            List<ABObject> hittable = new ArrayList<>();
            
            for (ABObject target : blocks) {
                Point tpoint = target.getCenter();
                List<Point> relpoints = tp.estimateLaunchPoint(sling, tpoint);
                boolean hits = false;
                for (Point rel : relpoints) {
                    
                    List<Point> traj = tp.predictTrajectory(sling, rel);
                    hits = isReachable(traj, target, blocks);
                    //System.out.println("Hit " + target.globalID + " with " + rel + ": " + hits);
                }
                if (hits){ 
                    hittable.add(target);
                }
                
                
                // test trajectory
            }
            
            System.out.println("Done. Hittables found: " + hittable.size());
            System.out.println("Hittable Percentage: " + (((double)hittable.size() / blocks.size()) * 100));
            
        }
    }
    
}*/
