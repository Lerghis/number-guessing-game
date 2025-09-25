import java.util.Random;
import java.util.Scanner;

public class Main
{
    Scanner keyboard;
    Random random;
    int generatedNumber;

    public Main()
    {
        keyboard = new Scanner(System.in);
        random = new Random();
        generatedNumber = random.nextInt(100);
    }

    public void Menu()
    {
        System.out.println("====================================");
        System.out.println("Welcome to the Number Guessing Game!");
        System.out.println("====================================");
        System.out.println("\nI'm thinking of a number between 1 and 100.");
        System.out.println("You have 5 chances to guess the correct number.");

        int choice;
        do
        {
            System.out.println("\nPlease select the difficulty level: ");
            System.out.println("1. Easy (10 chances)");
            System.out.println("2. Medium (5 chances)");
            System.out.println("3. Hard (3 chances)");
            System.out.println("4. Exit Game.");
            System.out.print("\nEnter your choice: ");
            choice = keyboard.nextInt();
            keyboard.nextLine(); // Clear Buffer

            switch (choice)
            {
                case 1 -> playGame(10, generatedNumber, "Easy");
                case 2 -> playGame(5, generatedNumber, "Medium");
                case 3 -> playGame(3, generatedNumber, "Hard");
                case 4 -> System.out.println("Exiting Game...");
                default -> System.out.println("Invalid choice!");
            }
        }
        while(choice != 4);
    }

    public void playGame(int maxAttempts, int generatedNumber, String difficultyLevel)
    {
        int counter = 0;
        boolean guessedCorrectly = false;

        System.out.println("\nGreat! You have selected " + difficultyLevel + " difficulty level.");
        System.out.println("Lets start the game!");

        for (int i = 0; i < maxAttempts; i++)
        {
            System.out.print("\nEnter your guess: ");
            int userGuess = keyboard.nextInt();
            counter++;

            if (userGuess > generatedNumber)
                System.out.println("Incorrect! The number is less than " + userGuess + ".");
            else if (userGuess < generatedNumber)
                System.out.println("Incorrect! The number is greater than " + userGuess + ".");
            else
            {
                System.out.println("Congratulations! You guessed the correct number in " + counter + " attempts!");
                System.out.println("Let's play again!");
                guessedCorrectly = true;
                break;
            }
        }

        if (!guessedCorrectly)
        {
            System.out.println("\nYou run out of chances...The Game is over!");
            System.out.println("The number was: " + generatedNumber);
            System.out.println("Let's start over!");
        }
    }
    public static void main(String[] args)
    {
        Main Game = new Main();
        Game.Menu();
        Game.keyboard.close();
    }
}