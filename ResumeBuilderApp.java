// FINALS MODIFICATION - Added Swing GUI components and JSON file save/load functionality
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;

// abstract class for resume sections
abstract class ResumeSection {
    String title;
    public ResumeSection(String title) {
        this.title = title;
    }
    public String getTitle() {
        return title;
    }
    public abstract String toString();
}

// class to store personal info
class PersonalInfo {
    private String name;
    private String contact;  // String for phone numbers
    private String email;

    public PersonalInfo(String name, String contact, String email) {
        this.name = name;
        this.contact = contact;
        this.email = email;
    }

    public String getName() { return name; }
    public String getContact() { return contact; }
    public String getEmail() { return email; }
}

// education section
class Education extends ResumeSection {
    private String degree;
    private String institution;
    private int yearOfGraduation;  // int for year

    public Education(String degree, String institution, int yearOfGraduation) {
        super("Education");
        this.degree = degree;
        this.institution = institution;
        this.yearOfGraduation = yearOfGraduation;
    }

    @Override
    public String toString() {
        return degree + " | " + institution + " | " + yearOfGraduation;
    }
}

// experience section
class Experience extends ResumeSection {
    private String role;
    private String company;
    private String duration;
    private String description;

    public Experience(String role, String company, String duration, String description) {
        super("Experience");
        this.role = role;
        this.company = company;
        this.duration = duration;
        this.description = description;
    }

    public String getDescription() { return description; }

    @Override
    public String toString() {
        return role + " | " + company + " | " + duration + "\n  " + description;
    }
}

// FINALS MODIFICATION - Multi-level inheritance: Academic education class
class AcademicEducation extends Education {
    private String honors; // Optional honors/distinction
    
    public AcademicEducation(String degree, String institution, int yearOfGraduation, String honors) {
        super(degree, institution, yearOfGraduation);
        this.honors = honors;
    }
    
    public AcademicEducation(String degree, String institution, int yearOfGraduation) {
        this(degree, institution, yearOfGraduation, "");
    }
    
    @Override
    public String toString() {
        String base = super.toString();
        return honors.isEmpty() ? base : base + " (" + honors + ")";
    }
}

// skill section
class Skill extends ResumeSection {
    private String name;

    public Skill(String name) {
        super("Skill");
        this.name = name;
    }

    @Override
    public String toString() {
        return "- " + name;
    }
}

// main resume class
class Resume {
    private PersonalInfo personalInfo;
    private List<ResumeSection> sections = new ArrayList<>();

    public void setPersonalInfo(PersonalInfo pi) {
        this.personalInfo = pi;
    }

    public void addSection(ResumeSection section) {
        sections.add(section);
    }

    public List<ResumeSection> getSections() {
        return sections;
    }

    public PersonalInfo getPersonalInfo() {
        return personalInfo;
    }

    // FINALS MODIFICATION - Added method to get formatted resume as string for GUI display
    public String getFormattedResume() {
        StringBuilder sb = new StringBuilder();
        sb.append("+--------------------------------------------------+\n");
        sb.append("|                       RESUME                     |\n");
        sb.append("+--------------------------------------------------+\n\n");

        if (personalInfo != null) {
            sb.append("Name: ").append(personalInfo.getName()).append("\n");
            sb.append("Contact: ").append(personalInfo.getContact()).append("\n");
            sb.append("Email: ").append(personalInfo.getEmail()).append("\n\n");
        }

        // group sections by type
        Map<String, List<ResumeSection>> grouped = new LinkedHashMap<>();
        for (ResumeSection s : sections) {
            grouped.computeIfAbsent(s.getTitle(), k -> new ArrayList<>()).add(s);
        }

        for (String sectionTitle : grouped.keySet()) {
            sb.append(sectionTitle.toUpperCase()).append("\n");
            sb.append("----------------------------------------------------\n");
            for (ResumeSection s : grouped.get(sectionTitle)) {
                sb.append(s.toString()).append("\n");
            }
            sb.append("\n");
        }

        sb.append("+--------------------------------------------------+\n");
        return sb.toString();
    }

