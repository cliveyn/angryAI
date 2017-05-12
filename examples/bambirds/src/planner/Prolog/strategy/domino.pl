%TODO give more targets for blue bird
%TODO if a structure protects a pig, return no plan - is implemented, but not tested

%%findDominoStrategyStDo/0
% findDominoStrategyStDo()
% this method finds all applicable domino strategies
% where at least one structure can collapse another one
% and where at least one pig is in one of these structures
% Then it saves all plans, so they can be outputted
findDominoStrategyStDo() :-
	findall(
		Structure,
		(
			structure(Structure),
			canCollapse(Structure, _OtherStructure)
		),
		List),
	length(List, ListLength),
	((ListLength > 0) ->
		pFindDominoStrategyInDirectionStDo(away, List, AwayList),
		pSysoStDo(away, AwayList),
		pFindDominoStrategyInDirectionStDo(towards, List, TowardsList),
		pSysoStDo(towards, TowardsList),
		pRetractDominoStDo,
		length(AwayList,AwayListLength),
		((AwayListLength > 0) ->
			pSysoStDo(AwayList, 'schrubbAway'),
			pAssertAllReactionsStDo(AwayList, away);
			true),
		length(TowardsList,TowardsListLength),
		((TowardsListLength > 0) ->
			pSysoStDo(TowardsList,'schrubbTowards'),
			pAssertAllReactionsStDo(TowardsList, towards);
			true),
		pCreatePlansStDo
		;
		true),
		pRetractDominoStDo.

% pCreatePlansStDo()
% this method starts the planfinding for all chainreactions
pCreatePlansStDo() :-
	findall(
		ListEntry,
		(
			dominoChainReactions(StructuresInReaction, Direction, RankingValue),
			ListEntry = [RankingValue, Direction, StructuresInReaction]
		),
		List),
	%removeDuplicates
	sort(List, SortedList),
	kwikeSortInverse(SortedList, RankedList),
	pSysoStDo('Ranked', RankedList),
	pCreatePlansForEachReactionStDo(RankedList).

% pCreatePlansForEachReactionStDo(+List)
% this method finds the needed values for plans, the goals and the targets
pCreatePlansForEachReactionStDo([]) :- !.
pCreatePlansForEachReactionStDo(List) :-
	splitListIn(List, [RankingValue, Direction, StructuresInReaction], RestOfList),
	pCreatePlansForEachReactionStDo(RestOfList),
	% get first structure, so we can create hittablesList
	splitListIn(StructuresInReaction, FirstStructure, _Rest),
	findAllPigsInStructure(FirstStructure, CheckForPigs),
	length(CheckForPigs, PlanCanBeUsed),
	((PlanCanBeUsed \== 0) ->
		findHittablesForCollapseSortedByMinHeightSeQS(FirstStructure, Direction, SortedHittablesList),
		pSysoStDo(SortedHittablesList, StructuresInReaction),
		findAllPigsInStructureList(StructuresInReaction, PigList),
		length(SortedHittablesList, Length),
		((Length \== 0 ) ->
			pSavePlansForEachHittableStDo(SortedHittablesList, RankingValue, PigList)
			;
			true)
		;
		true).

% pSavePlansForEachHittableStDo(+HittablesList, +RankingValue, +PigList)
% this method actually saves the plans for each hittable
pSavePlansForEachHittableStDo(HittablesList, RankingValue, PigList) :-
	pSysoStDo('Plan', HittablesList),
	pSysoStDo(RankingValue, PigList),
	length(HittablesList, HittablesListLength),
	((HittablesListLength > 0) ->
		splitListIn(HittablesList, Hittable, RestOfHittablesList),
		pSavePlansForEachHittableStDo(RestOfHittablesList, RankingValue, PigList),
		Target = [Hittable],
		Goal = PigList,
		Rank is RankingValue + HittablesListLength,
		savePlan(Target, Goal, domino, Rank)
		;
		true).

% pAssertAllReactionsStDo(+ReactionsList, +Direction)
% asserts all chainReactions with Direction and a RankingValue
pAssertAllReactionsStDo([], _Direction) :- !.
pAssertAllReactionsStDo(ReactionsList, Direction) :-
	splitListIn(ReactionsList,FirstReaction,RestOfReactionsList),
	splitListIn(FirstReaction, PigCount, StructuresInFirstReaction),
	splitListIn(StructuresInFirstReaction, FirstStructure, _RestOfStructures),
	length(StructuresInFirstReaction, StructureCount),
	totalHeightOfStructure(FirstStructure, HeightOfFirstStructure),
	
	%rank Reactions by PigCount, StructureCount, HeightOfFirstStructure
	%as hittables get evaluated as well, we need a small part where we can still sort
	X is 0,
	getPlanRankSeDH(PigCount, StructureCount, HeightOfFirstStructure, X, domino, RankValue),
	%rankingFactorForStrategies(RankingValue),
	%RankValue is (PigCount*(RankingValue^3))+(StructureCount*(RankingValue^2))+(HeightOfFirstStructure*RankingValue),
	pSysoStDo('RankValue', RankValue),
	
	%assert all Reactions with RankingValue
	assertz(dominoChainReactions(StructuresInFirstReaction, Direction, RankValue)),
	pAssertAllReactionsStDo(RestOfReactionsList, Direction).

% pRetractDominoStDo
% retracts all dominoChainReactions that have been asserted before
pRetractDominoStDo():-
	retractall(dominoChainReactions(_A, _B, _C)).
	
