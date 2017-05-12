% Birds & Pigs

bird(bird_1).
bird(bird_2).
bird(bird_3).
bird(bird_4).

hasColor(bird_1, red).
hasColor(bird_2, red).
hasColor(bird_3, red).
hasColor(bird_4, red).

birdOrder(bird_1,0).
birdOrder(bird_2,1).
birdOrder(bird_3,2).
birdOrder(bird_4,3).

pig(pig_1).

% High Architecture

structure(struct1).
structure(struct2).
structure(struct3).
structure(struct4).	% Baseplate relevant? -> YES!

structureType(struct1, object).
structureType(struct2, house).
structureType(struct3, object).
structureType(struct3, object).
structureType(struct4, mapElement).

isColapsable(struct1, true).
isColapsable(struct2, true).
isColapsable(struct3, true).
isColapsable(struct4, false).

canColapse(struct2, struct1).
canColapse(struct2, struct3).

contains(struct2, pig1).

% Mid structural integrety

% all objects in map
object(smallWoodBoulder1).
object(smallWoodBoulder2).
object(smallWoodBoulder3).
object(smallWoodBoulder4).
object(midWoodBoulder1).
object(midWoodBoulder2).
object(midWoodBoulder3).
object(bigWoodBoulder1).
object(bigWoodBoulder2).
object(bigWoodBoulder3).
object(bigWoodBoulder4).
object(bigWoodBoulder5).
object(midGlasBoulder1).
object(midGlasBoulder2).
object(midStoneCube1).
object(pig_1).
object(struct4).

% materials of objects
hasMaterial(smallWoodBoulder1, wood).
hasMaterial(smallWoodBoulder2, wood).
hasMaterial(smallWoodBoulder3, wood).
hasMaterial(smallWoodBoulder4, wood).
hasMaterial(midWoodBoulder1, wood).
hasMaterial(midWoodBoulder2, wood).
hasMaterial(midWoodBoulder3, wood).
hasMaterial(bigWoodBoulder1, wood).
hasMaterial(bigWoodBoulder2, wood).
hasMaterial(bigWoodBoulder3, wood).
hasMaterial(bigWoodBoulder4, wood).
hasMaterial(bigWoodBoulder5, wood).
hasMaterial(midGlasBoulder1, glas).
hasMaterial(midGlasBoulder2, glas).
hasMaterial(midStoneCube1, stone).

% sizes of objects
hasSize(smallWoodBoulder1, small).
hasSize(smallWoodBoulder2, small).
hasSize(smallWoodBoulder3, small).
hasSize(smallWoodBoulder4, small).
hasSize(midWoodBoulder1, medium).
hasSize(midWoodBoulder2, medium).
hasSize(midWoodBoulder3, medium).
hasSize(bigWoodBoulder1, big).
hasSize(bigWoodBoulder2, big).
hasSize(bigWoodBoulder3, big).
hasSize(bigWoodBoulder4, big).
hasSize(bigWoodBoulder5, big).
hasSize(midGlasBoulder1, medium).
hasSize(midGlasBoulder2, medium).
hasSize(midStoneCube1, medium).
hasSize(pig_1, small).

% forms of objects
hasForm(smallWoodBoulder1, bar).
hasForm(smallWoodBoulder2, bar).
hasForm(smallWoodBoulder3, bar).
hasForm(smallWoodBoulder4, bar).
hasForm(midWoodBoulder1, bar).
hasForm(midWoodBoulder2, bar).
hasForm(midWoodBoulder3, bar).
hasForm(bigWoodBoulder1, bar).
hasForm(bigWoodBoulder2, bar).
hasForm(bigWoodBoulder3, bar).
hasForm(bigWoodBoulder4, bar).
hasForm(bigWoodBoulder5, bar).
hasForm(midGlasBoulder1, bar).
hasForm(midGlasBoulder2, bar).
hasForm(midStoneCube1, cube).

% orientation
hasOrientation(smallWoodBoulder1,vertical).
hasOrientation(smallWoodBoulder2, vertical).
hasOrientation(smallWoodBoulder3, horizontal).
hasOrientation(smallWoodBoulder4, vertical).
hasOrientation(midWoodBoulder1, horizontal).
hasOrientation(midWoodBoulder2, horizontal).
hasOrientation(midWoodBoulder3, horizontal).
hasOrientation(bigWoodBoulder1, vertical).
hasOrientation(bigWoodBoulder2, horizontal).
hasOrientation(bigWoodBoulder3, vertical).
hasOrientation(bigWoodBoulder4, vertical).
hasOrientation(bigWoodBoulder5, vertical).
hasOrientation(midGlasBoulder1, vertical).
hasOrientation(midGlasBoulder2, vertical).
hasOrientation(midStoneCube1, horizontal).

