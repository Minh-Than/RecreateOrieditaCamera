package main;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;

import components.CPLine;
import model.CPCamera;
import model.FoldLineSet;

import javax.swing.JButton;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FileDialog;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Main {
    public static void main(String[] argv){
        CPCamera cpCamera = CPCamera.getInstance();
        FoldLineSet foldLineSet = FoldLineSet.getInstance();

        JFrame frame = new JFrame("camera testing");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setLayout(new BorderLayout());

        setupDefaultSqaure(foldLineSet);
    
        JPanel mainPanel = new DrawPanel();
        
        JButton openFileBtn = new JButton("Open file");
        openFileBtn.addActionListener(e -> {
            if(readFile(frame, foldLineSet)){
                cpCamera.initialize();
                mainPanel.repaint();
            }
            mainPanel.requestFocus();
        });

        JButton resetPosBtn = new JButton("Reset Position");
        resetPosBtn.addActionListener(e -> {
            cpCamera.initialize();
            mainPanel.repaint();
            mainPanel.requestFocus();
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(new MatteBorder(0, 0, 1, 0, Color.BLACK));
        buttonPanel.add(openFileBtn);
        buttonPanel.add(resetPosBtn);

        frame.add(buttonPanel, BorderLayout.NORTH);
        frame.add(mainPanel, BorderLayout.CENTER);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        frame.setVisible(true);
        mainPanel.requestFocus();
        
    }

    public static boolean readFile(JFrame frame, FoldLineSet foldLineSet){
        FileDialog fileDialog = new FileDialog(frame, "Select a file", FileDialog.LOAD);

        // Filter files with .cp extension
        fileDialog.setFilenameFilter((dir, name) -> name.endsWith(".cp"));

        fileDialog.setVisible(true);

        if (fileDialog.getFile() == null) {
            System.out.println("File selection cancelled.");
            return false;
        }

        if(!foldLineSet.getList().isEmpty()) foldLineSet.clear();
        
        File file = new File(fileDialog.getDirectory(), fileDialog.getFile());
        readFileLineByLine(file, foldLineSet);

        return true;
    }


    public static void readFileLineByLine(File file, FoldLineSet foldLineSet) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                    String[] arr = line.split("\\s+");

                    foldLineSet.append(new CPLine(Integer.parseInt(arr[0]),
                            Double.parseDouble(arr[1]),
                            Double.parseDouble(arr[2]),
                            Double.parseDouble(arr[3]),
                            Double.parseDouble(arr[4])
                    ));
            }


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) reader.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void setupDefaultSqaure(FoldLineSet foldLineSet){
        foldLineSet.append(new CPLine(1, -200.0, -200.0, 200.0, -200.0));
        foldLineSet.append(new CPLine(1, -200.0, -200.0, -200.0, 200.0));
        foldLineSet.append(new CPLine(1, -200.0, 200.0, 200.0, 200.0));
        foldLineSet.append(new CPLine(1, 200.0, -200.0, 200.0, 200.0));
    }
}
 