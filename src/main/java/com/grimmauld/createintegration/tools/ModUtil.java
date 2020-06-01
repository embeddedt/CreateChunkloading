package com.grimmauld.createintegration.tools;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;

public class ModUtil {
    public static INBTSerializable<CompoundNBT> safeNBTCast(IItemHandler h) {
        return (INBTSerializable<CompoundNBT>) h;
    }

    public static INBTSerializable<CompoundNBT> safeNBTCast(IEnergyStorage h) {
        return (INBTSerializable<CompoundNBT>) h;
    }

    public static Direction getFacingFromEntity(BlockPos clickedBlock, LivingEntity entity) {
        Vec3d vec = entity.getPositionVec();
        return Direction.getFacingFromVector((float) (entity.isSneaking() ? -1 : 1) * (vec.x - clickedBlock.getX()), (float) (entity.isSneaking() ? -1 : 1) * (vec.y - clickedBlock.getY()), (float) (entity.isSneaking() ? -1 : 1) * (vec.z - clickedBlock.getZ()));
    }

    public static Direction getFacingFromEntity(BlockPos clickedBlock, LivingEntity entity, boolean noUpDown) {
        Vec3d vec = entity.getPositionVec();
        return Direction.getFacingFromVector((float) (entity.isSneaking() ? -1 : 1) * (vec.x - clickedBlock.getX()), (float) (noUpDown ? 0 : 1) * (entity.isSneaking() ? -1 : 1) * (vec.y - clickedBlock.getY()), (float) (entity.isSneaking() ? -1 : 1) * (vec.z - clickedBlock.getZ()));
    }
}
