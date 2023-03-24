package com.gizmo.trophies;

import com.gizmo.trophies.item.TrophyItem;
import com.gizmo.trophies.trophy.Trophy;
import com.gizmo.trophies.trophy.behaviors.*;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.VillagerDataHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mod(OpenBlocksTrophies.MODID)
public class OpenBlocksTrophies {
	public static final String MODID = "obtrophies";

	public static final Logger LOGGER = LogManager.getLogger(MODID);
	public static final RandomSource TROPHY_RANDOM = RandomSource.create();
	public static final CreativeModeTab TROPHY_TAB = new CreativeModeTab("obtrophies") {

		private List<String> keys = new ArrayList<>();

		@Override
		public ItemStack makeIcon() {
			if (this.keys.isEmpty() && !Trophy.getTrophies().isEmpty()) {
				this.keys = Trophy.getTrophies().keySet().stream().map(ResourceLocation::toString).collect(Collectors.toList());
			}

			ItemStack stack = new ItemStack(Registries.TROPHY_ITEM.get());
			CompoundTag tag = new CompoundTag();
			tag.putString(TrophyItem.ENTITY_TAG, this.keys.get(TROPHY_RANDOM.nextInt(this.keys.size())));
			stack.addTagElement("BlockEntityTag", tag);
			return stack;
		}

		@Override
		public ItemStack getIconItem() {
			this.makeIcon();
			ItemStack stack = new ItemStack(Registries.TROPHY_ITEM.get());
			CompoundTag tag = new CompoundTag();
			tag.putString(TrophyItem.ENTITY_TAG, this.keys.get((int) (Minecraft.getInstance().level.getGameTime() / 20 % this.keys.size())));
			stack.addTagElement("BlockEntityTag", tag);
			return stack;
		}
	};

	public OpenBlocksTrophies() {
		{
			final Pair<TrophyConfig.CommonConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(TrophyConfig.CommonConfig::new);
			ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, specPair.getRight());
			TrophyConfig.COMMON_CONFIG = specPair.getLeft();
		}

		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		bus.addListener(this::commonSetup);
		MinecraftForge.EVENT_BUS.addListener(this::maybeDropTrophy);
		MinecraftForge.EVENT_BUS.addListener(this::registerCommands);
		MinecraftForge.EVENT_BUS.addListener(Trophy::reloadTrophies);
		MinecraftForge.EVENT_BUS.addListener(Trophy::syncTrophiesToClient);

		Registries.BLOCKS.register(bus);
		Registries.BLOCK_ENTITIES.register(bus);
		Registries.ITEMS.register(bus);
	}

	public static ResourceLocation location(String path) {
		return new ResourceLocation(MODID, path);
	}

	public void commonSetup(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			CustomBehaviorRegistry.registerBehavior(new MobEffectBehavior());
			CustomBehaviorRegistry.registerBehavior(new ItemDropBehavior());
			CustomBehaviorRegistry.registerBehavior(new PullFromLootTableBehavior());
			CustomBehaviorRegistry.registerBehavior(new ElderGuardianCurseBehavior());
			CustomBehaviorRegistry.registerBehavior(new ExplosionBehavior());
			CustomBehaviorRegistry.registerBehavior(new PlaceBlockBehavior());
			CustomBehaviorRegistry.registerBehavior(new PlayerSetFireBehavior());
			CustomBehaviorRegistry.registerBehavior(new ShootArrowBehavior());
			CustomBehaviorRegistry.registerBehavior(new ShootEnderPearlBehavior());
			CustomBehaviorRegistry.registerBehavior(new ShootLlamaSpitBehavior());
			CustomBehaviorRegistry.registerBehavior(new TotemOfUndyingEffectBehavior());
		});
		TrophyNetworkHandler.init();
	}

	public void registerCommands(RegisterCommandsEvent event) {
		TrophiesCommands.register(event.getDispatcher());
	}

	public void maybeDropTrophy(LivingDropsEvent event) {
		if (!(event.getSource().getEntity() instanceof Player) && !TrophyConfig.COMMON_CONFIG.anySourceDropsTrophies.get())
			return;
		if (event.getSource().getEntity() instanceof FakePlayer && !TrophyConfig.COMMON_CONFIG.fakePlayersDropTrophies.get())
			return;

		if (Trophy.getTrophies().containsKey(ForgeRegistries.ENTITY_TYPES.getKey(event.getEntity().getType()))) {
			Trophy trophy = Trophy.getTrophies().get(ForgeRegistries.ENTITY_TYPES.getKey(event.getEntity().getType()));
			if (trophy != null) {
				double trophyDropChance = TrophyConfig.COMMON_CONFIG.dropChanceOverride.get() >= 0.0D ? TrophyConfig.COMMON_CONFIG.dropChanceOverride.get() : trophy.getDropChance();
				double chance = ((event.getLootingLevel() + (TROPHY_RANDOM.nextDouble() / 4)) * trophyDropChance) - TROPHY_RANDOM.nextDouble();
				if (chance > 0.0D) {
					event.getDrops().add(new ItemEntity(event.getEntity().getLevel(), event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), TrophyItem.loadEntityToTrophy(trophy.getType(), this.fetchVariantIfAny(event.getEntity(), trophy))));
				}
			}
		}
	}

	private int fetchVariantIfAny(LivingEntity entity, Trophy trophy) {
		if (!trophy.getVariants(entity.getLevel().registryAccess()).isEmpty()) {
			CompoundTag tag = new CompoundTag();
			entity.addAdditionalSaveData(tag);
			for (int i = 0; i < trophy.getVariants(entity.getLevel().registryAccess()).size(); i++) {
				Map<String, String> variantKeys = trophy.getVariants(entity.getLevel().registryAccess()).get(i);
				for (Map.Entry<String, String> entry : variantKeys.entrySet()) {
					if (entity instanceof VillagerDataHolder villager) {
						if (ForgeRegistries.VILLAGER_PROFESSIONS.getKey(villager.getVillagerData().getProfession()).toString().equals(entry.getValue())) {
							return i;
						}
					} else {
						if (tag.contains(entry.getKey())) {
							if (StringUtils.isNumeric(entry.getValue()) && tag.getInt(entry.getKey()) == Integer.parseInt(entry.getValue())) {
								return i;
							} else if ((entry.getValue().equals("false") || entry.getValue().equals("true")) && tag.getBoolean(entry.getKey()) == Boolean.parseBoolean(entry.getValue())) {
								return i;
							} else if (tag.getString(entry.getKey()).equals(entry.getValue()) || tag.getString(entry.getKey()).equals(new ResourceLocation(entry.getValue()).toString())) {
								return i;
							}
						}
					}
				}
			}
		}
		return 0;
	}
}
