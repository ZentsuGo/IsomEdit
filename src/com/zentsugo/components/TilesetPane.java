package com.zentsugo.components;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicPanelUI;

import com.zentsugo.isomedit.IsomEdit;
import com.zentsugo.map.Tile;
import com.zentsugo.map.Tileset;
import com.zentsugo.map.Tileset.TILEMODE;

public class TilesetPane extends JPanel {
	
	private static final long serialVersionUID = 2800230485817821155L;
	
	private Tileset tileset;
	
	//properties
	private Dimension tile_resize = null;
	
	//pane
	//zoom
	private float zoom = 0.1f; //the tileset drawn is really big so need to zoom out a bit
	private float zoom_max = 20f; //max zoom of 2000%
	private float zoom_min = 0.01f;
	
	//selected tile
	private Tile selected_tile = null;
	
	//texture
	private BufferedImage drawn_tileset; //this is the drawn tileset which means it's not the tileset texture image,
	//this drawn tileset has been adjusted with crop and eventual resize, transparency color removed, spaces removed and (for zoom) image filtered
	//while the original tileset texture image is still kept by tileset.getTexture() method
	
	//selection
    private Shape shape = null;
    private Point2D startDrag, endDrag;
    
    //temps
    private Point first;
    private int lastButton;
    private boolean drag = false;
	
    //functions
    private boolean grid = true;
    
	public TilesetPane(IsomEdit isomedit) {
		setBackground(Color.GRAY.darker());
		
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (tileset == null) return;
				
				//one selection
				if (e.getButton() == MouseEvent.BUTTON1) {
					Point2D p = getTranslatedPoint(e.getX(), e.getY());
					int tile_x = (int) p.getX() / tileset.getTileSize().width; //get tile x position from the cursor
					int tile_y = (int) p.getY() / tileset.getTileSize().height; //same for y
					
					if (tile_x >= tileset.getTiles()[0].length || tile_y >= tileset.getTiles().length ||
							tile_x < 0 || tile_y < 0)
						return;
					
					Tile tile = tileset.getTiles()[tile_y][tile_x]; //get tile position from the tiles of the tileset
					selected_tile = tile; //select the selected tile
					repaint();
				}
				
				//hold selection
				if (e.getButton() == MouseEvent.BUTTON1) {
					//reset the previous selection
					shape = null;
					//because of the zoom, we need to convert the panel coordinates to the affine transform's coordinates of the graphics
					startDrag = getTranslatedPoint(e.getX(), e.getY());
	                endDrag = startDrag;
	                lastButton = 1;
	                repaint();
				}
				
				//refresh the map pane
				if (isomedit.getMapPane().isMapLoaded())
					isomedit.getMapPane().repaint();
			}
			
