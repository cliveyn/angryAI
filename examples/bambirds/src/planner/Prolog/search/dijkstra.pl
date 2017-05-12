%Important Note:
%Before using any method of this class,
%Edges should be created by the SeGraphGenerator,
%of the following format: hasEdgeToWith(StartObject, EndObject, Cost)
:- dynamic openNode(_OpenNode, _OpenCostsSoFar, _OpenConnection).
:- dynamic closedNode(_ClosedNode, _ClosedCostsSoFar, _ClosedConnection).

%Allows the Algorithm to run beyond his limit, until it finds the EndNode
%if active the dijkstra will also add a "endOfPenetration" Object to a resultpath.
%Everything before that object can be destroyed by the given Bird. Everything after is will be uneffected
:- dynamic searchBeyondLimit.



/*OutgoingConnections And Values*/
%%cleanUpDijkstraSeDi/0
%cleanUpDijkstraSeDi()
%deletes all predicates created while running dijkstra in.
%(Normally) it is called after every dijkstra call!
cleanUpDijkstraSeDi() :-
	retractall(openNodeToExpensive(_ExpensiveNode)),
	
	retractall(openNode(NodeA, _CostSoFarA, hasEdgeToWith(_PreviousNodeA, NodeA, _EdgeCostToA))),
	retractall(closedNode(NodeZ, _CostSoFarZ, hasEdgeToWith(_PreviousNodeZ, NodeZ, _EdgeCostToZ))),
	
	retractall(endPoint(_End)),
	retractall(startPoint(_Start)).

%%findShortestPathSeDi/3
%findShortestPathSeDi(+StartNode, +EndNode, -Path)
%2 Lists: Open and Closed
%Creates Predicates:
%openNode(Object, CostSoFar, Connection) = Nodes that have been "seen"
%				aka a Cost for the Node was calculated and added
%closedNode(Object, CostSoFar, Connection) = Nodes that have been analysed, iterated over
%"Connection" is always an edge as created by the graph "hasEdgeToWith(Node, NextNode, EdgeLength)"
%Note:
%Returns false if given would not work with Dijkstra
findPathByDijkstraSeDi(StartNode, EndNode, Path):-
	findall(
		TmpPath,
		pCallDijkstraSeDi(StartNode, EndNode, TmpPath),
		TmpPaths
	), nth0(0, TmpPaths, Path), pSysoSeDi('FinalPath', Path).

%%findPathByDijkstraWithLimitSeDi/4
%findPathByDijkstraWithLimitSeDi(+StartNode, +EndNode, +Limited, -Path)
%Same as standard, but it stops Dijkstra when the only fastest path is above certain penetration limit...
%If it is ended prematurely, then the returned PathList is empty
%Awaits a Limitvalue no path can surpase
findPathByDijkstraWithLimitSeDi(StartNode, EndNode, Limit, Path):-
	( (dijkstraIsLimitedBy(Limit)) -> retractall(dijkstraIsLimitedBy(Limit));
		asserta(dijkstraIsLimitedBy(Limit)), pSysoSeDi('[Dijk-PreWork] Limit was set', Limit)
	), findPathByDijkstraSeDi(StartNode, EndNode, Path).

