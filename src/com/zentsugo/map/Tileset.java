package com.zentsugo.map;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.zentsugo.utils.ImageCollector;
import com.zentsugo.utils.Utils;

public class Tileset implements Serializable {
	
	private static final long serialVersionUID = 1L;

	public enum TILEMODE {
		REGULAR,
		DYNAMIC
	}
	
	private Color transparency = null;
	
	//properties
	private transient BufferedImage texture; //make the texture transient for it to not be serialized when saved
	private File image;
	private TILEMODE mode;
	private Dimension tile_size;
	private Dimension margin, spacing;
	
	//dynamic
	private Color boundingcolor;
	
	/**
	 * Note : The tileset texture/image will get updated when loaded back from file, this way if the image has been modified,
	 * modifications will apply directly to the map.
	 */
	private Tile[][] tiles;
	private Tile[] indexed_tiles; //indexed_tiles array to keep track of tiles and access them directly with their idx
	
	//regardless of the mode
	private int totaltiles;
	
	//save file
	private File savefile;
	
	/**
	 * Creates a new tileset based on the texture image with the tiles to be split.
	 * In case of regular tileset, you need to define the tile size with setTileSize(Dimension) method.
	 * In case of a dynamic tileset, you need to define the bounding color with setBoundingColor(Color) method.
	 * Call the cut(CUTMODE) method to initialize the tileset.
	 * @param texture Image containing the tiles, extension of file should be .jpg or .png.
	 */
	public Tileset(File image, TILEMODE mode, Dimension margin, Dimension spacing) {
		this.image = image;
		try {
			this.texture = ImageIO.read(image);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.mode = mode;
		setMargin(margin == null ? new Dimension(0, 0) : margin);
		setSpacing(spacing == null ? new Dimension(1, 1) : spacing);
	}
	
	/**
	 * Sets tile size in case of a regular tileset.
	 * @param tile_size Dimension defining the size of the regular tile.
	 */
	public void setTileSize(Dimension tile_size) {
		this.tile_size = tile_size;
		//Math.ceil(double) rounds up the result, so it returns the upper rounded integer (ex 58.8 -> 59)
		//this way the tiles will fit all the image even though the last one might be in a different size
		//this is up to the user to draw in right proportions
		int tiles_y = (int) Math.ceil((double) (spacing.height + texture.getHeight() - margin.height) / (tile_size.height + spacing.height));
		int tiles_x = (int) Math.ceil((double) (spacing.width + texture.getWidth() - margin.width) / (tile_size.width + spacing.width));
		tiles = new Tile[tiles_y][tiles_x];
		indexed_tiles = new Tile[tiles_y * tiles_x];
		System.out.println("tileset of " + tiles_x + " tiles in a row and " + tiles_y + " tiles in a col");
	}
	
	/**
	 * Gets tile size in map (world) and as shown in tileset
	 * @return Returns the tile dimension
	 */
	public Dimension getTileSize() {
		return tile_size;
	}
	
	/**
	 * Sets the bounding color to bound the tiles for a dynamic tileset.
	 * @param color
	 */
	public void setBoundingColor(Color boundingcolor) {
		this.boundingcolor = boundingcolor;
	}
	
	public Color getBoundingColor() {
		return boundingcolor;
	}
	
	/**
	 * Sets margin between the tiles in case of a regular tileset.
	 * @param margin Dimension defining the margin between the regular tiles.
	 */
	public void setMargin(Dimension margin) {
		this.margin = margin;
	}
	
	/**
	 * Gets margin between each tile in pixel
	 * @return margin dimension
	 */
	public Dimension getMargin() {
		return margin;
	}
	
	public void setSpacing(Dimension spacing) {
		this.spacing = spacing;
	}
	
	public Dimension getSpacing() {
		return spacing;
	}
	
	/**
	 * Sets the transparency color to remove in both drawn tileset and for in map/world map tiles.
	 * @param transparency Transparency color
	 */
	public void setTransparencyColor(Color transparency) {
		this.transparency = transparency;
	}
	
	// FILE //
	public void setSaveFile(File savefile) {
		this.savefile = savefile;
	}
	
	/**
	 * Saves set into the previously specified file in tileset creator.
	 */
	public void saveSet() {
		if (savefile == null) return;
		Utils.saveSetFile(savefile, this);
	}
	
	/**
	 * Returns a list of split tiles based on the image texture.
	 * @return List of tiles
	 */
	public Tile[][] getTiles() {
		return tiles;
	}
	
	public int getTotalTiles() {
		return totaltiles;
	}
	
	/**
	 * Returns the save file.
	 * @return save file
	 */
	public File getSaveFile() {
		return savefile;
	}
	
	/**
	 * Updates the texture image from the texture file, as well as all the tiles from the tileset.
	 * 
	 * Note : The tile images are parts from the texture image, they are not saved in the file for memory and time purposes,
	 * thus the process of tileset cut is redone in the update method (only to update images).
	 * 
	 * This method is supposed to be called when the tileset is being loaded from a file.
	 */
	public void update() {
		if (image == null) {
			System.err.println("Update error, image texture file is null.");
			return;
		}
		
		try {
			this.texture = ImageIO.read(image);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (this.texture == null) {
			System.err.println("Texture has not been updated, error.");
			return;
		}
		
		switch (mode) {
			case REGULAR :
				cutRegular(true); //recut with update only
				break;
			case DYNAMIC :
				break;
		}
	}
	
	/**
	 * Returns the base image texture containing non-split the tiles.
	 * @return The texture
	 */
	public BufferedImage getTexture() {
		return texture;
	}
	
	public void setTexture(BufferedImage texture) {
		this.texture = texture;
	}
	
	public TILEMODE getMode() {
		return mode;
	}
	
	public Tile getTile(int sign) {
		return indexed_tiles[sign];
	}
	
	//public Tile getTile(int index) { return tileset[index]; }
	
	/**
	 * Cuts the tileset into tiles with the selected mode.
	 * The regular mode cuts the tileset into classic regular tiles, which means according to a defined width and height of tile.
	 * The dynamic mode cuts the tileset depending on the tile size and adapts itself in order to get the best tile size.
	 * 
	 * Note that the cut() method should only be called once, if the texture or/and the margin or/and the tile size (in case of a regular mode)
	 * has/have been changed you may want to call the cut() method again to update the tileset.
	 */
	public ArrayList<Tile> cut() {
		//Regardless of the tilemode, remove the transparency color, it's not only in the drawn tileset but also for the in map/world tiles
		if (transparency != null) {
			System.out.println("Transparency color.");
			removeColor(texture, transparency);
		}
		
		switch (mode) {
			case REGULAR :
				cutRegular(false);
				totaltiles = tiles.length * tiles[0].length;
				return null;
			case DYNAMIC :
				return cutDynamic();
			default :
				return null;
		}
	}
	
	/*
	 * Note : The tileset is cut into tiles that are saved into the tileset, when exporting/saving a tileset into a file
	 * with some extension like ".ieset", it'd be basically a .zip similar file with contents in it like properties,
	 * tiles and positions...
	 */
	
	/**
	 * Note : the margin represents in fact the pixels to crop at the top and left borders of the tileset
	 * the spacing represents the space between each tile.
	 * 
	 * Update flag is used to update the tile images only,
	 * if not then all the tiles are recreated.
	 * 
	 * @param update Flag to toggle update only or not
	 */
	//regular tileset = no differently sized tiles
	private void cutRegular(boolean update) {
		int idx = 0;
		int x = 0, y = 0;
		//Cut is done from left to right and top to down
		BufferedImage tile_image = null;
		for (int yoffset = margin.height; yoffset < texture.getHeight(); yoffset += (tile_size.height + spacing.height)) {
			for (int xoffset = margin.width; xoffset < texture.getWidth(); xoffset += (tile_size.width + spacing.width)) {
				//Side note : The data array is shared between the image and the sub images, which means if anything is
				//changed in the subimage it will be modified in the main image too, but this is irrelevant here since there is
				//no direct change in the tile images only in their rendering
				
				/*
				 * Updated note : The method subImage is useful to get a portion of image but the resulted image is being defined as "unmanaged"
				 * which means that the data can't be stored into memory and we don't know the type of the image either (contains alpha ? colors etc).
				 * If we render subImages or so unmanaged images it will get extremely slow.
				 * To fix that we need to create new managed images based on the unmanaged images.
				 * We then get these new images to save them, we'll use them later.
				 */
			    tile_image = createImage(texture.getSubimage(xoffset, yoffset,
			    		Math.min((texture.getWidth() - xoffset), tile_size.width),
			    		Math.min((texture.getHeight() - yoffset), tile_size.height)));
			    
			    //whether it is an update or not we reset the image in the image collector because it is needed in both cases
			    ImageCollector.setImage(idx, tile_image);
			    
			    if (!update) {
			    	//if not update only then create new tile
//				    Tile tile = new Tile(tile_image, idx, this);
				    Tile tile = new Tile(idx, this);
				    //remove spacing offset since the tiles are going to be stitched together when being displayed
				    tile.setTilesetPosition(new Point(xoffset - (x * spacing.width), yoffset - (y * spacing.height)));
				    tiles[y][x] = tile;
				    
				    indexed_tiles[idx] = tiles[y][x]; //save tile with idx
			    }
			    
			    ++idx;
			    ++x;
			    
			    //to save into a file
			    //useful later 
			    //ImageIO.write(source.getSubimage(0, y, 32, 32), "png", new File("<sourceDir>/1fby-6t-555d_" + idx++ + ".png"));
			}
			x = 0;
			++y;
		}
	}
	
	/**
	 * Creates a new managed image with the type ARGB (managing alpha + colors)
	 * based on the given image.
	 * @param image
	 * @return new image
	 */
	private BufferedImage createImage(BufferedImage image)
	{
	    BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g = newImage.createGraphics();
	    g.drawImage(image, 0, 0, null);
	    g.dispose();
	    return newImage;
	}
	
	public static void main(String[] args) {
		File file_image = new File("C:\\Users\\zents\\Desktop\\dynamictileset.png");
		BufferedImage test_image = null;
		try {
			test_image = ImageIO.read(file_image);
		} catch (IOException e) {
			e.printStackTrace();
		}
		final BufferedImage image = test_image;
		/*
		 * Note : Would need two subclasses (DynamicTileset and RegularTileset) to force pass important data through custom constructor
		 * since here I know myself to call these methods and it's written to use them in case of a regular tileset in the documentation of the class
		 * but need to do even so.
		 */
		Tileset tileset = new Tileset(file_image, TILEMODE.DYNAMIC, new Dimension(2, 2), new Dimension(2, 2));
		tileset.setBoundingColor(Color.WHITE);
		//no transparency set
		System.out.println("Tileset created based on the file " + file_image.getName());
//		ArrayList<ArrayList<Tile>> tiles = tileset.cut();
//		System.out.println("Creating new image based on virtual resize dimension : (width) 40px ; (height) 20px"
//				+ "\nwith a width of 20px and a height of 10px per tile");
		
		ArrayList<Tile> tiles = tileset.cut();
		System.out.println("Tileset cut");
		
		System.out.println("Showing process");
		
		Color[] colors = new Color[] {Color.yellow, Color.green, Color.gray, Color.orange};
		
		JFrame frame = new JFrame("Process");
		frame.setSize(new Dimension(500, 500));
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel panel = new JPanel() {
			private static final long serialVersionUID = -6807353236649972929L;
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g;
				g2d.drawImage(image, 0, 0, null);
				
//				g2d.setColor(Color.GREEN);
				
				int y = i;
				for (HashMap<Point, Integer> map : previous_pixels) {
					g2d.setColor(colors[i - y]);
					--y;
					for (Point p : map.keySet()) {
						g2d.drawRect(p.x, p.y, 1, 1);
					}
				}
			}
		};
		frame.add(panel);
		frame.setVisible(true);
		
		for (Tile tile : tiles) {
			try {
				ImageIO.write(tile.getTile(), "png", new File("C:\\Users\\zents\\Desktop\\dynamic\\tile_" + tile.getIdx() + ".png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("All tiles saved.");
	}
	
	/*
	 * Note : The dynamic cut is particularly useful for dynamic tiles, these tiles have different sizes so dimensions and all of them
	 * are put into the same file, so this cutDynamic() method has an algorithm to cut them efficiently with some cropping to the maximum
	 * and then the tiles are cut and saved into the tileset.
	 * For the tilesetpane to show the tileset image the tilesetpane class will contain a part in the adjustTileset() method where it takes care
	 * of a dynamic tileset case, if it needs to remove only the spaces then it would be better to create a new image with the tiles already cut by this
	 * algorithm and them put them aside each other, otherwise crop if needed or virtual resize quite the same just above.
	 * 
	 * Note 2 : Now, according to the classic tile cut (cutRegular()) in the classic tileset case the tiles array which means the whole tileset containing
	 * the tiles has proportional dimensions of the array which means row*col = even number, in the case of the dynamic tileset that is not true,
	 * row*col could be equal to an odd number but also an even number, indeed since different tiles have different sizes one big tile could take
	 * (considering a regular array) 3 or more indices in both row and col, which means the tiles array in this case is irregular so dynamic too.
	 *
	 * Reminder : A tile is a rectangle which means it only has 4 sides, there is no thing in the cut dynamic such as
	 * adaptive tile with more than 4 sides for specific polygons, this is not handled by the whole isomedit program.
	 */
	private ArrayList<Tile> cutDynamic() {
		//non regular tiles, dynamic tiles
		int idx = 0;
		int width = margin.width, height = margin.height; //sum of tile sizes
		
		ArrayList<Tile> tiles = new ArrayList<Tile>();
		
		Tile tile = null;
		
//		int total = texture.getHeight() * texture.getWidth();
//		int track = 0;
//		int tilecount = 0;
//		int numberoftiles = 3;
		//check rest of the space if bounding color then no more tiles or for now
		
		while (true) {
//			Tile previous = locateTile(tiles, x);
			int w = (int) (tiles.size() == 0 ? margin.width : tiles.get(tiles.size() - 1).getTilesetPosition().x +
					tiles.get(tiles.size() - 1).getSize().getWidth() + spacing.width);
			int h = getAdjustedHeight(tiles, w);
			System.out.println("w : " + w + " // h : " + getAdjustedHeight(tiles, w));
			
			Object[] cut = cutToBounds(w, h);
			
			if (cut == null) break;
			
			tile = new Tile(idx, this);
			tile.setTilesetPosition(new Point((int) cut[1], (int) cut[2]));
			System.out.println("just set xmin " + (int) cut[1] + " and ymin " + (int) cut[2]);
			tiles.add(tile);
			
			width += tile.getTile().getWidth() + spacing.width;
//			height += tile.getTile().getHeight() + spacing.height;
//			track += tile.getTile().getWidth() + spacing.width + tile.getTile().getHeight() + spacing.height;
			
			if (width >= texture.getWidth()) {
				//reached width bound in the x axis
				width = margin.width;
			}
			
			++idx;
		}
		
		return tiles;
	}
	
	private int getAdjustedHeight(ArrayList<Tile> tiles, int x) {
		if (tiles.isEmpty()) return margin.height;
		
		int maxheight = margin.height;
		
		for (Tile t : tiles) {
			if (x >= t.getTilesetPosition().x && x <= (t.getTilesetPosition().x + t.getSize().getWidth())) {
				if (t.getTilesetPosition().y + t.getSize().getHeight() > maxheight)
					maxheight = (int) (t.getTilesetPosition().y + t.getSize().getHeight());
			}
		}
		
		return maxheight;
	}
	
	private int getAdjustedWidth(ArrayList<Tile> tiles) {
		if (tiles.isEmpty()) return margin.width;
		
		int maxwidth = margin.width;
		
		Tile previous = tiles.get(tiles.size() - 1);
		maxwidth = previous.getTilesetPosition().x + previous.getSize().width + spacing.width;
		
		if (maxwidth >= texture.getWidth()) {
			int x = margin.width + 1;
			
			Tile located = locateTile(tiles, x);
			
			while (located != null) {
				if ((previous.getTilesetPosition().y + previous.getSize().getHeight()) <
						(located.getTilesetPosition().y + located.getSize().getHeight())) {
//					x +=
				}
			}
			
			return margin.width;
		}
		
		return maxwidth;
	}
	
	private Tile locateTile(ArrayList<Tile> tiles, int x) {
		if (tiles.isEmpty()) return null;
		
		for (Tile t : tiles) {
			if (x >= t.getTilesetPosition().x && x <= (t.getTilesetPosition().x + t.getSize().getWidth())) {
				return t;
			}
		}
		
		return null;
	}
	
	/**
	 * Returns a set of object containing :
	 *  0 - BufferedImage of the tile
	 *  1 - xmin
	 *  2 - ymin
	 *  	coordinates used for tileset position
	 * @param x starter coordinate
	 * @param y starter coordinate
	 * @param bound_width left bound coordinate
	 * @param bound_height upper bound coordinate
	 * @return
	 */
	private Object[] cutToBounds(int x, int y) {
		int xmin = texture.getWidth(), ymin = texture.getHeight(), xmax = -1, ymax = -1;
		
		int remaining_spacing_x = spacing.width, remaining_spacing_y = spacing.height;
		boolean minset_x = false, minset_y = false;
		boolean done_x = false, done_y = false;
		boolean bounded = false;
		
		int bound_x = x, bound_y = y;
		previous_pixels.add(new HashMap<Point, Integer>());
		
		int[] pixels = checkPixel(texture, x, y, bound_x, bound_y);
		
		while (!bounded) { //while bounds not found
			if (minset_x && minset_y) {
				if (pixels[0] == -1) {
					--remaining_spacing_y; //restrict the bounds in the y axis to avoid going down after spacing checking useless x
				}
				if (pixels[1] == -1) {
					--remaining_spacing_x;
				}
			}
			
			if (pixels[0] != -1) {
				if (xmin > pixels[0]) {
					xmin = pixels[0];
					if (!minset_x)
						minset_x = true;
				}
			}
			if (pixels[1] != -1) {
				if (ymin > pixels[1]) {
					ymin = pixels[1];
					if (!minset_y)
						minset_y = true;
				}
			}
			
			if (remaining_spacing_x <= 0) {
				xmax = x - (spacing.width - 1); //set xmax to x found before spacing
			} else {
				if (x + 1 < texture.getWidth())
					++x;
				else
					done_x = true;
			}
			
			if (remaining_spacing_y <= 0) {
				ymax = y - (spacing.height - 1); //subtract 1 to spacing so add 1 to ymax and xmax to get bottom right pixel
				//to take the whole tile to cut
			} else {
				if (y + 1 < texture.getHeight())
					++y;
				else
					done_y = true;
			}
			
			if ((xmax != - 1 && ymax != -1) || (done_x && done_y))
				bounded = true; //bounds set
			
			pixels = checkPixel(texture, x, y, bound_x, bound_y);
		}
		
		if (done_x && done_y) {
			System.out.println("Reached end of the file.");
			return null;
		}
		
		++i;
		
		System.out.println("Tile cut : (" + xmin + ", " + ymin + ", " + (xmax - margin.width) + ", " + (ymax - margin.height) + ")");
		BufferedImage tile = createImage(texture.getSubimage(xmin, ymin, xmax - margin.width, ymax - margin.height));
		//subtract the margin to get correct width and height dimensions starting from xmin and ymin (considered 0,0)
		return new Object[] {tile, xmin, ymin};
	}
	
	private static ArrayList<HashMap<Point, Integer>> previous_pixels = new ArrayList<HashMap<Point, Integer>>();
	
	/**
	 * Returns the two last pixels (if found) before max bounds reached in both x and y axis.
	 * @param image
	 * @param starter_x
	 * @param starter_y
	 * @return last pixels found, null if not found
	 */
	static int i = 0;
	private int[] checkPixel(BufferedImage image, int starter_x, int starter_y, int max_x, int max_y) {
		//pixels[0] : x axis
		//pixels[1] : y axis
		int[] pixels = new int[2];
		pixels[0] = -1;
		pixels[1] = -1;
		
		for (int x = starter_x; x >= max_x; x--) { //>= important !! for the loop to check at least the first pixel for all coords
			if (image.getRGB(x, starter_y) != boundingcolor.getRGB()) {
				pixels[0] = x; //if pixel found save it
			}
			previous_pixels.get(i).put(new Point(x, starter_y), image.getRGB(x, starter_y));
		}
		
		for (int y = starter_y; y >= max_y; y--) { //same here with >=
			if (image.getRGB(starter_x, y) != boundingcolor.getRGB())
				pixels[1] = y;
			previous_pixels.get(i).put(new Point(starter_x, y), image.getRGB(starter_x, y));
		}
		
		return pixels;
	}
	
	/*
	 * Note : This method is only useful for a single color as it doesn't manage the color variations with translucency 
	 */
	//used for transparency color
	private void removeColor(BufferedImage image, Color color) {
		Color transparent = new Color(0, 0, 0, 0);
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				//going through each pixel of the image
				if (image.getRGB(x, y) == color.getRGB()) {
					//if color from the image matches the given color then set it to the transparent pixel color
					image.setRGB(x, y, transparent.getRGB());
				}
			}
		}
	}
}
