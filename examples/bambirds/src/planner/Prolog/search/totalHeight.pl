
%%findTotalHeightsSeTH/2
% findTotalHeightsSeTH(+Struct,-MaxValue)
% saves the total height for each object and retrurns
% the total height for the hole structure
findTotalHeightsSeTH(Struct,MaxValue) :-
	findall(
		Value,
		(
			object(Object),
			belongsTo(Object,Struct),
			isDestroyable(Object,true),
			getTotalHeightValueOfSeTH(Object,Value),
			(
				(hasTotalHeight(Object,Value)) ->
					true;
					assertz(hasTotalHeight(Object,Value))
			)
		),
		ObjectList
	),
%	syso(Struct,ObjectList),
	(ObjectList == [] ->
		MaxValue = 0;
		max_member(MaxValue,ObjectList)
	).

cleanUpHeightsSeTH() :-
	retractall(hasTotalHeight(_Object,_Value)).
% getTotalHeightValueOfSeTH(+Object,-Value)
% returns the total height of the input object
getTotalHeightValueOfSeTH(Object,Value) :-
	TmpValue = 0,
	getTotalHeightValueOf(Object,TmpValue,Value).
	
getTotalHeightValueOf(Object,InputValue,OutputValue) :-
	getHeightSeDH(Object,ObjectHeight),
	TmpValue is InputValue + ObjectHeight,
	getElementBelow(Object,BelowObject),
	((BelowObject == ground;hill(BelowObject)) ->
			OutputValue = TmpValue;
			getTotalHeightValueOf(BelowObject,TmpValue,NextHeight),
			OutputValue = NextHeight
	). 

getElementBelow(Object,BelowObject) :-
	findall(
		GroundObject,
		(
			isOn(Object,GroundObject)
		),
		TmpList
	),
	nth0(0,TmpList,BelowObject).
	

