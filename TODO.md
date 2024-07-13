# TODO List
## Features
### New Levelable Things
- Shield
- Backpack
  - Trash (separate filters?)
  - Exp Storage ? (mode switch to turn on and off)
  - Transparency on filter slots
  - Component Matching filter
  - Change screen size so you need or don't need a scroll bar breaks things
  - Limit rows, will be easy with the limit to infinite nodes item below
  - Prio for multiple backpacks / pickup order
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
- Breaking animation on multibreaks
- Configure color of highlight?
- Advancements

## Bugs
- Furnace skill screen doesn't fully refresh when open when point is gained
- Block break event triggers 10 times instead of 9 on 3x3
- Items picked up with autopickup don't go into backpacks
- Can instamine things by breaking torches (maybe check if can mine the original block?)

## Code Improvements
- Do something better with tooltips
- Data gen ore gen

### 1.21 Things
- Sometimes leveling skills scrolls to the top of the menu
- Use BlockDropsEvent for handling smelting and auto pickup
  - Should remove the need for LevelUtils
- Attributes don't show up on items. Still works though
- Remove JEI access transformers
- Split config into client and common