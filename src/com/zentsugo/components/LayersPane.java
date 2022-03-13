package com.zentsugo.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.zentsugo.isomedit.IsomEdit;
import com.zentsugo.map.TileMap;
import com.zentsugo.map.layers.Layer;

/**
 * The LayersPane is a pane managing exchanges between the layers of the current map loaded and the layers pane where the user can interact,
 * the indices of the layers are the rendering order and the list contains the layers themselves.
 * @author ZentsuGo
 *
 */
public class LayersPane extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private IsomEdit isomedit;
	private TileMap map;
	private DefaultListModel<Layer> listModel;
	private JList<Layer> list;
	
	public LayersPane(IsomEdit isomedit) {
		this.isomedit = isomedit;
		setBackground(Color.GRAY.darker());
		setLayout(new BorderLayout(0, 0));
		
		listModel = new DefaultListModel<Layer>();
		
		list = new JList<Layer>(listModel);
		//selection of the current layer of the map
		list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting()) return;
				
				map.setCurrentLayer(getCurrentLayer());
				isomedit.getMapPane().repaint(isomedit.getMapPane().getVisibleRect());
			}
		});
		
		list.setBackground(Color.GRAY.darker());
		list.setForeground(Color.LIGHT_GRAY);
		list.setCellRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = 1L;

			@Override
		     public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		         Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		         if (isSelected) {
		             c.setBackground(Color.GRAY);
		         }
		         return c;
		     }
		});
		list.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setSelectedIndex(-1);
		
		JScrollPane scrollpane = new JScrollPane(list);
		scrollpane.setBorder(BorderFactory.createEmptyBorder());
		
		add(scrollpane);
	}
	
	//MAP
	public void loadMap(TileMap map) {
		this.map = map;
		
		listModel.clear();
		list.clearSelection();
		
		if (map.getLayers().isEmpty()) return;
		
		for (Layer l : map.getLayers()) {
			listModel.addElement(l);
		}
		list.setSelectedValue(map.getCurrentLayer(), true);
	}
	
	//LAYER
	public void addLayer(Layer layer) {
		if (map == null) return;
		map.addLayer(layer);
		listModel.addElement(layer);
		list.setSelectedIndex(listModel.getSize() - 1);
	}
	
	public Layer getCurrentLayer() {
		if (map.getLayers().isEmpty()) return null;
		if (list.getSelectedIndex() < 0 || list.getSelectedIndex() >= map.getLayers().size()) return null;
		return map.getLayers().get(list.getSelectedIndex());
	}
	
	public void removeLayer(Layer layer) {
		if (map == null) return;
		map.removeLayer(layer);
		listModel.removeElement(layer);
	}
}