    // FINALS MODIFICATION - Added JSON save functionality
    public void saveToJSON(String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("{");
            
            if (personalInfo != null) {
                writer.println("  \"personalInfo\": {");
                writer.println("    \"name\": \"" + escapeJSON(personalInfo.getName()) + "\",");
                writer.println("    \"contact\": \"" + escapeJSON(personalInfo.getContact()) + "\",");
                writer.println("    \"email\": \"" + escapeJSON(personalInfo.getEmail()) + "\"");
                writer.println("  },");
            }
            
            writer.println("  \"sections\": [");
            for (int i = 0; i < sections.size(); i++) {
                ResumeSection section = sections.get(i);
                writer.println("    {");
                writer.println("      \"type\": \"" + section.getClass().getSimpleName() + "\",");
                
                if (section instanceof Education) {
                    Education edu = (Education) section;
                    writer.println("      \"degree\": \"" + escapeJSON(edu.toString().split(" \\| ")[0]) + "\",");
                    writer.println("      \"institution\": \"" + escapeJSON(edu.toString().split(" \\| ")[1]) + "\",");
                    writer.println("      \"year\": \"" + edu.toString().split(" \\| ")[2] + "\"");
                } else if (section instanceof Experience) {
                    Experience exp = (Experience) section;
                    String[] parts = exp.toString().split(" \\| ");
                    writer.println("      \"role\": \"" + escapeJSON(parts[0]) + "\",");
                    writer.println("      \"company\": \"" + escapeJSON(parts[1]) + "\",");
                    writer.println("      \"duration\": \"" + escapeJSON(parts[2].split("\\n")[0]) + "\",");
                    writer.println("      \"description\": \"" + escapeJSON(exp.getDescription()) + "\"");
                } else if (section instanceof Skill) {
                    writer.println("      \"name\": \"" + escapeJSON(section.toString().substring(2)) + "\"");
                }
                
                writer.print("    }");
                if (i < sections.size() - 1) writer.println(",");
                else writer.println();
            }
            writer.println("  ]");
            writer.println("}");
        }
    }

    // FINALS MODIFICATION - Helper method to escape JSON strings
    private String escapeJSON(String str) {
        return str.replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }

    // method to print resume in a box
    public void printResume() {
        StringBuilder sb = new StringBuilder();
        sb.append("+--------------------------------------------------+\n");
        sb.append("|                       RESUME                     |\n");
        sb.append("+--------------------------------------------------+\n\n");

        if (personalInfo != null) {
            sb.append("Name: ").append(personalInfo.getName()).append("\n");
            sb.append("Contact: ").append(personalInfo.getContact()).append("\n");
            sb.append("Email: ").append(personalInfo.getEmail()).append("\n\n");
        }

        // group sections by type
        Map<String, List<ResumeSection>> grouped = new LinkedHashMap<>();
        for (ResumeSection s : sections) {
            grouped.computeIfAbsent(s.getTitle(), k -> new ArrayList<>()).add(s);
        }

        for (String sectionTitle : grouped.keySet()) {
            sb.append(sectionTitle.toUpperCase()).append("\n");
            sb.append("----------------------------------------------------\n");
            for (ResumeSection s : grouped.get(sectionTitle)) {
                sb.append(s.toString()).append("\n");
            }
            sb.append("\n");
        }

        sb.append("+--------------------------------------------------+\n");
        System.out.println(sb.toString());
    }
}

// analyzer class
class ResumeAnalyzer {
    private static final String[] ACTION_VERBS = {
        "developed", "led", "implemented", "created", "designed",
        "managed", "improved", "analyzed", "built"
    };

