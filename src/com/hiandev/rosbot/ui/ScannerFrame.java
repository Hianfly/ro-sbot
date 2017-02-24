package com.hiandev.rosbot.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import com.hiandev.rosbot.Scanner;

public class ScannerFrame {
	
	private final Scanner scanner;
    public ScannerFrame(Scanner scanner) {
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
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.add(new FramePanel());
                frame.setAlwaysOnTop(true);
                frame.pack();
                frame.setLocation(scanner._x, scanner._y);
                frame.setVisible(true);
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
                   	x = scanner.zoneChar[0];
                   	y = scanner.zoneChar[2];
                   	w = scanner.zoneChar[1] - scanner.zoneChar[0] - 1;
                   	h = scanner.zoneChar[3] - scanner.zoneChar[2] - 1;
                   	g2d.drawRect(x, y, w, h);
                   	
                    g2d.setColor(Color.RED);
                   	x = scanner.zoneIdle[0];
                   	y = scanner.zoneIdle[2];
                   	w = scanner.zoneIdle[1] - scanner.zoneIdle[0] - 1;
                   	h = scanner.zoneIdle[3] - scanner.zoneIdle[2] - 1;
                   	g2d.drawRect(x, y, w, h);
                   	x = scanner.zoneHpSp[0];
                   	y = scanner.zoneHpSp[2];
                   	w = scanner.zoneHpSp[1] - scanner.zoneHpSp[0] - 1;
                   	h = scanner.zoneHpSp[3] - scanner.zoneHpSp[2] - 1;
                   	g2d.drawRect(x, y, w, h);
                   	
                   	x = scanner.zoneChat[0];
                   	y = scanner.zoneChat[2];
                   	w = scanner.zoneChat[1] - scanner.zoneChat[0] - 1;
                   	h = scanner.zoneChat[3] - scanner.zoneChat[2] - 1;
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
    
    class PreviewPanel extends JPanel {
        public PreviewPanel() {
            setOpaque(false);
            setLayout(null);
        }
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
    }
    public final void updatePreview() {
    	
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
    
    /*
     * 
     * 
     * 
     */
    
    private CellPanel cellPanel = null;
    public final void updateCells(ArrayList<int[]> cellDiff) {
    	clearCells(0);
    	mainPanel.add(cellPanel = new CellPanel(cellDiff));
        cellPanel.setBounds(0, 0, scanner._w, scanner._h);
    }
    public final void clearCells(int wait) {
    	if (cellPanel != null) {
	    	mainPanel.remove(cellPanel);
	    	mainPanel.repaint();
	    	if (wait > 0) {
	    		try {
	    			Thread.sleep(wait);
	    		} catch (Exception e) {
	    		}
	    	}
    	}
    }
    class CellPanel extends JPanel {
    	private ArrayList<int[]> cellDiff = new ArrayList<>();
        public CellPanel(ArrayList<int[]> cellDiff) {
        	this.cellDiff = cellDiff;
            setOpaque(false);
            setLayout(null);
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setBackground(getBackground());
            g2d.setColor(Color.RED);
            if (cellDiff != null) {
	            for (int[] r : cellDiff) {
	            	g2d.drawRect(r[0], r[1], r[2] - 1, r[3] - 1);
	            }
            }
            g2d.dispose();
        }
    }
    
}