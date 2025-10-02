import java.util.*;

public class Main
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

    // Game Loop
    public void playGame(int maxAttempts, int generatedNumber, String difficultyLevel)
    {
        int attemptsLeft = maxAttempts;
        int hintsUsed = 0; // Increment each time the player asks for a hint.
        int hintLimit = HINT_LIMITS.getOrDefault(difficultyLevel.trim(),0); // getOrDefault means: if difficulty isn’t found, fall back to 0
        int hintsLeft = hintLimit - hintsUsed;
        List<String> hintHistory = new ArrayList<>(); // to avoid repeating the same type of hint. Every time a hint is given, add "parity" or "range" to this list.
        int counter = 0;
        boolean guessedCorrectly = false;

        long startTime = System.currentTimeMillis();

        System.out.println("\nGreat! You have selected " + difficultyLevel + " difficulty level. Your available hints are: " + (hintsLeft) + ".");
        System.out.println("Lets start the game!");

        /**
         * This loop keeps running until:
         * The user guesses correctly → guessedCorrectly = true, OR
         * The user runs out of attempts → attemptsLeft == 0
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

        /*
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
                long endTime = System.currentTimeMillis();
                long duration = (startTime - endTime) / 1000; // convert milliseconds to seconds

                System.out.println("Congratulations! You guessed the correct number in " + counter + " attempts!");
                System.out.println("Time takes: " + duration + " seconds.");
                System.out.println("Let's play again!");
                guessedCorrectly = true;

                // update high scores
                if (!highScores.containsKey(difficultyLevel) || counter < highScores.get(difficultyLevel))
                {
                    highScores.put(difficultyLevel, counter);
                    System.out.println("New high score for " + difficultyLevel + " difficulty: " + counter + " attempts!");
                }
                else
                    System.out.println("Current high score for " + difficultyLevel + ": " + highScores.get(difficultyLevel) + " attempts.");
                break;
            }
        }*/
    }

    public TurnResult processTurn(int generatedNumber, int attemptsLeft, int hintLimit, int hintsUsed, int hintsLeft, List<String> hintHistory, String difficultyLevel, int counter)
    {
        TurnResult result = new TurnResult();
        String input;


            System.out.println("\nEnter your guess (remaining chances: " + attemptsLeft +") or type 'hint' (hints left: " + hintsLeft + ", cost: 1 attempt): ");
            input = keyboard.next().trim();

            if (input.equalsIgnoreCase("hint"))
            {
                if (hintsUsed < hintLimit)
                {
                    System.out.println(generateHint(generatedNumber, hintHistory, difficultyLevel, hintsLeft, attemptsLeft));
                    hintsUsed++;
                    hintsLeft = hintLimit - hintsUsed;
                    attemptsLeft--; // penalty
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

    static class TurnResult
    {
        boolean guessedCorrectly;
        int attemptsLeft;
        int hintsUsed;
        int hintsLeft;
        int counter;
    }

    // Hint Logic
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

    private String parityHint(int generatedNumber, List<String> hintHistory)
    {
        hintHistory.add("parity");
        if (generatedNumber % 2 == 0)
            return "Hint: the number is even.";
        else
            return "Hint: the number is odd.";
    }

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
    public static void main(String[] args)
    {
        Main Game = new Main();
        Game.Menu();
        Game.keyboard.close();
    }
}