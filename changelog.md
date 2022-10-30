----------------------- 1.3.0 Changes -----------------------
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
    - `ROCKET_EXPERIENCE_BOOST`: Determines how much experience rockets get when used
    - `APPLE_EXPERIENCE_BOOST`: Increases the amount of experience apples get when used
- Adds config options to disable each tool and each piece of armor
	- Removes crafting of the tool
	- Will break if used, mined with, or attacked with (or wear if it is armor or elytra)
- Adds the crystal torch

### Changes
- Tools that can place torches now place crystal torches
	- These torches do not drop items when broken

----------------------- 1.2.2 Changes -----------------------
### Fixes
- Pickaxe can now place blocks and use items in the offhand

----------------------- 1.2.1 Changes -----------------------
### Fixes
- Fixes a fully upgraded crystal elytra causing infinite health and freezing the game

----------------------- 1.2.0 Changes -----------------------
### Overall
- Backports the elyta and the rocket to 1.18
  - Do not expect everything in 1.19 to be ported back, but some things will be
  
### Additions
- Adds the Crystal Elytra
    - It is armored and can be leveled like other crystal tools/armor
- Adds the Crystal Rocket
    - It is a firework rocket that is only used for flying, cannot break, and can be upgraded!
    - New Upgrades:
        - `Flight Duration`: Increases the amount of time that the boost lasts
- New config option: `MAX_EXP` - Determines the max amount of xp that can be required for the next level

### Changes
- Made spacing work better on the skill trees when there were more than 4 in a tier
- Updated to Forge 1.18.2-40.1.80

----------------------- 1.1.4 Changes -----------------------
### Fixes
- Fixes AIOT breaking bedrock when the 3x3 mining upgrade is unlocked

----------------------- 1.1.3 Changes -----------------------

NOTICE: This will probably be the last 1.18 version
### Changes
- Updated to Forge 1.18.2-40.1.0

### Fixes
- Fixes the missing text for the Mode Switch Keybind

----------------------- 1.1.2 Changes -----------------------
### Changes
- Updated to Forge 1.18.2-40.0.31

### Additions
- Added the following config options:
    - `deepslate_ore_generate`: Controls if deepslate crystal ore is generated in the world
    - `deepslate_ore_vein_size`: Controls the vein size of deepslate crystal ore
    - `deepslate_ore_per_chunk`: Controls the number of veins of deepslate crystal ore that generate per chunk
    - `deepslate_ore_y_min`: Controls the minimum y that deepslate crystal ore can generate
    - `deepslate_ore_y_max`: Controls the maximum y that deepslate crystal ore can generate
    - `stone_ore_generate`: Controls if crystal ore is generated in the world
    - `stone_ore_vein_size`: Controls the vein size of crystal ore
    - `stone_ore_per_chunk`: Controls the number of veins of crystal ore that generate per chunk
    - `stone_ore_y_min`: Controls the minimum y that crystal ore can generate
    - `stone_ore_y_max`: Controls the maximum y that crystal ore can generate

----------------------- 1.1.1 Changes -----------------------
### Changes
- Updated to Minecraft 1.18.2 (Forge 40.0.12)
- WILL NOT WORK FOR 1.18.1!

----------------------- 1.1.0 Changes -----------------------
### Additions
- Added the AIOT
  - Can mine any block
  - Switch between use-modes (hoe, shovel, axe, and torch placer) with `alt` + `m`
  - Craft with all the tools in a crafting table + a slime block

### Changes
- Changed Pickaxe skill tree to use or requirements instead of and requirements to match later tools
- Changed Axe skill tree to use or requirements instead of and requirements to match later tools
- Changed Hoe skill tree to use or requirements instead of and requirements to match later tools
- Changed Shovel skill tree to use or requirements instead of and requirements to match later tools
- Added what key you need to press to change different modes to tooltip

----------------------- 1.0.0 Changes -----------------------
### Additions
- Added ability to disable 3x3 mining (requires `Mode Switch`)
  - Hold shift and press mode switch key (`m` by default)
- Added ability to disable auto smelting (requires `Mode Switch`)
  - Hold control and press mode switch key (`m` by default)
- Added background to the upgrade screen
- Added config option to change background block for upgrade screen: `upgrade_screen_background`

### Changes
- Changed the upgrade screen to a pause screen (in single player)
- Changed Protection to Prot in the skill trees so it fits on the buttons

----------------------- 0.6.0 Changes -----------------------
### Additions
- Added lines to show dependencies in the skill trees
- Auto-smelt now drops exp
- Added a Mode Switch keybinding
- Added a Mode Switch Upgrade to the Pickaxe
  - Switches between Silk Touch and Fortune if you have both upgrades and the mode switch upgrade

### Fixes
- Fixed two upgrades in the axe having the same id and messing up the skill tree
- Fixed Pickaxe not being able to 3x3 mine and vein mine
- Fixed being able to level auto-smelt and multimine even when it doesn't work

