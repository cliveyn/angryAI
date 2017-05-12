:- [database].
:- [helpyMcHelpface].
:- [level/level1].
:- [search/bearingObject].
:- [search/databaseHelpers].
/*delete when dijk works*/%:- [search/dijkstraTestLevel].
:- [search/wayfinder].
:- [search/collapseStructure].
:- [search/graphGenerator].
:- [search/dijkstra].
:- [search/inferencialGrounding].
:- [search/quadrantSearch].
%:- [search/shortestPath].
:- [search/shortestPathChooser].
:- [search/totalHeight].
:- [strategy/destroyStructure].
:- [strategy/destroyStructureLow].
:- [strategy/domino].
:- [strategy/dominoNew].
:- [strategy/tnt].
:- [strategy/depot].
:- [strategy/pigContingency].
:- [strategy/pigSearch].
:- [strategy/heavyObject].
:- [strategy/penetrateStructure].
:- [strategy/whiteBird].
:- [strategy/roof].
:- [tactic/minPenetration].
:- [tactic/maxPenetration].

%:- [helpers].
%:- [functions_highArchitecture].
%:- [functions_database].
%:- [functions_efficientObjects].
%:- [functions_bearingObjects].


%%CodeConventions
% List 		-> findList
% Object 	-> getObject
% Bool 		-> isBool

% TODO get good rankingvalue, unload unused files
% TODO tnt need to use ranking
% TODO quadrantsearch lacks of good left/right assigning
% TODO give plans for more strategies
% TODO read TODOs in domino strategy
% TODO ----!!! minPenetration currently only uses objects with maxIntegrity, this is not good. Comments are missing. No behaviour for blue bird.

% Get the object to shoot at
%getBestShot(PlanList) :-
%	findAllPlans(PlanList).

%%sysoprint/2
% sysoprint(+String, +Object)
% can print a string and an object
syso(_String, _Object) :-
	write(_String),write(': '),writeln(_Object),
	true.

%syso for Plan saving
sysoPlan(Targets, Goals, Origin, Rank) :-
	write(Origin),write('-Plan saved: '),write(Targets), write(Goals), write('. Rank: '),writeln(Rank),
	true.

pAlexOutput(String,Object) :-
	write(String),write(': '),writeln(Object),
	true.

testMethodFerd(ObjectsAndRoofList) :-
	findall(
		ObjectAndRoof,
		(
			object(Important),
			(
				pig(Important);
				hasMaterial(Important,tnt)
			),
			isOver(Important,Roof),
			belongsTo(Important,ImportantStruct),
			belongsTo(Roof,RoofStruct),
			ImportantStruct == RoofStruct,
			ObjectAndRoof = [Important,Roof]
		),
		ObjectsAndRoofList
	).
%	findall(
%		ObjectAndRoof,
%	        (
%	            object(Important),
%	            pig(Important),
%	            object(Roof),
%	            belongsTo(Important, Structure), 
%	            belongsTo(Roof, Structure), 
%	            isOver(Roof,Important),
%	            ObjectAndRoof = [Important | Roof]
%	        ),
%	        ObjectsAndRoofList
%    ).

testMain :-
	testProlog().

testProlog() :-
	read(Number),
	string_concat('/Users/Alex/Documents/Uni/Semester_4/AI_Birds/Angry_Birds/finalProject/src/BamBird/src/planner/Prolog/level/level',Number,TmpFilename),
%	string_concat(TmpFilename,'.pl',Filename),
	catch(consult(TmpFilename),writeln('incorrect File Name'),initiateProlog()),
%	findAllPlans(AllPlans),
%	writeln(AllPlans),
%	flush_output(),
	
%	testMethodFerd(X),
%	pAlexOutput(test,X),
	
       	savePlansForDominoStDN(),
	unload_file(Filename),
	testProlog().

main :-
	initiateProlog().

