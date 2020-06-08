package com.grimmauld.createintegration.jei;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;

import com.grimmauld.createintegration.CreateIntegration;
import com.grimmauld.createintegration.blocks.AnimatedRoller;
import com.grimmauld.createintegration.blocks.ModBlocks;
import com.grimmauld.createintegration.recipes.RollingRecipe;
import com.simibubi.create.foundation.gui.AllGuiTextures;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.util.Translator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class RollingCategory implements IRecipeCategory<RollingRecipe> {
    public static ResourceLocation ID = new ResourceLocation(CreateIntegration.modid, "rolling");
    private final IDrawable background;
    private final IDrawable icon;
    private final String localizedName;
    private final AnimatedRoller roller;


    public RollingCategory(IGuiHelper guiHelper) {
        localizedName = Translator.translateToLocal("createintegration.category.rolling");
        background = guiHelper.createBlankDrawable(177, 55);
        icon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.ROLLING_MACHINE));
        roller = new AnimatedRoller();
    }


    @Nonnull
    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Nonnull
    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Nonnull
    @Override
    public String getTitle() {
        return localizedName;
    }

    @Nonnull
    @Override
    public ResourceLocation getUid() {
        return ID;
    }

    @Nonnull
    @Override
    public Class<? extends RollingRecipe> getRecipeClass() {
        return RollingRecipe.class;
    }


    @Override
    public void setIngredients(RollingRecipe recipe, IIngredients ingredients) {
        ingredients.setInputIngredients(recipe.getIngredients());
        ingredients.setOutputs(VanillaTypes.ITEM, recipe.getPossibleOutputs());
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, RollingRecipe recipeWrapper, @Nonnull IIngredients ingredients) {
        IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();
        List<ItemStack> results = recipeWrapper.getRollableResults();

        itemStacks.init(0, true, 26, 8);
        itemStacks.set(0, Arrays.asList(recipeWrapper.getInput().getMatchingStacks()));
        itemStacks.init(1, false, 131, 8);
        itemStacks.set(1, results.get(0).getStack());
    }


    @Override
    public void draw(@Nonnull RollingRecipe recipe, double mouseX, double mouseY) {
    	AllGuiTextures.JEI_SLOT.draw(26, 8);
    	AllGuiTextures.JEI_SLOT.draw(131, 8);

    	AllGuiTextures.JEI_SHADOW.draw(59, 45);
    	AllGuiTextures.JEI_LONG_ARROW.draw(52, 12);

        roller.draw(72, 25);
    }
}
