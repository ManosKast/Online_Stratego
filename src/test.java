import java.util.Random;
import javax.swing.*;
import java.awt.*;

public class test {
    public static void main(String[] args){
        // Create the window
        JWindow window = new JWindow();
        window.setSize(400, 600);

        // Scroll pane for holding vehicle panels
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Panel that contains all vehicle panels
        JPanel vehiclesPanel = new JPanel();
        vehiclesPanel.setLayout(new BoxLayout(vehiclesPanel, BoxLayout.Y_AXIS));

        // Sample data - replace with actual vehicle data
        String[] vehicleNames = {"Car Model 1", "Car Model 2", "Car Model 3", "Car Model 4"}; // Add all vehicle names
        int[] vehicleAvailability = {5, 3, 2, 1}; // Add availability counts
        String[] vehicleImages = {"path/to/car1.jpg", "path/to/car2.jpg"}; // Add paths to images

        // Create and add a panel for each vehicle
        for (int j = 0; j < 5; ++j) {
            for (int i = 0; i < vehicleNames.length; i++) {
                JPanel vehiclePanel = new JPanel();
                vehiclePanel.setLayout(new BorderLayout());
                vehiclePanel.setBorder(BorderFactory.createLineBorder(Color.black));

                JLabel nameLabel = new JLabel(vehicleNames[i], SwingConstants.CENTER);
                //ImageIcon vehicleIcon = new ImageIcon(vehicleImages[i]);
                JButton photoButton = new JButton();
                photoButton.setPreferredSize(new Dimension(window.getWidth(), (int)(window.getHeight())/3));
                JLabel availabilityLabel = new JLabel("Available: " + vehicleAvailability[i], SwingConstants.CENTER);
                availabilityLabel.setBorder((BorderFactory.createEmptyBorder(0, 0, 10, 0)));

                vehiclePanel.add(nameLabel, BorderLayout.NORTH);
                vehiclePanel.add(photoButton, BorderLayout.CENTER);
                vehiclePanel.add(availabilityLabel, BorderLayout.SOUTH);

                vehiclesPanel.add(vehiclePanel);
            }
        }
        // Add the vehicles panel to the scroll pane
        scrollPane.getViewport().add(vehiclesPanel);

        // Add the scroll pane to the window
        window.getContentPane().add(scrollPane);

        // Finalize and display the window
        window.setVisible(true);
    }
}
