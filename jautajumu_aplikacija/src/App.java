import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

class Question {
    public String questionText;
    public String[] answers;
    public boolean[] correctAnswers;

    public Question(String questionText, String[] answers, boolean[] correctAnswers) {
        this.questionText = questionText;
        this.answers = answers;
        this.correctAnswers = correctAnswers;
    }
}

public class App {

    // Saglabā katru jautājuma rezultātu - jautājums un lietotāja atbilde
    // Vajadzīgs, lai vēlāk varētu parādīt nepareizās atbildes un to labojumus
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
        ImageIcon icon = new ImageIcon(App.class.getResource("/img/logo.png"));

        while (true) {
            int choice = JOptionPane.showOptionDialog(
                null,
                "Sveicināts jautājumu spēlē!\nIzvēlies vienu no opcijām:",
                "Jautājumu Spēle",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                icon,
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
        ImageIcon icon = new ImageIcon(App.class.getResource("/img/info.png"));

        String info = "📜 INFO:\n"
                     + "1. Tests sastāv no 10 jautājumiem.\n"
                     + "2. Katram jautājumam ir 4 atbilžu varianti, no kuriem pareizi ir 2-3.\n"
                     + "3. Uz jautājumiem jāatbild pēc kārtas, tos nedrīkst izlaist.\n"
                     + "4. Pēc jautājuma atbildēšanas nav zināms, vai atbildēts pareizi vai nepareizi.\n"
                     + "5. Spēles beigās tiks parādīts rezultāts un atbildes uz nepareizi atbildētajiem jautājumiem.\n"
                     + "6. Jautājumi, pildot testu, katru reizi tiek parādīti citā secībā.\n"
                     + "7. Spiežot uz pogas <Nav ne jausmas>, programma automātiski izvēlēsies 2-3 atbildes, randomā.\n";
        JOptionPane.showMessageDialog(null, info, "Par spēli", JOptionPane.INFORMATION_MESSAGE, icon);
    }

    public static void startQuiz() {
        // Sagatavo rezultātu glabāšanu
        List<QuizResult> results = new ArrayList<>();
        List<Question> questions = new ArrayList<>();
        ImageIcon icon = new ImageIcon(App.class.getResource("/img/questionMark.png"));

        String[] options = {"Ielādēt jautājumus", "Turpināt ar noklusējuma jautājumiem"};
        int readFromText = JOptionPane.showOptionDialog(
                null,
                "Vai vēlies ielādēt jautājumus no faila?\nCitādi tiks izmantoti noklusējuma jautājumi.",
                "Jautājumu ielāde",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                icon,
                options,
                options[0]
        );

        if (readFromText == JOptionPane.YES_OPTION) {
            questions = loadQuestionsFromFile(); // Ielādē jautājumus no faila, ja lietotājs izvēlas to darīt
        }
        
        if (questions == null || questions.isEmpty()) {
            // Ja lietotājs atcēla vai notika kļūda, izmantot hardcoded jautājumus
            questions = getDefaultQuestions();
        }

        Collections.shuffle(questions); // Lai katru reizi jautājumi būtu citā secībā
        int score = 0;

        long startTime = System.currentTimeMillis(); // Saglabā sākuma laiku, lai vēlāk varētu aprēķināt, cik ilgi tika spēlēts

        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            boolean[] userAnswers = askQuestion(q, i + 1, questions.size());
            
            if (userAnswers == null) {
                // Ja lietotājs atceļ jautājumu – atgriežas uz galveno izvēlni
                return;
            }

            results.add(new QuizResult(q, userAnswers));

            if (Arrays.equals(userAnswers, q.correctAnswers)) {
                score++;
            }
        }
        
        long endTime = System.currentTimeMillis(); // Laiks, kad beidz atbildēt uz jautājumiem
        double totalTimeSeconds = (endTime - startTime) / 1000.0;
        int roundedSeconds = (int) Math.round(totalTimeSeconds);
        String formattedTime = formatTime(roundedSeconds);

        showMistakes(results, score, questions.size(), formattedTime);

        // Jautā, vai atkārtot spēli
        int restart = JOptionPane.showConfirmDialog(null, "Vai vēlies sākt spēli no jauna?", "Restartēt?", JOptionPane.YES_NO_OPTION);

        if (restart == JOptionPane.YES_OPTION) {
            startQuiz();  // Restartē spēli
        } else {
            showMainMenu();  // Atgriežas uz main menu
        }
    }

