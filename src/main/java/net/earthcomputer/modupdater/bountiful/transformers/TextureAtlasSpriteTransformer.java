package net.earthcomputer.modupdater.bountiful.transformers;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import net.minecraft.launchwrapper.IClassTransformer;

public class TextureAtlasSpriteTransformer implements IClassTransformer {

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		if (basicClass == null)
			return null;
		if ("net/minecraft/client/renderer/texture/TextureAtlasSprite".equals(transformedName)) {
			ClassReader reader = new ClassReader(basicClass);
			ClassNode node = new ClassNode();
			reader.accept(node, 0);
			node.interfaces.add("net/earthcomputer/modupdater/bountiful/oldclasses/IIcon");
			ClassWriter writer = new ClassWriter(0);
			node.accept(writer);
			return writer.toByteArray();
		}
		return basicClass;
	}

}
