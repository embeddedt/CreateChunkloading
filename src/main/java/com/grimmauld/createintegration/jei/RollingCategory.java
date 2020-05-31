package com.grimmauld.createintegration.jei;

import com.grimmauld.createintegration.CreateIntegration;
import com.grimmauld.createintegration.blocks.AnimatedRoller;
import com.grimmauld.createintegration.blocks.ModBlocks;
import com.grimmauld.createintegration.recipes.RollingRecipe;
import com.simibubi.create.ScreenResources;
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

import java.util.Arrays;
import java.util.List;

public class RollingCategory implements IRecipeCategory<RollingRecipe> {
    protected static final int inputSlot = 0;
    protected static final int outputSlot = 2;
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
        //new DoubleItemIcon(() -> AllItems.SAND_PAPER.asStack(), () -> ItemStack.EMPTY);
        // arrow = guiHelper.drawableBuilder(Constants.RECIPE_GUI_VANILLA, 82, 128, 24, 17)
        // 		.buildAnimated(200, IDrawableAnimated.StartDirection.LEFT, false);
    }


    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }


    @Override
    public String getTitle() {
        return localizedName;
    }

    // @Override
    public String getModName() {
        return "Create Integration";
    }

    @Override
    public ResourceLocation getUid() {
        return ID;
    }


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
    public void setRecipe(IRecipeLayout recipeLayout, RollingRecipe recipeWrapper, IIngredients ingredients) {

        IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();
        List<ItemStack> results = recipeWrapper.getRollableResults();

        itemStacks.init(0, true, 26, 8);
        itemStacks.set(0, Arrays.asList(recipeWrapper.getInput().getMatchingStacks()));
        itemStacks.init(1, false, 131, 8);
        itemStacks.set(1, results.get(0).getStack());


    }


    @Override
    public void draw(RollingRecipe recipe, double mouseX, double mouseY) {
        ScreenResources.JEI_SLOT.draw(26, 8);
        ScreenResources.JEI_SLOT.draw(131, 8);

        ScreenResources.JEI_SHADOW.draw(59, 45);
        ScreenResources.JEI_LONG_ARROW.draw(52, 12);

        roller.draw(72, 25);
    }
}
