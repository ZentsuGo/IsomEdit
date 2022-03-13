package com.zentsugo.map.layers;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.Serializable;

import javax.swing.text.View;

import com.zentsugo.map.Tile;
import com.zentsugo.map.TileMap;
import com.zentsugo.utils.Utils;

public class TileLayer extends Layer implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	//map
	private Tile[] tiles; //the layer is part of the map but represents only its owning objects (here tiles)
	private transient BufferedImage buffer; //buffer image containing all the tiles for performance
	
	public TileLayer(String name, TileMap map) {
		super(name, map);
		Dimension d = map.getDimension();
		this.tiles = new Tile[d.width * d.height]; //initialize the tile array to the size of the map dimensions (in tiles)
	}
	
	//SETTERS
	/**
	 * Adds a tile to the tile layer at the specified location (in tiles)
	 * @param x
	 * @param y
	 * @param tile
	 */
	public void addTile(Tile tile) {
		if (tile == null) return;
		/**
		 * Note : In order to keep reference to the tileset we save it from the tile,
		 * this is done since the map and the tileset are linked because the map is built upon it, thus
		 * when it's being saved into a file we need to keep track of it, thanks to this when the map will be loaded
		 * back from a file the map can be displayed correctly because the tileset has been saved and can be loaded back
		 * as well as the tiles inside of it.
		 * 
		 * Note 2 : if no tile has been added then the map is empty so the tileset is null, no worries nothing must be displayed in
		 * this case, no error.
		 */
		if (map.getTileset() == null)
			map.setTileset(tile.getTileset());
		
		if (!isValidCoord(tile)) return;

		int x = tile.getPosition().x; //x (in tiles)
		int y = tile.getPosition().y; //y (in tiles)
		
		Point p = Utils.toIsometric(x, y, map); //get screen coordinates from the tile position in the world map
		tile.setIsoPosition(p);
		
		this.tiles[x + y * map.getDimension().width] = tile; //place a tile at the specified location in a single array dimension
		
		updateLayer(tile, true);
	}
	
	/**
	 * Removes a tile to the tile layer at the specified location (in tiles)
	 * @param x
	 * @param y
	 */
	public void removeTile(int x, int y) {
		if (!map.isValidCoord(x, y)) return;
		
		Tile tile = getTile(x, y);
		
		if (tile != null) {
			updateLayer(tile, false); //call update layer before setting the tile to null
			this.tiles[x + y * map.getDimension().width] = null;
		}
	}
	
	//GETTERS
	public Tile getTile(int x, int y) {
		if (!isValidCoord(x, y)) return null;
		return this.tiles[x + y * map.getDimension().width]; //return the tile at the specified location in a single array dimension
	}
	
	public Tile[] getTiles() {
		return tiles;
	}
	
	public String getName() {
		return name;
	}
	
	/**
	 * Returns if the same tile has already been placed, useful in order to prevent from redrawing all the tiles to save computation.
	 * @param tile
	 * @return is same tile or not
	 */
	public boolean isSameTile(Tile tile) {
		if (!isValidCoord(tile)) return false;
		
		int x = tile.getPosition().x;
		int y = tile.getPosition().y;
		
		Tile tile2 = tiles[x + y * map.getDimension().width];
		if (tile2 == null) return false;
		
		return (tile.getIdx() == tile2.getIdx() && tile.getTilesetPosition().equals(tile2.getTilesetPosition()));
	}
	
	/**
	 * Returns if the tile has a position within the bounds of the map (in tiles)
	 * @param tile
	 * @return boolean
	 */
	private boolean isValidCoord(Tile tile) {
		int x = tile.getPosition().x; //x (in tiles)
		int y = tile.getPosition().y; //y (in tiles)
		
		//check map boundaries
		return !(y >= map.getDimension().height || y < 0 || x >= map.getDimension().width || x < 0);
	}
	
	private boolean isValidCoord(int x, int y) {
		//check map boundaries
		return !(y >= map.getDimension().height || y < 0 || x >= map.getDimension().width || x < 0);
	}
	
	//DRAW METHODS
	
	/**
	 * Initializes the layer by drawing all the tiles onto the buffer image.
	 * Note : this method is used to update the tiles on the screen but also when loading a new map.
	 */
	public void initLayer() {
		if (buffer == null) {
			this.buffer = new BufferedImage(map.getDimension().width * map.getTileSize().width, map.getDimension().height * map.getTileSize().height,
					BufferedImage.TYPE_INT_ARGB);
		}
		
		Graphics2D g = buffer.createGraphics();
		/*
		 * Note : We could render directly by iterating through the single dimension array but we use instead
		 * the nested loop in order to force the rendering order to be from up to down (behind to front) for the
		 * tiles with the background isometric effect
		 */
		for (int y = 0; y < map.getDimension().height; y++) {
			for (int x = 0; x < map.getDimension().width; x++) {
				Tile t = tiles[x + y * map.getDimension().width];
				//draw tile
				if (t != null) {
					if (t.getPosition() == null) continue;
					drawTile(t, g);
				}
			}
		}
		
		g.dispose();
	}
	
	/**
	 * Updates the layer (which is the buffer) by redrawing only the specified tile along with its neighbors.
	 * This optimizes performance because it avoids rendering the whole layer and is possible by using the buffer
	 * method where the image keeps the previous drawing.
	 * 
	 * @param tile specified tile
	 * @param place if we should update the layer with a placed/added tile or a removed tile
	 */
	public void updateLayer(Tile tile, boolean place) {
//		if (buffer == null) return;
		
//		Graphics2D g = buffer.createGraphics();
//		
//		Point position = tile.getPosition();
//		System.out.println("position : " + position);
////		Tile n1 = getTile(position.x - 1, position.y);
////		Tile n2 = getTile(position.x, position.y - 1);
//		Tile n3 = getTile(position.x, position.y + 1);
//		Tile n4 = getTile(position.x + 1, position.y);
//		
//		Tile[] tmp = {tile, n3, n4}; //draw in right order from top to down and left to right
//		
//		if (place) {
//			for (Tile t : tmp) {
//				if (t == null) continue;
//				drawTile(t, g); //draws on top of previous tile if there is
//			}
//		} else {
//			for (Tile t : tmp) {
//				if (t == null) continue;
//				if (t == tile)
//					g.clearRect(position.x, position.y, tile.getSize().width, tile.getSize().height);
//				else
//					drawTile(t, g);
//			}
//		}
//		
//		g.dispose();
	}
	
	@Override
	public void draw(Graphics2D g) {
//		if (buffer == null) initLayer();
//		
//		Rectangle view = map.getViewOffset();
//		g.drawImage(buffer, (view != null ? view.x : 0), (view != null ? view.y : 0), null);
		for (int y = 0; y < map.getDimension().height; y++) {
			for (int x = 0; x < map.getDimension().width; x++) {
				Tile t = tiles[x + y * map.getDimension().width];
				//draw tile
				if (t != null) {
					if (t.getPosition() == null) continue;
					drawTile(t, g);
				}
			}
		}
	}
	
	private void drawTile(Tile t, Graphics2D g) {
		if (t == null) return;
		Point p = t.getIsoPosition(); //has already been set for once when tile added to the map to avoid repeating calculations
		int offset_y = t.getSize().height - (map.getTileSize().height / 2); //offset y for taller or shorter tiles
		//subtract half of the map tile size height (map creator) to the tile size height for the tile to be placed on top of the grid
//		g.drawImage(t.getTile(), p.x, p.y - offset_y, null);
		g.drawImage(t.getTile(), map.getViewOffset().x + p.x, map.getViewOffset().y + p.y - offset_y, null);
	}
}
