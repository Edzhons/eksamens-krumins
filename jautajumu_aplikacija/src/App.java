import javax.swing.*;

public class App {
    public static void main(String[] args) {
        showMainMenu();
    }

    public static void showMainMenu() {
        String[] options = {"Sākt jautājumu spēli", "Noteikumi", "Iziet"};

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
                // Sākt jautājumu spēli
                startQuiz();
            } else if (choice == 1) {
                // Apskatīt noteikumus
                showRules();
            } else {
                // Iziet no programmas
                JOptionPane.showMessageDialog(null, "Paldies, ka izmantoji manu aplikāciju! ;]");
                System.exit(0);
            }
        }
    }

    public static void showRules() {
        String rules = "📜 Noteikumi:\n"
                     + "1. Te būs visādi noteikumi.\n";
        JOptionPane.showMessageDialog(null, rules, "Par spēli", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void startQuiz() {
        JOptionPane.showMessageDialog(null, "Šeit būs jautājumu spēle!", "Jautājumu Spēle", JOptionPane.INFORMATION_MESSAGE);
    }
}
