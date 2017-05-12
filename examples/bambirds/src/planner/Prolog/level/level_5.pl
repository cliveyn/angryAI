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
pig(pig_2).
pig(pig_3).
pig(pig_4).
pig(pig_5).

% High Architecture

structure(struct1).
structure(struct2).

structureType(struct1,house).
structureType(struct2,mapElement).

isColapsable(struct1,true).
isColapsable(struct2,false).

% Mid structural integrity

% all objects in map
object(bigStoneBall1).
object(bigStoneBar1).
object(bigStoneBar2).
object(bigStoneBar3).
object(bigStoneBar4).
object(bigStoneBar5).
object(bigStoneBar6).
object(bigStoneBar7).
object(bigStoneBar8).
object(midStoneBlock1).
object(midStoneBlock2).
object(midStoneBlock3).
object(midStoneBlock4).
object(bigWoodBar1).
object(bigWoodBar2).
object(bigWoodBar3).
object(bigWoodBar4).
object(bigWoodBar5).
object(bigWoodBar6).
object(midWoodBar1).
object(midWoodBar2).
object(bigGlasBar1).
object(bigGlasBar2).
object(bigGlasBar3).
object(midGlasBar1).
object(midGlasBar2).
object(midGlasBar3).
object(midGlasBar4).
object(smallGlasBar1).
object(smallGlasBar2).
object(smallGlasBar3).
object(smallGlasBar4).
object(pig_1).
object(pig_2).
object(pig_3).
object(pig_4).
object(pig_5).
object(objectTnt).

% materials
hasMaterial(bigStoneBall1,stone).
hasMaterial(bigStoneBar1,stone).
hasMaterial(bigStoneBar2,stone).
hasMaterial(bigStoneBar3,stone).
hasMaterial(bigStoneBar4,stone).
hasMaterial(bigStoneBar5,stone).
hasMaterial(bigStoneBar6,stone).
hasMaterial(bigStoneBar7,stone).
hasMaterial(bigStoneBar8,stone).
hasMaterial(midStoneBlock1,stone).
hasMaterial(midStoneBlock2,stone).
hasMaterial(midStoneBlock3,stone).
hasMaterial(midStoneBlock4,stone).
hasMaterial(bigWoodBar1,wood).
hasMaterial(bigWoodBar2,wood).
hasMaterial(bigWoodBar3,wood).
hasMaterial(bigWoodBar4,wood).
hasMaterial(bigWoodBar5,wood).
hasMaterial(bigWoodBar6,wood).
hasMaterial(midWoodBar1,wood).
hasMaterial(midWoodBar2,wood).
hasMaterial(bigGlasBar1,glass).
hasMaterial(bigGlasBar2,glass).
hasMaterial(bigGlasBar3,glass).
hasMaterial(midGlasBar1,glass).
hasMaterial(midGlasBar2,glass).
hasMaterial(midGlasBar3,glass).
hasMaterial(midGlasBar4,glass).
hasMaterial(smallGlasBar1,glass).
hasMaterial(smallGlasBar2,glass).
hasMaterial(smallGlasBar3,glass).
hasMaterial(smallGlasBar4,glass).
hasMaterial(objectTnt,tnt).

% Sizes
hasSize(bigStoneBall1, big).
hasSize(bigStoneBar1, big).
hasSize(bigStoneBar2, big).
hasSize(bigStoneBar3, big).
hasSize(bigStoneBar4, big).
hasSize(bigStoneBar5, big).
hasSize(bigStoneBar6, big).
hasSize(bigStoneBar7, big).
hasSize(bigStoneBar8, big).
hasSize(midStoneBlock1, medium).
hasSize(midStoneBlock2, medium).
hasSize(midStoneBlock3, medium).
hasSize(midStoneBlock4, medium).
hasSize(bigWoodBar1, big).
hasSize(bigWoodBar2, big).
hasSize(bigWoodBar3, big).
hasSize(bigWoodBar4, big).
hasSize(bigWoodBar5, big).
hasSize(bigWoodBar6, big).
hasSize(midWoodBar1, medium).
hasSize(midWoodBar2, medium).
hasSize(bigGlasBar1, big).
hasSize(bigGlasBar2, big).
hasSize(bigGlasBar3, big).
hasSize(midGlasBar1, medium).
hasSize(midGlasBar2, medium).
hasSize(midGlasBar3, medium).
hasSize(midGlasBar4, medium).
hasSize(smallGlasBar1, small).
hasSize(smallGlasBar2, small).
hasSize(smallGlasBar3, small).
hasSize(smallGlasBar4, small).
hasSize(pig_1, small).
hasSize(pig_2, small).
hasSize(pig_3, small).
hasSize(pig_4, big).
hasSize(pig_5, big).

