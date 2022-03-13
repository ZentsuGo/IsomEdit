package com.zentsugo.components;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.zentsugo.isomedit.IsomEdit;
import com.zentsugo.map.Tile;
import com.zentsugo.map.TileMap;
import com.zentsugo.map.layers.Layer;
import com.zentsugo.map.layers.TileLayer;
import com.zentsugo.utils.Utils;

/*
 * MapPane is a JPanel that manages the camera and the world where is the map,
 * it contains the tiles and animated tiles, optionally a grid depends on the user.
 * This is where the user can move the map and interact with it by drawing, erasing or transforming.
 * Since this panel is separately included into the frame, it needs public methods to interact with other
 * components.
 * 
 * Note : The TilesetPane and the MapPane are quite similar since they share most of their functionalities,
 * except the Map functions which are concentrated on editing the map.
*/

public class MapPane extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private IsomEdit isomedit;
	private boolean needSave; //needSave flag is used to keep track of changes and ask user to save if needed to avoid modification loss
	
	//maps
	private TileMap map; //we can load several maps at the same time that are managed with layers
	
	//pane
	//zoom
	private float zoom = 1f;
	private float zoom_max = 20f; //max zoom of 2000%
	private float zoom_min = 0.01f;
	
	//*** SETTINGS ***//
	
	//grid
	private boolean grid;
	//opacity layer
	private boolean opacity_layer = false; //by default false
	
	
	//mode
	public enum MODE {
		CURSOR,
		ERASER
	}
	
	//Among the tools in the toolbar the tool/mode is selected with an index
	private MODE current_mode = MODE.CURSOR; //default mode tool cursor
	
	/*
	 * 0 = cursor
	 * 1 = opacity layer
	 * 2 = eraser
	 * 
	 * 
	 * 
	 * 
	 */
	
	//selection
	//moving
	private Point first;
	private Point second = new Point(0, 0);
	private Rectangle view = new Rectangle(0, 0);
	private boolean drag;
	private long last;
	
	//tile dragging
	private boolean tiledrag_place;
	private boolean tiledrag_remove;
