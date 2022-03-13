package com.zentsugo.isomedit;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;

public class Main {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try 
				{
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				UIManager.put("PopupMenu.border", new LineBorder(Color.GRAY));
				UIManager.put("MenuItem.background", Color.DARK_GRAY);
				UIManager.put("MenuItem.foreground", Color.LIGHT_GRAY);
				UIManager.put("MenuItem.opaque", true);
				UIManager.put("Menu.background", Color.DARK_GRAY);
				UIManager.put("Menu.foreground", Color.LIGHT_GRAY);
				UIManager.put("Menu.opaque", true);
				UIManager.put("MenuItem.acceleratorForeground", Color.LIGHT_GRAY);
				UIManager.put("TabbedPane.unselectedTabBackground", Color.RED);
				
				
				Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
				IsomEdit.WIDTH = size.width;
				IsomEdit.HEIGHT = size.height;
				new IsomEdit();
			}
		});
	}
}
