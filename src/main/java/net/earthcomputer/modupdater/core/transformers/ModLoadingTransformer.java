package net.earthcomputer.modupdater.core.transformers;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import net.minecraft.launchwrapper.IClassTransformer;

public class ModLoadingTransformer implements IClassTransformer {

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		if ("net.minecraftforge.fml.common.Loader".equals(name) || "cpw.mods.fml.common.Loader".equals(name)) {
			ClassReader reader = new ClassReader(basicClass);
			ClassNode node = new ClassNode();
			reader.accept(node, ClassReader.SKIP_FRAMES);

			for (MethodNode method : node.methods) {
				if ("sortModList".equals(method.name)) {
					method.instructions.insert(new MethodInsnNode(Opcodes.INVOKESTATIC,
							"net/earthcomputer/modupdater/core/Hooks", "onPreSortModList", "()V", false));
				}
			}

			ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
			node.accept(writer);
			return writer.toByteArray();
		} else {
			return basicClass;
		}
	}

}
