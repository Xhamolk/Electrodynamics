package electrodynamics.util;

import java.util.Random;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class InventoryUtil {

	public static ItemStack getFirstItemInArray(ItemStack[] array) {
		for (ItemStack stack : array) {
			if (stack != null) {
				return stack;
			}
		}
		
		return null;
	}
	
	/** Returns whether the TileEntity at the specified coordinates is an instance of IInventory */
	public static boolean isInventory(World world, int x, int y, int z) {
		return world.getBlockTileEntity(x, y, z) instanceof IInventory;
	}
	
	/** 
	 * Whether the specified item fits in the specified inventory
	 * @param inv Instance of IInventory to check
	 * @param stack Instance of ItemStack to check
	 * @return Whether item fits or not
	 */
	public static boolean canStoreItem(IInventory inv, ItemStack stack) {
		return roomForItem(inv, stack) > 0;
	}
	
	/** 
	 * Whether the specified item fits in the specified inventory, and if so, how many of specified item
	 * @param inv Instance of IInventory to check
	 * @param stack Instance of ItemStack to check
	 * @return Amount of items from the specified stack that will fit
	 */
	public static int roomForItem(IInventory inv, ItemStack stack) {
		if (stack == null) return 0;
		
		int totalRoom = 0;
		for (int i=0; i<inv.getSizeInventory(); i++) {
			ItemStack invStack = inv.getStackInSlot(i);
			
			if (invStack == null) {
				totalRoom += Math.min(inv.getInventoryStackLimit(), stack.getMaxStackSize());
				continue;
			}
			
			if (stack.itemID != invStack.itemID || (!stack.getItem().isDamageable() && stack.getItemDamage() != invStack.getItemDamage())) {
				continue;
			}
			
			totalRoom += (Math.min(inv.getInventoryStackLimit(), stack.getMaxStackSize() - stack.stackSize));
		}
		
		return totalRoom;
	}
	
	public static void dispenseOutSide(World world, int x, int y, int z, ForgeDirection side, ItemStack item, Random random) {
		x += side.offsetX;
		y += side.offsetY;
		z += side.offsetZ;
		
		if (isInventory(world, x, y, z)) {
			IInventory inventory = (IInventory) world.getBlockTileEntity(x, y, z);
			
			if (canStoreItem(inventory, item)) {
				addToInventory(inventory, item);
			} else {
				ejectItem(world, x, y, z, side, item, random);
			}
		} else {
			ejectItem(world, x, y, z, side, item, random);
		}
	}
	
	/**
	 * Attempts to add the specified ItemStack to the specified instance of IInventory, and returns the remainder
	 * @param inv Instance of IInventory to check
	 * @param stack Instance of ItemStack to check
	 * @return null if the ItemStack fits, otherwise, the remainder
	 */
	public static ItemStack addToInventory(IInventory inv, ItemStack stack) {
		for (int i=0; i<inv.getSizeInventory(); i++) {
			ItemStack invStack = inv.getStackInSlot(i);
			
			if (invStack == null) {
				inv.setInventorySlotContents(i, stack);
				return null;
			}
			
			if (stack.itemID == invStack.itemID && (stack.getItem().isDamageable() || stack.getItemDamage() == invStack.getItemDamage()) && ItemStack.areItemStackTagsEqual(stack, invStack)) {
				if (stack.stackSize + invStack.stackSize <= stack.getMaxStackSize()) {
					invStack.stackSize += stack.stackSize;
					return null;
				}
				
				int itemsToMove = invStack.getMaxStackSize() - stack.stackSize;
				invStack.stackSize += itemsToMove;
				stack.stackSize -= itemsToMove;
				inv.setInventorySlotContents(i, stack);
			}
		}
		
		return stack;
	}

	/** Ejects the specified item out to the specified side */
	public static void ejectItem(World world, int x, int y, int z, ForgeDirection side, ItemStack item, Random random) {
		if (item != null) {
			double spawnX = x + 0.5D + (0.5D * side.offsetX);
			double spawnY = y + 0.5D + (0.5D * side.offsetY);
			double spawnZ = z + 0.5D + (0.5D * side.offsetZ);
			EntityItem entity = new EntityItem(world, spawnX, spawnY, spawnZ, item);
			
			if (item.hasTagCompound()) {
				entity.getEntityItem().setTagCompound((NBTTagCompound) item.getTagCompound().copy());
			}

			world.spawnEntityInWorld(entity);
		}
	}
	
}
