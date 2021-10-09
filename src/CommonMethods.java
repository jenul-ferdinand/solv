import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;

interface CommonMethods {
	/* Returns a random integer between a specified maximum and minimum */
	default int intRandomRange(int min, int max) { return (int) ((Math.random() * (max - min)) + min); }

	// Get image from filepath
	default BufferedImage getImage(BufferedImage image, String filename) {
		try {
			if (image == null) { image = ImageIO.read(new File("images/" + filename)); }
		} catch (IOException e) {}

		return image;
	}
	// Get font from file path
	default Font getFont(Font font, String filename) {
		try {
			if (font == null) font = Font.createFont(Font.TRUETYPE_FONT, new File("fonts/" + filename));
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("fonts/" + filename)));
		} catch (IOException | FontFormatException e) { }

		return font;
	}

	// Getting the height of a string
	default int getStringHeight(Graphics page, Font f) {
		FontMetrics fm = page.getFontMetrics(f);
		return fm.getAscent();
	}
	// Getting the width of a string
	default int getStringWidth(String string, Graphics g, Font f) {
		Graphics2D g2d = (Graphics2D) g.create();
		FontMetrics fm = g2d.getFontMetrics();
		return fm.stringWidth(string);
	}
	// For drawing multi line strings https://stackoverflow.com/a/19864657/10069286
	default void drawStringMultiLine(Graphics2D g, String text, int lineWidth, int x, int y) {
		FontMetrics m = g.getFontMetrics();
		if(m.stringWidth(text) < lineWidth) {
			g.drawString(text, x, y);
		} else {
			String[] words = text.split(" ");
			String currentLine = words[0];
			for(int i = 1; i < words.length; i++) {
				if(m.stringWidth(currentLine+words[i]) < lineWidth) {
					currentLine += " "+words[i];
				} else {
					g.drawString(currentLine, x, y);
					y += m.getHeight();
					currentLine = words[i];
				}
			}
			if(currentLine.trim().length() > 0) {
				g.drawString(currentLine, x, y);
			}
		}
	}

	default void drawString(Graphics g, String text, int x, int y) {
		int lineHeight = g.getFontMetrics().getHeight();
		for (String line : text.split("\n"))
			g.drawString(line, x, y += lineHeight);
	}

	// Linear interpolation method
	default float lerp(float a, float b, float f) {
		return a + f * (b - a);
	}
	// Incrementally, linear interpolate a value towards a target value.
	default double lerpDisplayed(double value, int target, double lerp) {
		if (value < target) {
			value = value + lerp * (target - value);
		} else {
			value = target;
		}

		return value;
	}

	// Adding a char to a position in a string
	default String addChar(String str, char ch, int position) {
		return str.substring(0, position) + ch + str.substring(position);
	}
	// Convert large numbers to abbreviated version
	default String stringLargeNumber(BigDecimal number) {
		//region ARRAYS
		BigDecimal[] large_numbers = {
				new BigDecimal("1000000"),
				new BigDecimal("1000000000"),
				new BigDecimal("1000000000000"),
				new BigDecimal("1000000000000000"),
				new BigDecimal("1000000000000000000"),
				new BigDecimal("1000000000000000000000"),
				new BigDecimal("1000000000000000000000000"),
				new BigDecimal("1000000000000000000000000000"),
				new BigDecimal("1000000000000000000000000000000"),
				new BigDecimal("1000000000000000000000000000000000"),
				new BigDecimal("1000000000000000000000000000000000000"),
				new BigDecimal("1000000000000000000000000000000000000000"),
				new BigDecimal("1000000000000000000000000000000000000000000"),
				new BigDecimal("1000000000000000000000000000000000000000000000"),
				new BigDecimal("1000000000000000000000000000000000000000000000000"),
				new BigDecimal("1000000000000000000000000000000000000000000000000000"),
				new BigDecimal("1000000000000000000000000000000000000000000000000000000"),
				new BigDecimal("1000000000000000000000000000000000000000000000000000000000"),
				new BigDecimal("1000000000000000000000000000000000000000000000000000000000000"),
				new BigDecimal("1000000000000000000000000000000000000000000000000000000000000000")
		};
		String[] abbreviations = {
				"Million",
				"Billion",
				"Trillion",
				"Quadrillion",
				"Quintillion",
				"Sextillion",
				"Septillion",
				"Octillion",
				"Nonillion",
				"Decillion",
				"Undecillion",
				"Duodecillion",
				"Tredecillion",
				"Quattuordecillion",
				"Quindecillion",
				"Sexdecillion",
				"Septendecillion",
				"Octodecillion",
				"Novemdecillion",
				"Vigintillion",
		}; // endregion

		BigDecimal number_prefix;
		String string_prefix = "";
		String string_suffix = "";

		for (int i = 0; i < large_numbers.length; i++) {
			BigDecimal lower = large_numbers[i].subtract(BigDecimal.ONE);
			BigDecimal upper;

			// Final index buffer
			if (i == large_numbers.length-1) { upper = new BigDecimal(large_numbers[large_numbers.length-1] + "000"); }
			else { upper = large_numbers[i+1]; }

			// Conversion of the number to abbreviated form.
			if (number.compareTo(lower) > 0 && number.compareTo(upper) < 0) {
				number_prefix = number.divide(large_numbers[i]);
				string_prefix = String.format(java.util.Locale.US,"%.3f", number_prefix); // Format to 3 decimal places.
				string_suffix = abbreviations[i];
			}
			// If the number is bigger than the largest listed value, flag it as "Infinity".
			else if (number.compareTo(large_numbers[large_numbers.length-1]) > 0) {
				string_prefix = "Infinity";
				string_suffix = "";
			}
			// If the number is less than one million
			else if (number.compareTo(large_numbers[0]) < 0) {
				// Store number as a string
				string_prefix = number.toString();
				// Adding commas between the numbers
				for (int j = string_prefix.length() - 3; j > 1; j -= 3) {
					string_prefix = addChar(string_prefix, ',', j);
				}

				string_suffix = ""; // No abbreviation
			}
		}

		return string_prefix + " " + string_suffix.toLowerCase();
	}
}