package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;

import java.util.HashMap;
import java.util.Map;

import static enigma.TestUtils.NAVALA;
import static org.junit.Assert.*;

import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the Permutation class.
 *  @esun
 */
public class PermutationTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(500);

    /* ***** TESTING UTILITIES ***** */

    private Permutation perm;
    private String alpha = UPPER_STRING;

    /** Check that perm has an alphabet whose size is that of
     *  FROMALPHA and TOALPHA and that maps each character of
     *  FROMALPHA to the corresponding character of FROMALPHA, and
     *  vice-versa. TESTID is used in error messages. */
    private void checkPerm(String testId,
                           String fromAlpha, String toAlpha) {
        int N = fromAlpha.length();
        assertEquals(testId + " (wrong length)", N, perm.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            assertEquals(msg(testId, "wrong translation of '%c'", c),
                         e, perm.permute(c));
            assertEquals(msg(testId, "wrong inverse of '%c'", e),
                         c, perm.invert(e));
            int ci = alpha.indexOf(c), ei = alpha.indexOf(e);
            assertEquals(msg(testId, "wrong translation of %d", ci),
                         ei, perm.permute(ci));
            assertEquals(msg(testId, "wrong inverse of %d", ei),
                         ci, perm.invert(ei));
        }
    }

    /* ***** TESTS ***** */

    @Test
    public void checkIdTransform() {
        perm = new Permutation("", UPPER);
        checkPerm("identity", UPPER_STRING, UPPER_STRING);
    }


    void checkOne(HashMap<String, String> navala,
                  HashMap<String, String> navalaMap) {
        for (Map.Entry<String, String> entry : navala.entrySet()) {
            String key = entry.getKey();
            String cycles = entry.getValue();

            if (!navalaMap.containsKey(key)) {
                continue;
            }
            perm = new Permutation(cycles, UPPER);

            String rightResult = navalaMap.get(key);
            System.out.printf("key%s=s, cycles=%s, rightResult=%s\n",
                    key, cycles, rightResult);
            int x = 0;
            for (char i = 'A'; i <= 'Z'; i++) {
                char actual = perm.permute(i);
                assertEquals(msg("NAVALA",
                        "wrong permute of key %s at char %c", key, i),
                        rightResult.charAt(i - 'A'), actual);
            }
        }
    }

    @Test
    public void checkNAVALA() {
        checkOne(NAVALA, NAVALA_MAP);
        checkOne(NAVALB, NAVALB_MAP);
        checkOne(NAVALZ, NAVALZ_MAP);

    }

}
