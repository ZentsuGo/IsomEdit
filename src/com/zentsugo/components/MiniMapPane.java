package com.zentsugo.components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import com.zentsugo.map.layers.TileLayer;

public class MiniMapPane extends JPanel {
	private static final long serialVersionUID = -3405062714029848937L;

	public MiniMapPane() {
		setBorder(new EtchedBorder(Color.gray, Color.lightGray));
		setOpaque(false); //set transparency for minimap to be drawn on top of the map pane
		setBackground(new Color(180, 210, 255, 30)); //set the transparency color
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(getBackground()); //update each time the color to the background color first set
		g.fillRect(0, 0, getWidth(), getHeight()); //clear the screen with this color
	}
}
