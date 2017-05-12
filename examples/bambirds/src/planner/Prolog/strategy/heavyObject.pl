

ptestHeavyObjectStHO() :-
	doInferencialGroundingSeIG(),
	savePlansForHeavyObjectStrategyStHO().
	
%%savePlansForHeavyObjectStrategyStHO/0
% savePlansForHeavyObjectStrategyStHO()
savePlansForHeavyObjectStrategyStHO() :-
	pfindHevyObjectStructuresStHO(StructList),
	(
		(StructList == []) ->
			psysoStHO('No','HeavyObject in Map');
			psavePlansForAllStructsWithHeavyObjectsStHO(StructList)
	).
	
% pfindHeavyObjectStructuresStHO(-StructList)
pfindHevyObjectStructuresStHO(StructList) :-
	findall(
		Struct,
		(
			structure(Struct),
			containsRelevant(Struct,Relevant),
			not(pig(Relevant)),
			not(hasMaterial(Relevant,tnt))
		),
		TmpStructList
	),
	sort(TmpStructList,StructList).

% psavePlansForAllStructsWithHeavyObjectsStHO(+StructList)
psavePlansForAllStructsWithHeavyObjectsStHO([]) :- !.
psavePlansForAllStructsWithHeavyObjectsStHO([Struct|Tail]) :-
	pfindAllRelevantsInStructureStHO(Struct,RelevantsInStruct),
	pfindProtectedRelevantsStHO(Struct,ProtectedRelevantsList),
	(
		(RelevantsInStruct == []) ->
			(
				psysoStHO('No Relevants in Structure',Struct)
			);
			(
				psavePlansForContainingPigsStHO(Struct,RelevantsInStruct)
			)
	),
	(
		(ProtectedRelevantsList == []) ->
			(
				psysoStHO('No Relevants protected by',Struct)
			);
			(
				psavePlansForProtectedPigsStHO(Struct,ProtectedRelevantsList)
			)
	),
	psavePlansForAllStructsWithHeavyObjectsStHO(Tail).

pfindProtectedRelevantsStHO(Struct,ProtectedList) :-
	findall(
		Relevant,
		(
			protects(Struct,Relevant),
			belongsTo(Relevant,OtherStruct),
			canCollapse(Struct,OtherStruct)
		),
		UnsortedProtected
	),
	sort(UnsortedProtected,ProtectedList).
	
pfindAllRelevantsInStructureStHO(Struct,RelevantList) :-
	findall(
		Relevant,
		(
			containsRelevant(Struct,Relevant),
			(
				pig(Relevant);
				hasMaterial(Relevant,tnt)
			)
		),
		UnsortedRelevantList
	),
	sort(UnsortedRelevantList,RelevantList).

% pfindHeavyObjectsInStructureStHO(+Structure,-HeavyObjectList)
% returns a List with heavy objects in given Structure
pfindHeavyObjectsInStructureStHO(Struct,HeavyObjects) :-
	findall(
		HeavyObject,
		(
			containsRelevant(Struct,HeavyObject),
			not(pig(HeavyObject)),
			not(hasMaterial(HeavyObject,tnt))
		),
		TmpObjectList
	),
	sort(TmpObjectList,HeavyObjects).


% psavePlansForContainingPigsStHO(+Struct,+PigList)
psavePlansForContainingPigsStHO(_Struct,[]) :- !.
psavePlansForContainingPigsStHO(Struct,[Pig|Tail]) :-
	pfindHeavyObjectsInStructureStHO(Struct,HeavyObjects),
	psavePlansForHeavyObjectToContainingPigStHO(HeavyObjects,Pig),
	psavePlansForContainingPigsStHO(Struct,Tail).

psavePlansForProtectedPigsStHO(_Struct,[]) :- !.
psavePlansForProtectedPigsStHO(Struct,[Pig|Tail]) :-
	pfindHeavyObjectsInStructureStHO(Struct,HeavyObjects),
	psavePlansForHeavyObjectToProtectedPigStHO(HeavyObjects,Pig),
	psavePlansForProtectedPigsStHO(Struct,Tail).

