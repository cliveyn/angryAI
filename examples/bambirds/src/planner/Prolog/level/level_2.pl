
% Birds & Pigs

bird(bird_1).
bird(bird_2).
bird(bird_3).
bird(bird_4).
bird(bird_5).

hasColor(bird_1, red).
hasColor(bird_2, red).
hasColor(bird_3, red).
hasColor(bird_4, red).
hasColor(bird_5, red).

birdOrder(bird_1, 0).
birdOrder(bird_2, 1).
birdOrder(bird_3, 2).
birdOrder(bird_4, 3).
birdOrder(bird_5, 4).

pig(pig_1).
pig(pig_2).
pig(pig_3).
pig(pig_4).

% High Architecture

structure(struct1).
structure(struct2).
structure(struct3).
structure(struct4).
structure(struct5).

structureType(struct1, mapElement).
structureType(struct2, tower).
structureType(struct3, tower).
structureType(struct4, tower).
structureType(struct5, tower).

isCollapsable(struct1, false).
isCollapsable(struct2, true).
isCollapsable(struct3, true).
isCollapsable(struct4, true).
isCollapsable(struct5, true).

canCollapse(struct5, struct4).
canCollapse(struct4, struct3).
canCollapse(struct3, struct4).
canCollapse(struct3, struct2).
canCollapse(struct2, struct3).

% contains(struct2,pig_1).
% contains(struct3,pig_2).
% contains(struct4,pig_3).
% contains(struct5,pig_4).

% Mid

object(midStoneBoulder1).
object(midStoneBoulder2).
object(midStoneBoulder3).
object(bigStoneBoulder1).
object(midWoodBoulder1).
object(midWoodBoulder2).
object(midWoodBoulder3).
object(midWoodBoulder4).
object(pig_1).
object(pig_2).
object(pig_3).
object(pig_4).

hasMaterial(midStoneBoulder1, stone).
hasMaterial(midStoneBoulder2, stone).
hasMaterial(midStoneBoulder3, stone).
hasMaterial(bigStoneBoulder1, stone).
hasMaterial(midWoodBoulder1, wood).
hasMaterial(midWoodBoulder2, wood).
hasMaterial(midWoodBoulder3, wood).
hasMaterial(midWoodBoulder4, wood).

hasSize(midStoneBoulder1, medium).
hasSize(midStoneBoulder2, medium).
hasSize(midStoneBoulder3, medium).
hasSize(bigStoneBoulder1, big).
hasSize(midWoodBoulder1, medium).
hasSize(midWoodBoulder2, medium).
hasSize(midWoodBoulder3, medium).
hasSize(midWoodBoulder4, medium).
hasSize(pig_1, small).
hasSize(pig_2, small).
hasSize(pig_3, small).
hasSize(pig_4, small).

hasForm(midStoneBoulder1, bar).
hasForm(midStoneBoulder2, bar).
hasForm(midStoneBoulder3, bar).
hasForm(bigStoneBoulder1, bar).
hasForm(midWoodBoulder1, bar).
hasForm(midWoodBoulder2, bar).
hasForm(midWoodBoulder3, bar).
hasForm(midWoodBoulder4, bar).

%isDestroyable(midStoneBoulder1, true).
%isDestroyable(midStoneBoulder2, true).
%isDestroyable(midStoneBoulder3, true).
%isDestroyable(bigStoneBoulder1, true).
%isDestroyable(midWoodBoulder1, true).
%isDestroyable(midWoodBoulder2, true).
%isDestroyable(midWoodBoulder3, true).
%isDestroyable(midWoodBoulder4, true).

isHittable(midStoneBoulder1, true).
isHittable(midStoneBoulder2, true).
isHittable(midStoneBoulder3, true).
isHittable(bigStoneBoulder1, true).
isHittable(midWoodBoulder1, true).
isHittable(midWoodBoulder2, true).
isHittable(midWoodBoulder3, true).
isHittable(midWoodBoulder4, true).
isHittable(pig_1, true).
isHittable(pig_2, true).
isHittable(pig_3, true).
isHittable(pig_4, true).

belongsTo(midStoneBoulder1, struct2).
belongsTo(midStoneBoulder2, struct3).
belongsTo(midStoneBoulder3, struct4).
belongsTo(bigStoneBoulder1, struct5).
belongsTo(midWoodBoulder1, struct2).
belongsTo(midWoodBoulder2, struct3).
belongsTo(midWoodBoulder3, struct4).
belongsTo(midWoodBoulder4, struct5).
belongsTo(pig_1, struct2).
belongsTo(pig_2, struct3).
belongsTo(pig_3, struct4).
belongsTo(pig_4, struct5).

standsOn(midStoneBoulder1, struct1).
standsOn(midStoneBoulder2, struct1).
standsOn(midStoneBoulder3, struct1).
standsOn(bigStoneBoulder1, struct1).

liesOn(midWoodBoulder1, midStoneBoulder1).
liesOn(midWoodBoulder2, midStoneBoulder2).
liesOn(midWoodBoulder3, midStoneBoulder3).
liesOn(midWoodBoulder4, bigStoneBoulder1).
liesOn(pig_1, midWoodBoulder1).
liesOn(pig_1, midWoodBoulder2).
liesOn(pig_1, midWoodBoulder3).
liesOn(pig_1, midWoodBoulder4).

% relations
isOn(midStoneBoulder1, struct1).
isOn(midStoneBoulder2, struct1).
isOn(midStoneBoulder3, struct1).
isOn(bigStoneBoulder1, struct1).
isOn(midWoodBoulder1, midStoneBoulder1).
isOn(midWoodBoulder2, midStoneBoulder2).
isOn(midWoodBoulder3, midStoneBoulder3).
isOn(midWoodBoulder4, bigStoneBoulder1).
isOn(pig_1, midWoodBoulder1).
isOn(pig_2, midWoodBoulder2).
isOn(pig_3, midWoodBoulder3).
isOn(pig_4, midWoodBoulder4).

hasOrientation(midStoneBoulder1, horizontal).
