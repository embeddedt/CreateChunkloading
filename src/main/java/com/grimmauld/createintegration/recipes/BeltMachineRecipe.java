package com.grimmauld.createintegration.recipes;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

public abstract class BeltMachineRecipe implements IRecipe<IInventory>{

	private final Ingredient input;
	private final ItemStack output;
	private final int processingDuration;
	private final ResourceLocation id;
	
	
	public BeltMachineRecipe(ResourceLocation id, Ingredient input, ItemStack output, int processingTime) {

		this.id = id;
		this.input = input;
		this.output = output;
		this.processingDuration = processingTime;
		
		

		// This output is not required, but it can be used to detect when a recipe has
		// been
		// loaded into the game.
		System.out.println("Loaded " + this.toString());
	}
	
	
	@Override
	public boolean matches(IInventory inv, World worldIn) {

		// This method is ignored by our custom recipe system, and only has partial
		// functionality. isValid is used instead.
		return this.input.test(inv.getStackInSlot(0));
	}
	
	@Override
	public ItemStack getCraftingResult(IInventory inv) {

		// This method is ignored by our custom recipe system. getRecipeOutput().copy()
		// is used
		// instead.
		return this.output.copy();
	}
	
	@Override
	public ItemStack getRecipeOutput() {
		return this.output;
	}
	
    public Ingredient getInput () {
        return this.input;
    }
	

	@Override
	public ResourceLocation getId() {

		return this.id;
	}

	@Override
	public abstract IRecipeSerializer<?> getSerializer();
	
	@Override
	public abstract IRecipeType<IRecipe<?>> getType();
	
	
	public boolean isValid(ItemStack input) {
		return this.input.test(input);
	}
	
	abstract static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>>
			implements IRecipeSerializer<BeltMachineRecipe>{};

	@Override
	public boolean canFit(int width, int height) {
		return true;
	}

	public int getProcessingDuration() {
		return processingDuration;
	}

	public List<ItemStack> getRollableResults() {
		List<ItemStack> out = new ArrayList<ItemStack>();
		out.add(this.getRecipeOutput().copy());
		return out;
	}

	public List<ItemStack> getPossibleOutputs() {
		return getRollableResults();
	}
}
