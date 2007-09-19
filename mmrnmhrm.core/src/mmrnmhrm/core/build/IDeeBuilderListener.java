package mmrnmhrm.core.build;

public interface IDeeBuilderListener {
	public static class NullDeeBuilderListener implements IDeeBuilderListener {

		//@Override
		public void println(String line) {
			// Do nothing
		}

		//@Override
		public void clear() {
			// Do nothing
		}
		
	}

	void clear();

	void println(String line);

}
