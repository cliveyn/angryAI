%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%	The Quadrants:			%	Towards Order:			%	Away Order:				%
%	2 1						%	  2						%	1						%
%	3 4						%	1						%	  2						%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%%findHittablesForCollapseSortedByMinHeightSeQs/3
% findHittablesForCollapseSortedByMinHeightSeQs(+Structure, +Direction, -SortedHittablesList)
% gives all hittables in a structure to help for collapsing in a direction sorted by height
% minimum height first for towards
% minimum height for q2 first, then for q4, as wished shooting order is different
findHittablesForCollapseSortedByMinHeightSeQS(Structure, Direction, SortedHittablesList) :-
	(	(Direction == away) ->
		findHittablesForCollapseAwaySeQS(Structure, QuadrantTwoList, _QuadrantFourList),
		pSortHittablesListByLowestHeightSeQS(QuadrantTwoList, SortedHittablesList)
		%pSortHittablesListByLowestHeightSeQS(QuadrantFourList, SortedQuadrantFourList),
		%append(SortedQuadrantTwoList, SortedQuadrantFourList, WDSortedHittablesList),
		%sort(WDSortedHittablesList, SortedHittablesList)
		;
		(	(Direction == towards) ->
			findHittablesForCollapseTowardsSeQS(Structure, QuadrantThreeList, _QuadrantOneList),
			pSortHittablesListByLowestHeightSeQS(QuadrantThreeList, SortedHittablesList)
			%syso('SortedHittableList', SortedHittablesList)
			;
			true
		)
	).

%%findHittablesForCollapseTowardsSeQS/2
% findHittablesForCollapseTowardsSeQS(-Structure,+List)
% gives all hittables in a structure to help for collapsing towards
findHittablesForCollapseTowardsSeQS(Structure, List):-
	findHittablesInQuadrantSeQS(Structure, 3, QuadrantThreeList),
	findHittablesInQuadrantSeQS(Structure, 1, QuadrantOneList),
	append(QuadrantThreeList,QuadrantOneList,List).
% 2 Lists
findHittablesForCollapseTowardsSeQS(Structure, QuadrantThreeList, QuadrantOneList):-
	findHittablesInQuadrantSeQS(Structure, 3, QuadrantThreeList),
	findHittablesInQuadrantSeQS(Structure, 1, QuadrantOneList).

%%findHittablesForCollapseAwaySeQS/2
% findHittablesForCollapseAwaySeQS(-Structure,+List)
% gives all hittables in a structure to help for collapsing away
findHittablesForCollapseAwaySeQS(Structure, List):-
	findHittablesInQuadrantSeQS(Structure, 2, QuadrantTwoList),
	findHittablesInQuadrantSeQS(Structure, 4, QuadrantFourList),
	append(QuadrantTwoList,QuadrantFourList,List).
% 2 Lists
findHittablesForCollapseAwaySeQS(Structure, QuadrantTwoList, QuadrantFourList):-
	findHittablesInQuadrantSeQS(Structure, 2, QuadrantTwoList),
	findHittablesInQuadrantSeQS(Structure, 4, QuadrantFourList).
	
%%findHittablesInQuadrantSeQS/2
% findHittablesInQuadrantSeQS(-Structure,+WasSuccessful)
% assigns each hittable in a structure
findHittablesInQuadrantSeQS(Structure, Quadrant, List):-
	pCleanHittableLocationsSeQS,
	findall(
		Object,
		(
			object(Object),
			isHittable(Object, true),
			belongsTo(Object, Structure)
		),
		HittableListInStructure),
	% remove duplicates
	sort(HittableListInStructure, DRHittableListInStructure),
	%syso('HittableListInStructure',DRHittableListInStructure),
	pAssignEachObjectToVerticalHalfInStructureSeQS(DRHittableListInStructure),
	pAssignEachObjectToHorizontalHalfInStructureSeQS(DRHittableListInStructure),
		findall(
			QuadrantObject,
			(
				((Quadrant == 1) ->
					isHittableAndRight(QuadrantObject),
					isHittableAndTop(QuadrantObject)
%					,syso(QuadrantObject, 'assignedQ1')
					;
					((Quadrant == 2) ->
						isHittableAndLeft(QuadrantObject),
						isHittableAndTop(QuadrantObject)
%						,syso(QuadrantObject, 'assignedQ2')
						;
						((Quadrant == 3) ->
							isHittableAndLeft(QuadrantObject),
							isHittableAndDown(QuadrantObject)
%							,syso(QuadrantObject, 'assignedQ3')
							;
							((Quadrant == 4) ->
								isHittableAndRight(QuadrantObject),
								isHittableAndDown(QuadrantObject)
%								,syso(QuadrantObject, 'assignedQ4')
								;
								false
			))))),
			List),
	%syso('HittableListInRequestedQuadrant', List),
	pCleanHittableLocationsSeQS.

