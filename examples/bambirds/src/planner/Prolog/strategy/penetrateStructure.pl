penetrateStPs():-
	findall(
		_Value,
		(
			pSysoStPS('[PS] penetration called', ''),
			planLimit(penStruct, AmountOfPlans),
			structureLimit(penStruct, AmountOfStructure),
			pSysoStPS('[PS] Amount of Plans', AmountOfPlans),
			findTheNImportantStructuresOf(AmountOfStructure, StructuresList),
			pSysoStPS('[PS] Most Important', StructuresList),
			deleteRankValues(StructuresList, RankFreeStructuresList),
			
			penetrateStructuresStPs(AmountOfPlans, RankFreeStructuresList)
		),
		_Values
	).
	
penetrateStructuresStPS(_Amount, []) :- !.
penetrateStructuresStPs(Amount, [[Structure] | StructuresRest]):-
	pSysoStPS('Structure', Structure),
	penetrate(Amount, Structure),
	penetrateStructuresStPs(Amount, StructuresRest).
	
penetrateAndReturnPlan(Structure, Amount, PlansList) :-
	savePlansForStructureTMaxP(Amount, Structure, PlansList1),
	saveMaximumOfNPlansForStructureMinPenetrationTMinP(Amount, Structure),
	PlansList2 = [],
	append(PlansList1, PlansList2, PlansList).
	
penetrate(Amount, Structure) :-
	penetrateAndReturnPlan(Structure, Amount, _PlansList).

pSysoStPS(_Text, _Value):- %syso(_Text, _Value),
	true.