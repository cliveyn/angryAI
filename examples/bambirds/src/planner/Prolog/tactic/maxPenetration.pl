savePlansForStructureTMaxP(Amount, Structure, Plans):-
	pSysoTMaxP("[TMaxP] Amount, Structure", [Amount, Structure]),
	pFindNHighestIntegrityObjectInTMaxP(3, Structure, RelevantObjectsList),
	pSysoTMaxP("[TMaxP] Root", RelevantObjectsList),
	(	(RelevantObjectsList == []) ->
		pSysoTMaxP('[TMaxP] MaxPen Not Started for', Structure),
		Plans = [],
		true;
		(
			pSysoTMaxP("[TMaxP] Inner-Root", RelevantObjectsList),
			%findHittablesInStructure(Structure, Hittables),
			%pSysoTMaxP("[TMaxP] Hittables", Hittables),
			
			%findPathsFromToForActiveBirdWithStandardLimitAndSearchBeyondLimitSeWf(RelevantObjectsList, Hittables, SortedBeyondLimitPaths),
			findPathsFromToHittablesForActiveBirdWithStandardLimitSeWf(RelevantObjectsList, SortedBeyondLimitPaths),
			pSysoTMaxP("[TMaxP] Found Paths with Standard Limit", SortedBeyondLimitPaths),
		
			%pFindStillReachablePaths(SortedBeyondLimitPaths, StillReachablePathsList),
			%pSysoTMaxP("[TMaxP] All still reachable Paths", StillReachablePathsList),
			kwikeSortInverse(SortedBeyondLimitPaths, DescendingReachablePathsList),
			pSysoTMaxP("[TMaxP] ReachablePaths in Descending Order", DescendingReachablePathsList),
			pConvertIntoPlansTMaxP(Amount, DescendingReachablePathsList, false, ConvertedPlans),
			Plans = ConvertedPlans
			/*
			substractFrom(StillReachablePathsList, SortedBeyondLimitPaths, UnreachablePaths),
			kwikeSort(UnreachablePaths, AscendingUnreachablePaths),
			pSysoTMaxP("[TMaxP] All still unreachable Paths(ascending)", StillReachablePathsList),
			pConvertIntoPlansTMaxP(Amount, AscendingUnreachablePaths, true)*/
		)
	).
	
pFindStillReachablePaths([], []) :- !.
pFindStillReachablePaths([Path | RestPaths], StillReachablePaths) :-	
	( (pIsPenetrationLimitReachedIn(Path)) ->
		StillReachablePaths = StillReachablePaths;
		append([Path], RestOfStillReachablePaths, StillReachablePaths)
	),
	pFindStillReachablePaths(RestPaths, RestOfStillReachablePaths).

pIsPenetrationLimitReachedIn([]):- true, !.
pIsPenetrationLimitReachedIn([Head, Tail]) :-
	(Head == endOfPenetration) ->
		false;
		pIsPenetrationLimitReachedIn(Tail).
	
pConvertIntoPlansTMaxP(Amount, DescendingBeyondLimitsPathsWithoutReachablesList, PathsAreUnreachable, Plans) :-
	createPlansFromOF(Amount, 1, DescendingBeyondLimitsPathsWithoutReachablesList, PathsAreUnreachable, Plans).

createPlansFromOF(_Amount, _Iterator, [], _PathsAreUnreachable, []) :- !.
createPlansFromOF(Amount, Iterator, [Path | PathsRest], PathsAreUnreachable, Plans) :- 
	pSysoTMaxP('[TMaxP] AmountOfIterations', Amount),
	(	(Iterator =< Amount) ->
		(
			createPlanFor(Path, PathsAreUnreachable, Plan),
			NewIterator is Iterator+1,
			pSysoTMaxP('[TMaxP] Iterator', NewIterator),
			createPlansFromOF(Amount, NewIterator, PathsRest, PathsAreUnreachable, NextPlans),
			pSysoTMaxP('[TMaxP] NextPlans', NextPlans),
			append([Plan], NextPlans, Plans),
			pSysoTMaxP('[TMaxP] Plans To This Stage', Plans)
		)
		;
		true,
		Plans = []
	).

