import java.util.*;

public class NumberGuessingGame
{
    int generatedNumber;
    Scanner keyboard;
    Random random;
    private static final int MIN_VALUE = 1; // MIN_VALUE, MAX_VALUE — bounds of the secret number range (useful for range hints).
    private static final int MAX_VALUE = 100; // you can change these to a HashMap if each difficulty level has different ranges
    Map<String, Integer> highScores = new HashMap<>();
    private static final Map<String, Integer> HINT_LIMITS = new HashMap<>(); // final means you can’t reassign the map
    static // block runs once when the class is loaded
    {
        HINT_LIMITS.put("Easy", 3);
        HINT_LIMITS.put("Medium", 2);
        HINT_LIMITS.put("Hard", 1);
    }

    /**
     * Constructs a new NumberGuessingGame instance.
     *
     * Initializes the Scanner for user input, the Random object for number generation,
     * and generates a random number between 1 and 100 as the target number.
     */
    public NumberGuessingGame()
    {
        keyboard = new Scanner(System.in);
        random = new Random();
        generatedNumber = random.nextInt(100);
    }

    /**
     * Displays the game menu, handles difficulty selection,
     * and starts the game rounds by calling playGame().
     */
    public void Menu()
    {
        System.out.println("====================================");
        System.out.println("Welcome to the Number Guessing Game!");
        System.out.println("====================================");
        System.out.println("\nI'm thinking of a number between 1 and 100.");

        int choice = 0;
        do
        {
            System.out.println("\nPlease select the difficulty level: ");
            System.out.println("1. Easy (10 chances)");
            System.out.println("2. Medium (5 chances)");
            System.out.println("3. Hard (3 chances)");
            System.out.println("4. Exit Game.");
            System.out.print("\nEnter your choice: ");
            String choiceInput = keyboard.nextLine().trim();

            try
            {
                choice = Integer.parseInt(choiceInput);
            }
            catch (NumberFormatException ex)
            {
                System.out.println("Invalid input! Enter 1, 2, 3 or 4.");
                continue;
            }

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

    /**
     * Runs a single round of the Number Guessing Game using the selected difficulty.
     *
     * @param maxAttempts total attempts allowed for the round
     * @param generatedNumber the target number the user must guess
     * @param difficultyLevel the chosen difficulty ("Easy", "Medium", or "Hard")
     */
    public void playGame(int maxAttempts, int generatedNumber, String difficultyLevel)
    {
        int attemptsLeft = maxAttempts;
        int hintsUsed = 0; // Increment each time the player asks for a hint.
        int hintLimit = HINT_LIMITS.getOrDefault(difficultyLevel.trim(),0); // if difficulty isn’t found, fall back to 0
        int hintsLeft = hintLimit - hintsUsed;
        List<String> hintHistory = new ArrayList<>(); // to avoid repeating the same type of hint. Every time a hint is given, add "parity" or "range" to this list.
        int counter = 0;
        boolean guessedCorrectly = false;

        long startTime = System.currentTimeMillis();

        System.out.println("\nGreat! You have selected " + difficultyLevel + " difficulty level. Your available hints are: " + (hintsLeft) + ".");
        System.out.println("Lets start the game!");

        /*
          This loop keeps running until:
          The user guesses correctly → guessedCorrectly = true, OR
          The user runs out of attempts → attemptsLeft == 0
         */
        while (attemptsLeft > 0 && !guessedCorrectly)
        {
            TurnResult result = processTurn(generatedNumber, attemptsLeft, hintLimit, hintsUsed, hintsLeft, hintHistory, difficultyLevel, counter);

            attemptsLeft = result.attemptsLeft;
            hintsUsed = result.hintsUsed;
            hintsLeft = result.hintsLeft;
            counter = result.counter;
            guessedCorrectly = result.guessedCorrectly;
        }

        if (guessedCorrectly)
        {
            long endTime = System.currentTimeMillis();
            long duration = (endTime - startTime) / 1000; // convert milliseconds to seconds

            System.out.println("Congratulations! You guessed the correct number: " + generatedNumber);
            System.out.println("Attempts: " + counter);
            System.out.println("Hints used: " + hintsUsed);
            System.out.println("Time taken: " + duration + " seconds.");

            // update high scores
            if (!highScores.containsKey(difficultyLevel) || counter < highScores.get(difficultyLevel))
            {
                highScores.put(difficultyLevel, counter);
                System.out.println("New high score for " + difficultyLevel + " difficulty: " + counter + " attempts " + "(hints used: " + hintsUsed + ")!");
            }
            else
                System.out.println("Current high score for " + difficultyLevel + ": " + highScores.get(difficultyLevel) + " attempts.");
        }
        else
        {
            System.out.println("\nYou run out of chances...The Game is over!");
            System.out.println("The number was: " + generatedNumber);
        }

        System.out.println("\nLet's play again!");
    }

    /**
     * Handles one user interaction (guess or hint), updates game state,
     * and returns the result of the turn.
     *
     * @param generatedNumber the target number the user must guess
     * @param attemptsLeft current remaining attempts
     * @param hintLimit hint limit for each difficulty (Easy -> 3, Medium -> 2, Hard -> 1)
     * @param hintsUsed count of used hints so far
     * @param hintsLeft the number of remaining hints
     * @param hintHistory list of used hint types
     * @param difficultyLevel the selected gameplay difficulty
     * @param counter number of guesses made
     * @return TurnResult object containing updated state after the turn
     */
    public TurnResult processTurn(int generatedNumber, int attemptsLeft, int hintLimit, int hintsUsed, int hintsLeft, List<String> hintHistory, String difficultyLevel, int counter)
    {
        TurnResult result = new TurnResult();
        String input;

        if (!difficultyLevel.equals("Hard"))
            System.out.println("\nEnter your guess (remaining chances: " + attemptsLeft +") or type 'hint' (hints left: " + hintsLeft + ", cost: 1 attempt): ");
        else
            System.out.println("\nEnter your guess (remaining chances: " + attemptsLeft +") or type 'hint' (hints left: " + hintsLeft + ", cost: 0 attempt): ");
        input = keyboard.nextLine().trim();

        if (input.equalsIgnoreCase("hint"))
        {
            if (hintsUsed < hintLimit)
            {
                System.out.println(generateHint(generatedNumber, hintHistory, difficultyLevel, hintsLeft, attemptsLeft));
                hintsUsed++;
                hintsLeft = hintLimit - hintsUsed;
                if (!difficultyLevel.equals("Hard"))
                    attemptsLeft--; // penalty for easy and medium modes
            }
            else
                System.out.println("No hints left!");
        }
        else
        {
            try
            {
                int userGuess = Integer.parseInt(input);
                counter++;
                attemptsLeft--;

                if (userGuess > generatedNumber)
                    System.out.println("Incorrect! The number is less than " + userGuess + ".");
                else if (userGuess < generatedNumber)
                    System.out.println("Incorrect! The number is greater than " + userGuess + ".");
                else
                    result.guessedCorrectly = true;
            }
            catch (NumberFormatException ex)
            {
                System.out.println("Invalid input! Enter a number or type 'hint'");
            }
        }

        result.attemptsLeft = attemptsLeft;
        result.hintsUsed = hintsUsed;
        result.hintsLeft = hintsLeft;
        result.counter = counter;
        return result;
    }

    /**
     * Represents the result of a single turn in the Number Guessing Game.
     * Encapsulates the current state after a turn, including whether the
     * user guessed correctly, remaining attempts, hints used, hints left,
     * and total guesses made so far.
     */
    static class TurnResult
    {
        boolean guessedCorrectly;
        int attemptsLeft;
        int hintsUsed;
        int hintsLeft;
        int counter;
    }

    /**
     * Generates a hint based on the game's current state, difficulty,
     * hint history, and remaining hints.
     *
     * @param generatedNumber the correct number
     * @param hintHistory list of used hint types
     * @param difficultyLevel selected difficulty
     * @param hintsLeft the number of remaining hints
     * @param attemptsLeft current remaining attempts
     * @return a hint string to show to the user
     */
    private String generateHint(int generatedNumber, List<String> hintHistory, String difficultyLevel, int hintsLeft, int attemptsLeft)
    {
        List<String> possibleHints = new ArrayList<>();
        boolean useStrongHint = false;

        if (difficultyLevel.equals("Hard"))
        {
            useStrongHint = true;
        }
        else if (difficultyLevel.equals("Easy") || difficultyLevel.equals("Medium"))
        {
            if (hintsLeft <= 1 || attemptsLeft <= 2)
                useStrongHint = true;
        }

        // picking strong hints from the hint pool
        if (!useStrongHint)
        {
            possibleHints.add("parity");
        }
        possibleHints.add("range");
        if (generatedNumber > 9)
            possibleHints.add("digitSum");

        possibleHints.removeAll(hintHistory);

        if (possibleHints.isEmpty())
        {
            if (!useStrongHint)
            {
                possibleHints.add("parity");
            }
            possibleHints.add("range");
            possibleHints.add("digitSum");

            // clearing hintHistory for full reuse
            hintHistory.clear();
        }

        // Picking a random hint type from possibleHints
        Random random = new Random();
        String chooseHint = possibleHints.get(random.nextInt(possibleHints.size()));

        // Calling the corresponding method and returning the result
        switch (chooseHint)
        {
            case "parity": return parityHint(generatedNumber, hintHistory);
            case "range": return rangeHint(generatedNumber, hintHistory, difficultyLevel);
            case "digitSum": return digitSumHint(generatedNumber, hintHistory);
            default: return "No hint available.";
        }
    }

    /**
     * Provides a parity hint indicating if the number is odd or even.
     *
     * @param generatedNumber the correct number
     * @param hintHistory list of used hints
     * @return a string indicating the parity
     */
    private String parityHint(int generatedNumber, List<String> hintHistory)
    {
        hintHistory.add("parity");
        if (generatedNumber % 2 == 0)
            return "Hint: the number is even.";
        else
            return "Hint: the number is odd.";
    }

    /**
     * Provides a range hint indicating a numerical window where the number is located.
     *
     * @param generatedNumber the correct number
     * @param hintHistory list of used hints
     * @param difficultyLevel selected difficulty
     * @return a string indicating the range
     */
    private String rangeHint(int generatedNumber, List<String> hintHistory, String difficultyLevel)
    {
        int easyWindow = 20;
        int mediumWindow = 10;
        int hardWindow = 5;
        int low;
        int high;

        hintHistory.add("range");

        switch (difficultyLevel)
        {
            case "Easy" ->
            {
                low = Math.max(MIN_VALUE, generatedNumber - easyWindow);
                high = Math.min(MAX_VALUE, generatedNumber + easyWindow);
                return "Hint: the number is between " + low + " and " + high + ".";
            }
            case "Medium" ->
            {
                low = Math.max(MIN_VALUE, generatedNumber - mediumWindow);
                high = Math.min(MAX_VALUE, generatedNumber + mediumWindow);
                return "Hint: the number is between " + low + " and " + high + ".";
            }
            case "Hard" ->
            {
                low = Math.max(MIN_VALUE, generatedNumber - hardWindow);
                high = Math.min(MAX_VALUE, generatedNumber + hardWindow);
                return "Hint: the number is between " + low + " and " + high + ".";
            }
            default ->
            {
                return "Hint: difficulty level unknown.";
            }
        }
    }

    /**
     * Provides a digit sum hint for numbers larger than 9.
     *
     * @param generatedNumber the correct number
     * @param hintHistory list of used hints
     * @return a string indicating the sum of the digits
     */
    private String digitSumHint(int generatedNumber, List<String> hintHistory)
    {
        hintHistory.add("digitSum");

        int sum = 0;
        int n = generatedNumber;

        while (n != 0)
        {
            int lastDigit = n % 10; // Extracts the last digit
            sum += lastDigit; // Adds last digit to sum
            n /= 10; // Gives the first digit by removing the last because it's an integer division and the decimal is dropped
        }
        return "Hint: the number's sum of digits is: " + sum;
    }

    /**
     * Entry point of the program.
     * Starts the game by calling the Menu() method.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args)
    {
        NumberGuessingGame Game = new NumberGuessingGame();
        Game.Menu();
        Game.keyboard.close();
    }
}