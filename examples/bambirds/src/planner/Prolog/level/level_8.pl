% Birds & Pigs

bird(bird_1).
bird(bird_2).
bird(bird_3).
bird(bird_4).

hasColor(bird_1, red).
hasColor(bird_2, red).
hasColor(bird_3, red).
hasColor(bird_4, red).

isInSlingshot(bird_1).

pig(pig_1).
pig(pig_2).
pig(pig_3).
pig(pig_4).

hasSize(pig_1, small).
hasSize(pig_2, small).
hasSize(pig_3, small).
hasSize(pig_4, small).

% High level architecture

structure(hill).
structure(stone_bullet_reservoir).
structure(tower_1).
structure(tower_2).
structure(pig_1).
structure(pig_2).
structure(pig_3).
structure(pig_4).

isReachable(hill, false).
isReachable(stone_bullet_reservoir, true).
isReachable(tower_1, true).
isReachable(tower_2, true).
isReachable(pig_1, true).
isReachable(pig_2, true).
isReachable(pig_3, true).
isReachable(pig_4, true).

canColapse(stone_bullet_reservoir, tower_1).
canColapse(stone_bullet_reservoir, tower_2).
canColapse(stone_bullet_reservoir, pig_1).
canColapse(stone_bullet_reservoir, pig_2).
canColapse(stone_bullet_reservoir, pig_3).
canColapse(stone_bullet_reservoir, pig_4).
canColapse(tower_1, pig_1).
canColapse(tower_1, pig_2).
canColapse(tower_2, pig_3).
canColapse(tower_2, pig_4).

% Mid level structural integrety

object(bigStoneBullet).
object(smallStoneBullet_1).
object(smallStoneBullet_2).
object(smallStoneCube_1).
object(smallStoneCube_2).
object(smallWoodCube_1).
object(smallWoodCube_2).
object(smallGlassCube_1).
object(smallGlassCube_2).

elementOfStructure(bigStoneBullet, stone_bullet_reservoir).
elementOfStructure(smallStoneBullet_2, stone_bullet_reservoir).
elementOfStructure(smallStoneCube_1, tower_1).
elementOfStructure(smallWoodCube_1, tower_1).
elementOfStructure(smallGlassCube_1, tower_1).
elementOfStructure(smallStoneCube_2, tower_2).
elementOfStructure(smallWoodCube_2, tower_2).
elementOfStructure(smallGlassCube_2, tower_2).

efficiencyRate(bigStoneBullet, 0.2).
efficiencyRate(smallStoneBullet_1, 0.3).
efficiencyRate(smallStoneBullet_2, 0.3).
efficiencyRate(smallStoneCube_1, 0.3).
efficiencyRate(smallStoneCube_2, 0.3).
efficiencyRate(smallWoodCube_1, 0.8).
efficiencyRate(smallWoodCube_2, 0.8).
efficiencyRate(smallGlassCube_1, 0.5).
efficiencyRate(smallGlassCube_2, 0.5).
efficiencyRate(pig_1, 1.0).
efficiencyRate(pig_2, 1.0).
efficiencyRate(pig_3, 1.0).
efficiencyRate(pig_4, 1.0).

integrityFactor(bigStoneBullet, 0.4).
integrityFactor(smallStoneBullet_1, 0.9).
integrityFactor(smallStoneBullet_2, 0.9).
integrityFactor(smallStoneCube_1, 1.0).
integrityFactor(smallStoneCube_2, 1.0).
integrityFactor(smallWoodCube_1, 0.5).
integrityFactor(smallWoodCube_2, 0.5).
integrityFactor(smallGlassCube_1, 0.0).
integrityFactor(smallGlassCube_2, 0.0).
integrityFactor(pig_1, 0.0).
integrityFactor(pig_2, 0.0).
integrityFactor(pig_3, 0.0).
integrityFactor(pig_4, 0.0).
