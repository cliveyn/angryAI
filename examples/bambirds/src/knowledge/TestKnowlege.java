/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package knowledge;

import java.util.List;
import java.util.ArrayList;
import ab.vision.ABObject;
import ab.vision.ABType;
import java.awt.Point;
import java.awt.Rectangle;
import java.nio.file.Path;

/**
 *
 * @author lordminx
 */
public class TestKnowlege {
    //Knowledge know;

    public TestKnowlege() {
        //this.know = new Knowledge();
    }
    
    public static List<List<ABObject>> getData() {
        ArrayList<List<ABObject>> data = new ArrayList<>();
        
        List<ABObject> blocks = new ArrayList<ABObject>();
        List<ABObject> pigs = new ArrayList<ABObject>();
        List<ABObject> birds = new ArrayList<ABObject>();
                    
        blocks.add(new ABObject(new Rectangle(10,10), ABType.Ice));
        blocks.add(new ABObject(new Rectangle(12, 10, 20, 10), ABType.Wood));
        blocks.add(new ABObject(new Rectangle(12, 20, 20, 10), ABType.Wood));
        blocks.add(new ABObject(new Rectangle(12, 30, 20, 10), ABType.Ice));
        blocks.add(new ABObject(new Rectangle(12, 100, 20, 10), ABType.Stone));
        
        pigs.add(new ABObject(new Rectangle(12, 90, 10, 10), ABType.Pig));
        
        
        
        data.add(blocks);
        data.add(pigs);
        return data;
     
    }
    
    public static void main(String[] args) {
        Knowledge foo = new Knowledge();
        
        List<List<ABObject>> blocks = getData();
        
        for (List<ABObject> x : blocks){
            for (ABObject block : x){
                System.out.println(block.globalID + ": " + block.toString());
            }
        }
        try {
            //test writeProlog
            Path filepath = foo.buildModell(blocks, null, "situation"); // FIXME: using null pointer instead of vision 

            // check path
            System.out.println(filepath.toString());
        } catch (BadWorldException e) {
            System.out.println(e.toString());
        }
    
        // test getStructures
       // List<ABObject> blocksandpigs = getData().get(0);
       // blocksandpigs.addAll(getData().get(1));
       // System.out.println(foo.getStructures(blocksandpigs));
        
        
        
    }
}