psavePlansForHeavyObjectToContainingPigStHO([],_Pig) :- !.
psavePlansForHeavyObjectToContainingPigStHO([HeavyObject|Tail],Pig) :-
	pfindObjectsBelowHeavyObjectStHO(HeavyObject,BelowObjects),
	(
		(BelowObjects == []) ->
			(
				psysoStHO('Object is on Ground',HeavyObject)
			);
			(
				psavePlansForBelowObjectsToContainingPigStHO(BelowObjects,Pig)
			)
	),
	psavePlansForHeavyObjectToContainingPigStHO(Tail,Pig).

psavePlansForHeavyObjectToProtectedPigStHO([],_Pig) :- !.
psavePlansForHeavyObjectToProtectedPigStHO([HeavyObject|Tail],Pig) :-
	pfindObjectsBelowHeavyObjectStHO(HeavyObject,BelowObjects),
	(% if
		(BelowObjects == []) ->
		(% then
			psysoStHO('Object is on Ground',HeavyObject)
		);
		(% else
			psavePlansForBelowObjectsToProtectedPigStHO(BelowObjects,Pig)
		)
	),
	psavePlansForHeavyObjectToProtectedPigStHO(Tail,Pig).

pfindObjectsBelowHeavyObjectStHO(HeavyObject,BelowObjects) :-
	findall(
		BelowObject,
		(
			isOn(HeavyObject,BelowObject),
			not(BelowObject == ground),
			not(hill(BelowObject))
		),
		UnsortedBelowObjects
	),
	sort(UnsortedBelowObjects,BelowObjects).

psavePlansForBelowObjectsToProtectedPigStHO([],_Pig) :- !.
psavePlansForBelowObjectsToProtectedPigStHO([BelowObject|Tail],Pig) :-
	(
		(% if
			isHittable(BelowObject,true)
		) ->
		(% then
			psavePlanForHittableBelowObjectProtectedStHO(BelowObject,Pig)
		);
		(% else
			psavePlanForNotHittableBelowObjectProtectedStHO(BelowObject,Pig)
		)
	),
	psavePlansForBelowObjectsToProtectedPigStHO(Tail,Pig).

psavePlansForBelowObjectsToContainingPigStHO([],_Pig) :- !.
psavePlansForBelowObjectsToContainingPigStHO([BelowObject|Tail],Pig) :-
	(
		(% if
			isHittable(BelowObject,true)
		) ->
		(% then
			psaveEasyPlanForHittableStHO(BelowObject,Pig),
			psavePlanForHittableBelowObjectContainingStHO(BelowObject,Pig)
		);
		(% else
			psaveEasyPlanForNotHittableStHO(BelowObject,Pig),
			psavePlanForNotHittableBelowObjectContainingStHO(BelowObject,Pig)
		)
	),
	psavePlansForBelowObjectsToContainingPigStHO(Tail,Pig).

psavePlanForHittableBelowObjectContainingStHO(BelowObject,Pig) :-
	(%if
		(
			isOver(BelowObject,Pig);
			isOn(BelowObject,Pig)
		) ->
		(% then
			psavePlanStHO([BelowObject],[Pig])
		);
		(% else
			findPathsFromToForActiveBirdWithStandardLimitSeWf([BelowObject],[Pig],PathList),
			(% if
				(PathList == []) ->
				(% then
					psysoStHO('Something went wrong with','Dijkstra')
				);
				(% else
					nth0(0,PathList,RawPath),
					splitListIn(RawPath,_Value,Path),
					psavePlanStHO([BelowObject],Path)
				)
			)
		)
	).
	
	
