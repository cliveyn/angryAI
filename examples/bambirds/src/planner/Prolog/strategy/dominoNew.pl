

ptestDominoStrategy() :-
	doInferencialGroundingSeIG(),
	savePlansForDominoStDN().

%%savePlansForDominoStDN
% savePlansForDominoStDN()	
savePlansForDominoStDN() :-
	psysoStDN('DominoNew','Started'),
	pfindStructuresForDominoStDN(StructList),
	(
		(
			StructList == []
		) ->
		(
			psysoStDN('No Domino in','Map')
		);
		(
			psavePlansForDominoStDN(StructList)
		)
	).

% pfindStructuresForDominoStDN(-StructureList)
% returns a List with potential relevant domino structures
pfindStructuresForDominoStDN(StructList) :-
	findall(
		Struct,
		(
			structure(Struct),
			canCollapse(Struct,_NextStruct),
			minHeightForDomino(MinHeight),
			totalHeightOfStructure(Struct,StructHeight),
			MinHeight =< StructHeight
		),
		TmpStructList
	),
	sort(TmpStructList,StructList).

% psavePlansforDominoStDN(+StructList)
% saves Plans for potential relevant Domino Structures
psavePlansForDominoStDN(StructList) :-
	psysoStDN(structList,StructList),
	pfindDominoCounterStDN(StructList,RankedStructList),
	psysoStDN(rankedStructList,RankedStructList),
	psavePlansForRelevantDominoStDN(RankedStructList).

% pfindDominoCounterStDN(+StructList,-RankedStructList)
% gets a List with potential relevant domino structures
% returns a List with domino structures, directions and Ranking values 
% Form: [[Counter,Struct,Direction]...]
pfindDominoCounterStDN([],[]) :- !.
pfindDominoCounterStDN([Struct|Tail],RankedStructList) :-
	pfindCollapsablesInDirectionStDN([Struct],away,AwayList),
	pdeleteNotRelevantStructuresStDN(AwayList,RelevantAwayList),
	length(RelevantAwayList,AwayCounter),
	AwayObject = [AwayCounter,Struct,away],
	pfindCollapsablesInDirectionStDN([Struct],towards,TowardsList),
	pdeleteNotRelevantStructuresStDN(TowardsList,RelevantTowardsList),
	length(RelevantTowardsList,TowardsCounter),
	TowardsObject = [TowardsCounter,Struct,towards],
	append([AwayObject],[TowardsObject],TmpList),
	pfindDominoCounterStDN(Tail,NextList),
	append(TmpList,NextList,FinalList),
	kwikeSortInverse(FinalList,RankedStructList).

% pdeleteNotRelevantStructuresStDN(+StructList,-RelevantStructList)
% gets a List with Structures
% returns a List without not relevant Structures
pdeleteNotRelevantStructuresStDN([],[]) :- !.
pdeleteNotRelevantStructuresStDN([Struct|Tail],OutputList) :-
	findall(
		Relevant,
		containsRelevant(Struct,Relevant),
		RelevantList
	),
	(
		(
			RelevantList == []
		) ->
		(
			ThisElement = []
		);
		(
			ThisElement = [Struct]
		)
	),
	pdeleteNotRelevantStructuresStDN(Tail,NextStruct),
	append(ThisElement,NextStruct,OutputList).

% pfindCollapsablesInDirectionStDN(+StructList,+Direction,-StructList)
% gets a List of Structures
% gets a direction
% returns a List with all collapsable Structures in a direction
pfindCollapsablesInDirectionStDN([],_Direction,[]) :- !.
pfindCollapsablesInDirectionStDN([Struct|Tail],Direction,OutputList) :-
	findall(
		Collapsable,
		(
			canCollapse(Struct,Collapsable),
			collapsesInDirection(Struct,Collapsable,Direction)
		),
		CollapsableList
	),
	pfindCollapsablesInDirectionStDN(CollapsableList,Direction,ThisList),
	pfindCollapsablesInDirectionStDN(Tail,Direction,NextList),
	append(NextList,ThisList,TmpList),
	append(CollapsableList,TmpList,OutputList).

% psavePlansForRelevantDominoStDN(+StructList)
% gets a List with domino Structures
% saves Plans for each relevant domino Structures
% ignores Structure that cannot collapse relevant Structures
psavePlansForRelevantDominoStDN([]) :- !.
psavePlansForRelevantDominoStDN([StructObject|Tail])	:-
	nth0(0,StructObject,RelevantCollapsableCounter),
	nth0(1,StructObject,Struct),
	nth0(2,StructObject,Direction),
	(
		(
			RelevantCollapsableCounter == 0
		) ->
		(
			psysoStDN('Cannot Collapse Relevant Structures',Struct)
		);
		(
			psavePlanForReleventStructureStDN(Struct,Direction,RelevantCollapsableCounter)
		)
	),
	psavePlansForRelevantDominoStDN(Tail).

% psavePlanForReleventStructureStDN(+Struct,+Direction,+Counter)
% gets a Structure
% gets a Direction
% gets a Counter
% saves plans to collapse Structure in given direction
psavePlanForReleventStructureStDN(Struct,Direction,Counter) :-
	findTargetsToCollapseStructureSeCS(Struct,Direction,TargetObjects),
	(
		(
			TargetObjects == []
		) ->
		(
			psysoStDN('No Way To Collapse',Struct)
		);
		(
			pfindGoalsForStructureStDN(Struct,Direction,Goals),
			psavePlansForRelevantStructureWithTargetsStDN(TargetObjects,Counter,Goals,Direction)
		)
	).

