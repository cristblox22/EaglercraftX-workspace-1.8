package net.minecraft.entity.passive;

import com.google.common.base.Predicate;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityCrocodile extends EntityAnimal {

    public int deathRollTicks;
    public int grabCooldown;
    public int timeUntilNextScute;

    public EntityCrocodile(World worldIn) {
        super(worldIn);
        this.setSize(2.0F, 0.8F);
        this.timeUntilNextScute = this.rand.nextInt(1201) + 3600;
        
        this.tasks.addTask(1, new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.2D, true));
        this.tasks.addTask(2, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(4, new EntityAILookIdle(this));
        
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
        
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityAnimal.class, 10, true, false, new Predicate<EntityAnimal>() {
            public boolean apply(EntityAnimal entity) {
                return entity != null && entity.isInWater() && !(entity instanceof EntityCrocodile);
            }
        }));
        
        this.targetTasks.addTask(4, new EntityAINearestAttackableTarget(this, EntityMob.class, 10, true, false, new Predicate<EntityMob>() {
            public boolean apply(EntityMob entity) {
                return entity != null && entity.isInWater();
            }
        }));
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.attackDamage);
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(30.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.25D);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(10.0D);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        
        if (!this.worldObj.isRemote && !this.isChild() && --this.timeUntilNextScute <= 0) {
            this.playSound("mob.chicken.plop", 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
            Item scute = Item.getByNameOrId("crocodile_scute");
            if (scute != null) {
                this.dropItem(scute, 1);
            }
            this.timeUntilNextScute = this.rand.nextInt(1201) + 3600;
        }

        if (this.isInWater()) {
            if (this.getAttackTarget() != null) {
                double targetY = this.getAttackTarget().posY;
                if (this.posY < targetY) {
                    this.motionY += 0.02D;
                } else if (this.posY > targetY) {
                    this.motionY -= 0.02D;
                }
            } else {
                BlockPos posUp = new BlockPos(this.posX, this.posY + 1.0D, this.posZ);
                if (this.worldObj.getBlockState(posUp).getBlock().getMaterial() == Material.water) {
                    this.motionY += 0.02D;
                } else {
                    this.motionY -= 0.01D;
                }
            }
            this.motionY = Math.max(-0.1D, Math.min(this.motionY, 0.1D));
        }
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        
        if (!this.worldObj.isRemote) {
            if (this.getAttackTarget() != null && this.riddenByEntity == null && this.grabCooldown == 0) {
                if (this.getDistanceToEntity(this.getAttackTarget()) < 3.5F) {
                    this.getAttackTarget().mountEntity(this);
                }
            }
            
            if (this.riddenByEntity instanceof EntityLivingBase) {
                this.grabCooldown = 40; 
                this.deathRollTicks++;
                if (this.deathRollTicks % 20 == 0) {
                    this.riddenByEntity.attackEntityFrom(DamageSource.causeMobDamage(this), 2.0F);
                }
            } else {
                this.deathRollTicks = 0;
                if (this.grabCooldown > 0) {
                    this.grabCooldown--;
                }
            }
        }
        
        if (this.riddenByEntity != null && this.isInWater()) {
            this.renderYawOffset += 30.0F; 
            this.rotationYaw = this.renderYawOffset;
            this.rotationPitch = 0.0F;
        }
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
        int count = this.rand.nextInt(2) + this.rand.nextInt(1 + lootingModifier);
        for (int i = 0; i < count; ++i) {
            Item scute = Item.getByNameOrId("crocodile_scute");
            if (scute != null) {
                this.dropItem(scute, 1);
            }
        }
    }

    @Override
    public void updateRiderPosition() {
        if (this.riddenByEntity != null) {
            float radius = 2.0F;
            double offsetX = -Math.sin(this.renderYawOffset * Math.PI / 180.0D) * radius;
            double offsetZ = Math.cos(this.renderYawOffset * Math.PI / 180.0D) * radius;
            
            this.riddenByEntity.setPosition(this.posX + offsetX, this.posY + this.getMountedYOffset() + this.riddenByEntity.getYOffset(), this.posZ + offsetZ);
        }
    }

    @Override
    public double getMountedYOffset() {
        return (double) this.height * 0.15D;
    }

    @Override
    public boolean attackEntityAsMob(Entity entityIn) {
        if (this.riddenByEntity == entityIn) {
            return false;
        }
        float f = (float) this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
        return entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), f);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.riddenByEntity != null && source.getEntity() == this.riddenByEntity) {
            return false;
        }
        return super.attackEntityFrom(source, amount);
    }

    @Override
    public EntityAgeable createChild(EntityAgeable ageable) {
        return new EntityCrocodile(this.worldObj);
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setInteger("ScuteDropTime", this.timeUntilNextScute);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompund) {
        super.readEntityFromNBT(tagCompund);
        if (tagCompund.hasKey("ScuteDropTime")) {
            this.timeUntilNextScute = tagCompund.getInteger("ScuteDropTime");
        }
    }
}