psavePlanForNotHittableBelowObjectContainingStHO(BelowObject,Pig) :-
	findPathsFromToHittablesForActiveBirdWithStandardLimitSeWf([BelowObject],PathList),
	(
		(
			PathList == []
		) ->
		(
			psysoStHO('No Way To Reach',BelowObject)
		);
		(
			nth0(0,PathList,RawPath),
			splitListIn(RawPath,_Value,Path),
			splitListIn(Path,TargetList,TmpGoalList),
			(% if
				(isOver(BelowObject,Pig);isOn(BelowObject,Pig)) ->
				(% then
					append(TmpGoalList,[Pig],GoalList),
					psavePlanStHO(TargetList,GoalList)
				);
				(% else
					findPathsFromToForActiveBirdWithStandardLimitSeWf([BelowObject],[Pig],NextPathList),
					(% if
						(
							NextPathList == []
						) ->
						(% then
							psysoStHO('Something went wrong in','Dijkstra')
						);
						(% else
							nth0(0,NextPathList,NextRawPath),
							splitListIn(NextRawPath,_NextValue,NextPath),
							append(TmpGoalList,NextPath,WorstCaseGoal),
							psavePlanStHO(TargetList,WorstCaseGoal)
						)
					)
				)
			)
		)
	).
	
psavePlanForHittableBelowObjectProtectedStHO(BelowObject,Pig) :-
	(
		(% if
			isOver(BelowObject,Pig)
		) ->
		(% then
			psavePlan([BelowObject],[Pig])
		);
		(% else
			(
				(% if
					isOver(ProtectingObject,Pig),
					belongsTo(BelowObject,StructOne),
					belongsTo(ProtectingObject,StructTwo),
					StructOne == StructTwo
				)->
				(% then
					findPathsFromToForActiveBirdWithStandardLimitSeWf([BelowObject],[ProtectingObject],PathList),
					(
						(% if
							PathList == []
						) ->
						(% then
							psysoStHO('Something went wrong with','Dijkstra')
						);
						(% else
							nth0(0,PathList,RawPath),
							splitListIn(RawPath,_Value,Path),
							psavePlanStHO([BelowObject],Path)
						)
					)
				);
				(% else
					psysoStHO('Something went wrong with','Modeling the map')
				)
					
			)
		)
	).

psavePlanForNotHittableBelowObjectProtectedStHO(BelowObject,Pig) :-
	findPathsFromToHittablesForActiveBirdWithStandardLimitSeWf([BelowObject],PathList),
	(
		(
			PathList == []
		) ->
		(
			psysoStHO('No Way to reach',BelowObject)
		);
		(
			nth0(0,PathList,RawPath),
			splitListIn(RawPath,_Value,Path),
			splitListIn(Path,TargetList,TmpGoalList),
			(
				(% if
					isOver(BelowObject,Pig)
				) ->
				(% then
					append(TmpGoalList,[Pig],GoalList),
					psavePlanStHO(TargetList,GoalList)
				);
				(% else
					(
						(% if
							isOver(ProtectingObject,Pig),
							belongsTo(BelowObject,StructOne),
							belongsTo(ProtectingObject,StructTwo),
							StructOne == StructTwo
						) ->
						(% then
							findPathsFromToForActiveBirdWithStandardLimitSeWf([BelowObject],[ProtectingObject],NextPathList),
							(
								(% if
									NextPathList == []
								) ->
								(% then
									psysoStHO('Something went wrong in','Dijkstra')
								);
								(% else
									nth0(0,NextPathList,NextRawPath),
									splitListIn(NextRawPath,_NextValue,NextPath),
									append(TmpGoalList,NextPath,WorstCaseGoal),
									psavePlanStHO(TargetList,WorstCaseGoal)
								)
							)
						);
						(% else
							psysoStHO('Something went wrong in','Modeling the map')
						)
					)
					
				)
			)
		)
	).

psaveEasyPlanForHittableStHO(Target,Goal) :-
	psaveEasyPlanStHO([Target],[Goal]).
