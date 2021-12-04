package hyperlink;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

//top panel that contains all the functionalities 
public class Interface extends JFrame{
    /**
     * constructor
     */
     String primary;
     String primaryMovieName;   ////
     String secondary;
     String secondaryMovieName; /////
     SliderDemo video1;
     SliderDemo video2;
     JButton connectButton;
     JComboBox linkList; 
     File myFile;
     FileWriter myWriter;
     List<int[]> boundingBoxList; // a list to store all the boudning boxs (int[4]{x,y,w,h})
    public Interface() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        JPanel featurePanel = new JPanel();
        add(featurePanel, BorderLayout.NORTH);
      
        //Add content to the window.
        //Display the window.
        featurePanel.setLayout(new FlowLayout());
        //Create the label.
        JLabel actionLable = new JLabel("ACTION:", JLabel.CENTER);
        String[] actionStrings = { "Import Primary Video", "Import Secondary Video"};

        JComboBox actionList = new JComboBox(actionStrings);
        actionList.setSelectedIndex(0);
        actionList.addActionListener(new ActionListener() { 
          public void actionPerformed(ActionEvent e) { 
            importVideo(actionList.getSelectedIndex());
          } 
        } );
        JLabel linkLable = new JLabel("SELECT LINK:", JLabel.CENTER);
         linkList = new JComboBox();

        
        connectButton = new JButton("Connect Video");
        connectButton.addActionListener(new ActionListener() { 
          public void actionPerformed(ActionEvent e) { 
            connectVideo();
          } 
        } );
        connectButton.disable();
        JButton saveButton = new JButton("Save Video");
        saveButton.addActionListener(new ActionListener() { 
          public void actionPerformed(ActionEvent e) { 
            saveFile();
          } 
        });
        featurePanel.add(actionLable);
        featurePanel.add(actionList);
        featurePanel.add(linkLable);
        featurePanel.add(linkList);
        featurePanel.add(connectButton);
        featurePanel.add(saveButton);

             //take in the first file path 
            video1 = new SliderDemo(primary, primaryMovieName);
            add(video1, BorderLayout.WEST);
        
       
            //take in the second file path 
            video2 = new SliderDemo(secondary, secondaryMovieName);
            add(video2, BorderLayout.EAST);
        
        
        
        pack();
        setVisible(true);
        
    }
    public void connectVideo(){
        JFrame frame = new JFrame();
        //enable the connect button
        String s = (String)JOptionPane.showInputDialog(
                frame,
                "Enter the name of the selected link :\n"
                + "\"Name the HyperLink: \"",
                "CREATE HYPERLINK",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null, "Dinosour");
        createNewLink(s);
    }
    public String importVideo(int mode) {
        JFrame frame = new JFrame();
        if(mode == 0) {
            //import primary video
          String s = (String)JOptionPane.showInputDialog(
                  frame,
                  "Enter the folder path for primary video :\n"
                  + "\"import video:\"",
                  "IMPORT VIDEO",
                  JOptionPane.PLAIN_MESSAGE,
                  null,
                  null, "AIFilmOne");
          String movieName = (String)JOptionPane.showInputDialog(
                frame,
                "Enter the movie name for primary video :\n"
                + "\"import video:\"",
                "IMPORT VIDEO",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null, "AIFilmOne");
          if(s==null || movieName == null) {
              return null;
          }
          primary = s;
          primaryMovieName = movieName; ////////
           //take in the first file path 
          video1.updatePath(s, movieName);  ///////
          boundingBoxList = new ArrayList<int[]>();
          //for each primary video, create a metadata file with the video name 
          try {
              myFile = new File(s+"_metadata.txt");
              if (myFile.createNewFile()) {
                System.out.println("File created: " + myFile.getName());
              } else {
                System.out.println("File already exists.");
              }
            } catch (IOException e) {
              System.out.println("An error occurred.");
              e.printStackTrace();
            }
          clearLinks();

        }else if(mode == 1) {
            //import primary video
          String s = (String)JOptionPane.showInputDialog(
                  frame,
                  "Enter the folder path for secondary video :\n"
                  + "\"import video: \"",
                  "IMPORT VIDEO",
                  JOptionPane.PLAIN_MESSAGE,
                  null,
                  null, "AIFilmTwo");
          String movieName = (String)JOptionPane.showInputDialog(
                frame,
                "Enter the movie name for primary video :\n"
                + "\"import video:\"",
                "IMPORT VIDEO",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null, "AIFilmTwo");
          secondary = s;
          secondaryMovieName = movieName;   //////
          video2.updatePath(secondary, movieName);
          video2.sethasVideo();
            
        }
        return null;
    }
    public void createNewLink(String name) {
        linkList.addItem(name);
        boundingBoxList.add(video1.getCurrentBoundingBox());
        //if no box is drawn, then ignore the button action
        if(video1.getCurrentBoundingBox() ==null) {
            return;
        }
        try {
              myWriter = new FileWriter(myFile.getName(),true);
              myWriter.write(name+"\n");
              myWriter.write("frame:"+video1.frameNumber+"\n");
              /*
               * found out why w, h  is always 0?????
               */
              
              myWriter.write("x:"+video1.getCurrentBoundingBox()[0]+"\n");
              myWriter.write("y:"+video1.getCurrentBoundingBox()[1]+"\n");
              myWriter.write("w:"+video1.getCurrentBoundingBox()[2]+"\n");
              myWriter.write("h:"+video1.getCurrentBoundingBox()[3]+"\n");
              myWriter.write("path:"+secondary+"\n");
              myWriter.write("subFrame:"+video2.frameNumber+"\n");
             
              System.out.println("Successfully wrote to the file.");
            } catch (IOException e) {
              System.out.println("An error occurred.");
              e.printStackTrace();
            }
        
    }
    public String getPrimaryPath () {
        return primary;
    }
    public String getSecondaryPath () {
        return secondary;
    }
    public void clearLinks() {
        linkList.removeAllItems();
    }
    public void saveFile() {
        try {
            myWriter.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
