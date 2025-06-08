import javax.swing.*;

public class App {
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
        JOptionPane.showMessageDialog(null, "Te būs tie jautājum");
    }
}
