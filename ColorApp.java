/******************************************\
            Name: Vincent Pham
   Assignment: Homework 8 - Color Sampler
               Date: 12/4/2021
\******************************************/

//EXIT CODES
// STATUS: 0 - SUCCESS
// STATUS: 1 - FAILED DURING PARSING (CHECK INPUT FILE FORMAT) 
// STATUS: 2 - FAILED TO LOAD GUI/HANDLER CLASS
// STATUS: 3 - FAILED TO WRITE OUTPUT FILE

import java.io.FileNotFoundException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.ArrayList;

//Main Application Class
public class ColorApp extends JFrame {
	//Handles File/IO and color model.
	/*
		All Color data is retrieved from the ColorHandler class. A list of the color names can be
		retrieved from the class which of each can be used to reference the colors of the class.
		The class loads data from a file using the java.util.Scanner library.
	*/
    public static class ColorHandler{
		HashMap<String, Color> colors = new HashMap<String, Color>();
		String filename;
		//Parameterized constructor setting the filename. Parses the input using Scanner. Throws error 
		//If format is incorrect as an error will be thrown.
		ColorHandler(String filename) throws FileNotFoundException{
			this.filename = filename;
			File f = new File(filename);
			Scanner sc = new Scanner(f);
			while (sc.hasNext()){
				String color_name = sc.next();
				try {
					int r = sc.nextInt();
					int g = sc.nextInt();
					int b = sc.nextInt();
					System.out.println("Loaded "+color_name+"("+r+","+g+","+b+")");
					colors.put(color_name, new Color(r,g,b));
				} catch (Exception e) {
					System.out.println("Error: Invalid color data format, file could not be parsed.");
					System.exit(1);
				}
			}
			sc.close();
		}
		//Get a list of names from the model
		public ArrayList<String> getNames(){
			ArrayList<String> res = new ArrayList<String>();
			for(Map.Entry me : colors.entrySet()) {
				res.add((String)me.getKey());
			}
			return res;
		}
		//Get the color data for the name provided from the hashmap
		public Color getColor(String colorName){
			return colors.get(colorName);
		}
		//Saves the data into the file
		public void save(String colorName, Color color){
			if(colors.get(colorName) != null){
				colors.put(colorName, color);
				try {
					FileWriter f = new FileWriter(filename);
					for(Map.Entry me : colors.entrySet()) {
						Color c = (Color)me.getValue();
						String color_name = (String)me.getKey();
						String r = Integer.toString(c.getRed());
						String g = Integer.toString(c.getGreen());
						String b = Integer.toString(c.getBlue());
						f.write(color_name+"\t"+r+"\t"+g+"\t"+b+"\n");
					}
					f.close();
				} catch (IOException e) {
					System.out.println(e);
					e.printStackTrace();
					System.exit(3);
				}
			}
		}
	}
	//View/Controller Class
	//Contains the GUI and all componenets of the GUI. Takes a ColorHandler object in arguments in which
	//it will reference for getting the color data.
    public static class GUIView extends JFrame {
		//General Components
		protected Boolean unsaved = false;
		protected ArrayList<String> colors = new ArrayList<String>();
		protected Color currentColor = new Color(0,0,0);
        protected ColorSampler colorSampler;
        protected JButton buttonSave;
        protected JButton buttonReset;
		protected JLabel currentRGB;
		protected JList<String> colorList;
        //Color=specific Component
        protected JLabel labelRed;
        protected JTextField tfRed8bit;
        protected JButton buttonDecRed;
        protected JButton buttonIncRed;
        protected JLabel labelGreen;
        protected JTextField tfGreen8bit;
        protected JButton buttonDecGreen;
        protected JButton buttonIncGreen;
        protected JLabel labelBlue;
        protected JTextField tfBlue8bit;
        protected JButton buttonDecBlue;
        protected JButton buttonIncBlue;
		protected ColorHandler ch;

