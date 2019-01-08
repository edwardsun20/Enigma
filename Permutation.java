package enigma;

import java.util.HashMap;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author esun
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        _forwardMap = new HashMap<>();
        _backwardMap = new HashMap<>();
        String[] c = cycles.split("[( )]");
        for (String i : c) {
            addCycle(i);
        }
    }

    /** Declaring HashMaps for forwards and backwards. */
    private HashMap<Character, Character> _forwardMap, _backwardMap;


    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */

    private void addCycle(String cycle) {
        if (cycle.length() == 0) {
            return;
        }
        char from, to;
        for (int i = 0; i < cycle.length(); i++) {
            if (i == cycle.length() - 1) {
                from = cycle.charAt(i);
                to = cycle.charAt(0);
                _forwardMap.put(from, to);
                _backwardMap.put(to, from);
                break;
            }
            int j = i + 1;
            from = cycle.charAt(i);
            to = cycle.charAt(j);
            _forwardMap.put(from, to);
            _backwardMap.put(to, from);
        }
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        char ch = _alphabet.toChar(p);
        char nch = permute(ch);
        int index = _alphabet.toInt(nch);
        return index;
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        char ch = _alphabet.toChar(c);
        int nch = invert(ch);
        int index = _alphabet.toInt((char) nch);
        return index;
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        if (_forwardMap.containsKey(p)) {
            return _forwardMap.get(p);
        }
        return p;
    }

    /** Return the result of applying the inverse of this permutation to C. */
    int invert(char c) {
        if (_backwardMap.containsKey(c)) {
            return _backwardMap.get(c);
        }
        return c;
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        return true;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

}
