package com.zentsugo.components.creators;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.text.NumberFormatter;

import com.zentsugo.isomedit.IsomEdit;
import com.zentsugo.map.TileMap;
import com.zentsugo.utils.Utils;

public class MapCreator extends JDialog {
	private static final long serialVersionUID = 1L;
	
	private IsomEdit isomedit;
	private TileMap map;
	private JTextField txtField_name;
	private JTextField txtField_save;
	private File save_location;
	
	public MapCreator(IsomEdit isomedit) {
		this.isomedit = isomedit;
		getContentPane().setBackground(Color.DARK_GRAY);
		setFont(new Font("Yu Gothic UI", Font.PLAIN, 15));
		setResizable(false);
		setTitle("New Map");
		setModalityType(ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setPreferredSize(new Dimension(400, 420));
		getContentPane().setLayout(null);
		
		JLabel lblMap = new JLabel("Properties");
		lblMap.setForeground(Color.LIGHT_GRAY);
		lblMap.setFont(new Font("Yu Gothic UI Semibold", Font.PLAIN, 14));
		lblMap.setBounds(12, 13, 370, 16);
		getContentPane().add(lblMap);
		
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBorder(new LineBorder(SystemColor.controlShadow, 1, true));
		panel.setBackground(Color.DARK_GRAY);
		panel.setBounds(12, 37, 370, 297);
		getContentPane().add(panel);
		
		txtField_name = new JTextField();
		txtField_name.setForeground(Color.DARK_GRAY);
		txtField_name.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
		txtField_name.setColumns(10);
		txtField_name.setBounds(74, 14, 284, 22);
		panel.add(txtField_name);
		
		JLabel label = new JLabel("Name :");
		label.setForeground(Color.LIGHT_GRAY);
		label.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
		label.setBounds(12, 16, 56, 16);
		panel.add(label);
		
		txtField_save = new JTextField();
		txtField_save.setForeground(Color.DARK_GRAY);
		txtField_save.setEditable(false);
		txtField_save.setFont(new Font("Arial", Font.PLAIN, 14));
		txtField_save.setColumns(10);
		txtField_save.setBounds(12, 85, 346, 22);
		panel.add(txtField_save);
		
		JLabel label_1 = new JLabel("Save location :");
		label_1.setForeground(Color.LIGHT_GRAY);
		label_1.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
		label_1.setBounds(12, 54, 234, 16);
		panel.add(label_1);
		
		JButton button = new JButton("Select");
		button.setBackground(Color.DARK_GRAY);
		button.setForeground(Color.DARK_GRAY);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser(System.getProperty("user.home"));
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int result = chooser.showDialog(MapCreator.this, "Select");
				if (result == JFileChooser.APPROVE_OPTION) {
					File file = chooser.getSelectedFile();
					save_location = file;
					txtField_save.setText(file.getAbsolutePath());
				}
			}
		});
		button.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
		button.setBounds(261, 54, 97, 22);
		panel.add(button);
		
		JLabel lblMap_1 = new JLabel("Map :");
		lblMap_1.setForeground(Color.LIGHT_GRAY);
		lblMap_1.setFont(new Font("Yu Gothic UI Semibold", Font.PLAIN, 14));
		lblMap_1.setBounds(12, 120, 56, 16);
		panel.add(lblMap_1);
		
		JLabel label_2 = new JLabel("Tile width :");
		label_2.setForeground(Color.LIGHT_GRAY);
		label_2.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
		label_2.setBounds(12, 149, 71, 27);
		panel.add(label_2);
		
		NumberFormat format = NumberFormat.getIntegerInstance();
    	NumberFormatter formatter = new NumberFormatter(format);
    	formatter.setValueClass(Integer.class);
    	formatter.setMinimum(0);
    	formatter.setMaximum(1000);
    	formatter.setAllowsInvalid(false);
    	formatter.setCommitsOnValidEdit(true);
		
		JFormattedTextField tileWidth = new JFormattedTextField(formatter);
		tileWidth.setBackground(Color.DARK_GRAY);
		tileWidth.setForeground(Color.LIGHT_GRAY);
		tileWidth.setText("20");
		tileWidth.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
		tileWidth.setColumns(10);
		tileWidth.setBounds(89, 152, 40, 22);
		panel.add(tileWidth);
		
		JLabel label_3 = new JLabel("Tile height :");
		label_3.setForeground(Color.LIGHT_GRAY);
		label_3.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
		label_3.setBounds(12, 191, 71, 27);
		panel.add(label_3);
		
		JFormattedTextField tileHeight = new JFormattedTextField(formatter);
		tileHeight.setBackground(Color.DARK_GRAY);
		tileHeight.setForeground(Color.LIGHT_GRAY);
		tileHeight.setText("10");
		tileHeight.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
		tileHeight.setColumns(10);
		tileHeight.setBounds(89, 194, 40, 22);
		panel.add(tileHeight);
		
		JLabel label_4 = new JLabel("px");
		label_4.setForeground(Color.LIGHT_GRAY);
		label_4.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
		label_4.setBounds(134, 149, 25, 27);
		panel.add(label_4);
		
		JLabel label_5 = new JLabel("px");
		label_5.setForeground(Color.LIGHT_GRAY);
		label_5.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
		label_5.setBounds(134, 191, 25, 27);
		panel.add(label_5);
		
		JLabel lblWidth = new JLabel("Width :");
		lblWidth.setForeground(Color.LIGHT_GRAY);
		lblWidth.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
		lblWidth.setBounds(170, 149, 71, 27);
		panel.add(lblWidth);
		
		JLabel lblHeight = new JLabel("Height :");
		lblHeight.setForeground(Color.LIGHT_GRAY);
		lblHeight.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
		lblHeight.setBounds(170, 191, 63, 27);
		panel.add(lblHeight);
		
		JFormattedTextField width = new JFormattedTextField(formatter);
		width.setBackground(Color.DARK_GRAY);
		width.setForeground(Color.LIGHT_GRAY);
		width.setText("10");
		width.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
		width.setColumns(10);
		width.setBounds(230, 152, 40, 22);
		panel.add(width);
		
		JFormattedTextField height = new JFormattedTextField(formatter);
		height.setBackground(Color.DARK_GRAY);
		height.setForeground(Color.LIGHT_GRAY);
		height.setText("10");
		height.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
		height.setColumns(10);
		height.setBounds(230, 194, 40, 22);
		panel.add(height);
		
		JLabel lblTiles_1 = new JLabel("tiles");
		lblTiles_1.setForeground(Color.LIGHT_GRAY);
		lblTiles_1.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
		lblTiles_1.setBounds(275, 191, 25, 27);
		panel.add(lblTiles_1);
		
		JLabel lblTiles = new JLabel("tiles");
		lblTiles.setForeground(Color.LIGHT_GRAY);
		lblTiles.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
		lblTiles.setBounds(275, 149, 25, 27);
		panel.add(lblTiles);
		
		JLabel origin_label = new JLabel("x :");
		origin_label.setForeground(Color.LIGHT_GRAY);
		origin_label.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
		origin_label.setBounds(12, 257, 25, 27);
		panel.add(origin_label);
		
		JFormattedTextField origin_x = new JFormattedTextField(formatter);
		origin_x.setBackground(Color.DARK_GRAY);
		origin_x.setForeground(Color.LIGHT_GRAY);
		origin_x.setText("0");
		origin_x.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
		origin_x.setColumns(10);
		origin_x.setBounds(37, 260, 40, 22);
		panel.add(origin_x);
		
		JLabel lblTiles_2 = new JLabel("tiles");
		lblTiles_2.setForeground(Color.LIGHT_GRAY);
		lblTiles_2.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
		lblTiles_2.setBounds(83, 257, 25, 27);
		panel.add(lblTiles_2);
		
		JLabel lblY = new JLabel("y :");
		lblY.setForeground(Color.LIGHT_GRAY);
		lblY.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
		lblY.setBounds(130, 257, 25, 27);
		panel.add(lblY);
		
		JFormattedTextField origin_y = new JFormattedTextField(formatter);
		origin_y.setBackground(Color.DARK_GRAY);
		origin_y.setForeground(Color.LIGHT_GRAY);
		origin_y.setText("0");
		origin_y.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
		origin_y.setColumns(10);
		origin_y.setBounds(155, 260, 40, 22);
		panel.add(origin_y);
		
		JLabel label_9 = new JLabel("tiles");
		label_9.setForeground(Color.LIGHT_GRAY);
		label_9.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
		label_9.setBounds(202, 257, 25, 27);
		panel.add(label_9);
		
		JLabel lblOrigin = new JLabel("Origin of the world :");
		lblOrigin.setForeground(Color.LIGHT_GRAY);
		lblOrigin.setFont(new Font("Yu Gothic UI Semibold", Font.PLAIN, 14));
		lblOrigin.setBounds(12, 228, 346, 19);
		panel.add(lblOrigin);
		
		JButton cancelBtn = new JButton("Cancel");
		cancelBtn.setBackground(Color.DARK_GRAY);
		cancelBtn.setForeground(Color.DARK_GRAY);
		cancelBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		cancelBtn.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
		cancelBtn.setBounds(268, 347, 114, 25);
		getContentPane().add(cancelBtn);
		
		JButton createBtn = new JButton("Create");
		createBtn.setBackground(Color.DARK_GRAY);
		createBtn.setForeground(Color.DARK_GRAY);
		createBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name = txtField_name.getText();
				String save = txtField_save.getText();
				
				name = name.replaceFirst("^ *", ""); //remove all the first spaces
				save = save.replaceFirst("^ *", "");
				
				if (name.isEmpty() || save.isEmpty() || save_location == null || width.getText().trim().isEmpty() ||
						height.getText().trim().isEmpty()) {
					return;
				}
				
				int mapwidth = ((Integer) width.getValue()).intValue();
				int mapheight = ((Integer) height.getValue()).intValue();
				int tilewidth = ((Integer) tileWidth.getValue()).intValue();
				int tileheight = ((Integer) tileHeight.getValue()).intValue();
				int originx = ((Integer) origin_x.getValue()).intValue();
				int originy = ((Integer) origin_y.getValue()).intValue();
				
				if (mapwidth <= 0 || mapheight <= 0 || tilewidth <= 0 || tileheight <= 0 ||
						originx < 0 || originy < 0) {
					return;
				}
				
				File savefile = new File(save_location.getAbsolutePath() + "/" + name + ".isomap");
				
				if (savefile.exists()) {
					int answer = JOptionPane.showConfirmDialog(null, "This map already exists, are you sure you want to overwrite it ?",
							"File already existing", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
					if (answer == JOptionPane.CANCEL_OPTION || answer == JOptionPane.CLOSED_OPTION || answer == JOptionPane.NO_OPTION) {
						return;
					}
				}
				
				map = new TileMap(name, new Dimension(mapwidth, mapheight),
						new Dimension(tilewidth, tileheight)); //create the map
				
				if (((long) (mapwidth * mapheight * 4) / Math.pow(10, 6) + 100) >= Runtime.getRuntime().freeMemory()) {
					JOptionPane.showConfirmDialog(null, "Heap space error, there is not enough memory for this map.",
							"Out of space", JOptionPane.OK_OPTION, JOptionPane.PLAIN_MESSAGE);
					return;
				}
				
				map.setOrigin(originx, originy); //set the world origin of the map
				
				map.setSaveFile(savefile);
//				Utils.saveMapFile(savefile, map); //save the map into file
				
				System.out.println("Map created and imported.\nMap data : dimensions : (" + mapwidth + ", " + mapheight + ")"
						+ "\norigin (" + originx + ", " + originy + ")\ntilesize : (" + tilewidth + ", " +
						tileheight + ")");
				
				dispose();
			}
		});
		createBtn.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
		createBtn.setBounds(147, 347, 114, 25);
		getContentPane().add(createBtn);
		
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	public TileMap getMap() {
		return map;
	}
}
