package com.zentsugo.components.creators;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import com.zentsugo.isomedit.IsomEdit;
import com.zentsugo.map.layers.Layer;
import com.zentsugo.map.layers.TileLayer;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class LayerCreator extends JDialog {
	private static final long serialVersionUID = 1L;
	
	private IsomEdit isomedit;
	
	private JTextField namefield;
	private JRadioButton tilelayer;
	private JRadioButton freelayer;
	private JRadioButton objectlayer;
	
	private Layer layer;
	private int layertype = 0; //0 = tile layer; 1 = object layer; 2 = free layer by default 0 for the tile layer
	
	public LayerCreator(IsomEdit isomedit) {
		this.isomedit = isomedit;
		
		getContentPane().setBackground(Color.DARK_GRAY);
		setFont(new Font("Yu Gothic UI", Font.PLAIN, 15));
		setResizable(false);
		setTitle("New Layer");
		setModalityType(ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setPreferredSize(new Dimension(450, 190));
		getContentPane().setLayout(null);
		
		namefield = new JTextField();
		namefield.setBounds(12, 48, 420, 22);
		getContentPane().add(namefield);
		namefield.setColumns(10);
		
		JLabel lblLayersName = new JLabel("Layer's name :");
		lblLayersName.setForeground(Color.LIGHT_GRAY);
		lblLayersName.setBackground(Color.DARK_GRAY);
		lblLayersName.setFont(new Font("Yu Gothic UI Semibold", Font.PLAIN, 15));
		lblLayersName.setBounds(176, 13, 110, 22);
		getContentPane().add(lblLayersName);
		
		tilelayer = new JRadioButton("Tile Layer");
		tilelayer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tilelayer.setSelected(true);
				
				layertype = 0;
				disableOthers();
			}
		});
		tilelayer.setSelected(true);
		tilelayer.setToolTipText("Build your map with tiles");
		tilelayer.setFont(new Font("Yu Gothic UI", Font.PLAIN, 13));
		tilelayer.setForeground(Color.LIGHT_GRAY);
		tilelayer.setBackground(Color.DARK_GRAY);
		tilelayer.setBounds(12, 79, 127, 25);
		getContentPane().add(tilelayer);
		
		freelayer = new JRadioButton("Free Layer");
		freelayer.setEnabled(false);
		freelayer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				freelayer.setSelected(true);
				
				layertype = 2; //set type before disable others since the method is based on it
				disableOthers();
			}
		});
		freelayer.setToolTipText("Markers, informations, free drawing");
		freelayer.setFont(new Font("Yu Gothic UI", Font.PLAIN, 13));
		freelayer.setForeground(Color.LIGHT_GRAY);
		freelayer.setBackground(Color.DARK_GRAY);
		freelayer.setBounds(305, 79, 127, 25);
		getContentPane().add(freelayer);
		
		objectlayer = new JRadioButton("Object Layer");
		objectlayer.setEnabled(false);
		objectlayer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				objectlayer.setSelected(true);
				
				layertype = 1;
				disableOthers();
			}
		});
		objectlayer.setToolTipText("Place objects wherever you want");
		objectlayer.setFont(new Font("Yu Gothic UI", Font.PLAIN, 13));
		objectlayer.setForeground(Color.LIGHT_GRAY);
		objectlayer.setBackground(Color.DARK_GRAY);
		objectlayer.setBounds(159, 79, 127, 25);
		getContentPane().add(objectlayer);
		
		JButton button = new JButton("Create");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name = namefield.getText();
				name = name.replaceFirst("^ *", "");
				
				if (name.isEmpty()) return;
				
				switch (layertype) {
					case 0 :
						layer = new TileLayer(name, isomedit.getMapPane().getMap());
						break;
					case 1 :
						layer = new TileLayer(name, isomedit.getMapPane().getMap());
						break;
					case 2 :
						layer = new TileLayer(name, isomedit.getMapPane().getMap());
						break;
				}
				dispose();
			}
		});
		button.setForeground(Color.DARK_GRAY);
		button.setBackground(Color.DARK_GRAY);
		button.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
		button.setBounds(197, 117, 114, 25);
		getContentPane().add(button);
		
		JButton button_1 = new JButton("Cancel");
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		button_1.setForeground(Color.DARK_GRAY);
		button_1.setBackground(Color.DARK_GRAY);
		button_1.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
		button_1.setBounds(318, 117, 114, 25);
		getContentPane().add(button_1);
		
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	public Layer getLayer() {
		return layer;
	}
	
	private void disableOthers() {
		switch (layertype) {
			case 0 :
				freelayer.setSelected(false);
				objectlayer.setSelected(false);
				break;
			case 1 :
				tilelayer.setSelected(false);
				freelayer.setSelected(false);
				break;
			case 2 :
				tilelayer.setSelected(false);
				objectlayer.setSelected(false);
				break;
		}
	}
}