createPlanFor([], _Bool, _Plan):-!.
createPlanFor([Value | Objects], PathsAreUnreachable, Plan) :-
	pSysoTMaxP("[TMaxP] Creating Plan from", [Value | Objects]),
	splitListIn(Objects, Hittable, Rest),
	pSysoTMaxP('[TMaxP] PathHittable', Hittable),
	pSysoTMaxP('[TMaxP] PathRest', Rest),
	belongsTo(Hittable, Structure),
	getPigsOf(Structure, PigList),
	length(PigList, PigsCount),
	pSysoTMaxP('[TMaxP] Pigs In structure', (PigsCount, Structure)), 
	getObjectCountOf(Structure, ObjectCount),
	ObjectCountTmp is (ObjectCount * 0.7),
	length(Rest, PathLengthTmp),
	pSysoTMaxP('[TMaxP] Objects in Structure', ObjectCount),	
	birdOrder(Bird, 0),
	modifyTargetForBird(Hittable, Bird, ModifiedHittables),
	pSysoTMaxP('[TMaxP] Hittables|Modified', ([Hittable], [ModifiedHittables])),
	PathLengthMod is 10-Value,
	
	% TODO better Goals!!
	%Original Goals of pathfinder
	(	(PathsAreUnreachable == true)	->
			getIndexOfLasReachableSeWf(Objects, Index),
			nth0(Index, Objects, Goal),
			ObjectCountInPath is PathLengthTmp-1,
			getPlanRankSeDH(PigsCount, ObjectCountTmp, PathLengthMod, ObjectCountInPath, maxPen, Rank)
			;
			nth0(ObjectCountInPath, Objects, Goal),
			ObjectCountInPath = PathLengthTmp,
			getPlanRankSeDH(PigsCount, ObjectCountTmp, PathLengthMod, ObjectCountInPath, maxPen, Rank)
	),
	pSysoTMaxP('[TMaxP] Planrank', (Rank, ModifiedHittables, [Goal | PigList], maxPen)),
	Goals = [Goal | PigList],
	savePlan(ModifiedHittables, Goals, maxPen, Rank),
	Plan = [plan(Rank, ModifiedHittables, Goals, maxPen), getPlanRankSeDH(PigsCount, ObjectCount, PathLengthMod, ObjectCountInPath, maxPen, Rank)].

pFindNHighestIntegrityObjectInTMaxP(Amount, Structure,ObjectList) :-
	findall(
		ValueAndObject,
		(
			belongsTo(Object, Structure),
			hasIntegrity(Object, IntergrityValue),
			ValueAndObject = [IntergrityValue, Object]
		),
		ValueAndObjectList
	), kwikeSortInverse(ValueAndObjectList, DescendingValueAndObjectList),
	pSysoTMaxP('[TMaxP]Objects Sorted with highest Integrity', DescendingValueAndObjectList),
	deleteRankValues(DescendingValueAndObjectList, DescendingValueAndObjectListWithoutIntegity),
	pSysoTMaxP('[TMaxP]Objects Sorted Without Ranks', DescendingValueAndObjectListWithoutIntegity),
	pFindNDestroyableObjectsTMaxP(Amount, DescendingValueAndObjectListWithoutIntegity, ObjectList).
	
pFindNDestroyableObjectsTMaxP(Amount, SortedObjectList, ObjectList) :-
	pFindNDestroyableObjectsTMaxP(Amount, 0, SortedObjectList, ObjectList).
	
pFindNDestroyableObjectsTMaxP(_Amount, _Iterator, [], []):- !.
pFindNDestroyableObjectsTMaxP(Amount, Amount, _ObjectList, []):- !.
pFindNDestroyableObjectsTMaxP(Amount, Iterator, [[Object] | Rest], ObjectList) :-
	%ObjectIs destroyable .. [I.] else [II.]
	(	(isObjectDestroyable(Object)) ->
		(	ThisObjectList = [Object], %[I.]Add Object to List
			NewIterator is Iterator+1, %Increase Iterator
			pFindNDestroyableObjectsTMaxP(Amount, NewIterator, Rest, NextObjectList), %RestartWithNextObject
			append(ThisObjectList, NextObjectList, ObjectList) %Combine RecursiveCall with ThisObject
		);
			
		(	pFindNDestroyableObjectsTMaxP(Amount, Iterator, Rest, NextObjectList), %[II.]Just RestartWIthNextOBject
			ObjectList = NextObjectList %And use only this as ObjectList
		)
	).

isObjectDestroyable(Object):-
	hasSize(Object,Size),
	hasForm(Object,Form),
	hasMaterial(Object,Material),
	birdOrder(Bird,0),
	hasColor(Bird,Color),
	efficiencySizeForm(Size,Form,Material,EffObValue),
	efficiencyBirdMaterial(Color,Material,EffBiValue),
	
	EffValue is EffObValue*EffBiValue,
	
	(	(EffValue =< 1) ->
		pSysoTMaxP('[TMaxP] Destroyable', Object);
		
		pSysoTMaxP('[TMaxP]	NOT Destroyable', Object),
		false
	).
	
pSysoTMaxP(_Text, _Value) :- %syso(_Text, _Value),
	true.
	
/* May we may need this methods... later for roof or so
%Sorts BeyondLimit-Paths by deepest (amount of Penetrated Objects)
pSaveBestOf(UnreachablePaths, DescendingReValuedPaths) :-
	%HighestValue is best highest Chance!
	pRecalculate(UnreachablePaths, NewlyValuedUnreachablePaths),
	kwikeSortInverse(NewlyValuedUnreachablePaths, DescendingReValuedPaths).
	
pRecalculate([], []):-!.
pRecalculate([UnreachablePath | RestOfUnreachablePaths], NewlyValuedUnreachablePaths) :-
	pGivePathNewValue(UnreachablePath, NewPath),
	pRecalculate(RestOfUnreachablePaths, RestOfNewlyValuedPaths),
	append(NewPath, RestOfNewlyValuedPaths, NewlyValuedUnreachablePaths).

pGivePathNewValue([_Head | Path], NewPath) :-
	pIterateOverPathAndGetTMaxP(Path, PenetratedObjectsCount),
	length(Path, PathLength),
	CorrectLength is PathLength-1,
	PenetrationCoeffcient is PenetratedObjectsCount/CorrectLength,
	NewPath = [].
*/