pfindGoalsForStructureStDN(Struct,Direction,Goals) :-
	findall(
		Relevant,
		(
			collapsesInDirection(Struct,NextStruct,Direction),
			containsRelevant(NextStruct,Relevant),
			(
				pig(Relevant);
				hasMaterial(Relevant,tnt)
			)
		),
		RelevantsList
	),
	sort(RelevantsList,TmpGoals),
	(
		(
			TmpGoals == []
		) ->
		(
			findall(
				Object,
				(
					collapsesInDirection(Struct,NextStruct,Direction),
					object(Object),
					belongsTo(Object,Struct)
				),
				ObjectList
			),
			sort(ObjectList,Goals)
		);
		(
			Goals = TmpGoals
		)
	).

% psavePlansForRelevantStructureWithTargets(+Struct,+TargetObjectList,+Counter,+Goals)
% gets a structure
% gets a List with target objects
% gets a counter
% gets a goal list
psavePlansForRelevantStructureWithTargetsStDN([],_Couter,_Goals,_Direction) :- !.
psavePlansForRelevantStructureWithTargetsStDN([TargetObject|Tail],Counter,Goals,Direction) :-
	nth0(1,TargetObject,Target),
	getEfficiencyValueSeDH(Target,EffValue),
	(
		(
			(EffValue > 1),
			(Direction == towards)
		) ->
		(
			psavePlansForRelevantStructureWithTargetsStDN(Tail,Counter,Goals,Direction)
		);
		(
			psavePlanStDN([Target],Goals,Counter,Direction)
		)
	).
%	psavePlansForRelevantStructureWithTargesStDN(Tail,Counter,Goals).

psavePlanStDN(Targets,Goals,Counter,Direction) :-
	pgetPlanRankStDN(Targets,Goals,Counter,Direction,Rank),
	savePlan(Targets,Goals,domino,Rank).

pgetPlanRankStDN(Targets,_Goals,Counter,Direction,Rank) :-
	nth0(0,Targets,Target),
	belongsTo(Target,Struct),!,
	pgetInvolvedPigsSeDH(Struct,Direction,SecValue),
	pgetRelativeStructValueSeDH(Counter,ThiValue),
	pgetObjectValueSeDH(Struct,FourValue),
	getPlanRankSeDH(Counter,SecValue,ThiValue,FourValue,domino,Rank).

pgetRelativeStructValueSeDH(TmpCounter,RelativeStructCounter) :-
	Counter is (TmpCounter + 1),
	findall(
		Struct,
		structure(Struct),
		StructList
	),
	length(StructList,AllStructs),
	RelativeStructCounter is ((Counter * 100) / AllStructs).

pgetObjectValueSeDH(Struct,ObjectValue) :-
	findall(
		Object,
		belongsTo(Object,Struct),
		ObjectList
	),
	length(ObjectList,ObjectValue).
	
pgetInvolvedPigsSeDH(Struct,Direction,PigCounter) :-
	findall(
		Value,
		(
			(
				containsRelevant(Struct,Relevant);
				(
					canCollapse(Struct,NextStruct),
					collapsesInDirection(Struct,NextStruct,Direction),
					containsRelevant(NextStruct,Relevant)
				)
			),
			(
				(
					pig(Relevant)
				) ->
				(
					Value = 1
				);
				(
					(
						hasMaterial(Relevant,tnt)
					) ->
					(
						Value = 2
					);
					(
						Value = 0
					)
				)
			)
		),
		ValueList
	),
	pgetAdditionValueStDN(ValueList,TmpPigCounter),
	(
		(
			TmpPigCounter == 0
		) ->
		(
			PigCounter is 1
		);
		(
			PigCounter = TmpPigCounter
		)
	).
	
pgetAdditionValueStDN([],0) :- !.
pgetAdditionValueStDN([Head|Tail],Value) :-
	pgetAdditionValueStDN(Tail,NextValue),
	Value is (Head + NextValue).
	

%psavePlansForDominoStDN(InputList) :-
%	pfindNewCollapsableLsitStDN(InputList,StructList),
%	psysoStDN('test',StructList).
%	
%pfindNewCollapsableLsitStDN([],[]) :- !.
%pfindNewCollapsableLsitStDN([Struct|_Tail],StructList) :-
%	pfindAllCollapsableStructuresStDN(Struct,_NumberOfCollapsables),
%	StructList = [].
%	
%
%pfindAllCollapsableStructuresStDN(Struct,NumberOfCollapsables) :-
%	findall(
%		Collapsable,
%		(
%			canCollapse(Struct,NextStruct),
%			collapsesInDirection(Struct,NextStruct,Direction),
%			Collapsable = [NextStruct,Direction]
%		),
%		TmpCollapsableList
%	),
%	(
%		(
%			TmpCollapsableList == []
%		) ->
%		(
%			NumberOfCollapsables = 0
%		);
%		(
%			NumberOfCollapsables = 1
%		)
%	).
%	


	
	
psysoStDN(Tag,TextOne,TextTwo) :-
	(
		(Tag == d) ->
			string_concat('[DominoNew][Debug] ',TextOne,Text);
			string_concat('[DominoNew][Sonstiges] ',TextOne,Text) 
	),
	pAlexOutput(Text,TextTwo),
	true.
psysoStDN(TextOne,TextTwo) :-
	string_concat('[DominoNew] ',TextOne,Text),
	pAlexOutput(Text,TextTwo),
	true.