package net.earthcomputer.modupdater.core;

import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.earthcomputer.modupdater.core.transformers.ClassNameReplacerTransformer;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.common.asm.transformers.SideTransformer;

public class ClassAdapters {

	private static final Map<String, List<String>> classAdapters = Maps.newHashMap();

	private static final Map<Triple<Class<?>, String, Class<?>[]>, MethodHandle> cachedMethodHandles = Maps
			.newHashMap();

	private static final IClassTransformer[] transformersOnAdapters = new IClassTransformer[] { new SideTransformer(),
			new ClassNameReplacerTransformer() };

	public static void registerClassAdapter(String className, String adapterClass) {
		if (!classAdapters.containsKey(className)) {
			classAdapters.put(className, Lists.<String> newArrayList());
		}
		classAdapters.get(className).add(adapterClass);
		ClassNameReplacements.replace(adapterClass, className);
	}

	public static List<String> getAdapterClasses(String className) {
		return classAdapters.containsKey(className) ? classAdapters.get(className) : ImmutableList.<String> of();
	}

	public static ClassNode loadAdapterClass(String adapterClass) {
		try {
			byte[] bytes = Launch.classLoader.getClassBytes(adapterClass);
			for (IClassTransformer transformer : transformersOnAdapters) {
				bytes = transformer.transform(adapterClass, adapterClass, bytes);
			}
			ClassReader reader = new ClassReader(bytes);
			ClassNode node = new ClassNode();
			reader.accept(node, 0);
			return node;
		} catch (IOException e) {
			throw new RuntimeException("Unable to load adapter class " + adapterClass, e);
		}
	}

	public static <R> R invokeDirectInThis(Object instance, Object[] args, String name, Class<R> returnType,
			Class<?>[] paramTypes) {
		Class<?> callerClass = instance.getClass();
		Triple<Class<?>, String, Class<?>[]> key = new ImmutableTriple<Class<?>, String, Class<?>[]>(callerClass, name,
				paramTypes);
		MethodHandle handle = cachedMethodHandles.get(key);
		if (handle == null) {
			try {
				handle = MethodHandles.lookup().findSpecial(callerClass, name,
						MethodType.methodType(returnType, paramTypes), callerClass);
			} catch (Exception e) {
				throw Throwables.propagate(e);
			}
			cachedMethodHandles.put(key, handle);
		}
		try {
			return (R) handle.invoke(instance, args);
		} catch (Throwable t) {
			throw Throwables.propagate(t);
		}
	}

}
