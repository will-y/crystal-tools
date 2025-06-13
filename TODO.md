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

Before full release
- Enchantment config option is broken
- remove as much as possible from the tool classes
- All checks should be registry or datacomponent checks, not instanceof checks
- Upgrading backpack in curios slot
- Quarry cube is black again
- Object in the datamap
  - Rl for tree
  - Base config
  - Exp scaling

Eventually
- Better way to get tools on server (could technically change positions)
- Datapack validation (make sure all requirements point to valid nodes / items)...
- try out some vanilla skill trees
- Resizing the screen after you spend a point resets it?
- Figure out if I want to keep disabling tools
  - Tooltip is main concern (skilldata on component would fix)
  - Partial `Levelable` without level access could also work?
  - Actually disabled isn't even on the levelable object. I guess could look up config by item somehow?
  - Config by item map initialized in some event (when items are ready)
- Maybe: Move repairing items to `EntityTickEvent` and iterate the inventory (would allow curios items to be ticked + repairing for vanilla tools if wanted)
- Config option for exp for non-crystal tools (maybe make it a part of the datamap). There could be lots of other useful stuff in there too
- Use actions should probably be datacomponents and all handled either in that event or the base levelable class