initiateProlog() :-
	read(Filename),
	catch(consult(Filename),writeln('incorrect File Name'),initiateProlog()),
	findAllPlans(AllPlans),
	writeln(AllPlans),
	flush_output(),
	unload_file(Filename),
	initiateProlog().
%	halt.
	
% Test functions
%getBestShotLists(_Object).
	
%getBestShotTerms(_Object).

findAllPlans(FinalPlanList) :-
%	pclearAllPlans(),
	psaveAllPlans(),
%	retract(plan(0,dummy,dummy,dummy)),
	findall(
		Plan,
		(
			plan(Rank, Target,Goal,Strategy),
%			syso(strategy,Strategy),
%			syso(rank,Rank),
%			syso(targets,Target),
%			syso(goal,Goal),
			Plan = [Rank, Target, Goal, [Strategy]]
%			syso(Plan, plan)
		),
		PlanList),
	sort(PlanList,SortPlanList),
	kwikeSortInverse(SortPlanList, SortedPlanList),
%	syso('Sorted', SortedPlanList),
	pDeleteRankValues(SortedPlanList, FinalPlanList),
	syso('FinalPlanList',FinalPlanList).

pDeleteRankValues([],[]) :- !.
pDeleteRankValues(PlanListWithRankValues, PlanList) :-
	splitListIn(PlanListWithRankValues, Plan, RestOfPlanListWithRankValues),
	splitListIn(Plan, _RankValue, Rest),
	syso(rest, Rest),
	pDeleteRankValues(RestOfPlanListWithRankValues, RestOfPlanList),
	append([Rest], RestOfPlanList, PlanList).

psaveAllPlans() :-
	doInferencialGroundingSeIG(),
	syso('TNTStrategy','started'),
	(
		(savePlansForTntStrategyStTNT()) ->
			syso('TNTStrategy','finished');
			syso('TNTStrategy','faild')
	),
	% works just for maps with no or with direct hittable tnt
	syso('DepotStrategy','started'),
	(
		(savePlansForDepotStrategyStDe()) ->
			syso('DepotStrategy','finished');
			syso('DepotStrategy','faild')
	),
	% collapsesInDirection didnt work
	syso('DestroyLowStrategy','started'),
	(
		(saveDestroyStructurePlansLowStDS()) ->
			syso('DestroyLowStrategy','finished');
			syso('DestroyLowStrategy','faild')
	),
	syso('DominoStrategy','started'),
	(
		(savePlansForDominoStDN()) ->
			syso('DominoStrategy','finished');
			syso('DominoStrategy','faild')
	),
	syso('PenetrationStrategy','started'),
	(
		(penetrateStPs()) ->
			syso('PenetrationStrategy','finished');
			syso('PenetrationStrategy','faild')
	),
	syso('DestroyHighStrategy','started'),
	(
		(saveDestroyStructurePlansStDS()) ->
			syso('DestroyHighStrategy','finished');
			syso('DestroyHighStrategy','faild')
	),
	syso('PigContigency','started'),
	(
		(findPigContingencyPlansStPc(true)) ->
			syso('PigContigency','finished');
			syso('PigContigency','faild')
	),
	syso('PigSearch','started'),
	(
		(savePlanBySearchStPiS()) ->
			syso('PigSearch','finished');
			syso('PigSearch','faild')
	),
	syso('WhiteBirdStrategy','started'),
	(
		(
			savePlansForWhiteBirdStWB()
		) ->
		(
			syso('WhiteBirdStrategy','finished')
		);
		(
			syso('WhiteBirdStrategy','failed')
		)
	),
	syso('HeavyObjectStrategy','started'),
	(
		(
			savePlansForHeavyObjectStrategyStHO()
		) ->
		(
			syso('HeavyObjectStrategy','finished')
		);
		(
			syso('HeavyObjectStrategy','failed')
		)
	),
	syso('RoofStrategy','started'),
	(
		(
			savePlansForRoofStRf()
		) ->
		(
			syso('RoofStrategy','finished')
		);
		(
			syso('RoofStrategy','failed')
		)
	).