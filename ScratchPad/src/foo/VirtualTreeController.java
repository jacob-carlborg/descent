package foo;



public class VirtualTreeController {

	public static Object[] model1 = new Object[] { 
		"Leaf with some more text",
			createLeafParent(),
			createBigTree(100, 1),
			createLeafParent(),
			new Object[] { "xxxx abcd12346789", "xxxx abcd12346789",},
	};


	private static Object[] createLeafParent() {
		return new Object[] { "xxxx abcd12346789", "xxxx abcd12346789", "xxxx abcd12346789", "xxxx abcd12346789",
				"xxxx abcd12346789", "xxxx abcd12346789", "xxxx abcd12346789", "xxxx abcd12346789",
				"xxxx abcd12346789", "xxxx abcd12346789", "xxxx abcd12346789", "xxxx abcd12346789",};
	}
	
	private static Object createBigTree(int numChildren, int level) {
		Object[] element = new Object[numChildren];
		if(level == 0) {
			for (int i = 0; i < element.length; i++) {
				element[i] = "["+level+"] Child " + i;
			}
		} else {
			for (int i = 0; i < element.length; i++) {
				element[i] = createBigTree(numChildren, level-1);
			}
		}
		return element;
	}
	
	public static Object[] model2 = new Object[] { 
		new Object[] { 
				createLeafParent(),
				createLeafParent(),
				"xxxx abcd12346789", "xxxx abcd12346789", "xxxx abcd12346789", "xxxx abcd12346789",
			},
			"Alternate Leaf",

	};
	public static Object[] activeModel = null;
	
	
	public static Object[] toggleModel() {
		if (activeModel == model1) {
			activeModel = model2;
		} else if (activeModel == model2) {
			activeModel = model1;
		}
		return activeModel;
	}


}