pCallDijkstraSeDi(StartNode, EndNode, Path) :-
	pSysoSeDi('++++++++++++++New Dijkstra Call++++++++++++++', ''),
	FromTo = [StartNode | EndNode], pSysoSeDi('Dijkstra, From -> TO', FromTo),
	(%If no graph was creates before.. 
		(	not(getNodeCountSeGG(_Count));
	%OR StartNode is no legit startingpoint in Graph...
			not(hasEdgeToWith(StartNode, _SomeObject, _SomeCost));
	%OR EndNode is no legit EndObject in Graph, then [I.] else [II.]
			not(hasEdgeToWith(_SomeOtherObject, EndNode, _SomeOtherCost)) ) ->			
					%[I.] a false will be returned.
					pSysoSeDi('Dijkstra not started, possibleReason', 'NoGraph; StartNode/EndPoint not part of graph'),
					Path = [],
					true;
					
					pSysoSeDi('StartNode', StartNode),
					pSysoSeDi('EndNode', EndNode),
					%[II.] Dijkstra is executed
						%If StartNode is already the EndNode, do [II.I], else [II.II] 
					(	(StartNode == EndNode) ->
						%[II.I]
						Path = [0 , StartNode], pSysoSeDi('Target is directly hittable', Path);					
						
						%[II.II]
						findall(
							PathTmp,
							(
								getNodeCountSeGG(Count),
								pSysoSeDi('Dijkstra was started, on a graph of following Node-Count', Count),
								%initiate
								asserta(startPoint(StartNode)),
								asserta(endPoint(EndNode)),
								asserta(openNode(StartNode, 0, hasEdgeToWith(none, StartNode, 0))),
								pSysoSeDi('Dijkstra initiated, with starting point', ''),
								%Dijkstra-Body
								pRunDijkstraSeDi(),
								pSysoSeDi('Dijkstra ended', ''),
								pGetShortestPathSeDi(PathTmp),
								pSysoSeDi('Djikstra created Resulting Path', PathTmp),
								cleanUpDijkstraSeDi(),
								pSysoSeDi('Dijkstra called cleanUp', 'is done...')
							),
							Paths
						), nth0(0, Paths, Path)
					)
	), !.

pRunDijkstraSeDi() :-
	pSysoSeDi('[Dijk]', 'starts running'),
	pIterationDijkstraSeDi().

%pGetShortestPathSeDi(-Path)
%Compiles the shortest Path from Nodes, by backtracking fom endpoint.  
pGetShortestPathSeDi(Path) :-
	(not(openNodeToExpensive(Node)); (searchBeyondLimit) ) ->
		endPoint(Node),
		pSysoSeDi("Endnote was", Node),
		openNode(Node, FinalCosts, hasEdgeToWith(PreviousNode, Node, _EdgeCost)),
		pSysoSeDi('PreviousNode to Endnode', PreviousNode),
		pCompilePathSeDi(PreviousNode, RecursiveStepList),
		pSysoSeDi('FinalCosts', FinalCosts),
		pSysoSeDi('Node', Node),
		pSysoSeDi('RecursiveStepList', RecursiveStepList),
		StepList = [Node|RecursiveStepList],
		Path = [FinalCosts | StepList];
		
		Path = [],
		pSysoSeDi('Dijkstra ended because endnode could not be reached within Limit. Result Path', Path).
		
%pCompilePathSeDi(+ActiveNode, -Path)
%recursiveLy compiles a Path  from closedNodes by backtracking.
pCompilePathSeDi(ActiveNode, Path) :-
	%if StartPoint was reached ...
	( (startPoint(ActiveNode)) ->
		%return activeNode as Path (terminate recursivecall)
		Path = [ActiveNode];
		%else find a previous closed Node and get its previous Node etc....
		closedNode(ActiveNode, _CostSoFar, hasEdgeToWith(PreviousNode, ActiveNode, _EdgeCost)),
		pCompilePathSeDi(PreviousNode, EarlierNodes),
		pSysoSeDi('[Dijk-PathCompiling] recursive Path-Steps', EarlierNodes),
		Path = [ActiveNode | EarlierNodes]
	),
	pSysoSeDi('[Dijk] Partial Path looks this way', Path).

pIterationDijkstraSeDi() :-
	pSysoSeDi('[Dijk]', 'start to analyse an openNode'),
	%if there is at least one 'open'-predicate left or the currentNode is the endpoint, end iteration... 
	(	(pGetCheapestOpenNodeSeDi(CurrentNode)) ->
		( not(pNodeIsTooExpensiveSeGG(CurrentNode)) ->
			(
				pSysoSeDi('[Dijk] cheapest node', CurrentNode),
				%AND the CurrentNode is NOT the goal...do [I.] else [II.]
				pIsNodeEndPointSeDi(CurrentNode, IsEndPoint),
				( not(IsEndPoint) ->
					%[I.]
					pWorkOnNodeSeDi(CurrentNode),
					%restart Iteration on the all other "open" Nodes.
					pIterationDijkstraSeDi();
					
					%[II.] End recursion...
					pSysoSeDi('[Dijk] Node was EndNode', CurrentNode)
				)
				
				;
				true
			);
			
			%if too expensive, but a search beyond limits was requested... [I.] else [II.]
			( (searchBeyondLimit) ->
				pSysoSeDi('![Dijk-SBL] Node too Expensive', 'SearchBeyondLimit was requested'),
				pIsNodeEndPointSeDi(CurrentNode, IsEndPoint),
				( not(IsEndPoint) ->
					insertEndOfPenetrationNode(CurrentNode),
					pWorkOnNodeSeDi(CurrentNode),
					pIterationDijkstraSeDi(searchBeyondLimit)
					
					;
					pSysoSeDi('[Dijk] Node was EndNode', CurrentNode)
				)				
				;				
				%[II.] terminate without Path
				pSysoSeDi('![Dijk] Node too Expensive', 'Djikstra has to terminate without new Path.')
			)
				
		);pSysoSeDi('![Dijk] No OpenNode Found', 'Dijkstra ends without Path')
	).
	