//	private ArrayList<Point> tiles = new ArrayList<Point>();
	
	//stats
	private int x, y;
	private int tx, ty;
	private Point p; //preview tile
	
	public MapPane(IsomEdit isomedit) {
		this.isomedit = isomedit;
		setBackground(Color.GRAY.darker());
		
		MouseAdapter listener = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				//if there is no map selected/loaded then cancel
				if (!isMapLoaded()) return;
				
				//left click
				//move panel with alt button + left mouse click
				if (e.isAltDown() && e.getButton() == MouseEvent.BUTTON1) {
					//move panel
					first = new Point(e.getX(), e.getY());
					first.x -= second.x;
					first.y -= second.y;
					
					setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
					drag = true;
				}
				//if the mouse is pressed then place a tile if selected left click
				else if (e.getButton() == MouseEvent.BUTTON1) {
					tiledrag_place = true;
					//IF PLACE TILE MODE IS SELECTED (AMONG THE TOOLS)
					placeTile(e.getPoint());
//					tiles.add(e.getPoint());
					
					repaint(e.getPoint());
				}
				
				//right click
				//remove tile is cursor is hovering a tile in map left click only
				//note : replace this with button mode erase then right click only will be for option/select/properties
				if (isModeEnabled(MODE.ERASER)) {
					if (e.getButton() == MouseEvent.BUTTON1 && isTileHovering(getTranslatedPoint(e.getX(), e.getY()))) {
						tiledrag_remove = true;
						
						removeTile(e);
						
						repaint(e.getPoint());
					}
				}
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				if (!isMapLoaded()) return;
				
				//if dragging on then place or remove tile all along
				if (tiledrag_place) {
                    p = e.getPoint();
					
					placeTile(e.getPoint());
//                    tiles.add(e.getPoint());
                    
					repaint(e.getPoint());
				}
				
				if (tiledrag_remove) {
					p = e.getPoint();
					
					removeTile(e);
					
					repaint(e.getPoint());
				}
				
				//move panel
				if (drag) {
//            		JViewport viewport = (JViewport) SwingUtilities.getAncestorOfClass(JViewport.class, MapPane.this);
//            		if (viewport != null) {
	            		if (first != null) {
	            			int deltaX = (int) (e.getX() - first.getX());
	                        int deltaY = (int) (e.getY() - first.getY());
	                        
	                        second = new Point(deltaX, deltaY);
	                        view.x = deltaX;
	                        view.y = deltaY;
	                        
	    					repaint(MapPane.this.getVisibleRect());
//	                        MapPane.this.scrollRectToVisible(view);
//	            		}
            		}
            	}
			}
			
			@Override
			public void mouseMoved(MouseEvent e) {
				if (!isMapLoaded()) return;
				
				if (!withinTheMap(getTranslatedPoint(e.getX(), e.getY()))) return;
				if (isomedit.getTilesetPane().getSelectedTile() == null) return;
				
				if (!tiledrag_place && !tiledrag_remove) {
					p = e.getPoint(); //update preview tile location
					
					repaint(e.getPoint());
				}
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				if (!isMapLoaded()) return;
				
				//cancel tiledrag
				if (tiledrag_place) {
					tiledrag_place = false;
//					tiles.clear();
				}
				if (tiledrag_remove)
					tiledrag_remove = false;
				
				//move panel
				if (e.getButton() == MouseEvent.BUTTON1 && drag) {
					setCursor(Cursor.getDefaultCursor());					
					drag = false;
				}
			}
		};
		
		addMouseListener(listener);
		addMouseMotionListener(listener);
		
		//handles zoom
		addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if (!isMapLoaded()) return;
				
				if(e.isControlDown()) {
					//zoom position
					tx = e.getX();
					ty = e.getY();
					
					int rotation = e.getWheelRotation();
					if (rotation > 0) {
						//if the scroll is towards the user, scroll down
						zoom -= 0.1f;
						zoom = Math.max(zoom_min, zoom);
					} else {
						//if the scroll is away from the user, scroll up
						zoom += 0.1f;
						zoom = Math.min(zoom_max, zoom);
					}
					p = e.getPoint();
//					isomedit.setMapPaneZoom(zoom);
					repaint(MapPane.this.getVisibleRect());
				}
			}
		});
		
		this.map = null;
		this.drag = false;
		this.grid = true; //by default
		this.needSave = false; //by default
	}
	
	private void previewTile(Graphics2D g, Point e) {
		if (e == null) return;
		Tile selected_tile = isomedit.getTilesetPane().getSelectedTile();
		if (selected_tile != null) {
			Layer layer = map.getCurrentLayer();
			
			if (layer instanceof TileLayer) {
				Point2D p = getTranslatedPoint((float) e.getX(), (float) e.getY());
				x = (int) p.getX();
				y = (int) p.getY();
				
				//convert cell position in mouse coordinates to tile coordinates (world map)
				
				Point iso = Utils.toWorld(x, y, map);
				x = iso.x;
				y = iso.y;
				
				Tile instance = selected_tile.clone();
				instance.setPosition(new Point(x, y));
				
				if (!map.isValidCoord(x, y)) return;
				
				//drawing
				Composite previous = g.getComposite();
				
				g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
				
				Point pos = Utils.toIsometric(x, y, map);
				int offset_y = instance.getSize().height - (map.getTileSize().height / 2); //offset y for taller or shorter tiles
				//subtract half of the map tile size height (map creator) to the tile size height for the tile to be placed on top of the grid
				g.drawImage(instance.getTile(), view.x + pos.x, view.y + pos.y - offset_y, null);
				
				g.setComposite(previous);
			}
		}
	}
	
	private void placeTile(Point e) {
		Tile selected_tile = isomedit.getTilesetPane().getSelectedTile();
		if (selected_tile != null) {
			Layer layer = map.getCurrentLayer();
			if (layer == null) return;
			
			if (layer instanceof TileLayer) {
				TileLayer tile_layer = (TileLayer) layer;
				//if the layer is a tile layer then we can add a tile to it
				Point2D p = getTranslatedPoint((float) e.getX(), (float) e.getY());
				x = (int) p.getX();
				y = (int) p.getY();
				
				//convert cell position in mouse coordinates to tile coordinates (world map)
				
				Point iso = Utils.toWorld(x, y, map);
				x = iso.x;
				y = iso.y;
				
				/**
				 * Note "Tile Placing" :
				 * The tile placing concept is about placing copy of the tile from the tileset and not directly the tile
				 * from the tileset itself, they are clones so copies of the tile from the tileset and have fixed location
				 * in the world map as well as their own unique properties, the only real copied stuff are the image and the sign.
				 */
				
				Tile instance = selected_tile.clone();
				instance.setPosition(new Point(x, y)); //define the position of the cloned tile to the world map
				
				if (tile_layer.isSameTile(instance)) return;
				
				tile_layer.addTile(instance); //add the tile to the tile layer
				
//				repaint(MapPane.this.getVisibleRect()); //repaint to refresh the world map
//				isomedit.getMiniMapPane().repaint(); //repaint to refresh the mini map
			}
		}
	}
	
	public void removeTile(MouseEvent e) {
		Layer layer = map.getCurrentLayer();
		
		if (layer instanceof TileLayer) {
			TileLayer tile_layer = (TileLayer) layer;
			//if the layer is a tile layer then we can add a tile to it
			Point2D p = getTranslatedPoint(e.getX(), e.getY());
			x = (int) p.getX();
			y = (int) p.getY();
			
			//convert cell position in mouse coordinates to tile coordinates (world map)
			
			Point iso = Utils.toWorld(x, y, map);
			
		    x = iso.x;
		    y = iso.y;
		    
		    //removes the tile if exists
		    tile_layer.removeTile(x, y);
//		    repaint(MapPane.this.getVisibleRect());
		}
	}
	
	private boolean withinTheMap(Point2D point2d) {
		if (point2d == null) return false;
		Point world_position = Utils.toWorld(point2d.getX(), point2d.getY(), map);
		return map.isValidCoord(world_position.x, world_position.y); 
	}
	
	private boolean isTileHovering(Point2D position) {
		if (position == null) return false;
		if (map.getCurrentLayer() == null) return false;
		
		Point world_position = Utils.toWorld(position.getX(), position.getY(), map);
		if (!map.isValidCoord(world_position.x, world_position.y)) return false;
		
		//should be get tile layer here
		/**
		 * Note : Here should be a switch case for the different types of layers where for each we handle different cases
		 * getTile or getObject or getSomething
		 * and so on
		 */
		return ((TileLayer) map.getCurrentLayer()).getTile(world_position.x, world_position.y) != null;
	}
	
	//			SETTERS			//
	
	/**
	 * Toggles the grid
	 */
	public void toggleGrid() {
		this.grid = !this.grid;
	}
	
	//*** MODES ***//
	/**
	 * Toggles the mode
	 */
	public void toggleMode(MODE mode) {
		switch (mode) {
			case ERASER:
				setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
				break;
			default:
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				break;
		}
		this.current_mode = mode;
	}
	
	/**
	 * Toggles opacity layer
	 */
	public void toggleOpacity() {
		if (!isMapLoaded()) return;
		
		this.opacity_layer = !this.opacity_layer;
		
		repaint(MapPane.this.getVisibleRect());
	}
	
	/**
	 * Load a new map to the maps
	 * @param map
	 */
	public void importMap(TileMap map) {
		if (map == null) return;
		this.map = map;
		initGrid();
		zoom = 1f;
		setBackground(new Color(84, 84, 84));
		
		repaint(MapPane.this.getVisibleRect());
		
		//update the last rendering fields after the repaint
		last = System.currentTimeMillis();
		last_rect = System.currentTimeMillis();
	}
	
	/**
	 * Updates the save need.
	 */
	public void needSave() {
		this.needSave = true;
	}
	
	//			GETTERS			//
	
	//*** MODES ***//
	/**
	 * Is grid option toggled ?
	 * @return grid
	 */
	public boolean isGrid() {
		return this.grid;
	}
	
	/**
	 * Is this mode currently enabled ?
	 * @return mode
	 */
	public boolean isModeEnabled(MODE mode) {
		return this.current_mode == mode;
	}
	
	/**
	 * Is opacity layer setting enabled ?
	 */
	public boolean isOpacityEnabled() {
		return this.opacity_layer;
	}
	
	/**
	 * Is map loaded ?
	 * @return map not null
	 */
	public boolean isMapLoaded() {
		return map != null;
	}
	
	/**
	 * Returns the current map loaded
	 * @return map loaded
	 */
	public TileMap getMap() {
		return map;
	}
	
	//			DRAWING FUNCTIONS			//
	
	private void paintSelection(Graphics2D g) {
		
	}
	
	private BufferedImage grid_image = null;
