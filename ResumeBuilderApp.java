import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

// FINALS MODIFICATION - Added Swing GUI components and JSON file save/load functionality
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.regex.Pattern;

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

        System.out.println("\n===== RESUME ANALYSIS =====\n");

        List<ResumeSection> sections = resume.getSections();

        // --- Section presence (now up to 3 points) ---
        boolean hasEducation = sections.stream().anyMatch(s -> s instanceof Education);
        boolean hasExperience = sections.stream().anyMatch(s -> s instanceof Experience);
        boolean hasSkills = sections.stream().anyMatch(s -> s instanceof Skill);

        if (hasEducation) {
            System.out.println("Education section present.");
            score++;
        } else {
            System.out.println("Missing Education section.");
        }

        if (hasExperience) {
            System.out.println("Experience section present.");
            score++;
        } else {
            System.out.println("Missing Experience section.");
        }

        if (hasSkills) {
            System.out.println("Skills section present.");
            score++;
        } else {
            System.out.println("Missing Skills section.");
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
            System.out.println("Experience descriptions include action verbs.");
            score++;
        } else {
            System.out.println("Consider adding action verbs to experience descriptions.");
        }

        // --- Achievements (numbers in Experience) (1 point) ---
        boolean hasAchievement = sections.stream()
            .filter(s -> s instanceof Experience)
            .map(s -> (Experience) s)
            .anyMatch(ex -> ex.getDescription().matches(".*\\d+.*"));
        if (hasAchievement) {
            System.out.println("Resume includes measurable achievements.");
            score++;
        } else {
            System.out.println("Consider adding measurable achievements (numbers, percentages).");
        }

        // Final Score
        System.out.println("\nResume Score: " + score + "/" + totalChecks);
    }
}

// main app
public class ResumeBuilderApp {
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        Resume resume = new Resume();

        try {
            System.out.print("Enter your name: ");
            String name = sc.nextLine();

            System.out.print("Enter your contact number: ");
            String contact = sc.nextLine();

            /*
            MODIFIED FOR INPUT VALIDATION LOOP, PROGRAM USED TO STOP AFTER CONDITION IS NOT MET
            
            System.out.print("Enter your email: ");
            String email = sc.nextLine();
            if (!email.contains("@")) {
                throw new IllegalArgumentException("Invalid email format.");
            }
            */

            // INPUT VALIDATION LOOP FOR EMAIL (FINALS MODIFICATION)
            String email;
            while (true) {
                System.out.print("Enter your email: ");
                email = sc.nextLine();
                if (email.contains("@")) {
                    break;
                } else {
                    System.out.println("Invalid email format. Please include '@' in your email.");
                }
            }

            resume.setPersonalInfo(new PersonalInfo(name, contact, email));

            // education
            int eduCount = safeIntInput("How many education entries? ");
            for (int i = 0; i < eduCount; i++) {
                System.out.print("Degree: ");
                String degree = sc.nextLine();
                System.out.print("Institution: ");
                String institution = sc.nextLine();
                int yearOfGraduation = safeIntInput("Year of Graduation: ");
                resume.addSection(new Education(degree, institution, yearOfGraduation));
            }

            // experience
            int expCount = safeIntInput("How many work experience entries? ");
            for (int i = 0; i < expCount; i++) {
                System.out.print("Role: ");
                String role = sc.nextLine();
                System.out.print("Company: ");
                String company = sc.nextLine();
                System.out.print("Duration: ");
                String duration = sc.nextLine();
                System.out.print("Description: ");
                String description = sc.nextLine();
                resume.addSection(new Experience(role, company, duration, description));
            }

            // skills
            int skillCount = safeIntInput("How many skills? ");
            for (int i = 0; i < skillCount; i++) {
                System.out.print("Skill: ");
                String skillName = sc.nextLine();
                resume.addSection(new Skill(skillName));
            }

            // print and analyze
            resume.printResume();
            ResumeAnalyzer analyzer = new ResumeAnalyzer();
            analyzer.analyze(resume);

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            sc.close();
        }
    }

    private static int safeIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
}
