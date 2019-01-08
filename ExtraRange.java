package enigma;

import java.util.HashMap;

import static enigma.EnigmaException.error;

/** An Alphabet consisting of the Unicode characters in a certain range in
 *  order.
 *  @author P. N. Hilfinger
 */
class ExtraRange extends Alphabet {

    /** HashMap for forwards Map.*/
    private HashMap<Character, Integer> _forward;
    /** HashMap for backwards Map.*/
    private HashMap<Integer, Character> _backward;


    /** An alphabet consisting of all characters between FIRST and LAST,
     *  inclusive.
     * @param range range of alph substring
     *  */
    ExtraRange(String range) {
        _forward = new HashMap<>();
        _backward = new HashMap<>();
        for (int i = 1; i < range.length() - 1; i++) {
            char left = range.charAt(i - 1);
            char right = range.charAt(i + 1);
            if (Character.isAlphabetic(left)
                    && range.charAt(i) == '-'
                    && Character.isAlphabetic(right)) {
                int size = Math.abs(right - left - 1);

                char[] temp = new char[size + range.length() - 1];
                for (int j = 0; j < i; j++) {
                    temp[j] = range.charAt(j);
                }
                for (int j = 0; j < size; j++) {
                    temp[j + i] = (char) (j + left + 1);
                }
                for (int j = i + size; j < temp.length; j++) {
                    temp[j] = range.charAt(j - size + 1);
                }
                range = new String(temp);
            }
        }
        for (int i = 0; i < range.length(); i++) {
            _forward.put(range.charAt(i), i);
            _backward.put(i, range.charAt(i));
        }
        if (_forward.size() != _backward.size()) {
            throw error("duplicate characters");
        }
    }

    @Override
    int size() {
        return _forward.size();
    }

    @Override
    boolean contains(char ch) {
        return _forward.containsKey(ch);
    }

    @Override
    char toChar(int index) {
        if (!_backward.containsKey(index)) {
            throw error("character index out of range");
        }
        return _backward.get(index);
    }

    @Override
    int toInt(char ch) {
        if (!_forward.containsKey(ch)) {
            throw error("character out of range");
        }
        return _forward.get(ch);
    }

}
