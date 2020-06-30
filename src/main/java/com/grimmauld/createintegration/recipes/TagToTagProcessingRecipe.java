package com.grimmauld.createintegration.recipes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.grimmauld.createintegration.CreateIntegration;
import com.simibubi.create.content.contraptions.processing.ProcessingIngredient;
import com.simibubi.create.content.contraptions.processing.ProcessingOutput;
import com.simibubi.create.content.contraptions.processing.ProcessingRecipe;
import com.simibubi.create.content.contraptions.processing.ProcessingRecipeSerializer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;

public class TagToTagProcessingRecipe<T extends ProcessingRecipe<?>> extends ProcessingRecipeSerializer<T> {

    public TagToTagProcessingRecipe(IRecipeFactory<T> factory, String type) {
        super(factory);
        CreateIntegration.logger.debug(new ResourceLocation(CreateIntegration.modid, "tagtotag"));
        this.setRegistryName(new ResourceLocation(CreateIntegration.modid, type));
    }

    @Override
    public T read(ResourceLocation recipeId, JsonObject json) {
        String s = JSONUtils.getString(json, "group", "");

        List<ProcessingIngredient> ingredients = new ArrayList<>();
        for (JsonElement e : JSONUtils.getJsonArray(json, "ingredients")) {
            int count = 1;
            if (JSONUtils.hasField((JsonObject) e, "count")) {
                count = JSONUtils.getInt(e.getAsJsonObject().get("count"), "count");
            }
            for (int i = 0; i < count; i++) {
                ingredients.add(ProcessingIngredient.parse(e.getAsJsonObject()));
            }
        }

        List<ProcessingOutput> results = new ArrayList<>();
        for (JsonElement e : JSONUtils.getJsonArray(json, "results")) {
            int i = JSONUtils.getInt(e.getAsJsonObject().get("count"), "count");
            Item item = null;

            if (e.getAsJsonObject().has("item")) {
                String s1 = JSONUtils.getString(e.getAsJsonObject().get("item"), "item");
                item = Registry.ITEM.getOrDefault(new ResourceLocation(s1));
            } else if (e.getAsJsonObject().has("tag")) {
                ResourceLocation s1 = new ResourceLocation(JSONUtils.getString(e.getAsJsonObject().get("tag"), "tag"));
                for (Item _item : Registry.ITEM) {
                    if (_item.getTags().contains(s1)) {
                        item = _item;
                        break;
                    }
                }
            }
            if (item == null)
                return null;
            ItemStack itemstack = new ItemStack(item, i);
			float chance = 1;
            if (JSONUtils.hasField((JsonObject) e, "chance"))
                chance = JSONUtils.getFloat(e.getAsJsonObject().get("chance"), "chance");
            results.add(new ProcessingOutput(itemstack, chance));
        }

        int duration = -1;
        if (JSONUtils.hasField(json, "processingTime"))
            duration = JSONUtils.getInt(json, "processingTime");
        System.out.println(recipeId);
        return this.factory.create(recipeId, s, ingredients, results, duration);
    }
}
