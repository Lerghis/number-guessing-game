# number-guessing-game
A console-based interactive [number-guessing-game](https://github.com/Lerghis/number_guessing_game) built in Java. This project demonstrates real application program structure, clean input handling, and exception management.

---

## **Features**

- Three difficulty levels: **Easy**, **Medium**, **Hard**
- Limited attempts per difficulty: Easy (10), Medium (5), Hard (3)
- Optional hints: **Parity**, **Range**, **Digit Sum**
- Strong hint logic based on difficulty level and remaining hints
- High score tracking per difficulty
- Game timer and replay system
- Exception handling for invalid input

---

## **Program Structure**

The program follows a **professional, layered method structure**, where each method has a clear responsibility:

main() → Menu() → playGame() → processTurn() → generateHint() → specificHintMethods()


- `main()` → starts the program and calls the `Menu`
- `Menu()` → handles difficulty selection and game loop
- `playGame()` → manages a single round, attempts, timer, high scores
- `processTurn()` → processes each user input (guess or hint)
- `generateHint()` → selects the next hint based on history, difficulty, and remaining hints
- `parityHint()`, `rangeHint()`, `digitSumHint()` → specific hint generators

This separation ensures **clean, maintainable, and scalable code**.

---

## **Input Handling & Exception Logic**

- All user input is read using `keyboard.nextLine()` to read full lines.
- Inputs are **manually parsed** using `Integer.parseInt()` when needed.
- Invalid input triggers **try/catch blocks** without crashing the program.
- Examples of handled invalid inputs: `" 45abc"`, `"hintt"`, `" "`, `"five"`.

This ensures the program **never leaves leftover tokens in the input buffer**, preventing crashes in subsequent menus or turns.

---

## **Strong Hint System**

- Hints are categorized as **regular** (`parity`) and **strong** (`range`, `digitSum`).
- Conditions for strong hints:
    - Few hints remaining for Easy and Medium
    - Always strong for Hard difficulty
- The `generateHint` method automatically selects the correct hint type and ensures no repeated hints are given.

---

## **How to Run**

1. Clone the repository:

```bash
git clone https://github.com/Lerghis/number_guessing_game.git
cd number_guessing_game
```

2. Compile the program:

```bash
javac Main.java
```

3. Run the program:

```bash
java Main
```

4. Follow on-screen prompts to select difficulty and guess the number.

5. Use "hint" to request hints (limited based on difficulty).

