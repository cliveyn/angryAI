
%%%%
% FUNCTIONS FOR DEPOT STRATEGY
%%%%

ptestDepotStrategyStDe() :-
	doInferencialGroundingSeIG(),
	savePlansForDepotStrategyStDe().

%%savePlansForDepotStrategyStDe
% savePlansForDepotStrategyStDe()
%  searches Depos in Maps and generates Plans for them
savePlansForDepotStrategyStDe() :-
	pfindDepotsInMapStDe(DepotList),
	(
		(DepotList==[]) ->
			psysoStDe('No','Depots');
			psavePlansForDepotsStDe(DepotList)
	).
%	retractall(depotPlanCounter(_X)).
	
% pfindDepotsInMapStDe(-DepotList)
% retruns a List with all structures that are depots
pfindDepotsInMapStDe(DepotList) :-
	findall(
		Struct,
		(
			structure(Struct),
			containsRelevant(Struct,RelevantObject),
			not(hasMaterial(RelevantObject,tnt)),
			not(pig(RelevantObject))
		),
		StructList
	),
	sort(StructList,DepotList).
	
% psavePlansForDepotsStDe(+DepotList)
% saves plans for all depots
psavePlansForDepotsStDe([]) :- !.
psavePlansForDepotsStDe(InputList) :-
	splitListIn(InputList,Depot,Tail),
	pfindCollapsableStructuresStDe(Depot,CollapseList),
	length(CollapseList,NumberOfCollapsables),
	(NumberOfCollapsables > 0 ->
		psavePlansToCollapseOtherStructuresStDe(Depot,CollapseList);
		pfindNextStructureStDe(Depot,NextStructs),
		(
			(NextStructs == []) ->
				psysoStDe('No','Structs to collapse');
				psavePlansForNextStructuresStDe(Depot,NextStructs)
		)
	),
	psavePlansForDepotsStDe(Tail),!.

% pfindNextStructureStDe(+Depot,-NextStruct)
% returns the Next structure from depot with direction
pfindNextStructureStDe(Depot,NextStructs) :-
	findall(
		StructDirect,
		(
			collapsesInDirection(Depot,StructDirectTmp,DirectionDirect),
			StructDirect = [StructDirectTmp,DirectionDirect]
		),
		StructDirectList
	),
	findall(
		StructIndirect,
		(
			collapsesInDirection(StructIndirectTmp,Depot,DirectionIndirectTmp),
			(
				(DirectionIndirectTmp == towards) ->
					DirectionIndirect = away;
					DirectionIndirect = towards
			),
			StructIndirect = [StructIndirectTmp,DirectionIndirect]
		),
		StructIndirectList
	),
	(
		(StructDirectList == [], StructIndirect == []) ->
			NextStructs = [];
			append(StructDirectList,StructIndirectList,NextStructs)
	).

% psavePlansForNextStructures(+Depot,+StructList)
% saves plans for all maybe reachable structures
psavePlansForNextStructuresStDe(_Depot,[]) :- !.
psavePlansForNextStructuresStDe(Depot,[Struct|Tail]) :-
	nth0(0,Struct,Structure),
	nth0(1,Struct,Direction),
	findall(
		Relevant,
		(
			containsRelevant(Structure,Relevant)
%			not(protects(Depot,Relevant))
		),
		RelevantObjects
	),
%	psysoStDe(Structure,RelevantObjects),
	(
		(RelevantObjects == []) ->
			psysoStDe('No relevant objects in Structure',Structure);
			pfindHittableObjectInDepotStDe(Depot,Structure,Direction,HittableObjects),
			length(HittableObjects,HittableValue),
			(
				(
					HittableValue > 3
				) ->
				(
					pgetBestThreeHittablesStDe(HittableObjects,BestHittableObjects),
					psavePlansToCollapseOtherStructuresWithDepotStDe(BestHittableObjects,RelevantObjects)
				);
				(
					psavePlansToCollapseOtherStructuresWithDepotStDe(HittableObjects,RelevantObjects)
				)
			)
	),
	psavePlansForNextStructuresStDe(Depot,Tail).

pgetBestThreeHittablesStDe(HittableObjects,BestHittables) :-
	nth0(0,HittableObjects,Best),
	nth0(1,HittableObjects,Sec),
	nth0(2,HittableObjects,Thi),
	append([],[Best],TmpOne),
	append(TmpOne,[Sec],TmpTwo),
	append(TmpTwo,[Thi],BestHittables).

% psavePlansToCollapseOtherStructuresStDe(+CollapseList)
% save plans for structures that can be collapsed by depot
psavePlansToCollapseOtherStructuresStDe(_Depot,[]) :- !.
psavePlansToCollapseOtherStructuresStDe(Depot,[Collapsable|Tail]) :-
	findall(
		Relevant,
		(
			containsRelevant(Collapsable,Relevant)
%			not(protects(Depot,Relevant))
		),
		RelevantObjects
	),
	length(RelevantObjects,NumberOfRelevantObjects),
