import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.io.*; // Changed to import all IO classes for File reading/writing
import com.formdev.flatlaf.extras.FlatSVGIcon;

public class LoginFrame extends JFrame {


    private JTextField usernameField;
    private JPasswordField passwordField;
    private JCheckBox rememberMeBox; // NEW

    // Premium Colors
    private final Color darkBlueColor = new Color(28, 70, 117);
    private final Color distinctBlue = new Color(59, 130, 246);

    // File path for saving the remembered credentials
    private final String REMEMBER_FILE = "remember.txt"; // NEW

    public LoginFrame() {


        // Create a list of icons in different sizes to ensure Windows picks the sharpest one
        java.util.List<Image> icons = new java.util.ArrayList<>();
//        icons.add(new ImageIcon("src/main/resources/logo.png").getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));



        setIconImages(icons);

        setTitle("MyWallet - Login");

        setMinimumSize(
                new Dimension(
                        1200,
                        700
                )
        );

        setSize(1400,800);

        setLocationRelativeTo(null);

        SwingUtilities.invokeLater(() ->
                setExtendedState(
                        JFrame.MAXIMIZED_BOTH
                )
        );

        JPanel mainScreen = new CustomGradientPanel();
        mainScreen.setLayout(new GridBagLayout());

        // --- The Login Card ---
        JPanel loginCard = new RoundedPanel(30, Color.WHITE);
        loginCard.setLayout(null);
        Dimension cardSize =
                new Dimension(
                        400,
                        630
                );

        loginCard.setPreferredSize(cardSize);
        loginCard.setMinimumSize(cardSize);
        loginCard.setMaximumSize(cardSize);

        // 1. TOP SECTION (Blue Background - Rounded Top)
        JPanel topSection = new RoundedTopPanel(30, darkBlueColor);
        topSection.setLayout(null);
        topSection.setBounds(0, 0, 400, 200);

        // --- MAIN LOGO IMPLEMENTATION ---
        try {
            String imagePath = "src/main/resources/logo.png";

            File imgFile = new File(imagePath);
            if (!imgFile.exists()) {
                System.err.println("WARNING: Image file not found at " + imgFile.getAbsolutePath());
            }

            ImageIcon originalIcon = new ImageIcon(imagePath);
            int logoSize = 140;
            Image scaledImage = originalIcon.getImage().getScaledInstance(logoSize, logoSize, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(scaledImage);

            JLabel iconLabel = new JLabel(scaledIcon);
            iconLabel.setBounds(140, 10, logoSize, logoSize);
            topSection.add(iconLabel);

        } catch (Exception e) {
            JLabel iconLabel = new JLabel("👤", SwingConstants.CENTER);
            iconLabel.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 60));
            iconLabel.setForeground(Color.WHITE);
            iconLabel.setBounds(0, 25, 400, 60);
            topSection.add(iconLabel);
        }

        JLabel titleLabel = new JLabel("WELCOME", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(0, 135, 400, 30);
        topSection.add(titleLabel);

        JLabel subtitleLabel = new JLabel("Sign in to continue to MyWallet", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitleLabel.setForeground(new Color(200, 200, 200));
        subtitleLabel.setBounds(0, 165, 400, 20);
        topSection.add(subtitleLabel);

        loginCard.add(topSection);

        // 2. BOTTOM SECTION (White Background - Form Fields)

        // Username
        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(new Font("Arial", Font.BOLD, 13));
        userLabel.setForeground(Color.DARK_GRAY);
        userLabel.setBounds(50, 210, 300, 20);
        loginCard.add(userLabel);

        usernameField = new JTextField();
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameField.setBounds(50, 235, 300, 40);
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(0, 10, 0, 10)
        ));
        loginCard.add(usernameField);

        // Password Label
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Arial", Font.BOLD, 13));
        passLabel.setForeground(Color.DARK_GRAY);
        passLabel.setBounds(50, 290, 300, 20);
        loginCard.add(passLabel);

