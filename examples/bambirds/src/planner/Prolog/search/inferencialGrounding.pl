% Used Predicates:
% hasIntegrity(Object,Value).
% containsRelevant(Struct,Object).
% isDestroyable(Object,Bool).
% hasMaxIntegrity(Struct,Value).
% totalHeightOfStructure(Struct,Value).
% hasTotalHeight(Object,Value).
% hasImportanceLevelOf(Structure, Value).
%*Not yet* isOnLongitudeOf(Object, Value, Structure).


%%doInferencialGroundingSeIG/0
% doInferencialGroundingSeIG()
% calls all inferencial methods
doInferencialGroundingSeIG() :-
	clearInferencialGroundingSeIG(),
	saveRelevantObjectsSeIG(),
	saveIsDestroyableSeIG(),
	saveBearingObjectsForAllStructuresSeIG(),
	saveTotalHeightsForAllStructuresSeIG(),
	saveImportanceLevelOfStructures(),
	savePlan([dummy],[dummy],dummy,0),
%	saveLongitudeOfObjects(),
	syso('All Groundings Done','').
	
clearInferencialGroundingSeIG() :-
	retractall(hasIntegrity(_X1,_Y1)),
	retractall(containsRelevant(_X2,_Y2)),
	retractall(isDestroyable(_X3,_Y3)),
	retractall(hasMaxIntegrity(_X4,_Y4)),
	retractall(totalHeightOfStructure(_X5,_Y5)),
	retractall(hasTotalHeight(_X6,_Y6)),
	retractall(plan(_W7,_X7,_Y7,_Z7)),
	retractall(hasImportanceLevelOf(_X8, _Y8)),
	cleanUpWayfinderSeWf().
	%retractall(isOnLongitudeOf(_X7, _Y7, _Z7)).
	
	
% saveRelevantObjectsSeIG()
% creates a new predicate (contains) for each relevant Object
% in all structures
saveRelevantObjectsSeIG() :-
	findall(
		Object,
		(
			structure(Struct),
			belongsTo(Object,Struct),
			(
				pig(Object);
				hasMaterial(Object,tnt);
				hasForm(Object,ball),
				(hasSize(Object,medium);
				hasSize(Object,big)),
				not(hasForm(Object,bar))
			),
			(
				(containsRelevant(Struct,Object)) ->
					true;
					assertz(containsRelevant(Struct,Object))
			)
		),
		_TmpList % Underscore suppresses singleton warnings
	).

% saveIsDestroyable()
% creates a new predicate (isDestroyable) for each Object
saveIsDestroyableSeIG() :-
	findall(
		Object,
		(
			object(Object),
			(
				(hasMaterial(Object,_Material);
				pig(Object)) ->
					(
						(isDestroyable(Object,true)) ->
							true;
							assertz(isDestroyable(Object,true))
						);
						(isDestroyable(Object,false)) ->
							true;
							assertz(isDestroyable(Object,false))
			)
		),
		_TmpList
	).
	
% saveBearingObjectsForAllStructures()
% creates new predicates (hasIntegrity) for each Object
% creates new predicates (hasMaxIntegrity) for each Structure
saveBearingObjectsForAllStructuresSeIG() :-
	findall(
		Values,
		(
			structure(Struct),
			findBearingObjectsSeBO(Struct,MaxValue),
			Values = [Struct,MaxValue]
		),
		ValueList
	),
	saveMaxIntegrityValuesForStructuresSeIG(ValueList).
	
% saveMaxValuesForStructuresSeGI(+ValueList)
% save the maximum integrity value for each Structure
saveMaxIntegrityValuesForStructuresSeIG([Head|Tail]) :-
	nth0(0,Head,Struct),
	nth0(1,Head,MaxValue),
	(
		(hasMaxIntegrity(Struct,MaxValue)) ->
			true;
			assertz(hasMaxIntegrity(Struct,MaxValue))
	),
	saveMaxIntegrityValuesForStructuresSeIG(Tail).
	
saveMaxIntegrityValuesForStructuresSeIG([]).

