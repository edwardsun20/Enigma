package enigma;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collection;
import java.util.HashSet;

import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author esun
 */

class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */

    private int _numRotors, _numPawls;

    /** HashMap for Rotors.*/
    private HashMap<String, Rotor> _allRotors;
    /** HashMap for activeRotors.*/
    private ArrayList<Rotor> _activeRotors;
    /** Permutation object for plugboard.*/
    private Permutation _plugboard;

    /** Machine constructor.
     * @param alpha Alphabet object
     * @param numRotors Number of Rotors
     * @param pawls Number of pawls
     * @param allRotors All Rotors
     * */

    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        /** @param alpha */
        _alphabet = alpha;
        _numRotors = numRotors;
        _numPawls = pawls;
        _allRotors = new HashMap<>();
        allRotors.forEach(i -> {
            _allRotors.put(i.name(), i); });

    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _numPawls;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        _activeRotors = new ArrayList<>();
        for (String r:rotors) {
            _activeRotors.add(_allRotors.get(r));
        }
        validateRotors();
    }

    /** validate. */
    private void validateRotors() {
        HashSet<String> names = new HashSet<>();
        for (Rotor r : _activeRotors) {
            if (r == null) {
                throw error("Rotor not found");
            }
            if (names.contains(r.name())) {
                throw error("Rotor repeated setting name");
            }
            names.add(r.name());
        }
        if (!_activeRotors.get(0).reflecting()) {
            throw error("First Rotor not refelcting");
        }
        for (int i = 1; i < _numPawls - 1; i++) {
            if (_activeRotors.get(i).reflecting()) {
                throw error("Refelctor not at first");
            }
            if (_activeRotors.get(i).rotates()) {
                throw error("Should not have rotating rotor here");
            }
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 upper-case letters. The first letter refers to the
     *  leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        for (int i = 1; i < _numRotors; i++) {
            char ch = setting.charAt(i - 1);
            _activeRotors.get(i).set(ch);
        }
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */

    /** Making convert method.
     * @return int
     * @param c integer to convert
     * */

    int convert(int c) {
        char in = (char) c;

        char pc = _plugboard.permute((char) c);
        int p = _alphabet.toInt(pc);

        Boolean[] pulled = new Boolean[_numRotors];
        for (int i = 0; i < _numRotors; i++) {
            pulled[i] = false;
        }
        for (int i = _numPawls - 1; i < _numRotors - 1; i++) {
            if (_activeRotors.get(i + 1).atNotch()) {
                pulled[i] = true;
                pulled[i + 1] = true;
            }
        }
        pulled[_numRotors - 1] = true;
        for (int i = _numPawls - 1; i < _numRotors; i++) {
            if (pulled[i]) {
                _activeRotors.get(i).advance();
            }
        }

        for (int i = _numRotors - 1; i >= 0; i--) {
            Rotor r = _activeRotors.get(i);
            p = r.convertForward(p);
        }

        char endOfForward = (char) (p + 'A');
        for (int i = 1; i < _numRotors; i++) {
            Rotor r = _activeRotors.get(i);
            p = r.convertBackward(p);
            char test = (char) (p + 'A');
            int x = 0;
        }

        p = _plugboard.invert(p);
        char nc = _alphabet.toChar(p);

        return nc;
    }

    /** Convert.
     * @param msg Message to convert
     * @return String
     * */
    String convert(String msg) {
        return msg.chars().filter(i -> i != ' ').map(this::convert)
                .collect(StringBuilder::new,
                        StringBuilder::appendCodePoint,
                        StringBuilder::append).toString();

    }

    /** Alphabet. */
    private final Alphabet _alphabet;
}
