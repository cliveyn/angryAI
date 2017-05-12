
ptestWhiteBirdStrategyStWB :-
	doInferencialGroundingSeIG(),
	savePlansForWhiteBirdStWB().
	
savePlansForWhiteBirdStWB() :-
	birdOrder(Bird,0),
	(
		(
			hasColor(Bird,white)
		) ->
		(
			psavePlansForWhiteBirdStWB(),!
		);
		(
			psysoStWB('No','White Bird')
		)
	).
	
% psavePlansForWhiteBirdStWB()
psavePlansForWhiteBirdStWB() :-
	pfindAllRelevantsInMapStWB(RelevantObjects),
	(
		(RelevantObjects == []) ->
		(
			psysoStWB('No','Relevant Objects')
		);
		(
			psavePlansForEachRelevantStWB(RelevantObjects)
		)
	).

% pfindAllRelevantsInMapStWB(-RelevantObjectList)
pfindAllRelevantsInMapStWB(RelevantObjects) :-
	findall(
		Object,
		(
			hasMaterial(Object,tnt);
			pig(Object);
			(
				hasSize(Object,big),
				not(hasForm(Object,bar))
			)
		),
		ObjectList
	),
	sort(ObjectList,RelevantObjects).

% psavePlansForEachRelevantStWB(+RelevantObjectList)
psavePlansForEachRelevantStWB([]) :- !.
psavePlansForEachRelevantStWB([RelevantObject|Tail]) :-
	pfindElementsOnObjectStWB(RelevantObject,ObjectsOnRelevant),
	(
		(
			ObjectsOnRelevant == []
		) ->
		(
			psavePlansForFreeObjectsStWB(RelevantObject)
		);
		(
			psavePlansForNotFreeObjectsStWB(RelevantObject,ObjectsOnRelevant)
		)
	),
	psysoStWB(d,RelevantObject,Tail),
	psavePlansForEachRelevantStWB(Tail),
	psysoStWB(d,'Not','Here').

% psavePlansForFreeObjectsStWB
psavePlansForFreeObjectsStWB(RelevantObject) :-
	pfindProtectingObjectsStWB(RelevantObject,ProtectingObjects),
	(
		(
			ProtectingObjects == []
		) ->
		(
			psavePlanStWB(RelevantObject)
		);
		(
			psysoStWB('Not Reachable',RelevantObject)
		)
	).

psavePlansForNotFreeObjectsStWB(_RelevantObject,[]) :- !.
psavePlansForNotFreeObjectsStWB(RelevantObject,[ObjectOnRelevant|Tail]) :-
	pfindElementsOnObjectStWB(ObjectOnRelevant,OtherObjects),
	(
		(
			OtherObjects == []
		) ->
		(
			psavePlansForObjectsWithTargetStWB(RelevantObject,ObjectOnRelevant)
		);
		(
			psysoStWB('Not reachable for Egg',RelevantObject)
		)
	),
	psavePlansForNotFreeObjectsStWB(RelevantObject,Tail).

psavePlansForObjectsWithTargetStWB(RelevantObject,ObjectOnRelevant) :-
	pfindProtectingObjectsStWB(ObjectOnRelevant,ProtectingObjects),
	(
		(
			ProtectingObjects == []
		) ->
		(
			psavePlanStWB(RelevantObject,ObjectOnRelevant)
		);
		(
			psysoStWB('Protected',RelevantObject)
		)
	).

pfindProtectingObjectsStWB(RelevantObject,ProtectingObjects) :-
	findall(
		Protecting,
		(
			isOver(RelevantObject,Protecting);
			protects(Protecting,RelevantObject)
		),
		UnsortedProtectingObjects
	),
	sort(UnsortedProtectingObjects,ProtectingObjects).

pfindElementsOnObjectStWB(RelevantObject,ObjectsOnRelevant) :-
	findall(
		Object,
		isOn(Object,RelevantObject),
		ObjectList
	),
	sort(ObjectList,ObjectsOnRelevant).

psavePlanStWB(RelevantObject,Target) :-
	pgetPlanRankStWB(RelevantObject,Target,Rank),
	savePlan([Target],[RelevantObject],whiteBird,Rank).
	
psavePlanStWB(RelevantObject) :-
	pgetPlanRankStWB(RelevantObject,Rank),
	savePlan([RelevantObject],[RelevantObject],whiteBird,Rank).

pgetPlanRankStWB(RelevantObject,Target,Rank) :-
	pgetTargetValueStWB(RelevantObject,TargetValue),
	pgetValueTwoStWB(RelevantObject,ValueTwo),
	pgetValueThreeStWB(Target,ValueThree),
	ValueFour is 1,
	getPlanRankSeDH(TargetValue,ValueTwo,ValueThree,ValueFour,whiteBird,Rank).
	

pgetPlanRankStWB(RelevantObject,Rank) :-
	pgetTargetValueStWB(RelevantObject,TargetValue),
	pgetValueTwoStWB(RelevantObject,ValueTwo),
	ValueThree is 99,
	ValueFour is 1,
	getPlanRankSeDH(TargetValue,ValueTwo,ValueThree,ValueFour,whiteBird,Rank).
	
	
pgetValueTwoStWB(RelevantObject,Value) :-
	(
		(
			hasMaterial(RelevantObject,tnt)
		) ->
		(
			pgetExplodablesValueStWB(RelevantObject,Value)
		);
		(
			(
				pig(RelevantObject)
			) ->
			(
				Value is 10
			);
			(
				Value is 1
			)
		)
	).

pgetValueThreeStWB(Target,Value) :-
	(
		(
			hasMaterial(Target,Material),
			hasForm(Target,Form),
			hasSize(Target,Size)
		) ->
		(
			efficiencySizeForm(Size,Form,Material,EffValue),
			Value = (100 - (EffValue * 10))
		);
		(
			psysoStWB('Target is strage',Target),
			Value is 50
		)
	).

pgetExplodablesValueStWB(RelevantObject,Value) :-
	findall(
		Explodable,
		canExplode(RelevantObject,Explodable),
		Explodables
	),
	sort(Explodables,SortedList),
	length(SortedList,Value).

pgetTargetValueStWB(RelevantObject,Rank) :-
	(
		(
			pig(RelevantObject)
		) ->
		(
			Rank is 2
		);
		(
			(
				(
					hasMaterial(RelevantObject,tnt)
				) ->
				(
					Rank is 3
				);
				(
					Rank is 1
				)
			)
		)
	).

psysoStWB(_Tag,_TextOne,_TextTwo) :-
%	(
%		(Tag == d) ->
%			string_concat('[WhiteBird][Debug] ',TextOne,Text);
%			string_concat('[WhiteBird][Sonstiges] ',TextOne,Text) 
%	),
%	pAlexOutput(Text,TextTwo),
	true.
psysoStWB(TextOne,TextTwo) :-
	string_concat('[WhiteBird] ',TextOne,Text),
	pAlexOutput(Text,TextTwo),
	true.

