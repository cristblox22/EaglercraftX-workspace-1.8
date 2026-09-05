package net.minecraft.entity.passive;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityTasmanianDevil extends EntityAnimal {

	// Animation IDs -> replaces Citadel's IAnimatedEntity system.
	// 0 = none, 1 = attack (bite), 2 = howl/screech
	public static final int ANIMATION_ATTACK = 1;
	public static final int ANIMATION_HOWL = 2;

	private static final int HOWL_DURATION = 40;
	private static final Item DEVIL_MEAT = Item.getItemById(423); // rotten flesh id, swap for a custom item if you registered one

	private int animation;
	private int animationTick;
	private int howlCooldown;

	public EntityTasmanianDevil(World worldIn) {
		super(worldIn);
		this.setSize(0.6F, 0.5F);

		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(1, new EntityAIAttackOnCollide(this, EntityLivingBase.class, 1.0D, true));
		this.tasks.addTask(2, new EntityAIWander(this, 1.0D));
		this.tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
		this.tasks.addTask(4, new EntityAILookIdle(this));

		this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
		this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<EntityChicken>(this, EntityChicken.class, 10, true, false, null));
		this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<EntityPig>(this, EntityPig.class, 10, true, false, null));
		this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<EntitySheep>(this, EntitySheep.class, 10, true, false, null));
		this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<EntityCow>(this, EntityCow.class, 10, true, false, null));
	}

	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getAttributeMap().registerAttribute(SharedMonsterAttributes.attackDamage);
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(10.0D);
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.3D);
		this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(3.0D);
	}

	protected void entityInit() {
		super.entityInit();
		// byte 18: bit 0 = angry/howling flag, synced to client for the texture swap
		this.dataWatcher.addObject(18, Byte.valueOf((byte) 0));
	}

	// --- animation state, synced replacement for Citadel's ModelAnimator ---

	public int getAnimation() {
		return this.animation;
	}

	public int getAnimationTick() {
		return this.animationTick;
	}

	public void setAnimation(int animation) {
		this.animation = animation;
		this.animationTick = 0;
	}

	public boolean isHowling() {
		return (this.dataWatcher.getWatchableObjectByte(18) & 1) != 0;
	}

	private void setHowling(boolean howling) {
		byte flags = this.dataWatcher.getWatchableObjectByte(18);
		if (howling) {
			this.dataWatcher.updateObject(18, Byte.valueOf((byte) (flags | 1)));
		} else {
			this.dataWatcher.updateObject(18, Byte.valueOf((byte) (flags & ~1)));
		}
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();

		if (this.animation != 0) {
			this.animationTick++;
			// attack animation is short and clears itself; howl is driven by howlCooldown below
			if (this.animation == ANIMATION_ATTACK && this.animationTick > 5) {
				this.setAnimation(0);
			}
		}

		if (!this.worldObj.isRemote) {
			if (this.isHowling()) {
				if (this.animationTick >= HOWL_DURATION) {
					this.setHowling(false);
					this.setAnimation(0);
				}
			} else if (this.howlCooldown > 0) {
				this.howlCooldown--;
			} else if (this.getAttackTarget() != null && this.rand.nextInt(100) == 0) {
				this.startHowl();
			}
		}
	}

	private void startHowl() {
		this.setHowling(true);
		this.setAnimation(ANIMATION_HOWL);
		this.howlCooldown = 200 + this.rand.nextInt(200);
		this.worldObj.playSoundAtEntity(this, "alexmobs:tasmanian_devil.roar", 1.0F, 0.9F + this.rand.nextFloat() * 0.2F);
	}

	@Override
	public boolean attackEntityAsMob(Entity entityIn) {
		float damage = (float) this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
		boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), damage);

		if (flag) {
			this.setAnimation(ANIMATION_ATTACK);
		}
		return flag;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		boolean flag = super.attackEntityFrom(source, amount);
		if (flag && !this.worldObj.isRemote) {
			Entity attacker = source.getEntity();
			if (attacker instanceof EntityLivingBase) {
				this.setAttackTarget((EntityLivingBase) attacker);
			}
			if (this.howlCooldown <= 0 && !this.isHowling()) {
				this.startHowl();
			}
		}
		return flag;
	}

	@Override
	public boolean interact(EntityPlayer player) {
		ItemStack stack = player.inventory.getCurrentItem();

		if (stack != null && stack.getItem() == DEVIL_MEAT) {
			if (this.getHealth() < this.getMaxHealth()) {
				if (!player.capabilities.isCreativeMode) {
					--stack.stackSize;
					if (stack.stackSize <= 0) {
						player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
					}
				}
				this.heal(4.0F);
				return true;
			}
		}

		return super.interact(player);
	}

	@Override
	protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
		int count = this.rand.nextInt(2 + lootingModifier);
		for (int i = 0; i < count; i++) {
			this.dropItem(DEVIL_MEAT, 1);
		}
	}

	@Override
	public EntityAgeable createChild(EntityAgeable ageable) {
		return new EntityTasmanianDevil(this.worldObj);
	}
}