        // --- Unified Password Field Container ---
        JPanel passwordContainer = new JPanel(new BorderLayout());
        passwordContainer.setBounds(50, 315, 300, 40);
        passwordContainer.setBackground(Color.WHITE);
        passwordContainer.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(0, 5, 0, 5)
        ));

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setBorder(null);
        passwordField.setOpaque(false);

        Icon showIcon = new FlatSVGIcon("show.svg", 20, 20);
        Icon hideIcon = new FlatSVGIcon("hide.svg", 20, 20);

        JButton showPassBtn = new JButton(showIcon);
        showPassBtn.setFocusPainted(false);
        showPassBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        showPassBtn.setBackground(Color.WHITE);
        showPassBtn.setBorder(null);
        showPassBtn.setPreferredSize(new Dimension(30, 40));

        passwordContainer.add(passwordField, BorderLayout.CENTER);
        passwordContainer.add(showPassBtn, BorderLayout.EAST);

        loginCard.add(passwordContainer);

        showPassBtn.addActionListener(e -> {
            if (passwordField.getEchoChar() == '\u0000') {
                passwordField.setEchoChar('•');
                showPassBtn.setIcon(showIcon);
            } else {
                passwordField.setEchoChar((char) 0);
                showPassBtn.setIcon(hideIcon);
            }
        });

        // --- NEW: Remember Me Checkbox ---
        rememberMeBox = new JCheckBox("Remember Me");
        rememberMeBox.setFont(new Font("Arial", Font.PLAIN, 12));
        rememberMeBox.setForeground(Color.DARK_GRAY);
        rememberMeBox.setBackground(Color.WHITE);
        rememberMeBox.setFocusPainted(false);
        rememberMeBox.setBounds(46, 365, 120, 20);
        loginCard.add(rememberMeBox);

        // Forgot Password (Shifted right to share row with Remember Me)
        JLabel forgotLabel = new JLabel("FORGOT PASSWORD?", SwingConstants.RIGHT);
        forgotLabel.setFont(new Font("Arial", Font.BOLD, 11));
        forgotLabel.setForeground(Color.GRAY);
        forgotLabel.setBounds(180, 365, 170, 20);
        forgotLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginCard.add(forgotLabel);

        // Login Button (Shifted down)
        JButton loginBtn = new JButton("LOG IN");
        loginBtn.setFont(new Font("Arial", Font.BOLD, 14));
        loginBtn.setBackground(darkBlueColor);
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.setBounds(50, 415, 300, 45);
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginCard.add(loginBtn);

        // Register Button (Shifted down)
        JButton registerBtn = new JButton("REGISTER");
        registerBtn.setFont(new Font("Arial", Font.BOLD, 14));
        registerBtn.setBackground(distinctBlue);
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setFocusPainted(false);
        registerBtn.setBounds(50, 470, 300, 45);
        registerBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginCard.add(registerBtn);

        // Footer Text (Shifted down)
        JLabel footerLabel = new JLabel("© MyWallet 2026 | Secure Login", SwingConstants.CENTER);
        footerLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        footerLabel.setForeground(Color.LIGHT_GRAY);
        footerLabel.setBounds(0, 590, 400, 20);
        loginCard.add(footerLabel);

        mainScreen.add(loginCard, new GridBagConstraints());
        add(mainScreen);

        // --- ACTIONS ---

        loginBtn.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            UserDAO dao = new UserDAO();
            if (dao.loginUser(username, password)) {

                // Save credentials if Remember Me is checked
                handleRememberMe(username, password);

                dispose();
                new DashboardFrame(username);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        registerBtn.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            if(username.isEmpty() || password.isEmpty()){
                JOptionPane.showMessageDialog(this, "Please enter a Username and Password to register.", "Notice", JOptionPane.WARNING_MESSAGE);
                return;
            }
            UserDAO dao = new UserDAO();
            if (dao.registerUser(username, password)) {
                JOptionPane.showMessageDialog(this, "Registration successful! You can now click LOG IN.");
            } else {
                JOptionPane.showMessageDialog(this, "Username might already exist.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        forgotLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String usernameInput = JOptionPane.showInputDialog(LoginFrame.this, "Enter your Username to reset password:", "Password Reset", JOptionPane.QUESTION_MESSAGE);
                if (usernameInput != null && !usernameInput.trim().isEmpty()) {
                    String username = usernameInput.trim();
                    UserDAO dao = new UserDAO();
                    if (dao.checkUserExists(username)) {
                        JPasswordField newPasswordField = new JPasswordField(20);
                        int action = JOptionPane.showConfirmDialog(LoginFrame.this, newPasswordField, "Enter your New Password:", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                        if (action == JOptionPane.OK_OPTION) {
                            String newPass = new String(newPasswordField.getPassword());
                            if (!newPass.isEmpty()) {
                                if (dao.updatePassword(username, newPass)) {
                                    JOptionPane.showMessageDialog(LoginFrame.this, "Password updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                                } else {
                                    JOptionPane.showMessageDialog(LoginFrame.this, "Failed to update database.", "Error", JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(LoginFrame.this, "Username not found in our records.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        // Load Remembered Data AFTER UI is Built
        loadRememberedUser(loginBtn);

        setVisible(true);
    }

    // --- HELPER METHODS FOR REMEMBER ME ---

    private void handleRememberMe(String username, String password) {
        File file = new File(REMEMBER_FILE);
        if (rememberMeBox.isSelected()) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(username + "," + password);
            } catch (IOException ex) {
                System.err.println("Could not save remember me setting: " + ex.getMessage());
            }
        } else {
            if (file.exists()) {
                file.delete();
            }
        }
    }

    private void loadRememberedUser(JButton loginBtn) {
        File file = new File(REMEMBER_FILE);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String data = reader.readLine();
                if (data != null && data.contains(",")) {
                    String[] parts = data.split(",", 2);
                    usernameField.setText(parts[0]);
                    passwordField.setText(parts[1]);
                    rememberMeBox.setSelected(true);
                }
            } catch (IOException ex) {
                System.err.println("Could not load remember me setting: " + ex.getMessage());
            }
        }
    }
}

class CustomGradientPanel extends JPanel {
    private final Color colorStart = new Color(161, 196, 253);
    private final Color colorEnd = new Color(194, 233, 251);

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        float startX = 0;
        float startY = 0;
        float endX = width;
        float endY = height * 0.6f;

        LinearGradientPaint gradient = new LinearGradientPaint(
                new Point2D.Float(startX, startY),
                new Point2D.Float(endX, endY),
                new float[]{0.0f, 1.0f},
                new Color[]{colorStart, colorEnd}
        );

        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, width, height);
    }
}

class RoundedPanel extends JPanel {
    private int cornerRadius = 30;
    private Color backgroundColor;

    public RoundedPanel(int radius, Color bgColor) {
        super();
        this.cornerRadius = radius;
        this.backgroundColor = bgColor;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(backgroundColor);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);

        g2.setColor(new Color(220, 220, 220));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);

        g2.dispose();
    }
}

class RoundedTopPanel extends JPanel {
    private int cornerRadius = 30;
    private Color backgroundColor;

    public RoundedTopPanel(int radius, Color bgColor) {
        super();
        this.cornerRadius = radius;
        this.backgroundColor = bgColor;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(backgroundColor);

        g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
        g2.fillRect(0, cornerRadius, getWidth(), getHeight() - cornerRadius);

        g2.dispose();
    }
}