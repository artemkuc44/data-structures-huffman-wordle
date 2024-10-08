package wordle;

import project20280.hashtable.ChainHashMap;
import project20280.interfaces.BinaryTree;
import project20280.interfaces.Position;
import project20280.priorityqueue.HeapPriorityQueue;
import project20280.tree.BinaryTreePrinter;
import project20280.tree.LinkedBinaryTree;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static java.lang.System.exit;

public class Wordle {

    public static final String MISS = "⬛";
    public static final String MISPLACED = "🟨";
    public static final String EXACT= "🟩";

    String fileName = "wordle/resources/dictionary.txt";
    //for normal dictionary:
    //Average number of guesses over 1000 games: 3.819
    //Percentage of games won within 6 guesses: 99.0%

    //String fileName = "wordle/resources/extended-dictionary.txt";
    //for extended dictionary:
    //Average number of guesses over 1000 games: 4.616
    //Percentage of games won within 6 guesses: 90.6%

    //String fileName = "wordle/resources/testDict.txt";
    List<String> dictionary = null;
    final int num_guesses = 20;
    int num_guesses_made;
    final long seed = 42;
    //Random rand = new Random(seed);
    Random rand = new Random();

    static final String winMessage = "CONGRATULATIONS! YOU WON! :)";
    static final String lostMessage = "YOU LOST :( THE WORD CHOSEN BY THE GAME IS: ";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
    public static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
    public static final String ANSI_GREY_BACKGROUND = "\u001B[100m";

    String target;


    Wordle() {

        this.dictionary = readDictionary(fileName);

        //System.out.println("dict length: " + this.dictionary.size());
        //System.out.println("dict: " + dictionary);

    }

    public static void main(String[] args) {

        Wordle game = new Wordle();

        //game.simulateGames(1);

        game.play("abbey");

    }

    public void simulateGames(int numberOfGames) {
        int totalGuesses = 0;
        int gamesWonWithinSixGuesses = 0;

        for (int i = 0; i < numberOfGames; i++) {
            Wordle gameInstance = new Wordle(); //new instance for each game
            String targetWord = gameInstance.getRandomTargetWord(); //randomly select a target word for the game
            gameInstance.play(targetWord);
            totalGuesses += gameInstance.num_guesses_made;

            if (gameInstance.num_guesses_made <= 6) {
                gamesWonWithinSixGuesses++;
            }
        }
        float averageGuesses = (float) totalGuesses / numberOfGames; //average number of guesses
        float percentageOfGamesWithinSixGuesses = (float) gamesWonWithinSixGuesses / numberOfGames * 100; //percentage of games won within 6 guesses

        System.out.println("Average number of guesses over " + numberOfGames + " games: " + averageGuesses);
        System.out.println("Percentage of games won within 6 guesses: " + percentageOfGamesWithinSixGuesses + "%");
    }

    public void play(String target) {
        // TODO
        // TODO: You have to fill in the code

        List<String> filtered_dict = dictionary;
        for(int i = 1; i <= num_guesses; ++i) {

            System.out.println("guess number: " + i);
            String guess = getMostLikelyGuess(filtered_dict);

            if(i==1){
                guess = "keeps";
            }

            System.out.println(guess);

            if(Objects.equals(guess, target)) { // you won!
                System.out.println("\t number of guesses " + i);
                num_guesses_made = i;
                win(target);
                return;
            }

            // the hint is a string where green="+", yellow="o", grey="_"
            // didn't win ;(

            // after setting the yellow and green positions, the remaining hint positions must be "not present" or "_"
            System.out.println("hint: " + Arrays.toString(getHint(target,guess)));

            filtered_dict = filterDictionaryBasedOnHint(filtered_dict,guess,getHint(target,guess));

            //System.out.println("possible words: \n" + createpossible_words(filtered_dict).toString());
            System.out.println(filtered_dict.size() + "\n" +filtered_dict + "\n");
            System.out.println("\n" + filtered_dict.size() + "\n");
        }

        lost(target);
    }

    public String[] getHint(String target,String guess){
        String [] hint = {MISS, MISS, MISS, MISS, MISS};

        boolean[] matched = new boolean[5]; // Tracks if a target character has been matched

        for (int k = 0; k < 5; k++) {
            //TODO
            if(guess.charAt(k) == target.charAt(k)){
                hint[k] = EXACT;
                matched[k] = true;
            }
        }
        // set the arrays for yellow (present but not in right place), grey (not present)
        // loop over each entry:
        //  if hint == "+" (green) skip it
        //  else check if the letter is present in the target word. If yes, set to "o" (yellow)
        for (int k = 0; k < 5; k++) {
            //TODO
            if (hint[k].equals(EXACT)) {
                continue;
            }

            for (int j = 0; j < 5; j++) {
                if (guess.charAt(k) == target.charAt(j) && !matched[j] && !hint[k].equals(EXACT)) {
                    hint[k] = MISPLACED;
                    matched[j] = true;
                    break;
                }
            }
        }
        return hint;
    }



    public void lost(String target) {
        System.out.println();
        System.out.println(lostMessage + target.toUpperCase() + ".");
        System.out.println();
        exit(0);

    }
    public void win(String target) {
        System.out.println(ANSI_GREEN_BACKGROUND + target.toUpperCase() + ANSI_RESET);
        System.out.println();
        System.out.println(winMessage);
        System.out.println();
    }

