package electrodynamics.block.item;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import electrodynamics.lib.block.Storage;

public class ItemBlockStorage extends ItemBlock {

	public ItemBlockStorage(int i) {
		super(i);
		setHasSubtypes(true);
	}

	public int getMetadata(int metadata) {
		return metadata;
	}

	public String getUnlocalizedName(ItemStack itemstack) {
		return Storage.get(itemstack.getItemDamage()).unlocalizedName;
	}
	
}
