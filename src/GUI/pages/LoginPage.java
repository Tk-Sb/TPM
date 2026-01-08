package GUI.pages;

import GUI.components.*;
import GUI.lib.EmailValidator;
import GUI.lib.PasswordValidator;
import GUI.lib.RequiredValidator;

import javax.swing.*;
import java.awt.*;

public class LoginPage extends JFrame {

    public LoginPage() {
        setTitle("TPM");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        setSize(600, 700);
        getContentPane().setBackground(new Color(9, 9, 11));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // margin around the card

        TextInputField username = new TextInputField("Username", "Enter your username");
        username.setValidator(new RequiredValidator());
        username.setValidator(new EmailValidator());

        TextInputField password = new TextInputField("Password", "Enter your username");
        password.setValidator(new RequiredValidator());
        password.setValidator(new PasswordValidator());

        PrimaryButton primaryButton = new PrimaryButton("Login", e -> {
            if (!username.getText().isEmpty() && !password.getText().isEmpty()) {
                if (username.getText().equals("admin@gmail.com") && password.getText().equals("admin123")) {
                    dispose();
                } else {
                    username.setHelperText("Username not found");
                    password.setHelperText("Password is incorrect");
                }
            }
        });

        SecondaryButton secondaryButton = new SecondaryButton("Reset", e -> {
            username.setText("");
            password.setText("");
        });

        Card card = new Card("Login in to your account", "Enter your email below to login to your account");
        card.addContent(username);
        card.addContent(password);
        card.addContent(primaryButton);
        card.addContent(secondaryButton);

        add(card, gbc);
        setVisible(true);
    }
}
