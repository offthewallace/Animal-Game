
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Scanner;
import java.io.File;

/**
 * Play the animal guessing game.
 *
 * @author Wallace He
 * @version 1.0, 12/1/2016
 */
public class AnimalGame {
    // Root of the tree containing the questions

    private static GameTreeNode gameTree = null;

    public static void main(String[] args) {
        String gameFile = null;

        // Set up a viewframe that manages the various input boxes we use.
        ViewFrame vf = new ViewFrame("Guess the Animal.");

        //setup the input file 
        gameFile = getInitialFile(vf);

        vf.setVisible(true);

        vf.println("Welcome to the animal guessing game.");

        // Load the tree of questions.
        gameTree = loadGameTree(gameFile);

        // Play until the user declines.
        while (playGame(vf));

        saveTreeToFile(gameTree, vf);

        vf.println("\nThanks for playing!");
    }

    /**
     * Get the name of the file from which the initial configuration should be
     * read, using the standard file dialog box provided by the AWT. This method
     * is static because it doesn't need to access any of the data members. It
     * is called before any object of type GUILife1D is created.
     *
     * @param f	 parent Frame for the dialog box.
     * @return	The name of the selected file, or null if no file was selected.
     */
    public static String getInitialFile(JFrame f) {

        String appDir = (String) (System.getProperty("user.dir"));
        JFileChooser d;
        d = new JFileChooser(appDir);
        d.setDialogTitle("Select Initial World)");
        int retVal = d.showOpenDialog(f);
        String fileName = null;
        String fileSeparator = (String) (System.getProperty("file.separator"));

        if (retVal == JFileChooser.APPROVE_OPTION) {
            fileName = d.getSelectedFile().getName();
        } else {
            System.exit(1);
        }

        System.out.println(appDir);

        
        System.out.println("filename = " + fileName);

        if (fileName == null) {

            // Cancel button was pushed -- assume user wants to quit.
            System.out.println(
                    "No file was specified: program will exit.");
            System.exit(0);
        }

        return fileName;
    }

    /**
     * Load the initial tree of questions from file.
     *
     * @param fileName filename string from the setIntinial tree.
     * @return A reference to the root node of the tree of questions.
     */
    public static GameTreeNode loadGameTree(String fileName) {
        GameTreeNode tree = null;
        String[] preorder = null;
        int[] inorder = null;

        File file = new File(fileName);

        try {

            //create a new scanner from file
            Scanner input = new Scanner(file);

            //get the size of tree fromthe file 
            int count = Integer.parseInt(input.nextLine());

            //create 2array, one string array storing the questions
            //int array inorder storing the index of inorder traversel
            preorder = new String[count];
            inorder = new int[count];

            //use time to track the postion of array 
            int tmp = 0;

            //read the question and inoder index from the file through while loop
            while (input.hasNext()) {
                String question = input.nextLine();
                int inOrderOrder = Integer.parseInt(input.nextLine());
                preorder[tmp] = question;
                inorder[tmp] = inOrderOrder;
                tmp++;
            }
            input.close();

        } catch (FileNotFoundException e) {
            //throw a fileNotFound exception
            System.out.println("Error in opening file: " + fileName + e);
        }

        //the start place of preoder string array
        int preStart = 0;
        //the end place of preorder string array
        int preEnd = preorder.length - 1;

        //the start place of inorder index array
        int inStart = 0;
        //the end place of inorder index array
        int inEnd = inorder.length - 1;

        //helper method return the tree
        tree = construct(preorder, preStart, preEnd, inorder, inStart, inEnd);

        return tree;

    }

    /**
     * Construct the tree from the given string preOrder array and
     * the int inOrder index array.
     *
     * @param preorder the string array get from the file containing the questions
     * @param preStart the beginning index of the preOrder array
     * @param preEnd the end index of preOrder array
     * @param inorder the int array containing inOrderpostion index of tree
     * @param inStart the beginning index of the inOrder array
     * @param inEnd the end index of inOrder array
     * @return a constructed tree from the given array.
     */
    private static GameTreeNode construct(String[] preorder, int preStart, int preEnd, int[] inorder, int inStart, int inEnd) {
        
        //if the begining position is larger than the ending position, recursion stop
        if (preStart > preEnd || inStart > inEnd) {
            return null;
        }

        //get the question of root node from preorder array
        String Question = preorder[preStart];
        //set up the root node 
        GameTreeNode p = new GameTreeNode(Question);
        //get the left tree ending position of the root not
        int inOrderIndex = inorder[preStart];

        
        //set up the left tree with the range of left tree from inorder poistion index
        p.left = construct(preorder, preStart + 1, preStart + (inOrderIndex - inStart), inorder, inStart, inOrderIndex - 1);
        
        //set upthe right tree with the range of right tree from the inoder position index
        p.right = construct(preorder, preStart + (inOrderIndex - inStart) + 1, preEnd, inorder, inOrderIndex + 1, inEnd);

        return p;
    }