psaveEasyPlanForNotHittableStHO(BelowObject,Goal) :-
	findPathsFromToHittablesForActiveBirdWithStandardLimitSeWf([BelowObject],PathList),
	(
		(% if
			PathList == []
		) ->
		(% then
			psysoStHO('No Way To Reach',BelowObject)
		);
		(
			nth0(0,PathList,RawPath),
			splitListIn(RawPath,_Value,Path),
			nth0(0,Path,Target),
			append([Goal],Path,GoalList),
			psaveEasyPlanStHO([Target],GoalList)
		)
	).



psaveEasyPlanStHO(TargetList,GoalList) :-
	pgetRankValuesStHO(TargetList,GoalList,ValueOne,ValueTwo,ValueThree,ValueFour),
	getPlanRankSeDH(ValueOne,ValueTwo,ValueThree,ValueFour,heavyObject,Rank),
	pfindDuplicatePlansStHO(TargetList,Rank,Duplicates),
	(
		(% if
			Duplicates > 0
		) ->
		(% then
			psysoStHO('Plan','Already safed')
		);
		(% else
			savePlan(TargetList,GoalList,heavyObject,Rank)
		)
	).
psavePlanStHO(TargetList,GoalList) :-
	pgetRankValuesStHO(TargetList,GoalList,ValueOne,ValueTwo,ValueThree,ValueFour),
	getPlanRankSeDH(ValueOne,ValueTwo,ValueThree,ValueFour,heavyObject,TmpRank),
	Rank is (TmpRank * 2),
	pfindDuplicatePlansStHO(TargetList,Rank,Duplicates),
	(
		(% if
			Duplicates > 0
		) ->
		(% then
			psysoStHO('Plan','Already safed')
		);
		(% else
			savePlan(TargetList,GoalList,heavyObject,Rank)
		)
	).

pfindDuplicatePlansStHO(TargetList,Rank,Duplicates) :-
	findall(
		Goals,
		(plan(Rank,TargetList,Goals,heavyObject)),
		TmpPlanList
	),
	sort(TmpPlanList,PlanList),
	length(PlanList,Duplicates).


% pgetRankValuesStHO(+TargetList,+GoalList,-ValueOne,-ValueTwo,-ValueThree,-ValueFour)
pgetRankValuesStHO(TargetList,GoalList,ValueOne,ValueTwo,ValueThree,ValueFour) :-
	append(TargetList,GoalList,TmpObjectList),
	sort(TmpObjectList,ObjectList),
	pgetPigCountStHO(ObjectList,PigValue),
	length(ObjectList,InvolvedObjectValue),
	pgetPercentageOfInvolvedStructuresStHO(ObjectList,RelativeStructValue),
	pgetTotalObjectsStHO(ObjectList,TotalObjectValue),
	ValueOne 	= PigValue,
	ValueTwo 	= InvolvedObjectValue,
	ValueThree	= RelativeStructValue,
	ValueFour	= TotalObjectValue.

pgetTotalObjectsStHO(ObjectList,ObjectValue) :-
	pfindAllObjectsInStructuresStHO(ObjectList,TmpTotalObjectList),
	sort(TmpTotalObjectList,TotalObjectList),
	length(TotalObjectList,ObjectValue).
	
pfindAllObjectsInStructuresStHO([],[]) :- !.
pfindAllObjectsInStructuresStHO([Object|Tail],TotalObjectList) :-
	belongsTo(Object,Struct),
	findall(
		Objects,
		(
			belongsTo(Objects,Struct),
			object(Objects)
		),
		TmpTotalObjectList
	),
	pfindAllObjectsInStructuresStHO(Tail,NextObjectList),
	append(TmpTotalObjectList,NextObjectList,TotalObjectList).

%
pgetPercentageOfInvolvedStructuresStHO(ObjectList,StructValue) :-
	pfindInvolvedStructuresStHO(ObjectList,TmpStructList),
	sort(TmpStructList,InvolvedStructList),
	pgetAllStructsInMapStHO(MapStructList),
	length(InvolvedStructList,InvolvedStructValue),
	length(MapStructList,MapStructValue),
	StructValue is ((InvolvedStructValue * 100) / MapStructValue).

