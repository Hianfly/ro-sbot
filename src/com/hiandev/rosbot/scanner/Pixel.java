package com.hiandev.rosbot.scanner;

public class Pixel {
	
	public static final int[] clonePixel(int[] pixel) {
		return new int[] { pixel[0], pixel[1], pixel[2] };
	}
	
	public static final long createPixelKey(int[] pixel) {
    	return ((pixel[0] * 1000000l) +  1000000000l) + 
			   ((pixel[1] *    1000l) +     1000000l) +
			   ((pixel[2] *       1l) +        1000l);
    }
	public static final boolean isMatch(int[] pixels, int offset, int r, int g, int b) {
		return (r == -1 || pixels[offset + 0] == r) && 
			   (g == -1 || pixels[offset + 1] == g) && 
			   (b == -1 || pixels[offset + 2] == b);
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