### Changes
- Updated to Forge 1.18.1-39.0.66
- Blocks dropped by vein miner, tree miner, and 3x3 mining now drop their resources on the player

----------------------- 0.5.2 Changes -----------------------
### Additions
- Added a chat message when your tools level up

### Fixes
- Leveling up a tool now correctly plays the experience level up sound
- Crystal Bows now zoom in like normal bows
- Fixed armor not auto repairing
- Armor no longer provides bonuses when broken (other than enchantments)
- Bow no longer works when broken
- Sword no longer works when broken
- Fixed an error where tools and bows could break

----------------------- 0.5.1 Changes -----------------------
### Additions
- Added the following config options:
  - `base_experience_cap`: Controls how much experience you need for one level of a tool
  - `experience_multiplier`: Controls how much experience required changes per level
  - `armor_experience_boost`: Controls how much more experience armor gets when you take damage
  - `bow_experience_boost`: Controls how much more experience bows get when you deal damage
  - `sword_experience_boost`: Controls how much more experience swords get when you deal damage

----------------------- 0.5.0 Changes -----------------------
### Additions
- Added crystal helmet
- Added crystal chestplate
- Added crystal leggings
- Added crystal boots
- Added the following attributes:
  - All Armor:
      - Protection
      - Fire Protection
      - Blast Protection
      - Projectile Protection
      - Thorns
      - Armor value bonus
      - Toughness bonus
  - Helmet:
    - Aqua Affinity
    - Respiration
    - Night Vision
  - Chestplate:
    - Health Bonus
  - Leggings:
    - Speed Bonus
  - Boots:
    - Feather Falling
    - Depth Strider
    - Soul Speed
    - Frost Walker

### Changes
- Updated to Forge 1.18.1-39.0.63

----------------------- 0.4.1 Changes -----------------------
### Changes
- Changed the crystal bow texture to a custom one

----------------------- 0.4.0 Changes -----------------------
### Additions
- Added the crystal Bow
- Added the following attributes for it
	- Arrow Damage
	- Arrow Speed
	- Flame
	- Infinity
	- Arrow Knockback
- I planned to add draw speed as well, but that is not working right now, will be added later
- Texture to come soon

### Changes
- Updated to Forge 1.18.1-39.0.59

----------------------- 0.3.0 Changes -----------------------
### Additions
- Added the crystal Sword
- Added the following attributes for it
	- Attack Damage
	- Attack Speed
	- Fire Aspect
	- Knockback
	- Knockback Resistance
		- You get knocked back less when holding the sword
	- Lifesteal
		- You heal when you hit enemies
	- Looting
- Added a command to add skillpoints to a tool (cheat mode only)
	- `add_points <points>` will add `<points>` number of points to any tool you are holding in your hand.

### Changes
Updated to Forge 1.18.1-39.0.40

----------------------- 0.2.0 Changes -----------------------
### Additions
- Added a vein miner keybind (`` ` `` by default)
- Added tree chopper upgrade to the axe
- Added leaf miner upgrade to the axe
- Added tree stripper upgrade to the axe
- Added 3x3 mining to the pickaxe and shovel
- Added vein mining to pickaxe
	- Only works on ores
	- Fortune and silk touch effects are applied to all broken ores
- Added auto smelting upgrade
	- Right now it doesn't work with vein mining or 3x3 mining (I can't figure out why). Will be fixed later.
- Added torch placer upgrade onto pickaxe
	- Right-Click to place a torch, costs 10 durability

### Changes
- Removed the Fortune I and II nodes from all tools
- Updated to Forge 1.18.1-39.0.19

----------------------- 0.1.2 Changes -----------------------
### Additions
- Added a broken tooltip when a tool is broken
- Holding `Shift` on a tooltip now shows the skills it has
	- Includes the points in infinite skills
- Infinite skill nodes now show the amount of points put in them

### Changes
- Updated to Forge 1.18.1-39.0.10

----------------------- 0.1.1 Changes -----------------------
### Additions
- Hoes now get exp when used
- Shovels now get exp when used to make paths
- Axes now get exp when striping logs, unwaxing copper ...

### Fixes
- Fixed Hoes not being able to hoe
- Fixed Shovel not being able to make paths
- Fixed Axe not being able to strip logs

----------------------- 0.1.0 Changes -----------------------
### Additions
- Added the crystal axe
- Added the crystal hoe
	- Currently doesn't level up from tilling dirt, just breaking blocks
- Added the crystal shovel

### Fixes
- Fixed tools only having 10 durability (oops)
- Fixed skill tree Buttons getting stuck on the top of the screen when dragging
- Fixed skill descriptions not showing up when you hover over them

----------------------- 0.0.2 Changes -----------------------
- Tools can now be repaired at anvil with a crystal item
- Tools no longer break
- New textures!
