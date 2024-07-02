import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * FontMenu class extends JDialog to provide a font settings menu for the Notepad application.
 */
public class FontMenu extends JDialog {
    // Reference to the GUI instance to update text area font and color
    private GUI source;

    // Components for font settings
    private JTextField currentFontField, currentFontStyleField, currentFontSizeField;
    private JPanel currentColorBox;

    /**
     * Constructor to initialize the FontMenu dialog.
     *
     * @param source The GUI instance that launched this menu.
     */
    public FontMenu(GUI source){
        this.source = source;
        setTitle("Font Settings");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(425, 350);
        setLocationRelativeTo(source); // Center the dialog relative to the GUI
        setModal(true); // Dialog is modal (blocks input to other windows)

        // Use absolute positioning for precise layout control
        setLayout(null);

        addMenuComponents(); // Add all components to the dialog
    }

    /**
     * Adds all menu components to the dialog.
     */
    private void addMenuComponents(){
        addFontChooser(); // Add font chooser components
        addFontStyleChooser(); // Add font style chooser components
        addFontSizeChooser(); // Add font size chooser components
        addFontColorChooser(); // Add font color chooser components

        // Apply button - applies selected font settings to the text area
        JButton applyButton = new JButton("Apply");
        applyButton.setBounds(230, 265, 75, 25);
        applyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get selected font settings
                String fontType = currentFontField.getText();
                int fontStyle = getFontStyle(currentFontStyleField.getText());
                int fontSize = Integer.parseInt(currentFontSizeField.getText());
                Color fontColor = currentColorBox.getBackground();

                // Create new font based on selected settings
                Font newFont = new Font(fontType, fontStyle, fontSize);

                // Update text area font and font color
                source.getTextArea().setFont(newFont);
                source.getTextArea().setForeground(fontColor);

                // Dispose the font menu dialog
                FontMenu.this.dispose();
            }
        });
        add(applyButton);

        // Cancel button - closes the font menu dialog without applying changes
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBounds(315, 265, 75, 25);
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Dispose the font menu dialog
                FontMenu.this.dispose();
            }
        });
        add(cancelButton);
    }

    /**
     * Adds components for choosing the font.
     */
    private void addFontChooser(){
        JLabel fontLabel = new JLabel("Font:");
        fontLabel.setBounds(10, 5, 125, 10);
        add(fontLabel);

        JPanel fontPanel = new JPanel();
        fontPanel.setBounds(10, 15, 125, 160);

        // Display current font name
        currentFontField = new JTextField(source.getTextArea().getFont().getFontName());
        currentFontField.setPreferredSize(new Dimension(125, 25));
        currentFontField.setEditable(false);
        fontPanel.add(currentFontField);

        // Display list of available fonts
        JPanel listOfFontsPanel = new JPanel();
        listOfFontsPanel.setLayout(new BoxLayout(listOfFontsPanel, BoxLayout.Y_AXIS));
        listOfFontsPanel.setBackground(Color.WHITE);

        // Scrollable list of available font names
        JScrollPane scrollPane = new JScrollPane(listOfFontsPanel);
        scrollPane.setPreferredSize(new Dimension(125, 125));

        // Retrieve all available font family names
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fontNames = ge.getAvailableFontFamilyNames();

        // Add each font name as a selectable label
        for(String fontName : fontNames){
            JLabel fontNameLabel = new JLabel(fontName);

            // Mouse listener for font selection and highlighting
            fontNameLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    currentFontField.setText(fontName); // Set selected font name
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    fontNameLabel.setOpaque(true); // Highlight on mouse hover
                    fontNameLabel.setBackground(Color.BLUE);
                    fontNameLabel.setForeground(Color.WHITE);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    fontNameLabel.setBackground(null); // Remove highlight on mouse exit
                    fontNameLabel.setForeground(null);
                }
            });

            // Add font name label to panel
            listOfFontsPanel.add(fontNameLabel);
        }
        fontPanel.add(scrollPane);

        add(fontPanel);
    }

    /**
     * Adds components for choosing the font style.
     */
    private void addFontStyleChooser(){
        JLabel fontStyleLabel = new JLabel("Font Style:");
        fontStyleLabel.setBounds(145, 5, 125, 10);
        add(fontStyleLabel);

        JPanel fontStylePanel = new JPanel();
        fontStylePanel.setBounds(145, 15, 125, 160);

        // Display current font style
        int currentFontStyle = source.getTextArea().getFont().getStyle();
        String currentFontStyleText = getFontStyleText(currentFontStyle);

        currentFontStyleField = new JTextField(currentFontStyleText);
        currentFontStyleField.setPreferredSize(new Dimension(125, 25));
        currentFontStyleField.setEditable(false);
        fontStylePanel.add(currentFontStyleField);

        // Display list of available font styles
        JPanel listOfFontStylesPanel = new JPanel();
        listOfFontStylesPanel.setLayout(new BoxLayout(listOfFontStylesPanel, BoxLayout.Y_AXIS));
        listOfFontStylesPanel.setBackground(Color.WHITE);

        // Labels for each font style option
        addFontStyleOption(listOfFontStylesPanel, "Plain", Font.PLAIN);
        addFontStyleOption(listOfFontStylesPanel, "Bold", Font.BOLD);
        addFontStyleOption(listOfFontStylesPanel, "Italic", Font.ITALIC);
        addFontStyleOption(listOfFontStylesPanel, "Bold Italic", Font.BOLD | Font.ITALIC);

        JScrollPane scrollPane = new JScrollPane(listOfFontStylesPanel);
        scrollPane.setPreferredSize(new Dimension(125, 125));
        fontStylePanel.add(scrollPane);

        add(fontStylePanel);
    }

    /**
     * Helper method to add a font style option to the panel.
     *
     * @param panel The panel to add the font style option to.
     * @param text The text to display for the font style option.
     * @param style The font style constant.
     */
    private void addFontStyleOption(JPanel panel, String text, int style){
        JLabel styleLabel = new JLabel(text);
        styleLabel.setFont(new Font("Dialog", style, 12));

        styleLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                currentFontStyleField.setText(text); // Set selected font style
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                styleLabel.setOpaque(true); // Highlight on mouse hover
                styleLabel.setBackground(Color.BLUE);
                styleLabel.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                styleLabel.setBackground(null); // Remove highlight on mouse exit
                styleLabel.setForeground(null);
            }
        });

        panel.add(styleLabel);
    }

    /**
     * Converts font style constant to text representation.
     *
     * @param style The font style constant.
     * @return The text representation of the font style.
     */
    private String getFontStyleText(int style){
        switch(style){
            case Font.PLAIN:
                return "Plain";
            case Font.BOLD:
                return "Bold";
            case Font.ITALIC:
                return "Italic";
            default: // Font.BOLD | Font.ITALIC
                return "Bold Italic";
        }
    }

    /**
     * Converts font style text to constant value.
     *
     * @param styleText The text representation of the font style.
     * @return The font style constant.
     */
    private int getFontStyle(String styleText){
        switch(styleText){
            case "Plain":
                return Font.PLAIN;
            case "Bold":
                return Font.BOLD;
            case "Italic":
                return Font.ITALIC;
            default: // "Bold Italic"
                return Font.BOLD | Font.ITALIC;
        }
    }

    /**
     * Adds components for choosing the font size.
     */
    private void addFontSizeChooser(){
        JLabel fontSizeLabel = new JLabel("Font Size: ");
        fontSizeLabel.setBounds(275, 5, 125, 10);
        add(fontSizeLabel);

        JPanel fontSizePanel = new JPanel();
        fontSizePanel.setBounds(275, 15, 125, 160);

        // Display current font size
        currentFontSizeField = new JTextField(Integer.toString(source.getTextArea().getFont().getSize()));
        currentFontSizeField.setPreferredSize(new Dimension(125, 25));
        currentFontSizeField.setEditable(false);
        fontSizePanel.add(currentFontSizeField);

        // Display list of available font sizes
        JPanel listOfFontSizesPanel = new JPanel();
        listOfFontSizesPanel.setLayout(new BoxLayout(listOfFontSizesPanel, BoxLayout.Y_AXIS));
        listOfFontSizesPanel.setBackground(Color.WHITE);

        // Add labels for each available font size
        for(int i = 8; i <= 72; i+= 2){
            JLabel fontSizeValueLabel = new JLabel(Integer.toString(i));

            fontSizeValueLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    currentFontSizeField.setText(fontSizeValueLabel.getText()); // Set selected font size
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    fontSizeValueLabel.setOpaque(true); // Highlight on mouse hover
                    fontSizeValueLabel.setBackground(Color.BLUE);
                    fontSizeValueLabel.setForeground(Color.WHITE);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    fontSizeValueLabel.setBackground(null); // Remove highlight on mouse exit
                    fontSizeValueLabel.setForeground(null);
                }
            });

            listOfFontSizesPanel.add(fontSizeValueLabel);
        }

        JScrollPane scrollPane = new JScrollPane(listOfFontSizesPanel);
        scrollPane.setPreferredSize(new Dimension(125, 125));
        fontSizePanel.add(scrollPane);

        add(fontSizePanel);
    }

    /**
     * Adds components for choosing the font color.
     */
    private void addFontColorChooser(){
        // Display current text color
        currentColorBox = new JPanel();
        currentColorBox.setBounds(175, 200, 23, 23);
        currentColorBox.setBackground(source.getTextArea().getForeground());
        currentColorBox.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        add(currentColorBox);

        JButton chooseColorButton = new JButton("Choose Color");
        chooseColorButton.setBounds(10, 200, 150, 25);
        chooseColorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Show color chooser dialog and update selected color
                Color selectedColor = JColorChooser.showDialog(FontMenu.this, "Select a color", Color.BLACK);
                currentColorBox.setBackground(selectedColor);
            }
        });
        add(chooseColorButton);
    }
}