%	psysoStDe(Collapsa,RelevantObjects),
	(
		(NumberOfRelevantObjects == 0) ->
			psysoStDe('No relevant objects in collapsable Structure',Collapsable);
			pfindHittableObjectInDepotStDe(Depot,Collapsable,HittableObjects),
			length(HittableObjects,HittableValue),
			(
				(
					HittableValue > 3
				) ->
				(
					pgetBestThreeHittablesStDe(HittableObjects,BestHittables),
					psavePlansToCollapseOtherStructuresWithDepotStDe(BestHittables,RelevantObjects)
				);
				(
					psavePlansToCollapseOtherStructuresWithDepotStDe(HittableObjects,RelevantObjects)
				)
			)
	),
	psavePlansToCollapseOtherStructuresStDe(Depot,Tail).

% pfindHittableObjectInDepotStDe(+Depot,+CollapsableStructure,-HittableObjectList)
% returns the object to collapse the depot in direction of the Collapsable Structure
pfindHittableObjectInDepotStDe(Depot,Collapsable,HittableObjects) :-
	collapsesInDirection(Depot,Collapsable,Direction),
	findTargetsToCollapseStructureSeCS(Depot,Direction,HittableObjects).
%	findHittablesForCollapseSortedByMinHeightSeQS(Depot,Direction,HittableObjects).

% pfindHittableObjectInDepotStDe(+Depot,+Structure,+Direction,-HittableObjectList)
pfindHittableObjectInDepotStDe(Depot,_Structure,Direction,HittableObjectList) :-
	findTargetsToCollapseStructureSeCS(Depot,Direction,HittableObjectList).
%	findHittablesForCollapseSortedByMinHeightSeQS(Depot,Direction,HittableObjectList),

% pfindCollapsableStructuresStDe(+Depot,-ListOfCollapsableStructures)
% find all Structures that can be collapsed by the depot
pfindCollapsableStructuresStDe(Depot,CollapseList) :-
	findall(
		Collapsable,
		canCollapse(Depot,Collapsable),
		TmpList
	),
	sort(TmpList,CollapseList).

% psavePlansToCollapseOtherStructuresWithDepotStDe(+HittableObjectsOfDepot,+RelevantObjectsOfTarget)
% save Plans for each hittable object of depot
psavePlansToCollapseOtherStructuresWithDepotStDe([],_RelevantObjectsOfTarget) :- !.
psavePlansToCollapseOtherStructuresWithDepotStDe([InputObject|Tail],RelevantObjectsOfTarget) :-
	nth0(1,InputObject,HittableObject),
	HittableObjectList = [HittableObject],
	append(HittableObjectList,RelevantObjectsOfTarget,Goals),
	Targets = [HittableObject],
	psavePlanForDepotStDe(Targets,Goals),
	psavePlansToCollapseOtherStructuresWithDepotStDe(Tail,RelevantObjectsOfTarget).
	
psavePlanForDepotStDe(Targets,Goals) :-
%	nth0(0,Targets,Target),
%	nth0(0,Goals,Goal),
	pgetValuesForDepotStDe(Targets,Goals,TmpTPigs,TmpTStruct,TmpTObjects,TmpTDepotSize),
	psysoStDe(d,test,test),
	getPlanRankSeDH(TmpTPigs,TmpTStruct,TmpTDepotSize,TmpTObjects,depot,Rank),
	findall(
		Plan,
		(
			plan(CurrentRank,Target,Goal,depot),
			(
				Target == Targets,
				Goal == Goals
			);
			(
				Target == Targets,
				CurrentRank == Rank
			),
			Plan = [Target,Goal]
		),
		ExistingPlanList
	),
	(
		(ExistingPlanList == []) ->
			savePlan(Targets,Goals,depot,Rank);
			psysoStDe('Plan not saved','Already exists')
	).


% calculate Rank

% pgetValuesForDepotStDe(+TargetList,-Pigs,-Structures,-Objects,-Count)
pgetValuesForDepotStDe(Target,Goal,Pigs,Struct,Objects,DepotSize) :-
	append(Target,Goal,UnsortedObjectList),
	sort(UnsortedObjectList,ObjectList),
	psysoStDe(d,list,ObjectList),
	pgetPigAndTNTValueStDe(ObjectList,Pigs),
	psysoStDe(d,pigs,Pigs),
	pgetAllInvolvedStructuresStDe(Target,TargetList),
	psysoStDe(d,target,TargetList),
	pgetAllInvolvedStructuresStDe(Goal,GoalList),
	psysoStDe(d,goal,GoalList),
	append(TargetList,GoalList,UnsortedStructList),
	psysoStDe(d,unsorted,UnsortedStructList),
	sort(UnsortedStructList,StructList),
	psysoStDe(d,sorted,StructList),
	length(StructList,Struct),
	Objects is 1,
	pgetDepotSizeStDe(Target,DepotSize).

