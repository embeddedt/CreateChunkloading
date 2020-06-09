package com.grimmauld.createintegration.recipes;

import com.grimmauld.createintegration.CreateIntegration;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public abstract class BeltMachineRecipe implements IRecipe<IInventory> {

    protected final Ingredient input;
    protected final ItemStack output;
    protected final int processingDuration;
    protected final ResourceLocation id;


    public BeltMachineRecipe(ResourceLocation id, Ingredient input, ItemStack output, int processingTime) {

        this.id = id;
        this.input = input;
        this.output = output;
        this.processingDuration = processingTime;
        CreateIntegration.logger.debug("Loaded " + this.toString());
    }

    @Override
    public boolean matches(IInventory inv, @Nonnull World worldIn) {
        return this.input.test(inv.getStackInSlot(0));
    }

    @Nonnull
    @Override
    public ItemStack getCraftingResult(@Nonnull IInventory inv) {
        return this.output.copy();
    }

    @Nonnull
    @Override
    public ItemStack getRecipeOutput() {
        return this.output;
    }

    public Ingredient getInput() {
        return this.input;
    }

    @Nonnull
    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Nonnull
    @Override
    public abstract IRecipeSerializer<?> getSerializer();


    @Nonnull
    @Override
    public abstract IRecipeType<?> getType();

    public boolean isValid(ItemStack input) {
        return this.input.test(input);
    }

    @Override
    public boolean canFit(int width, int height) {
        return true;
    }

    public int getProcessingDuration() {
        return processingDuration;
    }

    public List<ItemStack> getRollableResults() {
        List<ItemStack> out = new ArrayList<>();
        out.add(this.getRecipeOutput().copy());
        return out;
    }

    public List<ItemStack> getPossibleOutputs() {
        return getRollableResults();
    }
}
