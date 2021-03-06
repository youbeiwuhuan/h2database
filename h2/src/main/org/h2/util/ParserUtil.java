/*
 * Copyright 2004-2018 H2 Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (http://h2database.com/html/license.html).
 * Initial Developer: H2 Group
 */
package org.h2.util;

public class ParserUtil {

    /**
     * A keyword.
     */
    public static final int KEYWORD = 1;

    /**
     * An identifier (table name, column name,...).
     */
    public static final int IDENTIFIER = 2;

    /**
     * The token "ALL".
     */
    public static final int ALL = IDENTIFIER + 1;

    /**
     * The token "CHECK".
     */
    public static final int CHECK = ALL + 1;

    /**
     * The token "CONSTRAINT".
     */
    public static final int CONSTRAINT = CHECK + 1;

    /**
     * The token "CROSS".
     */
    public static final int CROSS = CONSTRAINT + 1;

    /**
     * The token "CURRENT_DATE".
     */
    public static final int CURRENT_DATE = CROSS + 1;

    /**
     * The token "CURRENT_TIME".
     */
    public static final int CURRENT_TIME = CURRENT_DATE + 1;

    /**
     * The token "CURRENT_TIMESTAMP".
     */
    public static final int CURRENT_TIMESTAMP = CURRENT_TIME + 1;

    /**
     * The token "DISTINCT".
     */
    public static final int DISTINCT = CURRENT_TIMESTAMP + 1;

    /**
     * The token "EXCEPT".
     */
    public static final int EXCEPT = DISTINCT + 1;

    /**
     * The token "EXISTS".
     */
    public static final int EXISTS = EXCEPT + 1;

    /**
     * The token "FALSE".
     */
    public static final int FALSE = EXISTS + 1;

    /**
     * The token "FETCH".
     */
    public static final int FETCH = FALSE + 1;

    /**
     * The token "FOR".
     */
    public static final int FOR = FETCH + 1;

    /**
     * The token "FOREIGN".
     */
    public static final int FOREIGN = FOR + 1;

    /**
     * The token "FROM".
     */
    public static final int FROM = FOREIGN + 1;

    /**
     * The token "FULL".
     */
    public static final int FULL = FROM + 1;

    /**
     * The token "GROUP".
     */
    public static final int GROUP = FULL + 1;

    /**
     * The token "HAVING".
     */
    public static final int HAVING = GROUP + 1;

    /**
     * The token "INNER".
     */
    public static final int INNER = HAVING + 1;

    /**
     * The token "INTERSECT".
     */
    public static final int INTERSECT = INNER + 1;

    /**
     * The token "IS".
     */
    public static final int IS = INTERSECT + 1;

    /**
     * The token "JOIN".
     */
    public static final int JOIN = IS + 1;

    /**
     * The token "LIKE".
     */
    public static final int LIKE = JOIN + 1;

    /**
     * The token "LIMIT".
     */
    public static final int LIMIT = LIKE + 1;

    /**
     * The token "LOCALTIME".
     */
    public static final int LOCALTIME = LIMIT + 1;

    /**
     * The token "LOCALTIMESTAMP".
     */
    public static final int LOCALTIMESTAMP = LOCALTIME + 1;

    /**
     * The token "MINUS".
     */
    public static final int MINUS = LOCALTIMESTAMP + 1;

    /**
     * The token "NATURAL".
     */
    public static final int NATURAL = MINUS + 1;

    /**
     * The token "NOT".
     */
    public static final int NOT = NATURAL + 1;

    /**
     * The token "NULL".
     */
    public static final int NULL = NOT + 1;

    /**
     * The token "OFFSET".
     */
    public static final int OFFSET = NULL + 1;

    /**
     * The token "ON".
     */
    public static final int ON = OFFSET + 1;

    /**
     * The token "ORDER".
     */
    public static final int ORDER = ON + 1;

    /**
     * The token "PRIMARY".
     */
    public static final int PRIMARY = ORDER + 1;

    /**
     * The token "ROWNUM".
     */
    public static final int ROWNUM = PRIMARY + 1;

    /**
     * The token "SELECT".
     */
    public static final int SELECT = ROWNUM + 1;

    /**
     * The token "TRUE".
     */
    public static final int TRUE = SELECT + 1;

    /**
     * The token "UNION".
     */
    public static final int UNION = TRUE + 1;

    /**
     * The token "UNIQUE".
     */
    public static final int UNIQUE = UNION + 1;

    /**
     * The token "WHERE".
     */
    public static final int WHERE = UNIQUE + 1;

