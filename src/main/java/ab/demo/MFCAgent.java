/*****************************************************************************
 ** ANGRYBIRDS AI AGENT FRAMEWORK
 ** Copyright (c) 2014, XiaoYu (Gary) Ge, Stephen Gould, Jochen Renz
 **  Sahan Abeyasinghe,Jim Keys,  Andrew Wang, Peng Zhang
 ** All rights reserved.
**This work is licensed under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
**To view a copy of this license, visit http://www.gnu.org/licenses/
 *****************************************************************************/
package src.ab.demo;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;

import javax.imageio.ImageIO;

import src.ab.demo.other.ActionRobot;
import src.ab.demo.other.Shot;
import ab.planner.TrajectoryPlanner;
import ab.utils.StateUtil;
import ab.vision.ABObject;
import ab.vision.VisionUtils;
import ab.vision.GameStateExtractor.GameState;
import ab.vision.Vision;

public class MFCAgent implements Runnable {

	private ActionRobot aRobot;
	private Random randomGenerator;
	public int currentLevel = 1;
	public static int time_limit = 12;
	private Map<Integer,Integer> scores = new LinkedHashMap<Integer,Integer>();
	TrajectoryPlanner tp;
	private boolean firstShot;
	private Point prevTarget;
	public static HashMap<Double, HashMap<String, Integer>> ADic_Ag;
	public static HashMap<String,int[][]> strToarrayStateDic;
	public int numTimestamp = 0;
	private int prevScore;
	public boolean useMFC = true; 
	
	// a standalone implementation of the Naive Agent
	public MFCAgent() {
		
		aRobot = new ActionRobot();
		tp = new TrajectoryPlanner();
		prevTarget = null;
		firstShot = true;
		randomGenerator = new Random();
 		
 		ADic_Ag = new HashMap<Double, HashMap<String, Integer>>();
 		strToarrayStateDic = new HashMap<String,int[][]>();
		for(double x = 0.0; x<80.0 ; x = x + 0.1){
			double action = Math.round(x*10.0)/10.0;
			ADic_Ag.put(action, new HashMap<String,Integer>()); 
		}
 		
		 try
		  //기존의 Adic 읽어들이기
	      {
	         FileInputStream fis = new FileInputStream("AdicMap_Ep1_2200.data");
	         ObjectInputStream ois = new ObjectInputStream(fis);
	         ADic_Ag = (HashMap<Double, HashMap<String, Integer>>) ois.readObject();
	         ois.close();
	         fis.close();
	         
	         FileInputStream fis2 = new FileInputStream("str2arrayDic_Ep1_2200.data");
	         ObjectInputStream ois2 = new ObjectInputStream(fis2);
	         strToarrayStateDic = (HashMap<String,int[][]>) ois2.readObject();
	         ois2.close();
	         fis2.close();
	      }catch(IOException ioe)
	      {
	         ioe.printStackTrace();
	      }catch(ClassNotFoundException c)
	      {
	         System.out.println("File not found");
	         c.printStackTrace();
	      }
		
		ActionRobot.GoFromMainMenuToLevelSelection();
	}

	
	// run the client
	public void run() {
		
		aRobot.loadLevel(currentLevel);
		
		
		while (true) {
			
			GameState state = solve();
			
			if (state == GameState.WON) {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				int score = StateUtil.getScore(ActionRobot.proxy);
				
				if(!scores.containsKey(currentLevel))
					scores.put(currentLevel, score);
				else
				{
					if(scores.get(currentLevel) < score)
						scores.put(currentLevel, score);
				}
				
				int totalScore = 0;
				for(Integer key: scores.keySet()){

					totalScore += scores.get(key);
					System.out.println(" Level " + key
							+ " Score: " + scores.get(key) + " ");
				}
				System.out.println("Total Score: " + totalScore);
				
				if(currentLevel==21) currentLevel = 0; //Episode 1에 대해서만 계속 반복함!
				aRobot.loadLevel(++currentLevel);
				
				// make a new trajectory planner whenever a new level is entered
				tp = new TrajectoryPlanner();

				// first shot on this level, try high shot first
				firstShot = true;
				prevScore  = 0;
				
			} else if (state == GameState.LOST) {
				System.out.println("Lost...");
				//System.out.println("Restart");
				prevScore  = 0;
				//aRobot.restartLevel();
			} else if (state == GameState.LEVEL_SELECTION) {
				System.out
				.println("Unexpected level selection page, go to the last current level : "
						+ currentLevel);
				aRobot.loadLevel(currentLevel);
			} else if (state == GameState.MAIN_MENU) {
				System.out
				.println("Unexpected main menu page, go to the last current level : "
						+ currentLevel);
				ActionRobot.GoFromMainMenuToLevelSelection();
				aRobot.loadLevel(currentLevel);
			} else if (state == GameState.EPISODE_MENU) {
				System.out
				.println("Unexpected episode menu page, go to the last current level : "
						+ currentLevel);
				ActionRobot.GoFromMainMenuToLevelSelection();
				aRobot.loadLevel(currentLevel);
			}

		}

	}

