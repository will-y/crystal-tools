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
    - Make actual skill tree
    - Front texture
    - Block state for on / off
    - Start a few blocks above the defined area?
    - https://gist.github.com/gigaherz/b8756ff463541f07a644ef8f14cb10f5
    - Default area when there are no stabilizers?
    - Show guide lines when clicking on not 4 stabilizers
    - Some particles or something along the lasers + on stabilizer connections
    - New laser texture
    - Probably don't need to use custom packet to sync block entity, just override the methods and only send what is useful to the client
    - Some way to see your skill points (want to do badge on button but that might come later)
    - Generator slots only accept fuel
    - Only show options unlocked on config screen
    - Autoselect autooutput
    - Show the block entity renderer when not looking at it
    - Config for base exp scaling
  - To do later:
    - Store fluids (or just output to tanks?)
    - Fluid trash filter
    - Mine 1 chunk at a time to keep few chunks loaded
    - Second pass on graphics
    - Holding certain items renders some things
      - Holding quarry renderes valid placements
      - Holding stabilizer shows lines on other stabilizers?
    - Y smoothing function for particles isn't really great because 
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