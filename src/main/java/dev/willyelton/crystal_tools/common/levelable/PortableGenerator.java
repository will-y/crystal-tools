package dev.willyelton.crystal_tools.common.levelable;

import com.google.common.base.Predicates;
import dev.willyelton.crystal_tools.common.capability.Levelable;
import dev.willyelton.crystal_tools.common.compat.curios.CuriosCompatibility;
import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.common.datamap.GeneratorFuelData;
import dev.willyelton.crystal_tools.common.inventory.PortableGeneratorInventory;
import dev.willyelton.crystal_tools.common.inventory.container.PortableGeneratorContainerMenu;
import dev.willyelton.crystal_tools.common.levelable.block.entity.data.ILevelableContainerData;
import dev.willyelton.crystal_tools.common.levelable.block.entity.data.ItemStackLevelableContainerData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static dev.willyelton.crystal_tools.common.levelable.block.entity.CrystalGeneratorBlockEntity.getFuelData;
import static dev.willyelton.crystal_tools.utils.StringUtils.intToCompactString;

public class PortableGenerator extends Item implements LevelableItem {
    public PortableGenerator(Item.Properties properties) {
        super(properties.stacksTo(1).fireResistant());
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(
                    new PortableGeneratorMenuSupplier(this, stack),
                    registryFriendlyByteBuf -> {
                        ItemStack.OPTIONAL_STREAM_CODEC.encode(registryFriendlyByteBuf, stack);
                    });
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean canFitInsideContainerItems() {
        return false;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        IEnergyStorage energyStorage = stack.getCapability(Capabilities.EnergyStorage.ITEM);

        if (energyStorage != null && energyStorage.getEnergyStored() > 0) {
            return Math.round(13.0F - 13.0F * getFillLevel(stack));
        }

        return 0;
    }

    @Override
    public int getBarColor(ItemStack stack) {
        // 180 / 355 -> 355
        return Mth.hsvToRgb(0.5F + getFillLevel(stack) / 2F, 1.0F, 1.0F);
    }

    private float getFillLevel(ItemStack stack) {
        IEnergyStorage energyStorage = stack.getCapability(Capabilities.EnergyStorage.ITEM);

        if (energyStorage != null && energyStorage.getEnergyStored() > 0) {
            return (energyStorage.getMaxEnergyStored() - energyStorage.getEnergyStored()) / (float) energyStorage.getMaxEnergyStored();
        }

        return 0;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltipDisplay, tooltipAdder, flag);

        tooltipAdder.accept(Component.literal(String.format("\u00A7b%s FE / %s FE", intToCompactString(getEnergy(stack)), intToCompactString(getCapacity(stack)))));
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, @Nullable EquipmentSlot slot) {
        tick(stack, level, List.of(entity), entity.getOnPos(), () -> getNextFuelStack(stack, level), true);
    }

    public static ItemStack getNextFuelStack(ItemStack stack, Level level) {
        PortableGeneratorInventory inventory = getInventory(stack, level);
        if (inventory != null) {
            return inventory.nextItem();
        }

        return ItemStack.EMPTY;
    }

    public static void tick(ItemStack stack, Level level, List<Entity> entities, BlockPos pos, Supplier<ItemStack> fuelSupplier, boolean levelItems) {
        Player player = null;
        if (level.getGameTime() % 20 == 4) {
            List<ItemStack> stacks = new ArrayList<>();
            for (Entity entity : entities) {
                IItemHandler handler = entity.getCapability(Capabilities.ItemHandler.ENTITY);
                if (handler != null) {
                    for (int i = 0; i < handler.getSlots(); i++) {
                        if (!stack.isEmpty()) {
                            stacks.add(handler.getStackInSlot(i));
                        }
                    }
                }

                if (entity instanceof Player p) {
                    player = p;
                    stacks.addAll(CuriosCompatibility.getItemInCurios(player, Predicates.alwaysTrue()));
                }
            }

            int leftOverEnergy = distributeEnergy(stack.getOrDefault(DataComponents.FE_STORED, 0), stacks);
            stack.set(DataComponents.FE_STORED, leftOverEnergy);
        }

        GeneratorFuelData fuelData = stack.get(DataComponents.BURNING_ITEM_DATA);
        if (fuelData == null) {
            // Burn Item
            ItemStack toBurn = fuelSupplier.get();
            if (!toBurn.isEmpty()) {
                GeneratorFuelData newFuelData = getFuelData(toBurn, stack.getOrDefault(DataComponents.FOOD_GENERATOR, false),
                        stack.getOrDefault(DataComponents.METAL_GENERATOR, false), stack.getOrDefault(DataComponents.GEM_GENERATOR, false));

                if (newFuelData == null) {
                    int burnTime = toBurn.getBurnTime(null, level.fuelValues());

                    if (burnTime > 0) {
                        newFuelData = new GeneratorFuelData(burnTime, 0);
                    }
                }

                if (newFuelData != null) {
                    newFuelData = new GeneratorFuelData((int) Math.ceil(newFuelData.burnTime() * (1 + stack.getOrDefault(DataComponents.FUEL_EFFICIENCY.get(), 0F))), newFuelData.bonusGeneration());
                    stack.set(DataComponents.BURNING_ITEM_DATA, newFuelData);
                    stack.set(DataComponents.MAX_BURN_TIME, newFuelData.burnTime());
                    fuelData = newFuelData;
                    Levelable levelable = stack.getCapability(dev.willyelton.crystal_tools.common.capability.Capabilities.ITEM_SKILL, level.registryAccess());
                    if (levelable != null && levelItems) {
                        levelable.addExp(level, pos, player, (float) Math.ceil(CrystalToolsConfig.PORTABLE_GENERATOR_SKILL_POINTS_PER_BURN_TIME.get() * newFuelData.burnTime()));
                    }
                }
            }
        }

        // Create energy if burning
        int energy = getEnergy(stack);
        int capacity = getCapacity(stack);
        if (fuelData != null) {
            int toGain = getGeneration(stack, fuelData);

            if (capacity - energy < toGain) {
                return;
            }

            stack.set(DataComponents.GENERATED_LAST_TICK, toGain);
            stack.set(DataComponents.FE_STORED, energy + toGain);
            GeneratorFuelData newFuelData = new GeneratorFuelData(fuelData.burnTime() - 1, fuelData.bonusGeneration());
            if (newFuelData.burnTime() > 0) {
                stack.set(DataComponents.BURNING_ITEM_DATA, newFuelData);
            } else {
                stack.remove(DataComponents.BURNING_ITEM_DATA);
            }
        } else {
            stack.remove(DataComponents.GENERATED_LAST_TICK);
        }
    }

