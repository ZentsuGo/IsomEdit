package com.zentsugo.isomedit;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

import com.zentsugo.components.ColoredMenuBar;
import com.zentsugo.components.LayersPane;
import com.zentsugo.components.MapPane;
import com.zentsugo.components.MapPane.MODE;
import com.zentsugo.components.MiniMapPane;
import com.zentsugo.components.TilesetPane;
import com.zentsugo.components.creators.LayerCreator;
import com.zentsugo.components.creators.MapCreator;
import com.zentsugo.components.creators.TilesetCreator;
import com.zentsugo.map.TileMap;
import com.zentsugo.map.Tileset;
import com.zentsugo.map.layers.Layer;
import com.zentsugo.utils.Utils;

public class IsomEdit extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private static IsomEdit instance;
	
	//CONSTANTS
	public static final String VERSION = "0.1.0"; //the first final version is 1.0.0
	
	//FULL HD at first but these fields are reset once started to the screen dimensions
	public static int WIDTH = 1920;
	public static int HEIGHT = 1080;
	
	//components
	private MiniMapPane minimap;
	private MapPane editor;
	private JToggleButton lastTool;
	
	private TilesetPane tileset;
	private LayersPane layerspane;
	
	//statics
	public static final Color COLOR = new Color(248, 248, 255);
	
	public IsomEdit() {
		setBackground(Color.DARK_GRAY);
		setTitle("IsomEdit " + VERSION);
		setPreferredSize(new Dimension(1920, 1080));
		setResizable(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		getContentPane().setBackground(Color.DARK_GRAY);
		setUndecorated(false);
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		//components
		
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		int size = WIDTH / 45;
		toolBar.setLayout(new FlowLayout());
		toolBar.setPreferredSize(new Dimension(42, 42));
		toolBar.setOrientation(SwingConstants.VERTICAL);
		toolBar.setRollover(true);
		toolBar.setFont(new Font("Yu Gothic UI", Font.PLAIN, 16));
		toolBar.setBackground(Color.DARK_GRAY);
		getContentPane().add(toolBar, BorderLayout.WEST);
		
		JToggleButton btnCursor = new JToggleButton("");
//		btnCursor.setBounds(10, 5, 25, 25);
		btnCursor.setFont(new Font("Yu Gothic UI", Font.PLAIN, 16));
		btnCursor.setToolTipText("Cursor");
		btnCursor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!btnCursor.isSelected()) return;
				updateTools(btnCursor);
				getMapPane().toggleMode(MODE.CURSOR);
			}
		});
		btnCursor.setBackground(Color.DARK_GRAY);
		btnCursor.setIcon(new ImageIcon("C:\\Users\\zents\\Desktop\\Bureau 8\\IsomEdit\\cursor.png")); //test
		btnCursor.setSelected(true);
		lastTool = btnCursor;
		toolBar.add(btnCursor);
		
		//separator
//		toolBar.addSeparator();
		
		JToggleButton btnEraser = new JToggleButton("");
		btnEraser.setIcon(new ImageIcon("C:\\Users\\zents\\Downloads\\temp icons\\pngegg.png"));
		btnEraser.setToolTipText("Eraser mode");
		btnEraser.setFont(new Font("Yu Gothic UI", Font.PLAIN, 16));
		Action action = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if (!btnEraser.isSelected()) return;
				updateTools(btnEraser);
				getMapPane().toggleMode(MODE.ERASER);
			}
		};
		btnEraser.addActionListener(action);
		btnEraser.setBackground(Color.DARK_GRAY);
		btnEraser.setMnemonic(KeyStroke.getKeyStroke(KeyEvent.VK_B, 0).getKeyCode());
		toolBar.add(btnEraser);
		
		ColoredMenuBar menuBar = new ColoredMenuBar();
