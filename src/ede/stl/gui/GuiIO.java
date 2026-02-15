package ede.stl.gui;

import java.util.HashMap;
import javax.swing.*;
import java.awt.*;

public class GuiIO extends JPanel {
    private JTabbedPane TabPane;

    private double actualWidth;
    private double actualHeight;

    private HashMap<String, JTextArea> IoPaneMap;
    
    public GuiIO(double Width, double Height){
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        TabPane = new JTabbedPane();
        TabPane.setMaximumSize(new Dimension((int)Width, (int)Height));
        TabPane.setPreferredSize(new Dimension((int)Width, (int)Height));

        IoPaneMap = new HashMap<>();
        this.actualWidth = Width;
        this.actualHeight = Height;
    }

    public void AddIoSection(String TabTitle, String... PaneTitles){
        JPanel IoPanes = new JPanel();
        IoPanes.setLayout(new BoxLayout(IoPanes, BoxLayout.Y_AXIS));

        for(String PaneTitle : PaneTitles){
            JPanel IoPaneWithLabel = new JPanel();
            IoPaneWithLabel.setLayout(new BoxLayout(IoPaneWithLabel, BoxLayout.Y_AXIS));
            JLabel Name = new JLabel(PaneTitle);
            JTextArea Area = new JTextArea();
            Area.setPreferredSize(new Dimension((int)actualWidth, (int)(actualHeight/PaneTitles.length)));

            IoPaneMap.put(PaneTitle, Area);
            IoPaneWithLabel.add(Name);
            IoPaneWithLabel.add(new JScrollPane(Area));
            IoPanes.add(IoPaneWithLabel);
        }

        JScrollPane SPane = new JScrollPane(IoPanes);

        TabPane.addTab(TabTitle, SPane);
    }

    public JTabbedPane getTabPane(){
        return TabPane;
    }

    public void writeIoText(String textAreaName, String toWrite){
        JTextArea ioArea = IoPaneMap.get(textAreaName);
        ioArea.setText(toWrite);
    }

    public String readIoText(String textAreaName){
        JTextArea ioArea = IoPaneMap.get(textAreaName);
        return ioArea.getText();
    }

    public void appendIoText(String textAreaName, String toAppend){
        JTextArea ioArea = IoPaneMap.get(textAreaName);
        StringBuilder appender = new StringBuilder();
        appender.append(ioArea.getText());
        appender.append(toAppend);
        ioArea.setText(appender.toString());
    }
}
