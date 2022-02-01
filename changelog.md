----------------------- 0.6.0 Changes -----------------------
### Additions
- Added lines to show dependencies in the skill trees
- Auto-smelt now drops exp

### Fixes
- Fixed two upgrades in the axe having the same id and messing up the skill tree
- Fixed Pickaxe not being able to 3x3 mine and vein mine
- Fixed being able to level auto-smelt and multimine even when it doesn't work

### Changes
- Updated to Forge 1.18.1-39.0.64
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