//		menuBar.setBorderPainted(false);
		menuBar.setBorder(BorderFactory.createBevelBorder(0, Color.DARK_GRAY, Color.DARK_GRAY));
		menuBar.setForeground(Color.LIGHT_GRAY);
		menuBar.setBackground(Color.DARK_GRAY);
		setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		mnFile.setForeground(Color.LIGHT_GRAY);
		mnFile.setFont(new Font("Yu Gothic UI", Font.PLAIN, 17));
		mnFile.setBackground(Color.DARK_GRAY);
		menuBar.add(mnFile);
		
		JMenu mnNew = new JMenu("New");
		mnNew.setForeground(Color.LIGHT_GRAY);
		mnNew.setBackground(Color.DARK_GRAY);
		mnNew.setFont(new Font("Yu Gothic UI", Font.PLAIN, 16));
		mnFile.add(mnNew);
		
		JMenuItem btnMap = new JMenuItem("Map");
		btnMap.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TileMap map = new MapCreator(IsomEdit.this).getMap();
				if (map == null) return;
				editor.importMap(map);
				layerspane.loadMap(map);
				minimap.repaint();
			}
		});
		mnNew.add(btnMap);
		btnMap.setFont(new Font("Yu Gothic UI", Font.PLAIN, 16));
		
		JMenuItem btnTileset = new JMenuItem("Tileset");
		mnNew.add(btnTileset);
		btnTileset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Tileset set = new TilesetCreator(IsomEdit.this).getTileset();
				if (set == null) return;
				tileset.importTileset(set);
				//blocks the code until tileset is created and imported
			}
		});
		btnTileset.setFont(new Font("Yu Gothic UI", Font.PLAIN, 16));
		
		JMenu mnImport = new JMenu("Import");
		mnImport.setForeground(Color.LIGHT_GRAY);
		mnImport.setBackground(Color.DARK_GRAY);
		mnImport.setFont(new Font("Yu Gothic UI", Font.PLAIN, 16));
		mnFile.add(mnImport);
		
		JMenuItem btnImportMap = new JMenuItem("Map");
		btnImportMap.setForeground(Color.LIGHT_GRAY);
		btnImportMap.setBackground(Color.DARK_GRAY);
		btnImportMap.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser(System.getProperty("user.home"));
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				chooser.setFileFilter(new FileNameExtensionFilter("IsomEdit map (.isomap)", "isomap"));
				int result = chooser.showDialog(IsomEdit.this, "Select");
				if (result == JFileChooser.APPROVE_OPTION) {
					File file = chooser.getSelectedFile();
					TileMap map = Utils.loadMapFile(file);
					if (map == null) return;
					editor.importMap(map); //will repaint automatically
					//to update the map also with potential new updates in the texture image (for tiles)
					if (editor.getMap().getTileset() != null)
						tileset.importTileset(editor.getMap().getTileset()); //same here
					layerspane.loadMap(map);
					minimap.repaint();
				}
			}
		});
		mnImport.add(btnImportMap);
		btnImportMap.setFont(new Font("Yu Gothic UI", Font.PLAIN, 16));
		
		JMenuItem btnImportTileset = new JMenuItem("Tileset");
		btnImportTileset.setForeground(Color.LIGHT_GRAY);
		btnImportTileset.setBackground(Color.DARK_GRAY);
		btnImportTileset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser(System.getProperty("user.home"));
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				chooser.setFileFilter(new FileNameExtensionFilter("IsomEdit tileset (.isoset)", "isoset"));
				int result = chooser.showDialog(IsomEdit.this, "Select");
				if (result == JFileChooser.APPROVE_OPTION) {
					File file = chooser.getSelectedFile();
					Tileset set = Utils.loadSetFile(file);
					if (set == null) return;
					tileset.importTileset(set);
				}
			}
		});
		mnImport.add(btnImportTileset);
		btnImportTileset.setFont(new Font("Yu Gothic UI", Font.PLAIN, 16));
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setForeground(Color.GRAY);
		separator_1.setBackground(new Color(245, 245, 245));
		mnFile.add(separator_1);
		
		JMenuItem mntmExport = new JMenuItem("Save");
		mntmExport.setForeground(Color.LIGHT_GRAY);
		mntmExport.setBackground(Color.DARK_GRAY);
		mntmExport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (editor.getMap() != null)
					editor.getMap().saveMap();
				if (tileset.getTileset() != null)
					tileset.getTileset().saveSet();
			}
		});
		mntmExport.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
		mntmExport.setFont(new Font("Yu Gothic UI", Font.PLAIN, 16));
		mnFile.add(mntmExport);
		
		JMenu mnMap = new JMenu("Map");
		mnMap.setEnabled(false);
		mnMap.setForeground(Color.LIGHT_GRAY);
		mnMap.setBackground(Color.DARK_GRAY);
		mnMap.setFont(new Font("Yu Gothic UI", Font.PLAIN, 17));
		menuBar.add(mnMap);
		
		JMenuItem mntmResize = new JMenuItem("Resize");
		mntmResize.setFont(new Font("Yu Gothic UI", Font.PLAIN, 16));
		mnMap.add(mntmResize);
		
		JMenuItem mntmExpand = new JMenuItem("Expand");
		mntmExpand.setFont(new Font("Yu Gothic UI", Font.PLAIN, 16));
		mnMap.add(mntmExpand);
		
		JMenu mnTileset = new JMenu("Tileset");
		mnTileset.setEnabled(false);
		mnTileset.setForeground(Color.LIGHT_GRAY);
		mnTileset.setBackground(Color.DARK_GRAY);
		mnTileset.setFont(new Font("Yu Gothic UI", Font.PLAIN, 17));
		menuBar.add(mnTileset);
		
		JMenuItem mntmOptions = new JMenuItem("Options");
		mntmOptions.setFont(new Font("Yu Gothic UI", Font.PLAIN, 16));
		mnTileset.add(mntmOptions);
		
		JMenu mnParameters = new JMenu("Parameters");
		mnParameters.setForeground(Color.LIGHT_GRAY);
		mnParameters.setFont(new Font("Yu Gothic UI", Font.PLAIN, 17));
		mnParameters.setBackground(Color.DARK_GRAY);
		menuBar.add(mnParameters);
		
		JMenu mnShowView = new JMenu("Show view");
		mnShowView.setForeground(Color.LIGHT_GRAY);
		mnShowView.setBackground(Color.DARK_GRAY);
		mnShowView.setFont(new Font("Yu Gothic UI", Font.PLAIN, 16));
		mnParameters.add(mnShowView);
		
		JMenuItem mntmMiniMap = new JMenuItem("Mini map");
		mntmMiniMap.setFont(new Font("Yu Gothic UI", Font.PLAIN, 16));
		mnShowView.add(mntmMiniMap);
		
		JMenuItem mntmLayers = new JMenuItem("Layers");
		mntmLayers.setFont(new Font("Yu Gothic UI", Font.PLAIN, 16));
		mnShowView.add(mntmLayers);
		
		JMenuItem mntmProperties = new JMenuItem("Properties");
		mntmProperties.setFont(new Font("Yu Gothic UI", Font.PLAIN, 16));
		mnShowView.add(mntmProperties);
		
		JMenuItem mntmSettings = new JMenuItem("Settings");
		mntmSettings.setForeground(Color.LIGHT_GRAY);
		mntmSettings.setBackground(Color.DARK_GRAY);
		mntmSettings.setFont(new Font("Yu Gothic UI", Font.PLAIN, 16));
		mnParameters.add(mntmSettings);
		
		JMenuItem btnEye = new JMenuItem("Layer opacity");
		btnEye.setForeground(Color.LIGHT_GRAY);
		mnParameters.add(btnEye);
		btnEye.setIcon(new ImageIcon("C:\\Users\\zents\\Downloads\\temp icons\\32x32.png"));
		btnEye.setFont(new Font("Yu Gothic UI", Font.PLAIN, 16));
		btnEye.setToolTipText("Layer transparency");
		btnEye.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getMapPane().toggleOpacity();
				btnEye.setForeground(getMapPane().isOpacityEnabled() ? Color.yellow : Color.LIGHT_GRAY);
			}
		});
		btnEye.setBackground(Color.DARK_GRAY);
		
		//minimap
		minimap = new MiniMapPane();
		getContentPane().add(minimap);
		
		BasicSplitPaneUI ui = new BasicSplitPaneUI() {
			public BasicSplitPaneDivider createDefaultDivider() {
				return new BasicSplitPaneDivider(this) {
					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					@Override
					public void paint(Graphics g) {
						g.setColor(Color.GRAY);
						g.fillRect(0, 0, getSize().width / 3, getSize().height);
						super.paint(g);
					}
				};
			}
		};
		
		BasicSplitPaneUI ui2 = new BasicSplitPaneUI() {
			public BasicSplitPaneDivider createDefaultDivider() {
				return new BasicSplitPaneDivider(this) {
					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;
					
					@Override
					public void paint(Graphics g) {
						g.setColor(Color.GRAY);
						g.fillRect(0, 0, getSize().width / 3, getSize().height);
						super.paint(g);
					}
				};
			}
		};
		
		//main split pane
		JSplitPane splitmiddlepane = new JSplitPane();
		splitmiddlepane.setUI(ui);
		splitmiddlepane.setForeground(Color.GRAY);
		splitmiddlepane.setBackground(Color.GRAY);
		splitmiddlepane.setContinuousLayout(true);
		splitmiddlepane.setDividerSize(3);
		splitmiddlepane.setDividerLocation(WIDTH - (WIDTH / 4));
		splitmiddlepane.setBorder(BorderFactory.createEmptyBorder());
		getContentPane().add(splitmiddlepane);
		
		//right split pane of main split pane
		JSplitPane splitrightpane = new JSplitPane();
		splitrightpane.setUI(ui2);
		splitrightpane.setForeground(Color.DARK_GRAY);
		splitrightpane.setBackground(Color.GRAY);
		splitrightpane.setContinuousLayout(true);
		splitrightpane.setDividerLocation(HEIGHT - (HEIGHT / 3) - 50);
		splitrightpane.setDividerSize(3);
		splitrightpane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitrightpane.setBorder(BorderFactory.createEmptyBorder());
		splitmiddlepane.setRightComponent(splitrightpane);
		
		//tileset pane
		tileset = new TilesetPane(this);
		tileset.setOpaque(true);
		
		scrollPane = new JScrollPane(tileset, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setOpaque(true);
		scrollPane.setViewportView(tileset);
		scrollPane.getHorizontalScrollBar().setUnitIncrement(15);
		scrollPane.getVerticalScrollBar().setUnitIncrement(15);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		
		JTabbedPane tilesetsPane = new JTabbedPane(JTabbedPane.TOP);
		tilesetsPane.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
		tilesetsPane.setForeground(Color.LIGHT_GRAY);
		tilesetsPane.setBackground(Color.DARK_GRAY);
		tilesetsPane.setOpaque(true);
//		tilesetsPane.setBounds(0, 0, getPreferredSize().width, getPreferredSize().height);
		tilesetsPane.addTab("Tileset", scrollPane);
		tilesetsPane.setBackgroundAt(0, Color.YELLOW);
		tilesetsPane.setBorder(BorderFactory.createEmptyBorder());
		
		lblZoom = new JLabel("100%");
		lblZoom.setHorizontalAlignment(SwingConstants.RIGHT);
		lblZoom.setOpaque(true);
		lblZoom.setBackground(Color.GRAY.darker());
		lblZoom.setForeground(Color.LIGHT_GRAY);
		lblZoom.setFont(new Font("Yu Gothic UI Semibold", Font.PLAIN, 14));
		scrollPane.setColumnHeaderView(lblZoom);
		
		//layers pane
		layerspane = new LayersPane(this);
		
		JPanel layerscontainer = new JPanel();
		layerscontainer.setLayout(new BorderLayout());
		layerscontainer.add(layerspane, BorderLayout.CENTER);
		
		//editor
		editor = new MapPane(this);
		
		scrollPane_map = new JScrollPane(editor, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane_map.setViewportView(editor);
		scrollPane_map.getHorizontalScrollBar().setUnitIncrement(15);
		scrollPane_map.getVerticalScrollBar().setUnitIncrement(15);
		scrollPane_map.setBorder(BorderFactory.createEmptyBorder());
		
		splitmiddlepane.setLeftComponent(scrollPane_map);
		splitrightpane.setLeftComponent(tilesetsPane);
		splitrightpane.setRightComponent(layerscontainer);
		
		JPanel panel = new JPanel();
		panel.setBackground(Color.GRAY.darker());
		layerscontainer.add(panel, BorderLayout.SOUTH);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		
		JButton button = new JButton("+");
		button.setContentAreaFilled(false);
		button.setBackground(Color.DARK_GRAY);
		button.setForeground(Color.LIGHT_GRAY);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!getMapPane().isMapLoaded()) return;
				
				Layer layer = new LayerCreator(IsomEdit.this).getLayer();
				if (layer == null) return;
				
				layerspane.addLayer(layer);
			}
		});
		button.setOpaque(true);
		panel.add(button);
		
		JButton button_1 = new JButton("-");
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!getMapPane().isMapLoaded()) return;
				
				int answer = JOptionPane.showConfirmDialog(null, "Are you sure you want to remove this layer ?",
						"Layer deletion", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
				if (answer == JOptionPane.CANCEL_OPTION || answer == JOptionPane.CLOSED_OPTION || answer == JOptionPane.NO_OPTION) {
					return;
				}
				Layer layer = layerspane.getCurrentLayer();
				if (layer != null) {
					getMapPane().getMap().removeLayer(layer);
					layerspane.removeLayer(layer);
				}
			}
		});
		button_1.setBackground(Color.DARK_GRAY);
		button_1.setForeground(Color.LIGHT_GRAY);
		button_1.setContentAreaFilled(false);
		button_1.setOpaque(true);
		panel.add(button_1);
		
		pack();
		setVisible(true);
		requestFocus();
		
		instance = this;
	}
	
	public static IsomEdit getInstance() {
		return instance;
	}
	
	private void updateTools(JToggleButton exception) {
		//JToggleButton setBackground method does not work, would need to implement own JPanel and modify paint method
//		exception.setBackground(exception.isSelected() ? Color.GRAY : Color.DARK_GRAY);
		if (getCursor().getType() != Cursor.DEFAULT_CURSOR)
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		if (lastTool != null) {
			if (lastTool != exception) {
	//			lastTool.setBackground(Color.DARK_GRAY);
				lastTool.setSelected(false);
			}
		}
		lastTool = exception;
	}
	
	public void setTilesetPaneZoom(float zoom) {
		lblZoom.setText((int) (zoom * 100) + "%");
	}
	
	public void setMapPaneZoom(float zoom) {
		//...
	}
		
	private JLabel lblZoom;
	
	public JScrollPane scrollPane;
	public JScrollPane scrollPane_map;
	
	public MiniMapPane getMiniMapPane() {
		return minimap;
	}
	
	public MapPane getMapPane() {
		return editor;
	}
	
	public TilesetPane getTilesetPane() {
		return tileset;
	}
}
