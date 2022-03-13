package com.zentsugo.utils;

import java.awt.image.BufferedImage;
import java.util.HashMap;

/**
 * Note : The ImageCollector is a bank of images containing all the tile images,
 * it serves as a reference for the tiles as they are picking their image here instead of storing their own copy
 * inside them (in object). This would normally save memory as we avoid copying BufferedImage objects.
 * This is possible since the tile image isn't modified by any means but instead rendered with modifications
 * (height, color...) this way we just need a base reference image stored statically here.
 * The ImageCollector is initialized when the tileset is being cut in the Tileset class.
 */

public class ImageCollector {
	private static HashMap<Integer, BufferedImage> collection = new HashMap<Integer, BufferedImage>();
	
	public static BufferedImage get(int idx) {
		return collection.get(idx);
	}
	
	public static void setImage(int idx, BufferedImage image) {
		collection.put(idx, image);
	}
	
	public static void removeImage(int idx) {
		collection.remove(idx);
	}
}
