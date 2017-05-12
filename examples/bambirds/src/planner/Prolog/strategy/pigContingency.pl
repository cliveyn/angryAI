findPigContingencyPlansStPc(PigHasToBeHittable) :-
	findall(
		_PigPlan,
		(
			pig(Pig),
			(
				PigHasToBeHittable == true,
				isHittable(Pig, true);
				true
			),
			%Maybe add some advanced categorisation
			%Atm its only the 1 pig;)
			getPlanRankSeDH(1,0,0,0, pigCont, Rank),
			
			pSysoStPC('[PicCont] Pig Directly Hit', 'StandardBird'),
			savePlan([Pig], [Pig], pigCont, Rank)
		),
		_PigPlans
	).
	
pSysoStPC(_Text, _Value) :- %syso(_Text, _Value),
	true.