% pAssignEachObjectToVerticalHalfInStructureSeQS(+ObjectList)
% assigns each given object in list to either left or right half
pAssignEachObjectToVerticalHalfInStructureSeQS([]) :- !.
pAssignEachObjectToVerticalHalfInStructureSeQS(ObjectList) :-
	splitListIn(ObjectList, Object, RestOfObjectList),
	((pAssignToLeftHalfSeQS(Object, true))->
%		syso('assignedLeft', Object),
		asserta(isHittableAndLeft(Object));true),
	((pAssignToRightHalfSeQS(Object, true)) ->
%		syso('assignedRight', Object),
		assertz(isHittableAndRight(Object));true),
	((pAssignToLeftHalfSeQS(Object, false), pAssignToRightHalfSeQS(Object, false)) ->
%		syso('assignedLeftAndRight', Object),
		asserta(isHittableAndLeft(Object)),
		assertz(isHittableAndRight(Object)); true),
	pAssignEachObjectToVerticalHalfInStructureSeQS(RestOfObjectList).
	
pAssignToLeftHalfSeQS(Object, Possible):-
	((isLeft(Object,_ObjectToTheRight), not(isLeft(_ObjectToTheLeft,Object)))->
		Possible = true; Possible = false).

pAssignToRightHalfSeQS(Object, Possible):-
	((isRight(Object,_ObjectToTheLeft), not(isRight(_ObjectToTheRight,Object)))->
		Possible = true; Possible = false).

% pAssignEachObjectToHorizontalHalfInStructureSeQS(+ObjectList)
% assigns each given object in list to either top or down half
pAssignEachObjectToHorizontalHalfInStructureSeQS([]) :- !.
pAssignEachObjectToHorizontalHalfInStructureSeQS(ObjectList) :-
	splitListIn(ObjectList, Object, RestOfObjectList),
	pAssignToHorizontalHalfSeQS(Object),
	pAssignEachObjectToHorizontalHalfInStructureSeQS(RestOfObjectList).

pAssignToHorizontalHalfSeQS(Object):-
	belongsTo(Object,Structure),
	totalHeightOfStructure(Structure,HeightOfStructure),
	((HeightOfStructure == 0) ->
		true;
		hasTotalHeight(Object, HeightOfObject),
		((HeightOfObject =< (round(HeightOfStructure/2))) ->
%			syso('assignedTop', Object),
			asserta(isHittableAndTop(Object));true),
		((HeightOfObject >= (round(HeightOfStructure/2))) ->
%			syso('assignedDown', Object),
			assertz(isHittableAndDown(Object));true)).

pCleanHittableLocationsSeQS():-
	retractall(isHittableAndLeft(_ObjectHitAndLeft)),
	retractall(isHittableAndRight(_ObjectHitAndRight)),
	retractall(isHittableAndTop(_ObjectHitAndTop)),
	retractall(isHittableAndDown(_ObjectHitAndDown)).

% pSortHittablesListByLowestHeightSeQS(+HittablesList, -SortedHittablesList)
% sorts given hittables by height, lowest first
pSortHittablesListByLowestHeightSeQS([],[]) :- !.
pSortHittablesListByLowestHeightSeQS(HittablesList, SortedHittablesList) :-
	% remove duplicates
	sort(HittablesList, DRHittablesList),
	pAddHeightValuesToHittablesListSeQS(DRHittablesList, HeightHittablesList),
	kwikeSortInverse(HeightHittablesList, SortedHeightHittablesList),
	pDeleteHeightValuesOfHittablesListSeQS(SortedHeightHittablesList, SortedHittablesList).

% pDeleteHeightValuesOfHittablesListSeQS(+HeightHittablesList, -HittablesList)
% deletes temporary height values
pDeleteHeightValuesOfHittablesListSeQS([],[]) :- !.
pDeleteHeightValuesOfHittablesListSeQS(HeightHittablesList, HittablesList) :-
	splitListIn(HeightHittablesList, [_Height, Hittable], RestOfHeightHittablesList),
	pDeleteHeightValuesOfHittablesListSeQS(RestOfHeightHittablesList, RestOfHittablesList),
	append([Hittable], RestOfHittablesList, HittablesList).

% pAddHeightValuesToHittablesListSeQS(+HittablesList, -HeightHittablesList)
% adds temporarily height values to the hittables to sort them
pAddHeightValuesToHittablesListSeQS([],[]) :- !.
pAddHeightValuesToHittablesListSeQS(HittablesList, HeightHittablesList) :-
	splitListIn(HittablesList, FirstHittable, RestOfHittablesList),
	pAddHeightValuesToHittablesListSeQS(RestOfHittablesList, RestOfHeightHittablesList),
	hasTotalHeight(FirstHittable, Height),
	HittableWithHeight = [Height, FirstHittable],
	append([HittableWithHeight], RestOfHeightHittablesList, HeightHittablesList).