    /**
     * The token "WINDOW".
     */
    public static final int WINDOW = WHERE + 1;

    /**
     * The token "WITH".
     */
    public static final int WITH = WINDOW + 1;

    private static final int UPPER_OR_OTHER_LETTER =
            1 << Character.UPPERCASE_LETTER
            | 1 << Character.TITLECASE_LETTER
            | 1 << Character.MODIFIER_LETTER
            | 1 << Character.OTHER_LETTER;

    private static final int UPPER_OR_OTHER_LETTER_OR_DIGIT =
            UPPER_OR_OTHER_LETTER
            | 1 << Character.DECIMAL_DIGIT_NUMBER;

    private ParserUtil() {
        // utility class
    }

    /**
     * Checks if this string is a SQL keyword.
     *
     * @param s the token to check
     * @param ignoreCase true if case should be ignored, false if only upper case
     *            tokens are detected as keywords
     * @return true if it is a keyword
     */
    public static boolean isKeyword(String s, boolean ignoreCase) {
        int length = s.length();
        if (length == 0) {
            return false;
        }
        return getSaveTokenType(s, ignoreCase, 0, length, false) != IDENTIFIER;
    }

    /**
     * Is this a simple identifier (in the JDBC specification sense).
     *
     * @param s identifier to check
     * @return is specified identifier may be used without quotes
     * @throws NullPointerException if s is {@code null}
     */
    public static boolean isSimpleIdentifier(String s) {
        int length = s.length();
        if (length == 0) {
            return false;
        }
        char c = s.charAt(0);
        // lowercase a-z is quoted as well
        if ((UPPER_OR_OTHER_LETTER >>> Character.getType(c) & 1) == 0 && c != '_') {
            return false;
        }
        for (int i = 1; i < length; i++) {
            c = s.charAt(i);
            if ((UPPER_OR_OTHER_LETTER_OR_DIGIT >>> Character.getType(c) & 1) == 0 && c != '_') {
                return false;
            }
        }
        return getSaveTokenType(s, false, 0, length, true) == IDENTIFIER;
    }

