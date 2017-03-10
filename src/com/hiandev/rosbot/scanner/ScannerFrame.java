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

public class ScannerFrame {
	
	private Scanner scanner;
    public void setScanner(Scanner scanner) {
    	this.scanner = scanner;
    }
    
    private JFrame frame = null;
    private boolean ready = false;
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
                frame.add(new FramePanel());
                frame.setAlwaysOnTop(true);
                frame.pack();
                frame.setLocation(scanner._x, scanner._y);
                frame.setVisible(true);
                ready = true;
            }
        });
    	return this;
    }

    /*
     * 
     * 
     * 
     */
    
    class FramePanel extends JPanel {
        public FramePanel() {
            setOpaque(false);
            setLayout(null);
            add(mainPanel = new MainPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setBackground(getBackground());
                    g2d.setColor(Color.GREEN);
                    int x = 0;
                   	int y = 0;
                   	int w = getWidth()  - 1;
                   	int h = getHeight() - 1;
                   	g2d.drawRect(x, y, w, h);
                    g2d.dispose();
                }
            });
            mainPanel.setBounds(0, 0, scanner._w, scanner._h);
            add(suppPanel = new MainPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setBackground(getBackground());
                    g2d.setColor(Color.GREEN);
                    int x = 0;
                   	int y = 0;
                   	int w = getWidth()  - 1;
                   	int h = getHeight() - 1;
                   	g2d.drawRect(x, y, w, h);
                    g2d.dispose();
                }
            });
            suppPanel.setBounds(scanner._w, 0, scanner._w, scanner._h);
        }
        @Override
        public Point getLocation() {
        	return new Point(0, 0);
        }
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(scanner._w * 2, scanner._h);
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setBackground(getBackground());
            g2d.setColor(Color.GREEN);
            g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
            g2d.dispose();
        }
    }
    
    
    /*
     * 
     * 
     * 
     */

    private PreviewPanel previewPanel = null;
    class PreviewPanel extends JPanel {
    	private BufferedImage image = null;
        public PreviewPanel(BufferedImage image) {
        	this.image = image;
            setOpaque(false);
            setLayout(null);
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setBackground(getBackground());
            g2d.setColor(Color.GREEN);
           	g2d.drawImage(image, 0, 0, null);
            g2d.dispose();
        }
    }
    public final void updatePreview(BufferedImage image) {
    	if (suppPanel == null) {
    		return ;
    	}
    	if (previewPanel != null) {
			suppPanel.remove(previewPanel);
			suppPanel.repaint();
    	}
    	suppPanel.add(previewPanel = new PreviewPanel(image));
    	previewPanel.setBounds(0, 0, scanner._w, scanner._h);
    }
    public final void clearPreview(int wait) {
    	if (suppPanel == null) {
    		return;
    	}
    	if (previewPanel != null) {
    		suppPanel.remove(previewPanel);
    		suppPanel.repaint();
    	}
    }
    
    /*
     * 
     * 
     * 
     */
    
    private MainPanel mainPanel = null;
    private MainPanel suppPanel = null;
    class MainPanel extends JPanel {
        public MainPanel() {
            setOpaque(false);
            setLayout(null);
        }
    }
    
}