
ptestDeepPenetrationTDP() :-
	doInferencialGroundingStIG(),
	savePlansForDeepPenetrationTDP().
	
savePlansForDeepPenetrationTDP() :-
	findall(
		Struct,
		structure(Struct),
		StructList
	),
	psavePlansForDeepPenetrationTDP(StructList).
	
psavePlansForDeepPenetrationTDP([]) :- !.
psavePlansForDeepPenetrationTDP([Struct|Tail]) :-
	findall(
		Hittable,
		(
			belongsTo(Hittable,Struct),
			isHittable(Hittable)
		),
		HittableList
	),
	pfindLongestPathIntoStructureTDP(HittableList),
	psavePlansForDeepPenetrationTDP(Tail).

pfindLongestPathIntoStructureTDP([]) :- !.
pfindLongestPathIntoStructureTDP([Hittable|Tail]) :-
	pfindNextObjects(Hittable,NextObjectList),
	pfindLongestPathTDP(NextObjectList,Hittable,EffValue),
	pfindLongestPathIntoStructureTDP(Tail).
	
pfindLongestPathTDP([NextObject|Tail],Object,EffValue) :-
	getEfficiencyValueSeDH(Object,ThisEffValue),
	pfindNextObjectsTDP(NextObjects,NextOverObjectList).
	
pfindNextObjects(Object,NextObjectList) :-
	findall(
		NextObject,
		(
			(
				isOn(Hittable,NextObject),
				(
					not(NextObject == ground),
					not(hill(NextObject)
				)
			);
			(
				isLeft(Hittable,NextObject),
				(
					not(hill(NextObject)
				)
			)
		),
		NextObjectList
	).