		//ListHandler which changes the color and retrieves the ColorHandler data.
		public class ListHandler implements ListSelectionListener {
			public void valueChanged(ListSelectionEvent e){
				if(e.getSource() == colorList){
					if (!e.getValueIsAdjusting()){
						int i = colorList.getSelectedIndex();
						String s = (String) colorList.getSelectedValue();
						currentColor = ch.getColor(s);
						updatePanel();
						System.out.println("Position " + i + " selected: " + s);
					}
				}
			}
		}
		//Parses input for the text fields..
		//If no int was found a -1 will be returned for no change.
		public int parseInput(String input){
			Scanner s = new Scanner(input);
			if(!s.hasNextInt()){
				return -1;
			}
			int val = s.nextInt();	
			return restrictRange(val);
		}
		//Verifies a correct 8-bit value for the integer provided.
		public int restrictRange(int val){
			if(val < 0){
				return 0;
			} else if (val>255){
				return 255;
			} else {
				return val;
			}
		}
		//Constructor which intializes all of the Event listeners, one for each color,
		//and organizes all the GUI componenets.
        public GUIView(ColorHandler ch){
            //Instantiate GUI Frame and Componenets
            super("Color Picker");
			this.ch = ch;
			colors = ch.getNames();
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			colorSampler = new ColorSampler();
			colorList = new JList<String>();
			String[] colorArray = colors.toArray(new String[colors.size()]);
			colorList.setListData(colorArray);
			colorList.setSelectedIndex(0);
			String s = (String) colorList.getSelectedValue();
			currentColor = ch.getColor(s);
			currentRGB = new JLabel("R: 0, G: 0, B: 0");
			buttonSave = new JButton("Save");
			buttonReset = new JButton("Reset");
			labelRed = new JLabel("Red: ");
			tfRed8bit = new JTextField("NA");
			buttonDecRed = new JButton("-");
			buttonIncRed = new JButton("+");
			labelGreen = new JLabel("Green: ");
			tfGreen8bit = new JTextField("NA");
			buttonDecGreen = new JButton("-");
			buttonIncGreen = new JButton("+");
			labelBlue = new JLabel("Blue: ");
			tfBlue8bit = new JTextField("NA");
			buttonDecBlue = new JButton("-");
			buttonIncBlue = new JButton("+");
			//Add to View/Jlist

			getContentPane().setLayout(null);
			getContentPane().add(currentRGB);
			getContentPane().add(colorList);
			getContentPane().add(labelRed);
			getContentPane().add(tfRed8bit);
			getContentPane().add(buttonDecRed);
			getContentPane().add(buttonIncRed);
			getContentPane().add(labelGreen);
			getContentPane().add(tfGreen8bit);
			getContentPane().add(buttonDecGreen);
			getContentPane().add(buttonIncGreen);
			getContentPane().add(labelBlue);
			getContentPane().add(tfBlue8bit);
			getContentPane().add(buttonDecBlue);
			getContentPane().add(buttonIncBlue);
			getContentPane().add(colorSampler);
			getContentPane().add(buttonSave);
			getContentPane().add(buttonReset);
			//Set Positions
			//Margin of 5
			setBounds(500,250,325,350);
			colorSampler.setBounds(5,5,190,175);
			currentRGB.setBounds(5,180,190,15);
			labelRed.setBounds(5,200,50,20);
			tfRed8bit.setBounds(60,200,35,20);
			buttonDecRed.setBounds(100, 200, 45, 20); 
			buttonIncRed.setBounds(150, 200, 45, 20);
			labelGreen.setBounds(5,225,50,20);
			tfGreen8bit.setBounds(60,225,35,20);
			buttonDecGreen.setBounds(100, 225, 45, 20);
			buttonIncGreen.setBounds(150, 225, 45, 20);
			labelBlue.setBounds(5,250,50,20);
			tfBlue8bit.setBounds(60,250,35,20);
			buttonDecBlue.setBounds(100,250,45,20);
			buttonIncBlue.setBounds(150,250,45,20);
			buttonSave.setBounds(5,275,95,25);
			buttonReset.setBounds(105,275,90,25);
			colorList.setBounds(200, 5, 120, 300);

			//Action Commands
			tfRed8bit.setActionCommand("Custom");
			buttonDecRed.setActionCommand("Dec");
			buttonIncRed.setActionCommand("Inc");
			tfGreen8bit.setActionCommand("Custom");
			buttonDecGreen.setActionCommand("Dec");
			buttonIncGreen.setActionCommand("Inc");
			tfBlue8bit.setActionCommand("Custom");
			buttonDecBlue.setActionCommand("Dec");
			buttonIncBlue.setActionCommand("Inc");
			buttonSave.setActionCommand("save");
			buttonReset.setActionCommand("reset");
			
			//FocusEvent
			FocusListener redFL = new FocusListener(){
				public void focusGained(FocusEvent e){
					//Do Nothing
				}
				public void focusLost(FocusEvent e){
					System.out.println("Custom Red: " + tfRed8bit.getText());
					int new_val = parseInput(tfRed8bit.getText());
					if(new_val == -1){
						new_val = currentColor.getRed();
					}
					currentColor = new Color(new_val, currentColor.getGreen(), currentColor.getBlue());
					unsaved = true;
					updatePanel();
				}
			};
			FocusListener greenFL = new FocusListener(){
				public void focusGained(FocusEvent e){
					//Do Nothing
				}
				public void focusLost(FocusEvent e){
					System.out.println("Custom Green: " + tfGreen8bit.getText());
					int new_val = parseInput(tfGreen8bit.getText());
					if(new_val == -1){
						new_val = currentColor.getGreen();
					}
					currentColor = new Color(currentColor.getRed(), new_val, currentColor.getBlue());
					unsaved = true;
					updatePanel();
				}
			};
			FocusListener blueFL = new FocusListener(){
				public void focusGained(FocusEvent e){
					//Do Nothing
				}
				public void focusLost(FocusEvent e){
					System.out.println("Custom Blue: " + tfBlue8bit.getText());
					int new_val = parseInput(tfBlue8bit.getText());
					if(new_val == -1){
						new_val = currentColor.getBlue();
					}
					currentColor = new Color(currentColor.getRed(), currentColor.getGreen(), new_val);
					unsaved = true;
					updatePanel();
				}
			};

			//ActionListener
			ActionListener redAL = new ActionListener(){
				public void actionPerformed(ActionEvent e){
					if(e.getActionCommand().equals("Dec")){
						currentColor = new Color(restrictRange(currentColor.getRed()-5), currentColor.getGreen(), currentColor.getBlue());
						System.out.println("Red decreased by 5");
						unsaved = true;
						updatePanel();
					}
					else if(e.getActionCommand().equals("Inc")){
						currentColor = new Color(restrictRange(currentColor.getRed()+5), currentColor.getGreen(), currentColor.getBlue());
						System.out.println("Red increased by 5");
						unsaved = true;
						updatePanel();
					}
					else if(e.getActionCommand().equals("Custom")){
						System.out.println("Custom Red: " + tfRed8bit.getText());
						int new_val = parseInput(tfRed8bit.getText());
						if(new_val == -1){
							new_val = currentColor.getRed();
						}
						currentColor = new Color(new_val, currentColor.getGreen(), currentColor.getBlue());
						unsaved = true;
						updatePanel();
					}
				}
			};
			ActionListener greenAL = new ActionListener(){
				public void actionPerformed(ActionEvent e){
					if(e.getActionCommand().equals("Dec")){
						System.out.println("Green decreased by 5");
						currentColor = new Color(currentColor.getRed(), restrictRange(currentColor.getGreen()-5), currentColor.getBlue());
						unsaved = true;
						updatePanel();
					}
					else if(e.getActionCommand().equals("Inc")){
						System.out.println("Green increased by 5");
						currentColor = new Color(currentColor.getRed(), restrictRange(currentColor.getGreen()+5), currentColor.getBlue());
						unsaved = true;
						updatePanel();
					}
					else if(e.getActionCommand().equals("Custom")){
						System.out.println("Custom Green: "+tfGreen8bit.getText());
						int new_val = parseInput(tfGreen8bit.getText());
						if(new_val == -1){
							new_val = currentColor.getGreen();
						}
						currentColor = new Color(currentColor.getRed(), new_val, currentColor.getBlue());
						unsaved = true;
						updatePanel();
					}
				}
			};
			ActionListener blueAL = new ActionListener(){
				public void actionPerformed(ActionEvent e){
					if(e.getActionCommand().equals("Dec")){
						System.out.println("Blue decreased by 5");
						currentColor = new Color(currentColor.getRed(), currentColor.getGreen(), restrictRange(currentColor.getBlue()-5));
						unsaved = true;
						updatePanel();
					}
					else if(e.getActionCommand().equals("Inc")){
						System.out.println("Blue increased by 5");
						currentColor = new Color(currentColor.getRed(), currentColor.getGreen(), restrictRange(currentColor.getBlue()+5));
						unsaved = true;
						updatePanel();
					}
					else if(e.getActionCommand().equals("Custom")){
						System.out.println("Custom Blue: " + tfBlue8bit.getText());
						int new_val = parseInput(tfBlue8bit.getText());
						if(new_val == -1){
							new_val = currentColor.getBlue();
						}
						currentColor = new Color(currentColor.getRed(), currentColor.getGreen(), new_val);
						unsaved = true;
						updatePanel();
					}
				}
			};
			ActionListener stateHandler = new ActionListener(){
				public void actionPerformed(ActionEvent e){
					if(e.getActionCommand().equals("save")){
						save();
					}
					else if (e.getActionCommand().equals("reset")){
						reset();
					}
				}
			};
			//Assign Listeners
			colorList.addListSelectionListener(new ListHandler());
			tfRed8bit.addActionListener(redAL);
			tfRed8bit.addFocusListener(redFL);
			buttonDecRed.addActionListener(redAL);
			buttonIncRed.addActionListener(redAL);
			tfGreen8bit.addActionListener(greenAL);
			tfGreen8bit.addFocusListener(greenFL);
			buttonDecGreen.addActionListener(greenAL);
			buttonIncGreen.addActionListener(greenAL);
			tfBlue8bit.addActionListener(blueAL);
			tfBlue8bit.addFocusListener(blueFL);
			buttonDecBlue.addActionListener(blueAL);
			buttonIncBlue.addActionListener(blueAL);
			buttonReset.addActionListener(stateHandler);
			buttonSave.addActionListener(stateHandler);
			updatePanel();
			setVisible(true);
		}

