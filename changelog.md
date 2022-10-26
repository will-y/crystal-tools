----------------------- 1.3.0-RELEASE Changes -----------------------
### Additions
- Adds the Crystal Apple
  - It is a food source with durability that can level up
  - Has the following upgrades:
    - Nutrition: Gain more food points back
    - Saturation: Gain more saturation whenever you eat
    - Eat Speed: Increases how fast you eat the apple
    - Durability: Gain more durability
    - Auto Repair: Repairs the apple over time
    - Always Eat: You can eat the apple even when full
    - Effects: You can get many positive effects that apply to the player when you eat the apple
- Adds new config options:
  - `TOOL_REPAIR_COOLDOWN`: Determines the amount of time between automatic tool repairs
  - `ROCKET_REPAIR_MODIFIER`: Increases the repair cooldown of the rocket specifically because it has a low durability
  - `APPLE_REPAIR_MODIFIER`: Increases the repair cooldown of the apple specifically because it has a low durability

----------------------- 1.2.3-RELEASE Changes -----------------------
### Fixes
- Actually fixes elytra bug
- Allows pickaxe to use items and place blocks in the offhand

----------------------- 1.2.2-RELEASE Changes -----------------------
### Fixes
- Fixes a fully upgraded crystal elytra causing infinite health and freezing the game

----------------------- 1.2.1-RELEASE Changes -----------------------
### Fixes
- Fixes AIOT breaking bedrock when the 3x3 mining upgrade is unlocked

----------------------- 1.2.0 RELEASE Changes -----------------------
### Additions
- Adds the Crystal Elytra
    - It is armored and can be leveled like other crystal tools/armor
- Adds the Crystal Rocket
  - It is a firework rocket that is only used for flying, cannot break, and can be upgraded!
  - New Upgrades:
    - `Flight Duration`: Increases the amount of time that the boost lasts
- Adds a new type of requirement:
  - `Item Requirement`: Skill nodes can require items to be upgraded. The items needed are shown in the corners of the node
- Adds a new Hoe Upgrade:
  - `Shears`: The hoe can now be used to shear sheep (and other entities)
- New config option: `MAX_EXP` - Determines the max amount of xp that can be required for the next level
- New config option: `ENABLE_ITEM_REQUIREMENTS` - If false, items will not be required for any upgrades
- New config option: `BACKGROUND_OPACITY` - Controls the opacity of the background of the skill tree screen, set to 0 to turn off the background
- New config option: `PAUSE_SCREEN` - Controls if the skill tree screen pauses the game if in single-player

### Changes
- Made spacing work better on the skill trees when there were more than 4 in a tier

----------------------- 1.1.3-RELEASE Changes -----------------------
### Fixes
- Hoe now works again
- Axe now stops stripping logs if it runs out of durability while vein stripping

----------------------- 1.1.3-BETA Changes -----------------------

NOTE: Ore generation configs do not work in this version
### Fixes
- Ores now generate again
- Keybinds now work again

----------------------- 1.1.3-ALPHA Changes -----------------------

Initial port to 1.19, oregen is disabled
