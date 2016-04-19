package net.earthcomputer.modupdater.core.transformers;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.earthcomputer.modupdater.core.AdapterMethod;
import net.earthcomputer.modupdater.core.ClassAdapters;
import net.earthcomputer.modupdater.core.EnumPriority;
import net.earthcomputer.modupdater.core.InSubclasses;
import net.earthcomputer.modupdater.core.MethodPair;
import net.earthcomputer.modupdater.core.ModUpdaterPlugin;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;

public class AdapterClassTransformer implements IClassTransformer {

	private static final Map<String, List<MethodNode>> inSubclassesMethodsOldToNew = Maps.newHashMap();
	private static final Map<String, List<MethodNode>> inSubclassesMethodsNewToOld = Maps.newHashMap();

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		if (basicClass == null)
			return null;

		String internalName = transformedName.replace('.', '/');

		ClassReader reader = new ClassReader(basicClass);
		ClassNode originalClass = new ClassNode();
		reader.accept(originalClass, ClassReader.SKIP_FRAMES);

		// Finding the superclass tree loads superclasses and helps with the
		// optional overrides
		List<String> superclassTree = Lists.newArrayList();
		Class<?> superclass;
		try {
			superclass = Class.forName(originalClass.superName.replace('/', '.'), false, Launch.classLoader);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("It seems the class " + transformedName + " does not have a superclass", e);
		}
		while (superclass != null) {
			superclassTree.add(Type.getInternalName(superclass));
			superclass = superclass.getSuperclass();
		}

		for (String superclassName : superclassTree) {
			if (inSubclassesMethodsOldToNew.containsKey(superclassName)) {
				for (MethodNode newMethod : inSubclassesMethodsOldToNew.get(superclassName)) {
					if (tryAddSubclassMethod(newMethod, originalClass.methods)) {
						ModUpdaterPlugin
								.debug(String.format("Added method %s to class %s", newMethod.name, transformedName));
					}
				}
			}
			if (inSubclassesMethodsNewToOld.containsKey(superclassName)) {
				for (MethodNode newMethod : inSubclassesMethodsNewToOld.get(superclassName)) {
					if (tryAddSubclassMethod(newMethod, originalClass.methods)) {
						ModUpdaterPlugin
								.debug(String.format("Added method %s to class %s", newMethod.name, transformedName));
					}
				}
			}
		}

		List<String> adapterClasses = ClassAdapters.getAdapterClasses(transformedName);

