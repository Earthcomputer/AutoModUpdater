package net.earthcomputer.modupdater.core.transformers;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import net.earthcomputer.modupdater.core.AccessMethod;
import net.earthcomputer.modupdater.core.FieldAccess;
import net.earthcomputer.modupdater.core.Getter;
import net.earthcomputer.modupdater.core.MethodAccess;
import net.earthcomputer.modupdater.core.Setter;
import net.minecraft.launchwrapper.IClassTransformer;
import scala.tools.asm.Type;

public class MemberAccessTransformer implements IClassTransformer {

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		if (basicClass == null)
			return null;

		ClassReader reader = new ClassReader(basicClass);
		ClassNode node = new ClassNode();
		reader.accept(node, 0);

		for (MethodNode method : node.methods) {
			boolean isGetter = false;
			boolean isSetter = false;
			boolean isAccessMethod = false;
			if (method.visibleAnnotations != null) {
				for (AnnotationNode annotation : method.visibleAnnotations) {
					if (annotation.desc.equals(Type.getDescriptor(Getter.class))) {
						isGetter = true;
					} else if (annotation.desc.equals(Type.getDescriptor(Setter.class))) {
						isSetter = true;
					} else if (annotation.desc.equals(Type.getDescriptor(AccessMethod.class))) {
						isAccessMethod = true;
					}
				}
			}

			if (!isGetter && !isSetter) {
				for (AbstractInsnNode insn : method.instructions.toArray()) {
					if (insn.getType() == AbstractInsnNode.FIELD_INSN) {
						FieldInsnNode fieldInsn = (FieldInsnNode) insn;
						String ownerClassName = fieldInsn.owner.replace('/', '.');
						if (insn.getOpcode() == Opcodes.GETFIELD) {
							if (!isGetter) {
								String getter = FieldAccess.getGetter(ownerClassName, fieldInsn.name);
								if (getter != null) {
									method.instructions.set(insn, new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
											fieldInsn.owner, getter, "()" + fieldInsn.desc, false));
								}
							}
						} else if (insn.getOpcode() == Opcodes.GETSTATIC) {
							if (!isGetter) {
								String getter = FieldAccess.getGetter(ownerClassName, fieldInsn.name);
								if (getter != null) {
									method.instructions.set(insn, new MethodInsnNode(Opcodes.INVOKESTATIC,
											fieldInsn.owner, getter, "()" + fieldInsn.desc, false));
								}
							}
						} else if (insn.getOpcode() == Opcodes.PUTFIELD) {
							if (!isSetter) {
								String setter = FieldAccess.getSetter(ownerClassName, fieldInsn.name);
								if (setter != null) {
									method.instructions.set(insn, new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
											fieldInsn.owner, setter, "(" + fieldInsn.desc + ")V", false));
								}
							}
						} else if (insn.getOpcode() == Opcodes.PUTSTATIC) {
							if (!isSetter) {
								String setter = FieldAccess.getSetter(ownerClassName, fieldInsn.name);
								if (setter != null) {
									method.instructions.set(insn, new MethodInsnNode(Opcodes.INVOKESTATIC,
											fieldInsn.owner, setter, "(" + fieldInsn.desc + ")V", false));
								}
							}
						}
					} else if (insn.getType() == AbstractInsnNode.METHOD_INSN) {
						if (!isAccessMethod) {
							MethodInsnNode methodInsn = (MethodInsnNode) insn;
							String ownerClassName = methodInsn.owner.replace('/', '.');
							String replacement = MethodAccess.getNewName(ownerClassName, methodInsn.name,
									methodInsn.desc);
							if (replacement != null) {
								methodInsn.name = replacement;
							}
						}
					}
				}
			}
		}

		ClassWriter writer = new ClassWriter(0);
		node.accept(writer);
		return writer.toByteArray();
	}
}
