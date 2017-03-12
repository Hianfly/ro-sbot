package com.hiandev.rosbot.scanner;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class PreviewFrame {
	
	public PreviewFrame() {
		
	}
	
	public PreviewFrame(int xOffset, int yOffset) {
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}
	
	private Scanner scanner;
    public void setScanner(Scanner scanner) {
    	this.scanner = scanner;
    }
    
    private JFrame frame = null;
	private int xOffset = 0;
	private int yOffset = 0;
    public final PreviewFrame show() {
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
                frame = new JFrame("PreviewFrame");
                frame.setUndecorated(true);
                frame.setBackground(new Color(0, 0, 0, 0));
//              frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.add(framePanel = new FramePanel());
                frame.setAlwaysOnTop(false);
                frame.pack();
                frame.setLocation(scanner._x + scanner._w + xOffset, scanner._y + yOffset);
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
    private FramePanel framePanel;
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
    }
    
    
    /*
     * 
     * 
     * 
     */
    private PreviewPanel prevwPanel = null;
    class PreviewPanel extends JPanel {
    	private BufferedImage image = null;
        public PreviewPanel(BufferedImage image) {
            setOpaque(false);
            setLayout(null);
        	this.image = image;
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
           	g2d.drawImage(image, 0, 0, null);
            g2d.dispose();
        }
    }
    public final void updatePreview(BufferedImage image) {
    	if (framePanel == null) {
    		return;
    	}
    	if (prevwPanel != null) {
    		framePanel.remove(prevwPanel);
    		framePanel.repaint();
    	}
    	framePanel.add(prevwPanel = new PreviewPanel(image));
    	prevwPanel.setBounds(0, 0, scanner._w, scanner._h);
    }
    public final void clearPreview(int wait) {
    	if (framePanel == null || prevwPanel == null) {
    		return;
    	}
   		framePanel.remove(prevwPanel);
   		framePanel.repaint();
    }
    
}