    public static boolean[] askQuestion(Question q, int questionNumber, int totalQuestions){
        Random random = new Random();
        JCheckBox[] checkboxes = new JCheckBox[q.answers.length];
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel("❓ " + q.questionText));

        for (int i = 0; i < q.answers.length; i++) {
            checkboxes[i] = new JCheckBox(q.answers[i]);
            panel.add(checkboxes[i]);
        }
        panel.add(new JLabel("📊 Jautājums " + questionNumber + " no " + totalQuestions));

        String[] options = {"Iesniegt", "Nav ne jausmas", "Atcelt"};
        ImageIcon warning = new ImageIcon(App.class.getResource("/img/warning1.png"));
        int warningCount = 0; // Skaits, cik reizes lietotājs ir spiedis "Iesniegt" bez atzīmētām atbildēm

        String[] iconPaths = {
            "/img/question1.png",
            "/img/question2.png",
            "/img/question3.png",
            "/img/question4.png",
            "/img/question5.png"
        };

        int randomIndex = random.nextInt(iconPaths.length); // 0–4
        ImageIcon randomIcon = new ImageIcon(App.class.getResource(iconPaths[randomIndex]));

        while (true) {
            int result = JOptionPane.showOptionDialog(
                null,
                panel,
                "Jautājums",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                randomIcon,
                options,
                options[0]
            );

            if (result == JOptionPane.CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION) {
                return null;  // Cancel - atgriežas uz main menu
            }

            boolean[] userSelections = new boolean[q.answers.length];
            if (result == 1) {
                // "Nav ne jausmas" poga – izvēlas 2–3 random atbildes
                List<Integer> indices = new ArrayList<>();
                for (int i = 0; i < q.answers.length; i++) indices.add(i);
                Collections.shuffle(indices);
                int numToSelect = 2 + new java.util.Random().nextInt(2); // 2 vai 3
                
                // Notīra iepriekš atzīmētos checkboxus
                for (JCheckBox cb : checkboxes) {
                    cb.setSelected(false);
                }

                // Atzīmē 2–3 random checkboxus
                for (int i = 0; i < numToSelect; i++) {
                    checkboxes[indices.get(i)].setSelected(true);
                }

                // Paliek šajā pašā logā – ļauj lietotājam pārbaudīt un uzspiest "OK", nevis submittot uzreiz
                continue;
            }

            // Saskaita, cik atbildes ir atzīmētas
            int selectedCount = 0;
            for (JCheckBox cb : checkboxes) {
                if (cb.isSelected()) selectedCount++;
            }

            // Prikolam, ikoniņas mainās, atkarībā no tā, cik reizes brīdinājums ir parādījies
            if (warningCount == 1){
                warning = new ImageIcon(App.class.getResource("/img/warning2.png"));
            }else if (warningCount >= 2) {
                warning = new ImageIcon(App.class.getResource("/img/warning3.png"));
            }
            
            // Atļauj turpināt tikai tad, ja izvēlētas vismaz 2 atbildes
            if (selectedCount >= 2 && selectedCount <= 3) {
                for (int i = 0; i < checkboxes.length; i++) {
                    userSelections[i] = checkboxes[i].isSelected();
                }
                warningCount = 0;
                return userSelections;
            } else {
                JOptionPane.showMessageDialog(null, "Lūdzu, atzīmē tikai 2-3 atbildes!", "VISMAZ 2 VAI 3, cmon", JOptionPane.WARNING_MESSAGE, warning);
                warningCount++;
            }
        }
    }

    public static void showMistakes(List<QuizResult> results, int score, int totalQuestions, String formattedTime) {
        // Aprēķina rezultātu procentos un formatē rezultātu izvadi
        double percentageScore = ((double) score * 100) / totalQuestions;
        String formattedScore = String.format("%.2f", percentageScore);

        StringBuilder sb = new StringBuilder("Spēle pabeigta!\n");
        sb.append("Tavs rezultāts: ").append(score).append("/").append(totalQuestions)
          .append(" (").append(formattedScore).append("%)\n")
          .append("Kopējais laiks: ").append(formattedTime).append("\n\n")
          .append("Tavas kļūdas:\n\n");

        for (QuizResult result : results) {
            // Pievieno tikai tos jautājumus, kuros atbilde bija nepareiza
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

        if (sb.toString().equals("Spēle pabeigta!\n"
                + "Tavs rezultāts: " + score + "/" + totalQuestions
                + " (" + formattedScore + "%)\n"
                + "Kopējais laiks: " + formattedTime + "\n\n"
                + "Tavas kļūdas:\n\n")) {
            sb.append("Apsveicu! Viss atbildēts pareizi!");
        }

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new java.awt.Dimension(500, 300));

        JOptionPane.showMessageDialog(null, scrollPane, "Pārbaudi savas atbildes", JOptionPane.INFORMATION_MESSAGE);
    }

    private static String formatTime(int totalSeconds) {
        // No int sekundēs pārvērš par String HH:MM:SS
        if (totalSeconds < 0) {
            return "00:00:00"; // Ja laiks ir negatīvs, atgriežam 0
        }
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static List<Question> loadQuestionsFromFile() {
        // Atver failu izvēlni, lai ielādētu jautājumus no teksta faila - to dara attiecīgi vietā, no kuras ir atvērta aplikācija
        JFileChooser fileChooser = new JFileChooser(new File(System.getProperty("user.dir")));
        fileChooser.setDialogTitle("Izvēlies jautājumu failu");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Teksta faili (*.txt)", "txt"));

        int result = fileChooser.showOpenDialog(null);
        if (result != JFileChooser.APPROVE_OPTION) {
            JOptionPane.showMessageDialog(null, "Faila ielāde atcelta. Tiek izmantoti noklusējuma jautājumi.");
            return null;
        }

        File file = fileChooser.getSelectedFile();
        List<Question> questions = new ArrayList<>();

        // UTF-8, priekš latviešu valodas garumzīmēm
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            String questionText = null;
            List<String> answers = new ArrayList<>();
            List<Boolean> correct = new ArrayList<>();

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    if (questionText != null && !answers.isEmpty()) {
                        questions.add(new Question(
                            questionText,
                            answers.toArray(new String[0]),
                            toBooleanArray(correct)
                        ));
                        questionText = null;
                        answers.clear();
                        correct.clear();
                    }
                    continue;
                }

                if (line.startsWith("Q:")) {
                    questionText = line.substring(2).trim();
                } else if (line.startsWith("A:")) {
                    String[] parts = line.substring(2).split("\\|");
                    if (parts.length != 2) continue;
                    answers.add(parts[0].trim());
                    correct.add(Boolean.parseBoolean(parts[1].trim()));
                }
            }

            // Pēdējais jautājums, ja faila beigās nav tukšas rindas
            if (questionText != null && !answers.isEmpty()) {
                questions.add(new Question(
                    questionText,
                    answers.toArray(new String[0]),
                    toBooleanArray(correct)
                ));
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Kļūda lasot failu: " + e.getMessage());
            return null;
        }

        return questions;
    }

    private static boolean[] toBooleanArray(List<Boolean> list) {
        boolean[] arr = new boolean[list.size()];
        for (int i = 0; i < list.size(); i++) arr[i] = list.get(i);
        return arr;
    }

    public static List<Question> getDefaultQuestions() {
    List<Question> questions = new ArrayList<>();
    questions.add(new Question(
            "Kuri no šiem ir derīgi Java datu tipi?",
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
            new String[]{"==", "&&", ">>", "!="},
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

    return questions;
}

}
