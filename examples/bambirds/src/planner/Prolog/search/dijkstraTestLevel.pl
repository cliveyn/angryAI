structure(struct1337).

bird(bird0).
birdOrder(bird0, 0).
hasColor(bird0, red).

pig(pig0).

belongsTo(bigWoodCube0, struct1337).
belongsTo(bigWoodBar0, struct1337).
belongsTo(smallWoodCube0, struct1337).
belongsTo(bigWoodCube1, struct1337).
belongsTo(mediumWoodBlock0, struct1337).
belongsTo(bigWoodBar1, struct1337).
belongsTo(smallWoodBar0, struct1337).
belongsTo(mediumWoodBar0, struct1337).
belongsTO(pig0).

hasMaterial(bigWoodCube0, wood).
hasMaterial(bigWoodBar0, wood).
hasMaterial(smallWoodCube0, wood).
hasMaterial(bigWoodCube1, wood).
hasMaterial(mediumWoodBlock0, wood).
hasMaterial(bigWoodBar1, wood).
hasMaterial(smallWoodBar0, wood).
hasMaterial(mediumWoodBar0, wood).

hasSize(bigWoodCube0, big).
hasSize(bigWoodBar0, big).
hasSize(smallWoodCube0, small).
hasSize(bigWoodCube1, big).
hasSize(mediumWoodBlock0, medium).
hasSize(bigWoodBar1, big).
hasSize(smallWoodBar0, small).
hasSize(mediumWoodBar0, medium).

hasForm(bigWoodCube0, cube).
hasForm(bigWoodBar0, bar).
hasForm(smallWoodCube0, cube).
hasForm(bigWoodCube1, cube).
hasForm(mediumWoodBlock0, block).
hasForm(bigWoodBar1, bar).
hasForm(smallWoodBar0, bar).
hasForm(mediumWoodBar0, bar).

hasOrientation(bigWoodCube0, horizontal).
hasOrientation(bigWoodBar0, horizontal).
hasOrientation(smallWoodCube0, horizontal).
hasOrientation(bigWoodCube1, horizontal).
hasOrientation(mediumWoodBlock0, horizontal).
hasOrientation(bigWoodBar1, vertical).
hasOrientation(smallWoodBar0, vertical).
hasOrientation(mediumWoodBar0, horizontal).

isRight(bigWoodCube0, bigWoodBar0).
isRight(bigWoodBar0, smallWoodCube0).
isRight(bigWoodCube1, mediumWoodBlock0).

isLeft(smallWoodCube0, bigWoodBar0).
isLeft(bigWoodBar0, bigWoodCube0).
isLeft(mediumWoodBlock0, bigWoodCube1).

isOn(mediumWoodBar0, bigWoodBar1).
isOn(mediumWoodBar0, smallWoodBar0).
isOn(bigWoodBar1, bigWoodCube1).
isOn(smallWoodBar0, mediumWoodBlock0).
isOn(bigWoodCube1, bigWoodCube0).
isOn(mediumWoodBlock0, bigWoodBar0).
isOn(pig0, mediumWoodBlock0).

isHittable(smallWoodCube0, true).
isHittable(bigWoodBar0, true).
isHittable(mediumWoodBlock0, true).
isHittable(smallWoodBar0, true).
isHittable(mediumWoodBar0, true).
isHittable(pig0, true).


object(X) :- hasMaterial(X,_).
object(X) :- hill(X).
object(X) :- pig(X).