     /**
     * Load the initial tree of questions to ask from a file.
     * @param p the root of a given tree
     * @param i the int for record the position of inorder traverls
     * @return return the last  inorder position of the whole tree based on p
     */
    private static int setinOrderPosition(GameTreeNode p, int i) {
        //if the node is null, return the positionto the next one 
        if (p == null) {
            return i;

        } else if (p.left == null || p.right == null) {
          
            //if the node is leaf, pass the position to the upper node  
            p.inOrderPostion = i;
            return i;
        }
        
        //calculate the left tree postion of the p first
        setinOrderPosition(p.left, i);
        //postion of p is equal the left position +1
        p.inOrderPostion = setinOrderPosition(p.left, i) + 1;
        //calculate the position of total right tree
        setinOrderPosition(p.right, p.inOrderPostion + 1);
        
        //return the last  inorder position of the whole tree based on p
        return setinOrderPosition(p.right, p.inOrderPostion + 1);

    }
    
    
 /**
     * save the modified tree of questions to ask from a file.
     * @param tree the root of a given tree
     * @param f pass in the Jframe to call the getInitialFile to get file name 
     */
    public static void saveTreeToFile(GameTreeNode tree, JFrame f) {
        //issues
        String fileName = saveFile(f);

        setinOrderPosition(tree, 0);
        try {

            PrintWriter output = new PrintWriter(fileName);
            output.println(CountNode(tree));
            outPutTree(tree, output);
            output.close();
            
        } catch (FileNotFoundException e) {

            System.err.println("Your file failed to create");
        }
    }

    /**
     * count how many node a given tree have.
     * @param tree the root of a given tree
     * @param f pass in the Jframe to call the getInitialFile to get file name
     * @return the number of node in the given tree.
     */
    private static int CountNode(GameTreeNode tree) {
        //local variable for counting the position
        int count = 0;
        
        //if the node ==null ignore it.
        if (tree == null) {
            return count;
        }
        
        count++;
        //passthe count to left and right treeand return.
        return count + CountNode(tree.left) + CountNode(tree.right);
    }

    /**
     * write in the  file of the given tree with preorder data and its inorder position
     * @param tree the root of a given tree
     * @param f pass in the Jframe to call the getInitialFile to get file name 
     */
    private static void outPutTree(GameTreeNode node, PrintWriter output) {
        
        if (node == null) {
            return;
            
        } else {
            //print the root first
            output.println(node.question);
            //print the root inorder position
            output.println(node.inOrderPostion);
            //pass to the left tree
            outPutTree(node.left, output);
            //pass  to the right tree
            outPutTree(node.right, output);

        }

    }
    
      /**
     * Get the name of the file for saving, using the standard file
     * dialog box provided by the AWT.
     * It is called before any object of type GUILife1D is created.
     *
     * @param f	 parent Frame for the dialog box.
     * @return	The name of the selected file, or null if no file was selected.
     */
    private static String saveFile(JFrame f) {

        String appDir = (String) (System.getProperty("user.dir"));
        JFileChooser d;
        d = new JFileChooser(appDir);
        d.setDialogTitle("Select Initial World)");
        int retVal = d.showOpenDialog(f);
        String fileName = null;
        String fileSeparator = (String) (System.getProperty("file.separator"));

        if (retVal == JFileChooser.APPROVE_OPTION) {
            
            fileName = d.getSelectedFile().getName();
            
        } else {
            System.exit(1);
        }

        System.out.println(appDir);

        System.out.println("filename = " + fileName);

        if (fileName == null) {

            // Cancel button was pushed -- assume user wants to quit.
            System.out.println(
                    "No file was specified: program will exit.");
            System.exit(0);
        }

        return fileName;
    }

    /**
     * Play the guessing game, asking at the end if the player wants to
     * continue.
     *
     * @param vf The ViewFrame which serves as the parent for the input boxes.
     * @return true if the player wants to go again, false otherwise.
     */
    public static boolean playGame(ViewFrame vf) {
        boolean result = false;

        // Starting at the root, follow the player's answers to a leaf.
        GameTreeNode current = gameTree;

        if (current == null) {
            System.out.println("Game initialization failed.");
            System.exit(1);
        }

        String response = null;

        // All internal nodes have both left and right children, so we don't
        // have to check both.
        while (current.left != null) {
            // Tack "(Yes or no)" onto the question, to make the expected
            //  response clear.  I.e., "Sometimes" or "maybe" won't do.
            response = vf.readString(current.question
                    + " (Yes or no)");

            // The next question for a "yes" response is found by following
            // the left link, etc.
            if (response.equalsIgnoreCase("yes")) {
                current = current.left;
            } else {
                current = current.right;
            }
        }

        // We've reached a leaf, which should ask a question about a specific
        // animal.
        response = vf.readString(current.question
                + " (Yes or no)");

        if (response.equalsIgnoreCase("yes")) {
            response = vf.readString(
                    "I thought so! Want to play again? (Yes or no)");
            result = response.equalsIgnoreCase("yes");
        } else {
            // If we didn't guess correctly, learn about the player's animal.
            addNewAnimal(current, vf);
            response = vf.readString(
                    "Thanks! Want to play again? (Yes or no)");
            result = response.equalsIgnoreCase("yes");

        }
        return (result);
    }

    /**
     * Learn about a new animal from the player.
     *
     * @param t The leaf node containing our incorrect guess.
     * @param vf The ViewFrame used to manage input boxes.
     */
    public static void addNewAnimal(GameTreeNode t, ViewFrame vf) {
        // Gather informatin about the player's animal.
        String newAnimal = vf.readString("What is your animal?");
        String newQuestion = vf.readString(
                "Type a y/n question to distinguish between my guess and your animal.");
        String newAnswer = vf.readString("What is the answer for your animal?");
        String oldAnimal = t.question;

        // Modify the current leaf node to hold the new question. The two 
        // answers will be the children.
        t.question = newQuestion;

        String newLeft = null;
        String newRight = null;
        if (newAnswer.equalsIgnoreCase("yes")) {
            newLeft = "Is your animal a(n) " + newAnimal + "?";
            newRight = oldAnimal;
        } else {
            newLeft = oldAnimal;
            newRight = "Is your animal a(n) " + newAnimal + "?";
        }

        t.left = new GameTreeNode(newLeft);
        t.right = new GameTreeNode(newRight);
    }
}
