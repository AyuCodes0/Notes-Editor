import javax.swing.*;

/**
 * Entry point for the Notepad application.
 */
public class App {

    /**
     * Main method to start the application.
     * @param args Command line arguments (not used in this application).
     */
    public static void main(String[] args){
        SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run(){
                try{
                    UIManager.setLookAndFeel("com.jtattoo.plaf.acryl.AcrylLookAndFeel");
                    new GUI().setVisible(true);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
}
