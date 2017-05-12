savePlanBySearchStPiS() :-	
	findall(
		Pig,			
		(
			pig(Pig),
			findPathsFromToHittablesForActiveBirdWithStandardLimitSeWf([Pig], PathsList),
			splitListIn(PathsList, BestPath, _RestOfPaths),
			pSysoStPC('[PicCont] Shortest Path', BestPath),
			
			getLengthOfSeWf(BestPath, Value),
			pSysoStPC('[PicCont] Value', Value),
			
			deleteRankValues([BestPath], [RawPath]),
			pSysoStPC('[PicCont] RawPath', RawPath),
			
			splitListIn(RawPath, Target, _RestPath),
			pSysoStPC('[PicCont] -> Target', Target),
								
			Usefullness is (1 - Value)*10,
			pSysoStPC('[PicCont] Usefullnes', Usefullness),
			
			length(RawPath, DestroyedObjects),
			pSysoStPC('[PicCont] Path and DestroyedObjects And Usefullness', (RawPath, DestroyedObjects, Usefullness)),
			
			getPlanRankSeDH(1, Usefullness, DestroyedObjects, 0, pigCont, Rank),
			savePlan([Target], [Pig], pigCont, Rank)
		),
		_PigsWithPlans
	).