	private double distance(Point p1, Point p2) {
		return Math
				.sqrt((double) ((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y)
						* (p1.y - p2.y)));
	}

	public GameState solve()
	{
		// capture Image
		BufferedImage screenshot = ActionRobot.doScreenShot();
		// process image
		Vision vision = new Vision(screenshot);
		// find the slingshot
		Rectangle sling = vision.findSlingshotMBR();
		// confirm the slingshot
		while (sling == null && aRobot.getState() == GameState.PLAYING) {
			System.out
			.println("No slingshot detected. Please remove pop up or zoom out");
			ActionRobot.fullyZoomOut();
			screenshot = ActionRobot.doScreenShot();
			vision = new Vision(screenshot);
			sling = vision.findSlingshotMBR();
		}
        // get all the pigs
 		List<ABObject> pigs = vision.findPigsMBR();
		GameState state = aRobot.getState();
		Point _tpt = null;
		
		// if there is a sling, then play, otherwise just skip.
		if (sling != null) {
			if (!pigs.isEmpty()) {
				prevTarget = null;
				Point releasePoint = null;
				Shot shot = new Shot();
				int dx,dy;
				
				int[][] MFCstate = vision.getMBRVision().findMFCState();
				System.out.println("Current state : "+ MFCstate.toString());
				
				String MFCstateStr = "";
				for(int i = 0 ; i < MFCstate.length;i++){
					for(int j = 0; j < MFCstate[i].length; j++){
						MFCstateStr += MFCstate[i][j];
					}
				}
				
				if(!strToarrayStateDic.containsKey(MFCstateStr)){
					BufferedImage stateImg = vision.getMBRVision().getStateImg();
					File outputfile = new File("stateImages/Lev"+currentLevel+"_"+MFCstate.toString()+".jpg");
					try {
						ImageIO.write(stateImg, "jpg", outputfile);
					} catch (IOException e) {
						e.printStackTrace();
					}
					strToarrayStateDic.put(MFCstateStr, MFCstate);
				}
				
				useMFC = true;
				double ag = -1;
				ag = getAction(MFCstateStr);		// 주석처리 될 경우 explore만 하는 것.		
				System.out.println("Best chosen angle : "+ ag);
				
				
				//if (randomGenerator.nextInt(2)==0 || ag <0){
				if (ag <0){
					useMFC = false;
					// random pick up a pig
					ABObject pig = pigs.get(randomGenerator.nextInt(pigs.size()));
					_tpt = pig.getCenter();// if the target is very close to before, randomly choose a
					System.out.println("e-Greedy, randomly Pick up a pig dest : " + _tpt.toString());
					
					// point near it
					if (prevTarget != null && distance(prevTarget, _tpt) < 10) {
						double _angle = randomGenerator.nextDouble() * Math.PI * 2;
						_tpt.x = _tpt.x + (int) (Math.cos(_angle) * 10);
						_tpt.y = _tpt.y + (int) (Math.sin(_angle) * 10);
						System.out.println("Randomly changing to " + _tpt);
					}
				}
				
				if (_tpt == null){
					releasePoint = tp.findReleasePoint(sling, Math.toRadians(ag));
				}else{	
					prevTarget = new Point(_tpt.x, _tpt.y);
					// estimate the trajectory
					ArrayList<Point> pts = tp.estimateLaunchPoint(sling, _tpt);

					// do a high shot when entering a level to find an accurate velocity
					if (firstShot && pts.size() > 1) releasePoint = pts.get(1);
					else if (pts.size() == 1) releasePoint = pts.get(0);
					else if (pts.size() == 2){
						// randomly choose between the trajectories, with a 1 in 6 chance of choosing the high one
						if (randomGenerator.nextInt(6) ==0) releasePoint = pts.get(1);
						else releasePoint = pts.get(0);
					}
					else if(pts.isEmpty()){
							System.out.println("No release point found for the target");
							System.out.println("Try a shot with 45 degree");
								releasePoint = tp.findReleasePoint(sling, Math.PI/4);
					}				
				}
					
				// Get the reference point
				Point refPoint = tp.getReferencePoint(sling);
				
				//Calculate the tapping time according the bird type 
				if (releasePoint != null) {
					double releaseAngle = tp.getReleaseAngle(sling, releasePoint);
					System.out.println("Release Angle: "+ Math.toDegrees(releaseAngle));
					
					int tapInterval = 0;
					switch (aRobot.getBirdTypeOnSling()){
						case RedBird:
							tapInterval = 0; break;               // start of trajectory
						case YellowBird:
							tapInterval = 65 + randomGenerator.nextInt(25);break; // 65-90% of the way
						case WhiteBird:
							tapInterval =  70 + randomGenerator.nextInt(20);break; // 70-90% of the way
						case BlackBird:
							tapInterval =  90 + randomGenerator.nextInt(9);
							System.out.println("Black bird tapInterval : "+tapInterval+"%");
							break; // 90-99% of the way
						case BlueBird:
							tapInterval =  65 + randomGenerator.nextInt(20);break; // 65-85% of the way
						default:
							tapInterval =  60;
					}
					
					if(_tpt == null){
						ABObject pig = pigs.get(randomGenerator.nextInt(pigs.size()));
						_tpt = pig.getCenter();// if the target is very close to before, randomly choose a
					}
					int tapTime = tp.getTapTime(sling, releasePoint, _tpt, tapInterval);
					dx = (int)releasePoint.getX() - refPoint.x;
					dy = (int)releasePoint.getY() - refPoint.y;
					shot = new Shot(refPoint.x, refPoint.y, dx, dy, 0, tapTime);
				}
				else
					{
						System.err.println("No Release Point Found");
						return state;
					}

				// check whether the slingshot is changed. the change of the slingshot indicates a change in the scale.
				{
					ActionRobot.fullyZoomOut();
					screenshot = ActionRobot.doScreenShot();
					vision = new Vision(screenshot);
					Rectangle _sling = vision.findSlingshotMBR();
					if(_sling != null)
					{
						double scale_diff = Math.pow((sling.width - _sling.width),2) +  Math.pow((sling.height - _sling.height),2);
						if(scale_diff < 25)
						{
							if(dx < 0)
							{
								aRobot.cshoot(shot);
								state = aRobot.getState();
								System.out.println("Previous Score: "+prevScore);
								int currentScore = StateUtil.getCurrentScore(ActionRobot.proxy);
								
								if(currentScore >=prevScore){
									int currentReward = currentScore - prevScore;
									prevScore = prevScore + currentReward;
									
									double thisAction = Math.toDegrees(tp.getReleaseAngle(sling, releasePoint));
									if(useMFC) thisAction = ag;
									else thisAction = Math.round(thisAction*10.0)/10.0;
									
									System.out.print("For state " + MFCstate.toString() + ", action "+thisAction+ ", ");
									if(ADic_Ag.get(thisAction).containsKey(MFCstateStr)){
										int prevReward = ADic_Ag.get(thisAction).get(MFCstateStr);
										if(prevReward < currentReward || currentReward ==0)
											ADic_Ag.get(thisAction).put(MFCstateStr, currentReward);
											System.out.println("reward is updated from " + prevReward + "to : "+currentReward);
									}else{
										ADic_Ag.get(thisAction).put(MFCstateStr, currentReward);
										System.out.println("new reward saved as : "+currentReward);
									}
								}
								
								//Print Action size
								for(double i = 0; i<10;i++){
									System.out.print(i/10.0 +": ");
									for(double j = 0;j<80; j++){
										double action = j+i*0.1;
										action = Math.round(action*10.0)/10.0;
										HashMap<String, Integer> SRDic = ADic_Ag.get(action);
										System.out.printf("%1d", SRDic.size());
									}
									System.out.println();
								}
								
								System.out.println("Timestamp : "+numTimestamp);
								numTimestamp++;
								if(numTimestamp % 50==0){
									try{
						                  FileOutputStream fos =
						                     new FileOutputStream("AdicMap_Ep1_"+numTimestamp+".data");
						                  ObjectOutputStream oos = new ObjectOutputStream(fos);
						                  oos.writeObject(ADic_Ag);
						                  oos.close();
						                  fos.close();
						                  System.out.printf("Serialized Action Dic is saved in AdicMap_ts.data");
							           }catch(IOException ioe){
							                  ioe.printStackTrace();
							           }
									
									try{
						                  FileOutputStream fos =
						                     new FileOutputStream("str2arrayDic_Ep1_"+numTimestamp+".data");
						                  ObjectOutputStream oos = new ObjectOutputStream(fos);
						                  oos.writeObject(strToarrayStateDic);
						                  oos.close();
						                  fos.close();
						                  System.out.printf("Serialized str to array state dic is saved in str2arrayDic_ts.data");
							           }catch(IOException ioe){
							                  ioe.printStackTrace();
							           }
								}
								
								
								if ( state == GameState.PLAYING )
								{
									screenshot = ActionRobot.doScreenShot();
									vision = new Vision(screenshot);
									List<Point> traj = vision.findTrajPoints();
									tp.adjustTrajectory(traj, sling, releasePoint);
									firstShot = false;
								}
							}
						}
						else
							System.out.println("Scale is changed, can not execute the shot, will re-segement the image");
					}
					else
						System.out.println("no sling detected, can not execute the shot, will re-segement the image");
				}

			}

		}
		System.out.println("---------------------");
		return state;
	}
	
