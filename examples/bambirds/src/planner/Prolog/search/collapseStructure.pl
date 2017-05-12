

ptestCollapseStructureSeCS() :-
	doInferencialGroundingSeIG(),
	findall(
		Struct,
		structure(Struct),
		StructList
	),
	ptestCollapseStructureSeCS(StructList).
ptestCollapseStructureSeCS([]) :- !.
ptestCollapseStructureSeCS([Struct|Tail]) :-
	findTargetsToCollapseStructureSeCS(Struct,away,X),
	psysoSeCS(Struct,away),
	psysoSeCS(Struct,X),
	findTargetsToCollapseStructureSeCS(Struct,towards,Y),
	psysoSeCS(Struct,towards),
	psysoSeCS(Struct,Y),
	ptestCollapseStructureSeCS(Tail).


%%findTargetsToCollapseStructureSeCS/3
% findTargetsToCollapseStructureSeCS(+Structure,+Direction,-Targets)
% returns a List of all Targets in the quadrant you need
% Output-Form: [[HighestIntegrity,Object1],[NextLowestIntegrity,Object2],...]
findTargetsToCollapseStructureSeCS(Struct,Direction,Targets) :-
	pfindHittablesInStructureSeCS(Struct,Hittables),
	pgetTotalHeightOfStructureSeCS(Struct,TotalHeight),
	(
		(
			not(Direction == away),
			not(Direction == towards)
		) ->
		(
			syso('Given direction is incorrect',Direction)
		);
		(
			true
		)
	),
	(
		(
			Hittables == [];
			TotalHeight == false
		) ->
		(
			psysoSeCS('No Hittables in Structure',Struct)
		);
		(
			(
				(
					Direction == away
				) ->
				(
					pfindTargetsToCollapseInQuadrantOneSeCS(Hittables,TotalHeight,TmpTargets)
				);
				(
					pfindTargetsToCollapseInQuadrantThreeSeCS(Hittables,TotalHeight,TmpTargets)
				)
			)	
		)
	),
	pfindIntegrityValuesForTargetsSeCS(TmpTargets,TmpTargetsWithIntegrity),
	kwikeSortInverse(TmpTargetsWithIntegrity,Targets).

pfindIntegrityValuesForTargetsSeCS([],[]) :- !.
pfindIntegrityValuesForTargetsSeCS([Target|Tail],TargetsWithIntegrity) :-
	hasIntegrity(Target,Integrity),
	TargetWithIntegrity = [Integrity,Target],
	pfindIntegrityValuesForTargetsSeCS(Tail,NextList),
	append([TargetWithIntegrity],NextList,TargetsWithIntegrity).

pfindHittablesInStructureSeCS(Struct,Hittables) :-
	findall(
		Hittable,
		(
			belongsTo(Object,Struct),
			isHittable(Object,true),
			hasTotalHeight(Object,Height),
			Hittable = [Height,Object]
		),
		TmpHittables
	),
	sort(TmpHittables,UnsortedHittables),
	kwikeSort(UnsortedHittables,Hittables).
	
pgetTotalHeightOfStructureSeCS(Struct,TotalHeight) :-
	findall(
		Height,
		totalHeightOfStructure(Struct,Height),
		TmpTotalHeight
	),
	(
		(
			TmpTotalHeight == []
		) ->
		(
			psysoSeCS('Something went wrong with','InferencialGrounding'),
			TotalHeight == false
		);
		(
			sort(TmpTotalHeight,TotalHeightList),
			nth0(0,TotalHeightList,TotalHeight)
		)
	).

pfindTargetsToCollapseInQuadrantThreeSeCS(HittableList,TotalHeight,Targets) :-
	QuadrantHeight is (TotalHeight/2),
	splitListIn(HittableList,Head,_Tail),
	nth0(1,Head,Object),
	pfindAllOtherTargetsInQuadrantThreeSeCS(HittableList,QuadrantHeight,NextList),
	append([Object],NextList,UnsortedTargets),
	sort(UnsortedTargets,Targets).

pfindAllOtherTargetsInQuadrantThreeSeCS([],_QuadrantHeight,[]) :- !.
pfindAllOtherTargetsInQuadrantThreeSeCS([Hittable|Tail],QuadrantHeight,OutputList) :-
	nth0(0,Hittable,Height),
	nth0(1,Hittable,Object),
	(
		(
			Height < QuadrantHeight
		) ->
		(
			pfindAllOtherTargetsInQuadrantThreeSeCS(Tail,QuadrantHeight,NextList),
			append([Object],NextList,OutputList)
		);
		(
			OutputList = [Object]
		)
	).
	
pfindTargetsToCollapseInQuadrantOneSeCS(HittableList,TotalHeight,Targets) :- 
	QuadrantHeight is (TotalHeight/2),
	kwikeSortInverse(HittableList,InverseHittableList),
	splitListIn(InverseHittableList,Head,_Tail),
	nth0(1,Head,Object),
	pfindAllOtherTargetsInQuadrantOneSeCS(InverseHittableList,QuadrantHeight,NextList),
	append([Object],NextList,UnsortedTargets),
	sort(UnsortedTargets,Targets).

pfindAllOtherTargetsInQuadrantOneSeCS([],_QuadrantHeight,[]) :- !.
pfindAllOtherTargetsInQuadrantOneSeCS([Hittable|Tail],QuadrantHeight,OutputList) :-
	nth0(0,Hittable,Height),
	nth0(1,Hittable,Object),
	(
		(
			Height > QuadrantHeight
		) ->
		(
			pfindAllOtherTargetsInQuadrantOneSeCS(Tail,QuadrantHeight,NextList),
			append([Object],NextList,OutputList)
		);
		(
			OutputList = [Object]
		)
	).

psysoSeCS(Tag,TextOne,TextTwo) :-
	(
		(Tag == d) ->
			string_concat('[CollapseStructure][Debug] ',TextOne,Text);
			string_concat('[CollapseStructure][Sonstiges] ',TextOne,Text) 
	),
	pAlexOutput(Text,TextTwo),
	true.
psysoSeCS(TextOne,TextTwo) :-
	string_concat('[CollapseStructure] ',TextOne,Text),
	pAlexOutput(Text,TextTwo),
	true.




%pfindTargetsToCollapseInQuadrantOne([],_TotalHeight,Targets) :- !.	
%pfindTargetsToCollapseInQuadrantOne([Hittable|Tail],TotalHeight,Targets) :-
%	nth0(0,Hittable,Height),
%	nth0(1,Hittable,Object),
%	(
%		(
%			
%		) ->
%	),
%	pfindTargetsToCollapseInQuadrantOne(Tail,TotalHeight,NextTargets),
%	true.
%
%pfindTargetsInQuadrantOne(Hittable,QuadrantHeight



