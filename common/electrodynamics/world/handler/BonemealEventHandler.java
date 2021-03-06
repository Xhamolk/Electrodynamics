package electrodynamics.world.handler;

import java.util.Random;

import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.BonemealEvent;
import electrodynamics.lib.block.BlockIDs;
import electrodynamics.util.BiomeHelper;
import electrodynamics.world.gen.WorldGenRubberTree;

public class BonemealEventHandler {

	@ForgeSubscribe
	public void onUseBonemeal(BonemealEvent e) {
		if (!e.world.isRemote) {
			if (e.ID == BlockIDs.BLOCK_RUBBER_SAPLING_ID) {
				(new WorldGenRubberTree(10, BiomeHelper.getBiomesForTypes(Type.PLAINS, Type.SWAMP, Type.JUNGLE))).grow(e.world, e.X, e.Y, e.Z, new Random());
			}
		}
	}
	
}
