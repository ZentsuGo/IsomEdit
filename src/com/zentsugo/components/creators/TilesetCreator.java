package com.zentsugo.components.creators;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.NumberFormatter;

import com.zentsugo.components.TilesetColorPicker;
import com.zentsugo.isomedit.IsomEdit;
import com.zentsugo.map.Tileset;
import com.zentsugo.map.Tileset.TILEMODE;
import com.zentsugo.utils.FileManager;
import com.zentsugo.utils.Utils;

public class TilesetCreator extends JDialog {
	private static final long serialVersionUID = -8285808184542067701L;
	private JTextField name_field;
	
	private JTextField location_field;
	private JTextField tileset_field;
	private JFormattedTextField marginx_field;
	private JFormattedTextField width_field;
	private JFormattedTextField height_field;
	
	private Tileset tileset;
	
	//temp
	private File save_location = null;
	private File texture_file = null;
	private BufferedImage texture = null;
	private TILEMODE mode = TILEMODE.REGULAR;
	private Color transparency = null;

	public TilesetCreator(IsomEdit isomedit) {
		getContentPane().setBackground(Color.DARK_GRAY);
		setFont(new Font("Yu Gothic UI", Font.PLAIN, 15));
		setResizable(false);
		setTitle("New Tileset");
		setModalityType(ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setPreferredSize(new Dimension(400, 605));
		getContentPane().setLayout(null);
		
		//components
		
		JPanel panel = new JPanel();
		panel.setBackground(Color.DARK_GRAY);
		panel.setBorder(new LineBorder(SystemColor.controlShadow, 1, true));
		panel.setBounds(12, 37, 370, 121);
		getContentPane().add(panel);
		panel.setLayout(null);
		
		name_field = new JTextField();
		name_field.setBackground(Color.DARK_GRAY);
		name_field.setForeground(Color.LIGHT_GRAY);
		name_field.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
		name_field.setBounds(74, 14, 284, 22);
		panel.add(name_field);
		name_field.setColumns(10);
		
		JLabel lblName = new JLabel("Name :");
		lblName.setForeground(Color.LIGHT_GRAY);
		lblName.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
		lblName.setBounds(12, 16, 56, 16);
		panel.add(lblName);
		
		location_field = new JTextField();
		location_field.setBackground(Color.DARK_GRAY);
		location_field.setForeground(Color.LIGHT_GRAY);
		location_field.setEditable(false);
		location_field.setFont(new Font("Arial", Font.PLAIN, 14));
		location_field.setBounds(12, 85, 346, 22);
		panel.add(location_field);
		location_field.setColumns(10);
		
		JLabel lblSaveLocation = new JLabel("Save location :");
		lblSaveLocation.setForeground(Color.LIGHT_GRAY);
		lblSaveLocation.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
		lblSaveLocation.setBounds(12, 54, 234, 16);
		panel.add(lblSaveLocation);
		
		JButton btnChoose = new JButton("Select");
		btnChoose.setBackground(Color.DARK_GRAY);
		btnChoose.setForeground(Color.DARK_GRAY);
		btnChoose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser(System.getProperty("user.home"));
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int result = chooser.showDialog(getInstance(), "Select");
				if (result == JFileChooser.APPROVE_OPTION) {
					File file = chooser.getSelectedFile();
					save_location = file;
					location_field.setText(file.getAbsolutePath());
				}
			}
		});
		btnChoose.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
		btnChoose.setBounds(261, 54, 97, 22);
		panel.add(btnChoose);
		
		JLabel lblProperties = new JLabel("Properties");
		lblProperties.setForeground(Color.LIGHT_GRAY);
		lblProperties.setFont(new Font("Yu Gothic UI Semibold", Font.PLAIN, 14));
		lblProperties.setBounds(12, 13, 370, 16);
		getContentPane().add(lblProperties);
		
		JLabel lblResource = new JLabel("Resource");
		lblResource.setForeground(Color.LIGHT_GRAY);
		lblResource.setFont(new Font("Yu Gothic UI Semibold", Font.PLAIN, 14));
		lblResource.setBounds(12, 171, 370, 16);
		getContentPane().add(lblResource);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new LineBorder(UIManager.getColor("Button.shadow"), 1, true));
		panel_1.setBackground(Color.DARK_GRAY);
		panel_1.setBounds(12, 195, 370, 201);
		getContentPane().add(panel_1);
		panel_1.setLayout(null);
		
		tileset_field = new JTextField();
		tileset_field.setBackground(Color.DARK_GRAY);
		tileset_field.setForeground(Color.LIGHT_GRAY);
		tileset_field.setEditable(false);
		tileset_field.setFont(new Font("Arial", Font.PLAIN, 14));
		tileset_field.setColumns(10);
		tileset_field.setBounds(12, 45, 346, 22);
		panel_1.add(tileset_field);
		
		JButton btnSelect = new JButton("Select");
		btnSelect.setBackground(Color.DARK_GRAY);
		btnSelect.setForeground(Color.DARK_GRAY);
		btnSelect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser(System.getProperty("user.home"));
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);
				chooser.addChoosableFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "png"));
				int result = chooser.showDialog(getInstance(), "Select");
				if (result == JFileChooser.APPROVE_OPTION) {
					File file = chooser.getSelectedFile();
					try {
						texture_file = file;
						texture = ImageIO.read(file);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					tileset_field.setText(file.getAbsolutePath());
				}
			}
		});
		btnSelect.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
		btnSelect.setBounds(261, 13, 97, 22);
		panel_1.add(btnSelect);
		
		JLabel lblTilesetTexture = new JLabel("Tileset texture :");
		lblTilesetTexture.setForeground(Color.LIGHT_GRAY);
		lblTilesetTexture.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
		lblTilesetTexture.setBounds(12, 16, 234, 16);
		panel_1.add(lblTilesetTexture);
		
		NumberFormat format = NumberFormat.getIntegerInstance();
    	NumberFormatter formatter = new NumberFormatter(format);
    	formatter.setValueClass(Integer.class);
    	formatter.setMinimum(0);
    	formatter.setMaximum(1000);
    	formatter.setAllowsInvalid(false);
    	formatter.setCommitsOnValidEdit(true);
    	
		marginx_field = new JFormattedTextField(formatter);
		marginx_field.setBackground(Color.DARK_GRAY);
		marginx_field.setForeground(Color.LIGHT_GRAY);
		marginx_field.setText("0");
		marginx_field.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
		marginx_field.setBounds(234, 83, 40, 22);
		panel_1.add(marginx_field);
		marginx_field.setColumns(10);
		
		JLabel lblMargin = new JLabel("Margin :");
		lblMargin.setForeground(Color.LIGHT_GRAY);
		lblMargin.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
		lblMargin.setBounds(170, 80, 71, 27);
		panel_1.add(lblMargin);
		
		JLabel lblPx = new JLabel("px");
		lblPx.setForeground(Color.LIGHT_GRAY);
		lblPx.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
		lblPx.setBounds(333, 80, 25, 27);
		panel_1.add(lblPx);
		
		width_field = new JFormattedTextField(formatter);
		width_field.setBackground(Color.DARK_GRAY);
		width_field.setForeground(Color.LIGHT_GRAY);
		width_field.setText("20");
		width_field.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
		width_field.setColumns(10);
		width_field.setBounds(89, 83, 40, 22);
		panel_1.add(width_field);
		
		JLabel label = new JLabel("px");
		label.setForeground(Color.LIGHT_GRAY);
		label.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
		label.setBounds(134, 80, 25, 27);
		panel_1.add(label);
		
		JLabel lblTileWidth = new JLabel("Tile width :");
		lblTileWidth.setForeground(Color.LIGHT_GRAY);
		lblTileWidth.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
		lblTileWidth.setBounds(12, 80, 71, 27);
		panel_1.add(lblTileWidth);
		
		height_field = new JFormattedTextField(formatter);
		height_field.setBackground(Color.DARK_GRAY);
		height_field.setForeground(Color.LIGHT_GRAY);
		height_field.setText("10");
		height_field.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
		height_field.setColumns(10);
		height_field.setBounds(89, 125, 40, 22);
		panel_1.add(height_field);
		
		JLabel label_1 = new JLabel("px");
		label_1.setForeground(Color.LIGHT_GRAY);
		label_1.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
		label_1.setBounds(134, 122, 25, 27);
		panel_1.add(label_1);
		
		JFormattedTextField marginy_field = new JFormattedTextField(formatter);
		marginy_field.setBackground(Color.DARK_GRAY);
		marginy_field.setForeground(Color.LIGHT_GRAY);
		marginy_field.setText("0");
		marginy_field.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
		marginy_field.setColumns(10);
		marginy_field.setBounds(287, 83, 40, 22);
		panel_1.add(marginy_field);
		
		JLabel lblTileHeight = new JLabel("Tile height :");
		lblTileHeight.setForeground(Color.LIGHT_GRAY);
		lblTileHeight.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
		lblTileHeight.setBounds(12, 122, 71, 27);
		panel_1.add(lblTileHeight);
		
		JCheckBox chckbxTransparentColor = new JCheckBox("Transparent color");
		chckbxTransparentColor.setForeground(Color.LIGHT_GRAY);
		chckbxTransparentColor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnColorPicker.setEnabled(chckbxTransparentColor.isSelected());
				if (chckbxTransparentColor.isSelected()) {
					transparency = Color.WHITE; //by default
				} else {
					transparency = null; //no transparency
				}
			}
		});
		chckbxTransparentColor.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
		chckbxTransparentColor.setBackground(Color.DARK_GRAY);
		chckbxTransparentColor.setBounds(12, 164, 136, 25);
		panel_1.add(chckbxTransparentColor);
		
		JPanel canvas = new JPanel();
		canvas.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		canvas.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (!chckbxTransparentColor.isSelected() || texture == null) return;
				Color color = JColorChooser.showDialog(getInstance(), "New Tileset - Transparent color", transparency);
				color = (color == null ? (transparency == null ? Color.WHITE : transparency) : color);
				canvas.setBackground(color);
				transparency = color;
			}
		});
		canvas.setEnabled(false);
		canvas.setBackground(new Color(255, 255, 255));
		canvas.setBounds(333, 164, 25, 25);
		panel_1.add(canvas);
		
		btnColorPicker = new JButton("Color picker");
		btnColorPicker.setBackground(Color.DARK_GRAY);
		btnColorPicker.setForeground(Color.DARK_GRAY);
		btnColorPicker.setEnabled(false);
		btnColorPicker.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (chckbxTransparentColor.isSelected() && texture != null) {
					TilesetColorPicker colorpicker = new TilesetColorPicker(texture);
					Color color = colorpicker.getSelectedColor();
					canvas.setBackground(color);
					transparency = color;
				}
			}
		});
		btnColorPicker.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
		btnColorPicker.setBounds(183, 164, 136, 25);
		panel_1.add(btnColorPicker);
		
		JLabel label_2 = new JLabel("px");
		label_2.setForeground(Color.LIGHT_GRAY);
		label_2.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
		label_2.setBounds(333, 122, 25, 27);
		panel_1.add(label_2);
		
		JLabel lblpadding = new JLabel("Spacing :");
		lblpadding.setForeground(Color.LIGHT_GRAY);
		lblpadding.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
		lblpadding.setBounds(170, 122, 63, 27);
		panel_1.add(lblpadding);
		
		JFormattedTextField spacingx_field = new JFormattedTextField(formatter);
		spacingx_field.setBackground(Color.DARK_GRAY);
		spacingx_field.setForeground(Color.LIGHT_GRAY);
		spacingx_field.setText("0");
		spacingx_field.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
		spacingx_field.setColumns(10);
		spacingx_field.setBounds(234, 125, 40, 22);
		panel_1.add(spacingx_field);
		
		JFormattedTextField spacingy_field = new JFormattedTextField(formatter);
		spacingy_field.setBackground(Color.DARK_GRAY);
		spacingy_field.setForeground(Color.LIGHT_GRAY);
		spacingy_field.setText("0");
		spacingy_field.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
		spacingy_field.setColumns(10);
		spacingy_field.setBounds(287, 125, 40, 22);
		panel_1.add(spacingy_field);
		
		JLabel label_3 = new JLabel(",");
		label_3.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
		label_3.setBounds(278, 91, 19, 16);
		panel_1.add(label_3);
		
		JLabel label_4 = new JLabel(",");
		label_4.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
		label_4.setBounds(278, 133, 19, 16);
		panel_1.add(label_4);
		
		JLabel lblCut = new JLabel("Cut");
		lblCut.setForeground(Color.LIGHT_GRAY);
		lblCut.setFont(new Font("Yu Gothic UI Semibold", Font.PLAIN, 14));
		lblCut.setBounds(12, 409, 370, 16);
		getContentPane().add(lblCut);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new LineBorder(UIManager.getColor("Button.shadow"), 1, true));
		panel_2.setBackground(Color.DARK_GRAY);
		panel_2.setBounds(12, 433, 370, 44);
		getContentPane().add(panel_2);
		panel_2.setLayout(null);
		
		JCheckBox chckbxRegularTileset = new JCheckBox("Regular tileset");
		chckbxRegularTileset.setForeground(Color.LIGHT_GRAY);
		chckbxRegularTileset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chckbxRegularTileset.setSelected(true);
				chckbxDynamicTileset.setSelected(false);
				width_field.setEnabled(true);
				height_field.setEnabled(true);
				mode = TILEMODE.REGULAR;
			}
		});
		chckbxRegularTileset.setSelected(true);
		chckbxRegularTileset.setBackground(Color.DARK_GRAY);
		chckbxRegularTileset.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
		chckbxRegularTileset.setBounds(35, 10, 115, 25);
		panel_2.add(chckbxRegularTileset);
		
		chckbxDynamicTileset = new JCheckBox("Dynamic tileset");
		chckbxDynamicTileset.setForeground(Color.LIGHT_GRAY);
		chckbxDynamicTileset.setEnabled(false);
		chckbxDynamicTileset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chckbxDynamicTileset.setSelected(true);
				chckbxRegularTileset.setSelected(false);
				mode = TILEMODE.DYNAMIC;
				width_field.setEnabled(false);
				height_field.setEnabled(false);
			}
		});
		chckbxDynamicTileset.setBackground(Color.DARK_GRAY);
		chckbxDynamicTileset.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
		chckbxDynamicTileset.setBounds(213, 10, 123, 25);
		panel_2.add(chckbxDynamicTileset);
		
		JButton btnCreate = new JButton("Create");
		btnCreate.setBackground(Color.DARK_GRAY);
		btnCreate.setForeground(Color.DARK_GRAY);
		btnCreate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean size = (mode == TILEMODE.DYNAMIC ? false : (width_field.getText().trim().isEmpty() ||
						height_field.getText().trim().isEmpty()));
				
				String name = name_field.getText();
				name = name.replaceFirst("^ *", "");
				String save = location_field.getText();
				save = save.replaceFirst("^ *", "");
				
				if (name.isEmpty() || (save_location == null && save.isEmpty()) || texture == null ||
						size || marginx_field.getText().trim().isEmpty() || marginy_field.getText().trim().isEmpty() ||
						spacingx_field.getText().trim().isEmpty() || spacingy_field.getText().trim().isEmpty()) {
					return;
				}
				
				if (chckbxShowTilesResized.isSelected()) {
					if (resizewidth_field.getText().trim().isEmpty() || resizeheight_field.getText().trim().isEmpty()) return;
					int rtilewidth = ((Integer) resizewidth_field.getValue()).intValue();
					int rtileheight = ((Integer) resizeheight_field.getValue()).intValue();
					if (rtilewidth <= 0 || rtilewidth <= 0) return;
					isomedit.getTilesetPane().setTileResize(new Dimension(rtilewidth, rtileheight));
				}
				
				int tilewidth = ((Integer) width_field.getValue()).intValue();
				int tileheight = ((Integer) height_field.getValue()).intValue();
				
				if (tilewidth <= 0 || tileheight <= 0) {
					return;
				}
				
				File savefile = new File(save_location.getAbsolutePath() + "/" + name + ".isoset");
				
				if (savefile.exists()) {
					int answer = JOptionPane.showConfirmDialog(null, "This map already exists, are you sure you want to overwrite it ?",
							"File already existing", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
					if (answer == JOptionPane.CANCEL_OPTION || answer == JOptionPane.CLOSED_OPTION || answer == JOptionPane.NO_OPTION) {
						return;
					}
				}
				
				tileset = new Tileset(texture_file, mode, new Dimension(((Integer) marginx_field.getValue()).intValue(), ((Integer)
						marginy_field.getValue()).intValue()),
						new Dimension(((Integer) spacingx_field.getValue()).intValue(), ((Integer) spacingy_field.getValue()).intValue()));
				
				if (chckbxTransparentColor.isSelected() && transparency != null) {
					tileset.setTransparencyColor(transparency);
				}
				
				tileset.setTileSize(new Dimension(((Integer)width_field.getValue()).intValue(), ((Integer)
						height_field.getValue()).intValue()));
				
				/*
				 * Note : The cut in fact cuts the image into tiles with the specified margin, the margin is considered as a needed space between tiles
				 * as the tileset texture/image has been created by the user using a custom grid or spaces between tiles in order to help himself.
				 * In fact, if a margin is specified and the tileset has been created not using a grid or spaces meaning that no margin is needed,
				 * this will result in giving a tileset with cropped tiles missing (margin) px in their image/texture.
				 */
				tileset.cut();
				
				tileset.setSaveFile(savefile);
//				Utils.saveSetFile(savefile, tileset); //save tileset into file
				
				System.out.println("Tileset created and imported.");
				
				dispose();
			}
		});
		btnCreate.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
		btnCreate.setBounds(147, 535, 114, 25);
		getContentPane().add(btnCreate);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.setBackground(Color.DARK_GRAY);
		btnCancel.setForeground(Color.DARK_GRAY);
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		btnCancel.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
		btnCancel.setBounds(268, 535, 114, 25);
		getContentPane().add(btnCancel);
		
		resizeheight_field = new JFormattedTextField(formatter);
		resizeheight_field.setBackground(Color.DARK_GRAY);
		resizeheight_field.setForeground(Color.LIGHT_GRAY);
		resizeheight_field.setEnabled(false);
		resizeheight_field.setText("10");
		resizeheight_field.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
		resizeheight_field.setColumns(10);
		resizeheight_field.setBounds(342, 490, 40, 22);
		getContentPane().add(resizeheight_field);
		
		resizewidth_field = new JFormattedTextField(formatter);
		resizewidth_field.setBackground(Color.DARK_GRAY);
		resizewidth_field.setForeground(Color.LIGHT_GRAY);
		resizewidth_field.setEnabled(false);
		resizewidth_field.setText("20");
		resizewidth_field.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
		resizewidth_field.setColumns(10);
		resizewidth_field.setBounds(227, 490, 40, 22);
		getContentPane().add(resizewidth_field);
		
		chckbxShowTilesResized = new JCheckBox("Resize tiles virtually");
		chckbxShowTilesResized.setForeground(Color.LIGHT_GRAY);
		chckbxShowTilesResized.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				resizeheight_field.setEnabled(chckbxShowTilesResized.isSelected());
				resizewidth_field.setEnabled(chckbxShowTilesResized.isSelected());
			}
		});
		chckbxShowTilesResized.setBackground(Color.DARK_GRAY);
		chckbxShowTilesResized.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
		chckbxShowTilesResized.setBounds(12, 488, 160, 25);
		getContentPane().add(chckbxShowTilesResized);
		
		JLabel lblWidth = new JLabel("width :");
		lblWidth.setForeground(Color.LIGHT_GRAY);
		lblWidth.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
		lblWidth.setBounds(180, 491, 50, 21);
		getContentPane().add(lblWidth);
		
		JLabel lblHeight = new JLabel("height :");
		lblHeight.setForeground(Color.LIGHT_GRAY);
		lblHeight.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
		lblHeight.setBounds(290, 491, 50, 20);
		getContentPane().add(lblHeight);
		
		pack();
		setLocationRelativeTo(null); //needs to be set after the frame has been packed for everything to be set up correctly then go to the location
		setVisible(true);
	}
	
	public Tileset getTileset() {
		return tileset;
	}
	
	private JButton btnColorPicker;
	private JFormattedTextField resizewidth_field, resizeheight_field;
	private JCheckBox chckbxShowTilesResized;
	private JCheckBox chckbxDynamicTileset;
	
	public TilesetCreator getInstance() {
		return this;
	}
}
