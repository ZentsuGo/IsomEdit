package com.zentsugo.components;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;

import com.zentsugo.isomedit.IsomEdit;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseWheelListener;
import java.awt.event.MouseWheelEvent;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import java.awt.CardLayout;
import javax.swing.SwingConstants;
import javax.swing.BoxLayout;

public class TilesetColorPicker extends JDialog {
	private static final long serialVersionUID = 2651620902433265576L;
	
	private Color color;
	
	//scroll
	private boolean drag = false;
	private Point first;
	
	private JPanel panel;
	
	public TilesetColorPicker(BufferedImage texture) {
		setAlwaysOnTop(true);
		setResizable(false);
		getContentPane().setBackground(IsomEdit.COLOR);
		setFont(new Font("Yu Gothic UI", Font.PLAIN, 15));
		setTitle("New Tileset - Color Picker");
		setModalityType(ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		//should do a layout
		
		MouseAdapter adapter = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				//move through the panel
				if (e.getButton() == MouseEvent.BUTTON3) {
					first = new Point(e.getX(), e.getY());
					drag = true;
				}
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				//move through the panel
				if (e.getButton() == MouseEvent.BUTTON3 && drag)
					drag = false;
			}
			
			@Override
            public void mouseDragged(MouseEvent e) {
            	//move through the panel
            	if (drag) {
            		JViewport viewport = (JViewport) SwingUtilities.getAncestorOfClass(JViewport.class, panel);
            		if (viewport != null) {
	            		if (first != null) {
	            			int deltaX = first.x - e.getX();
	                        int deltaY = first.y - e.getY();
	
	                        Rectangle view = viewport.getViewRect();
	                        view.x += deltaX;
	                        view.y += deltaY;
	                        
	                        panel.scrollRectToVisible(view);
	            		}
            		}
            	}
            }   
        };
		
		panel = new JPanel() {
			private static final long serialVersionUID = -2578435834004484544L;

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g;
				
				applyZoom(g);
				
				g2d.drawImage(texture, 0, 0, null);
				panel.setPreferredSize(new Dimension(
						Math.max((int) (texture.getWidth() * zoom), (int) 600), Math.max((int) (texture.getHeight() * zoom), (int) 600)));
				revalidate();
			}
		};
		
		panel.addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
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
					panel.repaint();
				}
			}
		});
		
		panel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Point2D origin = getTranslatedPoint(0, 0);
				Point2D limit = new Point(texture.getWidth(), texture.getHeight());
				Point2D coords = getTranslatedPoint(e.getX(), e.getY());
				if (coords == null) return;
				
				if (coords.getX() < origin.getX() || coords.getX() > limit.getX() || coords.getY() < origin.getY() || coords.getY() > limit.getY())
					return;
				
				color = new Color(texture.getRGB(Math.min((int) coords.getX(), texture.getWidth() - 1),
						Math.min((int) coords.getY(), texture.getHeight() - 1)));
				dispose();
			}
		});
		
		panel.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				Point2D origin = new Point(0, 0);
				Point2D limit = new Point(texture.getWidth(), texture.getHeight());
				Point2D coords = getTranslatedPoint(e.getX(), e.getY());
				if (coords == null) return;
				
				if (coords.getX() < origin.getX() || coords.getX() > limit.getX() || coords.getY() < origin.getY() || coords.getY() > limit.getY())
					return;
				
				Color color = new Color(texture.getRGB(Math.min((int) coords.getX(), texture.getWidth() - 1),
						Math.min((int) coords.getY(), texture.getHeight() - 1)));
				colorpanel.setBackground(color);
				colorlabel.setText("(" + color.getRed() + ", " + color.getGreen() + ", " + color.getBlue() + ", " + color.getAlpha() + ")");
			}
		});
		
		panel.addMouseMotionListener(adapter);
		panel.addMouseListener(adapter);
		panel.setPreferredSize(new Dimension(450, 400));
		
		panel.setBackground(new Color(114, 114, 113));
		getContentPane().add(panel);
		
		JScrollPane scrollPane = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setViewportView(panel);
		getContentPane().add(scrollPane);
		
		labelspace = new JLabel(" ");
		getContentPane().add(labelspace);
		
		colorlabel = new JLabel("(0, 0, 0, 1)");
		colorlabel.setFont(new Font("Yu Gothic UI Semilight", Font.PLAIN, 14));
		colorlabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		colorpanel = new JPanel();
		colorpanel.setBackground(Color.WHITE);
		colorpanel.setBorder(new LineBorder(Color.LIGHT_GRAY, 1, true));
		colorpanel.setLayout(new CardLayout(0, 0));
		colorpanel.add(colorlabel, "name_36764862574600");
		colorpanel.setBounds(610, 0, 190, 500);
		
		getContentPane().add(colorpanel);
		
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));
		
		setPreferredSize(new Dimension(Math.max(800, texture.getWidth() + 80), Math.max(600, texture.getHeight() + 80)));
		
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		
		at = new AffineTransform();
	}
	
	private JPanel colorpanel;
	private JLabel colorlabel;
	
	private AffineTransform at;
	private float zoom = 1f;
	private float zoom_max = 10f;
	private float zoom_min = 0.01f;
	private JLabel labelspace;
	
	//apply zoom
	private void applyZoom(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		
		float dzoom = Math.min(zoom_max, Math.abs(zoom));
		at = new AffineTransform();
		at.scale(dzoom, dzoom);
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
	
	public Color getSelectedColor() {
		return color;
	}
}
