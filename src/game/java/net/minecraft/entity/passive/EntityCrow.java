package net.minecraft.entity.passive;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import java.util.List;

public class EntityCrow extends EntityTameable {

	// Index 18 - if this collides with a flag EntityTameable already uses on your fork, bump it
	// (check EntityTameable.entityInit() for whatever IDs it reserves).
	private static final int SCARED_WATCHER_ID = 18;

	// Bat-style hover target - this is what makes it actually fly instead of ground-pathing.
	private BlockPos flightTarget;
	private Entity scaryThing;

	public EntityCrow(World worldIn) {
		super(worldIn);
		this.setSize(0.5F, 0.7F);

		this.tasks.addTask(0, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
		this.tasks.addTask(1, new EntityAILookIdle(this));
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataWatcher.addObject(SCARED_WATCHER_ID, Byte.valueOf((byte) 0));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(8.0D);
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.3D);
	}

	public boolean isScared() {
		return this.dataWatcher.getWatchableObjectByte(SCARED_WATCHER_ID) != 0;
	}

	private void setScared(boolean scared) {
		if (scared != this.isScared()) {
			this.dataWatcher.updateObject(SCARED_WATCHER_ID, Byte.valueOf((byte) (scared ? 1 : 0)));
		}
	}

	// --- Sounds ---

	protected float getSoundVolume() {
		return 0.3F;
	}

	protected String getLivingSound() {
		return this.isScared() ? "custom.croak" : (this.rand.nextInt(4) == 0 ? "custom.croak" : null);
	}

	protected String getHurtSound() {
		return "custom.croak";
	}

	protected String getDeathSound() {
		return "custom.croak";
	}

	// --- Flight ---
	// No PathNavigateFlying/EntityFlyHelper in 1.8.8, so this drives motion directly every tick,
	// exactly the way EntityBat does it: a hover target is re-picked periodically and motion is
	// lerped toward it hard enough that vanilla gravity never actually wins.

	@Override
	protected void updateAITasks() {
		super.updateAITasks();

		List<EntityLivingBase> nearby = this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class,
				this.getEntityBoundingBox().expand(6.0D, 6.0D, 6.0D));
		this.scaryThing = null;
		for (EntityLivingBase e : nearby) {
			if (e == this) continue;
			if (this.isTamed() && e == this.getOwner()) continue;
			this.scaryThing = e;
			break;
		}

		if (this.scaryThing != null) {
			this.setScared(true);

			double dx = this.posX - this.scaryThing.posX;
			double dz = this.posZ - this.scaryThing.posZ;
			double dist = Math.sqrt(dx * dx + dz * dz);
			if (dist < 0.5D) {
				dx = this.rand.nextDouble() - 0.5D;
				dz = this.rand.nextDouble() - 0.5D;
				dist = 1.0D;
			}

			this.motionX += (Math.signum(dx / dist) * 0.9D - this.motionX) * 0.2D;
			this.motionY += (0.6D - this.motionY) * 0.2D;
			this.motionZ += (Math.signum(dz / dist) * 0.9D - this.motionZ) * 0.2D;
			this.moveForward = 1.0F;
		} else {
			this.setScared(false);

			BlockPos anchor = this.isTamed() && this.getOwner() != null
					? new BlockPos(this.getOwner())
					: new BlockPos(this.posX, this.posY, this.posZ);

			if (this.flightTarget != null
					&& (this.flightTarget.distanceSq((int) this.posX, (int) this.posY, (int) this.posZ) < 4.0D
							|| this.rand.nextInt(30) == 0)) {
				this.flightTarget = null;
			}

			if (this.flightTarget == null) {
				this.flightTarget = anchor.add(this.rand.nextInt(11) - 5, this.rand.nextInt(6) - 2,
						this.rand.nextInt(11) - 5);
			}

			double d0 = (double) this.flightTarget.getX() + 0.5D - this.posX;
			double d1 = (double) this.flightTarget.getY() + 0.5D - this.posY;
			double d2 = (double) this.flightTarget.getZ() + 0.5D - this.posZ;
			double dist = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
			if (dist < 0.0001D) dist = 1.0D;

			this.motionX += (d0 / dist * 0.4D - this.motionX) * 0.1D;
			this.motionY += (d1 / dist * 0.4D - this.motionY) * 0.1D;
			this.motionZ += (d2 / dist * 0.4D - this.motionZ) * 0.1D;
			this.moveForward = 0.5F;
		}

		float f = (float) (MathHelper.func_181159_b(this.motionZ, this.motionX) * 180.0D / 3.1415927410125732D) - 90.0F;
		float f1 = MathHelper.wrapAngleTo180_float(f - this.rotationYaw);
		this.rotationYaw += f1;
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		// Cancel vanilla gravity accumulation each tick - the lerps above already give us controlled flight.
		this.motionY *= 0.6D;
	}

	@Override
	public boolean canBePushed() {
		return false;
	}

	@Override
	public void fall(float distance, float damageMultiplier) {
		// Flying creature, never takes fall damage.
	}

	protected boolean canTriggerWalking() {
		return false;
	}

	// --- Taming ---

	@Override
	public boolean interact(EntityPlayer player) {
		ItemStack stack = player.inventory.getCurrentItem();

		if (this.isTamed()) {
			return super.interact(player);
		}

		if (stack != null && stack.getItem() == Items.pumpkin_seeds) {
			if (!player.capabilities.isCreativeMode) {
				stack.stackSize--;
			}
			if (!this.worldObj.isRemote) {
				if (this.rand.nextInt(3) == 0) {
					this.setTamed(true);
					this.setOwnerId(player.getUniqueID().toString());
					this.playTameEffect(true);
					this.worldObj.setEntityState(this, (byte) 7);
				} else {
					this.playTameEffect(false);
					this.worldObj.setEntityState(this, (byte) 6);
				}
			}
			return true;
		}

		return super.interact(player);
	}

	@Override
	public EntityAgeable createChild(EntityAgeable ageable) {
		return new EntityCrow(this.worldObj);
	}
}