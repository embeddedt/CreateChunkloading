package com.grimmauld.createintegration.blocks;

import com.grimmauld.createintegration.Config;
import com.grimmauld.createintegration.tools.CustomEnergyStorage;
import com.grimmauld.createintegration.tools.ModUtil;
import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
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

import static com.grimmauld.createintegration.blocks.ModBlocks.DYNAMO_TILE;


public class DynamoTile extends KineticTileEntity implements ITickableTileEntity {

    private static final DamageSource damageSourceDynamo = new DamageSource("createintegration.dynamo").setDamageBypassesArmor();
    private final LazyOptional<IEnergyStorage> energy = LazyOptional.of(this::createEnergy);
    private byte damageCooldown;

    public DynamoTile() {
        super(DYNAMO_TILE);
        damageCooldown = 0;
        updateSpeed = true;
    }


    @Override
    public void tick() {
        super.tick();
        energy.ifPresent(e -> ((CustomEnergyStorage) e).addEnergy((int) Math.abs(Config.DYNAMO_GENERATE_MULTIPLIER.get() * getSpeed())));
        markDirty();
        sendOutPower();
        damageCooldown++;
        if (damageCooldown % 20 == 0) {
            boolean attacked = false;
            assert world != null;
            for (Entity entityIn : world.getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB(pos.getX() - 0.3, pos.getY() - 1, pos.getZ() - 0.3, pos.getX() + 1.3, pos.getY() + 2, pos.getZ() + 1.3), (Predicate<Entity>) testEntity -> true)) {
                entityIn.attackEntityFrom(damageSourceDynamo, MathHelper.clamp(Math.abs(10 * getEnergy() / Config.DYNAMO_MAXPOWER.get()), 0, 20));
                attacked = true;
            }
            if (attacked) {
                setEnergy(0);
                markDirty();
            }
        }
    }


    private void sendOutPower() {
        energy.ifPresent(energy -> {
            if (energy.getEnergyStored() > 0 && getBlockState().has(BlockStateProperties.FACING)) {
                Direction direction = getBlockState().get(BlockStateProperties.FACING).getOpposite();
                assert world != null;
                TileEntity te = world.getTileEntity(pos.offset(direction));
                if (te != null) {
                    te.getCapability(CapabilityEnergy.ENERGY, direction.getOpposite()).ifPresent(handler -> {
                                if (handler.canReceive())
                                    ((CustomEnergyStorage) energy).consumeEnergy(handler.receiveEnergy(Math.min(energy.getEnergyStored(), Config.DYNAMO_SEND.get()), false));
                            }
                    );
                }
            }
        });
        markDirty();
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
        return new CustomEnergyStorage(Config.DYNAMO_MAXPOWER.get(), 0, Config.DYNAMO_SEND.get());
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
    public float calculateStressApplied() {
        return Config.DYNAMO_SU.get();
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
