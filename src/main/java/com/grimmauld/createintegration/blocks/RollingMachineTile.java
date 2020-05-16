package com.grimmauld.createintegration.blocks;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.grimmauld.createintegration.Config;
import com.grimmauld.createintegration.CreateIntegration;
import com.grimmauld.createintegration.recipes.RollingRecipe;

import com.simibubi.create.foundation.item.ItemHelper;


import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

public class RollingMachineTile extends BeltMachineTile {
	
	public RollingMachineTile() {
		super(ModBlocks.ROLLING_MACHINE_TILE);
	}
	
	protected void applyRecipe() {
		List<? extends IRecipe<?>> recipes = getRecipes();
		if (recipes.isEmpty())
			return;
		if (recipeIndex >= recipes.size())
			recipeIndex = 0;

		IRecipe<?> recipe = recipes.get(recipeIndex);

		int rolls = inventory.getStackInSlot(0).getCount();
		inventory.clear();

		List<ItemStack> list = new ArrayList<>();
		for (int roll = 0; roll < rolls; roll++) {
			List<ItemStack> results = new LinkedList<ItemStack>();
			if (recipe instanceof RollingRecipe)
				results.add(recipe.getRecipeOutput().copy());

			for (int i = 0; i < results.size(); i++) {
				ItemStack stack = results.get(i);
				ItemHelper.addToList(stack, list);
			}
		}
		for (int slot = 0; slot < list.size() && slot + 1 < inventory.getSlots(); slot++)
			inventory.setStackInSlot(slot + 1, list.get(slot));
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

