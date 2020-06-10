package com.grimmauld.createintegration.blocks;

import com.grimmauld.createintegration.Config;
import com.grimmauld.createintegration.tools.CustomEnergyStorage;
import com.grimmauld.createintegration.tools.ModUtil;
import com.simibubi.create.content.contraptions.base.GeneratingKineticTileEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import static com.grimmauld.createintegration.blocks.ModBlocks.MOTOR_TILE;

public class MotorTile extends GeneratingKineticTileEntity {

    private static final DamageSource damageSourceMotor = new DamageSource("createintegration.motor").setDamageBypassesArmor();
    private final LazyOptional<IEnergyStorage> energy = LazyOptional.of(this::createEnergy);
    private boolean active;
    private byte damageCooldown;


    public MotorTile() {
        super(MOTOR_TILE);
        damageCooldown = 0;
    }

    @Override
    public void tick() {
        super.tick();
        boolean activeBefore = active;
        active = false;
        energy.ifPresent(energy -> {
                    AtomicInteger capacity = new AtomicInteger(energy.getEnergyStored());
                    if (capacity.get() > (activeBefore ? Config.MOTOR_FE.get() : 0.5 * Config.MOTOR_CAPACITY.get())) {
                        active = true;
                        ((CustomEnergyStorage) energy).consumeEnergy(Config.MOTOR_FE.get());
                        markDirty();
                    } else {
                        active = false;
                    }
                }
        );
        if (active != activeBefore) {
            updateGeneratedRotation();
            markDirty();
        }

        damageCooldown++;

        if (damageCooldown % 20 == 0) {
            boolean attacked = false;
            assert world != null;
            for (Entity entityIn : world.getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB(pos.getX() - 0.3, pos.getY() - 1, pos.getZ() - 0.3, pos.getX() + 1.3, pos.getY() + 2, pos.getZ() + 1.3), (Predicate<Entity>) testEntity -> true)) {
                entityIn.attackEntityFrom(damageSourceMotor, MathHelper.clamp(Math.abs(10 * getEnergy() / Config.MOTOR_CAPACITY.get()), 0, 20));
                attacked = true;
            }
            if (attacked) {
                setEnergy(0);
                markDirty();
            }
        }

    }

    @Override
    public void read(CompoundNBT tag) {
        CompoundNBT energyTag = tag.getCompound("energy");
        energy.ifPresent(h -> ModUtil.safeNBTCast(h).deserializeNBT(energyTag));

        super.read(tag);
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        energy.ifPresent(h -> {
            CompoundNBT compound = ModUtil.safeNBTCast(h).serializeNBT();
            tag.put("energy", compound);
        });
        return super.write(tag);
    }

    private IEnergyStorage createEnergy() {
        return new CustomEnergyStorage(Config.MOTOR_CAPACITY.get(), Config.MOTOR_MAXINPUT.get(), 0);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityEnergy.ENERGY) {
            return energy.cast();
        }
        return super.getCapability(cap, side);

    }

    @Override
    public float getGeneratedSpeed() {
        return Config.MOTOR_SPEED.get() * (active ? 1 : 0);
    }

    @Override
    public float calculateAddedStressCapacity() {
        return Config.MOTOR_SU.get() * (active ? 1 : 0);
    }

    @Override
    public float calculateStressApplied() {
        return 0;
    }

    private int getEnergy() {
        AtomicInteger energyStored = new AtomicInteger(0);
        energy.ifPresent(energy -> energyStored.set(energy.getEnergyStored()));
        return energyStored.get();
    }

    private void setEnergy(int value) {
        energy.ifPresent(energy -> ((CustomEnergyStorage) energy).setEnergy(value));
    }
}
