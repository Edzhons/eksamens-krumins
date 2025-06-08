class Question {
    String questionText;
    String[] answers;
    boolean[] correctAnswers;

    public Question(String questionText, String[] answers, boolean[] correctAnswers) {
        this.questionText = questionText;
        this.answers = answers;
        this.correctAnswers = correctAnswers;
    }
}