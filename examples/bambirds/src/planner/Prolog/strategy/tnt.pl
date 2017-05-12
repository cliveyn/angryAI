
ptestTNTStrategy() :-
	doInferencialGroundingSeIG(),
	savePlansForTntStrategyStTNT().

%%savePlansForTntStrategyStTNT/0
% savePlansForTntStrategyStTNT()
% save plans for TNT strategy if TNT is available
savePlansForTntStrategyStTNT() :-
	findall(
		TNT,
		(
			object(TNT),
			hasMaterial(TNT,tnt)
		),
		TNTList
	),
	(TNTList == [] ->
		psysoStTNT('No TNT in Map','');
		psaveAllPlansForTNTStTNT()
	).

% psavePlansForTNT()
% calls all available plan functions 
psaveAllPlansForTNTStTNT() :-
	psavePlansForDirectHittableTntStTNT(),
	psavePlansForTNTInStructureStTNTsStTNT().

% psavePlansForTNTInStructureStTNTsStTNT()
% saves all plans for TNT witch is built in a structure	
psavePlansForTNTInStructureStTNTsStTNT() :-
	findall(
		StructWithTNT,
		(
			structure(Struct),
			object(TNT),
			hasMaterial(TNT,tnt),
			belongsTo(TNT,Struct),
			StructWithTNT = [Struct,TNT]
%			containsRelevant(Struct,Object),
%			TNT \== Object
		),
		StructList
	),
	psavePlansForTNTInStructureStTNTsStTNT(StructList).
% psavePlansForTNTInStructureStTNTsStTNT(+List)
psavePlansForTNTInStructureStTNTsStTNT([]) :- !.
psavePlansForTNTInStructureStTNTsStTNT([Head|Tail]) :-
	nth0(0,Head,Struct),
	nth0(1,Head,TNT),
	psysoStTNT('Struct',Struct),
	psysoStTNT('TNT',TNT),
	findall(
		Hittable,
		(
			object(Hittable),
			belongsTo(Hittable,Struct),
			isHittable(Hittable,true)
		),
		Hittables
	),
	findall(
		Collapsable,
		canCollapse(Collapsable,Struct),
		Collapsables
	),
	length(Hittables,NumberOfHittables),
	length(Collapsables,NumberOfCollapsables),
	((not(isHittable(TNT,true))) ->
		(NumberOfHittables == 0 ->
			(NumberOfCollapsables == 0 ->
				psysoStTNT('undestructable TNT','');
				psavePlansToCollapseStructureWithTNTWithOtherStructureStTNT(Collapsables,Struct)
			);
			psysoStTNT('TODO','Find Shortest Path to TNT'),
			psavePlansForTNTInStructureStTNT(TNT)
		);
		psysoStTNT('Is saved by','savePlansForDirectHittableTntStTNT')
	),
	psavePlansForTNTInStructureStTNTsStTNT(Tail).
	
% psavePlansForTNTInStructureStTNT(+TNT,+ListOfHittableObjects)
psavePlansForTNTInStructureStTNT(TNT) :-
	psysoStTNT('As far as good',TNT),
	findPathsFromToHittablesForActiveBirdWithStandardLimitSeWf([TNT],Way),
	(
		(Way==[]) ->
			psysoStTNT('No Way to hit TNT',TNT);
			psavePlansToReachTNTStTNT(TNT,Way)
	).

psavePlansToReachTNTStTNT(_TNT,[]) :- !.
psavePlansToReachTNTStTNT(TNT,[Way|Tail]) :-
	nth0(0,Way,Distance),
	DistanceValue = ((1-Distance)*10),
	FinalValue is ceiling(DistanceValue),
	nth0(1,Way,Target),
	psavePlansForNotHittableTNTStTNT(Target,TNT,FinalValue),
	psavePlansToReachTNTStTNT(TNT,Tail).

% psavePlansToCollapseStructureWithTNTWithOtherStructureStTNT(+ListOfStructures,+Structure)
psavePlansToCollapseStructureWithTNTWithOtherStructureStTNT([],_Struct) :- !.
psavePlansToCollapseStructureWithTNTWithOtherStructureStTNT([Head|Tail],Struct) :-
	collapsesInDirection(Head,Struct,Direction),
	(Direction == towards ->
		findHittablesForCollapseTowardsSeQS(Head,TargetList);
		findHittablesForCollapseAwaySeQS(Head,TargetList)
	),
	psavePlansToCollapseStructuresStTNT(TargetList,Struct),
	psavePlansToCollapseStructureWithTNTWithOtherStructureStTNT(Tail,Struct).

% psavePlansToCollapseStructuresStTNT(+ListOfTargets,+Structure)
psavePlansToCollapseStructuresStTNT([],_Struct) :- !.
psavePlansToCollapseStructuresStTNT([Head|Tail],Struct) :-
	findall(
		TNT,
		(
			containsRelevant(Struct,TNT),
			hasMaterial(TNT,tnt)
		),
		TNTList
	),
	psavePlansForTNTStTNT([Head],TNTList),
	psavePlansToCollapseStructuresStTNT(Tail,Struct),
	psysoStTNT('TEST',TNTList).

% psavePlansForDirectHittableTntStTNT()
% searches direct hittable tnt boxes and save plans to shoot on them
psavePlansForDirectHittableTntStTNT() :-
	findall(
		TNT,
		(
			object(TNT),
			hasMaterial(TNT,tnt),
			isHittable(TNT,true)
		),
		TNTList
	),
	(TNTList == [] ->
		psysoStTNT('No direct hittable tnt','');
		psavePlansForDirectHittableTntStTNT(TNTList)
	).
	
