package ucr.jade.agents.example.gui;

import jade.core.AID;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import ucr.jade.agents.example.agent.SellerAgent;

/**
 * 
 */
public class SaleGui extends JFrame {
    
    private SellerAgent myAgent;
    private JTextField productName, productPrice;

    public SaleGui(SellerAgent a) {
        super(a.getLocalName());
        myAgent = a;

        JPanel p = new JPanel();
        p.setLayout(new GridLayout(2, 2));
        p.add(new JLabel("Name of product: "));
        productName = new JTextField(15);
        p.add(productName);
        
        p.add(new JLabel("Price: "));
        productPrice = new JTextField(15);
        p.add(productPrice);
        
        getContentPane().add(p, BorderLayout.CENTER);

        JButton addButton = new JButton("Add");
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                try {
                    String title = productName.getText().trim();
                    String price = productPrice.getText().trim();
                    myAgent.updateInventory(title, Integer.parseInt(price));
                    productName.setText("");
                    productPrice.setText("");
                    JOptionPane.showMessageDialog(SaleGui.this, "Product added to inventory!");
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(SaleGui.this, "Invalid value " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        p = new JPanel();
        p.add(addButton);
        getContentPane().add(p, BorderLayout.SOUTH);

        // Make the agent terminate when the user closes
        // the GUI using the button on the upper right corner
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                myAgent.doDelete();
            }
        });

        setResizable(false);
    }

    public void showGui() {
        pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int centerX = (int) screenSize.getWidth() / 2;
        int centerY = (int) screenSize.getHeight() / 2;
        setLocation(centerX - getWidth() / 2, centerY - getHeight() / 2);
        super.setVisible(true);
    }
    
}
