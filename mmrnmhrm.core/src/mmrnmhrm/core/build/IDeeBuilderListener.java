package mmrnmhrm.core.build;

public interface IDeeBuilderListener {
	public static class NullDeeBuilderListener implements IDeeBuilderListener {

		@Override
		public void println(String line) {
			// Do nothing
		}
		
	}

	void println(String line);
}
