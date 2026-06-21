package com.gizmo.trophies.datagen;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.gizmo.trophies.client.renderer.item.DisplayTrophySpecialRenderer;
import com.gizmo.trophies.client.renderer.item.TrophySpecialRenderer;
import com.gizmo.trophies.init.TrophyItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.renderer.item.properties.select.DisplayContext;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;
import java.util.stream.Stream;

public class ItemModelGenerator extends ModelProvider {

	public ItemModelGenerator(PackOutput output) {
		super(output, OpenBlocksTrophies.MODID);
	}

	@Override
	protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
		itemModels.itemModelOutput.accept(TrophyItems.TROPHY.get(), ItemModelUtils.composite(
			ItemModelUtils.plainModel(OpenBlocksTrophies.prefix("block/trophy")),
			ItemModelUtils.select(new DisplayContext(),
				ItemModelUtils.specialModel(OpenBlocksTrophies.prefix("block/trophy"), new TrophySpecialRenderer.Unbaked(Optional.empty())),
				ItemModelUtils.when(ItemDisplayContext.GUI, ItemModelUtils.specialModel(OpenBlocksTrophies.prefix("block/trophy"), new TrophySpecialRenderer.Unbaked(Optional.of(new Vec3(0.0F, 45.0F, 0.0F))))))
		));
		itemModels.itemModelOutput.accept(TrophyItems.DISPLAY_TROPHY.get(), ItemModelUtils.composite(
			ItemModelUtils.plainModel(OpenBlocksTrophies.prefix("block/trophy")),
			ItemModelUtils.select(new DisplayContext(),
				ItemModelUtils.specialModel(OpenBlocksTrophies.prefix("block/trophy"), new DisplayTrophySpecialRenderer.Unbaked(Optional.empty())),
				ItemModelUtils.when(ItemDisplayContext.GUI, ItemModelUtils.specialModel(OpenBlocksTrophies.prefix("block/trophy"), new DisplayTrophySpecialRenderer.Unbaked(Optional.of(new Vec3(0.0F, 45.0F, 0.0F))))))
		));
	}

	@Override
	protected Stream<? extends Holder<Block>> getKnownBlocks() {
		return Stream.empty();
	}

	@Override
	protected Stream<? extends Holder<Item>> getKnownItems() {
		return Stream.empty();
	}
}
