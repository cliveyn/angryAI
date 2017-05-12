%% destroyStructureStDS/0
% destroyStructureStDS()
% 

ptestDestroyStructureLow() :-
	doInferencialGroundingSeIG(),
	saveDestroyStructurePlansLowStDS().
%saveDestroyStructurePlansStDS() :-
saveDestroyStructurePlansLowStDS() :-
	pfindAllRelevantStructuresStDS(StructList),
	(
		(StructList == []) ->
			psysoStDS('No relevant structures in map','Something went totaly wrong');
			psavePlansForEachStructureStDS(StructList)
	).
	
% pfindAllRelevantStructuresStDS(-StructList)
pfindAllRelevantStructuresStDS(StructList) :-
	findall(
		Struct,
		(
			(
				structure(Struct),
				containsRelevant(Struct,_RelevantObject)
			);
			(
				structure(Struct),
				protects(Struct,_Relevant)
			)
		),
		TmpStructList
	),
	sort(TmpStructList,StructList).
	
% psavePlansForEachStructureStDS(+StructList)
psavePlansForEachStructureStDS([]) :- !.
psavePlansForEachStructureStDS([Struct|Tail]) :-
	pfindAllBearingObjectsInStructStDS(Struct,BearingObjects),
	(
		(BearingObjects == []) ->
			syso('No Hittable Objects in',Struct),
			psavePlansForEachStructureStDS(Tail);
			psavePlansForStructureStDS(BearingObjects),
			psavePlansForEachStructureStDS(Tail)
	).
	

% pfindAllBearingObjectsInStructStDS(+Struct,-BearingObjectList)
pfindAllBearingObjectsInStructStDS(Struct,BearingObjects) :-
	findall(
		BearingObject,
		(
			object(Object),
			belongsTo(Object,Struct),
			isHittable(Object,true),
			hasIntegrity(Object,Integrity),
%			not(pig(Object)),
			BearingObject = [Integrity,Object]
		),
		UnsortedBearingObjects
	),
	kwikeSortInverse(UnsortedBearingObjects,BearingObjects).

% psavePlansForStructureStDS(+ListOfBearingObjects)
psavePlansForStructureStDS([]) :- !.
psavePlansForStructureStDS([BearingObject|Tail]) :-
	nth0(0,BearingObject,IntegrityValue),
	nth0(1,BearingObject,Object),
	getEfficiencyValueSeDH(Object,EffValue),
	(
		(
			(
				IntegrityValue >= 1;
				pig(Object)
			),
			EffValue =< 1
		) ->
		(
			psavePlansForObjectStDS(Object)
		);
		(
			psavePlansForStructureStDS(Tail)
		)
	).
	

% psavePlanForObjectStDS(+Object)
psavePlansForObjectStDS(Object) :-
	findall(
		Goal,
		(
			(isOn(Goal,Object));
			(isBelow(Goal,Object),
			not(Object == ground));
			(belongsTo(Object,Struct),
			containsRelevant(Struct,Goal))
		),
		GoalList
	),
	Target = [Object],
	psavePlanStDS(Target,GoalList).
	
% psavePlanStDS(+Target,+Goal)
psavePlanStDS(Target,Goal) :-
	pgetRankForTargetStDS(Target,Goal,Rank),
	savePlan(Target,Goal,destroyPrimitive,Rank).




% pgetRankForTargetStDS(+TargetList,+GoalList,-Rank)
pgetRankForTargetStDS(Target,Goal,Rank) :-
	append(Target,Goal,Objects),
	pfindAllIncludedStructuresStDS(Objects,UnsortedStructList),
	sort(UnsortedStructList,Structures),
	length(Structures,StructCount),
	pfindAllContainingPigsStDS(Structures,UnsortedPigList),
	sort(UnsortedPigList,PigList),
	length(PigList,PigCount),
	pfindAllContainingObjectsStDS(Structures,UnsortedObjectList),
	sort(UnsortedObjectList,ObjectList),
	length(ObjectList,ObjectCount),
	nth0(0,Target,ThisTarget),
	hasIntegrity(ThisTarget,IntegrityValue),
	getPlanRankSeDH(PigCount,IntegrityValue,ObjectCount,StructCount,destroyPrimitive,Rank).

% pfindAllContainingObjectsStDS(+StructList,-Objects)
pfindAllContainingObjectsStDS([],[]) :- !.
pfindAllContainingObjectsStDS([Struct|Tail],ObjectList) :-
	findall(
		Object,
		(
			object(Object),
			belongsTo(Object,Struct)
		),
		TmpObjectList
	),
	pfindAllContainingObjectsStDS(Tail,NextObjectList),
	append(TmpObjectList,NextObjectList,ObjectList).

% pfindAllIncludedStructuresStDS(+Objects,-Structures)
pfindAllIncludedStructuresStDS([],[]) :- !.
pfindAllIncludedStructuresStDS([Object|Tail],Structures) :-
	findall(
		Struct,
		belongsTo(Object,Struct),
		TmpStructList
	),
	pfindAllIncludedStructuresStDS(Tail,NextStructList),
	append(TmpStructList,NextStructList,Structures).
% pfindAllContainingPigsStDS(+StructList,-Pigs)
pfindAllContainingPigsStDS([],[]) :- !.
pfindAllContainingPigsStDS([Struct|Tail],PigList) :-
	findall(
		Pig,
		(
			belongsTo(Pig,Struct),
			pig(Pig)
		),
		TmpPigList
	),
	pfindAllContainingPigsStDS(Tail,NextPigList),
	append(TmpPigList,NextPigList,PigList).

psysoStDS(Tag,TextOne,TextTwo) :-
	(
		(Tag == d) ->
			string_concat('[DestroyPrimitive][Debug] ',TextOne,Text);
			string_concat('[DestroyPrimitive][Sonstiges] ',TextOne,Text) 
	),
	syso(Text,TextTwo),
	true.
psysoStDS(_TextOne,_TextTwo) :-
%	string_concat('[DestroyPrimitive] ',TextOne,Text),
%	pAlexOutput(Text,TextTwo),
	true.
	
		