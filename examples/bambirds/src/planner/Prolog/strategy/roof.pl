%Trys to find structural intresting Hittables and then tries to work its way down
%into the structure

ptestRoofStRf() :-
	doInferencialGroundingSeIG(),
	savePlansForRoofStRf().


savePlansForRoofStRf():-
	%Object that are Roofs over a Important Object
	%Important Object = Pig/TNT 
	% =:= "Simple Roof"
	pFindPigsOrTNTThatProtectedAndTheirRespectiveRoofStRf(ObjectsAndRoofList),
	pSaveSimplePlansForAllStRF(ObjectsAndRoofList)

	%destroy structure that is protecting the structure with important objects...
	% =:= "Complex Roof"
	%TODO
	.

pFindPigsOrTNTThatProtectedAndTheirRespectiveRoofStRf(ObjectsAndRoofList) :-
	findall(
		ObjectAndRoof,
		(
			(
				object(Important),
				(
					pig(Important);
					hasMaterial(Important, tnt)
				),
				pSysoStRf("[RF] Important is pig/Material", (Important) ),
				isOver(Important, Roof),
				not(hill(Roof)),
				pSysoStRf("[RF] Important has roof", Roof ),
%				not(isHittable(Important,true)),
				pSysoStRf("[RF] Important is NOT hittable", (Important, Roof) ),
				(
					%If Both are in Same Structure
					(belongsTo(Important, Structure), belongsTo(Roof, Structure), ObjectAndRoof = [Important, Roof],
					findHittablesInStructure(Structure, Hittables), length(Hittables, N), (N>0));
					%Are roof is another structure that protects the Important Object 
					(protects(Structure, Important), belongsTo(Roof, Structure), ObjectAndRoof = [Important, Roof],
					findHittablesInStructure(Structure, Hittables), length(Hittables, N), (N>0))
				)
			); false
		),
		ObjectsAndRoofList
	), pSysoStRf('[RF] ObjectsAndRoofs', ObjectsAndRoofList),
	true.

pSaveSimplePlansForAllStRF([]):-!. 
pSaveSimplePlansForAllStRF([ObjectAndRoof | Rest]) :-
	splitListIn(ObjectAndRoof, Object, Roof),
	pSysoStRf('[RF] Object', Object),
	pSysoStRf('[RF] Roof', Roof),
	findPathsFromToHittablesForActiveBirdWithStandardLimitSeWf(Roof, Paths),
	pSaveSimplePlansForOneStRf(Object, Paths),
	
	pSaveSimplePlansForAllStRF(Rest).
	
findPathsForAll(_ObjectsAndRoofList):-
	true.
	
pSaveSimplePlansForOneStRf(_Object, []) :- !.
pSaveSimplePlansForOneStRf(Object, PathsList) :-
	pSaveSimplePlansForOneStRf(Object, PathsList, 0).
pSaveSimplePlansForOneStRf(Object, [Path | Rest], Iterator) :-
	planLimit(roof, Max),
	pSysoStRf('[RF-PlansForOne] Object, PathToRoof', (Object, Path)),
	pSysoStRf('[RF-PlansForOne] MaximumOfPlans, Iterator', (Max, Iterator)), 
	(	(Iterator =< Max) ->
		(
			pSysoStRf('---------NewSimplePlan is Beeing Saved---------',''),
			getLengthOfSeWf(Path, Value),
			pSysoStRf('[RF-PlansForOne] "Length"OfPath', Value),
			SuccessFactor is 1-Value,
			SuccessRate is SuccessFactor*10,
			pSysoStRf('[RF-PlansForOne] SuccessRate', SuccessRate),
			deleteRankValues([Path], [RawPath]),
			pSysoStRf('[RF-PlansForOne] RawPath', RawPath),
			splitListIn(RawPath, Target, _RestPath),
			pSysoStRf('[RF-PlansForOne] Target', Target),
			
			(	(pig(Object)) ->
				(
					pSysoStRf('[RF-PlansForOne] is Pig', Object),
					%TODO better values...
					getPlanRankSeDH(1, SuccessRate, 1, 1, roof, Rank),
					pSysoStRf('[RF-PlansForOne] PlanRank', Rank),
					
					savePlan([Target], [Object], roof, Rank),
					pSysoStRf('[RF-PlansForOne] SavedPlan', ([Target], [Object], roof, Rank)),
					
					pSaveSimplePlansForOneStRf(Object, Rest, Iterator)
				)
				;
				(	(hasMaterial(Object, tnt)) ->
					pSysoStRf('[RF-PlansForOne] is TNT', Object),
					%If TNT in target, find Pigs or Max Integrity Valued
					findPigsAndMaxIntegrityObjectsExplodableBy(Object, ExplodedList),
					pSysoStRf('[RF-PlansForOne] Pigs&MaxIntegrity, exploded', ExplodedList),
					pGetEfficiencyFactorForTNTOf(ExplodedList, Factor),
					pSysoStRf('[RF-PlansForOne] Resulting Efficiency Of TNT', Factor),
					
					%TODO Better values... plz
					getPlanRankSeDH(Factor, SuccessRate, 1, 5, roof, Rank),
					pSysoStRf('[RF-PlansForOne] PlanRank', Rank),
					savePlan([Target], [Object], roof, Rank),
					pSysoStRf('[RF-PlansForOne] SavedPlan', ([Target], [Object], roof, Rank)),
					
					pSaveSimplePlansForOneStRf(Object, Path, Iterator)
					;true
				)
			)
		);
		(
			pSysoStRf('[RF-PlansForOne] Enough Plans', ''),
			true
		)
	).

%pGetEfficiencyFactorForTNTOf(+ExplodableList, -Factor)
%returns a Factor
%List should never have other objects then Pigs or MaxIntegrity in it
pGetEfficiencyFactorForTNTOf([], 0):- !.
pGetEfficiencyFactorForTNTOf([Explodable | Rest], Factor) :-
	(	
		pig(Explodable),
		ThisFactor = 1,
		pSysoStRf("[RF] Pig and Factor", (Explodable, ThisFactor) );
		
		not(pig(Explodable)),
		belongsTo(Explodable, Structure),
		getObjectCountOf(Structure, ObjectCount),
		ThisFactor is ObjectCount/10,
		pSysoStRf("[RF] MaxIntegrity and Factor", (Explodable, ThisFactor) )
	),
	pGetEfficiencyFactorForTNTOf(Rest, NextFactor),
	pSysoStRf("[RF] This and NextFactor", (ThisFactor, NextFactor) ),
	Factor is ThisFactor + NextFactor.

pSysoStRf(_Text, _Value):- %syso(_Text, _Value),
	true.