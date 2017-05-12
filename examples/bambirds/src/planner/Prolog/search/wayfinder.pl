% This class is for preorganized Interaction with the pathfinder.
% It should always be used before directly touching anything of graphs or wayfinding algorithms

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Use if something goes wrong (false-return) of other functions
% else never use this! (may corrupt program flow/results)
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%%cleanUpWayfinderSeWf/0
%cleanUpWayfinderSeWf().
%If Anything within Wayfinder went wrong and a process failed:
%call this methode to clean all asserts, before retrying
cleanUpWayfinderSeWf() :-
	retractall(dijkstraIsLimitedBy(_Limit)),
	retractall(searchBeyondLimit),
	
	cleanUpDijkstraSeDi,
	cleanUpGraphSeGG.
	
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Tools
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%%getIndexOfLasReachableSeWf/2
%getIndexOfLasReachableSeWf(+BeyondLimitPath, -IndexOfLastDestroyable)
%after search for a Path that is longer then the penetratiolimit
%use this to get the index of the last Object in the Path that will be destroyed.
%Use following function to get the Paths that can used with this methode:
%"findPathsFromToForActiveBirdWithStandardLimitAndSearchBeyondLimitSeWf/3"
getIndexOfLasReachableSeWf([], 0) :- !.
getIndexOfLasReachableSeWf([Head | Rest], Index) :-
	(Head == endOfPenetration) ->
		Index = 0;
		getIndexOfLasReachableSeWf(Rest, NextIndex),
		Index = 1+NextIndex.
		
%%getValueOfPathSeWf/2
%getValueOfPathSeWf(+Path, -Value) 
%returns the internal "length" of a path created by this class.
getLengthOfSeWf(Path, Value) :-
	splitListIn(Path, Value, _Rest),
	pSysoWf('[WF] Found Value', Value).

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%Most Often Used
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%findPathsFromToHittablesForActiveBirdWithSpecificLimitSeWf/3
%findPathsFromToHittablesForActiveBirdWithSpecificLimitSeWf(+RootList, +Limit, -PathsList)
%IMPORTANT: All root-elements have to be in the same Structure!!!
%Finds Path to all Hittables in the same structure as the first root element, with a arbitrary distance.
findPathsFromToHittablesForActiveBirdWithSpecificLimitSeWf([], _Limit, []) :- !.
findPathsFromToHittablesForActiveBirdWithSpecificLimitSeWf(RootList, Limit, PathsList):-
	cleanUpWayfinderSeWf(),
	pSysoWf('[WF]Wayfinder', 'cleaned'),
	
	splitListIn(RootList, Root, _Rest),
	pSysoWf('[WF]First Object in Rootlist', Root),
	belongsTo(Root, Structure),
	pSysoWf('[WF]Structure of Root', Structure),
	birdOrder(Bird, 0),
	pSysoWf('[WF]Active Bird', Bird),
	standardDijkstraSearchLimit(Limit),
	pSysoWf('[WF]Limit for Dijkstra Set', Limit),
	
	findall(
		InternalPathsList,	
		(
			findPathsToHittablesFromOfForBIRDWithLIMITSeWf(RootList, Structure, Bird, Limit, InternalPathsList),
			pSysoWf('[WF]Path Found by Dijkstra', InternalPathsList)
		),
		Paths),
		
		nth0(0, Paths, PathsList),
		pSysoWf('[WF]All Paths found by Dijkstra', Paths),
		pSysoWf('[WF]Chosen and Returned Path', PathsList).
		
%%findPathsFromToHittablesForActiveBirdWithStandardLimitSeWf/2
%findPathsFromToHittablesForActiveBirdWithStandardLimitSeWf(+RootList, -PathsList)
%IMPORTANT: All root-elements have to be in the same Structure!!!
%Finds Path to all Hittables in the same structure as the first root element.
findPathsFromToHittablesForActiveBirdWithStandardLimitSeWf([], []) :- !.
findPathsFromToHittablesForActiveBirdWithStandardLimitSeWf(RootList, PathsList):-
	standardDijkstraSearchLimit(Limit),
	findPathsFromToHittablesForActiveBirdWithSpecificLimitSeWf(RootList, Limit, PathsList).

