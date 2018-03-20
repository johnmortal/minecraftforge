package org.dadsmods;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BigWorld {

	public static int radius = 8;
	public static int factor = 3;
	
	@SubscribeEvent
	public void makeBlock(LivingJumpEvent event) {
		
		World world = event.getEntity().getEntityWorld();
		
		if (!(event.getEntity() instanceof EntityPlayer)) return;
		
		
		double x = event.getEntity().posX;
		double y = event.getEntity().posY;
		double z = event.getEntity().posZ;
		
		int xInt = (int) Math.round(x); 
		int yInt = (int) Math.round(y); 
		int zInt = (int) Math.round(z); 
		
		double sourceX = x - radius;
		double sourceY = y - radius;
		double sourceZ = z - radius;

		double destinationX = x - factor * radius;
		double destinationY = y - factor * radius;
		double destinationZ = z - factor * radius;
		
		
		// Initialize the array
		IBlockState[][][] copy = new IBlockState[2*radius][][];
		for ( int i = 0; i < 2*radius; i++ ) {
			copy[i] = new IBlockState[2*radius][];
			for ( int j = 0; j < 2*radius; j ++ ) {
				copy[i][j] = new IBlockState[2*radius];
			}
		}
		
		// Fill the array with a copy, use null for blocks that are air
		for ( int i = 0; i < 2 * radius; i ++ ) {
			for ( int j = 0; j < 2 * radius; j ++ ) {
				for ( int k = 0; k < 2*radius; k ++ ) {
					BlockPos position = new BlockPos(sourceX + i, sourceY + j, sourceZ + k);
					if (!world.isAirBlock(position)) 	
						copy[i][j][k] = world.getBlockState(position);
				}
			}
		}
		
		// Use the array to make a copy, at factor times the original scale
		
		for ( int i = 0; i < 2 * radius*factor; i ++ ) {
			for ( int j = 0; j < 2 * radius*factor; j ++ ) {
				for ( int k = 0; k < 2*radius*factor; k ++ ) {
					// get the position in the world we are writing to
					BlockPos position = new BlockPos(destinationX + i, destinationY + j, destinationZ + k );
					// get the index of the arrays where we are copying from
					int iIndex = (int) Math.floor(i / factor);
					int jIndex = (int) Math.floor(j / factor);
					int kIndex = (int) Math.floor(k / factor);
					if ( copy[iIndex][jIndex][kIndex] == null ) { // null entries where air
						world.setBlockToAir(position);
					} else { // set block state to copies location
						world.setBlockState(position, copy[iIndex][jIndex][kIndex]);
					}
				}
			}
		}
		
		

		System.out.println("BigWorld at " + x + "," + "y" + "," + z);
				
	}

}
