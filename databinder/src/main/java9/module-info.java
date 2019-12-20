module com.github.skjolber.stcsv.databinder {

	exports com.github.skjolber.stcsv.databinder;
	exports com.github.skjolber.stcsv.databinder.builder;
	exports com.github.skjolber.stcsv.databinder.column.bi;
	exports com.github.skjolber.stcsv.databinder.column.tri;
	exports com.github.skjolber.stcsv.databinder.projection;
	
	requires net.bytebuddy;
	requires org.objectweb.asm;
	requires com.github.skjolber.stcsv;

}