package edu.sfls.Jeff.JavaDev.CVLib;

import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.List;

public class SmartCanny {

    private List<Integer[]> highPoints = new ArrayList<Integer[]>();

    private void DoubleThreshold(Mat image, double lowThreshold, double highThreshold) {
        for (int i = 1; i < image.rows() - 1; i ++)
            for (int j = 1; j < image.cols() - 1; j ++)
                if (image.get(i, j)[0] >= highThreshold) {
                    image.put(i, j, 255);
                    Integer[] p = new Integer[2];
                    p[0] = i; p[1] = j;
                    highPoints.add(p);
                } else if (image.get(i, j)[0] < lowThreshold)
                    image.put(i, j, 0);
    }

    private void DoubleThresholdLink(Mat image, double lowThreshold) {
        for (Integer[] p : highPoints) {
            DoubleThresholdLinkRecurrent(image, lowThreshold, p[0], p[1]);
        }
        for (int i = 1; i < image.rows() - 1; i ++)
            for (int j = 1; j < image.cols() - 1; j ++)
                if (image.get(i, j)[0] < 255)
                    image.put(i, j, 0);
    }

    private void DoubleThresholdLinkRecurrent(Mat image, double lowThreshold, int i, int j) {
        if (i <= 0 || j <= 0 || i >= image.rows() - 1 || j >= image.cols() - 1) return;
        if (image.get(i - 1, j - 1)[0] >= lowThreshold && image.get(i - 1, j - 1)[0] < 255) {
            image.put(i - 1, j - 1, 255);
            DoubleThresholdLinkRecurrent(image, lowThreshold, i - 1, j - 1);
        }
        if (image.get(i - 1, j)[0] >= lowThreshold && image.get(i - 1, j)[0] < 255) {
            image.put(i - 1, j, 255);
            DoubleThresholdLinkRecurrent(image, lowThreshold, i - 1, j);
        }
        if (image.get(i - 1, j + 1)[0] >= lowThreshold && image.get(i - 1, j + 1)[0] < 255) {
            image.put(i - 1, j + 1, 255);
            DoubleThresholdLinkRecurrent(image, lowThreshold, i - 1, j + 1);
        }
        if (image.get(i, j - 1)[0] >= lowThreshold && image.get(i, j - 1)[0] < 255) {
            image.put(i, j - 1, 255);
            DoubleThresholdLinkRecurrent(image, lowThreshold, i, j - 1);
        }
        if (image.get(i, j + 1)[0] >= lowThreshold && image.get(i, j + 1)[0] < 255) {
            image.put(i, j + 1, 255);
            DoubleThresholdLinkRecurrent(image, lowThreshold, i, j + 1);
        }
        if (image.get(i + 1, j - 1)[0] >= lowThreshold && image.get(i + 1, j - 1)[0] < 255) {
            image.put(i + 1, j - 1, 255);
            DoubleThresholdLinkRecurrent(image, lowThreshold, i + 1, j - 1);
        }
        if (image.get(i + 1, j)[0] >= lowThreshold && image.get(i + 1, j)[0] < 255) {
            image.put(i + 1, j, 255);
            DoubleThresholdLinkRecurrent(image, lowThreshold, i + 1, j);
        }
        if (image.get(i + 1, j + 1)[0] >= lowThreshold && image.get(i + 1, j + 1)[0] < 255) {
            image.put(i + 1, j + 1, 255);
            DoubleThresholdLinkRecurrent(image, lowThreshold, i + 1, j + 1);
        }
    }

    public Mat Canny(Mat image, int size, double sigma, double lowThreshold, double highThreshold) throws InterruptedException {
        Mat tmp = SmartConverter.RGB2Gray((SmartGaussian.colorGaussianFilter(image, size, sigma)));
        SmartSobel ss = new SmartSobel();
        ss.compute(tmp);
        ss.convert();
        Mat ret = SmartNMS.NMS(ss.getGradientXY(), ss.getPointDirection());
        this.DoubleThreshold(ret, lowThreshold, highThreshold);
        this.DoubleThresholdLink(ret, lowThreshold);
        return ret;
    }

}
