%Notes: left means towards, right means away
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%													%
%	Heuristic Values To Tweak						%
%													%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%Modifies edge-costs in graphgenerator
%Higher Value means higher costs
efficiencyFactorForOrientation(2).
standardDijkstraSearchLimit(1).


% Modifies rankingFactor
% use: rankingFactorForStrategies(RankingFactor)

% Domino Strategy:
% PigCount * (RankingFactor^3)
% StructureCount * (RankingFactor^2)
% HeightOfFirstStructure * RankingFactor
% Values in a range of 1-n are for sorted hittables

% its the absolute minumum! not make it smaller!!!
minHeightForDomino(7).

rankingFactor(10).

% Add factor for each new strategy!
%----------HighLevel (InterStructureLevel)
rankingFactorForStrategy(domino,10).
rankingFactorForStrategy(whiteBird,9).
rankingFactorForStrategy(depot, 7).
rankingFactorForStrategy(tnt, 9).
%-----------Mid (StructureLevel)
rankingFactorForStrategy(heavyObject,8).
rankingFactorForStrategy(roof, 4).
rankingFactorForStrategy(minPen, 5).
rankingFactorForStrategy(maxPen, 5).
%----------Low (ObjectLevel)
rankingFactorForStrategy(pigCont, 2).
rankingFactorForStrategy(destroyPrimitive, 1).


%Hardcoded amount of Plans created for specific strategies
planLimit(penStruct, 1).
planLimit(roof, 1).

%Hardcoded Limit of how many Structures should be analyzed
% 0 means all that can be found
structureLimit(penStruct, 1).

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%													%
%	Basic Values									%
%													%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Plan Structure: plan([targets],[goals],strategyFlag,Ranking).

% effRate of bird against material
efficiencyBirdMaterial(red, wood, 1).
efficiencyBirdMaterial(red, glass, 0.8).
efficiencyBirdMaterial(red, stone, 2.0).
efficiencyBirdMaterial(blue, wood, 1.0).
efficiencyBirdMaterial(blue, glass, 0.8).
efficiencyBirdMaterial(blue, stone, 2.5).
efficiencyBirdMaterial(yellow, wood, 0.2).
efficiencyBirdMaterial(yellow, glass, 0.8).
efficiencyBirdMaterial(yellow, stone, 1.4).
efficiencyBirdMaterial(black, wood, 1.2).
efficiencyBirdMaterial(black, glass, 0.8).
efficiencyBirdMaterial(black, stone, 0.4).
efficiencyBirdMaterial(white, wood, 1.0).
efficiencyBirdMaterial(white, glass, 1.0).
efficiencyBirdMaterial(white, stone, 1.0).

efficiencySizeForm(small, 	bar, 	wood, 	0.4).
efficiencySizeForm(medium, 	bar, 	wood, 	0.5).
efficiencySizeForm(big, 	bar, 	wood, 	0.7).
efficiencySizeForm(small, 	cube, 	wood, 	0.2).
efficiencySizeForm(medium, 	cube, 	wood, 	0.6).
efficiencySizeForm(big, 	cube, 	wood, 	1.2).
efficiencySizeForm(medium, 	block,	wood, 	0.8).
efficiencySizeForm(small, 	ball, 	wood, 	1.5).
efficiencySizeForm(big, 	ball, 	wood, 	2.0).
efficiencySizeForm(small, 	bar, 	glass, 	0.26).
efficiencySizeForm(medium, 	bar, 	glass, 	0.33).
efficiencySizeForm(big, 	bar, 	glass, 	0.47).
efficiencySizeForm(small, 	cube, 	glass, 	0.13).
efficiencySizeForm(medium, 	cube, 	glass, 	0.4).
efficiencySizeForm(big, 	cube, 	glass, 	0.7).
efficiencySizeForm(medium, 	block, 	glass, 	0.53).
efficiencySizeForm(small, 	ball, 	glass, 	1.0).
efficiencySizeForm(big, 	ball, 	glass, 	1.25).
efficiencySizeForm(small, 	bar, 	stone, 	0.6).
efficiencySizeForm(medium, 	bar, 	stone, 	0.8).
efficiencySizeForm(big, 	bar, 	stone, 	1.2).
efficiencySizeForm(small, 	cube, 	stone, 	0.3).
efficiencySizeForm(medium, 	cube, 	stone, 	0.8).
efficiencySizeForm(big, 	cube, 	stone, 	1.6).
efficiencySizeForm(medium, 	block, 	stone, 	1.4).
efficiencySizeForm(small, 	ball, 	stone, 	3.0).
efficiencySizeForm(big, 	ball, 	stone, 	6.0).

objectWidthHeight(small, bar, 1, 2).
objectWidthHeight(medium, bar, 1, 4).
objectWidthHeight(big, bar, 1, 8).
objectWidthHeight(small, cube, 1, 1).
objectWidthHeight(medium, cube, 2, 2).
objectWidthHeight(big, cube, 4, 4).
objectWidthHeight(small, block, 2, 4).
objectWidthHeight(medium, block, 2, 4).
objectWidthHeight(big, block, 2, 4).
objectWidthHeight(small, ball, 2, 2).
objectWidthHeight(medium, ball, 3, 3).
objectWidthHeight(big, ball, 3, 3).

pigSize(small,2).
pigSize(medium,4).
pigSize(big,6).


%%%%%%%%%%%%%%%%%%%%%%%%%

% OLD VALUES

%%%%%%%%%%%%%%%%%%%%%%%%%

% effRates of Forms
efficiencyForm(bar, 0.5).
efficiencyForm(block, 0.7).
efficiencyForm(cube, 0.9).
efficiencyForm(ball, 0.2).

% effRates of Sizes
efficiencySize(small, 0.7).
efficiencySize(medium, 0.5).
efficiencySize(big, 0.2).

% effRates of Pigs
efficiencyPig(small, 0.9).
efficiencyPig(big, 0.7).
efficiencyPig(helm, 0.5).
efficiencyPig(worker, 0.4).
efficiencyPig(grandpa, 0.3).
efficiencyPig(king, 0.1).

% strategies for structures
structureStrategy(tower, collapse).
structureStrategy(house, destroy).
structureStrategy(pig, kill).
structureStrategy(bunker, destroy).
structureStrategy(depot, collapse).
structureStrategy(object, ignore).
structureStrategy(mapElement, ignore).
structureStrategy(none, destroy).

killContainingPig(collapse, true).
killContainingPig(destroy, true).
killContainingPig(kill, true).
killContainingPig(ignore, false).

