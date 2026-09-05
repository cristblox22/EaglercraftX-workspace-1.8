package net.minecraft.entity.passive;

import java.util.List;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityRaccoon extends EntityCreature {

	public EntityRaccoon(World worldIn) {
		super(worldIn);
		this.setSize(0.7F, 0.7F);
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(1, new EntityAIWashFood(this));
		this.tasks.addTask(2, new EntityAIStealFood(this));
		this.tasks.addTask(3, new EntityAIWander(this, 1.0D));
		this.tasks.addTask(4, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
		this.tasks.addTask(5, new EntityAILookIdle(this));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(10.0D);
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.25D);
	}

	@Override
	protected Item getDropItem() {
		Item tail = Item.itemRegistry.getObject(new ResourceLocation("raccoon_tail"));
		return tail != null ? tail : super.getDropItem();
	}

	class EntityAIStealFood extends EntityAIBase {
		private final EntityRaccoon raccoon;
		private EntityPlayer target;

		public EntityAIStealFood(EntityRaccoon raccoon) {
			this.raccoon = raccoon;
			this.setMutexBits(3);
		}

		@Override
		public boolean shouldExecute() {
			if (this.raccoon.getHeldItem() != null) return false;
			List<EntityPlayer> list = this.raccoon.worldObj.getEntitiesWithinAABB(EntityPlayer.class, this.raccoon.getEntityBoundingBox().expand(8.0D, 3.0D, 8.0D));
			for (EntityPlayer p : list) {
				if (p.getHeldItem() != null && p.getHeldItem().getItem() instanceof ItemFood) {
					this.target = p;
					return true;
				}
			}
			return false;
		}

		@Override
		public void updateTask() {
			if (this.target == null) return;
			this.raccoon.getNavigator().tryMoveToEntityLiving(this.target, 1.2D);
			if (this.raccoon.getDistanceSqToEntity(this.target) < 4.0D) {
				ItemStack stack = this.target.getHeldItem();
				if (stack != null && stack.getItem() instanceof ItemFood) {
					ItemStack stolen = stack.copy();
					stolen.stackSize = 1;
					this.raccoon.setCurrentItemOrArmor(0, stolen);
					stack.stackSize--;
					if (stack.stackSize <= 0) {
						this.target.inventory.setInventorySlotContents(this.target.inventory.currentItem, null);
					}
				}
				this.target = null;
			}
		}
	}

	class EntityAIWashFood extends EntityAIBase {
		private final EntityRaccoon raccoon;
		private BlockPos waterPos;
		private int washTimer;

		public EntityAIWashFood(EntityRaccoon raccoon) {
			this.raccoon = raccoon;
			this.setMutexBits(3);
		}

		@Override
		public boolean shouldExecute() {
			if (this.raccoon.getHeldItem() == null) return false;
			if (!(this.raccoon.getHeldItem().getItem() instanceof ItemFood)) return false;
			this.waterPos = findWater();
			return this.waterPos != null;
		}

		private BlockPos findWater() {
			BlockPos pos = new BlockPos(this.raccoon);
			for (int x = -10; x <= 10; x++) {
				for (int y = -5; y <= 5; y++) {
					for (int z = -10; z <= 10; z++) {
						BlockPos p = pos.add(x, y, z);
						if (this.raccoon.worldObj.getBlockState(p).getBlock() == Blocks.water || this.raccoon.worldObj.getBlockState(p).getBlock() == Blocks.flowing_water) {
							return p;
						}
					}
				}
			}
			return null;
		}

		@Override
		public void startExecuting() {
			this.washTimer = 60;
		}

		@Override
		public void updateTask() {
			if (this.raccoon.getDistanceSq(this.waterPos) > 4.0D) {
				this.raccoon.getNavigator().tryMoveToXYZ(this.waterPos.getX(), this.waterPos.getY(), this.waterPos.getZ(), 1.2D);
			} else {
				this.washTimer--;
				if (this.washTimer % 5 == 0) {
					this.raccoon.worldObj.spawnParticle(EnumParticleTypes.WATER_SPLASH, this.raccoon.posX, this.raccoon.posY + 0.5D, this.raccoon.posZ, 0.0D, 0.0D, 0.0D);
				}
				if (this.washTimer <= 0) {
					this.raccoon.setCurrentItemOrArmor(0, null);
					this.raccoon.heal(5.0F);
					this.raccoon.worldObj.playSoundAtEntity(this.raccoon, "random.eat", 1.0F, 1.0F);
					for (int i = 0; i < 5; i++) {
						this.raccoon.worldObj.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, this.raccoon.posX + (this.raccoon.worldObj.rand.nextDouble() - 0.5D), this.raccoon.posY + this.raccoon.worldObj.rand.nextDouble(), this.raccoon.posZ + (this.raccoon.worldObj.rand.nextDouble() - 0.5D), 0.0D, 0.0D, 0.0D);
					}
				}
			}
		}
	}
}