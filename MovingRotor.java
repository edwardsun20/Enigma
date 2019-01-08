package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author esun
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initally in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        _notches = notches;
    }

    @Override
    boolean atNotch() {
        char ch = alphabet().toChar(setting());
        if (_notches.indexOf(ch) >= 0) {
            return true;
        }
        return false;
    }

    @Override
    boolean rotates() {
        return true;
    }

    /** String notches. */
    private String _notches;

}
