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
- Pedestal
  - Later:
    - Show range
    - Screens for configuration / info for specific actions
    - Some indication that the item in the pedestal doesn't do anything
    - Deal with using up a stack when it doesn't have durability (ender pearls to chunkload)
    - Some way to deal with Default parameters better
    - Pull mobs and experience?
    - More actions
      - ender pearl chunkloads
      - hopper auto outputs?
      - tools can mine
      - swords attack
    - Action type should not be an enum, it should be a registry or something
      - Things to define:
        - Name
        - JEI Description
        - Action Factory (creates the action)
          - Generic so context parameter can be passed in?
        - Item Animation (default is spin, would also like an attack and mine one)

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
- Maybe go through all get capability calls and pass in null
  - Possibly add an `isFull` boolean to `LevelableStack`? Only should need full on the upgrade screen
- Support limited nodes on blocks

## 1.21.9 Porting
- Quarry cube rotating based on its speed (stopped when stopped)
  - Dim lasers and cube when off?
- Check generator shift clicking in 1.21