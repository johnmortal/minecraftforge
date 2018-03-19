package org.dadsmods;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DadMakeBlock {

	static Random random = new Random();
	
	@SubscribeEvent
	public void makeBlock(LivingJumpEvent event) {
		double x = event.getEntity().posX;
		double y = event.getEntity().posY;
		double z = event.getEntity().posZ;
		int yInt = (int) Math.round(y); 
		
		if (random.nextInt(20) > 0) return;
		
		World world = event.getEntity().getEntityWorld();
		
//		if (event.getEntity() instanceof EntityPlayer) return;
		
		if ( world.isRemote ) return; // only run on server side
		
		int depth = random.nextInt(Math.max(1, yInt - 10));
		System.out.println("Explosion at " + depth + " where you are at " + y);
		BlockPos above = new BlockPos(x, y + 5 + random.nextInt(100), z);
		if ( y > 25 ) {
			world.createExplosion(null, x, depth, z, 8.0f, false);
		}
//		world.setBlockState(above, Blocks.SAND.getDefaultState());
		double maxHeight = y;
		double minHeight = 5;

		System.out.println("Explosion at " + depth + " at " + x + "," + "y" + "," + z);

		int destroyedStoneCount = 0;
		int destroyedDirtCount = 0;
		int destroyedGrassCount = 0;
		
		for ( int j = 5; j <= yInt + 5; ++j ) {
			int radius = (int) Math.round(Math.sqrt(j));
			for ( int i = -radius; i <= radius; ++i ) {
				for ( int k = -radius; k <= radius; ++k ) 
				{
					if ( j > maxHeight ) continue;
					if ( j < minHeight ) continue;
					BlockPos position = new BlockPos(x + i, j, z + k);
					IBlockState currentState = world.getBlockState(position);
					net.minecraft.block.Block currentBlock = world.getBlockState(position).getBlock();
					if ( currentBlock.equals(Blocks.STONE)) {
						world.setBlockToAir(position);
						destroyedStoneCount ++;
					}
					if ( currentBlock.equals(Blocks.DIRT)) {
						world.setBlockToAir(position);
						destroyedDirtCount ++;
					}
					if ( currentBlock.equals(Blocks.GRASS)) {
						world.setBlockToAir(position);
						destroyedGrassCount ++;
					}
					
//					net.minecraft.block.material.Material material =  
//					if (currentBlock.getMaterial().equals(Material.ROCK);				}
				}
			}
		}
		System.out.println("Explosion at " + depth + " at " + x + "," + "y" + "," + z);
		System.out.println("Destroyed " + destroyedStoneCount + " stone blocks");
		System.out.println("Destroyed " + destroyedDirtCount + " dirt blocks");
		System.out.println("Destroyed " + destroyedGrassCount + " grass blocks");
				
	}
}
