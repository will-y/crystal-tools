package dev.willyelton.crystal_tools.common.events;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.levelable.CrystalBackpack;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.List;
import java.util.Optional;

@EventBusSubscriber(modid = CrystalTools.MODID)
public class BlockEvents {
    @SubscribeEvent
    public static void breakEvent(BlockEvent.BreakEvent event) {
        // Backpack levels
        CrystalBackpack.addXpToBackpacks(event.getPlayer(), 1);
    }

    @SubscribeEvent
    public static void dropEvent(BlockDropsEvent event) {
        ItemStack tool = event.getTool();

        if (tool.isEmpty()) return;

        Entity entity = event.getBreaker();
        ServerLevel level = event.getLevel();

        if (entity instanceof ServerPlayer player) {
            List<ItemEntity> drops = event.getDrops();

            if (tool.getOrDefault(DataComponents.AUTO_SMELT, false) && !tool.getOrDefault(DataComponents.DISABLE_AUTO_SMELT, false)) {
                for (ItemEntity item : drops) {
                    ItemStack stack = item.getItem();
                    Optional<RecipeHolder<SmeltingRecipe>> recipeOptional = level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(stack), level);

                    if (recipeOptional.isPresent()) {
                        SmeltingRecipe recipe = recipeOptional.get().value();
                        popExperience(level, player, recipe.getExperience());
                        ItemStack result = recipe.getResultItem(level.registryAccess()).copy();
                        result.setCount(stack.getCount() * result.getCount());

                        if (!result.is(Items.AIR)) {
                            item.setItem(result);
                        }
                    }
                }
            }

            if (tool.getOrDefault(DataComponents.AUTO_PICKUP, false)) {
                boolean pickedUp = false;
                for (ItemEntity item : drops) {
                    ItemStack stack = item.getItem();
                    if (!player.getInventory().add(stack)) {
                        item.setItem(stack);
                    } else {
                        pickedUp = true;
                    }
                }

                if (pickedUp) {
                    player.connection.send(new ClientboundSoundPacket(Holder.direct(SoundEvents.ITEM_PICKUP), SoundSource.PLAYERS, player.getX(), player.getY(), player.getZ(),  0.2F, (level.random.nextFloat() - level.random.nextFloat()) * 1.4F + 2.0F, level.getRandom().nextLong()));
                }
            }
        }
    }

    private static void popExperience(ServerLevel level, LivingEntity entity, float experience) {
        int fullXp = (int) Math.floor(experience);
        float partialXp = experience - fullXp;

        if (partialXp > 0 && Math.random() < partialXp) {
            ExperienceOrb.award(level, entity.position(), fullXp + 1);
        } else {
            ExperienceOrb.award(level, entity.position(), fullXp);
        }
    }
}
