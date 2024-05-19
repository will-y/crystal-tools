# Crystal Tools Change Log
## 1.6.0
### Additions
- Adds new reach ability for all tools
- Adds new upgrade `save_fuel` to the crystal furnace
  - Fuel is no longer consumed when you have to items to smelt

### Fixes
- Fixes tools not rendering highlight on non-mineable blocks 

### Changes
- Updates to Forge 1.20.1-47.2.5

## 1.5.3
### Fixes
- Fixes Tools not extending `TieredItem`

## 1.5.2
### Additions
- Adds config option `REPAIR_IN_HAND`: If true, tools can repair while you are holding them

### Fixes
- Fixes recipe for crystal elytra and crystal AIOT not showing up in JEI
- Fixes the crystal bow not being able to shoot arrows from quivers
- Fixes more items being enchantable

## 1.5.1
### Fixes
- Fixes tools not taking damage with autosmelting on
- Fixes tools being enchantable by book

## 1.5.0
### Additions
- When 3x3 or vein mining it now shows a preview of which blocks you will be mining.
- Adds a new config option `DISABLE_BLOCK_TARGET_RENDERING`. Disables the breaking preview.

### Changes
- Skill Tress now use a datapack instead of a resource pack
- Heal button is now disabled when the tool is not damaged 
- Redesigned the skill tree page to look better and make requirements more clear
- 3x3 Mining a torch with obsidian around it will no longer break the obsidian (blocks have to have similar destroy speeds now)
- Removed the Tree Stripper upgrade, Tree Chopper now mines and strips whole trees
- AIOT and shovel now make paths in 3x3 when in 3x3 mode

### Fixes
- Fixes Crystal Torches dropping items when placed by a tool
- Fixes tooltip sticking in the upgrade screen
- 3x3 Mining is now only based on the face you break not the direction the place is facing
- Fixes auto smelting not working
- Fixes auto smelting giving too much experience
- AIOT now correctly hoes in 3x3 area
- AIOT now vein strips correctly

## 1.4.2
### Changes
- Updates to Minecraft 1.20.1 (forge 47.1.0)
- Removed the following config options:
  - `GENERATE_STONE_ORE`
  - `STONE_VEIN_SIZE`
  - `STONE_PER_CHUNK`
  - `STONE_BOTTOM`
  - `STONE_TOP`
  - `GENERATE_DEEPSLATE_ORE`
  - `DEEPSLATE_VEIN_SIZE`
  - `DEEPSLATE_PER_CHUNK`
  - `DEEPSLATE_BOTTOM`
  - `DEEPSLATE_TOP`
- Oregen is now completely data driven, you can modify all of these things with a datapack
- Changes recipe of Crystal Apple to require an Enchanted Golden Apple

## 1.4.1
### Additions
- Adds the `Unbreaking` upgrade to all tools.
- Adds a new config option `VEIN_MINER_RANGE`. Determines the range of the vein miner on the shovel, pickaxe, and AIOT. It will mine blocks up to this range away from the ore broken.
- Adds a new config option `AXE_CHOPPER_RANGE`. Determines the range of the tree chopper and tree stripper upgrade on the axe. It will mine logs up to this range away from the log broken.
- Adds a new texture for infinite skill buttons.
- Adds the ability to put more points into the `Tree Chopper`,`Tree Stripper`, and `Vein Miner` upgrades to increase the range.
- Adds ability to put more points in auto repair to increase the amount repaired.
- Adds ability to 3x3 mine and hoe with the Crystal Hoe
- Adds mine mode to
  - Axe
  - Hoe
  - Shovel

