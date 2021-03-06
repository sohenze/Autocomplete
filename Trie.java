import java.util.ArrayList;

public class Trie {

    // Wildcards
    final char WILDCARD = '.';

    //Size of children array of each node
    //0 - 9, A - Z, a - z (index 0-62)
    //empty string(index 63)
    static final int ALPHANUMERIC = 63;

    //Index to store empty string
    static final int EMPTY_INDEX = 62;

    static TrieNode root;

    private class TrieNode {
        //Array representing children
        /*
        Index should represent ascii value
        Index -1 represents root node
        Index 0-9 represents numeric values 0-9
        Index 10 - 35 represents uppercase alphabets A-Z
        Index 36 - 61 represents lowercase a-z
        Index 62 represents empty string
        */
        TrieNode[] children = new TrieNode[ALPHANUMERIC];

        //Int representation of char in node based on indexes listed above
        /*key -1: root
        key 0-9: 0-9
        key 10-35: A-Z
        key 36-61: a-z
        key 62: empty string
        */
        int key;

        //node indicating the end of word
        boolean endNode;

        public TrieNode(String s) {

            if (s == "root") {
                for (int i = 0; i < 36; i++) {
                    children[i] = null;
                }
                endNode = false;
                key = -1;

            } else if (s == "empty") {
                for (int i = 0; i < 36; i++) {
                    children[i] = null;
                }
                endNode = true;
                key = EMPTY_INDEX;
            }


        }

        public TrieNode(int asciiValue) {
            for (int i = 0; i < 36; i++) {
                children[i] = null;
            }
            endNode = false;
            key = asciiValue;
        }

    }

    public Trie() {
        this.root = new TrieNode("root");
    }

    //Converts ascii value to appropriate index to be used in children array
    public int asciiToIndex(int asciiValue) {

        int index;

        //If 0-9, convert to 0-9
        if (asciiValue >= 48 && asciiValue <= 57) {
            index = asciiValue - 48;

        //If A-Z, convert to 10-35
        } else if (asciiValue >= 65 && asciiValue <= 90) {
            index = asciiValue - 55;

        //If a-z, convert to 36-61
        } else {
            index = asciiValue - 61;
        }

        return index;
    }

    //Converts index to ascii value
    public int indexToAscii(int index) {

        int asciiValue;


        if (index >= 0 && index <= 9) {
            asciiValue = index + 48;

        } else if (index >= 10 && index <= 35) {
            asciiValue = index + 55;

        } else {
            asciiValue = index + 61;
        }

        return asciiValue;
    }


    /**
     * Inserts string s into the Trie.
     *
     * @param s string to insert into the Trie
     */
    void insert(String s) {

        //String length
        int len = s.length();

        //Node cursor
        TrieNode node = this.root;

        //If empty string
        if (len == 0) {
            node.children[EMPTY_INDEX] = new TrieNode("empty");
        }

        //Iterating through chars in s
        for (int i = 0; i < len; i++) {


            //Processing ascii value
            int index = asciiToIndex(s.charAt(i));

            //if char does not exist in children array, create new node
            if (node.children[index] == null) {
                //Insert
                node.children[index] = new TrieNode(index);
            }

            //Updating node cursor to child node based on next char of s
            node = node.children[index];

            //If last node of string, indicate it is the end of a string
            if (i == len - 1) {
                node.endNode = true;
            }
        }

    }

    /**
     * Checks whether string s exists inside the Trie or not.
     *
     * @param s string to check for
     * @return whether string s is inside the Trie
     */
    boolean contains(String s) {

        //String length
        int len = s.length();

        //Node cursor
        TrieNode node = this.root;

        //If s is empty string, return true if empty index of children array != null
        if (len == 0) {
            return node.children[EMPTY_INDEX] != null;
        }

        //Iterating through chars in s
        for (int i = 0; i < len; i++) {

            //Processing ascii value, convert to index
            int index = asciiToIndex(s.charAt(i));

            //if char does not exist in children array, return false
            if (node.children[index] == null) {
                return false;
            }

            //Updating node cursor to child node based on next char of s
            node = node.children[index];

            //If last char of string, check if current node is an end node
            //If end node, return true;
            if (i == len - 1) {
                return (node.endNode == true);
            }
        }

        return false;
    }


    public void resultsSetter(TrieNode node, String prefix, int prefixIndex, String builder, ArrayList<String> results, int limit) {

        if (results.size() >= limit) return;

        //Base case
        //If current node is endNode flag and currently built word length >= prefix length
        if (node.endNode && (builder.length() >= prefix.length())) {
            results.add(builder);
        }


        //Tracing prefix
        if (prefixIndex < prefix.length()) {
            char prefixChar = prefix.charAt(prefixIndex);
            int prefixCharIndex = asciiToIndex(prefixChar);

            //If prefixChar is alphanumeric
            if (prefixChar != '.') {
                //Check for child node that matches prefix char
                if (node.children[prefixCharIndex] != null) {
                    resultsSetter(node.children[prefixCharIndex], prefix, prefixIndex + 1, builder + prefixChar, results, limit);
                }

                //Stops from printing multiple times, but not sure why this needs to be here
                return;
            }
            //Else recurse through all children, done below
        }

        //If no longer tracing prefix
        //Recurse through all children
        for (int i = 0; i < ALPHANUMERIC - 1; i++) {
            if (node.children[i] != null) {
                char nodeChar = (char) indexToAscii(i);
                resultsSetter(node.children[i], prefix, prefixIndex + 1, builder + nodeChar, results, limit);
            }
        }
    }

    /**
     * Searches for strings with prefix matching the specified pattern sorted by lexicographical order. This inserts the
     * results into the specified ArrayList. Only returns at most the first limit results.
     *
     * @param s       pattern to match prefixes with
     * @param results array to add the results into
     * @param limit   max number of strings to add into results
     */
    void prefixSearch(String s, ArrayList<String> results, int limit) {

        resultsSetter(this.root, s, 0, "", results, limit);

    }


    // Simplifies function call by initializing an empty array to store the results.
    // PLEASE DO NOT CHANGE the implementation for this function as it will be used
    // to run the test cases.
    String[] prefixSearch(String s, int limit) {
        ArrayList<String> results = new ArrayList<String>();
        prefixSearch(s, results, limit);
        return results.toArray(new String[0]);
    }


    public static void main(String[] args) {
        Trie t = new Trie();
        t.insert("peter");
        t.insert("piper");
        t.insert("picked");
        t.insert("a");
        t.insert("peck");
        t.insert("of");
        t.insert("pickled");
        t.insert("peppers");
        t.insert("pepppito");
        t.insert("pepi");
        t.insert("pik");



        String[] result1 = t.prefixSearch("a", 10);
        for (int i = 0; i < result1.length; i++) {
            System.out.println(result1[i]);
        }

//        String[] result2 = t.prefixSearch("e",10);
//        for (int i = 0; i < result2.length; i++) {
//            System.out.println(result2[i]);
//        }
        // result1 should be:
        // ["peck", "pepi", "peppers", "pepppito", "peter"]
        // result2 should contain the same elements with result1 but may be ordered arbitrarily
    }
}
