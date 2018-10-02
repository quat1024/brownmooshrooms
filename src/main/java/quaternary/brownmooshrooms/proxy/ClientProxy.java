package quaternary.brownmooshrooms.proxy;

import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import quaternary.brownmooshrooms.EntityBrownMooshroom;
import quaternary.brownmooshrooms.client.RenderBrownMooshroom;

public class ClientProxy extends CommonProxy {
	@Override
	public void preinit(FMLPreInitializationEvent e) {
		super.preinit(e);
		
		RenderingRegistry.registerEntityRenderingHandler(EntityBrownMooshroom.class, RenderBrownMooshroom::new);
	}
}
