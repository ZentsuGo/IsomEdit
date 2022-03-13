package com.zentsugo.map;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.io.Serializable;
import java.util.LinkedList;

import com.zentsugo.components.MapPane;
import com.zentsugo.isomedit.IsomEdit;
import com.zentsugo.map.layers.Layer;
import com.zentsugo.map.layers.TileLayer;
import com.zentsugo.utils.Utils;

/*
 * Isometric Tiled Map where tiles, objects and the background are rendered in.
 * It contains a world with isometric coordinates based on screen coordinates with conversion,
 * it uses a camera to display different parts of the world.
 */

public class TileMap implements Serializable {
	
	private static final long serialVersionUID = 1L;

	//			ISOMEDIT			//
	private String name; //file name
	
	//			MAP			//
//	private ArrayList<Tile> tiles; //from the different tilesets they are all gathered here
	private LinkedList<Layer> layers;
	private Layer selected_layer; //by default a tile layer is created with index 0
	
	private Dimension dimension; //world dimension
	private Dimension tile_dimension; //tile dimension
	private Rectangle camera; //temp camera
	
	private Point origin; //world origin (in tiles)
	private Rectangle view; //view offset
	private boolean opacity_enabled = false;
	
	//save file
	private File savefile = null;
	
	private Tileset tileset = null;
	
	public TileMap(String name, Dimension dimension, Dimension tile_dimension) {
		this.name = name;
		this.dimension = dimension;
		this.tile_dimension = tile_dimension;
		this.layers = new LinkedList<Layer>();
		this.origin = new Point(0, 0); //to initialize before adding new tile layer because the tile layer constructor stores the origin
		this.layers.add(new TileLayer("Main Tile layer", this));
		this.selected_layer = layers.getFirst();
	}
	
	public void draw(Graphics2D g) {
		//draw layers in the linkedlist order
		for (Layer l : layers) {
			if (!IsomEdit.getInstance().getMapPane().isOpacityEnabled())
				l.draw(g);
			else {
				if (l != getCurrentLayer()) {
					/**
					 * Note : once the buffer is done for the map we can apply a tint/transparency for each other layer than the selected one
					 * in order for the user to find his way.
					 * 
					 * Note 2 : there is an image/buffer for each layer in order to manage easier the different layers
					 */
					Composite previous = g.getComposite();
					g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
					l.draw(g);
					g.setComposite(previous);
				} else {
					l.draw(g);
				}
			}
		}
	}
	
	//			ISOMEDIT FUNCTIONS			//
	//SETTERS
	public void setViewOffset(Rectangle view) {
		this.view = view;
	}
	
	public void toggleOpacity() {
		this.opacity_enabled = !this.opacity_enabled;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setTileset(Tileset tileset) {
		this.tileset = tileset;
	}

	public void setOrigin(int x, int y) {
		origin.x = x;
		origin.y = y;
	}
	
	public void selectLayer(Layer layer) {
		if (layers.isEmpty()) return;
		
		if (layers.contains(layer))
			selected_layer = layer;
	}
	
	public void setSaveFile(File savefile) {
		this.savefile = savefile;
	}
	
	/**
	 * Saves map into the previously specified file in the map creator.
	 */
	public void saveMap() {
		if (savefile == null) return;
		Utils.saveMapFile(savefile, this);
	}
	
	//GETTERS
	public String getName() {
		return name;
	}
	
	/**
	 * Returns tileset(s) it has been built upon.
	 * @return tileset(s)
	 */
	public Tileset getTileset() {
		return tileset;
	}
	
	public Point getOrigin() {
		return origin;
	}
	
	/**
	 * Gets the tile size as defined in the map creator.
	 * @return tile size
	 */
	public Dimension getTileSize() {
		return tile_dimension;
	}
	
	/**
	 * Returns if the tile has a position within the bounds of the map (in tiles)
	 * @param x
	 * @param y
	 * @return boolean
	 */
	public boolean isValidCoord(int x, int y) {
		return !(y >= getDimension().height || y < 0 || x >= getDimension().width || x < 0);
	}
	
	/**
	 * Returns the save file.
	 * @return save file
	 */
	public File getSaveFile() {
		return savefile;
	}
	
	/**
	 * Updates the map (basically the tileset since there are no other images)
	 * 
	 * This method is supposed to be called when the map is being loaded from a file.
	 */
	public void update() {
		if (tileset == null) return;
		 //if tileset is not null (if null then it's an empty map without tileset)
		tileset.update(); //update the tileset (texture image as well as the tile subimages)
		
		//update the image of the tiles on the map
		//because they are different since they are copies of the tiles of the tileset to have their own unique properties
		//we can update their image with their idx
		
		/**
		 * Update Note :
		 * This system has been changed by adding the ImageCollector which serves as a bank of images for all the tiles,
		 * the tiles pointing to the ImageCollector as a reference instead of storing their image inside them (in objects).
		 * Thus we just need to initialize the layers.
		 */
		for (Layer l : layers) {
			if (l instanceof TileLayer) {
				TileLayer tlayer = (TileLayer) l;
				tlayer.initLayer();
			}
		}
	}
	
	//			MAP FUNCTIONS			//
	
	//SETTERS
	public void addLayer(Layer l) {
		layers.add(l);
	}
	
	public void removeLayer(Layer l) {
		if (this.selected_layer == l) {
			this.selected_layer = null;
		}
		layers.remove(l);
	}
	
	public void setCurrentLayer(Layer l) {
		this.selected_layer = l;
	}
	
	public void setDimension(Dimension dimension) {
		this.dimension = dimension;
	}
	
	//GETTERS
	public Rectangle getViewOffset() {
		return view;
	}
	
	public LinkedList<Layer> getLayers() {
		return layers;
	}
	
	public Layer getCurrentLayer() {
		if (layers.isEmpty()) return null;
		return selected_layer;
	}
	
	public Rectangle getCamera() {
		return camera;
	}
	
	public Dimension getDimension() {
		return dimension;
	}
}
