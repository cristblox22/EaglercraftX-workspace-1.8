package net.minecraft.entity.passive;

import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityGrizzlyBear extends EntityAnimal {

	private static final int AGGRO_TICKS = 600;
	private static final double PROXIMITY_RANGE = 5.0D;
	private static final double SHED_CHANCE = 0.0015D;
	private static final Item BEAR_FUR = Item.getItemById(471);
	private static final EaglercraftUUID AGGRO_SPEED_UUID = EaglercraftUUID
			.fromString("8e28e1c0-1f2e-4b3a-9d6a-9b6f2c9f5a11");
	private static final AttributeModifier AGGRO_SPEED_MODIFIER = new AttributeModifier(AGGRO_SPEED_UUID,
			"Aggro speed boost", 0.15D, 2);

	private int angerCooldown;

	public EntityGrizzlyBear(World worldIn) {
		super(worldIn);
		this.setSize(1.4F, 1.6F);

		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(1, new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.2D, true));
		this.tasks.addTask(2, new EntityAIWander(this, 1.0D));
		this.tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.tasks.addTask(4, new EntityAILookIdle(this));

		this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
	}

	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getAttributeMap().registerAttribute(SharedMonsterAttributes.attackDamage);
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(30.0D);
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.25D);
		this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(6.0D);
	}

	protected void entityInit() {
		super.entityInit();
		this.dataWatcher.addObject(18, Byte.valueOf((byte) 0));
	}

	public boolean isAggressive() {
		return (this.dataWatcher.getWatchableObjectByte(18) & 1) != 0;
	}

	public void setAggressive(boolean aggressive) {
		byte flags = this.dataWatcher.getWatchableObjectByte(18);
		if (aggressive) {
			this.dataWatcher.updateObject(18, Byte.valueOf((byte) (flags | 1)));
		} else {
			this.dataWatcher.updateObject(18, Byte.valueOf((byte) (flags & ~1)));
		}

		IAttributeInstance speed = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
		speed.removeModifier(AGGRO_SPEED_MODIFIER);
		if (aggressive) {
			speed.applyModifier(AGGRO_SPEED_MODIFIER);
		}
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();

		if (!this.worldObj.isRemote) {
			if (this.getAttackTarget() == null) {
				EntityPlayer nearest = this.worldObj.getClosestPlayerToEntity(this, PROXIMITY_RANGE);
				if (nearest != null && !nearest.capabilities.isCreativeMode && !nearest.isSpectator()) {
					this.setAttackTarget(nearest);
				}
			}

			if (this.getAttackTarget() != null) {
				this.angerCooldown = AGGRO_TICKS;
			} else if (this.isAggressive()) {
				if (this.angerCooldown > 0) {
					this.angerCooldown--;
				} else {
					this.setAggressive(false);
				}
			}

			if (this.rand.nextDouble() < SHED_CHANCE) {
				this.dropFur();
			}
		}
	}

	private void dropFur() {
		ItemStack stack = new ItemStack(BEAR_FUR);
		EntityItem entityitem = new EntityItem(this.worldObj, this.posX, this.posY + 0.2D, this.posZ, stack);
		this.worldObj.spawnEntityInWorld(entityitem);
	}

	@Override
	public boolean attackEntityAsMob(Entity entityIn) {
		// Calculate the damage from the attribute you registered
		float damage = (float) this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
		
		// Apply the damage to the target entity
		boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), damage);
		
		if (flag) {
			this.setAggressive(true);
			this.angerCooldown = AGGRO_TICKS;
		}
		return flag;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		boolean flag = super.attackEntityFrom(source, amount);
		if (flag && !this.worldObj.isRemote) {
			this.setAggressive(true);
			this.angerCooldown = AGGRO_TICKS;

			Entity attacker = source.getEntity();
			if (attacker instanceof EntityLivingBase) {
				this.setAttackTarget((EntityLivingBase) attacker);
			}
		}
		return flag;
	}

	@Override
	protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
		int count = 1 + this.rand.nextInt(2 + lootingModifier);
		for (int i = 0; i < count; i++) {
			this.dropItem(BEAR_FUR, 1);
		}
	}

	@Override
	public EntityAgeable createChild(EntityAgeable ageable) {
		return new EntityGrizzlyBear(this.worldObj);
	}
}