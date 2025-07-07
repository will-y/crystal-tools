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
  - Use Crystals to expand size?
- Auto crafter
  - Speed
  - Energy Reduction
  - Number of Recipes
- Magnet
  - Item Speed
  - Durability
  - Instant Grab
  - Filters
  - XP
  - Mobs (on activate)
- Enchanting Table
  - Reduce bookshelves required
  - Increase enchantibility
  - Reduce XP Cost
- Anvil
- Crystal Arrow
- Shulker
- Wolf
- Cobble generator
- Battery
- Maybe crusher
- Potion Factory
  - Speed
  - inventory slots
  - number of recipes
    - Store multiple recipes, will do them in order or round robin
  - Blaze cost reduction
  - RF Instead of blaze (maybe)
  - Spring water
    - Adjacent water blocks will fill it up
      - Maybe do a cool model thing to show it picking up water?

### New Blocks
- Crystal glass (conn texture)

### Other
- Pressing k with no tools will go to equipped items, buttons at the top of the screen to arrow between equipped / held items
- Breaking animation on multibreaks
- Configure color of highlight?
- Advancements
- https://jademc.readthedocs.io/en/latest/plugins20/getting-started/
- Look at mekanism vein mining, might want to call playerBreak.
- Furnace should auto split on insert
- Exclude hoppers from auto outputting (maybe create a tag?)
- GLM for auto smelting
- Armor piercing upgrade: https://docs.neoforged.net/docs/entities/livingentity/#damage-events
- Something to do with extra points (turn to xp?)

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
- Vanilla bow tree (mixins probably)
- Vanilla trident tree
- Better way to get tools on server (could technically change positions) (this is relating to client to server packets)
- Maybe: Move repairing items to `EntityTickEvent` and iterate the inventory (would allow curios items to be ticked + repairing for vanilla tools if wanted)
  - Should be fine now that cap lookup is cheaper
- Maybe go through all get capability calls and pass in null
  - Possibly add an `isFull` boolean to `LevelableStack`? Only should need full on the upgrade screen
- I think quarry lasers are in the wrong level render stage