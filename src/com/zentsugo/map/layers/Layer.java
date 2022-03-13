package com.zentsugo.map.layers;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.Serializable;

import com.zentsugo.map.TileMap;

public abstract class Layer implements Serializable {
	private static final long serialVersionUID = 1L;
	
	protected String name;
	protected TileMap map;
	
	public Layer(String name, TileMap map) {
		this.name = name;
		this.map = map;
	}
	
	public abstract void draw(Graphics2D g);
	public abstract String getName();
	
	@Override
	public String toString() {
		return "[" + getClass().getSimpleName() + "] " + name;
	}
}