    public void analyze(Resume resume) {
        int score = 0;
        int totalChecks = 5;

        System.out.println("\n============================= RESUME ANALYSIS =============================\n");

        List<ResumeSection> sections = resume.getSections();

        // --- Section presence (now up to 3 points) ---
        boolean hasEducation = sections.stream().anyMatch(s -> s instanceof Education);
        boolean hasExperience = sections.stream().anyMatch(s -> s instanceof Experience);
        long skillCount = sections.stream().filter(s -> s instanceof Skill).count();

        if (hasEducation) {
            System.out.println("- Education section present.");
            score++;
        } else {
            System.out.println("- Missing Education section.");
        }

        if (hasExperience) {
            System.out.println("- Experience section present.");
            score++;
        } else {
            System.out.println("- Missing Experience section.");
        }

        if (skillCount >= 5) {
            System.out.println("- Skills section has 5 or more skills (" + skillCount + " skills).");
            score++;
        } else if (skillCount > 0) {
            System.out.println("- Skills section present but needs at least 5 skills (currently " + skillCount + " skills).");
        } else {
            System.out.println("- Missing Skills section.");
        }

        // --- Action verbs in Experience (1 point) ---
        boolean hasActionVerb = sections.stream()
            .filter(s -> s instanceof Experience)
            .map(s -> (Experience) s)
            .anyMatch(ex -> {
                for (String verb : ACTION_VERBS) {
                    if (ex.getDescription().toLowerCase().contains(verb)) {
                        return true;
                    }
                }
                return false;
            });
        if (hasActionVerb) {
            System.out.println("- Experience descriptions include action verbs.");
            score++;
        } else {
            System.out.println("- Consider adding action verbs to experience descriptions.");
            System.out.println("  Examples: developed, led, implemented, created, designed, managed, improved, analyzed, built");
        }

        // --- Achievements (numbers in Experience) (1 point) ---
        boolean hasAchievement = sections.stream()
            .filter(s -> s instanceof Experience)
            .map(s -> (Experience) s)
            .anyMatch(ex -> ex.getDescription().matches(".*\\d+.*"));
        if (hasAchievement) {
            System.out.println("- Resume includes measurable achievements.");
            score++;
        } else {
            System.out.println("- Consider adding measurable achievements (numbers, percentages).");
            System.out.println("  Examples: 'improved performance by 30%', 'managed team of 5', 'processed 100+ orders daily'");
        }

        // Final Score
        System.out.println("\nResume Score: " + score + "/" + totalChecks);
    }
}

// FINALS MODIFICATION - Added Swing GUI main class
public class ResumeBuilderApp extends JFrame {
    private Resume resume;
    private JTextField nameField, contactField, emailField;
    private JTextField degreeField, institutionField, yearField, honorsField;
    private JTextField roleField, companyField, durationField, descriptionField;
    private JTextField skillField;
    private JTextArea resumePreview;
    private DefaultListModel<String> educationListModel, experienceListModel, skillListModel;
    private JList<String> educationList, experienceList, skillList;

    public ResumeBuilderApp() {
        resume = new Resume();
        initializeGUI();
    }