	public static double getStateDist(int[][] state1, int[][] state2){
		double sum = 0;
		for(int i = 0 ; i < state1.length;i++){
			for(int j = 0; j < state1[i].length; j++){
				sum += Math.pow((state1[i][j]-state2[i][j]),2);
			}
		}
		if(sum ==0){
			return 0.0;
		}else{
			double dist = Math.sqrt(sum);
			return dist;
		}
	}
	
	public static double getAction(String state){
		int n = 5;
		int maxReward = -1;
		double bestAction = -1;
		int[][] arrayState1 = strToarrayStateDic.get(state); 
		int[][] arrayState2;
		
		for (Double action : ADic_Ag.keySet()){
			HashMap<String, Integer> SRDic = ADic_Ag.get(action);
			int expectedReward = Integer.MIN_VALUE;
			
			if(SRDic.containsKey(state)){
				expectedReward = SRDic.get(state);
			}else{			
				if(SRDic.keySet().size() >=n){
					HashMap<String, Double> SDistDic = new HashMap<String, Double>();
						
					for (String actionState : SRDic.keySet()){
						arrayState2 = strToarrayStateDic.get(actionState);
						
						double dist = getStateDist(arrayState1, arrayState2);
						//System.out.println("Calculated distance: " + dist);
						SDistDic.put(actionState,dist);
					}
					
					List<String> topNstates = getTopNStates(SDistDic, n);
					 
					//System.out.println();
					int rewardSum =0;
					for(String actionState : topNstates){
						//System.out.println("Distance: "+SDistDic.get(actionState));
						rewardSum += SRDic.get(actionState);
					}
					expectedReward = rewardSum/n;
				}
			}
			if (expectedReward > maxReward){
				maxReward = expectedReward;
				System.out.println("New best angle is " + action.toString() + "(E(reward): "+maxReward+")");
				bestAction = action;
			}
		}
		
		if(maxReward>0)
			return bestAction;
		else{
			System.out.println("No previous action available");
			return -1;
		}
			
	}
	
	public static List<String> getTopNStates(final HashMap<String, Double> map, int n) {
	    PriorityQueue<String> topN = new PriorityQueue<String>(n, new Comparator<String>() {
	        public int compare(String s1, String s2) {
	            return Double.compare(map.get(s2), map.get(s1));
	        }
	    });

	    for(String key:map.keySet()){
	    	//System.out.println(map.get(key));
	        if (topN.size() < n)
	            topN.add(key);
	        else if (map.get(topN.peek()) > map.get(key)) {
	            topN.poll();
	            topN.add(key);
	        }
	    }
	    
	    List<String> topNlist = new ArrayList<String>();
	    for (String state: topN) {
	    	topNlist.add(state);
	    }
	    
	    return topNlist;
	}
	
	

	public static void main(String args[]) {

		MFCAgent na = new MFCAgent();
		if (args.length > 0)
			na.currentLevel = Integer.parseInt(args[0]);
		na.run();

	}
}
