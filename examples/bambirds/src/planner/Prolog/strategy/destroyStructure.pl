%%destroyStructureStDS/0
% destroyStructureStDS()
% 
saveDestroyStructurePlansStDS() :-
%saveDestroyStructurePlansStDS().
%saveDestroyStructurePlansStDSNOTWORKINGASDIJKSTRADOESNOTWORK() :-
	%if there are Structures With Pigs do [I.] else [2]
	( (findStructuresSortedByPigCount(false, StructuresWithPigsList)) ->
		%[I.] Sort Structures by highest objectCount //TODO Replace with "Destruction Potential"
		sortEqualValuesByPredicate(StructuresWithPigsList, _NewlySortedStructuresLists);
		%[II.] see if there are structures that protect something do [II.I.] else [II.II.]
		( (findStructuresSortedByProtectedObjectsCount(false, StructuresProtectingList)) ->
			%[II.I.]Sort Structures by Highest ObjectCount
			sortEqualValuesByPredicate(StructuresProtectingList, _NewlySortedStucturesList)
			;			
			
			%[II.II.] Just find Structures With HighestObjectCount
			pFindStructuresSortedByObjectCountStDS(_StructuresSortedByObjectCountList)
		)
	).
				
/*
	findall(
		Structure,
		(
			structure(Structure),
			pig(Pig),
			belongsTo(Pig, Structure)
		),
		List),
	length(List, ListLength),
	((ListLength > 0) ->
		pFindPlansForMinPenetrationTMinP(List)
		;
		true).
	%retractStuff.
*/
sortEqualValuesByPredicate([], []) :- !.	
sortEqualValuesByPredicate(StructureList, NewlySortedStructuresLists) :-
	findAllStructuresOfSameValue(StructureList, SameValueStructures),
	substractFrom(SameValueStructures, StructureList, RestOfStructuresList),
	
	sortInDecendingOrderByObjectAmount(SameValueStructures, NewlySortedStructures),
	
	sortEqualValuesByPredicate(RestOfStructuresList, NextBestStructuresNewlySorted),
	append(NewlySortedStructures, NextBestStructuresNewlySorted, NewlySortedStructuresLists).
	
sortInDecendingOrderByObjectAmount(SameValueStructures, NewlySortedStructures) :-
	getRankingValueForAllstructures(SameValueStructures, NewlyValuesStructures),
	kwikeSortInverse(NewlyValuesStructures, NewlySortedStructures).
	
getRankingValueForAllstructures([], []):- !.
getRankingValueForAllstructures([ValuedStructure | Rest], NewlyRankedList) :-
	splitListIn(ValuedStructure, _Value, Structure),
	aggregate( count, (object(Object), not(hill(Object)), belongsTo(Object, Structure)), ObjectCount ),
	NewValuedStructure = [[ObjectCount, Structure]],
	
	getRankingValueForAllstructures([ValuedStructure | Rest], NextNewlyRankedList),
	
	append(NewValuedStructure, NextNewlyRankedList, NewlyRankedList).

pFindStructuresSortedByObjectCountStDS(StructuresSortedByObjectCountList) :-
	findall(
		ValuedStructure,
		(
			aggregate( count, (object(Object), not(hill(Object)), belongsTo(Object, Structure)), ObjectCount ),
			ValuedStructure = [ObjectCount| Structure]
		),
		StructuresWithObjectCount
	),
	kwikeSortInverse(StructuresWithObjectCount, StructuresSortedByObjectCountList).