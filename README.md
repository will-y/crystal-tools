# Crystal Tools
This mod adds a new class of tools to Minecraft called crystal tools. They are meant to be a post-netherite tool that can keep getting better as you use it.

You can download the mod on [Curse Forge](https://www.curseforge.com/minecraft/mc-mods/upgradable-crystal-tools) or from [releases](https://github.com/will-y/crystal-tools/releases).

Please report any bugs you find in the Issues tab or in the comments.
You can also suggest features or ask questions there.

## Blocks
Currently, there are three blocks added to the game

- Crystal Ore
- Deepslate Crystal Ore
- Crystal Block

## World Generation
Deepslate Crystal Ore Generates Below Y -44 in veins of size 5.
The ore generates very rarely, currently only about one vein per chunk.

## Tools
Tools gain experience by using them. You can see how much exp you need to get to the next level by looking at the tooltip. You can open the skill tree by pressing `k` by default. Skill points can be using to get a new skill or to fully repair your tool.

The following tools/armor pieces are implemented:

- Crystal Pickaxe
- Crystal Axe
- Crystal Shovel
- Crystal Hoe
- Crystal Sword
- Crystal AIOT (All In One Tool)
- Crystal Bow
- Crystal Helmet
- Crystal Chestplate
- Crystal Leggings
- Crystal Boots

## Upgrades
There will be upgrades that are common to most tools, and tools will each get unique upgrades as well. Below is a list of all currently implemented upgrades.

### All Tools
- Durability
- Auto-Repair

### Mining Tools (Pickaxe, axe, shovel, hoe)
- Mining Speed
- Silk Touch
- Fortune
- Auto Smelting
    - This currently doesn't work with 3x3 mining or vein mining, you can only have this or one of those.
- 3x3 Mining
- Mode Switch
  - Allows you to upgrade both Fortune and Silk Touch on your tool, and switch between them. (`m` by default)

### Pickaxe
- Vein Mining
	- Will mine out an entire vein of ore when you are holding the vein miner key.
	- It applies silk touch and fortune to evey one mined.
- Torch Placer
	- Places a torch when you right-click on a block, uses 10 durability.

### Axe
- Tree Chopper
    - Will chop an entire tree if you hold the vein miner key
- Tree Stripper
    - Strips an entire tree when you hold the vein miner key
- Leaf Miner
    - Allows axe to mine leaves efficiently.

### Sword
- Attack Damage
- Attack Speed
- Fire Aspect
- Knockback
- Knockback Resistance
	- You take less knockback when holding the sword
- Lifesteal
	- You heal each time you hit an enemy
- Looting

### Bow
- Arrow Damage
- Arrow Speed
- Flame
- Infinity
- Arrow Knockback

### All Armor
- Protection
- Fire Protection
- Blast Protection
- Projectile Protection
- Thorns
- Armor Value Bonus
- Toughness Bonus

### Helmet
- Aqua Affinity
- Respiration
- Night Vision

### Chestplate
- Health Bonus

### Leggings
- Speed Bonus

### Boots
- Feather Falling
- Depth Strider
- Soul Speed
- Frost Walker

## Crafting Recipes
### Tools
#### Pickaxe
![Pickaxe Crafting](/img/crafting/pickaxe.png)

#### Axe
![Axe Crafting](/img/crafting/axe.png)

#### Shovel
![Shovel Crafting](/img/crafting/shovel.png)

#### Hoe
![Hoe Crafting](/img/crafting/hoe.png)

#### Sword
![Sword Crafting](/img/crafting/sword.png)

#### AIOT
![AIOT Crafting](/img/crafting/aiot.png)

### Armor
#### Helmet
![Helmet Crafting](/img/crafting/helmet.png)

#### Chestplate
![Chestplate Crafting](/img/crafting/chestplate.png)

#### Leggings
![Leggings Crafting](/img/crafting/leggings.png)

#### Boots
![Boots Crafting](/img/crafting/boots.png)

### Misc
#### Netherite Stick
![Netherite Stick Crafting](/img/crafting/netherite_stick.png)

#### Crystal Block
![Crystal Block Crafting](/img/crafting/block.png)

#### Crystal
![Crystal Crafting](/img/crafting/crystal.png)

## Config
The following config options are available. The default values are in parentheses.
You can change these values in `config/crystal_tools/toml`.

`base_experience_cap` (50): Starting EXP requirements for Tools and Armor. Range: 1 - 10000.  
`experience_multiplier` (1.25): Multiplier for max experience to the next level. Range: 1.0 - 100.0.  
`armor_experience_boost` (2.0): Multiplies how much experience Armor gets, experience is calculated by `EXP_GAINED` = DAMAGE_TAKEN * ARMOR_EXPERIENCE_BOOST. Range: 0.1 - 10000.0.  
`bow_experience_boost` (1.0): Multiplies how much experience Bows get, experience is calculated by `EXP_GAINED` = UNMITIGATED_DAMAGE_DONE * BOW_EXPERIENCE_BOOST. Range: 0.1 - 10000.0.  
`sword_experience_boost` (1.0): Multiplies how much experience Swords get, experience is calculated by EXP_GAINED = UNMITIGATED_DAMAGE_DONE * SWORD_EXPERIENCE_BOOST. Range: 0.1 - 10000.0.  
`upgrade_screen_background` ("cracked_deepslate_tiles"): Determines the block texture to use for the background of the upgrade screen. Must be a vanilla block's resource location. [Here](https://minecraft.fandom.com/wiki/Java_Edition_data_values#Blocks) is a list of options from the wiki.
