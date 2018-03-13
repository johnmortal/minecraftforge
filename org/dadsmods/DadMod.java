package org.dadsmods;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = DadMod.MODID, name = DadMod.NAME, version = DadMod.VERSION)
public class DadMod {
	public static final String MODID = "dadsmods";
	public static final String NAME = "Dads Mods";
	public static final String VERSION = "1.0";
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new DadMakeBlock());
	}

}