			public void mouseReleased(MouseEvent e) {
				if (tileset == null) return;
				
				//selection
				if (e.getButton() == MouseEvent.BUTTON1) {
	                if(endDrag != null && startDrag != null) {
	                    try {
	                    	Point2D coords = getTranslatedPoint(e.getX(), e.getY());
	                        shape = rectangle((float) startDrag.getX(), (float) startDrag.getY(),
	                        		(float) coords.getX(), (float) coords.getY());
	                        
	                        startDrag = null;
	                        endDrag = null;
	                        
	                        repaint();
	                    } catch (Exception e1) {
	                        e1.printStackTrace();
	                    }   
	                }
				}
            }
		});
		
		MouseAdapter adapter = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (tileset == null) return;
				
				//move through the panel
				if (e.getButton() == MouseEvent.BUTTON3) {
					first = new Point(e.getX(), e.getY());
					drag = true;
					setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
				}
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				if (tileset == null) return;
				
				//move through the panel
				if (e.getButton() == MouseEvent.BUTTON3 && drag) {
					drag = false;
					setCursor(Cursor.getDefaultCursor());
				}
			}
			
			@Override
            public void mouseDragged(MouseEvent e) {
            	if (tileset == null) return;
            	
            	//move through the panel
            	if (drag) {
            		JViewport viewport = (JViewport) SwingUtilities.getAncestorOfClass(JViewport.class, TilesetPane.this);
            		if (viewport != null) {
	            		if (first != null) {
	            			int deltaX = first.x - e.getX();
	                        int deltaY = first.y - e.getY();
	
	                        Rectangle view = viewport.getViewRect();
	                        view.x += deltaX;
	                        view.y += deltaY;
	                        
	                        TilesetPane.this.scrollRectToVisible(view);
	            		}
            		}
            	}
            	
            	//selection
            	if (lastButton == MouseEvent.BUTTON1) {
            		endDrag = getTranslatedPoint(e.getX(), e.getY());
            		repaint();
            	}
            }   
        };
        
        addMouseListener(adapter);
        addMouseMotionListener(adapter);
		
		//handles zoom
		addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if (tileset == null) return; 
				
				if(e.isControlDown()) {
					int rotation = e.getWheelRotation();
					if (rotation > 0) {
						//if the scroll is towards the user, scroll down
						zoom -= 0.15f;
						zoom = Math.max(zoom_min, zoom);
					} else {
						//if the scroll is away from the user, scroll up
						zoom += 0.15f;
						zoom = Math.min(zoom_max, zoom);
					}
					isomedit.setTilesetPaneZoom(zoom);
					repaint();
				}
			}
		});
	}
	
	/**
	 * Loads a tileset into the tileset pane.
	 * @param tileset Tileset created based on a texture file.
	 */
	public void importTileset(Tileset tileset) {
		this.tileset = tileset;
		drawn_tileset = adjustTileset(tileset); //adjusts the tileset to draw it better in the tilesetpane
		initGrid(); //init the grid
//		System.out.println("Adjusted Tileset counting filter and resizing - dimensions : (" + drawn_tileset.getWidth() + ", " + drawn_tileset.getHeight() + ")");
		System.out.println("Tileset adjusted.");
		zoom = 1f;
		repaint();
	}
	
	public Tileset getTileset() {
		return tileset;
	}
	
	/*
	 * Display tileset as one image with drawn rectangles on top for selection of each tile ?
	 * This way : Select tile by coordinates
	 * Too complicated for not much performance gain
	 * Classic way : Display tileset as all the tiles so different images forming the single tileset
	 * once clicking on a tile it selects it
	 */
	
	private final int x_offset = 50, y_offset = 50; //for the panel dimensions
	private final int drawoffset = 5; //for the tileset image
	
	/*
	 * Method to render and show the tiles into the tilesetpane,
	 * There are 2 render modes available for now :
	 * 	1 - Classic rendering mode, render each tile according to its original (from the texture) size and its position with some offset.
	 * 	2 - Virtual (forced regular) rendering mode, render all times with the same size according to a virtual size set in the tilesetcreator,
	 * 		this is only for showing purpose to make it easier to select/see, this means that once selected the preview on the map and when
	 * 		put into the map the tile has its original size.
	 * 	The aspect ratio of the tiles / the whole tileset should be overviewed as the user wants, so include zoom/dezoom functions into the tilesetpane
	 * 
	 * Note :
	 * The display modes are using separate tile images already cut, it costs more performance to render each image separately than drawing the whole tileset
	 * image, so why ? This should be done only if the regular (forced) rendering mode is used, indeed by rendering this way it can scale the tiles.
	 * But this is not needed for the classic rendering mode, as it costs more performance.
	 * The tile selection in the tilesetpane is done by searching coordinates and drawing a rectangle selection on top of the tile to indicate the user
	 * that the tile is found and selected, since there is no click event on an rendered image that isn't a component.
	 * 
	 * 15/04 : thus, need an update here, to in case of a regular mode, draw only the whole tileset for more performance.
	 * 16/04 : the tileset should be drawn as one single image/texture with a drawn grid and the selection would work with coordinates.
	 	update 2 : the virtual regular rendering mode is set only exclusive for dynamic tilesets since there is no real
	 	purpose in doing per-tile resize in the case of a regular tileset because they have regular tiles,
	 	resizing the whole image would have no interest too as it would probably result in quality loss while the user
	 	can simply use the zoom functions in the tileset to virtually scale the tileset.
	 	For test purpose now I'll let that whether the tileset is regular or dynamic.
	 */
	private void loadTileset(Graphics2D g) {
		if (tileset == null) {
			g.setRenderingHint(
			        RenderingHints.KEY_TEXT_ANTIALIASING,
			        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g.setFont(new Font("Yu Gothic UI", Font.PLAIN, 18));
			g.setColor(Color.LIGHT_GRAY);
			g.drawString("No tileset loaded", (int) getVisibleRect().getWidth() / 2 - 60,
					(int) getVisibleRect().getHeight() / 2 - 5);
			return;
		}
		
		/*
		 * Note : The rendering of the tiles in the tilesetpane here should work for both regular and dynamic tilesets.
		 */
		
		//draw tileset image texture
		g.drawImage(drawn_tileset, drawoffset, drawoffset, null);
		
		
		Dimension panelsize = new Dimension(); //panel size is changing depending of if the tileresize has been enabled
		
		panelsize.width = drawn_tileset.getWidth();
		panelsize.height = drawn_tileset.getHeight();
		
		//zoom factor to scale the panel size expanding with the zoom
		panelsize.width *= zoom;
		panelsize.height *= zoom;
		
		//offsets are used to show more in the tilesetpane to be able to view and select border tiles easier
		setPreferredSize(new Dimension(panelsize.width + x_offset, panelsize.height + y_offset)); //to resize the panel
		revalidate();
	}
	
	//			SETTERS			//
	
	//			GETTERS			//
	
	public Tile getSelectedTile() {
		return selected_tile;
	}
	
	/***** rendering methods *****/
	
	/**
	 * This method should only be called once if the tileset has not been updated, it adjusts by cropping (with resize) the image if there is a margin,
	 * then it returns a new image as a compact tileset with tiles drawn aside each other with no space if there is tile resize or spacing.
	 * That is used only for the drawn tileset in the tilesetpane for design purpose, so it is used regardless of the tilemode.
	 * 
	 * Note : If you recall this method to update some changes, you need to call the initGrid() method to update the grid dimensions too.
	 * @return image Adjusted tileset texture image
	 */
	public BufferedImage adjustTileset(Tileset tileset) {
		BufferedImage image = tileset.getTexture();
		Dimension margin = tileset.getMargin();
		Dimension spacing = tileset.getSpacing();
		BufferedImage result = image; //initialize at first to image if none of the three below conditions check reinitialize the result
		//variable then it means that the image doesn't have tile resize, margin or spacing parameters and thus we should just filter it for high
		//quality and return it but in any case the result is filtered.
		
		/*
		 * Note : If the tileset has both tile resize and spacing at the same time then we check for the tile resize first in priority and it overrides,
		 * takes over the spacing. Indeed, in both cases the drawn tileset will get stitched, but in the case of the tile resize it is applied forced
		 * regular dimensions, while in the case of spacing it's the same dimensions as a normal tile but it's just used in order to pack the tiles together
		 * drawing them aside each other. At first the main purpose of the stitch method was to resize the tiles according to the virtual dimensions imposed,
		 * but the method has in fact another useful purpose which is removing spaces between tiles creating a new tileset and this way render the whole
		 * adjusted tileset image instead than drawing each tile aside each other each time which is less efficient.
		 * So we check the tile resize first even in the case where there are both tile resize and spacing because the spacing case only serves to
		 * remove the spaces between tiles, and this function is already done in the tile resize case while resizing each tile.
		 * In short : tile resize case has 2 functions -> resize each tile and remove the spaces
		 * 			  spacing case has 1 function -> remove the spaces
		 * Since in the spacing case we pass as tile width and height parameters the classic world/map tile width and height dimensions.
		 * 
		 * Note 2 : By the way, the stitchImage(...) method doesn't care about if the tileset image has margin or not since the stitch creates a new
		 * image using the tiles already cut according to the margin and spacing. So the stitch fulfills both the crop and space functions.
		 * Thus we should check the two conditions using stitch first then see for the last which is the margin only (meaning there is no resize or/and spacing).
		 * 
		 * Note by the way that the transparency color has already been managed during the cut by the tileset itself, since it concerns both
		 * the tileset and the in world/map tiles.
		 */
		
		//First : Stitch depending on the parameters, this takes care of both cropping and removing spaces
		if (tile_resize != null) {
			//if there is tile resize mode enabled we stitch the image according to the virtual tile resize dimensions regardless of the mode
			System.out.println("Tileresize detected, process to stitch.");
			result = stitchTiles(tileset.getTiles(), tile_resize.width, tile_resize.height, image.getType());
		} else if (!spacing.equals(new Dimension(0, 0))) { //else if is important here to not restitch again the image it's only one case to handle first prior
			//if there's a spacing and no tile resize then we need to stitch the image
			//stitch according to the classic tile dimensions meaning there's no tile resize only stitching
			System.out.println("Spacing detected, process to stitch.");
			result = stitchTiles(tileset.getTiles(), tileset.getTileSize().width, tileset.getTileSize().height, image.getType());
		} else if (!margin.equals(new Dimension(0, 0))) {
			//if there's a margin, no tile resize and no spacing then we just need to crop the image
			System.out.println("Margin detected, process to crop.");
			BufferedImage crop = new BufferedImage(image.getWidth() - margin.width, image.getHeight() - margin.height, image.getType());
			Graphics2D g2d = (Graphics2D) crop.createGraphics();
			
			g2d.drawImage(image, -margin.width, -margin.height, null); //draw the image at the top corner outside of the image to ignore the margin borders
			g2d.dispose();
			result = crop;
		}
		
		//regardless of the done process, filter the image for high quality to adapt to zoom scaling
		return result;
	}
	
	/*
	 * Note : this method should also be used in the real map/world since it will also implement zoom scaling functions
	 * 
	 * Update 18/04 : IMPORTANT NOTE : Don't need to use this costy operation anymore, in fact the real quality problem was due to applying to the graphics
	 * rendering hints that make look like mostly big images better with smoothing and so on but not on small images like tilesets that will look like blurry.
	 * This method is thus no longer used even though useful, mostly because it resizes the images in a way too big resolution considering the zoom max
	 * which shouldn't be lowered because of that. It causes some lag to render too big images (really it scales up, resizes images like 43 times bigger).
	 */
	/**
	 * Filters given image according to the max zoom to get an adapted image according to all zooms lower than the max zoom given,
	 * which means the created image has been filtered and resized to a high resolution image for high quality regardless of the zoom (<= zoom_max).
	 * This operation is costy and thus this method should be only called as needed.
	 * Default AffineTransformOp applied : TYPE_NEAREST_NEIGHBOR.
	 * @return High quality filtered and resized image according to given zoom
	 */
	public BufferedImage filterImage(BufferedImage image, float zoom_max) {
		AffineTransformOp quality_operation = new AffineTransformOp(AffineTransform.getScaleInstance(zoom_max, zoom_max), AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		/*
		 * Note : We have to use 2 different variables, the first one (original_tileset) contains the original tileset image texture it's the source image,
		 * the second one (drawn_tileset) is the drawn tileset to the tilesetpane, it has been applied a filter for a better image quality with the zoom,
		 * it's also the receiver, storer variable initialized with the filtered original tileset image. We can't use only 1 variable that serves at the same
		 * time both the storer and the original image otherwise it will filter on a already previously (for the next loops) filtered image where
		 * here the original_tileset is not modified it's kept as a static source used to be filtered creating a new image based on it then returned (a copy).
		 * Important : the second parameter is set as null for the filter method to create a zeroed compatible desktop image (see filter method javadoc),
		 * it returns then the zeroed compatible filtered image, otherwise if set to a destination image it will return a non zeroed compatible desktop image
		 * which will result in the problem of a like double zoom (into the image and into the content of the image) with a cropped texture image so the
		 * content is not shown fully.
		 * 
		 * Note 2 : Setting null as second parameter avoids the effect of a cropping image, in reality the image is not cropping it's standing to its
		 * original (from original_tileset) dimensions which means they don't change, adding the zoom scaling seems like it's visually cropping.
		 * The fact that this effect is avoided setting null as second parameter makes the drawn_tileset so the filtered result image resized according
		 * to the zoom scaling from the affine transform. So the grid has to adapt too.
		 */
		return quality_operation.filter(image, null); //this is a costy operation
	}
	
	/* Note : stitchTiles(tiles) method is only used if virtual tile resize option is enabled OR if there is spacing between tiles in the tileset image.
	 * It resizes all of the tiles (already cut) to a same size and then pack them into one big image that is the tileset,
	 * this is done in order to draw the whole image easier than several different images, this is about performance gain.
	 * There is no need to use this method for a classic tileset rendering since we can render the tileset's texture/image directly.
	 * 
	 * Note 2 : This method is particularly useful for dynamic tilesets since they have differently sized tiles, after the dynamic cut the tiles array
	 * contains all tiles with their specific size and then each one gets resized to form the virtually resized tileset to show in the tilesetpane.
	 * But this method is not recommended to be used in case of a regular tileset since the tiles in this case all have same dimensions,
	 * a per-tile resizing method like stitchTiles(...) method would not be necessary because we would only need to resize the whole tileset image/texture
	 * considering all tiles will together resize according to the specified virtual tile dimensions along with it.
	 * Example : if a tile is 20 width and 10 height for a regular tileset and we want a virtual tile resize of say 40 width and 20 height so the double,
	 * we just have to do after the cut of the tileset, number_of_tiles_in_row * (virtualtilewidth - tilewidth) + tilesetwidth to get the virtualtilesetwidth.
	 * Considering in this case virtualtilewidth > tilewidth, otherwise if virtualtilewidth < tilewidth then it's a resize down of tiles and we
	 * use tilesetwidth - (number_of_tiles_in_row * (tilewidth - virtualtilewidth)) same for height, or find something with absolute value with some maths.
	 */
	
	/**
	 * Stitch tile images into one big image forming the tileset, tiles are put aside each other making a compact image.
	 * Tiles are resized to given tile width and height parameters.
	 * @param tiles array of tiles to pack
	 * @param tile_width width of the tile
	 * @param tile_height height of the tile
	 * @param type image type, should be the type of the tile image
	 * @return BufferedImage This image is the tileset's texture image containing all of the tiles
	 */
	private static BufferedImage stitchTiles(Tile[][] tiles, int tile_width, int tile_height, int type) { //or basically getVirtuallyResizedTileset(tiles, ...)
		/*
		 * Note : This method is working using a tiles array where all tiles are already cut from the tileset that already counted the margin and padding in,
		 * the different positions of the tiles on the tileset considering the margin and padding are already set using tile.getTilesetPosition(),
		 * (also note that we don't use the getSize() method of the tile or anything else because it is virtually resized and drawn not officially)
		 * but here we recreate the whole tileset without counting in the potential margin and padding, which means we don't use the tilesetposition already set.
		 * We use instead the width and height passed as parameters in the method with the number of tiles to find the regular width and height of
		 * a tile. Thus, the tiles only contain their own tile image, we draw them aside each other
		 * without any space, this will do a packed tileset texture image that will be separated with a graphic drawn grid on top.
		 */
		int width = tile_width * tiles[0].length; //virtual tile width multiplied by number of tiles in a row
		int height = tile_height * tiles.length; //virtual tile height multiplied by number of tiles in a col
		
		BufferedImage tileset_image = new BufferedImage(width, height, type);
		
		int xpos;
		int ypos;
		
		Graphics2D g2d = tileset_image.createGraphics();
		
		for (int y = 0; y < tiles.length; y++) {
			ypos = y * tile_height;
			for (int x = 0; x < tiles[y].length; x++) {
				Tile tile = tiles[y][x];
				
				xpos = x * tile_width;
						
				//since the tileset cuts the tiles considering a potential margin,
				//we need to take that in count in order to recreate the image with correct coordinates
				g2d.drawImage(tile.getTile(), xpos, ypos, tile_width, tile_height, null);
			}
		}
		
		g2d.dispose();
		
		return tileset_image;
	}
	
	//test
	private static void main(String[] args) {
		File file_image = new File("C:\\Users\\zents\\Desktop\\maplestory.jpg");
		BufferedImage test_image = null;
		try {
			test_image = ImageIO.read(file_image);
		} catch (IOException e) {
			e.printStackTrace();
		}
		/*
		 * Note : Would need two subclasses (DynamicTileset and RegularTileset) to force pass important data through custom constructor
		 * since here I know myself to call these methods and it's written to use them in case of a regular tileset in the documentation of the class
		 * but need to do even so.
		 */
		Tileset tileset = new Tileset(file_image, TILEMODE.REGULAR, new Dimension(0, 0), new Dimension(0, 0));
		tileset.setTileSize(new Dimension(20, 10));
		System.out.println("Tileset created based on the file maplestory.jpg");
		tileset.cut();
		System.out.println("Tileset cut");
		System.out.println("Creating new image based on virtual resize dimension : (width) 40px ; (height) 20px"
				+ "\nwith a width of 20px and a height of 10px per tile");
		
		//should give a fully resized tileset to the specified virtual resize dimension
		
		BufferedImage stitched_image = stitchTiles(tileset.getTiles(), 40, 20, test_image.getType());
		
		try {
			ImageIO.write(stitched_image, "png", new File("C:\\Users\\zents\\Desktop\\maplestory2.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Virtually resized image created maplestory2.png on the desktop.");
		
		System.out.println("other test : resize the whole image to see the difference between per-tile resize and whole resize");
		BufferedImage timage = new BufferedImage(stitched_image.getWidth(), stitched_image.getHeight(), stitched_image.getType());
		Graphics2D g2d = timage.createGraphics();
		
		int twidth = (tileset.getTiles()[0].length * 40);
		int theight = (tileset.getTiles().length * 20);
		
		g2d.drawImage(test_image, 0, 0, twidth, theight, null);
		
		g2d.dispose();
		
		try {
			ImageIO.write(timage, "png", new File("C:\\Users\\zents\\Desktop\\timage.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/*
		 * Test note :
		 * Considering margin of 1, 1 :
			 * Finally, the whole image resize has better quality than the per-tile resize image which was expected,
			 * indeed the per-tile resize image and the whole resized image have the same resolution so the same pixel amount,
			 * but the second one weighs more than the first one, is heavier than the first one in size.
			 * The per-tile has like screen tearings which are the tile separations since it's per-tile resize not that easy to see.
			 * The quality loss is surely due to the pixel loss during the cut process with the margin counted in.
			 * The whole resized image doesn't have these screen tearings and has better harmony than the first one.
		 * Considering no margin :
		 	* The two images are basically the same.
		 */
	}
	
	//manage selection for one or multiple selections
	private void paintSelection(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		
		//transparency
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
		
		//NEW
		if (selected_tile != null) { //selection per tile
			float xpos = (float) selected_tile.getTilesetPosition().getX() + drawoffset;
			float ypos = (float) selected_tile.getTilesetPosition().getY() + drawoffset;
			Rectangle2D rectangle = rectangle(xpos, ypos, xpos + (float) selected_tile.getSize().getWidth(), ypos + (float) selected_tile.getSize().getHeight());
			g2d.setPaint(new Color(0, 255, 255, 150));
            g2d.draw(rectangle);
            g2d.setPaint(new Color(77, 201, 249, 150));
			g2d.fill(rectangle);
		}
		
		//OLD
		//selection preview
//		if (startDrag != null && endDrag != null) {
//			g2d.setPaint(Color.cyan);
//            Shape selection = rectangle((float) startDrag.getX(), (float) startDrag.getY(),
//            		(float) endDrag.getX(), (float) endDrag.getY());
////            g2d.draw(selection);
//            g2d.fill(selection);
//        }
		
		//actual selection needs to be modified to selet only the tiles for regular and dynamic
//        if (shape != null) {
//        	//selection frame
////        	g2d.setColor(Color.gray);
////            g2d.draw(shape);
//            //selection fill
//            g2d.setPaint(new Color(77, 201, 249));
//            g2d.fill(shape);
//        }
        
        /*
         * Personal note : here I commented out the line below, it resets the alpha transparency of the 2d graphics "g" object.
         * I did that because it is not needed since I separated my graphics management in 3 different methods (zoom first for scaling, tileset rendering
         * that is using the zoom (that is why it is after), and finally the selection at the end because the selection is drawn on top of the tiles.
         * There is nothing after that that is drawn, which means that if I set the transparency for the selection then it will only be used for it
         * since at the end nothing more is done and the paintComponent(g) method is done after that, which means no need to reset.
         * Each time paintComponent(g) method is called the object "g" is reset so the graphics are and the transparency and colors are back to normal.
         */
        
//        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
	}
	
	//Grid
	private int height, width;
	private double tile_height, tile_width;
	private BufferedImage grid_image; //buffer image for faster rendering/performance
	
	/*
	 * Note : this method is separated to avoid useless repeated calculations for the same results if put in the paintGrid(g) method.
	 */
	/**
	 * This method inits the grid coordinates, values, dimensions.
	 * To be called after the drawn_tileset variable has been initalized so as an already adjusted tileset image.
	 */
	public void initGrid() {
		//the width and height of the tile dimensions return actually the bottom right corner of the tile
		height = drawn_tileset.getHeight();
		width = drawn_tileset.getWidth();
		
		//regular tile or resized tile so every tile has the same size
		tile_height = (tile_resize != null ? tile_resize.height : tileset.getTileSize().getHeight());
		tile_width = (tile_resize != null ? tile_resize.width : tileset.getTileSize().getWidth());
		
		grid_image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D g2d = grid_image.createGraphics();
		
		g2d.setColor(Color.GRAY.darker()); //same color as background
		g2d.setStroke(new BasicStroke(0.5f)); //width size of line
		
		if (tile_resize != null || tileset.getMode() == TILEMODE.REGULAR) {
			//if virtual tile resize option is enabled or it's a regular tileset, then we draw a grid according
			//to the calculated constants (since there is no update for these specific values)
			
			//needs to count the margin in
			for (double x = tile_width; x < width; x += tile_width) {
				g2d.drawLine((int) x, 0, (int) x, (int) height);
			}
			for (double y = tile_height; y < height; y += tile_height) {
				g2d.drawLine(0, (int) y, (int) width, (int) y);
			}
		} else if (tileset.getMode() == TILEMODE.DYNAMIC) { //at this point the tileset can only be only dynamic
			//16/04 : this should work but to test it I need to finish the cutDynamic() method ...
			//for each tile draw a rectangle around
			Tile[][] tiles = tileset.getTiles();
			for (int y = 0; y < tiles.length; y++) {
				for (int x = 0; x < tiles[y].length; x++) {
					Tile tile = tiles[y][x];
					Rectangle rectangle = new Rectangle(tile.getTilesetPosition().x, tile.getTilesetPosition().y,
							(int) tile.getSize().getWidth(), (int) tile.getSize().getHeight());
					g2d.draw(rectangle);
				}
			}
		}
		g2d.dispose();
	}
	
	/*
	 * Note : The grid is drawn on top of the tileset (of the selection too?) as multiple lines.
	 * There is no need to apply getTranslatedPoint(x, y) method because there is no need to translate a screen coordinate to the world,
	 * indeed the graphic.setTransform(at) method is already used in applyZoom() method called at first so before the paint methods including this one,
	 * thus the transform is already applied to the lines so the grid.
	 * Also, the location/position or anything of the grid must not be changed since it's a static grid that separates the tiles between each other to
	 * overview all the tiles in the tileset easier, thus we need to keep the ratio of the grid so we leave it as it is drawn at first.
	 * 
	 * Update : Because of the zoom scaling the texture image is resized for a better image quality, which means the grid has to adapt its size too
	 * according to the tileset dimensions with the zoom factor.
	 */
	//grid to separate tiles drawn on top of tileset
	private void paintGrid(Graphics g) {
		g.drawImage(grid_image, drawoffset, drawoffset, null);
	}
	
	//returns the coordinates of the rectangle
	private Rectangle2D.Float rectangle(float x1, float y1, float x2, float y2) {
        return new Rectangle2D.Float(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x1 - x2), Math.abs(y1 - y2));
    }
	
	/**
	 * Sets the dimensions to resize the virtual tile shown in the tileset.
	 */
	public void setTileResize(Dimension tile_resize) {
		this.tile_resize = tile_resize;
	}
	
	private AffineTransform at;
	
	//apply zoom
	private void applyZoom(Graphics g) {
		if (tileset == null) return;
		
		Graphics2D g2d = (Graphics2D) g;
		at = new AffineTransform(); //need to be reset each call otherwise it'll zoom over the current zoom and zoom is already saved by zoom variable
		at.scale(zoom, zoom);
		
		g2d.transform(at);
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
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
//		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); //somehow the image is more blurry with these rendering hints set
//		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY); //really don't use them
//		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR); //mostly on small images
		
		applyZoom(g2d);
		loadTileset(g2d);
		if (tileset != null) {
			if (grid)
				paintGrid(g2d);
			paintSelection(g2d);
		}
	}
}
