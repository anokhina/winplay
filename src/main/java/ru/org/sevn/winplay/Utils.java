/*******************************************************************************
 * Copyright 2017 Veronica Anokhina.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package ru.org.sevn.winplay;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

public class Utils {
    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');
 
        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }
 
    public static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = Utils.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
    
    public static ImageIcon getStretchedImageIcon(ImageIcon ii, int w, int h, boolean incr) {
		if (ii != null) {
			ii = new ImageIcon(ii.getImage().getScaledInstance(w, h, Image.SCALE_DEFAULT));
		}
		return ii;
    }
    public static ImageIcon getScaledImageIcon(ImageIcon ii, int w, int h, boolean incr) {
		if (ii != null) {
			if (ii.getIconWidth() > w || incr) {
				ii = new ImageIcon(ii.getImage().getScaledInstance(w, -1, Image.SCALE_DEFAULT));
			}
			if (ii.getIconHeight() > h) {
				ii = new ImageIcon(ii.getImage().getScaledInstance(-1, h, Image.SCALE_DEFAULT));
			}
		}
		return ii;
    }
    public static Icon getScaledIcon(Icon tmpIcon, int w, int h, boolean incr) {
    	ImageIcon ii = null;
    	if (tmpIcon instanceof ImageIcon) {
    		return getScaledImageIcon((ImageIcon)tmpIcon, w, h, incr);
    	}
		return tmpIcon;
    }
    public static Color getColorByString(String str, Color def) {
		int delim = 255;
		if (str != null && str.length() > 0 ) {
			int hc = str.hashCode();
			if (hc < 0) hc *= -1;
			int b = hc % delim;
			hc = hc / delim;
			int g = hc % delim;
			hc = hc / delim;
			int r = hc % delim;
			return new Color(r, g, b);
		}
		return def;
    }
    public static ImageIcon imageWithBackground(ImageIcon icon, Color colorBg) {
    	if (icon != null && colorBg != null) {
	        BufferedImage bi = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
	        Graphics2D ig2 = bi.createGraphics();
	        ig2.setColor(colorBg);
	        ig2.fillRect(0, 0, icon.getIconWidth(), icon.getIconHeight());
	        ig2.drawImage(icon.getImage(), 0, 0, icon.getIconWidth(), icon.getIconHeight(), null);
	        return new ImageIcon(bi);
    	}
    	return icon;
    }
    public static void drawString(Graphics2D ig2, String message, Font font, Color fg, int width, int height) {
        //Font font = new Font("TimesRoman", Font.BOLD, 20);
        ig2.setFont(font);
        FontMetrics fontMetrics = ig2.getFontMetrics();
        int stringWidth = fontMetrics.stringWidth(message);
        int stringHeight = fontMetrics.getAscent();
        ig2.setPaint(fg);
        ig2.drawString(message, (width - stringWidth) / 2, height / 2 + stringHeight / 4);
    }
    public static BufferedImage getBufferedImage(ImageIcon icon) {
        BufferedImage bi = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D ig2 = bi.createGraphics();
        ig2.drawImage(icon.getImage(), 0, 0, icon.getIconWidth(), icon.getIconHeight(), null);
        return bi;
    }
    public static void writeImage(BufferedImage bi, String format, File fl) throws IOException {
    	ImageIO.write(bi, format, fl);
    }
}