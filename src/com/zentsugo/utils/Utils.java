package com.zentsugo.utils;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;

import com.zentsugo.map.TileMap;
import com.zentsugo.map.Tileset;

public class Utils {
	/**
	 * Converts world coordinates (tile map) to screen coordinates (isometric location in pixels)
	 * @param x (in tiles)
	 * @param y (in tiles)
	 * @param map TileMap
	 * @return Screen coordinates
	 */
	public static Point toIsometric(int x, int y, TileMap map) {
		Point world_coords = new Point();
		Point origin = map.getOrigin();
		//isometric coordinates conversion + origin of the world offset
		world_coords.x = (x - y) * (map.getTileSize().width / 2) + (origin.x * map.getTileSize().width);
		world_coords.y = (x + y) * (map.getTileSize().height / 2) + (origin.y * map.getTileSize().height);
		return world_coords;
	}
	
	/**
	 * Converts screen coordinates (isometric location in pixels) to world coordinates (tile map)
	 * @param x (in pixels)
	 * @param y (in pixels)
	 * @param map TileMap
	 * @return World coordinates
	 */
	public static Point toWorld(double x, double y, TileMap map) {
		double tx = ((x / (map.getTileSize().width / 2)) + (y / (map.getTileSize().height / 2))) / 2;
	    double ty = ((y / (map.getTileSize().height / 2)) - (x / (map.getTileSize().width / 2))) / 2;
	    ty = Math.ceil(ty); //1 tile y offset needed for perfect selection using math ceil function
	    Point point = new Point((int) tx, (int) ty);
	    return point;
	}
	
	//SAVE
	/**
	 * Saves the map into a map file.
	 * @param file
	 */
	public static void saveMapFile(File savefile, TileMap map) {
		FileManager fm = new FileManager(savefile);
		fm.setup();
		fm.writeObject(map);
	}
	
	public static void saveSetFile(File savefile, Tileset set) {
		FileManager fm = new FileManager(savefile);
		fm.setup();
		fm.writeObject(set);
	}
	
	//LOAD
	
	/**
	 * Loads the map from the map file.
	 * The map contains the tileset(s) it was built upon.
	 * Note : we could consider it a global project.
	 * 
	 * @param file Map
	 * @return Map
	 */
	public static TileMap loadMapFile(File file) {
		if (!file.exists()) return null;
		
		/*
		 * Note : Since it is considered as a global project, in order to display the content of the built map we need
		 * the tileset(s) on which it was built, it's done by using the update method of the tilemap.
		 */
		
		FileManager fm = new FileManager(file);
		Object object = fm.readObject();
		if (!(object instanceof TileMap)) return null;
		
		TileMap map = (TileMap) object;
		map.update();
		
		return map;
	}
	
	/**
	 * Loads the tileset from the tileset file.
	 * The loaded tileset will not override the current one coming with the map, but will be overrided if a map is loaded by
	 * its tileset which it was built on. The loaded tileset will be loaded as a an additional tileset with its own tileset pane.
	 * 
	 * @param file Tileset
	 * @return Tileset
	 */
	public static Tileset loadSetFile(File file) {
		if (!file.exists()) return null;
		
		FileManager fm = new FileManager(file);
		Object object = fm.readObject();
		if (!(object instanceof Tileset)) return null;
		
		Tileset set = (Tileset) object;
		set.update(); //update the tileset for the texture image and the tiles
		
		return set;
	}
	
	/**
	 * Tints the given image with the given color.
	 * @param image
	 * @param color
	 * @return buffered image
	 */
	public static BufferedImage dye(BufferedImage image, Color color) {
	    int w = image.getWidth();
	    int h = image.getHeight();
	    BufferedImage dyed = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g = dyed.createGraphics();
	    g.drawImage(image, 0, 0, null);
	    g.setComposite(AlphaComposite.SrcAtop);
	    g.setColor(color);
	    g.fillRect(0, 0, w, h);
	    g.dispose();
	    return dyed;
	  }
}
