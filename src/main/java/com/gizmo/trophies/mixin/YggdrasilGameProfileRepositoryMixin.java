package com.gizmo.trophies.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.authlib.yggdrasil.YggdrasilGameProfileRepository;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(YggdrasilGameProfileRepository.class)
public class YggdrasilGameProfileRepositoryMixin {

	@WrapOperation(method = "findProfileByName", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V"), remap = false)
	public void reduceProfileExceptionVisibility(Logger instance, String s, Object name, Object exception, Operation<Void> original) {
		//NO-OP
		//The profile warning is annoying to deal with when renaming player trophies
	}
}