//	private BufferedImage[] grids = null;	
//	private boolean composed_grid = false;
	private boolean initGrid = false;
//	private final int max_unit = (int) Math.sqrt(Integer.MAX_VALUE) / 8;
	
	private void initGrid() {
		int width = map.getDimension().width * map.getTileSize().width + map.getTileSize().width; //some offset
		int height = map.getDimension().height * map.getTileSize().height + map.getTileSize().height;
//		long size = (long) width * height; //casting to long is important otherwise it goes beyond integer limit and becomes negative
//		System.out.println("size : " + size);
//		if (size > (max_unit * max_unit)) { //if map size is greater than max integer sqrt
//			grids = new BufferedImage[(int) Math.ceil(size / (max_unit * max_unit))];
//			System.out.println("grids " + grids.length);
//			//one grid image size is max Math.sqrt(Integer.MAX_VALUE) both width and height
//			int x_grids = (int) Math.ceil(width / max_unit);
//			int y_grids = (int) Math.ceil(height / max_unit);
//			long remaining_width = width;
//			long remaining_height = height;
//			for (int y = 0; y < y_grids; y++) {
//				for (int x = 0; x < x_grids; x++) {
//					if (remaining_width <= 0 || remaining_height <= 0) break;
//					int index = x + y * x_grids;
//					
//					//image
//					BufferedImage grid = new BufferedImage(
//							(int) Math.min(remaining_width, max_unit),
//							(int) Math.min(remaining_height, max_unit), BufferedImage.TYPE_INT_ARGB);
//					
//					//drawing
//					drawGrid(grid);
//					System.out.println("still");
//					//grids
//					remaining_width -= max_unit;
//					grids[index] = grid;
//				}
//				remaining_width = width;
//				remaining_height -= max_unit;
//			}
//			System.out.println("here");
//			composed_grid = true;
//		} else {
		
		grid_image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		drawGrid(grid_image);
		initGrid = true;
	}
	
	private void drawGrid(BufferedImage image) {
		Graphics2D g = image.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); //somehow the image is more blurry with these rendering hints set
		g.setColor(Color.GRAY);
		g.setStroke(new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, new float[]{2f, 2f}, 0)); //dashed line
		Point origin = map.getOrigin();
		map.setOrigin(map.getDimension().width / 2, 1); //modify the origin of the map in order to fit the image as the screen coordinates
		int x = 0, max_x = map.getDimension().width; //keep the coordinates and dimensions in world map system since the toIsometric convert them already
		for (int y = -1; y < map.getDimension().height; y++) {
			Point start = Utils.toIsometric(x, y, map);
			Point end = Utils.toIsometric(max_x, y, map);
			g.drawLine(start.x, start.y, end.x, end.y);
		}
		
		int y = -1, max_y = map.getDimension().height - 1;
		for (x = 0; x <= map.getDimension().width; x++) {
			Point start = Utils.toIsometric(x, y, map);
			Point end = Utils.toIsometric(x, max_y, map);
			g.drawLine(start.x, start.y, end.x, end.y);
		}
		map.setOrigin(origin.x, origin.y);
		g.dispose();
	}
	
	/**
	 * Rendering the grid is an extremely costly operation, we need to optimize it.
	 * In order to prevent heavy rendering, we create a buffered image where the grid has been drawn on it once,
	 * this way we only need to directly render the grid.
	 * THIS IS A TEMPORARY SOLUTION : this works very well for tiny maps, since they are in the dimensions of "normal" images
	 * but for big maps the dimensions are too high and we get a java heap space out of memory error exception because
	 * the image can't be this big since its size depends on the map dimensions.
	 * Maybe we could make it several images to split in order to render as little as possible images to fill the map ?
	 * See also some rendering hints or others to render fully the grid since when zoomed-out the grid doesn't display well.
	 * @param g
	 */
	private void paintGrid(Graphics2D g) {
		if (!initGrid) {
			initGrid();
		}
		if (grid_image != null) {
			g.drawImage(grid_image, view.x, view.y, null);
		}
	}
	
	private void loadMap(Graphics2D g) {
		if (map == null) { //if no map is selected or there is no map
			g.setRenderingHint(
			        RenderingHints.KEY_TEXT_ANTIALIASING,
			        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g.setFont(new Font("Yu Gothic UI", Font.PLAIN, 18));
			g.setColor(Color.LIGHT_GRAY);
			drawString(g, "      No map loaded\nCreate oar import a map", (int) getVisibleRect().getWidth() / 2 - 60,
					(int) getVisibleRect().getHeight() / 2 - 5);
			return;
		}
		
		g.setColor(getBackground()); //set color to the background color first set (a gray)
        g.fillRect(0, 0, getWidth(), getHeight()); //clear the screen with it
        
        at.translate(view.x + map.getOrigin().x * map.getTileSize().width, view.y + map.getOrigin().y * map.getTileSize().height);
        
//        if (tiles != null) {
//        	if (tiles.size() > 2) {
//        		for (int i = 1; i < tiles.size(); i++) {
//        			Point p = tiles.get(i - 1);
//        			Point p2 = tiles.get(i);
//        			Line2D line = new Line2D.Double(p.getX(), p.getY(), p2.getX(), p2.getY());
//        			PathIterator pi = line.getPathIterator(null);
//        			
//        			float[] coords = new float[6];
//        			Point2D previous_world_coords = null;
//        			g.setColor(Color.RED);
//        			while (!pi.isDone()) {
//        				pi.currentSegment(coords);
//        				Point2D world_coords = Utils.toWorld(coords[0], coords[1], map);
//        				if (!world_coords.equals(previous_world_coords)) {
//        					placeTile(new Point((int) coords[0], (int) coords[1]));
//        				}
//        				previous_world_coords = world_coords;
//        				pi.next();
//        			}
//        		}
//        	}
//        }
        
        map.setViewOffset(view); //draw offset
        map.draw(g);
	}
	
	private void showInformations(Graphics2D g) {
		if (map == null) return;
		g.setColor(Color.WHITE);
		Tile selected_tile = isomedit.getTilesetPane().getSelectedTile();
		drawString(g, "Mouse position : (" + x + ", " + y + ")\n"
				+ "Tile selected : " + (selected_tile == null ? "empty" : selected_tile.getIdx()) + "\n"
				+ "Zoom : " + zoom + "\n"
				+ "Map dimensions : (" + map.getDimension().width + ", " + map.getDimension().height + ")",
				this.getVisibleRect().x + 20, this.getVisibleRect().height - 100);
		if (selected_tile != null) {
			g.drawImage(selected_tile.getTile(), this.getVisibleRect().x + 200, this.getVisibleRect().height - 80, 50, 50, null);
		}
	}
	
	private void drawString(Graphics g, String text, int x, int y) {
        for (String line : text.split("\n"))
            g.drawString(line, x, y += g.getFontMetrics().getHeight());
    }
	
	private AffineTransform at = new AffineTransform();
	
	//apply zoom
	private void applyZoom(Graphics2D g) {
		if (map == null) return;
		
		at = new AffineTransform(); //need to be reset each call otherwise it'll zoom over the current zoom and zoom is already saved by zoom variable
		
		//ensures the zoom is relative to cursor position
		at.translate(tx, ty); //translate to cursor
		at.scale(zoom, zoom); //scale from there
		at.translate(-tx, -ty); //translate back
		
		g.transform(at);
	}
	
	//Convert the panel coordinates into the corresponding coordinates on the translated (zoomed) image
	//From panel coordinates to travel space (affine transform) coordinates
	private Point2D getTranslatedPoint(float panelX, float panelY) {
	    Point2D point2d = new Point2D.Float(panelX, panelY);
	    try {
	        return at.inverseTransform(point2d, null);
	    } catch (NoninvertibleTransformException e) {
	        e.printStackTrace();
	        return null;
	    }
	}
	
	/**
	 * Repaints with a rectangle according to the specified point.
	 * Optimizes performance but also with capped rendering every 20 milliseconds minimum.
	 * @param p
	 */
	private void repaint(Point p) {
		/**
		 * If the last time rendered was less than 20 milliseconds ago then we cancel the rendering.
		 */
		if (System.currentTimeMillis() - last < 20) return;
		last = System.currentTimeMillis(); //update the last time rendered
		
		Dimension s = null;
		
		if (map.getTileset() != null)
			s = map.getTileset().getTileSize();
		else
			s = map.getTileSize();
		
		repaint(p.x - (s.width * 3), p.y - (s.height * 3), s.width * 6, s.height * 6); //render a square around the cursor for a good area
	}
	
	private long last_rect;
	
	@Override
	public void repaint(Rectangle r) {
		/**
		 * If the last time rendered was less than 5 milliseconds ago then we cancel the rendering.
		 */
		if (System.currentTimeMillis() - last_rect < 5) return;
		last_rect = System.currentTimeMillis(); //update the last time rendered
		
		super.repaint(r);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g.create();
		
		//DEBUG
//		if (isomedit.getTilesetPane().getSelectedTile() != null) {
//			Dimension s = map.getTileset().getTileSize();
//			if (p == null) return;
////			Rectangle r = new Rectangle(p.x - (s.width * 3), p.y - (s.height * 3), s.width * 6, s.height * 6);
//			Rectangle r = g2.getClipBounds();
//			g2.setColor(Color.RED);
//			g2.draw(r);
//		}
		applyZoom(g2); //fix the zoom with location
		loadMap(g2);
		if (map != null) {
			if (grid)
				paintGrid(g2);
			if (!drag)
				previewTile(g2, p);
			paintSelection(g2);
		}
		g2.setTransform(new AffineTransform()); //reset transform
		showInformations(g2); //draw independently of the zoom and last because it's on top of everything else
		g2.dispose();
	}
}