% pFindDominoStrategyInDirectionStDo(+Direction, +List, -StrategyPartList)
% finds all reactions that contain pig(s) and retruns a list containg those strategies
pFindDominoStrategyInDirectionStDo(Direction, List, StrategyPartList) :-
	pFindDominoChainReactionsStDo(List, Direction, ChainReactionsList),
	sort(ChainReactionsList,CleanChainReactionsList),
	pSysoStDo(chacka, CleanChainReactionsList),
	pfilterChainReactionsProtectingPigsStDo(CleanChainReactionsList, ChainTempPigList),
	pSysoStDo('NoProtectedReactions', ChainTempPigList),
	pfilterChainReactionsWithoutPigsStDo(ChainTempPigList, ChainPigList),
	pSysoStDo('PigReactions', ChainPigList),
	psortChainPigListByPigCountStDo(ChainPigList, SortedChainPigList),
	pSysoStDo('SortedPigList', SortedChainPigList),
	StrategyPartList = SortedChainPigList.

% pFindDominoChainReactionsStDo(+StructureList, +Direction, -ChainReactionsList)
% returns all chainreactions with structures that can be collapsed directly and indirectly by structures in a direction
pFindDominoChainReactionsStDo([], _Direction, []) :- !.
pFindDominoChainReactionsStDo(StructureList, Direction, ChainReactionsList):-
	splitListIn(StructureList, Structure, RestOfStructureList),
	pFindDominoChainReactionsStDo(RestOfStructureList, Direction, TempChainReactionsList),
	pFindDominoChainReactionForStructureStDo(Structure, Direction, ChainListForStructure),
	(([Structure] == ChainListForStructure) ->
		ChainReactionsList = TempChainReactionsList;
		append([ChainListForStructure],TempChainReactionsList,ChainReactionsList)).

% pFindDominoChainReactionForStructureStDo(+Structure, +Direction, -List)
% returns the structures that can be collapsed directly and indirectly by one structure in a direction
pFindDominoChainReactionForStructureStDo(Structure, Direction, List):-
	((canCollapse(Structure, NextStructure),
	collapsesInDirection(Structure, NextStructure, Direction))->
		pFindDominoChainReactionForStructureStDo(NextStructure, Direction, RecursiveList),
		append([Structure], RecursiveList, List);
		List = [Structure]).

% pfilterChainReactionsWithoutPigsStDo(+ChainReactionsList, -ChainPigList)
% only returns chain reactions with more than 1 pig
pfilterChainReactionsWithoutPigsStDo([], []) :- !.
pfilterChainReactionsWithoutPigsStDo(ChainReactionsList, ChainPigList):-
	splitListIn(ChainReactionsList, ChainReaction, RestOfChainReactionsList),
	pfilterChainReactionsWithoutPigsStDo(RestOfChainReactionsList, RestOfPigList),
	pgetPigCountForEachStructureStDo(ChainReaction, PigCount),
	%pSysoStDo(ChainReaction, PigCount),
	((PigCount \== 0) ->
		%pSysoStDo([PigCount|ChainReaction], RestOfPigList),
		append([[PigCount|ChainReaction]],RestOfPigList, ChainPigList)
		;
		ChainPigList = RestOfPigList).

% pgetPigCountForEachStructureStDo(+ChainReaction, -PigCount)
% gives count of pigs in the structures in a chain reaction
pgetPigCountForEachStructureStDo([],0) :- !.
pgetPigCountForEachStructureStDo(ChainReaction, PigCount):-
	splitListIn(ChainReaction, Structure, RestOfChainReaction),
	pgetPigCountForEachStructureStDo(RestOfChainReaction, RestPigCount),
	findall(
			Pigs,
			(
				pig(Pigs),
				belongsTo(Pigs, Structure)
			),
			PigList
	),
	length(PigList, Int),
	PigCount is (Int + RestPigCount).

% pfilterChainReactionsProtectingPigsStDo(+ChainReactionsList, -ChainPigList)
% only returns chain reactions protecting no pigd
pfilterChainReactionsProtectingPigsStDo([], []) :- !.
pfilterChainReactionsProtectingPigsStDo(ChainReactionsList, ChainPigList):-
	splitListIn(ChainReactionsList, ChainReaction, RestOfChainReactionsList),
	pfilterChainReactionsProtectingPigsStDo(RestOfChainReactionsList, RestProtectionList),
	%pSysoStDo(chain, ChainReaction),pfilterChainReactionsProtectingPigsStDo(RestOfChainReactionsList, RestOfPigList),
	pgetProtectionCountForEachStructureStDo(ChainReaction, ProtectionCount),
	%pSysoStDo(ChainReaction, ProtectionCount),
	((ProtectionCount == 0) ->
		%pSysoStDo([PigCount|ChainReaction], RestOfPigList),
		append([ChainReaction],RestProtectionList, ChainPigList);
		ChainPigList = RestProtectionList).

% pgetProtectionCountForEachStructureStDo(+ChainReaction, -PigCount)
% gives count of protected pigs in the structures in a chain reaction
pgetProtectionCountForEachStructureStDo([],0) :- !.
pgetProtectionCountForEachStructureStDo(ChainReaction, PigCount):-
	splitListIn(ChainReaction, Structure, RestOfChainReaction),
	pgetProtectionCountForEachStructureStDo(RestOfChainReaction, RestPigCount),
	findall(
			Pigs,
			(
				pig(Pigs),
				protects(Structure, Pigs)
			),
			PigList
	),
	length(PigList, Int),
	PigCount is (Int + RestPigCount).

% psortChainPigListByPigCountStDo(+ChainPigList, -SortedChainPigList)
% sorts the chainreactions, those with most pigs come first
psortChainPigListByPigCountStDo([],[]) :- !.
psortChainPigListByPigCountStDo(ChainPigList, SortedChainPigList):-
	kwikeSortInverse(ChainPigList, SortedChainPigList).

pSysoStDo(_Text, _Value) :- %syso(_Text, _Value),
	true.