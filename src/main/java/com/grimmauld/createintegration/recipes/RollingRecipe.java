package com.grimmauld.createintegration.recipes;


import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.grimmauld.createintegration.CreateIntegration;
import com.grimmauld.createintegration.blocks.ModBlocks;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class RollingRecipe implements IRecipe<IInventory> {
	public static final Serializer SERIALIZER = new Serializer();

	private final Ingredient input;
	private final ItemStack output;
	private final int processingDuration;
	private final ResourceLocation id;
	
//	public static List<RollingRecipe> allRollingRecipes = new ArrayList<RollingRecipe>();

	public RollingRecipe(ResourceLocation id, Ingredient input, ItemStack output, int processingTime) {

		this.id = id;
		this.input = input;
		this.output = output;
		this.processingDuration = processingTime;
		
//		allRollingRecipes.add(this);
		

		// This output is not required, but it can be used to detect when a recipe has
		// been
		// loaded into the game.
		System.out.println("Loaded " + this.toString());
	}

	@Override
	public String toString() {

		// Overriding toString is not required, it's just useful for debugging.
		return "RollingRecipe [input=" + this.input + ", output=" + this.output + ", id=" + this.id + "]";
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
	public IRecipeSerializer<?> getSerializer() {

		return SERIALIZER;
	}

	@Override
	public IRecipeType<?> getType() {

		return CreateIntegration.ROLLING_RECIPE;
	}

	@Override
	public ItemStack getIcon() {
		return new ItemStack(new BlockItem(ModBlocks.ROLLING_MACHINE, new Item.Properties()));
	}

	public boolean isValid(ItemStack input) {

		return this.input.test(input);
	}

	private static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>>
			implements IRecipeSerializer<RollingRecipe> {

		Serializer() {

			// This registry name is what people will specify in their json files.
			this.setRegistryName(new ResourceLocation(CreateIntegration.modid, "rolling"));
		}

		@Override
		public RollingRecipe read(ResourceLocation recipeId, JsonObject json) {

			// Reads a recipe from json.

			// Reads the input. Accepts items, tags, and anything else that
			// Ingredient.deserialize can understand.
			final JsonElement inputElement = JSONUtils.isJsonArray(json, "input")
					? JSONUtils.getJsonArray(json, "input")
					: JSONUtils.getJsonObject(json, "input");
			final Ingredient input = Ingredient.deserialize(inputElement);
			
			final int processingDuration = JSONUtils.hasField(json, "duration") ? JSONUtils.getInt(json, "duration"): 100;

			// Reads the output. The common utility method in ShapedRecipe is what all
			// vanilla
			// recipe classes use for this.
			final ItemStack output = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, "output"));

			return new RollingRecipe(recipeId, input, output, processingDuration);
		}

		@Override
		public RollingRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {

			// Reads a recipe from a packet buffer. This code is called on the client.
			final Ingredient input = Ingredient.read(buffer);
			final ItemStack output = buffer.readItemStack();
			final int processingDuration = buffer.readInt();

			return new RollingRecipe(recipeId, input, output, processingDuration);
		}

		@Override
		public void write(PacketBuffer buffer, RollingRecipe recipe) {

			// Writes the recipe to a packet buffer. This is called on the server when a
			// player
			// connects or when /reload is used.
			recipe.input.write(buffer);
			buffer.writeItemStack(recipe.output);
			buffer.writeInt(recipe.getProcessingDuration());
		}
	}

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