pWorkOnNodeSeDi(CurrentNode):-
		pSysoSeDi('[Dijk]: Current Node is no Endnode', CurrentNode),
		%[I.] Look at all Next Nodes...
		pAnalyseNextNodesSeDi(CurrentNode),
		openNode(CurrentNode, CostSoFar, hasEdgeToWith(PreviousNode, CurrentNode, EdgeCost)),
		pSysoSeDi('[Dijk]: Adjacent-Node-Analysation done', ''),
		%unflag CurrentObject as "seen"/open...
		retractall(openNode(CurrentNode, CostSoFar, hasEdgeToWith(PreviousNode, CurrentNode, EdgeCost))),
		(not(openNode(CurrentNode, CostSoFar, hasEdgeToWith(PreviousNode, CurrentNode, EdgeCost))) -> pSysoSeDi('[Dijk] was retracted open correctly', CurrentNode); pSysoSeDi('![Dijk] NOT RETRACTED form open', CurrentNode)),
		%AND flag CurrentObject as "analysed"/closed..
		assertz(closedNode(CurrentNode, CostSoFar, hasEdgeToWith(PreviousNode, CurrentNode, EdgeCost))),
		(closedNode(CurrentNode, CostSoFar, hasEdgeToWith(PreviousNode, CurrentNode, EdgeCost)) -> pSysoSeDi('[Dijk] was asserted closed correctly', CurrentNode); pSysoSeDi('![Dijk] NOT ASSERTED as closed', CurrentNode)).

insertEndOfPenetrationNode(CurrentNode) :-
	%Get Start and End Node of new PenetrationNode
	closedNode(PreviousNode, PreCostSoFar, hasEdgeToWith(_PrePreviousNode, PreviousNode, _PreEdgeCost)),
	openNode(CurrentNode, CostSoFar, hasEdgeToWith(PreviousNode, CurrentNode, EdgeCost)),
	
	%create new node 
	assertz(closedNode(endOfPenetration, PreCostSoFar, hasEdgeToWith(PreviousNode, endOfPenetration, 0))),	
	asserta(openNode(CurrentNode, CostSoFar, hasEdgeToWith(endOfPenetration, CurrentNode, 0))),
	
	%clean up
	retractall(openNode(CurrentNode, CostSoFar, hasEdgeToWith(PreviousNode, CurrentNode, EdgeCost))),
	pSysoSeDi("[Dijk-SBL] endOfPenetrationNode", closedNode(endOfPenetration, PreCostSoFar, hasEdgeToWith(PreviousNode, endOfPenetration, 0))),
	pSysoSeDi("[Dijk-SBL] newly changed CurrentNode", openNode(CurrentNode, CostSoFar, hasEdgeToWith(endOfPenetration, CurrentNode, 0))).
	
