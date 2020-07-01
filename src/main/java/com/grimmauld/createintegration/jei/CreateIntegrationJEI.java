package com.grimmauld.createintegration.jei;

import com.grimmauld.createintegration.CreateIntegration;
import com.grimmauld.createintegration.blocks.ModBlocks;
import mezz.jei.Internal;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.runtime.JeiHelpers;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

@JeiPlugin
@SuppressWarnings("unused")
public class CreateIntegrationJEI implements IModPlugin {

    private static final ResourceLocation ID = new ResourceLocation(CreateIntegration.modid, "jei_plugin");

    @SuppressWarnings("unused")
    public CreateIntegrationJEI() {
    }

    @Nonnull
    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        JeiHelpers jeiHelpers = Internal.getHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

        registry.addRecipeCategories(new RollingCategory(guiHelper));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.ROLLING_MACHINE), RollingCategory.ID);
    }


    @Override
    public void registerRecipes(@Nonnull IRecipeRegistration registry) {
        if (Minecraft.getInstance().world == null) {
            CreateIntegration.logger.warn("Can not register JEI recipes: world is null");
            return;
        }
        registry.addRecipes(CreateIntegration.getRecipes(CreateIntegration.ROLLING_RECIPE, Minecraft.getInstance().world.getRecipeManager()).values(), RollingCategory.ID);
    }
}