		//Adds a color to the selection list, part of the GUIView interface
		public void addColor(String cname){
			colors.add(cname);
			System.out.println("Loaded " + cname + " to the list of colors");
		}
		//Refreshes the color sampler to whatever is in the RGBtf's
		public void updatePanel(){
			if(unsaved){
				setTitle("*Color Picker");
			} else {
				setTitle("Color Picker");
			}
			colorSampler.repaint();
			tfRed8bit.setText(Integer.toString(currentColor.getRed()));
			tfGreen8bit.setText(Integer.toString(currentColor.getGreen()));
			tfBlue8bit.setText(Integer.toString(currentColor.getBlue()));
			currentRGB.setText("R:"+currentColor.getRed()
				+" G:"+currentColor.getGreen()
				+" B:"+currentColor.getBlue()
				+" Hex:#"+String.format("%02X", currentColor.getRed())
				+String.format("%02X", currentColor.getGreen())
				+String.format("%02X", currentColor.getBlue()));
		}

		//Resets values of RGB from saved model
		public void reset(){
			unsaved = false;
			String savedColor = (String)colorList.getSelectedValue();
			System.out.println("Restting color to " + savedColor);
			currentColor = ch.getColor(savedColor);
			updatePanel();
		}

		//Saves current values from RGBtf to model
		public void save(){
			unsaved = false;
			Color oldColor = ch.getColor((String)colorList.getSelectedValue());
			System.out.println("Saving color " + (String)colorList.getSelectedValue() + ": (" + oldColor.getRed()+","+oldColor.getGreen()+","+oldColor.getBlue()+")->("
				+currentColor.getRed()+","+currentColor.getGreen()+","+currentColor.getBlue()+")");
			ch.save((String)colorList.getSelectedValue(),currentColor);
			updatePanel();
		}
        public class ColorSampler extends JPanel {
            public void paintComponent(Graphics g) {
                Dimension d = getSize();
                g.setColor(currentColor);
                g.fillRect(0,0,d.width,d.height);
            }
        }

    }
    public static void main(String argv []){
		try{    
			GUIView g = new GUIView(new ColorHandler("colors.txt"));
		} catch(Exception e) {
			System.out.println(e);
			e.printStackTrace();
			System.exit(2);
		}
    }
}
