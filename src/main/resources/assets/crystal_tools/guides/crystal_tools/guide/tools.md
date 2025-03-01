---
navigation:
  title: Tools
  icon: crystal_pickaxe
  parent: index.md
item_ids:
  - crystal_tools:crystal_pickaxe
  - crystal_tools:crystal_axe
  - crystal_tools:crystal_shovel
  - crystal_tools:crystal_hoe
  - crystal_tools:crystal_sword
  - crystal_tools:crystal_aiot
  - crystal_tools:crystal_bow
  - crystal_tools:crystal_trident
  - crystal_tools:crystal_fishing_rod
  - crystal_tools:crystal_shield
---
# Tools
## Overview
Crystal tools can be crafted with <ItemLink id="crystal" />s and <ItemLink id="netherite_stick" />s

<Row fullWidth={false}>
  <Column>
    <RecipeFor id="crystal_pickaxe" />
    <RecipeFor id="crystal_axe" />
    <RecipeFor id="crystal_shovel" />
    <RecipeFor id="crystal_bow" />
    <RecipeFor id="crystal_trident" />
  </Column>
  <Column>
    <RecipeFor id="crystal_hoe" />
    <RecipeFor id="crystal_sword" />
    <RecipeFor id="crystal_aiot" />
    <RecipeFor id="crystal_fishing_rod" />
    <RecipeFor id="crystal_shield" />
  </Column>
</Row>

When you use these tools, that will gain skill points.
You can open their skill trees with `k` to unlock new skills.

## Skills
### All Tools
- Durability
- Auto Repair
  - The tools will repair itself over time
### Mining Tools (Pickaxe, axe, shovel, hoe, AIOT)
- Mining Speed
- Silk Touch
- Fortune
- Auto Smelting
  - Items mined will automatically be smelted if possible
- 3x3 Mining
  - Tools will mine in a 3x3 area
- Auto Pickup
  - Blocks mined will automatically go into your inventory if there is space
- Mode Switch
  - Allows you to upgrade both Fortune and Silk Touch and switch between then. (`m` by default)
  - You can also change the mining mode (3x3 vs 1x1) by holding shift and pressing `m`
  - Auto smelting can also be turned off with ctrl and `m`

### Pickaxe
- Vein Mining
  - Holding `~` will mine an entire ore vein
  - It will apply silk touch, fortune, and auto smelt to every block mined
  - Add more points will increase the number of blocks that can be mined
- Torch Placer
  - Uses 10 durability to place a torch

### Axe
- Tree Chopper
  - Holding `~` will chop down an entire tree
  - Holding `~` and right-clicking will automatically strip the entire tree
  - Add more points to increase the number of logs mined
- Leaf Miner
  - Allows to the axe to mine leaves efficiently

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

### Trident
- Attack Damage
  - Increases damage dealt when hitting people with the trident (left-clicking)
- Projectile Damage
  - Increases the damage dealt when throwing the trident
- Projectile Speed
- Riptide
- Loyalty
- Channeling
  - Adding more points increases the lightning damage
- Riptide Toggle
  - Allows you to toggle between Loyalty and Riptide with `m`
- Always Riptide
  - Allows you to Riptide even when it isn't raining
- Instant Loyalty
  - The trident will instantly go back to your inventory when it hits something instead of flying back to you

### Bow
- Arrow Damage
- Arrow Speed
- Draw Speed
- Flame
- Infinity
- Arrow Knockback
- Tipped Arrows
  - Poison
  - Weakness
  - Slowness
  - Wither
- Target Mobs
  - You can select mobs (by looking at them while aiming)
  - Arrows fired will auto-target that mob

### Fishing Rod
- Lure
- Luck of the Sea
- Chance to Double Drops
  - 20% Per level

### Shield
- Armor
  - Increases your armor when you have the shield in you off-hand
- Thorns
  - Deals damage when you block mobs with the shield (25% chance per level to do 3 damage)
- Reduce Cooldown
  - Decreases the cooldown when the shield is disabled (if you have all of the points in this, the shield can't be disabled)
- Shield Bash
  - Increases the attack damage of the shield
- Flaming Shield
  - Mobs blocked by the shield will catch on fire
- Slow Mobs
  - Mobs blocked by this shield will be slowed
- Totem SLot
  - Allows you to attach totems of undying to the shield. They trigger like normal totems
  - Craft with a totem of undying to add it
- Shield Knockback
  - Mobs blocked by this shield will be knocked back
- Poison Mobs
  - Mobs blocked by this shield will be poisoned
- Wither Mobs
  - Mobs blocked by this shield will be withered
- Target Mobs
  - You can select mobs (by looking at them while blocking)
  - Any projectiles blocked will automatically target that mob