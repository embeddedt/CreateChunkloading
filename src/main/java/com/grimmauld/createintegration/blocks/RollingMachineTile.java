package com.grimmauld.createintegration.blocks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.grimmauld.createintegration.Config;
import com.grimmauld.createintegration.CreateIntegration;
import com.grimmauld.createintegration.recipes.RollingRecipe;

import com.simibubi.create.foundation.item.ItemHelper;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

public class RollingMachineTile extends BeltMachineTile {
	
	public RollingMachineTile() {
		super(ModBlocks.ROLLING_MACHINE_TILE);
	}
	
	protected List<ItemStack> applyRecipe() {
		List<ItemStack> outputStacks = new ArrayList<>();
		List<? extends IRecipe<?>> recipes = getRecipes();
		if (recipeIndex >= recipes.size())
			recipeIndex = 0;
		if (recipes.isEmpty()) {  // no recipe for the inserted item
			outputStacks.add(inventory.getStackInSlot(0).copy());
			outputStacks.get(0).setCount(recipeNumber);
			
		}else {
			IRecipe<?> recipe = recipes.get(recipeIndex);
			
			for (int roll = 0; roll < this.recipeNumber; roll++) {
				List<ItemStack> results = new LinkedList<ItemStack>();
				if (recipe instanceof RollingRecipe)
					results.add(recipe.getRecipeOutput().copy());
	
				for (int i = 0; i < results.size(); i++) {
					ItemStack stack = results.get(i);
					ItemHelper.addToList(stack, outputStacks);
				}
			}
		}
		
		inventory.getStackInSlot(0).setCount(inventory.getStackInSlot(0).getCount() - recipeNumber);
		if(inventory.getStackInSlot(0).getCount() == 0) {
			inventory.setStackInSlot(0, ItemStack.EMPTY);
			inventory.clear();
		}
		markDirty();
		return outputStacks;
		
	}
	
	protected List<? extends IRecipe<?>> getRecipes() {
		List<IRecipe<?>> recipeList = new ArrayList<IRecipe<?>>();
		for(IRecipe<?> recipe: CreateIntegration.getRecipes(CreateIntegration.ROLLING_RECIPE, world.getRecipeManager()).values()) {
			
			if(((RollingRecipe)recipe).isValid(inventory.getStackInSlot(0))) {
				recipeList.add(recipe);
			}
		}
		return recipeList;
	}
	
    @Override
	public float calculateStressApplied() {
		return Config.ROLLER_SU.get();
	}
}

