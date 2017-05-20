package com.hiandev.rosbot.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;

import com.hiandev.rosbot.scanner.battle.BattleScanner;

public class UIFrame {
	
	public UIFrame(int _x, int _y, int _w, int _h) {
		this._x = _x;
		this._y = _y;
		this._w = _w;
		this._h = _h;
	}

	private int _x = 0;
	private int _y = 0;
	private int _w = 0;
	private int _h = 0;	
	private  JFrame frame = null;
    public final UIFrame show() {
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
                frame = new JFrame("UIFrame");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.add(framePanel = new FramePanel());
                frame.setAlwaysOnTop(false);
                frame.pack();
                frame.setLocation(_x, _y);
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
    
    private FramePanel framePanel = null;
    public FramePanel getFramePanel() {
    	return framePanel;
    }
    public class FramePanel extends JPanel {
        public FramePanel() {
        	setBorder(new EmptyBorder(5, 5, 5, 5));
        	setOpaque(false);
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            lblLocation = new JLabel();
            lblLocation.setText("<html><p><b>YOUR LOCATION:</b><br>UNKNOWN<br><br></p></html>");
            add(lblLocation);
            JLabel lblPortal = new JLabel();
            lblPortal.setText("<html><p><b>PORTAL LOCATION:</b></p></html>");
            add(lblPortal);
            txtPortal = new JTextField();
            txtPortal.setMaximumSize(new Dimension(Integer.MAX_VALUE, txtPortal.getPreferredSize().height) );
            add(txtPortal);
        }
        @Override
        public Point getLocation() {
        	return new Point(0, 0);
        }
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(_w, _h);
        }
    	public JLabel lblLocation = null;
        public void updateYourLocationInfo(int _mx, int _my) {
        	String value = "<html><p><b>YOUR LOCATION:</b><br>" + _mx + "," + _my;
        	value += "<br><br></p></html>";
        	lblLocation.setText(value);
        }
    	public JTextField txtPortal = null;
        public void updatePortalInfo(ArrayList<int[]> portals) {
        	String value = ""; 
            for (int x = 0; x < portals.size(); x++) {
            	value += (portals.get(x)[0] + "," + portals.get(x)[1] + ";");
            }
            txtPortal.setText(value);
        }
    }
    
}