package quaternary.brownmooshrooms;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.init.Biomes;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.registries.IForgeRegistry;
import quaternary.brownmooshrooms.proxy.CommonProxy;

@Mod(modid = BrownMooshrooms.MODID, name = BrownMooshrooms.NAME, version = BrownMooshrooms.VERSION)
@Mod.EventBusSubscriber(modid = BrownMooshrooms.MODID)
public class BrownMooshrooms {
	public static final String MODID = "brownmooshrooms";
	public static final String NAME = "Brown Mooshrooms";
	public static final String VERSION = "GRADLE:VERSION";
	
	@SidedProxy(clientSide = "quaternary.brownmooshrooms.proxy.ClientProxy", serverSide = "quaternary.brownmooshrooms.proxy.CommonProxy")
	public static CommonProxy PROXY = null;
	
	@Mod.EventHandler
	public static void preinit(FMLPreInitializationEvent e) {
		//Yeah shoutouts to this 1 entity renderer thing having to be done in preinit
		PROXY.preinit(e);
	}
	
	@SubscribeEvent
	public static void entities(RegistryEvent.Register<EntityEntry> e) {
		IForgeRegistry<EntityEntry> reg = e.getRegistry();
		
		reg.register(
						EntityEntryBuilder.create()
						.entity(EntityBrownMooshroom.class)
						.egg(0xad7855, 0xb7b7b7)
						.tracker(128, 2, true)   //see BiomeMushroomIsland for weights
						.spawn(EnumCreatureType.CREATURE, 8, 4, 8, Biomes.MUSHROOM_ISLAND, Biomes.MUSHROOM_ISLAND_SHORE)
						.id(new ResourceLocation(MODID, "brown_mooshroom"), 0)
						.name("brown_mooshroom")
						.build()
		);
	}
	
	@SubscribeEvent
	public static void lightingStrike(EntityStruckByLightningEvent e) {
		Entity hit = e.getEntity();
		World world = hit.world;
		if(world.isRemote) return;
		
		if(hit instanceof EntityMooshroom) {
			EntityMooshroom hitMoo = (EntityMooshroom) hit;
			
			//This prevents it from flickering back and forth
			//Since this event is fired every tick that the lightning bolt exists.
			if(hitMoo.isPotionActive(MobEffects.RESISTANCE)) return;
			
			boolean hitIsBrown = hitMoo instanceof EntityBrownMooshroom;
			EntityMooshroom newMoo = hitIsBrown ? new EntityMooshroom(world) : new EntityBrownMooshroom(world);
			
			newMoo.setLocationAndAngles(hitMoo.posX, hitMoo.posY, hitMoo.posZ, hitMoo.rotationYaw, hitMoo.rotationPitch);
			newMoo.setHealth(hitMoo.getHealth());
			newMoo.setGrowingAge(hitMoo.getGrowingAge());
			newMoo.renderYawOffset = hitMoo.renderYawOffset;
			
			if (hitMoo.hasCustomName()) {
				newMoo.setCustomNameTag(hitMoo.getCustomNameTag());
			}
			
			//They keep dying lmao
			newMoo.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 200));
			newMoo.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 200, 4));
			
			world.spawnEntity(newMoo);
			hit.setDead();
		}
	}
}
