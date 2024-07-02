import javax.swing.*;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

/**
 * GUI class extends JFrame to create a simple Notepad application.
 */
public class GUI extends JFrame {

    // file explorer
    private JFileChooser fileChooser;

    // text area for editing text
    private JTextArea textArea;

    // currently opened file
    private File currentFile;

    // UndoManager for managing undo and redo actions
    private UndoManager undoManager;

    /**
     * Constructor to initialize the GUI.
     */
    public GUI() {
        super("Notepad");
        setSize(400, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Initialize file chooser
        fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("src/assets"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));

        // Initialize UndoManager
        undoManager = new UndoManager();

        // Add GUI components
        addGuiComponents();
    }

    /**
     * Adds all GUI components to the frame.
     */
    private void addGuiComponents() {
        addToolbar();

        // Text area for editing text
        textArea = new JTextArea();
        textArea.getDocument().addUndoableEditListener(new UndoableEditListener() {
            @Override
            public void undoableEditHappened(UndoableEditEvent e) {
                undoManager.addEdit(e.getEdit());
            }
        });

        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Adds the toolbar to the frame.
     */
    private void addToolbar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        // Menu bar
        JMenuBar menuBar = new JMenuBar();
        toolBar.add(menuBar);

        // Add menus
        menuBar.add(addFileMenu());
        menuBar.add(addEditMenu());
        menuBar.add(addFormatMenu());
        menuBar.add(addViewMenu());

        add(toolBar, BorderLayout.NORTH);
    }

