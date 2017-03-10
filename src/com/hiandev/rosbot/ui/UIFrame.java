package com.hiandev.rosbot.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Label;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.hiandev.rosbot.scanner.battle.ItemScanner;

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
	private JFrame frame = null;
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
                frame.add(new FramePanel());
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
    
    class FramePanel extends JPanel {
        public FramePanel() {
            setOpaque(false);
            setLayout(null);
            Label lblInfo = new Label();
            lblInfo.setText("Hello!");
            lblInfo.setBounds(0, 0, 800, 20);
            lblInfo.setBackground(Color.red);
            add(lblInfo);
        }
        @Override
        public Point getLocation() {
        	return new Point(0, 0);
        }
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(_w, 100);
        }
    }
    
}