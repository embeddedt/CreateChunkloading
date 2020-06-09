package com.grimmauld.createintegration.recipes;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.grimmauld.createintegration.CreateIntegration;
import com.grimmauld.createintegration.blocks.ModBlocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;

public class RollingRecipe extends BeltMachineRecipe {
    public static final Serializer SERIALIZER = new Serializer();


    public RollingRecipe(ResourceLocation id, Ingredient input, ItemStack output, int processingTime) {
        super(id, input, output, processingTime);
    }


    @Override
    public String toString() {
        return "RollingRecipe [input=" + this.input + ", output=" + this.output + ", id=" + this.id + "]";
    }

    @Nonnull
    @Override
    public IRecipeType<?> getType() {
        return CreateIntegration.ROLLING_RECIPE;
    }


    @Nonnull
    @Override
    public ItemStack getIcon() {
        return new ItemStack(new BlockItem(ModBlocks.ROLLING_MACHINE, new Item.Properties()));
    }

    @Nonnull
    @Override
    public IRecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    private static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>>
            implements IRecipeSerializer<RollingRecipe> {

        Serializer() {
            this.setRegistryName(new ResourceLocation(CreateIntegration.modid, "rolling"));
        }

        @Nonnull
        @Override
        public RollingRecipe read(@Nonnull ResourceLocation recipeId, @Nonnull JsonObject json) {
            final JsonElement inputElement = JSONUtils.isJsonArray(json, "input")
                    ? JSONUtils.getJsonArray(json, "input")
                    : JSONUtils.getJsonObject(json, "input");
            final Ingredient input = Ingredient.deserialize(inputElement);

            final int processingDuration = JSONUtils.hasField(json, "duration") ? JSONUtils.getInt(json, "duration") : 100;
            final ItemStack output = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, "output"));

            return new RollingRecipe(recipeId, input, output, processingDuration);
        }

        @Override
        public RollingRecipe read(@Nonnull ResourceLocation recipeId, @Nonnull PacketBuffer buffer) {
            final Ingredient input = Ingredient.read(buffer);
            final ItemStack output = buffer.readItemStack();
            final int processingDuration = buffer.readInt();

            return new RollingRecipe(recipeId, input, output, processingDuration);
        }

        @Override
        public void write(@Nonnull PacketBuffer buffer, RollingRecipe recipe) {
            recipe.input.write(buffer);
            buffer.writeItemStack(recipe.output);
            buffer.writeInt(recipe.getProcessingDuration());
        }
    }
}