pIterationDijkstraSeDi(AlreadyEndOfPenetrationReached) :-
	(	(AlreadyEndOfPenetrationReached == searchBeyondLimit) ->
		pSysoSeDi('[Dijk-SBL]', 'start to analyse an openNode'),
		%if there is at least one 'open'-predicate left or the currentNode is the endpoint, end iteration... 
		(	(pGetCheapestOpenNodeSeDi(CurrentNode)) ->
			(
				pSysoSeDi('[Dijk-SBL] cheapest node', CurrentNode),
				%AND the CurrentNode is NOT the goal...do [I.] else [II.]
				pIsNodeEndPointSeDi(CurrentNode, IsEndPoint),
				( not(IsEndPoint) ->
						%[I.]
						pWorkOnNodeSeDi(CurrentNode),
						%restart Iteration on the all other "open" Nodes.
						pIterationDijkstraSeDi(AlreadyEndOfPenetrationReached);
						
						%[II.] End recursion...
						pSysoSeDi('[Dijk-SBL]Node was EndNode', CurrentNode)
				)	
			);
			
			pSysoSeDi('![Dijk-SBL] No OpenNode Found', 'Dijkstra ends without Path')
		)
		;
		true
	).
/*
pWorkOnNodeSeDi(CurrentNode, AlreadyEndOfPenetrationReached):-
		pSysoSeDi('[Dijk-SBL]: Current Node is no Endnode', CurrentNode),
		%[I.] Look at all Next Nodes...
		pAnalyseNextNodesSeDi(CurrentNode),
		openNode(CurrentNode, CostSoFar, hasEdgeToWith(PreviousNode, CurrentNode, EdgeCost)),
		pSysoSeDi('[Dijk-SBL]: Adjacent-Node-Analysation done', ''),
		%unflag CurrentObject as "seen"/open...
		retractall(openNode(CurrentNode, CostSoFar, hasEdgeToWith(PreviousNode, CurrentNode, EdgeCost))),
		(not(openNode(CurrentNode, CostSoFar, hasEdgeToWith(PreviousNode, CurrentNode, EdgeCost))) -> pSysoSeDi('[Dijk] was retracted open correctly', CurrentNode); pSysoSeDi('![Dijk] NOT RETRACTED form open', CurrentNode)),
		%AND flag CurrentObject as "analysed"/closed..
		assertz(closedNode(CurrentNode, CostSoFar, hasEdgeToWith(PreviousNode, CurrentNode, EdgeCost))),
		(closedNode(CurrentNode, CostSoFar, hasEdgeToWith(PreviousNode, CurrentNode, EdgeCost)) -> pSysoSeDi('[Dijk] was asserted closed correctly', CurrentNode); pSysoSeDi('![Dijk] NOT ASSERTED as closed', CurrentNode)).
*/

%pNodeIsTooExpensiveSeGG(+Node)
%checks if cost to a given Node is higher then the Limit.
pNodeIsTooExpensiveSeGG(Node) :-
	% dijkstra is limited
	(dijkstraIsLimitedBy(Limit)) ->
		openNode(Node, CostsSoFar, hasEdgeToWith(_PrevNode, Node, _EdgeCost)),
		%and CostSoFar of found openNode is higher then that limit
		( (CostsSoFar > Limit) ->
				%save that cheapest node is still too expensive and 
				assert(openNodeToExpensive(Node)),
				true;
				
				%...else
				false
		);
		%...else
		false.

pGetCheapestOpenNodeSeDi(CheapestNode) :-
	findall(
		CostAndNode,
		(
			openNode(Node, Cost, hasEdgeToWith(_PreviousNode, Node, _EdgeCost)),
			CostAndNode = [Cost, Node]
		),
		AllCostsAndNodes
	),
	((AllCostsAndNodes == []) -> false;
		kwikeSort(AllCostsAndNodes, SortedNodes),
		splitListIn(SortedNodes, [_CostSoFar | CheapestNodeList], _Rest)),
		nth0(0, CheapestNodeList, CheapestNode),
		pSysoSeDi('All open nodes', AllCostsAndNodes).

pIsNodeEndPointSeDi(CurrentNode, Truth) :-
	findall(
		TruthValue,
		(
			(endPoint(CurrentNode)) -> TruthValue = true; TruthValue = false
		),
		TruthValues
	), splitListIn(TruthValues, Truth, _Rest).

