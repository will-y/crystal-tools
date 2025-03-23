# Crystal Tools Change Log
## 2.3.2
### Additions
- Adds two new upgrade for the sword, bow, AIOT, and trident
  - Beheading: Adds a chance for killed mobs to drop their heads
  - Capturing: Adds a small chance for killed mobs to drop a spawn egg
- Adds auto targeting to the trident
- Adds a toggle to disable auto targeting (shift + mode switch key)

## 2.3.1
### Additions
- Adds a new upgrade for the Crystal Shield:
  - Cooldown Reduction
    - Decreases the cooldown when the shield is disabled (add enough points and the shield won't be able to be disabled!)
- Adds the Shield to the guideme guide

### Changes
- Updates to Neo 21.1.128

### Fixes
- Fixes an issue where the guideme guides were not actually loading
- Fixes some issues with formatting in the guides
- Fixes an issue where the Crystal Shield had more durability than intended

## 2.3.0
### Additions
- Adds the Crystal Shield
  - Has the following upgrades:
    - Durability
    - Armor
      - You gain armor while the shield is in your off-hand
    - Thorns
      - Mobs that hit your shield take damage
    - Shield Bash
      - Increases the damage of the shield (when attacking)
    - Flaming Shield
      - Mobs who attack the shield catch on fire
    - Slow Mobs
      - Mobs who attack the shield are slowed
    - Totem Slot
      - Adds a slot of a Totem of Undying. Craft the shield with a Totem of Undying to insert it.
    - Shield Knockback
      - Mobs who attack the shield are knocked back
    - Poison Mobs
      - Mobs who attack the shield are poisoned
    - Wither Mobs
      - Mobs who attack the shield are withered
    - Target Mobs
      - Reflected projectile will auto-target mobs (mostly just arrows in vanilla)
- Adds the Auto Target Ability to the Crystal Bow

### Changes
- Quarry Stabilizers are now easily breakable with a pickaxe

### Fixes
- Fixes effects not being properly reset on the Crystal Apple
- Fixes some issues with the armor skill trees
- Fixes an issue where the quarry couldn't mine some blocks (like dead bushes and torches)
- Fixes a rare crash when the quarry mines certain blocks

## 2.2.3
### Additions
- Adds a new keybind (`R`). This will automatically use a Crystal Rocket from your inventory while you are flying.

### Fixes
- Fixes issue with reequip animations playing too often when the repair in hand config option was true

## 2.2.2
### Additions
- Adds in-game documentation using [GuideMe](https://www.curseforge.com/minecraft/mc-mods/guideme)
  - Just hover over an item and hold `G` to see documentation!
- Crystal tools can now be crafted with any item with a tag `c:rods/netherite`
- Netherite sticks now have the following tags:
  - `c:rods/netherite`
  - `c:rods/all_metal`
- Added color to the level up message
- Adds a new tag `auto_output_blacklist`
  - Furnaces and generators will not output to blocks with this tag
  - Default value is just `hopper`

### Fixes
- Fixes not being able to use off-hand items while holding a Crystal Pickaxe in your main hand
- Fixes the `repair_in_hand` config option not allowing you to mine
- Fixes furnaces auto outputting without the upgrade
- Fixes blocks not keeping their data when broken with a 3x3 tool

## 2.2.1
### Fixes
- Fixes backpack Sub screens not working
- Fixes backpack scrolling not working

## 2.2.0
### Additions
- Adds the Crystal Quarry!
    - Requires a square of 4 Quarry Stabilizers
        - You can right-click on a stabilizer to see if it forms a valid quarry shape
        - Right-click the quarry on a stabilizer to set the bounds
        - You can then place the quarry next to the stabilizers
    - Has the following upgrades:
        - Quarry Speed: Increases the mining speed of the quarry
        - Redstone Control: Allows the quarry to be turned off with a redstone signal
        - Trash Filter: Allows you to void certain items
        - Auto Output: Will output items to adjacent inventories
        - Silk Touch: Will mine blocks with silk touch
        - Fortune: Will mine blocks with fortune
        - Chunkloading: Will keep the quarry chunkloaded even when you are not around
    - The quarry also has a settings screen where you can change the following options:
        - Fill with Dirt: Will fill in the mined area with dirt
        - Enable Silk Touch: Allows you to enable / disable silk touch
        - Enable Fortune: Allows you to enable / disable fortune
        - Enable Auto Output: Allows you to enable / disable auto output
    - Has the following config options:
        - `quarry_base_energy_cost` (40): Quarry base RF/tick. Range: 0 - 100000.
        - `quarry_speed_cost_increase` (10): How much RF/tick is added to the quarry per speed upgrade. Range: 0 - 100000.
        - `quarry_silk_touch_cost_increase` (40): How much RF/tick is added to the quarry when silk touch is active. Range: 0 - 100000.
        - `quarry_fortune_cost_increase` (40): How much RF/tick is added to the quarry when fortune is active. Range: 0 - 100000.
        - `quarry_initial_point_multiplier` (0.1): What percentage of the points from an AIOT the quarry gets when crafted. Range: 0 - 1.
        - `quarry_base_experience_cap` (500): Starting EXP Cap for the quarry. Range: 1 - 100000.
        - `quarry_speed_upgrade_multiplier` (50): Multiplier for the speed upgrade of the quarry. Range: 1 - 100000.
        - `quarry_max_size` (64): Max size of the quarry. Range: 1 - 256.
- Adds the following config option for the generator:
    - `generator_base_experience_cap` (50): Starting EXP Cap for the generator. Range 1 - 100000.

### Changes
- Updates to Neo 21.1.115
- Changes the Crystal Generator Texture
- The Crystal Generator's slot now only accepts items it can burn
- Updates some formatting on tooltips
- The generator now only accepts items it can burn

## 2.1.8
### Fixes
- Fixes Xp Dupe with a very large amount of points

## 2.1.7
### Fixes
- Fixes the AIOT not mining dirt and other shovel blocks in 3x3 mode

## 2.1.6
### Additions
- Adds Accessories support for the crystal backpack
- Adds Curios Continuation support for the crystal backpack
- Adds Adorned support for the crystal backpack

### Changes
- Updates to Neo 21.1.80

## 2.1.5
### Additions
- Adds the ability to gain more skill points with xp by holding shift and control
- Adds the ability to toggle frost walker (shift + mode switch key)
- Adds the ability to toggle creative flight while wearing the elytra (ctrl + mode switch key)
- Adds tooltips to armor saying how to toggle abilities while wearing them
- Adds a Fortune V upgrade to the tools
- Adds a Looting V upgrade to the sword and AIOT
- Adds infinite unbreaking to the following tools
  - Fishing rod
  - Bow

### Fixes
- Fixes resetting points on tools voiding unspent points
- Fixes issue with the night vision toggle bugging out on servers
- Fixes incorrect item models for tools
- Fixes issue where the fishing rod doesn't take damage

## 2.1.4
### Additions
- Added a reset skills button to the furnace and generator
- Added ability to disable night vision with the mode switch key

### Fixes
- Fixes buying skill points with experience not working for furnaces and generators when you had 0 skill points
- Fixes resetting skill points using a crystal even when the config was set to not use it

## 2.1.3
### Fixes
- Fixes reset skills button being broken

## 2.1.2
### Fixes
- Fixes other fishing rods being broken

## 2.1.1
### Fixes
- Fixes adding points with experience not working correctly

## 2.1.0
### Additions
- Adds the Crystal Generator
  - It will generate FE from burnable fuels and output it to adjacent blocks
  - Can get the following upgrades:
    - FE Generation
      - Increases the FE per tick generated
    - Fuel Efficiency
      - Increases the burntime of fuels
    - FE Capacity
      - Increases the FE capacity
    - Redstone Control
      - Allows you to turn off the generator with a redstone signal
    - Metal Generator
      - Allows the generator to burn metals for FE
      - Currently works with all vanilla metals and any item with the `ingot` tag
    - Food Generator
      - Allows the generator to burn food for FE
    - Gem Generator
      - Allows the generator to burn gems for FE
      - Currently works with all vanilla gems and any item with the `gem` tag
    - Save Fuel
      - Doesn't waste fuel when there is no room to output FE
  - Currently still a work in progress, so please report and bugs or comment and suggestions!
  - Has the following new config options:
    - `base_fe_generation` (40): Base FE generation per tick. Range: 1 - 1000000.
    - `base_fe_storage` (10000): Base FE the crystal generator can store. Range: 1 - 1000000.
    - `base_fe_transfer` (80): Base FE transfer per tick. Range: 1 - 1000000.
    - `fe_generation_per_level` (10): FE generation gained per level of FE Generation. Range: 1 - 1000000.
    - `fe_storage_per_level` (2000): FE storage gained per level of FE capacity. Range: 1 - 1000000.
    - `food_burn_time_multiplier` (20): The food burntime formula is: (`food_nutrition` + `food saturation`) * `food_burn_time_multiplier`. Range: 0.01 - 10000.
    - `skill_points_per_burn_time` (0.00625): Determines how much skill experience you get from burning items. `skill_exp` = `fuel_burn_time` * `skill_points_per_burn_time`. Range: 0 - 1.
- Adds the ability to add multiple skill points to an infinite node at a time
  - Hold `Shift` to add 10 points by default
  - Hold `Control` to add 100 points by default
  - Hold both to add 1000 points by default
  - Adds new config options for these values:
    - `shift_point_spend` (10): Number of points to spend while you are holding shift. Range: 1 - 10000.
    - `control_point_spend` (100): Number of points to spend while you are holding control. Range: 1 - 10000.
- Adds the creative flight ability to the crystal elytra!
  - If you spend 100 (by default) points in the creative flight node, your elytra will allow you to fly like you were in creative mode
  - Can be disabled with the mode switch key while you are holding the crystal elytra
  - Adds new config option:
    - `creative_flight_points` (100): The number of points you need to have in the creative flight node for it to enable. Range: 1 - 1000000.

### Changes
- Updates to Neo 21.1.22
- Now requires at least Neo 21.0.161
- Furnaces and generators now show skills and items when in item form

## Fixes
- Fixes attack speed and attack damage upgrades not working correctly
- Fixes possible issue with armor attributes
- Fixed an issue where armor pieces were giving double base armor and toughness

## 2.0.1
### Changes
- Updates to Neo 21.0.163

### Fixes
- Fixes backpack crash when inserting into a backpack with a lot of slots
- Fixes double shift clicking many of the same type of item into a backpack
- Fixes auto pickup tooltip displaying before you have the upgrade on the backpack

## 2.0.0
### Additions
- Adds the config screen
  - Also moves some config options around and changes some names

### Changes
- Updates to Neo 21.0.143
- Allows modded blocks to be set as the background to the upgrade screen in the config option `UPGRADE_SCREEN_BACKGROUND`
- Removes the dark background on the upgrade screen
- **BREAKING DATAPACK CHANGE** : `type` in nodes was changed to `limit`. Old normal nodes are equivalent to `limit=1` and old infinite nodes are equivalent to `limit=0`
  - You can now set a limit greater than 1 to how many points can be put into a node
  - Infinite unbreaking nodes now have a limit (still allows for 100% unbreaking)
  - Backpack capacity is limited right now due to a change in how inventories are stored in 1.21. Will be fixed soon
- Changes the texture of NOT and OR dependency lines to be different from the AND dependency lines

### Fixes
- Fixes Crystal Elytra not getting skill points from enchantments on the Elytra used
- Fixes backpack compressions not working and voiding items

## 2.0.0-beta4
### Changes
- Updates to Neo 21.0.61-beta
- Removes pause screen config option

### Fixes
- Fixes dependency lines in the upgrade screen being dark
- Re-enables the Crystal Backpack

## 2.0.0-beta3
### Changes
- Updates to Neo 21.0.37-beta

### Fixes
- Fixes JEI Integration
- Fixes ore generation

## 2.0.0-beta2
### Changes
- Updates to Neo 21.0.10-beta

### Fixes
- Fixes upgrade screen not drawing the shaded background correctly
- Crystal Apple effects work again
- Crystal Bow effects work again
- Crystal Bow Punch upgrades work again

## 2.0.0-beta1
### Changes
- Updates to Minecraft 1.21
- Most things should work, the Backpack crafting recipe was disabled because it is very broken (will return soon)
- Effects on the Crystal Apple and Crystal Bow are broken (will return soon)

## 1.7.0
### Additions
- Adds the Crystal Trident
  - Can be leveled by hitting enemies like a sword, or by shooting it at entities
  - Levels can be used to:
    - Increase Attack Damage
    - Increase Projectile Damage
    - Increase Durability
    - Increase Projectile Speed
    - Increase Unbreaking Level
    - Increase Reach
    - Increase Riptide Level
    - Increase Loyalty Level
    - Increase Channeling Damage
    - Auto Repair
    - Switch between Loyalty and Riptide
    - Allow Riptide even when it's not raining
    - Instant Loyalty (It won't have to travel back to you, when it hits something it will instantly appear in your inventory)
  - Adds config option `DISABLE_TRIDENT`: Disables Crystal Trident
  - Adds config option `ALWAYS_CHANNEL`: If true, channeling Crystal Tridents will summon lightning even if they don't hit an entity
  - Adds config option `TRIDENT_EXPERIENCE_BOOST`: Multiplies experience Tridents get
- Adds Tipped Arrow upgrades for Crystal Bows
  - Poison
  - Slowness
  - Weakness
  - Wither
- Adds the Crystal Fishing Rod
  - Levels can be used to:
    - Increase Lure Level
    - Increase Luck of the Sea Level
    - Increase Durability
    - Increase Unbreaking Level
    - Add a chance to double fishing drops
    - Auto Repair
  - Adds config option `DISABLE_CRYSTAL_FISHING_ROW`: Disables Crystal Fishing Rod
  - Adds config option `FISHING_ROD_EXP`: Determines how much experience you get for fish caught
- Adds thorns option to the armor pieces
- Adds ability to use the scroll wheel to scroll on upgrade screens
- Adds new upgrade `auto_pickup`. Blocks mined will automatically go into your inventory if there is space

### Fixes
- Fixes prot enchantments not going on armor
- Fixes reach upgrades not increasing attack range

### Changes
- The Autosmelt tooltip is always shown when autosmelt and mode switch are upgraded

## 1.6.1
### Additions
- Adds a new tag `entity_blacklist`. Entities with this tag will not level up swords, bows, and the AIOT when attacked
- Adds the ability to equip backpacks in Curios slots
- Adds the ability to press the `open_backpack` key (b by default) to open a backpack anywhere inside your inventory or curios slots
- Adds a button to convert experience into skill points
- Adds config option `EXPERIENCE_LEVELING_SCALING`: Number of levels in a tool before the experience level costs increases. Set to 0 to disable scaling
- Adds config option `EXPERIENCE_PER_SKILL_LEVEL`: Determines the number of experience levels you need to gain a level on a tool. Set to 0 to disable

### Fixes
- Fixes tools being leveled up by attacking armor stands
- Fixes a crash if you ate a Crystal Apple with supersaturation installed

## 1.6.0
### Additions
- Adds new reach ability for all tools
- Adds new upgrade `save_fuel` to the crystal furnace
  - Fuel is no longer consumed when you have no items to smelt
- Adds new upgrade `draw_speed` to the crystal bow
- Adds new upgrade `no_fall_damage` to the crystal boots. Will negate all fall damage
- Adds config option `REACH_INCREASE`: The amount of reach you get for each level (in blocks)
- Adds config option `ENCHANT_TOOLS`: If true, tools will be enchantable. This could cause weird interactions and issues
- Adds the Crystal Backpack
  - It starts as a backpack with only 9 slots to store items
  - Can be leveled up by breaking blocks and hitting enemies
  - Levels can be used to:
    - Upgrade the capacity
    - Allow backpack to auto pickup items
    - Add pickup filter slots (can be either whitelist or blacklist)
    - Add the option to sort the backpack's contents
    - Add the option to store all items in the backpack into an inventory block/
- Adds config option `BACKPACK_SORT_TYPE`: Sort method that the backpack uses. Can be one of: `QUANTITY`, `NAME`, `MOD`, or `ID`
- Adds config option `BACKPACK_BASE_EXPERIENCE_CAP`: Starting EXP Value for the Backpack
  - Backpacks are kind of op and don't require as many levels total, so by default they take a little more xp to level up

### Fixes
- Fixes tools not rendering highlight on non-mineable blocks
- Fixes vein miner missing some ores diagonally
- Fixes silk touch and fortune not unlocking when you unlocked mine mode on the AIOT
- Fixes vein mining and vein stripping not working on servers

### Changes
- Updates to Forge 1.20.1-47.2.5
- It felt like things were leveling up a little to fast, you can change back in the configs if you like
  - Changes the default option for `BASE_EXPERIENCE_CAP` to 75
  - Changes the default option for `EXPERIENCE_MULTIPLIER` to 1.1

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
