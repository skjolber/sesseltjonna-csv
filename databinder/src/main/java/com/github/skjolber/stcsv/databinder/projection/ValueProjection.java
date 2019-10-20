package com.github.skjolber.stcsv.databinder.projection;

import org.objectweb.asm.MethodVisitor;

public interface ValueProjection {

	void write(MethodVisitor mv, String subClassInternalName, int endIndex);

}
