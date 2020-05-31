package com.grimmauld.createintegration.misc;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import java.util.Hashtable;
import java.util.Objects;
import java.util.Set;

public class EnderList implements IEnderList {
    Hashtable<Integer, LazyOptional<IItemHandler>> ender_ids = new Hashtable<>();

    public LazyOptional<IItemHandler> getOrCreate(int id) {
        if (!ender_ids.containsKey(id)) {
            ender_ids.put(id, LazyOptional.of(this::createHandler));
        }
        return ender_ids.get(id);
    }

    public Set<Integer> getIDs() {
        return ender_ids.keySet();
    }

    private IItemHandler createHandler() {
        return new ItemStackHandler(9);
    }

    public static class Storage implements Capability.IStorage<EnderList> {

        @Override
        public INBT writeNBT(Capability<EnderList> capability, EnderList instance, Direction side) {
            CompoundNBT tag = new CompoundNBT();
            if (instance == null) return null;
            for (int id : instance.getIDs()) {
                instance.ender_ids.get(id).ifPresent(h -> {
                    CompoundNBT compound = ((INBTSerializable<CompoundNBT>) h).serializeNBT();
                    if (compound.contains("Items") && compound.get("Items") instanceof ListNBT && !((ListNBT) Objects.requireNonNull(compound.get("Items"))).isEmpty()) {
                        tag.put(String.valueOf(id), compound);
                    }
                });
            }
            return tag;
        }

        @Override
        public void readNBT(Capability<EnderList> capability, EnderList instance, Direction side, INBT nbt) {
            if (!(nbt instanceof CompoundNBT)) return;
            CompoundNBT tag = (CompoundNBT) nbt;
            for (String id : tag.keySet()) {
                try {
                    instance.getOrCreate(Integer.parseInt(id)).ifPresent(h -> ((INBTSerializable<CompoundNBT>) h).deserializeNBT(tag.getCompound(id)));
                } catch (NumberFormatException e) {
                    // fixme: Add invalid NBT handling here!
                }
            }
            System.out.println(instance.ender_ids);
        }
    }
}
