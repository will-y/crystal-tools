# TODO List
## Features
### New Levelable Things
- Bow
  - Add looting (IItemExtension#supportsEnchantment)
- Shield
  - Totem of undying charges
- Backpack
  - Trash (separate filters?)
  - Exp Storage ? (mode switch to turn on and off)
  - Transparency on filter slots
  - Component Matching filter
  - Change screen size so you need or don't need a scroll bar breaks things
  - Limit rows, will be easy with the limit to infinite nodes item below
  - Prio for multiple backpacks / pickup order
  - Client indexes don't get updated on scroll. Might cause problems but doesn't seem to
- Crystal Generator
  - JEI Plugin things
- Spawner
- Quarry
  - Model off of build craft quarry
  - Laser barriers and breaking animations?
  - Things to level:
    - Reduce energy cost?
    - Remove liquids (put in tank / void options?)
    - Trash filters (make a new more generic filter screen that can work with backpack and quarry)
  - Left to do:
    - Do actual energy costs
    - Make sure you place it near the area it is mining
    - Chunkloading upgrade
    - Actually gain skill points
    - Make actual skill tree
    - Pickup storing things
    - Crafting recipe
    - Some different texture (maybe generator too)
    - Block state for on / off
    - Start a few blocks above the defined area?
    - Graphics (at least a laser beam and small breaking animation)
    - Model (or at least texture) for the stabilizers
- Pump
- Hopper
  - Inventory space
  - Speed
  - Filters
- Chest
- Auto crafter
- Magnet
- Enchanting Table
- Anvil
- Crystal Arrow
- Shulker
- Wolf
- Cobble generator
- Battery
- Maybe crusher

### New Blocks
- Crystal glass (conn texture)

### Other
- Pressing k with no tools will go to equipped items, buttons at the top of the screen to arrow between equipped / held items
- Breaking animation on multibreaks
- Configure color of highlight?
- Advancements
- https://jademc.readthedocs.io/en/latest/plugins20/getting-started/
- Look at mekanism vein mining, might want to call playerBreak.
- Fix xp gaining points being controlled by the client (hard because adding skill points is different for blocks and items)

### Breaking Improvements
- Rename furnace and rocket skill trees to be consistent
- Look into using attribute modifiers 

## Bugs
- Furnace skill screen doesn't fully refresh when open when point is gained
- Block break event triggers 10 times instead of 9 on 3x3
- Items picked up with autopickup don't go into backpacks
- Can instamine things by breaking torches (maybe check if can mine the original block?)

## Code Improvements
- Data gen ore gen

### 1.21 Things
- Sometimes leveling skills scrolls to the top of the menu
- Remove JEI access transformers

### Next breaking changes
- Skill data should probably be a capability, at least on items
- Allow for more general skill data, Attributes, DataComponents?