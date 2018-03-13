package org.dadsmods;

import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DadMakeBlock {
	
	@SubscribeEvent
	public void makeBlock(LivingJumpEvent event) {
		double x = event.getEntity().posX;
		double y = event.getEntity().posY;
		double z = event.getEntity().posZ;
		BlockPos above = new BlockPos(x, y - 1, z);
		if (!event.getEntity().getEntityWorld().isAirBlock(above) ) {
//			event.getEntity().getEntityWorld().getBlockState(above).
			event.getEntity().getEntityWorld().setBlockState(above, Blocks.WATER.getDefaultState());
		}
	}
}
