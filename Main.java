package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Arrays;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author esun
 */

public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            new Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Check ARGS and open the necessary files (see comment on main). */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or 3 command-line arguments allowed");
        }

        _config = getInput(args[0]);

        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }

        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {

        Machine m = readConfig();
        Boolean init = false;

        Boolean e = false;
        while (_input.hasNext() || (_input.findInLine("(?=\\S)") == null)) {
            if (_input.hasNext("[*]")) {
                e = _input.findInLine("(?=\\S)") == null;
                if (e) {
                    printMessageLine(_input.nextLine());
                }
                _input.next();
                setUp(m, _input.nextLine().toUpperCase());
                init = true;
            }
            if (!init) {
                throw error("input file truncated");
            }
            if (!_input.hasNextLine()) {
                break;
            }
            if (_input.hasNext("[*]")) {
                continue;
            }
            String line = _input.nextLine().toUpperCase();
            String outcome = m.convert(line);

            if (!_upperCase) {
                outcome = outcome.toLowerCase();
            }

            printMessageLine(outcome);
        }
    }

    /** Instance variables for TotalRotors, MovingRotors. */
    private int _numTotalRotors, _numMovingRotors;
    /** Instance variable for allRotors. */
    private ArrayList<Rotor> _allRotors;
    /** Instance variable for UpperCase. */
    private boolean _upperCase = true;


    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            String al = _config.next();
            _upperCase = Character.isUpperCase(al.charAt(0));
            al = al.toUpperCase();

            _alphabet = new ExtraRange(al);
            if (!_config.hasNextInt()) {
                throw error("configuration file format error");
            }
            _numTotalRotors = _config.nextInt();
            if (!_config.hasNextInt()) {
                throw error("configuration file format error");
            }
            _numMovingRotors = _config.nextInt();
            _allRotors = new ArrayList<>();
            while (_config.hasNext()) {
                _allRotors.add(readRotor());
            }

            validateRotors();
            return new Machine(_alphabet, _numTotalRotors,
                    _numMovingRotors, _allRotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** validate. */
    private void validateRotors() {
        HashSet<String> names = new HashSet<>();
        for (Rotor r : _allRotors) {
            if (names.contains(r.name())) {
                throw error("Rotor repeated name");
            }
            names.add(r.name());
        }
    }

    /** Validate.
     * @param cycles - check
     */
    private void validateCycles(String cycles) {
        for (String i : cycles.split(" ")) {
            if (i.length() == 0) {
                continue;
            }
            if (!i.matches("[(].+[)]")) {
                throw error("bad cycles format");
            }
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            String name = _config.next().toUpperCase();
            String type = _config.next().toUpperCase();
            String notches = type.substring(1, type.length());
            String cycles = _config.nextLine().toUpperCase();
            if (_config.hasNext("[(].+")) {
                cycles = cycles.concat(_config.nextLine().toUpperCase());
            }
            validateCycles(cycles);

            if (type.charAt(0) == 'M') {
                return new MovingRotor(name,
                        new Permutation(cycles, _alphabet), notches);
            } else if (type.charAt(0) == 'N') {
                return new FixedRotor(name, new Permutation(cycles, _alphabet));
            } else if (type.charAt(0) == 'R') {
                return new Reflector(name, new Permutation(cycles, _alphabet));
            } else {
                throw error("bad rotor format");
            }

        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {

        String[] all = settings.trim().split(" ");
        String[] rotors = Arrays.copyOfRange(all, 0, _numTotalRotors);
        if (_numTotalRotors >= all.length) {
            throw error("bad number of rotors");
        }
        String machineSetting = all[_numTotalRotors];
        String plugBoardCycles = new String();

        for (int i = _numTotalRotors + 1; i < all.length; i++) {
            plugBoardCycles = String.join(" ", plugBoardCycles, all[i]);
        }

        M.insertRotors(rotors);
        M.setRotors(machineSetting);
        M.setPlugboard(new Permutation(plugBoardCycles, _alphabet));
    }


    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        String newret = msg.replaceAll(".{5}", "$0 ");
        _output.println(newret);
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;
}