    /**
     * Constructs and returns the File menu.
     *
     * @return The constructed File menu.
     */
    private JMenu addFileMenu() {
        JMenu fileMenu = new JMenu("File");

        // New functionality - resets everything
        JMenuItem newMenuItem = new JMenuItem("New");
        newMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setTitle("Notepad");
                textArea.setText("");
                currentFile = null;
            }
        });
        fileMenu.add(newMenuItem);

        // Open functionality - open a text file
        JMenuItem openMenuItem = new JMenuItem("Open");
        openMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int result = fileChooser.showOpenDialog(GUI.this);
                if (result != JFileChooser.APPROVE_OPTION) return;

                try {
                    newMenuItem.doClick(); // Reset notepad

                    File selectedFile = fileChooser.getSelectedFile();
                    currentFile = selectedFile;
                    setTitle(selectedFile.getName());

                    // Read the file
                    FileReader fileReader = new FileReader(selectedFile);
                    BufferedReader bufferedReader = new BufferedReader(fileReader);
                    StringBuilder fileText = new StringBuilder();
                    String readText;
                    while ((readText = bufferedReader.readLine()) != null) {
                        fileText.append(readText).append("\n");
                    }
                    textArea.setText(fileText.toString());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        fileMenu.add(openMenuItem);

        // Save As functionality - creates a new text file and saves user text
        JMenuItem saveAsMenuItem = new JMenuItem("Save As");
        saveAsMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int result = fileChooser.showSaveDialog(GUI.this);
                if (result != JFileChooser.APPROVE_OPTION) return;

                try {
                    File selectedFile = fileChooser.getSelectedFile();
                    if (!selectedFile.getName().endsWith(".txt")) {
                        selectedFile = new File(selectedFile.getAbsolutePath() + ".txt");
                    }
                    selectedFile.createNewFile();

                    FileWriter fileWriter = new FileWriter(selectedFile);
                    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                    bufferedWriter.write(textArea.getText());
                    bufferedWriter.close();
                    fileWriter.close();

                    setTitle(selectedFile.getName());
                    currentFile = selectedFile;

                    JOptionPane.showMessageDialog(GUI.this, "Saved File!");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        fileMenu.add(saveAsMenuItem);

        // Save functionality - saves text into current text file
        JMenuItem saveMenuItem = new JMenuItem("Save");
        saveMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentFile == null) {
                    saveAsMenuItem.doClick();
                    return;
                }
                try {
                    FileWriter fileWriter = new FileWriter(currentFile);
                    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                    bufferedWriter.write(textArea.getText());
                    bufferedWriter.close();
                    fileWriter.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        fileMenu.add(saveMenuItem);

        // Exit functionality - ends program process
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GUI.this.dispose();
            }
        });
        fileMenu.add(exitMenuItem);

        return fileMenu;
    }

    /**
     * Constructs and returns the Edit menu.
     *
     * @return The constructed Edit menu.
     */
    private JMenu addEditMenu() {
        JMenu editMenu = new JMenu("Edit");

        // Undo functionality
        JMenuItem undoMenuItem = new JMenuItem("Undo");
        undoMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (undoManager.canUndo()) {
                    undoManager.undo();
                }
            }
        });
        editMenu.add(undoMenuItem);

        // Redo functionality
        JMenuItem redoMenuItem = new JMenuItem("Redo");
        redoMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (undoManager.canRedo()) {
                    undoManager.redo();
                }
            }
        });
        editMenu.add(redoMenuItem);

        // Find functionality
        JMenuItem findMenuItem = new JMenuItem("Find...");
        findMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchText = JOptionPane.showInputDialog(GUI.this, "Enter text to find:");
                if (searchText != null && !searchText.isEmpty()) {
                    String text = textArea.getText();
                    int index = text.indexOf(searchText);
                    if (index != -1) {
                        textArea.select(index, index + searchText.length());
                        textArea.requestFocus();
                    } else {
                        JOptionPane.showMessageDialog(GUI.this, "Text not found!");
                    }
                }
            }
        });
        editMenu.add(findMenuItem);

        // Replace functionality
        JMenuItem replaceMenuItem = new JMenuItem("Replace...");
        replaceMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String findText = JOptionPane.showInputDialog(GUI.this, "Enter text to find:");
                if (findText != null && !findText.isEmpty()) {
                    String replaceText = JOptionPane.showInputDialog(GUI.this, "Enter replacement text:");
                    if (replaceText != null) {
                        String text = textArea.getText();
                        text = text.replace(findText, replaceText);
                        textArea.setText(text);
                    }
                }
            }
        });
        editMenu.add(replaceMenuItem);

        return editMenu;
    }

    /**
     * Constructs and returns the Format menu.
     *
     * @return The constructed Format menu.
     */
    private JMenu addFormatMenu() {
        JMenu formatMenu = new JMenu("Format");

        // Word wrap functionality
        JCheckBoxMenuItem wordWrapMenuItem = new JCheckBoxMenuItem("Word Wrap");
        wordWrapMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean isChecked = wordWrapMenuItem.getState();
                textArea.setLineWrap(isChecked);
                textArea.setWrapStyleWord(isChecked);
            }
        });
        formatMenu.add(wordWrapMenuItem);

        // Text alignment options
        JMenu alignTextMenu = new JMenu("Align Text");

        JMenuItem alignTextLeftMenuItem = new JMenuItem("Left");
        alignTextLeftMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textArea.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
            }
        });
        alignTextMenu.add(alignTextLeftMenuItem);

        JMenuItem alignTextRightMenuItem = new JMenuItem("Right");
        alignTextRightMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
            }
        });
        alignTextMenu.add(alignTextRightMenuItem);

        formatMenu.add(alignTextMenu);

        // Font options
        JMenuItem fontMenuItem = new JMenuItem("Font...");
        fontMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new FontMenu(GUI.this).setVisible(true);
            }
        });
        formatMenu.add(fontMenuItem);

        return formatMenu;
    }

    /**
     * Constructs and returns the View menu.
     *
     * @return The constructed View menu.
     */
    private JMenu addViewMenu() {
        JMenu viewMenu = new JMenu("View");

        // Zoom options
        JMenu zoomMenu = new JMenu("Zoom");

        JMenuItem zoomInMenuItem = new JMenuItem("Zoom in");
        zoomInMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Font currentFont = textArea.getFont();
                textArea.setFont(new Font(
                        currentFont.getName(),
                        currentFont.getStyle(),
                        currentFont.getSize() + 1
                ));
            }
        });
        zoomMenu.add(zoomInMenuItem);

        JMenuItem zoomOutMenuItem = new JMenuItem("Zoom out");
        zoomOutMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Font currentFont = textArea.getFont();
                textArea.setFont(new Font(
                        currentFont.getName(),
                        currentFont.getStyle(),
                        currentFont.getSize() - 1
                ));
            }
        });
        zoomMenu.add(zoomOutMenuItem);

        JMenuItem zoomRestoreMenuItem = new JMenuItem("Restore Default Zoom");
        zoomRestoreMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Font currentFont = textArea.getFont();
                textArea.setFont(new Font(
                        currentFont.getName(),
                        currentFont.getStyle(),
                        12
                ));
            }
        });
        zoomMenu.add(zoomRestoreMenuItem);

        viewMenu.add(zoomMenu);

        return viewMenu;
    }

    /**
     * Retrieves the text area component.
     *
     * @return The JTextArea component.
     */
    public JTextArea getTextArea() {
        return textArea;
    }
}