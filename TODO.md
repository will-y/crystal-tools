# TODO List
## Features
### New Levelable Things
- Bow
  - Add looting (IItemExtension#supportsEnchantment)
- Backpack
  - Trash (separate filters?)
  - Exp Storage ? (mode switch to turn on and off)
  - Transparency on filter slots
  - Component Matching filter
  - Change screen size so you need or don't need a scroll bar breaks things
  - Limit rows, will be easy with the limit to infinite nodes item below
  - Prio for multiple backpacks / pickup order
  - Client indexes don't get updated on scroll. Might cause problems but doesn't seem to
- Spawner
- Quarry
  - To do later:
    - Rename the backpack stuff that is now used also for the quarry
    - Store fluids (or just output to tanks?)
    - Fluid trash filter
    - Mine 1 chunk at a time to keep few chunks loaded
    - Second pass on graphics
    - Holding certain items renders some things
      - Holding quarry renders valid placements
      - Holding stabilizer shows lines on other stabilizers?
    - Y smoothing function for particles isn't really great because it goes really high when at the bottom of the world and too low when mining close
    - Block state for finished (also grey out be renderer or something)
    - https://gist.github.com/gigaherz/b8756ff463541f07a644ef8f14cb10f5
    - Some particles or something along the lasers + on stabilizer connections
    - Guide-lines when there are only 2 stabilizers
    - Show particles when you are down at the bottom too
    - Breaking stabilizers breaks quarry?
    - Some screen that shows loaded chunks or something?
    - Don't allow xp leveling or make it a lot harder
    - Test on server
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
- Furnace should auto split on insert
- Exclude hoppers from auto outputting (maybe create a tag?)
- GLM for auto smelting
- Armor piercing upgrade: https://docs.neoforged.net/docs/entities/livingentity/#damage-events
- Something to do with extra points (turn to xp?)
- Gradle task or something to generate the 2 new datapacks

## Bugs
- Furnace skill screen doesn't fully refresh when open when point is gained
- Block break event triggers 10 times instead of 9 on 3x3
- Items picked up with autopickup don't go into backpacks
- Can instamine things by breaking torches (maybe check if can mine the original block?)
- Sometimes leveling skills scrolls to the top of the menu

## Code Improvements
- Data gen ore gen

## 1.21.6
Eventually
- Upgrading backpack in curios slot
- Better way to get tools on server (could technically change positions) (this is relating to client to server packets)
- Datapack validation (make sure all requirements point to valid nodes / items)...
- Resizing the screen after you spend a point resets it?
- Maybe: Move repairing items to `EntityTickEvent` and iterate the inventory (would allow curios items to be ticked + repairing for vanilla tools if wanted)
- Use actions should probably be datacomponents and all handled either in that event or the base levelable class
- Block entities should have and use levelable capability
- Tooltip is wrong for unused items
- Vanilla bow tree (mixins probably)
- Vanilla trident tree
- Should probably use interface for capability actually, if possible (HolderLookup.Provider)
- Block highlighting broke in 1.21.6