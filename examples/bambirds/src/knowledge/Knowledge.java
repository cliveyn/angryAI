package knowledge;

import ab.planner.TrajectoryPlanner;
import main.BamBirdModule;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ab.vision.*;
import ab.vision.real.shape.Poly;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import static java.lang.Math.abs;
import static java.lang.Math.max;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Knowledge extends BamBirdModule {

    private Rectangle sling;
    private List<ABObject> allBlocks;

    public Knowledge() {

    }
    

    // predicate templates for string formatting
    private static String pred_1 = "%s(%s).\n";
    private static String pred_2 = "%s(%s,%s).\n";
    private static String pred_3 = "%s(%s,%s,%s).\n";
    
    private int bar = 2000;
    
    /**
     * @param objects List<List<ABObjects>>
     * @param filename String with the filename for the prolog file ("level1", ...)
     * @return Path to prolog file
     * 
     * takes a list of list of ABObjects and generates Prolog predicates
     * uses the writeProlog Method to write the predicates in
     * situation.pl
     */
    public Path buildModell(List<List<ABObject>> objects, Vision vision, String filename) throws BadWorldException {
        
        if (vision == null) {
            throw new BadWorldException("[KR]Can't build a Modell without Vision.");
        }
        
        if (objects == null) {
            throw new BadWorldException("[KR]Can't build modell on null.");
        } else if (objects.get(0) == null || objects.get(0).size() == 0) {
            throw new BadWorldException("[KR]No pigs, no Modell.");
        } else if (objects.get(1) == null || objects.get(1).size() == 0) {
            throw new BadWorldException("[KR]No birds, no Modell.");
        }
        
        
        // List of resulting predicate strings
        ArrayList<String> results = new ArrayList<String>();

        List<ABObject> birds = new ArrayList<>();
        this.allBlocks = new ArrayList<>();
        List<ABObject> pigs = new ArrayList<>();
        List<ABObject> hills = new ArrayList<>();
        this.sling = vision.findSlingshotMBR();
        
        if(this.sling == null){
        	return null;
        }
        
        // seperate  birds and write one list for relation check
        for (List<ABObject> l : objects ) {
            if (l == null) {
                System.out.println("[KR] Vision Fail: Null list in objects!");
                continue;
            }
            for (ABObject block : l) {
                if (block == null) {
                    System.out.println("[KR] Vision Fail: Null block in objects!");
                    continue;
                }
                if (block.getType().toString().contains("Bird")) {
                    birds.add(block);
                } else if (block.getType() == ABType.Hill ){
                    hills.add(block);
                } else if (block.getType() == ABType.Ground || block.getType() == ABType.Unknown){
                    // ignore for now
                } else {
                    
                    this.allBlocks.add(block);
                    if (block.getType() == ABType.Pig) {
                        pigs.add(block);
                    }
                
                    
                }
            }  
        }
        
        // Iterate over List of List
        for (List<ABObject> list : objects) {
            
            for (ABObject ob : list) {
            	
            
            	ABObject above = ob;
            	String orientation;
            	String form;
            	
            	if(ob.width >= ob.height) orientation = "horizontal";
            	else orientation ="vertical";

                switch (ob.getType()) { 
                    
                    case Ground:    results.add(String.format(pred_1, "ground", ob.globalID));
                                    break;
                                    
                    case Hill: 		results.add(String.format(pred_1, "hill", ob.globalID));
                    				break;
                    case Sling: break;
                    case RedBird:   results.add(String.format(pred_1, "bird", ob.globalID)); 
                                    results.add(String.format(pred_2, "hasColor", ob.globalID, "red"));
                                    break;

                    case YellowBird: results.add(String.format(pred_1, "bird", ob.globalID));
                                     results.add(String.format(pred_2, "hasColor", ob.globalID, "yellow"));
                                     break;

                    case BlueBird:  results.add(String.format(pred_1, "bird", ob.globalID));
                                    results.add(String.format(pred_2, "hasColor", ob.globalID,"blue"));
                                    break;

                    case BlackBird: results.add(String.format(pred_1, "bird", ob.globalID));
                                    results.add(String.format(pred_2, "hasColor", ob.globalID,"black"));
                                    break;
                                    
                    case WhiteBird: results.add(String.format(pred_1, "bird", ob.globalID));
                                    results.add(String.format(pred_2, "hasColor", ob.globalID,"white"));
                                    break;
                                    
                    case Pig:       results.addAll(getRelations(ob,allBlocks));
                                    results.add(String.format(pred_1, "pig", ob.globalID));
                                    above = whatsAbove(ob, allBlocks);
                                    if(above != ob) results.add(String.format(pred_2,"isOver", ob.globalID, above.globalID));
                                    break;
                                    
                    case Ice:       results.addAll(getRelations(ob,allBlocks));
                                    results.add(String.format(pred_2, "hasMaterial", ob.globalID, "glass"));
                                    results.add(String.format(pred_2, "hasOrientation", ob.globalID, orientation));
                                    results.add(String.format(pred_2, "hasForm", ob.globalID, getForm(ob)));
                                    break;
                                    
                    case Wood:      results.addAll(getRelations(ob,allBlocks));
                                    results.add(String.format(pred_2, "hasMaterial", ob.globalID, "wood"));
                    				results.add(String.format(pred_2, "hasOrientation", ob.globalID, orientation));
                    				form = getForm(ob);
                    				if (form == "ball") {
                    					above = whatsAbove(ob,list);
                    					if(above != ob) results.add(String.format(pred_2,"isOver", ob.globalID, above.globalID));
                    				}
                    				results.add(String.format(pred_2, "hasForm", ob.globalID, getForm(ob)));
                                    break;
                                    
                    case Stone:     results.addAll(getRelations(ob,allBlocks));
                                    results.add(String.format(pred_2, "hasMaterial", ob.globalID, "stone"));
                    				results.add(String.format(pred_2, "hasOrientation", ob.globalID, orientation));
                    				form = getForm(ob);
                    				if (form == "ball"){
                    					above = whatsAbove(ob,list);
                    					if(above != ob) results.add(String.format(pred_2,"isOver", ob.globalID, above.globalID));
                    				}
                    				results.add(String.format(pred_2, "hasForm", ob.globalID, form));
                                    break;
                                    
                    case TNT:       results.addAll(getRelations(ob,allBlocks));
                                    results.add(String.format(pred_2,"hasMaterial", ob.globalID, "tnt"));
                    				above = whatsAbove(ob, allBlocks);
                    				if(above != ob) results.add(String.format(pred_2,"isOver", ob.globalID, above.globalID));
                                    List<ABObject> explodes = getExplodables(ob, allBlocks);
                                    for (ABObject ex : explodes) {
                                        results.add(String.format(pred_2, "canExplode", ob.globalID, ex.globalID));
                                    }
                                    break;
                                    
                    default:        // System.out.println("These aren't the blocks you're looking for.");
                                    // results.addAll(getRelations(ob,allBlocks));
                                    // results.add(String.format(pred_1, "object", ob.globalID));
                                    break;

                }
                    
            }

        } 
        
        
        // get Structures
        List<List<ABObject>> structures = getStructures(allBlocks);
        
        // write structures to predicate list
        results.addAll(getStructurePredicates(structures, pigs));
        System.out.println("[KR] Structures found and written.");
        
        
        
        // UGLY HACK to check for hills
        // TODO: There has to be a better way!!!
        if (hills.size() > 0) {
            
            System.out.println("[KR] Checking Hills.");
            Pattern p = Pattern.compile("isOn\\(\\w+,ground\\).\n");

            for (int i = 0; i < results.size(); i++) {
                if (p.matcher(results.get(i)).matches()) {
                    String[] foo = results.get(i).split("[\\(,\\)]");
                    
                    // find ABObject with regexed globalID                    
                    for (ABObject x : allBlocks) {
                        if (x.globalID.equals(foo[1])) {
                            results.set(i, results.get(i).replace("ground", onHill(x, hills)));
                        }
                    }
                }
            }
        } else {
            System.out.println("[KR] No Hills.");
        }
        
        // write size predicates
        for (ABObject block : allBlocks){
            results.add(String.format(pred_2, "hasSize", block.globalID, getSize(block)));
        }
        System.out.println("[KR] Size Predicates checked and written.");
        
        // write birds
        Collections.sort(birds, new YComparator());
        for (int i = 0; i < birds.size(); i++) {
            String birdID = birds.get(i).globalID;
            results.add(String.format(pred_2, "birdOrder", birdID, i));
        }
        
        
        
        // add some inference rules
        
        results.add("object(X) :- hasMaterial(X,_).\n");
        results.add("object(X) :- pig(X).\n");
        if (!hills.isEmpty()) {
            results.add("object(X) :- hill(X).\n");
        }
        
        //TODO: Default:
        /*  protects(structID, pig), isCollapsable(structID)
            belongsTo(obj, structID)
            isAnchorPointFor(struct.get(0).globalID, structID)
            isHittable(block.globalID, "true")
            canCollapse(structID1, structID2), collapsesInDirection(structID2, structID1, "towards")
            isOn(ob1,ob2);isBelow(ob1,ob2);isLeft(ob1,ob2);isRight(ob1,ob2)
            supports(o2, o1)
            birdOrder(birdID, i)
            hill(ob), pig(ob), object(ob), bird(ob), ground(ob)
            hasSize(ob, size), hasForm(ob,form), hasMaterial(ob,mat)
            canExplode(ob.globalID, ex.globalID)
            hasOrientation(ob.globalID, orientation)
         */
        
        System.out.println("[KR]Checking Defaults...");
        results = defaultPredicates(results);
        
        // remove duplicates and sort results
        Set<String> hs = new HashSet<>();
        hs.addAll(results);
        results.clear();
        results.addAll(hs);
        
        Collections.sort(results);
        
      //  checkForDuplicates(results);
        
        
            
        return writeProlog(results, filename);
        
    }
    
    private String getForm(ABObject ob) {
    	String shape = "none";
    	
		switch(ob.shape) {
		case Circle: 	shape = "ball";
						break;
		default:		if(ob.height == ob.width){
							shape = "cube";
						}
						else if (ob.height > (2* ob.width) || ob.width > (2*ob.height)) {
							shape = "bar";
							if (max(ob.height, ob.width) < bar) {
								bar = max(ob.height, ob.width);
							}
						}
						else {
							shape = "block";
						}
						break;
		
		}
    	
    	return shape;
    	
    }
    
    private String getSize (ABObject block) {
        String size = "medium";
        
        switch(getForm(block)){
        
            case "cube":            if (block.height < 2 * bar) {
                                        size = "small";
                                    } else if (block.height > 2 * bar) {
                                        size = "big";
                                    }
                                    break;
            case "bar":             if (max(block.height, block.width) == bar) {
                                        size = "small";
                                    } else if (max(block.height, block.width) > 2* bar) {
                                        size = "big";
                                    }
                                    break;
            default:                return size;
        } 
        
        return size;
    }
    
    /**
     * @param o2 
     * @param list possible objects above o2
     * @return the object above o2. If there is no object than return o2
     */
    
    private ABObject whatsAbove(ABObject o2, List<ABObject> blocks) {
        List<ABObject> list = new ArrayList(blocks);
    	Collections.sort(list, Collections.reverseOrder(new YComparator()));
    	
    	int ex_o2 = o2.x + o2.width;
    	int gap = 5;
    	
    	for (ABObject o1: list) {
    		int ex_ob = o1.x + o1.width;
    		
    		if(o2.y > o1.y && !( o2.x - ex_ob  > gap || o1.x - ex_o2 > gap ))  return o1;
    		
    	}
    	
    	return o2;
    	
    }
    
    /**
     * @param o1 ABObject 
     * @param list List<ABObject>
     * @return  ArrayList<String>  predicates of relations eg isLeft(block2_id,block1_id).
     * 
     * Takes an ABOBject o2 and looks for every ABObject in list if there is an direct relation
     * between them. if hasRelation returns "isOn" than o2 lies on o1
     * 
     * Note: The onGround variable is (hopefully) just a temporary workaround
     */
    
    private ArrayList<String> getRelations(ABObject o2, List<ABObject> list){
    	ArrayList<String> rel = new ArrayList<String>();
    	boolean onGround = true;
    	
    	for(ABObject o1:list) {
    		String relation = hasRelation(o2,o1);
    		switch(hasRelation(o2,o1)) {
    			case "isOn": 	rel.add(String.format(pred_2, relation, o2.globalID, o1.globalID));
    							onGround = false;
    							break;
    			case "isBelow": rel.add(String.format(pred_2, relation, o2.globalID, o1.globalID));
    							break;
    			case "isRight":	rel.add(String.format(pred_2, relation, o2.globalID, o1.globalID));
    							if(o2.getType() != ABType.Pig && o2.angle > 0) rel.add(String.format(pred_2, "supports", o2.globalID, o1.globalID));
    							break;
    			case "isLeft":	rel.add(String.format(pred_2, relation, o2.globalID, o1.globalID));
    							if(o2.getType() != ABType.Pig && o2.angle > 0) rel.add(String.format(pred_2, "supports", o2.globalID, o1.globalID));
    							break;
    			default:	break;
    		}
    	}
    	
    	if(onGround) rel.add(String.format(pred_2, "isOn", o2.globalID, "ground"));
    	
    	return rel;
    }
    
    
    /**
     * 
     * @param block ABObject
     * @param hill Poly
     * @return Boolean whether block is on hill
     */
    private String onHill(ABObject block, List<ABObject> hills) {
        int tolerance = 5;
        
        for (int i = 0; i < hills.size(); i++) {
            ABObject hill = hills.get(i);
            Poly poly;
            poly = (Poly) hill;
            
            if (poly.polygon.intersects(block.x, block.y, block.width, block.height + tolerance)) {
                return hill.globalID;
            }
        }
        return "ground";
    }
    
    
        /**
         * @param o2 ABObject
         * @param o1 ABObject
         * @return String type Relation
         * 
         * checks if o2 is left/right/top of or under o1. 
         */
         
        /*
	private String hasRelation(ABObject o2, ABObject o1)
	{
		String relation = "none";
		
		int gap = 5;
		
		if(o2.x == o1.x && o2.y == o1.y && o2.width == o1.width && o2.height == o1.height)
				return relation;
		
		int ex_o1 = o1.x + o1.width;
		int ex_o2 = o2.x + o2.width;
		
		int ey_o1 = o1.y + o1.height;
		int ey_o2 = o2.y + o2.height;
		
               
                // NOTE : Objects with the same y coordinates can't be on or below each other
                // THIS DOES NOT WORK!!!
                
		if(o2.y != o1.y && (Math.abs(ey_o2 - o1.y) < gap)
			&& !( o2.x - ex_o1  > gap || o1.x - ex_o2 > gap )) 
		{
	        relation = "isOn";

	        
		} else if (o2.y != o1.y && (Math.abs(ey_o1 - o2.y) < gap)
			&& !( o1.x - ex_o2  > gap || o2.x - ex_o1 > gap ))  
		{
			relation = "isBelow";
			
		} else if ((Math.abs(ex_o2 - o1.x) < gap)
				&& !(o2.y - ey_o1 > gap || o1.y - ey_o2 > gap))  
		{
			relation = "isLeft";
			
		} else if ((Math.abs(ex_o1- o2.x) < gap)
				&& !(o1.y - ey_o2 > gap || o2.y - ey_o1 > gap))  
		{
			relation = "isRight";	
		}
		
		return relation;
	}*/
        
        private String hasRelation(ABObject o2, ABObject o1 ) {
            String relation = "none";
            
            if ( o2.contains(o1) && o1.contains(o2)) {
                return relation;
            }
            
            if ((new Rectangle(o2.x, o2.y + o2.height, o2.width, 5).intersects(o1))) {
                return "isOn";
            } else if ((new Rectangle(o2.x, o2.y -5, o2.width, 5).intersects(o1))) {
                return "isBelow";
            } else if ((new Rectangle(o2.x + o2.width, o2.y, 5, o2.height)).intersects(o1)) {
                return "isLeft";
            } else if ((new Rectangle(o2.x -5 , o2.y, 5, o2.height)).intersects(o1)) {
                return "isRight";
            }
            
            
            
            return relation;
        }
        
        /**
         * @param struct1 (for now) structure with smaller x-value
         * @param structID1 
         * @param struct2 (for now) structure with higher x-value
         * @param structID2
         * @return List<String> of predicates e.g. "canCollapse(structID1, structID2)."
         */
        private List<String> orderStructures (List<ABObject> struct1, String structID1, List<ABObject> struct2, String structID2){
            List<String> predicates = new ArrayList();
            
            double range = 0.6;
            
            // direction of structures
            predicates.add(String.format(pred_3, "collapsesInDirection", structID1, structID2, "away"));
            predicates.add(String.format(pred_3, "collapsesInDirection", structID2, structID1, "towards"));
            
            Collections.sort(struct1, new YComparator());
            Collections.sort(struct2, new YComparator());
            
            ABObject top1 = struct1.get(0);
            ABObject bottom1 = struct1.get(struct1.size() -1);
            
            ABObject top2 = struct2.get(0);
            ABObject bottom2 = struct2.get(struct2.size() -1);
            
            int height1 = (bottom1.y + bottom1.height) - top1.y;
            int height2 = (bottom2.y + bottom2.height) - top2.y;
            
            Collections.sort(struct1, new XComparator());
            Collections.sort(struct2, new XComparator());
            
            ABObject right1 = struct1.get(struct1.size() -1);
            ABObject left1 = struct1.get(0);
            
            ABObject right2 = struct2.get(struct2.size() -1);
            ABObject left2 = struct2.get(0);

            int center1 = (right1.x + right1.width + left1.x) / 2;
            int center2 = (right2.x + right2.width + left2.x) / 2;
            
            // difference in elevation between two structures
            int elevation = (bottom1.y + bottom1.height) - (bottom2.y + bottom2.height);
            
            
            if ( elevation < 0) {  // struct1 is at highter elevation than struct 2
                height1 += abs(elevation);
            } else {  
                height2 += elevation;
            }
            
            
            
            if (center1 + (height1 * range(top1)) > left2.x) {
                predicates.add(String.format(pred_2, "canCollapse", structID1, structID2));
            }
            
            if (center2 - (height2 * range(top2)) < (right1.x + right1.width)) {
                predicates.add(String.format(pred_2, "canCollapse", structID2, structID1));
            }
            
            return predicates;
        }
        
        /**
         * Return range of Object depending on Object shape
         * @param block ABObject to decide range for
         * @return double
         */
        private double range(ABObject block) {
            if (block.shape == ABShape.Circle) {
                return 2.0;
            } else {
                return 1.0;
            }
        }
        
        private List<ABObject> getExplodables(ABObject tnt, List<ABObject> candidates) {
            List<ABObject> explodables = new ArrayList<>();
            
            Point center = tnt.getCenter();
            int multiplier = 3;
            int width = tnt.width;
            
            for (ABObject block : candidates) {
                Point centerb = block.getCenter();
                double dist = Math.sqrt((center.x - centerb.x)^2 + (center.y - centerb.y)^2);
                if ( block != tnt && dist <= width * multiplier) {
                    explodables.add(block);
                }
            }
            
            return explodables;
        }
        
        
        
        /**
         * Get isHittable predicate for group of blocks
         * @param blocks List of blocks in a structure
         * @return List<String> of prolog predicates
         */
        private List<String> isReachable (List<ABObject> blocks) {
            
            List<ABObject> reachables = new ArrayList<>();
            List<String> predicates = new ArrayList<>();
            
            
            for (ABObject block : blocks) {
                boolean reachable = false;
                List<List<Point>> trajs = getTrajectories(sling, block);
                for (List<Point> traj : trajs) {
                    reachable = isReachable(traj, block, allBlocks);
                }
                if (reachable) {
                    reachables.add(block);
                    System.out.println("[KR]Now hit using Trajectories: " + block.globalID);
                }
            }
            
            for (ABObject block : reachables) {
                predicates.add(String.format(pred_2, "isHittable", block.globalID, "true"));
            }
            
            return predicates;
        }
        
        
        private void checkForDuplicates(List<String> predicates) {
            Pattern pred = Pattern.compile("(\\w+)\\((\\w+),(\\w+)\\).\n");
           

            for (int i = 0; i < predicates.size() -1; i++) {
                
                String foo = predicates.get(i);
                
                if (foo.contains("dummyObject")) {
                    continue;
                }
                
                Matcher m = pred.matcher(foo);

                if (m.find()) {
                    String s1 = m.group(1);
                    String s2 = m.group(2);
                    String s3 = m.group(3);
                    
                    Pattern dup = Pattern.compile(String.format(pred_2, s1, s3, s2));
                    System.out.println("Looking for: " + dup.toString());
                    
                    for (int j = i + 1; j < predicates.size(); j++) {
                        String target = predicates.get(j);
                        
                        Matcher n = dup.matcher(target);
                        if (n.matches()) {
                            System.err.println(target);
                        }
                        
                    }
                    
                    
                    
                    
                }
                
            }

        }


	/**
         * Writes the given ArrayList into a new Prolog file
         * 
         * @param results [ArrayList<String>]
         * @return Path to prolog file
        */
	private Path writeProlog(ArrayList<String> results, String filename) {
		
            Path filepath = Paths.get("./" + filename + ".pl").toAbsolutePath().normalize();

            try {
                FileWriter fw;
                fw = new FileWriter(filepath.toFile());
                BufferedWriter bw = new BufferedWriter(fw);

                for(String result: results) {
                    bw.write(result);
                }
			
                bw.close();
                

            } catch (IOException e) {
                System.out.println("[KR] Couldn't write prolog" + e.getMessage());
            }
            
            return filepath;
	}
    
        
    /**
     * Search candidates for blocks neighboring target.
     * @param target ABObject
     * @param candidates List of ABObjects to search for neighbors
     * @return List<ABObject> of neighbors to target.
     */
    private List<ABObject> getNeighbors(ABObject target, List<ABObject> candidates) {

        List<ABObject> neighbors = new ArrayList<>();
        
        for (ABObject x : candidates) {

            if (hasRelation(target, x) != "none") {

                neighbors.add(x);
            }
        }      
	return neighbors;
    }
    


    /**
     * Recursively searches candidates for neighboring blocks.
     * 
     * @param target ABObject to start search from.
     * @param candidates List<ABObject> of possible candidates
     * @param visited List<ABObject> of already visited blocks
     * @return List<ABObject> of closely neighboring blocks
     * 
     */
    private List<ABObject> floodFill(ABObject target, List<ABObject> candidates, List<ABObject> visited) {
        if(visited.contains(target)){
            return visited;
        } else {

            visited.add(target);
            List<ABObject> neighbors = getNeighbors(target, candidates);
            
            for (ABObject neighbor:neighbors) {
                
                visited = floodFill(neighbor, candidates, visited);
            }
            
            return visited;
        }
    }
    
    
    /**
     * Find groups of neighboring blocks.
     * 
     * @param blocklist List of ABObjects to search for grouped blocks.
     * @return List of Lists of grouped blocks.
     */
    public List<List<ABObject>> getStructures(List<ABObject> blocklist){
        List<ABObject> candidates = new ArrayList<>(blocklist);
        List<List<ABObject>> structures = new ArrayList<List<ABObject>>();
        
        while (candidates.size() != 0) {
            
            List<ABObject> structure = floodFill(candidates.get(0), candidates, new ArrayList<ABObject>());
            
            candidates.removeAll(structure);
            
            
            structures.add(structure);
            
        }        
        return structures;
    }
    
    
    /**
     * Build structural Prolog Predicates from List of list of ABObjects.
     * 
     * @param structures List of Lists of grouped ABObjects
     * @return List of Strings with Prolog Predicates.
     */
    public List<String> getStructurePredicates(List<List<ABObject>> structures, List<ABObject> pigs) {
        List<String> predicates = new ArrayList<String>();
        
        Collections.sort(structures, new sortStructuresByX());
        
        for (int i=0; i < structures.size(); i++){
            List<ABObject> struct = structures.get(i);
            
            predicates.addAll(isReachable(struct));
            
            String structID = "struct" + i;
            predicates.add(String.format(pred_1, "structure", structID));
            
            //sort struct for finding leftmost object
            Collections.sort(struct, new XComparator());
            predicates.add(String.format(pred_2, "isAnchorPointFor", struct.get(0).globalID, structID ));
            
            for (ABObject obj : struct) {
                predicates.add(String.format(pred_2, "belongsTo", obj.globalID, structID));    
            }
            
            //make structure collapsable.
            predicates.add(String.format(pred_1, "isCollapsable", structID));
            
            if (i != structures.size() -1){
                // check which structures can collapse each other
                predicates.addAll(orderStructures(struct, structID, structures.get(i + 1), "struct" + (i+1)));
            }

            // check if structure protects a pig
            for (ABObject pig : pigs) {
                if (!struct.contains(pig) && whatsAbove(pig, struct) != pig) {
                    predicates.add(String.format(pred_2, "protects", structID, pig.globalID));
                }
            }
        }
        
        return predicates;
        
    }
  
    
        private List<List<Point>> getTrajectories(Rectangle sling, ABObject target) {
            List<List<Point>> res = new ArrayList();
            TrajectoryPlanner tp = new TrajectoryPlanner();
            
            
            List<Point> relPoints = tp.estimateLaunchPoint(sling, target.getCenter());
            
            for (Point rel: relPoints) {
                List<Point> traj = tp.predictTrajectory(sling, rel);
                res.add(traj);
            }
            
            return res;
        }
        
    
        
        public static Boolean isReachable(List<Point> traj, ABObject target, List<ABObject> candidates) {
        
        List<ABObject> blocks = new ArrayList(candidates);
        Collections.sort(blocks, new XComparator());
        
        //System.out.println("Target: " + target.globalID + "(" + target.x  + "," + target.y + ")");
        
        Point current = traj.get(0);
        int last_x = target.x;
        
        Boolean reachable = true;
        
        while (current.x < last_x && reachable) {
            
            for (ABObject block : blocks) {
                // System.out.println("Looking at " + String.format(pred_2, block.globalID, block.x, block.y));

                if (block.x + block.width < current.x) {
                    continue;
                } else if (block.x > current.x) {
                    break;
                } else {

                    if (block.getType() == ABType.Hill) {
                        Poly poly = (Poly) block;
                        if (poly.polygon.contains(current)) { 
                            reachable = false;
                            break;
                        }
                    } else {
                        if (block.contains(current)) { 
                            reachable = false;
                            break;
                        }
                    }
                }
            }
            
            if (!reachable) {
                break;
            }
            try {
                traj = traj.subList(3, traj.size() -1);
                current = traj.get(0);
            } catch (IndexOutOfBoundsException e) {
                System.out.println("[KR]Out of trajectory!");
                break;
            }

        }
        
        return reachable;
    }
        
    
    private ArrayList<String> defaultPredicates(ArrayList<String> predicates) {
        
        String[] defaults3 = {"collapsesInDirection" };
        
        String[] defaults2 = {"protects", "belongsTo", "isAnchorPointFor", "canCollapse",
                            "isOn", "isBelow", "isLeft", "isRight", "supports", "hasSize", 
                            "hasForm", "hasMaterial", "canExplode", "hasOrientation", "isOver"};
        
        String[] defaults1 = {"isCollapsable", "isHittable", "hill", "object"};
        
        List<List<String>> all = new ArrayList<>();
        
        all.add(Arrays.asList(defaults1));
        all.add(Arrays.asList(defaults2));
        all.add(Arrays.asList(defaults3));
        
        int pred_i = 0;
        
        for (List<String> defaults : all) {
            pred_i += 1;
            System.out.println("[KR]Now checking defaults/" + pred_i);
        
            for (String def : defaults) {
                boolean missing = true;

                for (String pred : predicates) {
                    if (pred.contains(def)) {
                        missing = false;
                    }
                }

                if (missing) {
                    switch (pred_i) {
                        case 1: predicates.add(String.format(pred_1, def, "dummyObject"));
                                System.out.println("[KR]Written Default Predicate: " + def);
                                break;
                        
                        case 2: predicates.add(String.format(pred_2, def, "dummyObject", "dummmyObject"));
                                System.out.println("[KR]Written Default Predicate: " + def);
                                break;
                               
                        case 3: predicates.add(String.format(pred_3, def, "dummyObject", "dummyObject", "dummyObject"));
                                System.out.println("[KR]Written Default Predicate: " + def);
                                break;
                                
                }
                    
                } 
            }
        }
        
        
        
        return predicates;
    }
    
    /*
    * TODO:
    * - hittable/reachable improved
    * - onGround has a temporary workaround (every object that has no "isOn" relation)
    * - size - seperate method for pigs, and more resilient
    * - balls as important objects: only big circles? more than just "above" ?
    */
    
    
    @Override
	public void shutdown() {
		// TODO Auto-generated method stub

	}

}