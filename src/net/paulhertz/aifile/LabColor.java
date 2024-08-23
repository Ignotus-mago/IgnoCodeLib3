package net.paulhertz.aifile;

/**
 * 
 * @author paulhz
 * 
 * A class for implementing the Lab colorspace.
 * This is meant to work with a ColorConverter class that will handle RGB - CMYK - HSB - Lab color spaces
 * and conversions from the RGB and HSB formats typical in Processing. 
 * 
 * My conversion methods were AIded: https://chat.openai.com/share/44d99906-82dd-40a7-8d9f-2f9167b74b1c.
 *
 */
public class LabColor {
	/** the lightness component of the Lab color (0-100) */
	protected double l; 
	/** the green-red component of the Lab color (-128 to 127) */
	protected double a; 
	/** the blue-yellow component of the Lab color (-128 to 127) */
	protected double b;
	
	
	
	/**
     * @param L the lightness component of the Lab color (0-100)
     * @param a the green-red component of the Lab color (-128 to 127)
     * @param b the blue-yellow component of the Lab color (-128 to 127)
	 */
	public LabColor(double L, double a, double b) {
		this.l = L;
		this.a = a;
		this.b = b;
	}

	
    /**
     * Converts a Lab color value to RGB color space.
     *
     * @param l the lightness component of the Lab color (0-100)
     * @param a the green-red component of the Lab color (-128 to 127)
     * @param b the blue-yellow component of the Lab color (-128 to 127)
     * @return an array containing the RGB values: [red, green, blue]
     */
    public static int[] convertLabtoRGB(double l, double a, double b) {
        double fy = (l + 16.0) / 116.0;
        double fx = fy + (a / 500.0);
        double fz = fy - (b / 200.0);

        double xn = Math.pow(fx, 3.0);
        double yn = Math.pow(fy, 3.0);
        double zn = Math.pow(fz, 3.0);

        double x = xn * 0.950456;
        double y = yn;
        double z = zn * 1.088754;

        // Convert XYZ to RGB
        double R = 3.2404542 * x - 1.5371385 * y - 0.4985314 * z;
        double G = -0.969266 * x + 1.8760108 * y + 0.041556 * z;
        double B = 0.0556434 * x - 0.2040259 * y + 1.0572252 * z;

        // Clamp RGB values to 0-255
        int red = clamp((int) (R * 255.0), 0, 255);
        int green = clamp((int) (G * 255.0), 0, 255);
        int blue = clamp((int) (B * 255.0), 0, 255);

        return new int[]{red, green, blue};
    }	
	
    /**
     * Clamps a value within a specified range.
     *
     * @param value the value to clamp
     * @param min   the minimum value of the range
     * @param max   the maximum value of the range
     * @return the clamped value
     */
    private static int clamp(int value, int min, int max) {
        return Math.min(Math.max(value, min), max);
    }
    
    /**
     * Converts an RGB color value to Lab color space.
     *
     * @param red   the red component of the RGB color (0-255)
     * @param green the green component of the RGB color (0-255)
     * @param blue  the blue component of the RGB color (0-255)
     * @return an array containing the Lab values: [L, a, b]
     */
    public static double[] convertRGBtoLab(int red, int green, int blue) {
        double r = red / 255.0;
        double g = green / 255.0;
        double b = blue / 255.0;

        // Convert RGB to XYZ
        double x = 0.4124564 * r + 0.3575761 * g + 0.1804375 * b;
        double y = 0.2126729 * r + 0.7151522 * g + 0.072175 * b;
        double z = 0.0193339 * r + 0.119192 * g + 0.9503041 * b;

        // Normalize XYZ values
        double xn = x / 0.950456;
        double zn = z / 1.088754;

        // Calculate Lab values
        double fx = f(xn);
        double fy = f(y);
        double fz = f(zn);

        double L = 116.0 * fy - 16.0;
        double A = 500.0 * (fx - fy);
        double B = 200.0 * (fy - fz);

        return new double[]{L, A, B};
    }

    /**
     * Computes the f(t) function used in the Lab color conversion.
     *
     * @param t the input value
     * @return the computed result
     */
    private static double f(double t) {
        double threshold = 0.008856;
        if (t > threshold) {
            return Math.cbrt(t);
        } else {
            return (903.3 * t + 16.0) / 116.0;
        }
    }
    
	
}
