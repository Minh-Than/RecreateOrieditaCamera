package mainPkg;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;

import components.CPLine;
import model.CPCamera;

import javax.swing.JButton;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FileDialog;
import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Test {
    int mouseX, mouseY, displayOffsetX, displayOffsetY;
    

    public static void main(String[] argv){
        CPCamera cpCamera = CPCamera.getInstance();

        JFrame frame = new JFrame("camera testing");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setLayout(new BorderLayout());

        List<CPLine> cpLines = new ArrayList<>();
        setupDefaultSqaure(cpLines);
    
        JPanel mainPanel = new DrawPanel(cpLines);
        
        JButton openFileBtn = new JButton("Open file");
        openFileBtn.addActionListener(e -> {
            readFile(frame, cpLines);
            cpCamera.initialize();
            mainPanel.requestFocus();
            mainPanel.repaint();
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(new MatteBorder(0, 0, 1, 0, Color.BLACK));
        buttonPanel.add(openFileBtn);

        frame.add(buttonPanel, BorderLayout.NORTH);
        frame.add(mainPanel, BorderLayout.CENTER);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        frame.setVisible(true);
        mainPanel.requestFocus();
        
    }

    public static void readFile(JFrame frame, List<CPLine> cpLines){
        if(cpLines.size() != 0) cpLines.clear();
        
        FileDialog fileDialog = new FileDialog(frame, "Select a file", FileDialog.LOAD);

        // Filter files with .cp extension
        fileDialog.setFilenameFilter(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".cp");
            }
        });

        fileDialog.setVisible(true);

        if (fileDialog.getFile() == null) System.out.println("File selection cancelled.");
        
        File test = new File(fileDialog.getDirectory(), fileDialog.getFile());

        try (BufferedReader br = Files.newBufferedReader(test.toPath())) {
            // Process lines using parallel stream for faster processing
            cpLines.addAll(br.lines().parallel()
                .map(line -> {
                    String[] coords = line.split("\\s+");
                    return new CPLine(
                        Integer.parseInt(coords[0]),
                        Double.parseDouble(coords[1]),
                        Double.parseDouble(coords[2]),
                        Double.parseDouble(coords[3]),
                        Double.parseDouble(coords[4])
                    );
                })
                .collect(Collectors.toList())
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setupDefaultSqaure(List<CPLine> cpLines){
        cpLines.add(new CPLine(1, -200.0, -200.0, 200.0, -200.0));
        cpLines.add(new CPLine(1, -200.0, -200.0, -200.0, 200.0));
        cpLines.add(new CPLine(1, -200.0, 200.0, 200.0, 200.0));
        cpLines.add(new CPLine(1, 200.0, -200.0, 200.0, 200.0));
    }
}
 