import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.swing.*;

public class App {

    // Saglabā jautājumu un atbilžu struktūru no lietotāja
    static class QuizResult {
        Question question;
        boolean[] userAnswers;

        QuizResult(Question question, boolean[] userAnswers) {
            this.question = question;
            this.userAnswers = userAnswers;
        }
    }

    public static void main(String[] args) {
        showMainMenu();
    }

    public static void showMainMenu() {
        String[] options = {"Sākt jautājumu spēli", "INFO", "Iziet"};

        while (true) {
            int choice = JOptionPane.showOptionDialog(
                null,
                "Sveicināts jautājumu spēlē!\nIzvēlies vienu no opcijām:",
                "Jautājumu Spēle",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]
            );

            if (choice == 0) {
                startQuiz();
            } else if (choice == 1) {
                showInfo();
            } else {
                JOptionPane.showMessageDialog(null, "Paldies, ka izmantoji manu aplikāciju! ;]");
                System.exit(0);
            }
        }
    }

    public static void showInfo() {
        String rules = "📜 INFO:\n"
                     + "1. Tests sastāv no 10 jautājumiem.\n"
                     + "2. Katram jautājumam ir 4 atbilžu varianti, no kuriem pareizi ir 2-3.\n"
                     + "3. Uz jautājumiem jāatbild pēc kārtas, tos nedrīkst izlaist.\n"
                     + "4. Pēc jautājuma atbildēšanas nav zināms, vai atbildēts pareizi vai nepareizi.\n"
                     + "5. Spēles beigās tiks parādīts rezultāts un atbildes uz nepareizi atbildētajiem jautājumiem.\n"
                     + "6. Jautājumi, pildot testu, katru reizi tiek parādīti citā secībā.\n";
        JOptionPane.showMessageDialog(null, rules, "Par spēli", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void startQuiz() {
        List<Question> questions = new ArrayList<>();
        List<QuizResult> results = new ArrayList<>();

        // --- Add questions here ---
        questions.add(new Question(
            "Kurie no šiem ir derīgi Java datu tipi? (Atzīmē visas pareizās)",
            new String[]{"boolean", "String", "int", "float"},
            new boolean[]{true, false, true, true}
        ));
        questions.add(new Question(
            "Kas atbilst 'boolean' tipa vērtībai?",
            new String[]{"true", "false", "null", "0"},
            new boolean[]{true, true, false, false}
        ));

        Collections.shuffle(questions);
        int score = 0;

        for (Question q : questions) {
            boolean[] userAnswers = askQuestion(q);
            results.add(new QuizResult(q, userAnswers));

            if (Arrays.equals(userAnswers, q.correctAnswers)) {
                score++;
            }
        }

        JOptionPane.showMessageDialog(null, "Spēle pabeigta! Tavs rezultāts: " + score + "/" + questions.size());
        showMistakes(results);
    }

    public static boolean[] askQuestion(Question q) {
        JCheckBox[] checkboxes = new JCheckBox[q.answers.length];
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel("❓ " + q.questionText));

        for (int i = 0; i < q.answers.length; i++) {
            checkboxes[i] = new JCheckBox(q.answers[i]);
            panel.add(checkboxes[i]);
        }

        int result = JOptionPane.showConfirmDialog(null, panel, "Jautājums", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        boolean[] userSelections = new boolean[q.answers.length];

        if (result == JOptionPane.OK_OPTION) {
            for (int i = 0; i < q.answers.length; i++) {
                userSelections[i] = checkboxes[i].isSelected();
            }
        }

        return userSelections;
    }

    public static void showMistakes(List<QuizResult> results) {
        StringBuilder sb = new StringBuilder("Tavas kļūdas:\n\n");

        for (QuizResult result : results) {
            if (!Arrays.equals(result.userAnswers, result.question.correctAnswers)) {
                sb.append("❌ ").append(result.question.questionText).append("\n");

                for (int i = 0; i < result.question.answers.length; i++) {
                    String answer = result.question.answers[i];
                    boolean correct = result.question.correctAnswers[i];
                    boolean userPicked = result.userAnswers[i];

                    if (correct && userPicked) {
                        sb.append("   ✅ Pareizi atzīmēts: ").append(answer).append("\n");
                    } else if (correct) {
                        sb.append("   ⚠️ Neatzīmēts, bet vajadzēja: ").append(answer).append("\n");
                    } else if (userPicked) {
                        sb.append("   ❌ Nepareizi atzīmēts: ").append(answer).append("\n");
                    }
                }

                sb.append("\n");
            }
        }

        if (sb.toString().equals("Tavas kļūdas:\n\n")) {
            sb.append("🎉 Apsveicu! Viss atbildēts pareizi!");
        }

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new java.awt.Dimension(500, 300));

        JOptionPane.showMessageDialog(null, scrollPane, "Pārbaudi savas atbildes", JOptionPane.INFORMATION_MESSAGE);
    }

}
