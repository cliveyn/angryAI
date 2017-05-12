
%%saveBearingObjectsSeBO/2
% saveBearingObjectsSeBO(+Structure,-MaxValue)
% creates a new predicate (hasIntegrity) for each object in structure
% saves the object name and the integrity value
% and creates a new predicate (hasMaxIntegrity) 
% with the highest integrity value of the structure
findBearingObjectsSeBO(Struct,MaxValue) :-
	findall(
		Value,
		(
			object(Object),
			belongsTo(Object,Struct),
			getIntegrityValueOfSeBO(Object,Value),
			assertz(hasIntegrity(Object,Value))
%			write('hasIntegrity('),
%			write(Object),
%			write(','),
%			write(Value),
%			writeln(').')
		),
		TmpList
	),
	max_member(MaxValue,TmpList).
%	assertz(hasMaxIntegrity(Struct,MaxValue)).

% getIntegrityValueOfSeBO(+Object, -Value)
% returns the integrity value for one object
getIntegrityValueOfSeBO(Object,Value) :-
	findCarriedObjectsSeBO(Object,List),
	length(List,Value).

% findCarriedObjectsSeBO(+Object, -List)
% returns a list with all objects which are on the input object
findCarriedObjectsSeBO(Object,List) :-
	findall(
		ObjectOn,
		isOn(ObjectOn,Object),
		FindAllList
	),
	findRecursivelyCarriedObjectsSeBO(FindAllList, RecursiveList),
	append(FindAllList,RecursiveList,UnsortedList),
	sort(UnsortedList,List).

% findRecursivelyCarriedObjectsSeBO(+List, -List)
% recursive version of findCarriedObjects
findRecursivelyCarriedObjectsSeBO([],[]) :- !.
findRecursivelyCarriedObjectsSeBO([Head|Tail],List) :-
	findall(
		ObjectOn,
		isOn(ObjectOn,Head),
		FindAllList
	),
	findRecursivelyCarriedObjectsSeBO(FindAllList, RecursiveList1),
	findRecursivelyCarriedObjectsSeBO(Tail, RecursiveList2),
	append(RecursiveList1, RecursiveList2, TmpList),
	append(TmpList,FindAllList,List).

% findRecursivelyCarriedObjectsSeBO(+EmptyList, -EmptyList)
% stops the recursive call of findCarriedObjects
	