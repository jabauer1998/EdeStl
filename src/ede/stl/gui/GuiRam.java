package ede.stl.gui;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.swing.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.text.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import ede.stl.gui.GuiLineNumberGutter;
import javax.swing.border.Border;

public class GuiRam extends JPanel {
    private ArrayList<JLabel> Bytes;
    private ArrayList<JLabel> Addresses;
    
    private int NumberOfBytes;
    private int NumberRowsRounded;
    private int BytesPerRow;

    private JScrollPane Pane;
    private GuiRams.AddressFormat AddrFormat;
    private GuiRams.MemoryFormat MemFormat;
    private boolean collapsed;
    
    private double screenWidth;
    private double screenHeight;

    private JLabel dragHandle;
    private TitledBorder border;
    private String name;
    private JPanel bottomPane;
    private int currentHeight = 300;

    public GuiRam(String name, int numBytes, int BytesPerRow, GuiRams.AddressFormat AddrFormat, GuiRams.MemoryFormat MemFormat, double Width, double Height){
        this.setLayout(new BorderLayout(0,0));
        this.BytesPerRow = BytesPerRow;

        this.MemFormat = MemFormat;
        this.AddrFormat = AddrFormat;
	this.collapsed = false;
	this.name = name;

	this.border = new TitledBorder(name + " [-]");
	border.setTitleColor(Color.DARK_GRAY);
	setBorder(border);

        this.Pane = new JScrollPane(this);
        this.Pane.setPreferredSize(new Dimension((int)Width, (int)Height));
        this.Pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.Pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

	JButton collapseBtn = new JButton("-");
        collapseBtn.setMargin(new Insets(0, 4, 0, 4));
        collapseBtn.setToolTipText("Collapse/Expand");
        collapseBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toggleCollapsed();
                collapseBtn.setText(collapsed ? "+" : "-");
            }
	});

	this.bottomPane = new JPanel();
	this.bottomPane.setLayout(new BoxLayout(this.bottomPane, BoxLayout.Y_AXIS));

	JPanel resizeHandle = new JPanel();
        resizeHandle.setPreferredSize(new Dimension(0, 6));
        resizeHandle.setBackground(new Color(180, 180, 180));
        resizeHandle.setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
        resizeHandle.setToolTipText("Drag to resize");
        final GuiRam self = this;
        resizeHandle.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                resizeHandle.setBackground(new Color(130, 130, 130));
            }
            public void mouseExited(MouseEvent e) {
                resizeHandle.setBackground(new Color(180, 180, 180));
            }
        });
        resizeHandle.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (collapsed) return;
                Point p = SwingUtilities.convertPoint(resizeHandle, e.getPoint(), self.getParent());
                int newHeight = p.y - self.getY();
                if (newHeight < 80) newHeight = 80;
                currentHeight = newHeight;
                setPreferredSize(new Dimension(getWidth(), currentHeight));
                setMaximumSize(new Dimension(Integer.MAX_VALUE, currentHeight));
                revalidate();
                if (getParent() != null) {
                    getParent().revalidate();
                    getParent().repaint();
                }
            }
        });

	this.add(collapseBtn, BorderLayout.NORTH);
	this.add(bottomPane, BorderLayout.CENTER);
	this.add(resizeHandle, BorderLayout.SOUTH);
	
	
        Bytes = new ArrayList<>();
        Addresses = new ArrayList<>();
        
        this.screenWidth = Width;
        this.screenHeight = Height;

	
	setMemory(numBytes);
    }

    private void toggleCollapsed() {
        collapsed = !collapsed;
        bottomPane.setVisible(!collapsed);
        Component south = ((BorderLayout) getLayout()).getLayoutComponent(BorderLayout.SOUTH);
        if (south != null) south.setVisible(!collapsed);
        if (collapsed) {
            border.setTitle(name + " [+]");
        } else {
            border.setTitle(name + " [-]");
        }
        revalidate();
        repaint();
        if (getParent() != null) {
            getParent().revalidate();
            getParent().repaint();
        }
    }
    
    private void setMemory(int numBytes) {
        this.NumberRowsRounded = (int)Math.ceil((double)numBytes / BytesPerRow);
        this.NumberOfBytes = this.NumberRowsRounded * BytesPerRow;
        
        int Byte = 0;
        for(int Row = 0; Row < this.NumberRowsRounded; Row++){
            JPanel AddressToMemory = new JPanel(new BorderLayout());
            JPanel RowOfMemory = new JPanel();
            RowOfMemory.setLayout(new BoxLayout(RowOfMemory, BoxLayout.X_AXIS));

            JLabel addrLabel;
            if(this.AddrFormat == GuiRams.AddressFormat.HEXIDECIMAL){
                addrLabel = new JLabel(Integer.toHexString(Byte));
            } else if(this.AddrFormat == GuiRams.AddressFormat.BINARY){
                addrLabel = new JLabel(Integer.toBinaryString(Byte));
            } else if(this.AddrFormat == GuiRams.AddressFormat.OCTAL){
                addrLabel = new JLabel(Integer.toOctalString(Byte));
            } else {
                addrLabel = new JLabel(Integer.toString(Byte));
            }
            Addresses.add(addrLabel);
            
            for(int i = 0; i < this.BytesPerRow; i++, Byte++){
                JLabel byteLabel;
                if(this.MemFormat == GuiRams.MemoryFormat.HEXADECIMAL){
                    byteLabel = new JLabel("00");
		    byteLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
                } else {
                    byteLabel = new JLabel("00000000");
                }
                byteLabel.setHorizontalAlignment(SwingConstants.CENTER);
                Bytes.add(byteLabel);
                RowOfMemory.add(byteLabel);
            }
            addrLabel.setHorizontalAlignment(SwingConstants.LEFT);
            addrLabel.setPreferredSize(new Dimension((int)(screenWidth/3), 20));
            AddressToMemory.add(addrLabel, BorderLayout.WEST);
            AddressToMemory.add(RowOfMemory, BorderLayout.CENTER);
            AddressToMemory.setPreferredSize(new Dimension((int)screenWidth, 20));
            AddressToMemory.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
            bottomPane.add(AddressToMemory);
        }
        this.revalidate();
        this.repaint();
    }

    public void LoadProgram(String Program){
        Program = Program.trim();
        int Length = Program.length();
        List<String> Results = new LinkedList<String>(); 
        for (int i = 0; i < Length; i += 8) {
            Results.add(Program.substring(i, Math.min(Length, i + 8)));
        }

        for(int i = 0; i < Results.size() && i < NumberOfBytes; i++){
            String First = Results.remove(0);
            Bytes.get(i).setText(First);
        }
    }

    public JScrollPane getScrollPane(){
        return Pane;
    }

    public void setMemoryValue(int address, long dataValue){
        JLabel Byte = Bytes.get(address);
        if(MemFormat == GuiRams.MemoryFormat.BINARY){
            String asString = Long.toBinaryString(dataValue);
            if(asString.length() > 8){
                asString = asString.substring(asString.length() - 8);
            } else if(asString.length() < 8){
                StringBuilder Builder = new StringBuilder();
                int NumberOfZeros = 8 - asString.length();
                for(int i = 0; i < NumberOfZeros; i++){
                    Builder.append('0');
                }
                Builder.append(asString);
                asString = Builder.toString();
            }
            Byte.setText(asString);
        } else {
            String asString = Long.toHexString(dataValue);
            if(asString.length() > 2){
                asString = asString.substring(asString.length() - 2);
            } else if(asString.length() < 2){
                StringBuilder Builder = new StringBuilder();
                int NumberOfZeros = 2 - asString.length();
                for(int i = 0; i < NumberOfZeros; i++){
                    Builder.append('0');
                }
                Builder.append(asString);
                asString = Builder.toString();
            }
            Byte.setText(asString);
        }
    }

    public long getMemoryValue(int address){
        JLabel Byte = Bytes.get(address);
        String text = Byte.getText();
        
        if(MemFormat == GuiRams.MemoryFormat.BINARY){
            return Long.parseLong(text, 2);
        } else {
            return Long.parseLong(text, 16);
        }
    }

    public void clearMemory(){
        for(int i = 0; i < NumberOfBytes; i++){
            this.setMemoryValue(i, 0);
        }
    }
}
