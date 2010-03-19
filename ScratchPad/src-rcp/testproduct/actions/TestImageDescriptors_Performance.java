package testproduct.actions;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import scratch.swt.SWTApp;
import scratch.utils.ImageRegistryCache;
import testproduct.TestProductPlugin;

public class TestImageDescriptors_Performance {

	private static final String ICON_SAMPLE_CLONES = "icons/samples/";
	
	public static class TestProductUI {
		
		public static final ImageRegistryCache imageRegistryCache = new ImageRegistryCache();

		public static synchronized ImageDescriptor getImageDescriptor(String imagePath) {
			return imageRegistryCache.getImageDescriptor(TestProductPlugin.PLUGIN_ID, imagePath);
		}

	}
	

	
	protected static SWTApp swtApp = new SWTApp();
	@BeforeClass
	public static void setupShell() {
		swtApp.createShell();
	}
	@AfterClass
	public static void disposeShell() {
		swtApp.disposeShell();
	}
	
	
	@Test
	public void doTest() {
		Label label = new Label(swtApp.shell, SWT.NONE);
		label.setText("label");
		
		TestProductUI.getImageDescriptor(ICON_SAMPLE_CLONES + "sample00.gif");
		TestProductUI.getImageDescriptor(ICON_SAMPLE_CLONES + "sample01.gif");
		TestProductUI.getImageDescriptor(ICON_SAMPLE_CLONES + "sample02.gif");
		TestProductUI.getImageDescriptor(ICON_SAMPLE_CLONES + "sample03.gif");
		TestProductUI.getImageDescriptor(ICON_SAMPLE_CLONES + "sample04.gif");
		TestProductUI.getImageDescriptor(ICON_SAMPLE_CLONES + "sample05.gif");
		TestProductUI.getImageDescriptor(ICON_SAMPLE_CLONES + "sample06.gif");
		TestProductUI.getImageDescriptor(ICON_SAMPLE_CLONES + "sample07.gif");
		TestProductUI.getImageDescriptor(ICON_SAMPLE_CLONES + "sample08.gif");
		TestProductUI.getImageDescriptor(ICON_SAMPLE_CLONES + "sample09.gif");
		
		while(true) {
			long counter = 0;
			long time = System.currentTimeMillis();
			while(System.currentTimeMillis() - time < 1000) {
				counter++;
				int a = 2 * 10;
			}
			System.out.println("empty: " + counter);

			counter = 0;
			time = System.currentTimeMillis();
			while(System.currentTimeMillis() - time < 1000) {
				counter++;
				String s = "asfasd" + "asdfa";
			}
			System.out.println("String: " + counter);
			
			counter = 0;
			time = System.currentTimeMillis();
			while(System.currentTimeMillis() - time < 1000) {
				counter++;
				try {
					ImageDescriptor.createFromURL(new URL("file://asdf/foo.bar"));
				} catch (MalformedURLException e) {
					throw melnorme.miscutil.ExceptionAdapter.unchecked(e);
				}
			}
			System.out.println("ImageDescriptor.createFromURL: " + counter);
			
			counter = 0;
			time = System.currentTimeMillis();
			while(System.currentTimeMillis() - time < 1000) {
				counter++;
				
				AbstractUIPlugin.imageDescriptorFromPlugin(TestProductPlugin.PLUGIN_ID, ICON_SAMPLE_CLONES + "sample00.gif");
			}
			System.out.println("imageDescriptorFromPlugin: " + counter);
	
			counter = 0;
			time = System.currentTimeMillis();
			while(System.currentTimeMillis() - time < 1000) {
				counter++;
				TestProductUI.getImageDescriptor(ICON_SAMPLE_CLONES + "sample00.gif");
			}
			System.out.println("cached: " + counter);
			
		}
	}
	

}