    /**
     * Get the token type.
     *
     * @param s the string with token
     * @param ignoreCase true if case should be ignored, false if only upper case
     *            tokens are detected as keywords
     * @param start start index of token
     * @param end index of token, exclusive; must be greater than start index
     * @param additionalKeywords whether TOP, INTERSECTS, and "current data /
     *                           time" functions are keywords
     * @return the token type
     */
    public static int getSaveTokenType(String s, boolean ignoreCase, int start, int end, boolean additionalKeywords) {
        /*
         * JdbcDatabaseMetaData.getSQLKeywords() and tests should be updated when new
         * non-SQL:2003 keywords are introduced here.
         */
        char c = s.charAt(start);
        if (ignoreCase) {
            /*
             * Convert a-z to A-Z. This method is safe, because only A-Z
             * characters are considered below.
             */
            c &= 0xffdf;
        }
        switch (c) {
        case 'A':
            if (eq("ALL", s, ignoreCase, start, end)) {
                return ALL;
            }
            return IDENTIFIER;
        case 'C':
            if (eq("CHECK", s, ignoreCase, start, end)) {
                return CHECK;
            } else if (eq("CONSTRAINT", s, ignoreCase, start, end)) {
                return CONSTRAINT;
            } else if (eq("CROSS", s, ignoreCase, start, end)) {
                return CROSS;
            } else if (eq("CURRENT_DATE", s, ignoreCase, start, end)) {
                return CURRENT_DATE;
            } else if (eq("CURRENT_TIME", s, ignoreCase, start, end)) {
                return CURRENT_TIME;
            } else if (eq("CURRENT_TIMESTAMP", s, ignoreCase, start, end)) {
                return CURRENT_TIMESTAMP;
            }
            return IDENTIFIER;
        case 'D':
            if (eq("DISTINCT", s, ignoreCase, start, end)) {
                return DISTINCT;
            }
            return IDENTIFIER;
        case 'E':
            if (eq("EXCEPT", s, ignoreCase, start, end)) {
                return EXCEPT;
            } else if (eq("EXISTS", s, ignoreCase, start, end)) {
                return EXISTS;
            }
            return IDENTIFIER;
        case 'F':
            if (eq("FETCH", s, ignoreCase, start, end)) {
                return FETCH;
            } else if (eq("FROM", s, ignoreCase, start, end)) {
                return FROM;
            } else if (eq("FOR", s, ignoreCase, start, end)) {
                return FOR;
            } else if (eq("FOREIGN", s, ignoreCase, start, end)) {
                return FOREIGN;
            } else if (eq("FULL", s, ignoreCase, start, end)) {
                return FULL;
            } else if (eq("FALSE", s, ignoreCase, start, end)) {
                return FALSE;
            }
            return IDENTIFIER;
        case 'G':
            if (eq("GROUP", s, ignoreCase, start, end)) {
                return GROUP;
            }
            return IDENTIFIER;
        case 'H':
            if (eq("HAVING", s, ignoreCase, start, end)) {
                return HAVING;
            }
            return IDENTIFIER;
        case 'I':
            if (eq("INNER", s, ignoreCase, start, end)) {
                return INNER;
            } else if (eq("INTERSECT", s, ignoreCase, start, end)) {
                return INTERSECT;
            } else if (eq("IS", s, ignoreCase, start, end)) {
                return IS;
            }
            if (additionalKeywords) {
                if (eq("INTERSECTS", s, ignoreCase, start, end)) {
                    return KEYWORD;
                }
            }
            return IDENTIFIER;
        case 'J':
            if (eq("JOIN", s, ignoreCase, start, end)) {
                return JOIN;
            }
            return IDENTIFIER;
        case 'L':
            if (eq("LIMIT", s, ignoreCase, start, end)) {
                return LIMIT;
            } else if (eq("LIKE", s, ignoreCase, start, end)) {
                return LIKE;
            } else if (eq("LOCALTIME", s, ignoreCase, start, end)) {
                return LOCALTIME;
            } else if (eq("LOCALTIMESTAMP", s, ignoreCase, start, end)) {
                return LOCALTIMESTAMP;
            }
            return IDENTIFIER;
        case 'M':
            if (eq("MINUS", s, ignoreCase, start, end)) {
                return MINUS;
            }
            return IDENTIFIER;
        case 'N':
            if (eq("NOT", s, ignoreCase, start, end)) {
                return NOT;
            } else if (eq("NATURAL", s, ignoreCase, start, end)) {
                return NATURAL;
            } else if (eq("NULL", s, ignoreCase, start, end)) {
                return NULL;
            }
            return IDENTIFIER;
        case 'O':
            if (eq("OFFSET", s, ignoreCase, start, end)) {
                return OFFSET;
            } else if (eq("ON", s, ignoreCase, start, end)) {
                return ON;
            } else if (eq("ORDER", s, ignoreCase, start, end)) {
                return ORDER;
            }
            return IDENTIFIER;
        case 'P':
            if (eq("PRIMARY", s, ignoreCase, start, end)) {
                return PRIMARY;
            }
            return IDENTIFIER;
        case 'R':
            if (eq("ROWNUM", s, ignoreCase, start, end)) {
                return ROWNUM;
            }
            return IDENTIFIER;
        case 'S':
            if (eq("SELECT", s, ignoreCase, start, end)) {
                return SELECT;
            }
            if (additionalKeywords) {
                if (eq("SYSDATE", s, ignoreCase, start, end) || eq("SYSTIME", s, ignoreCase, start, end)
                        || eq("SYSTIMESTAMP", s, ignoreCase, start, end)) {
                    return KEYWORD;
                }
            }
            return IDENTIFIER;
        case 'T':
            if (eq("TRUE", s, ignoreCase, start, end)) {
                return TRUE;
            }
            if (additionalKeywords) {
                if (eq("TODAY", s, ignoreCase, start, end) || eq("TOP", s, ignoreCase, start, end)) {
                    return KEYWORD;
                }
            }
            return IDENTIFIER;
        case 'U':
            if (eq("UNIQUE", s, ignoreCase, start, end)) {
                return UNIQUE;
            } else if (eq("UNION", s, ignoreCase, start, end)) {
                return UNION;
            }
            return IDENTIFIER;
        case 'W':
            if (eq("WHERE", s, ignoreCase, start, end)) {
                return WHERE;
            } else if (eq("WINDOW", s, ignoreCase, start, end)) {
                return WINDOW;
            } else if (eq("WITH", s, ignoreCase, start, end)) {
                return WITH;
            }
            return IDENTIFIER;
        default:
            return IDENTIFIER;
        }
    }

    private static boolean eq(String expected, String s, boolean ignoreCase, int start, int end) {
        int len = expected.length();
        // First letter was already checked
        return end - start == len && expected.regionMatches(ignoreCase, 1, s, start + 1, len - 1);
    }

}
