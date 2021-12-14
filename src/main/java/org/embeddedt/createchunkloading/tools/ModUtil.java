package org.embeddedt.createchunkloading.tools;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

public class ModUtil {
    public static INBTSerializable<CompoundTag> safeNBTCast(IItemHandler h) {
        return (INBTSerializable<CompoundTag>) h;
    }

    public static INBTSerializable<CompoundTag> safeNBTCast(IEnergyStorage h) {
        return (INBTSerializable<CompoundTag>) h;
    }

    public static Direction getFacingFromEntity(BlockPos clickedBlock, @Nullable LivingEntity entity) {
        if (entity == null) return Direction.NORTH;
        Vec3 vec = entity.position();
        return Direction.getNearest((float) (entity.isShiftKeyDown() ? -1 : 1) * (vec.x - clickedBlock.getX()), (float) (entity.isShiftKeyDown() ? -1 : 1) * (vec.y - clickedBlock.getY()), (float) (entity.isShiftKeyDown() ? -1 : 1) * (vec.z - clickedBlock.getZ()));
    }

    public static Direction getFacingFromEntity(BlockPos clickedBlock, @Nullable LivingEntity entity, boolean noUpDown) {
        if (entity == null) return Direction.NORTH;
        Vec3 vec = entity.position();
        return Direction.getNearest((float) (entity.isShiftKeyDown() ? -1 : 1) * (vec.x - clickedBlock.getX()), (float) (noUpDown ? 0 : 1) * (entity.isShiftKeyDown() ? -1 : 1) * (vec.y - clickedBlock.getY()), (float) (entity.isShiftKeyDown() ? -1 : 1) * (vec.z - clickedBlock.getZ()));
    }
}
