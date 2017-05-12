:- [database].
:- [helpyMcHelpface].
%:- [level/level2].
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
%:- [strategy/domino].
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
%	write(String),write(': '),writeln(Object),
	true.

%syso for Plan saving
sysoPlan(_Targets, _Goals, _Origin, _Rank) :-
%	write(Origin),write('-Plan saved: '),write(Targets), write(Goals), write('. Rank: '),writeln(Rank),
	true.

pAlexOutput(_String,_Object) :-
%	write(String),write(': '),writeln(Object),
	true.

main :-
	initiateProlog().

easyMain :-
	easyInitiateProlog().

easyInitiateProlog() :-
	read(Filename),
	catch(consult(Filename),writeln('incorrect File Name'),initiateProlog()),
	pfindAllEasyPlans(AllPlans),
	writeln(AllPlans),
	flush_output(),
	unload_file(Filename),
	halt.

initiateProlog() :-
	read(Filename),
	catch(consult(Filename),writeln('incorrect File Name'),initiateProlog()),
	findAllPlans(AllPlans),
	writeln(AllPlans),
	flush_output(),
	unload_file(Filename),
%	initiateProlog().
	halt.
	
% Test functions
%getBestShotLists(_Object).
	
%getBestShotTerms(_Object).

pfindAllEasyPlans(FinalPlanList) :-
	psaveAllEasyPlans(),
	findall(
		Plan,
		(
			plan(Rank, Target,Goal,Strategy),
			Plan = [Rank, Target, Goal, [Strategy], [Rank]]
		),
		PlanList),
	sort(PlanList,SortPlanList),
	kwikeSortInverse(SortPlanList, SortedPlanList),
	pDeleteRankValues(SortedPlanList, FinalPlanList),
	syso('FinalPlanList',FinalPlanList).


findAllPlans(FinalPlanList) :-
%	pclearAllPlans(),
	psaveAllPlans(),
%	retract(plan(0,dummy,dummy,dummy)),
	pGeneratePlanList(PrePlanList),
	
	pJustShootAtPigsIfNoUsefullPlans(PrePlanList, PlanList),	
	
	sort(PlanList,SortPlanList),
	kwikeSortInverse(SortPlanList, SortedPlanList),
%	syso('Sorted', SortedPlanList),
	pDeleteRankValues(SortedPlanList, FinalPlanList),
	syso('FinalPlanList',FinalPlanList).

pGeneratePlanList(PlanList) :-
	findall(
		Plan,
		(
			plan(Rank, Target,Goal,Strategy),
			Plan = [Rank, Target, Goal, [Strategy], [Rank]]
		),
		PlanList).
		
pJustShootAtPigsIfNoUsefullPlans(PlanList, CorrectedPlanList) :- 
	length(PlanList, N),
	(	(N == 1) ->
		(	(findPigContingencyPlansStPc(false)) ->
			generatePlanList(CorrectedPlanList);true 
		);
		CorrectedPlanList = PlanList
	).

pDeleteRankValues([],[]) :- !.
pDeleteRankValues(PlanListWithRankValues, PlanList) :-
	splitListIn(PlanListWithRankValues, Plan, RestOfPlanListWithRankValues),
	splitListIn(Plan, _RankValue, Rest),
	syso(rest, Rest),
	pDeleteRankValues(RestOfPlanListWithRankValues, RestOfPlanList),
	append([Rest], RestOfPlanList, PlanList).

psaveAllEasyPlans() :-
	doInferencialGroundingSeIG(),
	syso('DepotStrategy','started'),
	(
		(savePlansForDepotStrategyStDe()) ->
			syso('DepotStrategy','finished');
			syso('DepotStrategy','faild')
	),
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
	syso('PigStrategy','started'),
	(
		(findPigContingencyPlansStPc(true)) ->
			syso('PigStrategy','finished');
			syso('PigStrategy','faild')
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
	).

psaveAllPlans() :-
	doInferencialGroundingSeIG(),
	syso('TNTStrategy','started'),
	(
		(savePlansForTntStrategyStTNT()) ->
			syso('TNTStrategy','finished');
			syso('TNTStrategy','faild')
	),
	% works just for maps with no or with direct hittable tnt
/*	syso('DepotStrategy','started'),
	(
		(savePlansForDepotStrategyStDe()) ->
			syso('DepotStrategy','finished');
			syso('DepotStrategy','faild')
	),*/
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
	syso('PigStrategy','started'),
	(
		(findPigContingencyPlansStPc(true)) ->
			syso('PigStrategy','finished');
			syso('PigStrategy','faild')
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

	
	

%pclearAllPlans() :-
%	retractall(plan(_A,_B,_C,_D)).