import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class puzzleGame extends JFrame {
    JPanel panel;
    private final int numRows = 5, numCols = 3;
    private final int width = 900;
    private int height = 900;
    private List<fancyButton> buttons;
    private List<fancyButton> buttonsSolution;
    private BufferedImage image;
    private BufferedImage resizedImage;

    public puzzleGame() {
        super("Puzzle Game");//setTitle("Puzzle Game")

        panel = new JPanel(); //create new instance of JPanel
        panel.setLayout(new GridLayout(numRows, numCols));
        add(panel);

        try {
            image = loadImage();

            //resize the image to fit given width and height
            int sourceWidth = image.getWidth();
            int sourceHeight = image.getHeight();
            //resize height so height/width = sourceHeight/sourceWidth
            height =(int)((double) sourceHeight / sourceWidth * width);
            resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

            var g = resizedImage.createGraphics();
            g.drawImage(image, 0, 0, width, height, null);
            g.dispose();

        } catch(IOException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(),
             "Error loading this title", JOptionPane.ERROR_MESSAGE);
        }
        

        buttons = new ArrayList<fancyButton>();
        //instantiate buttons and add them to the list
        for(int i = 0; i < numCols * numRows; i++)
        {
            //get row and col from i
            int row = i / numCols, col = i % numCols;

            Image imageSlice = createImage(new FilteredImageSource(resizedImage.getSource(),
            new CropImageFilter(col * width / numCols, row * height / numRows, width / numCols, height / numRows)));

            fancyButton btn = new fancyButton();//instantiate our button

            if(i == numCols * numRows -1) {//the last button
                btn.setBorderPainted(false);
                btn.setContentAreaFilled(false);
            }
            else {
                btn.setIcon(new ImageIcon(imageSlice));
            }


            buttons.add(btn);//add to button list
            btn.setBorder(BorderFactory.createLineBorder(Color.GRAY));//import java.awt.Color
            btn.addActionListener(e -> myClickEventHandler(e));
        }

        buttonsSolution = List.copyOf(buttons); //create the solution copy of all buttons
        Collections.shuffle(buttons); //shuffle the buttons

        for(var btn: buttons){
            panel.add(btn);//add btn to panel
        }

        setSize(width, height);
        setResizable(false);//not a resizeable window
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void myClickEventHandler(ActionEvent e) {//import java.awt.event.ActionEvent;
        
        fancyButton btnClicked = (fancyButton) e.getSource();//what button was clicked?
        int i = buttons.indexOf(btnClicked); // find the button's index in the list:

        // where is our button at in the grid - which row and column
        int row = i / numCols;
        int col = i % numCols;

        int iempty = -1;  //find the empty button in the list:
        for(int j = 0; j < buttons.size(); j++)
        {
            if(buttons.get(j).getIcon() == null) // the button with no icon
            {
                iempty = j;
                break;
            }
        }
        // where is our button at in the grid - which row and column
        int rowEmpty = iempty / numCols;  // find it's row and column
        int colEmpty = iempty % numCols;

        //check if clicked button is adjacent (same row + adjacent cols, or same col + adjacent rows) to the empty one
        if((row ==rowEmpty && Math.abs(col - colEmpty) ==1) || (col == colEmpty && Math.abs(row - rowEmpty) == 1))
        {
            Collections.swap(buttons, i, iempty);
            updateButtons();
        }

        //check for solution
        if(buttonsSolution.equals(buttons)){
            JOptionPane.showMessageDialog(null, "Well done!");
        }

    }

    public void updateButtons()
    {
        panel.removeAll(); //remove all buttons from panel
        //re-add them to the panel
        for(var btn : buttons) {
            panel.add(btn);
        }
        //reload the panel
        panel.validate();
    }

    private BufferedImage loadImage() throws IOException {
        return ImageIO.read(new File("Mt_Rainier.jpg"));
    }
}
