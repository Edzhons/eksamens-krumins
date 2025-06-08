import java.util.ArrayList;
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

        questions.add(new Question(
            "Kuri no šiem ir derīgi Java datu tipi? (Atzīmē visas pareizās)",
            new String[]{"boolean", "String", "int", "float"},
            new boolean[]{true, false, true, true}
        ));

        questions.add(new Question(
            "Kas atbilst 'boolean' tipa vērtībai?",
            new String[]{"true", "false", "null", "0"},
            new boolean[]{true, true, false, false}
        ));

        // VĒL JAUTĀJUMI .......

        Collections.shuffle(questions); // sajauc jautājumus

        int score = 0;

        for (Question q : questions) {
            boolean correct = askQuestion(q);
            if (correct) score++;
        }

        JOptionPane.showMessageDialog(null, "Spēle pabeigta! Tavs rezultāts: " + score + "/" + questions.size());
    }

    public static boolean askQuestion(Question q) {
        JCheckBox[] checkboxes = new JCheckBox[q.answers.length];
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(new JLabel("❓ " + q.questionText));

        for (int i = 0; i < q.answers.length; i++) {
            checkboxes[i] = new JCheckBox(q.answers[i]);
            panel.add(checkboxes[i]);
        }

        int result = JOptionPane.showConfirmDialog(null, panel, "Jautājums", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            for (int i = 0; i < q.answers.length; i++) {
                if (checkboxes[i].isSelected() != q.correctAnswers[i]) {
                    return false;
                }
            }
            return true;
        }

        return false;
    }
}