% saveTotalHeightsForAllStructuresSeIG()
% save all total heights for each object and for each structure
saveTotalHeightsForAllStructuresSeIG() :-
	findall(
		Values,
		(
			structure(Struct),
			findTotalHeightsSeTH(Struct,MaxValue),
			Values = [Struct,MaxValue]
		),
		ValueList
	),
	saveTotalHeightsForEachStructureSeIG(ValueList).

saveTotalHeightsForEachStructureSeIG([Head|Tail]) :-
	nth0(0,Head,Struct),
	nth0(1,Head,Value),
	(
		(totalHeightOfStructure(Struct,Value)) ->
			true;
			assertz(totalHeightOfStructure(Struct,Value))
	),
	saveTotalHeightsForEachStructureSeIG(Tail).
saveTotalHeightsForEachStructureSeIG([]).

saveImportanceLevelOfStructures() :-
	findall(
		Value,
		(
			structure(Structure),
			getPigCountOf(Structure, PigCount),
			PigCountFinal is PigCount*10000,
			
			getObjectCountOf(Structure, ObjectCount),
			ObjectCountFinal is ObjectCount,
			
			totalHeightOfStructure(Structure, Heigth),
			HeightFinal is Heigth*5,
			
			Value is PigCountFinal+ObjectCountFinal+HeightFinal,
			asserta(hasImportanceLevelOf(Structure, Value))
		),
		_Values
	).

/*
TODO optimize Graph generator!
saveLongitudeOfObjects() :-
	findall(
		CheckedStructure,
		(
			isAnchorPointOf(Anchor, CheckedStructure),
			pStartLongitudeCalculationAt(Anchor)	
		),
		_CheckedStructures
	).
	
pStartLongitudeCalculationAtOf(Anchor):- 
	pSaveLongitudeOfIn(Anchor, 0).
	
pSaveLongitudeOfIn(Object, CurrentLongitudeValue):-
	assertLongitudeSavely(Object, CurrentLongitudeValue),
	findall(
		OtherObject,
		(
			pAnalyseLeftObject(Object, OtherObject, CurrentLongitudeValue);
			pAnalyseRightObject(Object, OtherObject, CurrentLongitudeValue);
			pAnalyseLowerObject(Object, OtherObject, CurrentLongitudeValue);
			pAnalyseUpperObject(Object, OtherObject, CurrentLongitudeValue);
			true
		),
		_FoundObjects		
	).
	
pAnalyseLeftObject(Object, OtherObject,CurrentLongitude) :-
	((isLeft(OtherObject, Object)) ->
		getWidthSeDH(OtherObject, Width),
		NewLongitude is CurrentLongitude-1-Width,
		pSaveLongitudeOfIn(OtherObject, NewLongitude); true
	).

pAnalyseRightObject(Object, OtherObject, CurrentLongitude) :-
	((isRight(OtherObject, Object)) ->
		getWidthSeDH(OtherObject, Width),
		NewLongitude is CurrentLongitude+1+Width,
		pSaveLongitudeOfIn(OtherObject, NewLongitude); true
	).
pAnalyseLowerObject(Object, OtherObject, CurrentLongitude) :-
	((isOn(Object, OtherObject)) ->
		%get Both Widths__
		getWidthSeDH(Object, ObjectWidth),
		getWidthSeDH(OtherObject, OtherWidth),
		%TODO Decide if it is the same or difference.
		%ActiveObject is smaller then the next found Object
		(	(ObjectWidth < OtherWidth) ->
			%Change CurrentLongitude respectively
			WidthNext = OtherWidth-ObjectWidth,
			OtherLongitude = CurrentLongitude+WidthNext;
			
			%else just use CurrentLongitude
			OtherLongitude = OtherLongitude 
		),
		%If Object is longer, use this value,
		%If OtherObject is longer use difference as Longitude valueaddition
		pSaveLongitudeOfIn(OtherObject, OtherLongitude); true
	).
pAnalyseUpperObject(Object, OtherObject, CurrentLongitude) :-
	((isOn(OtherObject, Object)) ->
			pSaveLongitudeOfIn(OtherObject, CurrentLongitude); true
	).
	
assertLongitudeSavely(Object, CurrentLongitudeValue) :-
	belongsTo(Object, Structure),
	(isOnLongitudeOf(Object, Structure, _Value)) -> true;
	assert(isOnLongitudeOf(Object, Structure, CurrentLongitudeValue)).
*/