pfindInvolvedStructuresStHO([],[]) :- !.
pfindInvolvedStructuresStHO([Object|Tail],StructList) :-
	findall(
		Struct,
		belongsTo(Object,Struct),
		TmpStructList
	),
	pfindInvolvedStructuresStHO(Tail,NextStructList),
	append(TmpStructList,NextStructList,StructList).

pgetAllStructsInMapStHO(MapStructList) :-
	findall(
		Struct,
		structure(Struct),
		MapStructList
	).
	
% pgetPigCountStHO(+InputList,-PigValue)
pgetPigCountStHO(ObjectList,PigValue) :-
	pfindPigsInInvolvedStructuresStHO(ObjectList,TmpPigList),
	sort(TmpPigList,PigList),
	length(PigList,PigValue).
	
pfindPigsInInvolvedStructuresStHO([],[]) :- !.
pfindPigsInInvolvedStructuresStHO([Object|Tail],PigList) :-
	belongsTo(Object,Struct),
	pfindRelevantPigsForStructureStHO(Struct,TmpPigList),
	pfindPigsInInvolvedStructuresStHO(Tail,NextPigList),
	append(TmpPigList,NextPigList,PigList).

pfindRelevantPigsForStructureStHO(Struct,PigList) :-
	findall(
		Pig,
		(
			(
				belongsTo(Pig,Struct),
				pig(Pig)
			);
			(
				belongsTo(Pig,Struct),
				hasMaterial(Pig,tnt)
			);
			(
				protects(Pig,Struct)
			)
		),
		PigList
	).

%
%% psavePlansForEachHeavyObject(+HeavyObjects,+PigList)
%psavePlansForEachHeavyObjectStHO([],_RelevantList) :- !.
%psavePlansForEachHeavyObjectStHO([HeavyObject|Tail],RelevantList) :-
%	
%	pfindWayFromHeavyTorRelevantStHO(HeavyObject,RelevantList,Way),
%	pfindTargetsBelowHeavyObject
%	psavePlansForHeavyObjectToRelevantsStHO(HeavyObject,RelevantList),
%	psavePlansForEachHeavyObjectStHO(Tail,RelevantList).
%psavePlansForEachHeavyObjectStHO(Tail,RelevantList).
%
%pfindTargetsBelowHeavyObject(HeavyObject,Targets) :-
%	findall(
%		BelowObject,
%		(
%			isOn(HeavyObject,BelowObject),
%			isHittable(BelowObject)
%		),
%		BelowList
%	),
%	sort(BelowList,Targets).	
%	
%pfindWayFromHeavyTorRelevantStHO(HavyObject,Relevants,Way,WayFound) :-
%	
%% psavePlansForEachHeavyObjectStHO(+HeavyObject,RelevantList)
%psavePlansForHeavyObjectToRelevantsStHO(_HeavyObject,[]) :- !.
%psavePlansForHeavyObjectToRelevantsStHO(HeavyObject,[Relevant|Tail]) :-
%	pfindWayFromHeavyToRelevant(.
%
%
%
%pfindWayFromPigToHeavyObjectStHO().


psysoStHO(_Tag,_TextOne,_TextTwo) :-
%	(
%		(Tag == d) ->
%			string_concat('[HeavyObject][Debug] ',TextOne,Text);
%			string_concat('[HeavyObject][Sonstiges] ',TextOne,Text) 
%	),
%	pAlexOutput(Text,TextTwo),
	true.
psysoStHO(_TextOne,_TextTwo) :-
%	string_concat('[HeavyObject] ',TextOne,Text),
%	pAlexOutput(Text,TextTwo),
	true.
	
	
% pfad zum hittable
% findPathsFromToHittablesForActiveBirdWithStandardLimitSeWf


% pfad vom tragenden zum Schwein
% findPathsFromToForActiveBirdWithStandardLimitSeWf	