%	psysoStDe(d,'structList',StructList),
%	pfindAllObjectsInTargetsAndGoalsStDe(StructList,UnsortedObjectList),
%	psysoStDe(d,'unsortedobjectList',UnsortedObjectList),
%	sort(UnsortedObjectList,ObjectList),
%	psysoStDe(d,'objectList',ObjectList),
%	length(ObjectList,Objects),
%	psysoStDe(d,'objects',Objects),

	
	

pgetPigAndTNTValueStDe([],0) :- !.
pgetPigAndTNTValueStDe([Object|Tail],Pigs) :-
	psysoStDe(d,Object,Tail),
	(
		(
			pig(Object)
		) ->
		(
			Value is 2
		);
		(
			(
				(
					hasMaterial(Object,tnt)
				) ->
				(
					Value is 4
				);
				(
					Value is 0
				)
			)
		)
	),
	pgetPigAndTNTValueStDe(Tail,NextValue),
	Pigs is (Value + NextValue).

% pgetAllInvolvedStructuresStDe(+TargetList,-StructList)
pgetAllInvolvedStructuresStDe([],[]) :- !.
pgetAllInvolvedStructuresStDe([Target|Tail],StructList) :-
	belongsTo(Target,StructTmp),
	Struct = [StructTmp],
	pgetAllInvolvedStructuresStDe(Tail,OtherStructs),
	append(Struct,OtherStructs,StructList).
	
% pgetDepotSizeStDe(+Target,-DepotSize)
pgetDepotSizeStDe(Target,DepotSize) :-
	findall(
		Object,
		(
			belongsTo(Target,Depot),
			object(Object),
			belongsTo(Object,Depot)
		),
		ObjectList
	),
	sort(ObjectList,OrderedList),
	length(OrderedList,DepotSize).
	
% pfindAllObjectsInTargetsAndGoals(+StructList,-Objects)
pfindAllObjectsInTargetsAndGoalsStDe([],[]) :- !.
pfindAllObjectsInTargetsAndGoalsStDe([Struct|Tail],Objects) :-
	psysoStDe(d,Struct,Tail),
	findall(
		Object,
		(
			belongsTo(Object,Struct),
			object(Object)
		),
		ObjectList
	),
	psysoStDe(d,objectList,ObjectList),
	psysoStDe(d,tail,Tail),
	pfindAllObjectsInTargetsAndGoalsStDe(Tail,FurtherObjects),
	psysoStDe(d,further,FurtherObjects),
	append(ObjectList,FurtherObjects,Objects),
	psysoStDe(d,objects,Objects).
	
% pfindAllPigsInTargetsAndGoalsStDe(+StructList,-Pigs)
pfindAllPigsInTargetsAndGoalsStDe([],[]) :- !.
pfindAllPigsInTargetsAndGoalsStDe([Struct|Tail],Pigs) :-
	findall(
		Pig,
		(
			containsRelevant(Struct,Pig),
			pig(Pig)
		),
		PigList
	),
	pfindAllPigsInTargetsAndGoalsStDe(Tail,FurtherPigs),
	append(FurtherPigs,PigList,Pigs).
		
% pgetAllReachableStructuresStDe(+Target,+Goal,-StructList)
pgetAllReachableStructuresStDe(Target,Goal,StructList) :-
	findall(
		TStructs,
		belongsTo(Target,TStructs),
		TStructList
	),
	nth0(0,TStructList,TStruct),
	findall(
		Struct,
		(
			belongsTo(Goal,Struct);
			belongsTo(Goal,GStruct),
			collapsesInDirection(TStruct,GStruct,Direction),
			collapsesInDirection(TStruct,Struct,Direction)
		),
		GStructList
	),
	append(TStructList,GStructList,TmpStructList),
	sort(TmpStructList,StructList).
	

% pgetPigsInTtargetStDe(+Target,-Pigs)
pgetPigsInTargetStDe(Target,Pigs) :-
	belongsTo(Target,Struct),
	findall(
		Pig,
		(
			containsRelevant(Struct,Pig),
			pig(Pig)
		),
		PigList
	),
	sort(PigList,OrderedList),
	length(OrderedList,Pigs).

psysoStDe(_Tag,_TextOne,_TextTwo) :-
%	(
%		(Tag == d) ->
%			string_concat('[Depot][Debug] ',TextOne,Text);
%			string_concat('[Depot][Sonstiges] ',TextOne,Text) 
%	),
%	pAlexOutput(Text,TextTwo),
	true.
psysoStDe(TextOne,TextTwo) :-
	string_concat('[Depot] ',TextOne,Text),
	pAlexOutput(Text,TextTwo),
	true.