### Changes
- Reset skill points button is now disabled if `require_crystal_for_reset` is true, and you have no crystal items in your inventory.
- Tools (non sword) now do the same amount of damage as netherite tools.
- Increases the default time between auto repair ticks (50 ticks -> 300 ticks).
- Increases the speed of crystal tools. Having Mining Speed V is now roughly equivalent to having a netherite pickaxe with Efficiency V.
- Crystal tools no longer get speed bonuses on blocks they are not effective on.
- Displays skills in the tooltip when holding shift.
- Decreases the exp gain of the Crystal Apple.
- Increases the repair time for the Crystal Apple.
- Decreases the experience gain of the Crystal Sword slightly.
- Changes the item name color to be consistent

### Fixes
- Fixes durability IV and mining speed IV were unlocked at incorrect times on the axe and pickaxe
- Fixes auto smelt always being unlocked on the shovel
- Fixes swords being effective on carpets
- Placing crystal torches with a tool now plays the place sound
- Fixes crystal torches not being able to be placed in dungeons
- Fixes 3x3 mining and vein mining not dropping exp for all blocks other than the first
- Fixes crystal apple leveling up and taking damage when breaking blocks

## 1.4.0
### Additions
- Adds the Crystal Furnace
  - An upgradable furnace that gains experience by smelting items
    - Experience gained is equal to the experience amount of the recipe
  - Has the following upgrades:
    - Furnace Speed: Decreases the time to smelt
    - Fuel Efficiency: Increases the time that fuel burns
    - Exp. Boost: Increases the experience (Minecraft Experience) that you get from smelting items
    - Slot: Adds an extra input and output slot
    - Fuel Slot: Adds an extra fuel slots
    - Auto Split: Automatically splits the inputs over the input slots
    - Auto Output: Automatically puts the output to connected inventories
- Adds new config options
  - `FUEL_EFFICIENY_ADDED_TICKS`: Determines how many ticks are added to each piece of fuel per level of Fuel Efficiency
  - `SPEED_UPGRADE_SUBTRACT_TICKS`: Number of ticks to subtract from each smelting recipe's cooking time per level of Furnace Speed
  - `EXPERIENCE_BOOST_PERCENTAGE`: Percentage increase for experience gained from smelting items

### Changes
- Updates to Forge 1.19.2-43.2.0

# 1.3.0
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
- Adds reset skill points button
- Adds new config options:
  - `TOOL_REPAIR_COOLDOWN`: Determines the amount of time between automatic tool repairs
  - `ROCKET_REPAIR_MODIFIER`: Increases the repair cooldown of the rocket specifically because it has a low durability
  - `APPLE_REPAIR_MODIFIER`: Increases the repair cooldown of the apple specifically because it has a low durability
  - `REQUIRE_CRYSTAL_FOR_RESET`: If true, resetting skill points consumes a crystal item in your inventory
  - `ROCKET_EXPERIENCE_BOOST`: Determines how much experience rockets get when used
  - `APPLE_EXPERIENCE_BOOST`: Increases the amount of experience apples get when used
- Adds config options to disable each tool and each piece of armor
  - Removes crafting of the tool
  - Will break if used, mined with, or attacked with (or worn if it is armor or elytra)
- Adds the crystal torch

### Changes
- Updates to Forge 1.19.2-43.1.47
- Tools that can place torches now place crystal torches
  - These torches do not drop items when broken
- Crafting a Crystal AIOT now gives you the skill points from the tools used to make it

### Fixes
- Fixes other items being able to be added in to the crystal elytra recipe

# 1.2.3
### Fixes
- Actually fixes elytra bug
- Allows pickaxe to use items and place blocks in the offhand

# 1.2.2
### Fixes
- Fixes a fully upgraded crystal elytra causing infinite health and freezing the game

# 1.2.1
### Fixes
- Fixes AIOT breaking bedrock when the 3x3 mining upgrade is unlocked

# 1.2.0
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

# 1.1.3
### Fixes
- Hoe now works again
- Axe now stops stripping logs if it runs out of durability while vein stripping

# 1.1.3-BETA

NOTE: Ore generation configs do not work in this version
### Fixes
- Ores now generate again
- Keybinds now work again

# 1.1.3-ALPHA

Initial port to 1.19, oregen is disabled
