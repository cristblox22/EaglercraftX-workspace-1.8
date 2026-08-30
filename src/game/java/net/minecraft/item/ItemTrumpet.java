package net.minecraft.item;

import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class ItemTrumpet extends Item {
    public ItemTrumpet() {
        this.setMaxStackSize(1);
        this.setMaxDamage(20);
        this.setCreativeTab(CreativeTabs.tabMisc);
        this.setUnlocalizedName("trumpet");
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
        if (!worldIn.isRemote) {
            long lastUse = itemStackIn.hasTagCompound() ? itemStackIn.getTagCompound().getLong("LastTrumpetUse") : 0L;
            long currentTime = worldIn.getTotalWorldTime();
            
            if (currentTime - lastUse >= 120) {
                if (itemStackIn.getItemDamage() < itemStackIn.getMaxDamage()) {
                    if (!itemStackIn.hasTagCompound()) {
                        itemStackIn.setTagCompound(new net.minecraft.nbt.NBTTagCompound());
                    }
                    itemStackIn.getTagCompound().setLong("LastTrumpetUse", currentTime);
                    worldIn.playSoundAtEntity(playerIn, "custom.trumpet", 1.0F, 1.0F);
                    playerIn.setItemInUse(itemStackIn, this.getMaxItemUseDuration(itemStackIn));

                    itemStackIn.damageItem(1, playerIn);

                    double radius = 4.0D;
                    AxisAlignedBB bb = playerIn.getEntityBoundingBox().expand(radius, radius, radius);
                    List<EntityLivingBase> entities = worldIn.getEntitiesWithinAABB(EntityLivingBase.class, bb);

                    for (EntityLivingBase entity : entities) {
                        if (entity != playerIn) {
                            double dx = entity.posX - playerIn.posX;
                            double dz = entity.posZ - playerIn.posZ;
                            double dist = Math.sqrt(dx * dx + dz * dz);
                            if (dist > 0.0D) {
                                dx /= dist;
                                dz /= dist;
                            } else {
                                dx = 1.0D;
                                dz = 0.0D;
                            }
                            entity.addVelocity(dx * 2.0D, 0.4D, dz * 2.0D);
                            entity.isAirBorne = true;
                            entity.attackEntityFrom(DamageSource.causePlayerDamage(playerIn), 2.0F);
                        }
                    }
                }
            }
        }
        return itemStackIn;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 120;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.NONE;
    }
}