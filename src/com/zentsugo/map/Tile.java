package com.zentsugo.map;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.Serializable;

import com.zentsugo.utils.ImageCollector;

/*
 * Note that this Tile class could also be used in the game, but here is only used for the IsomEdit Editor.
 * Indeed, the classes for the map are pretty similar to the game's, since a map editor is actually a preview
 * for the in-game map but with tools and utils to customize the map, so we could include as well the map editor into the game itself.
 */

public class Tile implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	//mark image as transient for it not to be serialized when saved
	private int idx; //the sign that represents the tile text format
	
	private Point position = null; //position in the world map
	private Point iso_position = null; //position in the screen (isometric)
	
	//save reference
	private Tileset tileset = null;
	
	//The Tile is created during the cut (pixel-space algorithm) process
	//in the loading of tileset when creating a new tileset/importing one
	//which means that the tile is already space optimized and the size of the tile is the size of the image
	public Tile(int idx, Tileset tileset) {
		this.idx = idx;
		this.tileset = tileset;
	}
	
	public void setIdx(int idx) {
		this.idx = idx;
	}
	
	public BufferedImage getTile() {
		return ImageCollector.get(idx); //return the image from the image collector
	}
	
	public int getIdx() {
		return idx;
	}
	
	public void setPosition(Point position) {
		this.position = position;
	}
	
	public void setIsoPosition(Point position) {
		this.iso_position = position;
	}
	
	/**
	 * Returns the position of the cell containing the tile in the map (in cell).
	 * @return Position/Location of the cell
	 */
	public Point getPosition() {
		return position;
	}
	
	public Tileset getTileset() {
		return tileset;
	}
	
	public Point getIsoPosition() {
		return iso_position;
	}
	
	public Dimension getSize() {
//		return new Dimension(image.getWidth(), image.getHeight());
		BufferedImage image = ImageCollector.get(idx);
		return new Dimension(image.getWidth(), image.getHeight());
	}
	
	//position of the tile in the texture image
	private Point tilesetpos;
	
	public void setTilesetPosition(Point tilesetpos) {
		this.tilesetpos = tilesetpos;
	}
	
	/**
	 * Returns the position of the tile in the tileset.
	 * @return
	 */
	public Point getTilesetPosition() {
		return tilesetpos;
	}
	
	public Tile clone() {
		Tile copy = new Tile(idx, tileset);
		copy.setTilesetPosition(this.tilesetpos);
		copy.setIsoPosition(iso_position);
		return copy;
	}
}