% can be destroyed
%isDestroyable(smallWoodBoulder1, true).
%isDestroyable(smallWoodBoulder2, true).
%isDestroyable(smallWoodBoulder3, true).
%isDestroyable(smallWoodBoulder4, true).
%isDestroyable(midWoodBoulder1, true).
%isDestroyable(midWoodBoulder2, true).
%isDestroyable(midWoodBoulder3, true).
%isDestroyable(bigWoodBoulder1, true).
%isDestroyable(bigWoodBoulder2, true).
%isDestroyable(bigWoodBoulder3, true).
%isDestroyable(bigWoodBoulder4, true).
%isDestroyable(bigWoodBoulder5, true).
%isDestroyable(midGlasBoulder1, true).
%isDestroyable(midGlasBoulder2, true).
%isDestroyable(midStoneCube1, true).
%isDestroyable(pig_1, true).


% Can I reach the object with the actual bird directly?
isHittable(smallWoodBoulder1, true).
isHittable(smallWoodBoulder2, true).
isHittable(smallWoodBoulder3, true).
isHittable(smallWoodBoulder4, false).
isHittable(midWoodBoulder1, true).
isHittable(midWoodBoulder2, false).
isHittable(midWoodBoulder3, false).
isHittable(bigWoodBoulder1, true).
isHittable(bigWoodBoulder2, true).
isHittable(bigWoodBoulder3, true).
isHittable(bigWoodBoulder4, false).
isHittable(bigWoodBoulder5, false).
isHittable(midGlasBoulder1, false).
isHittable(midGlasBoulder2, false).
isHittable(midStoneCube1, true).
isHittable(pig_1, false).

% modeling of the map
% assign objects to structures
belongsTo(smallWoodBoulder1, struct1).
belongsTo(smallWoodBoulder2, struct3).
belongsTo(ground, struct4).
belongsTo(smallWoodBoulder3, struct2).
belongsTo(smallWoodBoulder4, struct2).
belongsTo(midWoodBoulder1, struct2).
belongsTo(midWoodBoulder2, struct2).
belongsTo(midWoodBoulder3, struct2).
belongsTo(bigWoodBoulder1, struct2).
belongsTo(bigWoodBoulder2, struct2).
belongsTo(bigWoodBoulder3, struct2).
belongsTo(bigWoodBoulder4, struct2).
belongsTo(bigWoodBoulder5, struct2).
belongsTo(midStoneCube1, struct2).
belongsTo(midGlassBoulder1, struct2).
belongsTo(midGlassBoulder2, struct2).
belongsTo(pig_1, struct2).

% standing objects
%standsOn(smallWoodBoulder1, ground).
%standsOn(bigWoodBoulder1, ground).
%standsOn(bigWoodBoulder3, ground).
%standsOn(smallWoodBoulder2, ground).
%standsOn(bigWoodBoulder4, bigWoodBoulder2).
%standsOn(bigWoodBoulder5, bigWoodBoulder2).
%standsOn(smallWoodBoulder4, midStoneCube1).
%standsOn(midGlasBoulder1, midWoodBoulder1).
%standsOn(midGlasBoulder2, midWoodBoulder1).
%
%% lying objects
%liesOn(midWoodBoulder1, ground).
%liesOn(midWoodBoulder2, midGlasBoulder1).
%liesOn(midWoodBoulder2, midGlasBoulder2).
%liesOn(bigWoodBoulder2, bigWoodBoulder1).
%liesOn(bigWoodBoulder2, bigWoodBoulder3).
%liesOn(smallWoodBoulder3, bigWoodBoulder2).
%liesOn(pig_1, smallWoodBoulder3).
%liesOn(midWoodBoulder3, bigWoodBoulder4).
%liesOn(midWoodBoulder3, bigWoodBoulder5).
%liesOn(midStoneCube1, midWoodBoulder3).


% relations
isOn(smallWoodBoulder1, ground).
isOn(bigWoodBoulder1, ground).
isOn(bigWoodBoulder3, ground).
isOn(smallWoodBoulder2, ground).
isOn(bigWoodBoulder4, bigWoodBoulder2).
isOn(bigWoodBoulder5, bigWoodBoulder2).
isOn(smallWoodBoulder4, midStoneCube1).
isOn(midGlasBoulder1, midWoodBoulder1).
isOn(midGlasBoulder2, midWoodBoulder1).
isOn(midWoodBoulder1, ground).
isOn(midWoodBoulder2, midGlasBoulder1).
isOn(midWoodBoulder2, midGlasBoulder2).
isOn(bigWoodBoulder2, bigWoodBoulder1).
isOn(bigWoodBoulder2, bigWoodBoulder3).
isOn(smallWoodBoulder3, bigWoodBoulder2).
isOn(pig_1, smallWoodBoulder3).
isOn(midWoodBoulder3, bigWoodBoulder4).
isOn(midWoodBoulder3, bigWoodBoulder5).
isOn(midStoneCube1, midWoodBoulder3).

% supporting Objects
%% None


% is not longer needed
%efficiencyRate(smallWoodBoulder1, 1).
%efficiencyRate(bigWoodBoulder2, 0.8).
%%...
%
%%???
%integrityFactor(smallWoodBoulder1, 0).
%integrityFactor(bigWoodBoulder1, 0.8).

%...

