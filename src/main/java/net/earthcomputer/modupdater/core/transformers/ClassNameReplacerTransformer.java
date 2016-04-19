package net.earthcomputer.modupdater.core.transformers;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.TypePath;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LocalVariableAnnotationNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TryCatchBlockNode;
import org.objectweb.asm.tree.TypeAnnotationNode;

import net.earthcomputer.modupdater.core.ClassNameReplacements;
import net.minecraft.launchwrapper.IClassTransformer;

public class ClassNameReplacerTransformer implements IClassTransformer {

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		if (basicClass == null)
			return null;

		ClassReader reader = new ClassReader(basicClass);
		ReplacerClassVisitor visitor = new ReplacerClassVisitor();
		reader.accept(visitor, ClassReader.SKIP_FRAMES);

		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		visitor.accept(writer);
		return writer.toByteArray();
	}

	private static String renameInternal(String internalName) {
		return ClassNameReplacements.getReplacement(internalName.replace('/', '.')).replace('.', '/');
	}

	private static String renameDescriptor(String desc) {
		return renameType(Type.getType(desc)).getDescriptor();
	}

	private static Type renameType(Type type) {
		int sort = type.getSort();
		if (sort == Type.OBJECT) {
			return Type.getObjectType(ClassNameReplacements.getReplacement(type.getClassName()).replace('.', '/'));
		} else if (sort == Type.ARRAY) {
			String dimensions = type.getDescriptor().substring(0, type.getDimensions());
			return Type.getType(dimensions + renameType(type.getElementType()));
		} else if (sort == Type.METHOD) {
			Type[] argumentTypes = type.getArgumentTypes();
			for (int i = 0; i < argumentTypes.length; i++) {
				argumentTypes[i] = renameType(argumentTypes[i]);
			}
			return Type.getMethodType(renameType(type.getReturnType()), argumentTypes);
		} else {
			return type;
		}
	}

	private static class ReplacerClassVisitor extends ClassNode {

		public ReplacerClassVisitor() {
			super(Opcodes.ASM5);
		}

		@Override
		public void visit(int version, int access, String name, String signature, String superName,
				String[] interfaces) {
			if (interfaces != null)
				for (int i = 0; i < interfaces.length; i++) {
					interfaces[i] = renameInternal(interfaces[i]);
				}
			super.visit(version, access, name, signature, renameInternal(superName), interfaces);
		}

		@Override
		public void visitOuterClass(String owner, String name, String desc) {
			super.visitOuterClass(renameInternal(owner), name, desc == null ? null : renameDescriptor(desc));
		}

		@Override
		public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
			ReplacerAnnotationVisitor an = new ReplacerAnnotationVisitor(renameDescriptor(desc));
			if (visible) {
				if (visibleAnnotations == null) {
					visibleAnnotations = new ArrayList<AnnotationNode>(1);
				}
				visibleAnnotations.add(an);
			} else {
				if (invisibleAnnotations == null) {
					invisibleAnnotations = new ArrayList<AnnotationNode>(1);
				}
				invisibleAnnotations.add(an);
			}
			return an;
		}

		@Override
		public void visitInnerClass(String name, String outerName, String innerName, int access) {
			super.visitInnerClass(renameInternal(name), outerName == null ? null : renameInternal(outerName), innerName,
					access);
		}

		@Override
		public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
			ReplacerFieldVisitor fn = new ReplacerFieldVisitor(access, name, renameDescriptor(desc), signature, value);
			fields.add(fn);
			return fn;
		}

		@Override
		public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
			if (exceptions != null)
				for (int i = 0; i < exceptions.length; i++) {
					exceptions[i] = renameInternal(exceptions[i]);
				}
			ReplacerMethodVisitor mn = new ReplacerMethodVisitor(access, name, renameDescriptor(desc), signature,
					exceptions);
			methods.add(mn);
			return mn;
		}

	}

	private static class ReplacerAnnotationVisitor extends AnnotationNode {

		public ReplacerAnnotationVisitor(String desc) {
			super(Opcodes.ASM5, desc);
		}

		public ReplacerAnnotationVisitor(List<Object> values) {
			this((String) null);
			this.values = values;
		}

		@Override
		public AnnotationVisitor visitAnnotation(String name, String desc) {
			if (values == null) {
				values = new ArrayList<Object>(this.desc != null ? 2 : 1);
			}
			if (this.desc != null) {
				values.add(name);
			}
			ReplacerAnnotationVisitor annotation = new ReplacerAnnotationVisitor(renameDescriptor(desc));
			values.add(annotation);
			return annotation;
		}

		@Override
		public void visitEnum(String name, String desc, String value) {
			super.visitEnum(name, renameDescriptor(desc), value);
		}

		@Override
		public AnnotationVisitor visitArray(String name) {
			if (values == null) {
				values = new ArrayList<Object>(this.desc != null ? 2 : 1);
			}
			if (this.desc != null) {
				values.add(name);
			}
			List<Object> array = new ArrayList<Object>();
			values.add(array);
			return new ReplacerAnnotationVisitor(array);
		}

	}

	private static class ReplacerTypeAnnotationVisitor extends TypeAnnotationNode {

		public ReplacerTypeAnnotationVisitor(int typeRef, TypePath typePath, String desc) {
			super(Opcodes.ASM5, typeRef, typePath, desc);
		}

		@Override
		public AnnotationVisitor visitAnnotation(String name, String desc) {
			if (values == null) {
				values = new ArrayList<Object>(this.desc != null ? 2 : 1);
			}
			if (this.desc != null) {
				values.add(name);
			}
			ReplacerAnnotationVisitor annotation = new ReplacerAnnotationVisitor(renameDescriptor(desc));
			values.add(annotation);
			return annotation;
		}

		@Override
		public void visitEnum(String name, String desc, String value) {
			super.visitEnum(name, renameDescriptor(desc), value);
		}

		@Override
		public AnnotationVisitor visitArray(String name) {
			if (values == null) {
				values = new ArrayList<Object>(this.desc != null ? 2 : 1);
			}
			if (this.desc != null) {
				values.add(name);
			}
			List<Object> array = new ArrayList<Object>();
			values.add(array);
			return new ReplacerAnnotationVisitor(array);
		}

	}

	private static class ReplacerLocalVariableAnnotationVisitor extends LocalVariableAnnotationNode {

		public ReplacerLocalVariableAnnotationVisitor(int typeRef, TypePath typePath, LabelNode[] start,
				LabelNode[] end, int[] index, String desc) {
			super(Opcodes.ASM5, typeRef, typePath, start, end, index, desc);
		}

		@Override
		public AnnotationVisitor visitAnnotation(String name, String desc) {
			if (values == null) {
				values = new ArrayList<Object>(this.desc != null ? 2 : 1);
			}
			if (this.desc != null) {
				values.add(name);
			}
			ReplacerAnnotationVisitor annotation = new ReplacerAnnotationVisitor(renameDescriptor(desc));
			values.add(annotation);
			return annotation;
		}

		@Override
		public void visitEnum(String name, String desc, String value) {
			super.visitEnum(name, renameDescriptor(desc), value);
		}

		@Override
		public AnnotationVisitor visitArray(String name) {
			if (values == null) {
				values = new ArrayList<Object>(this.desc != null ? 2 : 1);
			}
			if (this.desc != null) {
				values.add(name);
			}
			List<Object> array = new ArrayList<Object>();
			values.add(array);
			return new ReplacerAnnotationVisitor(array);
		}

	}

	private static class ReplacerFieldVisitor extends FieldNode {

		public ReplacerFieldVisitor(int access, String name, String desc, String signature, Object value) {
			super(Opcodes.ASM5, access, name, desc, signature, value);
		}

		@Override
		public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
			ReplacerAnnotationVisitor an = new ReplacerAnnotationVisitor(renameDescriptor(desc));
			if (visible) {
				if (visibleAnnotations == null) {
					visibleAnnotations = new ArrayList<AnnotationNode>(1);
				}
				visibleAnnotations.add(an);
			} else {
				if (invisibleAnnotations == null) {
					invisibleAnnotations = new ArrayList<AnnotationNode>(1);
				}
				invisibleAnnotations.add(an);
			}
			return an;
		}

	}

	private static class ReplacerMethodVisitor extends MethodNode {

		public ReplacerMethodVisitor(int access, String name, String desc, String signature, String[] exceptions) {
			super(Opcodes.ASM5, access, name, desc, signature, exceptions);
		}

		@Override
		public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
			ReplacerAnnotationVisitor an = new ReplacerAnnotationVisitor(renameDescriptor(desc));
			if (visible) {
				if (visibleAnnotations == null) {
					visibleAnnotations = new ArrayList<AnnotationNode>(1);
				}
				visibleAnnotations.add(an);
			} else {
				if (invisibleAnnotations == null) {
					invisibleAnnotations = new ArrayList<AnnotationNode>(1);
				}
				invisibleAnnotations.add(an);
			}
			return an;
		}

		@SuppressWarnings("unchecked")
		@Override
		public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
			ReplacerAnnotationVisitor an = new ReplacerAnnotationVisitor(renameDescriptor(desc));
			if (visible) {
				if (visibleParameterAnnotations == null) {
					int params = Type.getArgumentTypes(this.desc).length;
					visibleParameterAnnotations = (List<AnnotationNode>[]) new List<?>[params];
				}
				if (visibleParameterAnnotations[parameter] == null) {
					visibleParameterAnnotations[parameter] = new ArrayList<AnnotationNode>(1);
				}
				visibleParameterAnnotations[parameter].add(an);
			} else {
				if (invisibleParameterAnnotations == null) {
					int params = Type.getArgumentTypes(this.desc).length;
					invisibleParameterAnnotations = (List<AnnotationNode>[]) new List<?>[params];
				}
				if (invisibleParameterAnnotations[parameter] == null) {
					invisibleParameterAnnotations[parameter] = new ArrayList<AnnotationNode>(1);
				}
				invisibleParameterAnnotations[parameter].add(an);
			}
			return an;
		}

		@Override
		public void visitTypeInsn(int opcode, String type) {
			super.visitTypeInsn(opcode, renameInternal(type));
		}

		@Override
		public void visitFieldInsn(int opcode, String owner, String name, String desc) {
			super.visitFieldInsn(opcode, renameInternal(owner), name, renameDescriptor(desc));
		}

		@Override
		public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
			super.visitMethodInsn(opcode, renameInternal(owner), name, renameDescriptor(desc), itf);
		}

		@Override
		public void visitLdcInsn(Object cst) {
			if (cst instanceof Type) {
				cst = renameType((Type) cst);
			}
			super.visitLdcInsn(cst);
		}

		@Override
		public void visitMultiANewArrayInsn(String desc, int dims) {
			super.visitMultiANewArrayInsn(renameDescriptor(desc), dims);
		}

		@Override
		public AnnotationVisitor visitInsnAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
			// Finds the last real instruction, i.e. the instruction targeted by
			// this annotation.
			AbstractInsnNode insn = instructions.getLast();
			while (insn.getOpcode() == -1) {
				insn = insn.getPrevious();
			}
			// Adds the annotation to this instruction.
			ReplacerTypeAnnotationVisitor an = new ReplacerTypeAnnotationVisitor(typeRef, typePath,
					renameDescriptor(desc));
			if (visible) {
				if (insn.visibleTypeAnnotations == null) {
					insn.visibleTypeAnnotations = new ArrayList<TypeAnnotationNode>(1);
				}
				insn.visibleTypeAnnotations.add(an);
			} else {
				if (insn.invisibleTypeAnnotations == null) {
					insn.invisibleTypeAnnotations = new ArrayList<TypeAnnotationNode>(1);
				}
				insn.invisibleTypeAnnotations.add(an);
			}
			return an;
		}

		@Override
		public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
			super.visitTryCatchBlock(start, end, handler, type == null ? null : renameInternal(type));
		}

		@Override
		public AnnotationVisitor visitTryCatchAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
			TryCatchBlockNode tcb = tryCatchBlocks.get((typeRef & 0x00FFFF00) >> 8);
			ReplacerTypeAnnotationVisitor an = new ReplacerTypeAnnotationVisitor(typeRef, typePath,
					renameDescriptor(desc));
			if (visible) {
				if (tcb.visibleTypeAnnotations == null) {
					tcb.visibleTypeAnnotations = new ArrayList<TypeAnnotationNode>(1);
				}
				tcb.visibleTypeAnnotations.add(an);
			} else {
				if (tcb.invisibleTypeAnnotations == null) {
					tcb.invisibleTypeAnnotations = new ArrayList<TypeAnnotationNode>(1);
				}
				tcb.invisibleTypeAnnotations.add(an);
			}
			return an;
		}

		@Override
		public AnnotationVisitor visitLocalVariableAnnotation(int typeRef, TypePath typePath, Label[] start,
				Label[] end, int[] index, String desc, boolean visible) {
			ReplacerLocalVariableAnnotationVisitor an = new ReplacerLocalVariableAnnotationVisitor(typeRef, typePath,
					getLabelNodes(start), getLabelNodes(end), index, renameDescriptor(desc));
			if (visible) {
				if (visibleLocalVariableAnnotations == null) {
					visibleLocalVariableAnnotations = new ArrayList<LocalVariableAnnotationNode>(1);
				}
				visibleLocalVariableAnnotations.add(an);
			} else {
				if (invisibleLocalVariableAnnotations == null) {
					invisibleLocalVariableAnnotations = new ArrayList<LocalVariableAnnotationNode>(1);
				}
				invisibleLocalVariableAnnotations.add(an);
			}
			return an;
		}

		private LabelNode[] getLabelNodes(Label[] l) {
			LabelNode[] nodes = new LabelNode[l.length];
			for (int i = 0; i < l.length; ++i) {
				nodes[i] = getLabelNode(l[i]);
			}
			return nodes;
		}

	}

}