    // FINALS MODIFICATION - GUI initialization method
    private void initializeGUI() {
        setTitle("Resume Builder Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();

        // Personal Info Tab
        JPanel personalInfoPanel = createPersonalInfoPanel();
        tabbedPane.addTab("Personal Info", personalInfoPanel);

        // Education Tab
        JPanel educationPanel = createEducationPanel();
        tabbedPane.addTab("Education", educationPanel);

        // Experience Tab
        JPanel experiencePanel = createExperiencePanel();
        tabbedPane.addTab("Experience", experiencePanel);

        // Skills Tab
        JPanel skillsPanel = createSkillsPanel();
        tabbedPane.addTab("Skills", skillsPanel);

        // Preview Tab
        JPanel previewPanel = createPreviewPanel();
        tabbedPane.addTab("Preview", previewPanel);

        add(tabbedPane, BorderLayout.CENTER);

        // Bottom panel with action buttons
        JPanel bottomPanel = new JPanel(new BorderLayout());
        
        // Left side buttons
        JPanel leftButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton generateButton = new JButton("Generate Resume");
        JButton saveButton = new JButton("Save to TXT");
        JButton analyzeButton = new JButton("Analyze Resume");
        JButton clearButton = new JButton("Clear All");
        
        // Right side exit button
        JPanel rightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton exitButton = new JButton("Exit");

        generateButton.addActionListener(e -> generateResume());
        saveButton.addActionListener(e -> saveToTXT());
        analyzeButton.addActionListener(e -> analyzeResume());
        clearButton.addActionListener(e -> clearAllFields());
        exitButton.addActionListener(e -> exitApplication());

        leftButtons.add(generateButton);
        leftButtons.add(saveButton);
        leftButtons.add(analyzeButton);
        leftButtons.add(clearButton);
        rightButtons.add(exitButton);
        
        bottomPanel.add(leftButtons, BorderLayout.WEST);
        bottomPanel.add(rightButtons, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    // FINALS MODIFICATION - Create personal info panel
    private JPanel createPersonalInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField(20);
        panel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Contact:"), gbc);
        gbc.gridx = 1;
        contactField = new JTextField(20);
        panel.add(contactField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField(20);
        panel.add(emailField, gbc);

        return panel;
    }

    // FINALS MODIFICATION - Create education panel
    private JPanel createEducationPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("Degree:"), gbc);
        gbc.gridx = 1;
        degreeField = new JTextField(15);
        inputPanel.add(degreeField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(new JLabel("Institution:"), gbc);
        gbc.gridx = 1;
        institutionField = new JTextField(15);
        inputPanel.add(institutionField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        inputPanel.add(new JLabel("Year:"), gbc);
        gbc.gridx = 1;
        yearField = new JTextField(15);
        inputPanel.add(yearField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        inputPanel.add(new JLabel("Honors (Optional):"), gbc);
        gbc.gridx = 1;
        honorsField = new JTextField(15);
        inputPanel.add(honorsField, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        JButton addEducationButton = new JButton("Add Education");
        addEducationButton.addActionListener(e -> addEducation());
        inputPanel.add(addEducationButton, gbc);

        panel.add(inputPanel, BorderLayout.NORTH);

        educationListModel = new DefaultListModel<>();
        educationList = new JList<>(educationListModel);
        JScrollPane scrollPane = new JScrollPane(educationList);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // FINALS MODIFICATION - Create experience panel
    private JPanel createExperiencePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        roleField = new JTextField(15);
        inputPanel.add(roleField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(new JLabel("Company:"), gbc);
        gbc.gridx = 1;
        companyField = new JTextField(15);
        inputPanel.add(companyField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        inputPanel.add(new JLabel("Duration:"), gbc);
        gbc.gridx = 1;
        durationField = new JTextField(15);
        inputPanel.add(durationField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        inputPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        descriptionField = new JTextField(15);
        inputPanel.add(descriptionField, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        JButton addExperienceButton = new JButton("Add Experience");
        addExperienceButton.addActionListener(e -> addExperience());
        inputPanel.add(addExperienceButton, gbc);

        panel.add(inputPanel, BorderLayout.NORTH);

        experienceListModel = new DefaultListModel<>();
        experienceList = new JList<>(experienceListModel);
        JScrollPane scrollPane = new JScrollPane(experienceList);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // FINALS MODIFICATION - Create skills panel
    private JPanel createSkillsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.add(new JLabel("Skill:"));
        skillField = new JTextField(20);
        inputPanel.add(skillField);
        JButton addSkillButton = new JButton("Add Skill");
        addSkillButton.addActionListener(e -> addSkill());
        inputPanel.add(addSkillButton);

        panel.add(inputPanel, BorderLayout.NORTH);

        skillListModel = new DefaultListModel<>();
        skillList = new JList<>(skillListModel);
        JScrollPane scrollPane = new JScrollPane(skillList);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // FINALS MODIFICATION - Create preview panel
    private JPanel createPreviewPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        resumePreview = new JTextArea();
        resumePreview.setEditable(false);
        resumePreview.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(resumePreview);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    // FINALS MODIFICATION - Add education method
    private void addEducation() {
        try {
            String degree = degreeField.getText().trim();
            String institution = institutionField.getText().trim();
            String yearText = yearField.getText().trim();
            String honors = honorsField.getText().trim();

            if (degree.isEmpty() || institution.isEmpty() || yearText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all required education fields (Degree, Institution, Year).");
                return;
            }

            int year = Integer.parseInt(yearText);
            
            // Use AcademicEducation if honors is provided, otherwise use regular AcademicEducation without honors
            Education education;
            if (!honors.isEmpty()) {
                education = new AcademicEducation(degree, institution, year, honors);
            } else {
                education = new AcademicEducation(degree, institution, year);
            }
            
            resume.addSection(education);
            educationListModel.addElement("- " + education.toString());

            // Clear fields
            degreeField.setText("");
            institutionField.setText("");
            yearField.setText("");
            honorsField.setText("");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid year.");
        }
    }

    // FINALS MODIFICATION - Add experience method
    private void addExperience() {
        String role = roleField.getText().trim();
        String company = companyField.getText().trim();
        String duration = durationField.getText().trim();
        String description = descriptionField.getText().trim();

        if (role.isEmpty() || company.isEmpty() || duration.isEmpty() || description.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all experience fields.");
            return;
        }

        Experience experience = new Experience(role, company, duration, description);
        resume.addSection(experience);
        experienceListModel.addElement("- " + role + " at " + company + " | " + duration + " | " + description);

        // Clear fields
        roleField.setText("");
        companyField.setText("");
        durationField.setText("");
        descriptionField.setText("");
    }

    // FINALS MODIFICATION - Add skill method
    private void addSkill() {
        String skillName = skillField.getText().trim();
        
        if (skillName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a skill.");
            return;
        }

        Skill skill = new Skill(skillName);
        resume.addSection(skill);
        skillListModel.addElement("- " + skillName);

        skillField.setText("");
    }

    // FINALS MODIFICATION - Generate resume method
    private void generateResume() {
        // Update personal info
        String name = nameField.getText().trim();
        String contact = contactField.getText().trim();
        String email = emailField.getText().trim();

        if (name.isEmpty() || contact.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all personal information fields.");
            return;
        }

        if (!isValidContactNumber(contact)) {
            JOptionPane.showMessageDialog(this, "Please enter a valid contact number (digits, spaces, hyphens, parentheses, and + allowed).");
            return;
        }

        if (!email.contains("@")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address.");
            return;
        }

        resume.setPersonalInfo(new PersonalInfo(name, contact, email));
        resumePreview.setText(resume.getFormattedResume());
    }

    // FINALS MODIFICATION - Save to TXT method
    private void saveToTXT() {
        try {
            generateResume(); // Ensure resume is updated
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new File("resume.txt"));
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                String resumeText = resume.getFormattedResume();
                try (PrintWriter writer = new PrintWriter(new FileWriter(fileChooser.getSelectedFile()))) {
                    writer.print(resumeText);
                }
                JOptionPane.showMessageDialog(this, "Resume saved successfully as TXT!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving file: " + e.getMessage());
        }
    }

    // FINALS MODIFICATION - Analyze resume method
    private void analyzeResume() {
        // Check if personal info fields are filled
        String name = nameField.getText().trim();
        String contact = contactField.getText().trim();
        String email = emailField.getText().trim();

        if (name.isEmpty() || contact.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please fill all personal information fields before analyzing the resume.",
                "Incomplete Personal Information",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!email.contains("@")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address before analyzing the resume.");
            return;
        }

        if (!isValidContactNumber(contact)) {
            JOptionPane.showMessageDialog(this, "Please enter a valid contact number before analyzing the resume.");
            return;
        }

        generateResume(); // Ensure resume is updated
        ResumeAnalyzer analyzer = new ResumeAnalyzer();
        
        // Capture analyzer output and show in dialog
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream old = System.out;
        System.setOut(ps);
        
        analyzer.analyze(resume);
        
        System.out.flush();
        System.setOut(old);
        
        String analysis = baos.toString();
        JOptionPane.showMessageDialog(this, analysis, "Resume Analysis", JOptionPane.INFORMATION_MESSAGE);
    }

    // FINALS MODIFICATION - Clear all fields method
    private void clearAllFields() {
        int result = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to clear all data? This action cannot be undone.",
            "Confirm Clear",
            JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            // Clear personal info fields
            nameField.setText("");
            contactField.setText("");
            emailField.setText("");
            
            // Clear education fields
            degreeField.setText("");
            institutionField.setText("");
            yearField.setText("");
            honorsField.setText("");
            
            // Clear experience fields
            roleField.setText("");
            companyField.setText("");
            durationField.setText("");
            descriptionField.setText("");
            
            // Clear skill field
            skillField.setText("");
            
            // Clear all list models
            educationListModel.clear();
            experienceListModel.clear();
            skillListModel.clear();
            
            // Clear preview
            resumePreview.setText("");
            
            // Create new resume object
            resume = new Resume();
            
            JOptionPane.showMessageDialog(this, "All fields cleared successfully!");
        }
    }

    // FINALS MODIFICATION - Contact number validation method
    private boolean isValidContactNumber(String contact) {
        // Allow digits, spaces, hyphens, parentheses, and plus sign
        // Minimum 7 digits (for shortest valid phone numbers)
        String cleanContact = contact.replaceAll("[\\s\\-\\(\\)\\+]", "");
        
        // Check if it contains only digits after cleaning
        if (!cleanContact.matches("\\d+")) {
            return false;
        }
        
        // Check minimum length (at least 7 digits)
        if (cleanContact.length() < 7) {
            return false;
        }
        
        // Check maximum length (international numbers can be up to 15 digits)
        if (cleanContact.length() > 15) {
            return false;
        }
        
        return true;
    }

    // FINALS MODIFICATION - Exit application method
    private void exitApplication() {
        int result = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to exit the application?",
            "Confirm Exit",
            JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ResumeBuilderApp().setVisible(true);
        });
    }
}
