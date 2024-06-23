# TODO List
## Features
### New Levelable Things
- Shield
- Backpack
  - Trash (separate screen)
  - Exp Storage ? (mode switch to turn on and off)
  - Transparency on filter slots
  - NBT Matching filter
  - Change screen size so you need or don't need a scroll bar breaks things
  - Limit rows, will be easy with the limit to infinite nodes item below
  - Prio for multiple backpacks / pickup order
  - Match backpack items filter
  - Nerf skill gain
  - 2x2 compressions
  - Quark integration (no idea how to do this)
  - Shift right click into other mods (ae2 terminal)
  - Maybe toggle buttons in gui (on right maybe)
    - Pickup
    - Trash
- Crystal Generator
- Spawner
- Quarry
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

### New Blocks
- Crystal glass (conn texture)

### Other
- Make or / not / unless node connections look different somehow
- Pressing k with no tools will go to equipped items, buttons at the top of the screen to arrow between equipped / held items
- Adds tools to respective tool tags
- Breaking animation on multibreaks
- Configure color of highlight?
- Advancements

## Bugs
- Furnace skill screen doesn't fully refresh when open when point is gained
- Block break event triggers 10 times instead of 9 on 3x3
- Items picked up with autopickup don't go into backpacks

## Code Improvements
- Packet interface
- Do something better with tooltips
- Data gen ore gen
- Event driven block breaking things (smelting / 3x3 / vein mining) maybe

### 1.21 Things
- Change to using data components
- maybe try tags and events instead of needing my own type
- Event / code client / server organization
- Limit to infinite nodes (breaking json change so wait until 1.21)
  - Require x points in infinite node as requirement?
