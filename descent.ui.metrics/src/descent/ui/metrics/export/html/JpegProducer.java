package descent.ui.metrics.export.html;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;

import descent.ui.metrics.MetricsPlugin;
import descent.ui.metrics.collator.MetricsCollator;
import com.sun.image.codec.jpeg.ImageFormatException;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

final class JpegProducer {
    private static final int IMAGE_WIDTH = 800;
    private static final int IMAGE_HEIGHT = 500;

    public void produce(File file, MetricsCollator collator, String metricId) throws IOException {
        BufferedImage image = new BufferedImage(JpegProducer.IMAGE_WIDTH, JpegProducer.IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
        paintImage((Graphics2D) image.getGraphics(), collator, metricId);
        saveImage(file, image);
        image.getGraphics().dispose();
    }

    private void saveImage(File file, BufferedImage image) throws ImageFormatException, IOException {
        if (file.exists()) {
            file.delete();
        }
        OutputStream out = new FileOutputStream(file);
        JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
        JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(image);
        param.setQuality(1, true);
        encoder.encode(image, param);
        out.close();
    }

    private void paintImage(Graphics2D graphics, MetricsCollator collator, String metricId) {
        JFreeChart chart = ChartFactory.createStackedVerticalBarChart(MetricsPlugin.getMetricPresentationName(metricId), MetricsPlugin.getMetricPresentationName(metricId), "Occurrences", new MetricsDataset(collator, metricId), true, false, false);
        chart.draw(graphics, new Rectangle2D.Double(0, 0, JpegProducer.IMAGE_WIDTH, JpegProducer.IMAGE_HEIGHT));
    }
}
