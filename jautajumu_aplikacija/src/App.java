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

        questions.add(new Question(
            "Kuri no šiem ir derīgi Java datu tipi? (Atzīmē visas pareizās)",
            new String[]{"boolean", "word", "int", "long"},
            new boolean[]{true, false, true, true}
        ));
        questions.add(new Question(
            "Kādas vērtības var atbilst boolean tipam?",
            new String[]{"true", "false", "null", "0"},
            new boolean[]{true, true, false, false}
        ));
        questions.add(new Question(
            "Kuri no šiem ir derīgi Java operatori?",
            new String[]{"==", "&&", ">|", "!="},
            new boolean[]{true, true, false, true}
        ));
        questions.add(new Question(
            "Kurus no šiem datu tipiem Java izmanto skaitļu glabāšanai?",
            new String[]{"String", "int", "short", "boolean"},
            new boolean[]{false, true, true, false}
        ));
        questions.add(new Question(
            "Kuri no šiem apgalvojumiem par String ir patiesi?",
            new String[]{"String nav primitīvais tips", "String ir nemainīgs (immutable)", "String vienmēr jābūt null",
            "String ir tas pats, kas char[]"},
            new boolean[]{true, true, false, false}
        ));
        questions.add(new Question(
            "Kas ir final mainīgais Java?",
            new String[]{"To nedrīkst pārrakstīt pēc inicializācijas", "To var mainīt, ja tas ir String",
            "Bieži tiek izmantots konstantēm", "Obligāti jāsauc ar lielajiem burtiem"},
            new boolean[]{true, false, true, false}
        ));
        questions.add(new Question(
            "Kurus no šiem var izmantot mainīgo nosaukumos Java?",
            new String[]{"Atstarpes", "Cipari (bet ne kā pirmais simbols)", "Apakšsvītra (_)", "Burti (a-z, A-Z)"},
            new boolean[]{false, true, true, true}
        ));
        questions.add(new Question(
            "Kurā vietā var deklarēt mainīgo Java klasē?",
            new String[]{"Klases līmenī (instance field)", "Metodes iekšienē", "Cikla iekšienē", "Tieši ārpus klases"},
            new boolean[]{true, true, true, false}
        ));
        questions.add(new Question(
            "Kas notiek, ja mainīgajam tiek piešķirts nepareizs tips?",
            new String[]{"Java automātiski pārveido tipu", "Kompilators izmet kļūdu", "Mainīgais kļūst par null",
            "Nepareizs tips izraisa kļūdu vēl pirms izpildes"},
            new boolean[]{false, true, false, true}
        ));
        questions.add(new Question(
            "Kuri no šiem ir Java primārie datu tipi?",
            new String[]{"int", "String", "char", "double"},
            new boolean[]{true, false, true, true}
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
        
        double percentageScore = (score * 100) / questions.size();
        JOptionPane.showMessageDialog(null, "Spēle pabeigta! Tavs rezultāts: " + score + "/" + questions.size() + " (" + percentageScore + "%)");
        showMistakes(results);
    }

    public static boolean[] askQuestion(Question q) {
        JCheckBox[] checkboxes = new JCheckBox[q.answers.length];

        while (true) {
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.add(new JLabel("❓ " + q.questionText));

            for (int i = 0; i < q.answers.length; i++) {
                checkboxes[i] = new JCheckBox(q.answers[i]);
                panel.add(checkboxes[i]);
            }

            int result = JOptionPane.showConfirmDialog(null, panel, "Jautājums", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (result == JOptionPane.CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION) {
                // Atgriezties uz galveno izvēlni
                showMainMenu(); // Restartē visu no sākuma
            }

            // Saskaitīt, cik atzīmētas izvēles
            int selectedCount = 0;
            for (JCheckBox cb : checkboxes) {
                if (cb.isSelected()) {
                    selectedCount++;
                }
            }

            if (selectedCount >= 2) {
                boolean[] userSelections = new boolean[q.answers.length];
                for (int i = 0; i < q.answers.length; i++) {
                    userSelections[i] = checkboxes[i].isSelected();
                }
                return userSelections;
            } else {
                JOptionPane.showMessageDialog(null, "Lūdzu, atzīmē vismaz DIVAS atbildes!", "Nepietiek izvēļu", JOptionPane.WARNING_MESSAGE);
            }
        }
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
