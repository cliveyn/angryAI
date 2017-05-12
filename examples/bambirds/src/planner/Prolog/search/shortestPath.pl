
%%findShortestPathsSeSP/4
% findShortestPathsSeSP(+Object,-ObjectList)
% find the shortest path through an structure to the given object
findShortestPathsSeSP(HittableObjectList,GoalObjectList,OutputList) :-
	nth0(0,GoalObjectList,GoalObject,NextList),
	(member(GoalObject,HittableObjectList) ->
		PathList = GoalObject;
		writeln(GoalObject),
		findPathRecursively(GoalObject,HittableObjectList,PathList)
	),
	findShortestPathsSeSP(HittableObjectList,NextList,TmpList),
	append(TmpList,PathList,OutputList).
		
findPathRecursively(GoalObject,HittableObjectList,PathList) :-
	((isOn(TopObject,GoalObject)) ->
		(member(TopObject,HittableObjectList) ->
			TopList = TopObject;
			findPathRecursively(TopObject,HittableObjectList,TopTempList),
			append(TopTempList,TopObject,TopList),
			writeln(TopList),
			writeln(TopObject)
		);
		TopList =[]
	),
	((isLeft(LeftObject,GoalObject)) ->
		(member(LeftObject,HittableObjectList) ->
			LeftList = LeftObject;
			findPathRecursively(LeftObject,HittableObjectList,LeftTempList),
			append(LeftTempList,LeftObject,LeftList)
		);
		LeftList = []
	),
	append([],LeftList,TmpList),
	append(TmpList,TopList,BlubberList),
	writeln(BlubberList),
	writeln(LeftList),
	append(BlubberList,[],PathList).
	
%%findHittablesBetweenTwoAnglesSeSP/6
%findHittablesBetweenTwoAnglesSeSP(+Breadth1, +Height1, +Breadth2, +Height2, +RootList, -HittablesList)
%HittablesList is a List of Lists containing shortest Path between Hittables and the given rootList.
%All within the given Anglerates...
findHittablesBetweenTwoAnglesSeSP(Breadth1, Height1, Breadth2, Height2, _RootList, HittablesList) :-
	findHittablesWithinAngleForALLRoots(Breadth1, Height1, Rootlist, HittablesForAngle1),
	findHittablesWithinAngleForALLRoots(Breadth2, Height2, Rootlist, HittablesForAngle2),
	
	splitList(HittablesForAngle1, HighestObject1, _Tail1),
	splitList(HittablesForAngle2, HighestObject2, _Tail2),
	
	hasTotalHeight(HighestObject1, MaxHeight1),
	hasTotalHeight(HighestObject2, MaxHeight2),
	(
		(MaxHeight1 > MaxHeight2) ->
			substractFrom(HittablesForAngle2, HittablesForAngle1, HittablesList);
			((MaxHeight1 == MaxHeight2) ->
				HittablesList = HittablesForAngle2;
				substractFrom(HittablesForAngle1, HittablesForAngle1, HittablesList)
			)
		
	).

findHittablesWithinAngleForALLRoots(Breadth, Height, Rootlist, HittablesForAngle):-
	splitListIn(RootList, Root, Rest),
	%findHittablesWithinAngleForOneRoot(Breadth, Height, 0, 0, Root, HittablesOfOneRoot),
	findHittablesWithinAngleForALLRoots(Breadth, Height, Rest, HittablesOfRest),
	append(HittablesOfOneRoot, HittablesOfRest, HittablesForAngle).

findHittablesWithinAngleForOneRoot(Breadth, Height, ActiveBreadth, ActiveHeight, Root, HittablesForAngle) :-
	true.
