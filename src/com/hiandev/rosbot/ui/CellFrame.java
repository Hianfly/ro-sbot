package com.hiandev.rosbot.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class CellFrame {
	
    public CellFrame(int screenX, int screenY, int width, int height) {
        this.screenX = screenX;
        this.screenY = screenY;
        this.width   = width;
        this.height  = height;
    }
        
    private int screenX = 40;
    public final int getScreenX() {
    	return screenX;
    }
	private int screenY = 40;
	public final int getScreenY() {
		return screenY;
	}
    private int width = 800;
    public final int getWidth() {
    	return width;
    }
    private int height = 600;
    public final int getHeight() {
    	return height;
    }
    
    private JFrame frame = null;
    public final CellFrame show() {
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
                frame = new JFrame("MainFrame");
                frame.setUndecorated(true);
                frame.setBackground(new Color(0, 0, 0, 0));
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.add(mainPanel = new MainPanel());
                frame.setAlwaysOnTop(true);
                frame.pack();
                frame.setLocation(screenX, screenY);
                frame.setVisible(true);
                if (mCellFrameListener != null) {
                	mCellFrameListener.onFrameReady();
                }
            }
        });
    	return this;
    }

    /*
     * 
     * 
     * 
     */
    
    private MainPanel mainPanel = null;
    class MainPanel extends JPanel {
        public MainPanel() {
            setOpaque(false);
            setLayout(null);
        }
        @Override
        public Point getLocation() {
        	return new Point(0, 0);
        }
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(width, height);
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
    
    private CellPanel cellPanel = null;
    public final void updateCells(ArrayList<int[]> cellData) {
    	clearCells(0);
    	mainPanel.add(cellPanel = new CellPanel(cellData));
        cellPanel.setBounds(0, 0, width, height);
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
    	private ArrayList<int[]> cellData = new ArrayList<>();
        public CellPanel(ArrayList<int[]> cellData) {
        	this.cellData = cellData;
            setOpaque(false);
            setLayout(null);
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setBackground(getBackground());
            g2d.setColor(Color.RED);
            for (int[] r : cellData) {
            	g2d.drawRect(r[0], r[1], r[2] - 1, r[3] - 1);
            }
            g2d.dispose();
        }
    }
    
    /*
     * 
     * 
     * 
     */
    
    private CellFrameListener mCellFrameListener = null;
    public void setCellFrameListener(CellFrameListener listener) {
    	mCellFrameListener = listener;
    }
    public static interface CellFrameListener {
    	public void onFrameReady();
    }
    
    
}