% forms
hasForm(bigStoneBall1, ball).
hasForm(bigStoneBar1, bar).
hasForm(bigStoneBar2, bar).
hasForm(bigStoneBar3, bar).
hasForm(bigStoneBar4, bar).
hasForm(bigStoneBar5, bar).
hasForm(bigStoneBar6, bar).
hasForm(bigStoneBar7, bar).
hasForm(bigStoneBar8, bar).
hasForm(midStoneBlock1, block).
hasForm(midStoneBlock2, block).
hasForm(midStoneBlock3, block).
hasForm(midStoneBlock4, block).
hasForm(bigWoodBar1, bar).
hasForm(bigWoodBar2, bar).
hasForm(bigWoodBar3, bar).
hasForm(bigWoodBar4, bar).
hasForm(bigWoodBar5, bar).
hasForm(bigWoodBar6, bar).
hasForm(midWoodBar1, bar).
hasForm(midWoodBar2, bar).
hasForm(bigGlasBar1, bar).
hasForm(bigGlasBar2, bar).
hasForm(bigGlasBar3, bar).
hasForm(midGlasBar1, bar).
hasForm(midGlasBar2, bar).
hasForm(midGlasBar3, bar).
hasForm(midGlasBar4, bar).
hasForm(smallGlasBar1, bar).
hasForm(smallGlasBar2, bar).
hasForm(smallGlasBar3, bar).
hasForm(smallGlasBar4, bar).

%isDestroyable(bigStoneBall1, true).
%isDestroyable(bigStoneBar1, true).
%isDestroyable(bigStoneBar2, true).
%isDestroyable(bigStoneBar3, true).
%isDestroyable(bigStoneBar4, true).
%isDestroyable(bigStoneBar5, true).
%isDestroyable(bigStoneBar6, true).
%isDestroyable(bigStoneBar7, true).
%isDestroyable(bigStoneBar8, true).
%isDestroyable(midStoneBlock1, true).
%isDestroyable(midStoneBlock2, true).
%isDestroyable(midStoneBlock3, true).
%isDestroyable(midStoneBlock4, true).
%isDestroyable(bigWoodBar1, true).
%isDestroyable(bigWoodBar2, true).
%isDestroyable(bigWoodBar3, true).
%isDestroyable(bigWoodBar4, true).
%isDestroyable(bigWoodBar5, true).
%isDestroyable(bigWoodBar6, true).
%isDestroyable(midWoodBar1, true).
%isDestroyable(midWoodBar2, true).
%isDestroyable(bigGlasBar1, true).
%isDestroyable(bigGlasBar2, true).
%isDestroyable(bigGlasBar3, true).
%isDestroyable(midGlasBar1, true).
%isDestroyable(midGlasBar2, true).
%isDestroyable(midGlasBar3, true).
%isDestroyable(midGlasBar4, true).
%isDestroyable(smallGlasBar1, true).
%isDestroyable(smallGlasBar2, true).
%isDestroyable(smallGlasBar3, true).
%isDestroyable(smallGlasBar4, true).
%isDestroyable(pig_1, true).
%isDestroyable(pig_2, true).
%isDestroyable(pig_3, true).
%isDestroyable(pig_4, true).
%isDestroyable(pig_5, true).

isHittable(bigStoneBall1, false).
isHittable(bigStoneBar1, true).
isHittable(bigStoneBar2, true).
isHittable(bigStoneBar3, false).
isHittable(bigStoneBar4, false).
isHittable(bigStoneBar5, false).
isHittable(bigStoneBar6, true).
isHittable(bigStoneBar7, true).
isHittable(bigStoneBar8, false).
isHittable(midStoneBlock1, false).
isHittable(midStoneBlock2, false).
isHittable(midStoneBlock3, false).
isHittable(midStoneBlock4, false).
isHittable(bigWoodBar1, false).
isHittable(bigWoodBar2, false).
isHittable(bigWoodBar3, false).
isHittable(bigWoodBar4, false).
isHittable(bigWoodBar5, false).
isHittable(bigWoodBar6, false).
isHittable(midWoodBar1, true).
isHittable(midWoodBar2, false).
isHittable(bigGlasBar1, true).
isHittable(bigGlasBar2, true).
isHittable(bigGlasBar3, true).
isHittable(midGlasBar1, true).
isHittable(midGlasBar2, false).
isHittable(midGlasBar3, false).
isHittable(midGlasBar4, false).
isHittable(smallGlasBar1, true).
isHittable(smallGlasBar2, true).
isHittable(smallGlasBar3, true).
isHittable(smallGlasBar4, true).
isHittable(pig_1, false).
isHittable(pig_2, false).
isHittable(pig_3, false).
isHittable(pig_4, false).
isHittable(pig_5, false).
isHittable(objectTnt,true).

