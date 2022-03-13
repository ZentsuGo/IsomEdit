package com.zentsugo.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class tests {
	public static void maines(String[] args) {
//		String[] a = {"a", "c", "x"};
//		String[] b = {"b", "c", "d", "x"};
//		
//		System.out.println("count : " + commonTwo(a, b));
		for (int i = 0; i < 1; i++) {
			System.out.println(i);
		}
	}
	
	static class A {
		A a;
		String c;
		public A(String c) {
			this.c = c;
		}
	}
	
	public static void main(String[] args) {
		A alright = new A("alright");
		A a = new A(alright.c);
		
		System.out.println("alright : " + alright.c);
		System.out.println("a.c : " + a.c);
		
		alright.c = "manas";
		System.out.println("change");
		
		System.out.println("alright : " + alright.c);
		System.out.println("a.c : " + a.c);
	}
	
	public static void mainq(String[] args) {
		JFrame frame = new JFrame();
		frame.setPreferredSize(new Dimension(1920, 1080));
		JPanel panel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.setColor(Color.black);
				g.drawString("hello", 100, 100);
			}
			
		};
		panel.setBackground(Color.LIGHT_GRAY);
		frame.setContentPane(panel);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		panel.addMouseListener(new MouseAdapter() {
			private void mousemoved() {
				panel.repaint();
			}
			@Override
			public void mouseDragged(MouseEvent e) {
				panel.repaint();
			}
			@Override
			public void mousePressed(MouseEvent e) {
				panel.repaint();
			}
		});
	}
	
	public static void maina(String[] args) {
		Rectangle r = new Rectangle(5, 2);
		Rectangle b = r;
		System.out.println("r.x : " + r.x + " and r.y : " + r.y);
		System.out.println("b.x : " + b.x + " and b.y : " + b.y);
		r.x = 3;
		System.out.println("r.x : " + r.x + " and r.y : " + r.y);
		System.out.println("b.x : " + b.x + " and b.y : " + b.y);
		b.y = -1;
		System.out.println("r.x : " + r.x + " and r.y : " + r.y);
		System.out.println("b.x : " + b.x + " and b.y : " + b.y);
	}
	
	public static int commonTwo(String[] a, String[] b) {
		int count = 0;
		
		for (int i = 0, j = 0; i < a.length && j < b.length;) {
			if (a[i].equals(b[j])) {
				System.out.println("1");
				++count;
				++j;
				++i;
			} else if (a[i].charAt(0) < b[j].charAt(0)){
				System.out.println("2");
				++i;
			} else {
				System.out.println("3");
				++j;
			}
		}
		
		return count;
	}
}
