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
  - Add more fuels (gems, metals, foods ...)
  - Don't waste power upgrade
  - Redstone to disable (might not need upgrade?)
  - Increase rf/tick (generation and output)
  - Increase burn time
  - RF storage
  - Add crystal fuel? Upgrade to turn generator into fuel maker for fuel generator?
    - Maybe solid fuel too?
  - JEI Plugin things
- Spawner
- Quarry
  - Model off of build craft quarry
  - Laser barriers and breaking animations?
  - Things to level:
    - Speed
    - Reduce energy cost?
    - Remove liquids (put in tank / void options?)
    - Trash filters
    - Auto output to inventories?
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
- Attributes don't show up on items. Still works though
- Remove JEI access transformers