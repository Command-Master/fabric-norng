package org.commandmaster.norng;

import net.devtech.grossfabrichacks.instrumentation.InstrumentationApi;
import net.devtech.grossfabrichacks.unsafe.UnsafeUtil;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.launch.common.FabricLauncherBase;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.MethodNode;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class ExampleMod implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.
        try {
            Class<?> unsafe = Class.forName("sun.misc.Unsafe");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            byte[] source = FabricLauncherBase.getLauncher().getClassByteArray("org/commandmaster/norng/Endpoint", false);
            System.out.println(Arrays.toString(source));
            UnsafeUtil.defineClass("org/commandmaster/norng/Endpoint", source, Random.class.getClassLoader());
        } catch (IOException e) {
            e.printStackTrace();
        }
        InstrumentationApi.retransform(Random.class, (s, b) -> {
            for (MethodNode node : b.methods) {
                if (node.name.equals("<init>") && node.desc.equals("()V")) {
                    node.instructions.clear();
                    node.visitVarInsn(ALOAD, 0);
                    node.visitMethodInsn(INVOKESPECIAL, Type.getInternalName(Object.class), "<init>", "()V", false);
                    node.visitVarInsn(ALOAD, 0);
                    try {
                        node.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Endpoint.class), "random",
                                Type.getMethodDescriptor(Endpoint.class.getMethod("random", Random.class)),
                                false);
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                    node.visitInsn(RETURN);
                }
            }
        });
        System.out.println(Type.getInternalName(ExampleMod.class));
        System.out.println("Hello Fabric world!");
    }
}
