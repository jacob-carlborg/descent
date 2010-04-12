package testproduct.core;

import melnorme.miscutil.StringUtil;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

public class Path_Snippet {
	
	public static void main(String[] args) {
		
		Path path1 = new Path("foo");
		printPath(path1);
		Path path2 = new Path("/foo");
		printPath(path2);
		Path path3 = new Path("/foo/");
		printPath(path3);
		
		Path path4 = new Path("/foo/bar");
		printPath(path4.removeLastSegments(1));
		System.out.println("-------------------");
		System.out.println(path1.equals(path2));
		
		part2();
	}

	private static void part2() {
		Path path = new Path("/d/foo");
		System.out.println(path.equals(new Path("/d/foo")));
		System.out.println(path.equals(new Path("/d/foo/")));
		System.out.println(path.equals(new Path("/d/foo/").makeAbsolute()));
		System.out.println(path.equals(new Path("/d///foo//")));
	}

	private static void printPath(IPath path) {
		System.out.println("-------------------");
		System.out.println(path);
		System.out.println("Device: " + path.getDevice());
		System.out.println("Segments: " + StringUtil.collToString(path.segments(), ","));
		System.out.println("absolute: " + path.isAbsolute());
		System.out.println("absolute: " + path.hasTrailingSeparator());
	}
	
}