% modeling
% assign objects to structures
belongsTo(bigStoneBall1, struct1).
belongsTo(bigStoneBar1, struct1).
belongsTo(bigStoneBar2, struct1).
belongsTo(bigStoneBar3, struct1).
belongsTo(bigStoneBar4, struct1).
belongsTo(bigStoneBar5, struct1).
belongsTo(bigStoneBar6, struct1).
belongsTo(bigStoneBar7, struct1).
belongsTo(bigStoneBar8, struct1).
belongsTo(midStoneBlock1, struct1).
belongsTo(midStoneBlock2, struct1).
belongsTo(midStoneBlock3, struct1).
belongsTo(midStoneBlock4, struct1).
belongsTo(bigWoodBar1, struct1).
belongsTo(bigWoodBar2, struct1).
belongsTo(bigWoodBar3, struct1).
belongsTo(bigWoodBar4, struct1).
belongsTo(bigWoodBar5, struct1).
belongsTo(bigWoodBar6, struct1).
belongsTo(midWoodBar1, struct1).
belongsTo(midWoodBar2, struct1).
belongsTo(bigGlasBar1, struct1).
belongsTo(bigGlasBar2, struct1).
belongsTo(bigGlasBar3, struct1).
belongsTo(midGlasBar1, struct1).
belongsTo(midGlasBar2, struct1).
belongsTo(midGlasBar3, struct1).
belongsTo(midGlasBar4, struct1).
belongsTo(smallGlasBar1, struct1).
belongsTo(smallGlasBar2, struct1).
belongsTo(smallGlasBar3, struct1).
belongsTo(smallGlasBar4, struct1).
belongsTo(pig_1, struct1).
belongsTo(pig_2, struct1).
belongsTo(pig_3, struct1).
belongsTo(pig_4, struct1).
belongsTo(pig_5, struct1).
belongsTo(objectTnt, struct1).

% is on
isOn(bigStoneBar1, ground).
isOn(bigStoneBar2, ground).
isOn(bigStoneBar3, ground).
isOn(bigStoneBar4, ground).
isOn(bigStoneBar5, ground).
isOn(bigStoneBar6, ground).
isOn(midStoneBlock1, ground).
isOn(midStoneBlock2, ground).
isOn(bigWoodBar1, ground).
isOn(bigWoodBar2, ground).
isOn(pig_5, ground).
isOn(pig_4, pig_5).
isOn(pig_3, pig_4).
isOn(pig_2, pig_3).
isOn(pig_1, pig_2).
isOn(midStoneBlock3, midStoneBlock1).
isOn(midStoneBlock4, midStoneBlock2).
isOn(bigGlasBar1, bigStoneBar2).
isOn(bigGlasBar1, bigStoneBar3).
isOn(bigGlasBar1, bigStoneBlock3).
isOn(bigGlasBar1, bigWoodBar1).
isOn(bigGlasBar2, bigWoodBar2).
isOn(bigGlasBar2, bigStoneBlock4).
isOn(bigGlasBar2, bigStoneBar4).
isOn(bigGlasBar2, bigStoneBar5).
isOn(bigStoneBar7, bigGlasBar1).
isOn(bigWoodBar3, bigGlasBar1).
isOn(bigWoodBar4, bigGlasBar1).
isOn(bigStoneBar8, bigGlasBar2).
isOn(bigWoodBar5, bigGlasBar2).
isOn(bigWoodBar6, bigGlasBar2).
isOn(midWoodBar1, bigStoneBar7).
isOn(midWoodBar1, bigWoodBar3).
isOn(midWoodBar1, bigWoodBar4).
isOn(midWoodBar2, bigStoneBar8).
isOn(midWoodBar2, bigWoodBar5).
isOn(midWoodBar2, bigWoodBar6).
isOn(midGlasBar1, midWoodBar1).
isOn(midGlasBar2, midWoodBar1).
isOn(midGlasBar3, midWoodBar2).
isOn(midGlasBar4, midWoodBar2).
isOn(bigGlasBar3, midGlasBar1).
isOn(bigGlasBar3, midGlasBar2).
isOn(bigGlasBar3, midGlasBar3).
isOn(bigGlasBar3, midGlasBar4).
isOn(smallGlasBar1, bigGlasBar3).
isOn(smallGlasBar2, bigGlasBar3).
isOn(smallGlasBar3, bigGlasBar3).
isOn(smallGlasBar4, bigGlasBar3).
isOn(bigStoneBall1, midWoodBar1).
isOn(bigStoneBall1, midWoodBar2).
isOn(objectTnt,ground).

isLeft(midStoneBlock1, bigWoodBar1).
isLeft(midStoneBlock3, bigWoodBar1).
isLeft(bigStoneBar3, midStoneBlock1).
isLeft(bigStoneBar3, midStoneBlock3).
isLeft(bigStoneBar2, bigStoneBar3).
isLeft(bigStoneBar1, bigStoneBar2).

isLeft(midStoneBlock1, bigWoodBar1).
isLeft(midStoneBlock3, bigWoodBar1).
isLeft(bigStoneBar3, midStoneBlock1).
isLeft(bigStoneBar3, midStoneBlock3).
isLeft(bigStoneBar2, bigStoneBar3).
isLeft(bigStoneBar1, bigStoneBar2).

% supporting objects
supports(bigStoneBar1, bigStoneBar2).
supports(bigStoneBar6, bigStoneBar5).

canCollapse(_X,_Y).
