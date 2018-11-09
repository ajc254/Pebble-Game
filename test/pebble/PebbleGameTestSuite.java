package pebble;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Run all the test classes.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses(value={PebbleGameTest.class, GameUserInputTest.class, BagTest.class, BlackBagTest.class, WhiteBagTest.class})
public class PebbleGameTestSuite {}