%%findPathsFromToForActiveBirdWithStandardLimitSeWf/3
%findPathsFromToForActiveBirdWithStandardLimitSeWf(+RootList, +TargetList, -PathsList)
%IMPORTANT: All root- & target-elements have to be in the same Structure!!!
%Finds path to all Targets in the same structure.
findPathsFromToForActiveBirdWithStandardLimitSeWf(RootList, TargetList, PathsList) :-
	pSysoWf('[Wf] Start Wayfinder', ''),
	splitListIn(RootList, Root, _RootRest),
	splitListIn(TargetList, Target, _TargetRest),
	pSysoWf('[Wf] Root', Root),
	pSysoWf('[Wf] Target',  Target),
		
	belongsTo(Root, Structure),
	belongsTo(Target, Structure),
	birdOrder(Bird, 0),
	standardDijkstraSearchLimit(Limit),
	
	findPathsFromToForBirdWithLimitSeWf(RootList, TargetList, Structure, Bird, Limit, PathsList).

findPathsFromToForActiveBirdWithStandardLimitAndSearchBeyondLimitSeWf([], _TargetList, []) :-!.
findPathsFromToForActiveBirdWithStandardLimitAndSearchBeyondLimitSeWf(_RootList, [], []) :-!.
findPathsFromToForActiveBirdWithStandardLimitAndSearchBeyondLimitSeWf([], [], []) :-!.
findPathsFromToForActiveBirdWithStandardLimitAndSearchBeyondLimitSeWf(RootList, TargetList, PathsList) :-
	pSysoWf('[Wf] Try to assert', 'searchBeyondLimit'),
	asserta(searchBeyondLimit),
	pSysoWf('[Wf] searchBeyondLimit', 'asserted'),
	
	splitListIn(RootList, Root, _RootRest),
	splitListIn(TargetList, Target, _TargetRest),
	pSysoWf('[Wf] Root', Root),
	pSysoWf('[Wf] Target',  Target),
	
	belongsTo(Root, Structure),
	belongsTo(Target, Structure),
	birdOrder(Bird, 0),
	standardDijkstraSearchLimit(Limit),

	findPathsFromToForBirdWithLimitSeWf(RootList, TargetList, Structure, Bird, Limit, PathsList).

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%Look here if you want more control/construct your own searches
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%findPathsToHittablesFromOfForBIRDWithLIMITSeWf/5
%findPathsToHittablesFromOfForBIRDWithLIMITSeWf((+RootList, +Structure, +Bird, +Limit, -PathsList)
%Important: Every Entry in RootList has to be in the given Structure!!! otherwise, no solution!!
findPathsToHittablesFromOfForBIRDWithLIMITSeWf(RootList, Structure, Bird, Limit, PathsList) :-
	pfindHittablesSeWf(Structure, HittablesList),
	findPathsFromToForBirdWithLimitSeWf(RootList, HittablesList, Structure, Bird, Limit, PathsList).
	
%%getPathsFromToHittablesSeWf/3
%getPathsFromToHittablesSeWf(+RootList, +Structure, -PathsList)
%Important: Every Entry in RootList has to be in the given Structure!!! otherwise, no solution!!
findPathsToHittablesFromOfSeWf(RootList, Structure, PathsList) :-
	pfindHittablesSeWf(Structure, HittablesList),
	findPathsFromToSeWf(RootList, HittablesList, Structure, PathsList).

%%findPathsFromToForBirdWithLimitSeWf/6
%findPathsFromToForBirdWithLimitSeWf(+RootList, +Targetlist, +Structure, +Bird, +Limit, -PathsList)
%Use arbitrary rootobjects in List to find Target Objects!
findPathsFromToForBirdWithLimitSeWf(RootList, Targetlist, Structure, Bird, Limit, PathsList) :-
	createEdgesForAndBirdSeGG(Structure, Bird),
	%%Make Limit relevant for Dijkstra
	assertz(dijkstraIsLimitedBy(Limit)),
	
	pIterateOverBothWithDiJkstraSeWf(RootList, Targetlist, ComputedPathsList),
	pSysoWf("[WF] Djik done with Paths", ComputedPathsList),
	kwikeSort(ComputedPathsList, PathsList),
	pSysoWf('[wf] Sorted List of all found paths, shortest first.', PathsList),
	
	cleanUpWayfinderSeWf().

%%findPathsFromToSeWf/4
%findPathsFromToSeWf(+RootList, +Targetlist, +Structure, -PathsList)
%Use arbitrary rootobjects in List to find Target Objects!
findPathsFromToSeWf(RootList, Targetlist, Structure, PathsList) :-
	createEdgesForSeGG(Structure),
	
	pIterateOverBothWithDiJkstraSeWf(RootList, Targetlist, ComputedPathsList),
	kwikeSort(ComputedPathsList, PathsList),
	pSysoWf('[wf] Sorted List of all found paths, shortest first.', ComputedPathsList),
	
	cleanUpWayfinderSeWf().

pIterateOverBothWithDiJkstraSeWf([], [], []) :- !.
pIterateOverBothWithDiJkstraSeWf([], _TargetsList, []) :- !.
pIterateOverBothWithDiJkstraSeWf(_Rootlist, [], []) :- !.
pIterateOverBothWithDiJkstraSeWf([Root| SpareRoots], TargetsList, PathsList):-
	pSysoWf('[WF]Root is', Root),
	pfindPathsOfToSeWf(Root, TargetsList, FirstPathsList),
	pIterateOverBothWithDiJkstraSeWf(SpareRoots, TargetsList, RemainingPaths),
	append(FirstPathsList, RemainingPaths, PathsList).

pfindPathsOfToSeWf([],[],[]) :- !.
pfindPathsOfToSeWf([],_TargetList,[]) :- !.
pfindPathsOfToSeWf(_Root,[], []) :- !.
pfindPathsOfToSeWf(Root, TargetsList, PathsList) :-
	splitListIn(TargetsList, Target, Rest),
	pSysoWf('[WF]TargetList was split, new target is', Target),
	
	cleanUpDijkstraSeDi,
	pSysoWf('[WF]Dijkstra', 'Cleaned'),
	findPathByDijkstraSeDi(Root, Target, Path),
	length(Path, N),
	pSysoWf('[WF]Resulting Path has Length', [N | Path]),
	
	( (N >= 2) ->
		pSysoWf('[WF]FoundPath', 'valid'),
		PathsList = [Path | Paths];
		
		pSysoWf('[WF]FoundPath', 'INvalid'),
		PathsList = Paths
	),
	
	pfindPathsOfToSeWf(Root, Rest, Paths).
	
pfindHittablesSeWf(Structure, HittablesList) :-
	findall(
		Hittable,
		(	
			object(Hittable),
			belongsTo(Hittable, Structure),
			isHittable(Hittable,true)
		),
		HittablesList
	), 
	pSysoWf('[WF]Hittables', HittablesList).
	
/*
%%Two Birds
findPathsForTwoConsecutiveBirdsToHittablesWithStandardLimitSeWf(Rootlist,FirstBird, SecondBird, PathList) :-
	%Do Search for Secound Bird
	pfindAllHittablesBelongingToRootStructureSeWf(RootList, HittablesList),
	pFindObjectsDestroyableByFirstBirdAndResultingHittablesforSecondBirdSeWf(HittablesList, FirstBird, DestroyableByFirst, HittablesForSecond),
	true.

pfindAllHittablesBelongingToRootStructureSeWf([Root | _Rest], HittablesList) :-
	findHittablesInStructure(Root, HittablesList).
pFindObjectsDestroyableByFirstBirdAndResultingHittablesforSecondBirdSeWf(StartObjects, FirstBird, DestroyableByFirst, HittablesForSecound) :-
true.
*/
	
	
pSysoWf(_Text, _Value) :- %syso(_Text, _Value),
	true.