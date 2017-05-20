package com.hiandev.rosbot.scanner;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class ScannerFrame {
	
	private Scanner scanner;
    public void setScanner(Scanner scanner) {
    	this.scanner = scanner;
    }
    
    private JFrame frame = null;
    public final ScannerFrame show() {
    	if (frame != null) {
    		return this;
    	}
    	EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                    ex.printStackTrace();
                }
                frame = new JFrame("ScannerFrame");
                frame.setUndecorated(true);
                frame.setBackground(new Color(0, 0, 0, 0));
//              frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.add(framPanel = new FramePanel());
                frame.setAlwaysOnTop(true);
                frame.pack();
                frame.setLocation(scanner._x, scanner._y);
                frame.setVisible(true);
                ready = true;
            }
        });
    	return this;
    }
    private boolean ready = false;
    public boolean isReady() {
    	return ready;
    }

    /*
     * 
     * 
     * 
     */
    private FramePanel framPanel;
    class FramePanel extends JPanel {
        public FramePanel() {
            setOpaque(false);
            setLayout(null);
        }
        @Override
        public Point getLocation() {
        	return new Point(0, 0);
        }
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(scanner._w, scanner._h);
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setBackground(getBackground());
            g2d.setColor(Color.CYAN);
            int x = 0;
           	int y = 0;
           	int w = getWidth()  - 1;
           	int h = getHeight() - 1;
           	g2d.drawRect(x, y, w, h);
            g2d.dispose();
        }
    }
    
}