    public String getGuess() {//scans in guess
        Scanner myScanner = new Scanner(System.in, StandardCharsets.UTF_8.displayName());  
        System.out.println("Guess:");

        String userWord = myScanner.nextLine();  //read user input
        userWord = userWord.toLowerCase(); //covert to lowercase

        //check the length of the word and if it in dict
        while ((userWord.length() != 5) || !(dictionary.contains(userWord))) {
            if ((userWord.length() != 5)) {
                System.out.println("The word " + userWord + " does not have 5 letters.");
            } else {
                System.out.println("The word " + userWord + " is not in the word list.");
            }
            // Ask for a new word
            System.out.println("Please enter a new 5-letter word.");
            userWord = myScanner.nextLine();
        }
        return userWord;
    }

    public String getMostLikelyGuess(List<String> dictionary){
        //Scanner myScanner = new Scanner(System.in, StandardCharsets.UTF_8.displayName());  // Create a Scanner object
        System.out.println("Guess: ");

        //String userWord = myScanner.nextLine();  // Read user input
        String userWord = getMostLikelyWord(dictionary);
        userWord = userWord.toLowerCase();

        // check the length of the word and if it exists
        while ((userWord.length() != 5) || !(dictionary.contains(userWord))) {
            if ((userWord.length() != 5)) {
                System.out.println("The word " + userWord + " does not have 5 letters.");
            } else {
                System.out.println("The word " + userWord + " is not in the word list.");
            }
            // Ask for a new word
            //System.out.println("Please enter a new 5-letter word.");
            //userWord = myScanner.nextLine();
        }
        return userWord;
    }


    public String getRandomTargetWord() {
        // generate random values from 0 to dictionary size
        return dictionary.get(rand.nextInt(dictionary.size()));
    }
    public List<String> readDictionary(String fileName) {
        List<String> wordList = new ArrayList<>();

        try {
            // Open and read the dictionary file
            InputStream in = this.getClass().getClassLoader().getResourceAsStream(fileName);
            assert in != null;
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String strLine;

            //Read file line By line
            while ((strLine = reader.readLine()) != null) {
                wordList.add(strLine.toLowerCase());
            }
            //Close the input stream
            in.close();

        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
        return wordList;
    }

    public List<String> filterDictionaryBasedOnHint(List<String> dictionary, String guess, String[] hint) {
        List<String> filteredWords = new ArrayList<>();

        for(String word:dictionary){
            if(isWordCompatibleWithHint(word,hint,guess)){
                filteredWords.add(word);
            }
        }

        return filteredWords;
    }

    public boolean isWordCompatibleWithHint(String word, String[] hint, String guess) {

        //target: close
        //guess: cheer
        //hint: 🟩⬛🟨⬛⬛

        //word: camel

        //handle duplicates
        boolean[] isMatched = new boolean[word.length()];
        ArrayList<Character> misplaced = new ArrayList<>();

        //exact
        for (int i = 0; i < guess.length(); i++) {
            if (hint[i].equals(EXACT)) {
                if (guess.charAt(i) != word.charAt(i)) {
                    return false;
                }
                isMatched[i] = true;
            }
        }

        //misplaced
        for (int i = 0; i < guess.length(); i++) {
            if (hint[i].equals(MISPLACED)) {
                if(guess.charAt(i) == word.charAt(i)){
                    return false;
                }
                boolean foundMisplaced = false;
                for (int j = 0; j < word.length(); j++) {
                    if (guess.charAt(i) == word.charAt(j) && !isMatched[j]) {
                        foundMisplaced = true;
                        isMatched[j] = true;
                        misplaced.add(guess.charAt(i));
                        break;
                    }
                }
                if (!foundMisplaced) return false;
            }
        }

        //miss
        for (int i = 0; i < guess.length(); i++) {
            if (hint[i].equals(MISS)) {
                if(misplaced.contains(word.charAt(i)) && guess.charAt(i) == word.charAt(i)){//camel
                    return false;
                }
                for (int j = 0; j < word.length(); j++) {
                    if (guess.charAt(i) == word.charAt(j) && !isMatched[j]) {
                        return false;
                    }
                }
            }
        }

        return !guess.equals(word);//lever revel
    }


    public String getMostLikelyWord(List<String> dictionary) {
        int freq = 0;
        String highestFreqWord = "";
        // First calculate the frequency of each letter in the entire dictionary
        project20280.hashtable.ChainHashMap<Character, Integer> letterFrequencyMap = new project20280.hashtable.ChainHashMap<>();
        for (String word : dictionary) {
            for (char c : word.toCharArray()) {
                letterFrequencyMap.put(c, letterFrequencyMap.getOrDefault(c, 0) + 1);
            }
        }

        // Now calculate the score for each word based on the letter frequencies
        project20280.hashtable.ChainHashMap<String, Integer> possible_words = new project20280.hashtable.ChainHashMap<>();
        for (String word : dictionary) {
            int score = 0;
            for (char c : word.toCharArray()) {
                score += letterFrequencyMap.get(c);
            }
            possible_words.put(word, score);
            if(score>freq){
                freq = score;
                highestFreqWord = word;
            }
        }
        System.out.println("Load factor: " + possible_words.getLoadFactor());
        System.out.println("Collisions: " + possible_words.countCollisions());

        return highestFreqWord;
    }



}

//AVERAGE: 4.6