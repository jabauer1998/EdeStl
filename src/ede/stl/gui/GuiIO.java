package ede.stl.gui;

import java.util.HashMap;
import javax.swing.*;
import java.awt.*;

public class GuiIO extends JPanel {
    private JTabbedPane TabPane;

    private double actualWidth;
    private double actualHeight;

    public enum Editable{
        READ_ONLY,
        EDITABLE
    }

    private HashMap<String, JTextArea> IoPaneMap;
    private HashMap<String, JPanel> TabMap;
    private HashMap<String, Integer> TabPaneCountMap;
    
    public GuiIO(double Width, double Height){
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        TabPane = new JTabbedPane();
        TabPane.setMaximumSize(new Dimension((int)Width, (int)Height));
        TabPane.setPreferredSize(new Dimension((int)Width, (int)Height));

        IoPaneMap = new HashMap<>();
        TabMap = new HashMap<>();
        TabPaneCountMap = new HashMap<>();
        this.actualWidth = Width;
        this.actualHeight = Height;
    }

    public void AddIoSection(String TabTitle, String PaneTitle, Editable editable){
        JPanel IoPanes;
        int paneCount;

        if (TabMap.containsKey(TabTitle)) {
            IoPanes = TabMap.get(TabTitle);
            paneCount = TabPaneCountMap.get(TabTitle) + 1;
            TabPaneCountMap.put(TabTitle, paneCount);
        } else {
            IoPanes = new JPanel();
            IoPanes.setLayout(new BoxLayout(IoPanes, BoxLayout.Y_AXIS));
            TabMap.put(TabTitle, IoPanes);
            paneCount = 1;
            TabPaneCountMap.put(TabTitle, paneCount);

            JScrollPane SPane = new JScrollPane(IoPanes);
            TabPane.addTab(TabTitle, SPane);
        }

        JPanel IoPaneWithLabel = new JPanel();
        IoPaneWithLabel.setLayout(new BoxLayout(IoPaneWithLabel, BoxLayout.Y_AXIS));
        JLabel Name = new JLabel(PaneTitle);
        JTextArea Area = new JTextArea();
        Area.setLineWrap(true);
        Area.setWrapStyleWord(true);
        Area.setEditable(editable == Editable.EDITABLE);
        Area.setPreferredSize(new Dimension((int)actualWidth, (int)(actualHeight / paneCount)));

        IoPaneMap.put(PaneTitle, Area);
        IoPaneWithLabel.add(Name);
        IoPaneWithLabel.add(new JScrollPane(Area));
        IoPanes.add(IoPaneWithLabel);

        IoPanes.revalidate();
        IoPanes.repaint();
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