    private static int distributeEnergy(int energy, List<ItemStack> itemsToCharge) {
        for (ItemStack stack : itemsToCharge) {
            if (energy <= 0) break;

            IEnergyStorage storage = stack.getCapability(Capabilities.EnergyStorage.ITEM);
            if (storage != null) {
                energy = energy - storage.receiveEnergy(energy, false);
            }
        }

        return energy;
    }

    public static PortableGeneratorInventory getInventory(ItemStack stack, Level level) {
        IItemHandler inventory = stack.getCapability(Capabilities.ItemHandler.ITEM);
        if (inventory == null) {
            return new PortableGeneratorInventory(0);
        } else if (inventory instanceof PortableGeneratorInventory portableGeneratorInventory) {
            portableGeneratorInventory.setLevel(level);
            return portableGeneratorInventory;
        } else {
            return new PortableGeneratorInventory(0);
        }
    }

    public static int getCapacity(ItemStack stack) {
        return stack.getOrDefault(DataComponents.FE_CAPACITY, 0) * CrystalToolsConfig.PORTABLE_GENERATOR_FE_STORAGE_PER_LEVEL.get()
                + CrystalToolsConfig.PORTABLE_GENERATOR_BASE_FE_STORAGE.get();
    }

    public static int getEnergy(ItemStack stack) {
        return stack.getOrDefault(DataComponents.FE_STORED, 0);
    }

    public static int getGeneration(ItemStack stack, GeneratorFuelData fuelData) {
        if (fuelData == null) return 0;

        return fuelData.bonusGeneration() + CrystalToolsConfig.PORTABLE_GENERATOR_BASE_FE_GENERATION.get() +
                stack.getOrDefault(DataComponents.FE_GENERATION, 0) * CrystalToolsConfig.PORTABLE_GENERATOR_FE_GENERATION_PER_LEVEL.get();
    }

    private record PortableGeneratorMenuSupplier(PortableGenerator generatorItem, ItemStack stack) implements MenuProvider {
        @Override
        public Component getDisplayName() {
            return stack.getHoverName();
        }

        @Override
        public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
            return new PortableGeneratorContainerMenu(containerId, playerInventory, createDataAccess(stack), stack);
        }

        private ILevelableContainerData createDataAccess(ItemStack stack) {
            return new ItemStackLevelableContainerData(stack) {
                @Override
                protected int getExtra(int index) {
                    return switch (index) {
                        case 3 -> stack.getOrDefault(DataComponents.BURNING_ITEM_DATA, new GeneratorFuelData(0, 0)).burnTime();
                        case 4 -> stack.getOrDefault(DataComponents.MAX_BURN_TIME, 0);
                        case 5 -> getEnergy(stack);
                        case 6 -> getCapacity(stack);
                        case 7 -> stack.getOrDefault(DataComponents.GENERATED_LAST_TICK, 0);
                        default -> 0;
                    };
                }

                @Override
                protected void setExtra(int index, int value) {

                }

                @Override
                protected int getExtraDataSize() {
                    return 5;
                }
            };
        }
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged && oldStack.is(newStack.getItem());
    }

    @Override
    public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
        return !oldStack.is(newStack.getItem());
    }
}