		for (String adapter : adapterClasses) {
			ClassNode adapterClass = ClassAdapters.loadAdapterClass(adapter);
			if (!internalName.equals(adapterClass.superName)) {
				ModUpdaterPlugin.LOGGER.warn(
						"Skipping adapter class " + adapter + " as its superclass does not match what it's adapting");
				continue;
			}

			// Copy fields
			for (FieldNode newField : adapterClass.fields) {
				boolean fieldExists = false;
				for (FieldNode existingField : originalClass.fields) {
					if (newField.name.equals(existingField.name)) {
						fieldExists = true;
						break;
					}
				}
				if (fieldExists) {
					ModUpdaterPlugin.LOGGER.warn("Skipping adapter field " + newField.name + " as it already exists");
				} else {
					originalClass.fields.add(newField);
				}
			}

			searchForMethodPairs(originalClass.name, adapterClass.methods);

			// Copy methods and register optional overrides
			for (MethodNode newMethod : adapterClass.methods) {
				boolean isAdapterMethod = false;
				if (newMethod.visibleAnnotations != null) {
					Iterator<AnnotationNode> annotationI = newMethod.visibleAnnotations.iterator();
					while (annotationI.hasNext()) {
						AnnotationNode annotation = annotationI.next();
						if (annotation.desc.equals(Type.getDescriptor(AdapterMethod.class))) {
							isAdapterMethod = true;
							annotationI.remove();
						} else if (annotation.desc.equals(Type.getDescriptor(InSubclasses.class))) {
							EnumPriority priority = EnumPriority.OLD_TO_NEW;
							for (int i = 0; i < annotation.values.size(); i += 2) {
								if ("priority".equals(annotation.values.get(i))) {
									String[] enumValue = (String[]) annotation.values.get(i + 1);
									if (!enumValue[0].equals(Type.getDescriptor(EnumPriority.class))) {
										throw new RuntimeException("@InSubclasses.priority wrong type");
									}
									priority = EnumPriority.valueOf(enumValue[1]);
								}
							}
							if (priority == EnumPriority.OLD_TO_NEW) {
								if (!inSubclassesMethodsOldToNew.containsKey(internalName)) {
									inSubclassesMethodsOldToNew.put(internalName, Lists.<MethodNode> newArrayList());
								}
								inSubclassesMethodsOldToNew.get(internalName).add(newMethod);
							} else {
								if (!inSubclassesMethodsNewToOld.containsKey(internalName)) {
									inSubclassesMethodsNewToOld.put(internalName, Lists.<MethodNode> newArrayList());
								}
								inSubclassesMethodsNewToOld.get(internalName).add(0, newMethod);
							}
						}
					}
				}
				if (isAdapterMethod) {
					boolean methodExists = false;
					for (MethodNode existingMethod : originalClass.methods) {
						if (newMethod.name.equals(existingMethod.name) && newMethod.desc.equals(existingMethod.desc)) {
							methodExists = true;
							break;
						}
					}
					if (methodExists) {
						ModUpdaterPlugin.LOGGER
								.warn("Skipping adapter method " + newMethod.name + " as it already exists");
					} else {
						originalClass.methods.add(newMethod);
						ModUpdaterPlugin
								.debug(String.format("Added method %s to class %s", newMethod.name, transformedName));
					}
				}
			}
		}

		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		originalClass.accept(writer);
		return writer.toByteArray();
	}

	private static boolean tryAddSubclassMethod(MethodNode newMethod, List<MethodNode> originalMethods) {
		AnnotationNode inSubclassesAnnotation = null;
		for (AnnotationNode annotation : newMethod.visibleAnnotations) {
			if (annotation.desc.equals(Type.getDescriptor(InSubclasses.class))) {
				inSubclassesAnnotation = annotation;
				break;
			}
		}
		String[] requirements = null;
		String[] conflicts = null;
		for (int i = 0; i < inSubclassesAnnotation.values.size(); i += 2) {
			String key = (String) inSubclassesAnnotation.values.get(i);
			Object value = inSubclassesAnnotation.values.get(i + 1);
			if (key.equals("requirements")) {
				List<?> listValue = (List<?>) value;
				requirements = new String[listValue.size()];
				for (int j = 0; j < requirements.length; j++) {
					requirements[j] = (String) listValue.get(j);
				}
			} else if (key.equals("conflicts")) {
				List<?> listValue = (List<?>) value;
				conflicts = new String[listValue.size()];
				for (int j = 0; j < conflicts.length; j++) {
					conflicts[j] = (String) listValue.get(j);
				}
			}
		}
		if (shouldAddSubclassMethod(newMethod.name, newMethod.desc, originalMethods, requirements, conflicts)) {
			originalMethods.add(newMethod);
			return true;
		}
		return false;
	}

	private static boolean shouldAddSubclassMethod(String methodName, String methodDesc,
			List<MethodNode> currentMethods, String[] requirements, String[] conflicts) {
		if (requirements == null)
			requirements = new String[0];
		if (conflicts == null)
			conflicts = new String[0];

		String[] conflictNames = new String[conflicts.length + 1];
		String[] conflictDescs = new String[conflicts.length + 1];
		for (int i = 0; i < conflicts.length; i++) {
			int bracketIndex = conflicts[i].indexOf('(');
			conflictNames[i] = conflicts[i].substring(0, bracketIndex);
			conflictDescs[i] = conflicts[i].substring(bracketIndex);
		}
		conflictNames[conflicts.length] = methodName;
		conflictDescs[conflicts.length] = methodDesc;

		for (MethodNode currentMethod : currentMethods) {
			for (int i = 0; i < conflictNames.length; i++) {
				if (currentMethod.name.equals(conflictNames[i]) && currentMethod.desc.equals(conflictDescs[i]))
					return false;
			}
		}

		String[] requirementNames = new String[requirements.length];
		String[] requirementDescs = new String[requirements.length];
		for (int i = 0; i < requirements.length; i++) {
			int bracketIndex = requirements[i].indexOf('(');
			requirementNames[i] = requirements[i].substring(0, bracketIndex);
			requirementDescs[i] = requirements[i].substring(bracketIndex);
		}

		int requirementsNeeded = requirements.length;

		for (MethodNode currentMethod : currentMethods) {
			for (int i = 0; i < requirements.length; i++) {
				if (currentMethod.name.equals(requirementNames[i]) && currentMethod.desc.equals(requirementDescs[i])) {
					requirementsNeeded--;
				}
			}
		}

		if (requirementsNeeded != 0)
			return false;

		return true;
	}

	private static void searchForMethodPairs(String ownerClassName, List<MethodNode> methods) {
		Map<String, EnumMap<MethodPair.EnumType, MethodNode>> methodPairs = Maps.newHashMap();
		Set<String> incompletePairs = Sets.newHashSet();
		for (MethodNode method : methods) {
			if (method.visibleAnnotations != null) {
				for (AnnotationNode annotation : method.visibleAnnotations) {
					if (annotation.desc.equals(Type.getDescriptor(MethodPair.class))) {
						String name = null;
						MethodPair.EnumType type = null;
						for (int i = 0; i < annotation.values.size(); i += 2) {
							Object key = annotation.values.get(i);
							if (key.equals("name")) {
								name = (String) annotation.values.get(i + 1);
							} else if (key.equals("type")) {
								String[] enumValue = (String[]) annotation.values.get(i + 1);
								if (!enumValue[0].equals(Type.getDescriptor(MethodPair.EnumType.class))) {
									new RuntimeException("@MethodPair.type is wrong type. Very funny.");
								}
								type = MethodPair.EnumType.valueOf(enumValue[1]);
							}
						}
						if (name == null || type == null) {
							throw new RuntimeException(
									"Encountered a @MethodPair annotation without required properties");
						}
						if (!methodPairs.containsKey(name)) {
							EnumMap<MethodPair.EnumType, MethodNode> enumMap = Maps
									.newEnumMap(MethodPair.EnumType.class);
							enumMap.put(type, method);
							methodPairs.put(name, enumMap);
							incompletePairs.add(name);
						} else {
							EnumMap<MethodPair.EnumType, MethodNode> enumMap = methodPairs.get(name);
							if (enumMap.containsKey(type)) {
								throw new RuntimeException(String.format(
										"Encountered two identical of @MethodPair(name = \"%s\", type = %s)", name,
										type.name()));
							}
							enumMap.put(type, method);
							incompletePairs.remove(name);
						}
					}
				}
			}
		}
		if (!incompletePairs.isEmpty()) {
			throw new RuntimeException("Found " + incompletePairs.size() + " incomplete pairs: " + incompletePairs);
		}

		for (EnumMap<MethodPair.EnumType, MethodNode> methodPair : methodPairs.values()) {
			MethodNode method1 = methodPair.get(MethodPair.EnumType.OLDER);
			MethodNode method2 = methodPair.get(MethodPair.EnumType.NEWER);
			linkPairedMethod(ownerClassName, method1, method2, true);
			linkPairedMethod(ownerClassName, method2, method1, false);
		}
	}

	private static void linkPairedMethod(String ownerClassName, MethodNode methodToPair, MethodNode pairedMethod,
			boolean older) {
		for (AbstractInsnNode insn : methodToPair.instructions.toArray()) {
			if (insn.getType() == AbstractInsnNode.METHOD_INSN) {
				MethodInsnNode methodInsn = (MethodInsnNode) insn;
				if (methodInsn.owner.equals(ownerClassName) && methodInsn.name.equals(pairedMethod.name)
						&& methodInsn.desc.equals(pairedMethod.desc)) {
					// complexInvokespecialAlternative(methodToPair.instructions,
					// methodInsn, methodToPair.maxLocals);
					methodInsn.setOpcode(Opcodes.INVOKESPECIAL);
				}
			}
		}
		if (older)
			methodToPair.visibleAnnotations.add(new AnnotationNode(Type.getDescriptor(AdapterMethod.class)));
		AnnotationNode inSubclasses = new AnnotationNode(Type.getDescriptor(InSubclasses.class));
		inSubclasses.visit("requirements", Lists.<String> newArrayList(pairedMethod.name + pairedMethod.desc));
		inSubclasses.visitEnum("priority", Type.getDescriptor(EnumPriority.class),
				older ? EnumPriority.NEW_TO_OLD.name() : EnumPriority.OLD_TO_NEW.name());
		methodToPair.visibleAnnotations.add(inSubclasses);
	}

	@SuppressWarnings("unused")
	private static void complexInvokespecialAlternative(InsnList instructions, MethodInsnNode methodInsn,
			int maxLocals) {
		Type returnType = Type.getReturnType(methodInsn.desc);
		Type[] paramTypes = Type.getArgumentTypes(methodInsn.desc);
		InsnList inserting = new InsnList();
		inserting.add(loadInt(paramTypes.length));
		inserting.add(new TypeInsnNode(Opcodes.ANEWARRAY, Type.getInternalName(Object.class)));
		int arrayLocalvar = maxLocals;
		inserting.add(new VarInsnNode(Opcodes.ASTORE, arrayLocalvar));
		for (int i = paramTypes.length - 1; i >= 0; i--) {
			Class<?>[] primitiveClasses = getPrimitiveClasses(paramTypes[i]);
			Class<?> objectType = primitiveClasses[0];
			Class<?> primitiveType = primitiveClasses[1];
			if (objectType != null) {
				inserting.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(objectType), "valueOf",
						Type.getMethodDescriptor(Type.getType(objectType), Type.getType(primitiveType)), false));
			}
			inserting.add(new VarInsnNode(Opcodes.ALOAD, arrayLocalvar));
			inserting.add(new InsnNode(Opcodes.SWAP));
			inserting.add(loadInt(i));
			inserting.add(new InsnNode(Opcodes.SWAP));
			inserting.add(new InsnNode(Opcodes.AASTORE));
		}
		inserting.add(new LdcInsnNode(methodInsn.name));
		inserting.add(new LdcInsnNode(returnType));
		inserting.add(loadInt(paramTypes.length));
		inserting.add(new TypeInsnNode(Opcodes.ANEWARRAY, Type.getInternalName(Class.class)));
		for (int i = 0; i < paramTypes.length; i++) {
			inserting.add(new InsnNode(Opcodes.DUP));
			inserting.add(loadInt(i));
			inserting.add(new LdcInsnNode(paramTypes[i]));
		}
		inserting.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(ClassAdapters.class),
				"invokeDirectInThis",
				Type.getMethodDescriptor(Type.getType(Object.class), Type.getType(Object.class),
						Type.getType(Object[].class), Type.getType(String.class), Type.getType(Class.class),
						Type.getType(Class[].class)),
				false));
		Class<?>[] primitiveClasses = getPrimitiveClasses(returnType);
		Class<?> objectType = primitiveClasses[0];
		Class<?> primitiveType = primitiveClasses[1];
		if (objectType == null) {
			if (returnType.getSort() != Type.VOID)
				inserting.add(new TypeInsnNode(Opcodes.CHECKCAST, returnType.getInternalName()));
		} else {
			inserting.add(new TypeInsnNode(Opcodes.CHECKCAST, Type.getInternalName(objectType)));
			inserting.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, Type.getInternalName(objectType),
					Type.getType(primitiveType).getClassName() + "Value",
					Type.getMethodDescriptor(Type.getType(primitiveType)), false));
		}
		instructions.insertBefore(methodInsn, inserting);
		instructions.remove(methodInsn);
	}

	private static Class<?>[] getPrimitiveClasses(Type type) {
		Class<?> objectType = null;
		Class<?> primitiveType = null;
		switch (type.getSort()) {
		case Type.BOOLEAN:
			objectType = Boolean.class;
			primitiveType = boolean.class;
			break;
		case Type.BYTE:
			objectType = Byte.class;
			primitiveType = byte.class;
			break;
		case Type.CHAR:
			objectType = Character.class;
			primitiveType = char.class;
			break;
		case Type.DOUBLE:
			objectType = Double.class;
			primitiveType = double.class;
			break;
		case Type.FLOAT:
			objectType = Float.class;
			primitiveType = float.class;
			break;
		case Type.INT:
			objectType = Integer.class;
			primitiveType = int.class;
			break;
		case Type.LONG:
			objectType = Long.class;
			primitiveType = long.class;
			break;
		case Type.SHORT:
			objectType = Short.class;
			primitiveType = short.class;
			break;
		}
		return new Class<?>[] { objectType, primitiveType };
	}

	private static AbstractInsnNode loadInt(int val) {
		return val <= 5 && val >= -1 ? new InsnNode(Opcodes.ICONST_0 + val)
				: (val < 256 && val >= -128 ? new IntInsnNode(val < 128 ? Opcodes.BIPUSH : Opcodes.SIPUSH, val)
						: new LdcInsnNode(val));
	}

}
