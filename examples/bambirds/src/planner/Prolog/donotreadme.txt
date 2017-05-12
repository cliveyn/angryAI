Consult functions
consult('/Users/Alex/Documents/Uni/Semester_4/AI_Birds/Angry_Birds/project/src/BamBird/src/planner/Prolog/functions.pl').

Level 2
'/Users/Alex/Documents/Uni/Semester_4/AI_Birds/Angry_Birds/project/src/BamBird/src/planner/Prolog/level_2.pl'.

Level 1
'/Users/Alex/Documents/Uni/Semester_4/AI_Birds/Angry_Birds/project/src/BamBird/src/planner/Prolog/level_1.pl'.


pig2    -> none none	   small  wood8
stone10 -> bar  vertical   medium hill2 <- Problem
wood8   -> bar  horizontal medium stone10


java -jar Bambird.jar 127.0.0.1 424242 "C:\Program Files\swipl\bin\swipl.exe"


object(Important),
(
	pig(Important);
	hasMaterial(Important, tnt)
),
isOver(Important, Roof),
(
	belongsTo(Important, Structure), belongsTo(Roof, Structure), ObjectAndRoof = [Important, Roof];
	protects(Structure, Important), belongsTo(Roof, Structure), ObjectAndRoof = [Important, Roof] 
)