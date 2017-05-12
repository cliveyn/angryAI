% deletes paths that can not be used due to material and bird, sorts remaining paths by efficiency (easiest paths first)
findAndSortMostUsefulPaths(UnsortedPathList, UsefulSortedPathList):-
	deleteNotUsefulPaths(UnsortedPathList, UsefulUnsortedPathList),
	sort(0, @<, UsefulUnsortedPathList, List),
	writeln(List),
	UsefulSortedPathList = UsefulUnsortedPathList.

% deleteNotUsefulPaths(+PathList, -UsefulPathListInclEffValue)
% returns only paths with an EffValue smaller than 1 for the whole path
% so bird can destroy path in one shot
deleteNotUsefulPaths(PathList, UsefulPathListInclEffValue) :-
	nth0(0, PathList, Path, RestOfPathList),
	calculateEfficiencyValuesForPath(Path, EffValue),
	deleteNotUsefulPaths(RestOfPathList, PartialUsefulPathListInclEffValue),
		((EffValue =< 1) ->
			((PartialUsefulPathListInclEffValue == []) -> 
				UsefulPathListInclEffValue = [[EffValue|Path]];
			append([[EffValue|Path]],PartialUsefulPathListInclEffValue,UsefulPathListInclEffValue));
			UsefulPathListInclEffValue = PartialUsefulPathListInclEffValue).
deleteNotUsefulPaths([],[]).

%%
% calculateEfficiencyValuesForPath(+Path, -PathEffValue)
% calculates the summed efficiency value for all objects in a given path
calculateEfficiencyValuesForPath(Path, PathEffValue):-
	nth0(0, Path, Element, RestOfPath),
	getEfficiencyValueSeDH(Element, ElementEffValue),
	calculateEfficiencyValuesForPath(RestOfPath, RestOfPathEffValues),
	((RestOfPathEffValues == 0) ->
		PathEffValue is ElementEffValue;
		PathEffValue is ElementEffValue + RestOfPathEffValues
	).
calculateEfficiencyValuesForPath([],0).