% psavePlansForDirectHittableTntStTNT(+TNTList)
% asserts the plans for direct hittable tnts
psavePlansForDirectHittableTntStTNT([]) :- !.
psavePlansForDirectHittableTntStTNT([Head|Tail]) :-
	psavePlansForTNTStTNT([Head],[Head]),
	psavePlansForDirectHittableTntStTNT(Tail).

% psavePlansForTNTStTNT(+ListOfTargets,+ListOfGoal,+RankInt)
% saves a plan with the right format
psavePlansForTNTStTNT(Targets,Goals) :-
	pgetRankForTNTStTNT(Targets,Goals,Rank),
	savePlan(Targets,Goals,tnt,Rank).
	
% psavePlansForNotHittableTNTStTNT(+Target,+Goal,+DistanceValue)
psavePlansForNotHittableTNTStTNT(Target,TNT,DistanceValue) :-
	Targets = [Target],
	Goals = [TNT],
	psysoStTNT(d,target,Target),
	pgetRankForTNTWithDistanceStTNT(Targets,Goals,DistanceValue,Rank),
	psysoStTNT(d,target,Rank),
	savePlan(Targets,Goals,tnt,Rank).



% CALCULATE TNT RANK

pgetRankForTNTWithDistanceStTNT(Targets,Goals,Distance,Rank) :-
	append(Targets,Goals,ObjectList),
	pfindAllStructuresStTNT(ObjectList,UnorderdStructList),
	sort(UnorderdStructList,StructList),
	length(StructList,StructCount),
	pfindAllTntInStructuresStTNT(StructList,UnsortedTNTList),
	sort(UnsortedTNTList,TNTList),
	length(TNTList,TmpTNTCount),
	TNTCount = (TmpTNTCount * 2),
	pfindAllPigsInStructuresStTNT(StructList,UnsortedPigList),
	sort(UnsortedPigList,PigList),
	length(PigList,PigCount),
	getPlanRankSeDH(TNTCount,PigCount,StructCount,Distance,tnt,Rank).

pgetRankForTNTStTNT(Targets,Goals,Rank) :-
	append(Targets,Goals,ObjectList),
	pfindAllStructuresStTNT(ObjectList,UnorderdStructList),
	sort(UnorderdStructList,StructList),
	pfindAllTntInStructuresStTNT(StructList,UnsortedTNTList),
	sort(UnsortedTNTList,TNTList),
	length(TNTList,TmpTNTCount),
	TNTCount = (TmpTNTCount * 2),
	pfindAllPigsInStructuresStTNT(StructList,UnsortedPigList),
	sort(UnsortedPigList,PigList),
	length(PigList,PigCount),
	pfindAllObjectsInStructuresStTNT(StructList,UnsortedObjectList),
	sort(UnsortedObjectList,AllObjectsList),
	length(AllObjectsList,ObjectCount),
	length(StructList,StructCount),
	getPlanRankSeDH(TNTCount,PigCount,StructCount,ObjectCount,tnt,Rank).
	
% pfindAllObjectsInStructuresStTNT(+StructList,-ObjectList)
pfindAllObjectsInStructuresStTNT([],[]) :- !.
pfindAllObjectsInStructuresStTNT([Struct|Tail],ObjectList) :-
	findall(
		Object,
		(
			belongsTo(Object,Struct),
			object(Object)
		),
		TmpObjectList
	),
	pfindAllObjectsInStructuresStTNT(Tail,NextObjectList),
	append(TmpObjectList,NextObjectList,ObjectList).
	
% pfindAllPigsInStructuresStTNT(+StructList,-PigList)
pfindAllPigsInStructuresStTNT([],[]) :- !.
pfindAllPigsInStructuresStTNT([Struct|Tail],PigList) :-
	findall(
		Pig,
		(
			belongsTo(Pig,Struct),
			pig(Pig)
		),
		TmpPigList
	),
	pfindAllPigsInStructuresStTNT(Tail,NextPigList),
	append(TmpPigList,NextPigList,PigList).

% pfindAllTntInStructuresStTNT(+StructList,-TNTList)
pfindAllTntInStructuresStTNT([],[]) :- !.
pfindAllTntInStructuresStTNT([Struct|Tail],TNTList) :-
	findall(
		TNT,
		(
			belongsTo(TNT,Struct),
			hasMaterial(TNT,tnt)
		),
		TmpTNTList
	),
	pfindAllTntInStructuresStTNT(Tail,NextTNTList),
	append(TmpTNTList,NextTNTList,TNTList).
% pfindAllStructuresStTNT(+ObjectList,-StructureList)
% gets a List with Objects and returns all associated structures
pfindAllStructuresStTNT([],[]) :- !.
pfindAllStructuresStTNT([Object|Tail],StructList) :-
	findall(
		Structure,
		belongsTo(Object,Structure),
		TmpStructList
	),
	pfindAllStructuresStTNT(Tail,NextStructList),
	append(TmpStructList,NextStructList,StructList).

psysoStTNT(Tag,TextOne,TextTwo) :-
	(
		(Tag == d) ->
			string_concat('[Depot][Debug] ',TextOne,Text);
			string_concat('[Depot][Sonstiges] ',TextOne,Text) 
	),
	pAlexOutput(Text,TextTwo),
	true.
psysoStTNT(Print,Value) :-
	string_concat('[TNT] ',Print,NewPrint),
	pAlexOutput(NewPrint,Value),
	true.
