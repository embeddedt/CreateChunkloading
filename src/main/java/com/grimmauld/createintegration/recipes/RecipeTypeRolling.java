package com.grimmauld.createintegration.recipes;

import com.grimmauld.createintegration.CreateIntegration;

import net.minecraft.item.crafting.IRecipeType;

public class RecipeTypeRolling implements IRecipeType<RollingRecipe> {
    
    @Override
    public String toString () {
        return CreateIntegration.modid + ":rolling";
    }
}