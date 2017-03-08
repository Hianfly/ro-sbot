package com.hiandev.rosbot.scanner;

public class Pixel {
	
	public static final int[] clonePixel(int[] pixel) {
		return new int[] { pixel[0], pixel[1], pixel[2] };
	}
	public static final boolean isMatch(int[] pixels, int offset, int r, int g, int b) {
		return (r == -1 || pixels[offset + 0] == r) && 
			   (g == -1 || pixels[offset + 1] == g) && 
			   (b == -1 || pixels[offset + 2] == b);
	}
	public static final boolean isMatch(int[] pixels, int offset, int[] pixel) {
		return (pixel[0] == -1 || pixels[offset + 0] == pixel[0]) && 
			   (pixel[1] == -1 || pixels[offset + 1] == pixel[1]) && 
			   (pixel[2] == -1 || pixels[offset + 2] == pixel[2]);
	}
	public static final int findIndex(int[] pixels, int offset, int[][] pixelPool) {
		int index = -1;
		for (int x = 0; x < pixelPool.length; x++) {
			if (isMatch(pixels, offset, pixelPool[x])) {
				index = x;
				break;
			}
		}
		return index;
	}
	public static final boolean isBelow(int[] pixels, int offset, int threshold) {
		return (pixels[offset + 0] < threshold) && 
			   (pixels[offset + 1] < threshold) && 
			   (pixels[offset + 2] < threshold);
	}
	public static final boolean isAbove(int[] pixels, int offset, int threshold) {
		return (pixels[offset + 0] > threshold) && 
			   (pixels[offset + 1] > threshold) && 
			   (pixels[offset + 2] > threshold);
	}
	public static final int[] getPixel(int[] pixels, int offset) {
		return new int[] { pixels[offset + 0], pixels[offset + 1], pixels[offset + 2] };
	}
	public static final void setPixel(int[] pixels, int offset, int r, int g, int b) {
		pixels[offset + 0] = r;
		pixels[offset + 1] = g;
		pixels[offset + 2] = b;
	}
	public static final void setPixel(int[] pixels, int offset, int[] pixel) {
		pixels[offset + 0] = pixel[0];
		pixels[offset + 1] = pixel[1];
		pixels[offset + 2] = pixel[2];
	}
	public static final int[] floorPixels(int[] samples, int threshold) {
		for (int x = 0; x < samples.length; x++) {
			samples[x] = (samples[x] / threshold) * threshold;
		}
		return samples;
	}
	public static final int[] toPixels(int[][] pixels2D) {
		int[] pixels = new int[pixels2D.length * pixels2D[0].length];
		int   index = 0;
		for (int y = 0; y < pixels2D.length; y++) {
			for (int x = 0; x < pixels2D[y].length; x++) {
				pixels[index++] = pixels2D[y][x];
			}
		}
		return pixels;
	}
	public static final long createPixelKey(int[] pixel) {
    	return ((pixel[0] * 1000000l) +  1000000000l) + 
			   ((pixel[1] *    1000l) +     1000000l) +
			   ((pixel[2] *       1l) +        1000l);
    }
	public static final int[] floorBlueOnePixels(int[] pixels) {
		for (int i = 0; i < pixels.length; i += 3) {
			if (pixels[i + 2] == 1) {
				pixels[i + 2]  = 5;
			}
		}
		return pixels;
	}
	public static final int[] getAveragePixels(int[][] pixels, int prmFloorThreshold, boolean skipWhite, boolean changeSourcePixels) {
		int r = 0;
		int g = 0;
		int b = 0;
		int d = 0;
		for (int i = 0; i < pixels.length; i += 1) {
			for (int j = 0; j < pixels[i].length; j += 3) {
				if (skipWhite && isMatch(pixels[i], j, 255, 255, 255)) {
					continue;
				}
				r += pixels[i][j + 0];
				g += pixels[i][j + 1];
				b += pixels[i][j + 2];
				d += 1;
			}
		}
		if (d == 0) {
			r = 255;
			g = 255;
			b = 255;
			d = 1;
		}
		r = ((r / d) / prmFloorThreshold) * prmFloorThreshold;
		g = ((g / d) / prmFloorThreshold) * prmFloorThreshold;
		b = ((b / d) / prmFloorThreshold) * prmFloorThreshold;
		if (changeSourcePixels) {
    		for (int i = 0; i < pixels.length; i += 1) {
    			for (int j = 0; j < pixels[i].length; j += 3) {
    				pixels[i][j + 0] = r;
    				pixels[i][j + 1] = g;
    				pixels[i][j + 2] = b;
    			}
    		}
		}
		return new int[] { r, g, b };
    }
	
	
	
}
