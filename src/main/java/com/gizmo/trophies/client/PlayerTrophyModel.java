package com.gizmo.trophies.client;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.player.Player;

public class PlayerTrophyModel extends PlayerModel<Player> {

	public PlayerTrophyModel(ModelPart root, boolean slim) {
		super(root, slim);
	}

	public static MeshDefinition createMesh(boolean slim) {
		MeshDefinition meshdefinition = PlayerModel.createMesh(CubeDeformation.NONE, slim);
		PartDefinition partdefinition = meshdefinition.getRoot();
		partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.ZERO);
		partdefinition.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(2.75F)), PartPose.ZERO);
		return meshdefinition;
	}

	@Override
	public void setupAnim(Player player, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch) {

	}
}