pAnalyseNextNodesSeDi(CurrentNode) :-
	findall(
		NextNode,
		(
			pSysoSeDi('[Dijk-AnalyseNextNodes] FINDING&ANAlYSING next nodes of', CurrentNode),
			%find NextNode that is connected to current one and get it's edgelength...
			hasEdgeToWith(CurrentNode, NextNode, EdgeLength),
			pSysoSeDi('[Dijk-AnalyseNextNodes] NextNode found', NextNode),
			%then, get "CostSoFar" from CurrentNode...
			openNode(CurrentNode, CostSoFar, hasEdgeToWith(_PreviousCurrentNode, CurrentNode, _EdgeCost)),
			pSysoSeDi('[Dijk-AnalyseNextNodes] CurrentNode has Cost', CostSoFar),
			%and add both to the new 'CostSoFar'
			NewCostSoFar is CostSoFar+EdgeLength,
			pSysoSeDi('[Dijk-AnalyseNextNodes] Cost For Move To NextNode', NewCostSoFar),
			%if the NextNode is not already closed... do [I.] else do [II.]
			( not(pIsNodeAlreadyClosedSeDi(NextNode)) ->
				%[I.] if NextNode is not already open... do [III.] else do [IV.]
				( not(pWasNodeAlreadySeenSeDi(NextNode))	->
					%[III.] just create a new openNode of the Nextone...
					asserta(openNode(NextNode, NewCostSoFar, hasEdgeToWith(CurrentNode, NextNode, EdgeLength))),
					pSysoSeDi('[Dijk-AnalyseNextNodes] Node was flagged as "seen"/open', NextNode);
					%[IV.]
					pSysoSeDi('![Dijk-AnalyseNextNodes] Node is already open', NextNode),
					pAdaptOpenNodeSeDi(NextNode, CurrentNode, NewCostSoFar)					
				);
				%[II.]
				pSysoSeDi('![Dijk-AnalyseNextNodes] Node is closed', NextNode),
				pAdaptClosedNodeSeDi(NextNode, CurrentNode, NewCostSoFar)
			)
		),
		NextNodes
	), pSysoSeDi('[Dijk-AnalyseNextNodes] Now "seen"/open Nodes of CurrentNode', NextNodes).
	
pIsNodeAlreadyClosedSeDi(Node) :-
	(closedNode(Node, _CostSoFar, hasEdgeToWith(_PreviousNode, Node, _EdgeCost))) ->
		true; false.
		
pWasNodeAlreadySeenSeDi(Node) :-
	(openNode(Node, _Cost, hasEdgeToWith(_PreviousNode, Node, _EdgeCost))) ->
		true, false.
		
pAdaptOpenNodeSeDi(OpenNode, CurrentNode, NewCost) :-
	open(OpenNode, OldCost, hasEdgeToWith(FormerNode, OpenNode, EdgeCosts)),
	( %if open could be reached cheaper by using the CurrentNode
		(NewCost < OldCost) ->
			%then: update 'CostsSoFar' with NewCost and change Edge to CurrentNode*/
			hasEdgeToWith(CurrentNode, OpenNode, NewEdgeCost),
			%by deleting the opennode...
			retract(openNode(OpenNode, OldCost, hasEdgeToWith(FormerNode, OpenNode, EdgeCosts))),
			%and by creating a new open node with the same object and altered costs and new edge.
			asserta(openNode(OpenNode, NewCost, hasEdgeToWith(CurrentNode, OpenNode, NewEdgeCost)));
			%else, keep openNode as is
			true
	).
	
pAdaptClosedNodeSeDi(OpenNode, CurrentNode, NewCost) :-
	/*check if closed Node could be reached fast, if yes change Cost&Edge and move it to "open"*/
	closedNode(OpenNode, OldCost, hasEdgeToWith(FormerNode, OpenNode, EdgeCosts)),
	(	(NewCost < OldCost) ->
		/*update 'CostsSoFar' and change Edge to CurrentNode*/
		hasEdgeToWith(CurrentNode, OpenNode, NewEdgeCost),
		retract(closedNode(OpenNode, OldCost, hasEdgeToWith(FormerNode, OpenNode, EdgeCosts))),
		assert(openNode(OpenNode, NewCost, hasEdgeToWith(CurrentNode, OpenNode, NewEdgeCost)));
		true
	).
	
%pSysoSeDi(+Text, +Value)
pSysoSeDi(_Text, _Value) :- %syso(_Text, _Value),
	true.