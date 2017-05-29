/*****************************************************************************
 ** ANGRYBIRDS AI AGENT FRAMEWORK
 ** Copyright (c) 2014, XiaoYu (Gary) Ge, Stephen Gould, Jochen Renz
 **  Sahan Abeyasinghe,Jim Keys,  Andrew Wang, Peng Zhang
 ** All rights reserved.
**This work is licensed under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
**To view a copy of this license, visit http://www.gnu.org/licenses/
 *****************************************************************************/
package ab.demo;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
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

import ab.demo.other.ActionRobot;
import ab.demo.other.Shot;
import ab.planner.TrajectoryPlanner;
import ab.utils.StateUtil;
import ab.vision.ABObject;
import ab.vision.VisionUtils;
import ab.vision.GameStateExtractor.GameState;
import ab.vision.Vision;

public class MFECAgent implements Runnable {

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
	
	public double gamma = 1;
	public ArrayList<Integer> rewardHist = new ArrayList<Integer>();
	public ArrayList<String> stateHist = new ArrayList<String>();
	public ArrayList<Double> actionHist = new ArrayList<Double>();
	
	public ArrayList<Integer> totalRewardHist = new ArrayList<Integer>();
	
	// a standalone implementation of the Naive Agent
	public MFECAgent() {
		
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

		//기존의  Adic 읽어들이기
		 try
	      {
	         FileInputStream fis = new FileInputStream("AdicMap_300.data");
	         ObjectInputStream ois = new ObjectInputStream(fis);
	         ADic_Ag = (HashMap<Double, HashMap<String, Integer>>) ois.readObject();
	         ois.close();
	         fis.close();
	         
	         FileInputStream fis2 = new FileInputStream("str2arrayDic_300.data");
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

	public void learn(){
		System.out.println("####### End of Level. Let's learn! #######");
		/* ############## Start Learning ##############*/
		for(int t = rewardHist.size(); t>0;t--){
			double action = actionHist.get(t-1);
			String statestr = stateHist.get(t-1);
			int cummReward = 0;
			for(int i = t; i>0;i--) cummReward += rewardHist.get(i-1)*Math.pow(gamma, t-i);
			
			System.out.print("At timestep "+t+", for state: " + strToarrayStateDic.get(statestr).toString() + ", action: "+action+ ", ");
			if(ADic_Ag.get(action).containsKey(statestr)){
				int prevReward = ADic_Ag.get(action).get(statestr);
				if(prevReward < cummReward)
					System.out.println("reward is updated from " + prevReward + " to : "+cummReward);
					ADic_Ag.get(action).put(statestr, cummReward);
			}else{
				System.out.println("new reward is saved as : "+cummReward);
				ADic_Ag.get(action).put(statestr, cummReward);
			}
		}
		/*############## End Learning ############## */
		
		rewardHist.clear(); // reward history 초기화
		prevScore  = 0; // previous score 초기화
		stateHist.clear(); // state history 초기화
		actionHist.clear(); // action history 초기화
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
				this.learn();
				
				int score = StateUtil.getScore(ActionRobot.proxy);
				if(!scores.containsKey(currentLevel)) scores.put(currentLevel, score);
				else if(scores.get(currentLevel) < score) scores.put(currentLevel, score);
				int totalScore = 0;
				for(Integer key: scores.keySet()){
					totalScore += scores.get(key);
					System.out.println(" Level " + key + " Score: " + scores.get(key) + " ");
				}
				System.out.println("Total Score: " + totalScore);
				
				if(currentLevel==21) currentLevel = 0; // map1애 대해서만
				
				aRobot.loadLevel(++currentLevel);
				
				// make a new trajectory planner whenever a new level is entered
				tp = new TrajectoryPlanner();

				// first shot on this level, try high shot first
				firstShot = true;
			} else if (state == GameState.LOST) {
				this.learn();
				
				/*//Write total reward history as a txt file
				int totalReward = 0;
				for(Integer key: scores.keySet()) totalReward += scores.get(key);
				totalRewardHist.add(totalReward);
				try {
					PrintWriter writer = new PrintWriter("totalRewardHistory.txt", "UTF-8");
					for(int reward:totalRewardHist) writer.println(reward);
				    writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}*/
				
				//print size of states
				int sizeADic = 0;
				for(double i = 0; i<10;i++){
					for(double j = 0;j<80; j++){
						double action = j+i*0.1;
						action = Math.round(action*10.0)/10.0;
						HashMap<String, Integer> SRDic = ADic_Ag.get(action);
						sizeADic += SRDic.size();
					}
				}
				System.out.println("Number of states collected: " + sizeADic);
				
				System.out.println("####### Restart this level #######");
				aRobot.restartLevel();
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
				ag = getAction(MFCstateStr);	
				System.out.println("Best chosen angle : "+ ag);
				
				
				if (randomGenerator.nextInt(2)==0 || ag <0){
				//if (ag <0){
					useMFC = false;
					
					//greedy 목표물로 block들도 추가.
					List<ABObject> blocks = vision.findBlocksMBR();
					pigs.addAll(blocks);
					
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
						if (randomGenerator.nextInt(6) ==0) releasePoint = pts.get(1);
						else releasePoint = pts.get(0);
					}
					else if(pts.isEmpty()){
							System.out.println("No release point found for the target");
							System.out.println("Try a shot with 45 degree");
								releasePoint = tp.findReleasePoint(sling, Math.PI/4);
					}
					
					//Do not make a minus angle shot
					if(Math.toDegrees(tp.getReleaseAngle(sling, releasePoint))<0) 
						releasePoint = tp.findReleasePoint(sling, Math.PI/4);
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
							tapInterval = 0; break; 
						case YellowBird:
							tapInterval = 75; break;
						case WhiteBird:
							tapInterval =  85; break;
						case BlackBird:
							tapInterval =  95; break;
						case BlueBird:
							tapInterval =  75; break;
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
								int currentScore = StateUtil.getCurrentScore(ActionRobot.proxy);
								if(currentScore ==0) currentScore = prevScore;
								System.out.println("Previous Score: "+prevScore+", Current Score: "+currentScore);
								state = aRobot.getState();
								
								if(currentScore >=prevScore){
									/* State */
									stateHist.add(MFCstateStr);
									
									/* Reward */
									int currentReward = currentScore - prevScore;
									prevScore = prevScore + currentReward;
									System.out.println("Current Reward: "+currentReward);
									rewardHist.add(currentReward);
									
									/* Action */
									double thisAction = Math.toDegrees(tp.getReleaseAngle(sling, releasePoint));
									if(useMFC) thisAction = ag;
									else thisAction = Math.round(thisAction*10.0)/10.0;
									actionHist.add(thisAction);

								}
								
								/*//Print Action size
								for(double i = 0; i<10;i++){
									System.out.print(i/10.0 +": ");
									for(double j = 0;j<80; j++){
										double action = j+i*0.1;
										action = Math.round(action*10.0)/10.0;
										HashMap<String, Integer> SRDic = ADic_Ag.get(action);
										System.out.printf("%1d", SRDic.size());
									}
									System.out.println();
								}*/
								
								System.out.println("Timestamp : "+numTimestamp);
								numTimestamp++;
								
								if(numTimestamp % 100 ==0){
									try{
						                  //FileOutputStream fos = new FileOutputStream("AdicMap_"+numTimestamp+".data");
						                  FileOutputStream fos = new FileOutputStream("AdicMap_"+(numTimestamp+300)+".data");
						                  ObjectOutputStream oos = new ObjectOutputStream(fos);
						                  oos.writeObject(ADic_Ag);
						                  oos.close();
						                  fos.close();
						                  System.out.printf("Serialized Action Dic is saved in AdicMap_ts.data");
							           }catch(IOException ioe){
							                  ioe.printStackTrace();
							           }
									
									try{
										  //FileOutputStream fos = new FileOutputStream("str2arrayDic_"+numTimestamp+".data");  
										  FileOutputStream fos = new FileOutputStream("str2arrayDic_"+(numTimestamp+300)+".data");
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

		MFECAgent na = new MFECAgent();
		if (args.length > 0)
			na.currentLevel = Integer.parseInt(args[0]);
		na.run();

	}
}
