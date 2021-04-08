package kuzaev.webwordstat;

import static kuzaev.webwordstat.HyperTextProcessor.ETX;
import static kuzaev.webwordstat.HyperTextProcessor.STX;

/**
 * A simple DFA for html-tags.<br>
 * This is a minimal reasonable implementation for word extraction.
 */
public enum HyperTextState {

    BEGIN {
        @Override
        public HyperTextState nextState(char c) {
            if (c == STX)
                return BASE;
            else
                return ERROR;
        }
    },

    BASE {
        @Override
        public HyperTextState nextState(char c) {
            if (c == '<')
                return COMMENT_OR_TAG;
            else if (Character.isLetter(c))
                return WORD;
            else if (c != ETX)
                return BASE;
            else
                return END;
        }
    },

    COMMENT_OR_TAG {
        @Override
        public HyperTextState nextState(char c) {
            if (c == 'S')
                return SCRIPT_OR_STYLE;
            else if (isLatinLetter(c))
                return OPEN_TAG;
            else if (c == '/')
                return CLOSE_TAG3;
            else if (c == '!')
                return COMMENT_OR_DOCTYPE;
            else if (Character.isLetter(c))
                return WORD;
            else
                return BASE;
        }
    },

    WORD {
        @Override
        public HyperTextState nextState(char c) {
            if (Character.isLetter(c))
                return WORD;
            else if (c == '<')
                return COMMENT_OR_TAG;
            else
                return BASE;
        }
    },

    SCRIPT_OR_STYLE {
        @Override
        public HyperTextState nextState(char c) {
            if (c == 'C')
                return SCRIPT_OPEN_TAG4;
            else if (c == 'T')
                return STYLE_OPEN_TAG4;
            else if (isLatinLetter(c))
                return OPEN_TAG;
            else
                return BASE;
        }
    },

    SCRIPT_OPEN_TAG4 {
        @Override
        public HyperTextState nextState(char c) {
            if (c == 'R')
                return SCRIPT_OPEN_TAG5;
            else if (isLatinLetter(c))
                return OPEN_TAG;
            else
                return BASE;
        }
    },
    SCRIPT_OPEN_TAG5 {
        @Override
        public HyperTextState nextState(char c) {
            if (c == 'I')
                return SCRIPT_OPEN_TAG6;
            else if (isLatinLetter(c))
                return OPEN_TAG;
            else
                return BASE;
        }
    },
    SCRIPT_OPEN_TAG6 {
        @Override
        public HyperTextState nextState(char c) {
            if (c == 'P')
                return SCRIPT_OPEN_TAG7;
            else if (isLatinLetter(c))
                return OPEN_TAG;
            else
                return BASE;
        }
    },
    SCRIPT_OPEN_TAG7 {
        @Override
        public HyperTextState nextState(char c) {
            if (c == 'T')
                return SCRIPT_OPEN_TAG8;
            else if (isLatinLetter(c))
                return OPEN_TAG;
            else
                return BASE;
        }
    },
    SCRIPT_OPEN_TAG8 {
        @Override
        public HyperTextState nextState(char c) {
            if (c == ' ')
                return SCRIPT_ATTRIBUTES;
            else if (isLatinLetter(c))
                return OPEN_TAG;
            else if (c == '>')
                return SCRIPT_CONTENT;
            else
                return BASE;
        }
    },
    SCRIPT_ATTRIBUTES {
        @Override
        public HyperTextState nextState(char c) {
            if (c == '>')
                return SCRIPT_CONTENT;
            else if (c != ETX)
                return SCRIPT_ATTRIBUTES;
            else
                return ERROR;
        }
    },
    SCRIPT_CONTENT {
        @Override
        public HyperTextState nextState(char c) {
            if (c == '<')
                return SCRIPT_CLOSE_TAG2;
            else if (c != ETX)
                return SCRIPT_CONTENT;
            else
                return ERROR;
        }
    },
    SCRIPT_CLOSE_TAG2 {
        @Override
        public HyperTextState nextState(char c) {
            if (c == '/')
                return SCRIPT_CLOSE_TAG3;
            else if (c != ETX)
                return SCRIPT_CONTENT;
            else
                return ERROR;
        }
    },
    SCRIPT_CLOSE_TAG3 {
        @Override
        public HyperTextState nextState(char c) {
            if (c == 'S')
                return SCRIPT_CLOSE_TAG4;
            else if (c != ETX)
                return SCRIPT_CONTENT;
            else
                return ERROR;
        }
    },
    SCRIPT_CLOSE_TAG4 {
        @Override
        public HyperTextState nextState(char c) {
            if (c == 'C')
                return SCRIPT_CLOSE_TAG5;
            else if (c != ETX)
                return SCRIPT_CONTENT;
            else
                return ERROR;
        }
    },
    SCRIPT_CLOSE_TAG5 {
        @Override
        public HyperTextState nextState(char c) {
            if (c == 'R')
                return SCRIPT_CLOSE_TAG6;
            else if (c != ETX)
                return SCRIPT_CONTENT;
            else
                return ERROR;
        }
    },
    SCRIPT_CLOSE_TAG6 {
        @Override
        public HyperTextState nextState(char c) {
            if (c == 'I')
                return SCRIPT_CLOSE_TAG7;
            else if (c != ETX)
                return SCRIPT_CONTENT;
            else
                return ERROR;
        }
    },
    SCRIPT_CLOSE_TAG7 {
        @Override
        public HyperTextState nextState(char c) {
            if (c == 'P')
                return SCRIPT_CLOSE_TAG8;
            else if (c != ETX)
                return SCRIPT_CONTENT;
            else
                return ERROR;
        }
    },
    SCRIPT_CLOSE_TAG8 {
        @Override
        public HyperTextState nextState(char c) {
            if (c == 'T')
                return SCRIPT_CLOSE_TAG9;
            else if (c != ETX)
                return SCRIPT_CONTENT;
            else
                return ERROR;
        }
    },
    SCRIPT_CLOSE_TAG9 {
        @Override
        public HyperTextState nextState(char c) {
            if (c == '>')
                return BASE;
            else if (c == ' ')
                return SCRIPT_CLOSE_TAG9;
            else
                return ERROR;
        }
    },

    STYLE_OPEN_TAG4 {
        @Override
        public HyperTextState nextState(char c) {
            if (c == 'Y')
                return STYLE_OPEN_TAG5;
            else if (isLatinLetter(c))
                return OPEN_TAG;
            else
                return BASE;
        }
    },
    STYLE_OPEN_TAG5 {
        @Override
        public HyperTextState nextState(char c) {
            if (c == 'L')
                return STYLE_OPEN_TAG6;
            else if (isLatinLetter(c))
                return OPEN_TAG;
            else
                return BASE;
        }
    },
    STYLE_OPEN_TAG6 {
        @Override
        public HyperTextState nextState(char c) {
            if (c == 'E')
                return STYLE_OPEN_TAG7;
            else if (isLatinLetter(c))
                return OPEN_TAG;
            else
                return BASE;
        }
    },
    STYLE_OPEN_TAG7 {
        @Override
        public HyperTextState nextState(char c) {
            if (c == ' ')
                return STYLE_ATTRIBUTES;
            else if (c == '>')
                return STYLE_CONTENT;
            else if (isLatinLetter(c))
                return OPEN_TAG;
            else
                return BASE;
        }
    },
    STYLE_ATTRIBUTES {
        @Override
        public HyperTextState nextState(char c) {
            if (c == '>')
                return STYLE_CONTENT;
            else if (c != ETX)
                return STYLE_ATTRIBUTES;
            else
                return ERROR;
        }
    },
    STYLE_CONTENT {
        @Override
        public HyperTextState nextState(char c) {
            if (c == '<')
                return STYLE_CLOSE_TAG2;
            else if (c != ETX)
                return STYLE_CONTENT;
            else
                return ERROR;
        }
    },
    STYLE_CLOSE_TAG2 {
        @Override
        public HyperTextState nextState(char c) {
            if (c == '/')
                return STYLE_CLOSE_TAG3;
            else if (c != ETX)
                return STYLE_CONTENT;
            else
                return ERROR;
        }
    },
    STYLE_CLOSE_TAG3 {
        @Override
        public HyperTextState nextState(char c) {
            if (c == 'S')
                return STYLE_CLOSE_TAG4;
            else if (c != ETX)
                return STYLE_CONTENT;
            else
                return ERROR;
        }
    },
    STYLE_CLOSE_TAG4 {
        @Override
        public HyperTextState nextState(char c) {
            if (c == 'T')
                return STYLE_CLOSE_TAG5;
            else if (c != ETX)
                return STYLE_CONTENT;
            else
                return ERROR;
        }
    },
    STYLE_CLOSE_TAG5 {
        @Override
        public HyperTextState nextState(char c) {
            if (c == 'Y')
                return STYLE_CLOSE_TAG6;
            else if (c != ETX)
                return STYLE_CONTENT;
            else
                return ERROR;
        }
    },
    STYLE_CLOSE_TAG6 {
        @Override
        public HyperTextState nextState(char c) {
            if (c == 'L')
                return STYLE_CLOSE_TAG7;
            else if (c != ETX)
                return STYLE_CONTENT;
            else
                return ERROR;
        }
    },
    STYLE_CLOSE_TAG7 {
        @Override
        public HyperTextState nextState(char c) {
            if (c == 'E')
                return STYLE_CLOSE_TAG8;
            else if (c != ETX)
                return STYLE_CONTENT;
            else
                return ERROR;
        }
    },
    STYLE_CLOSE_TAG8 {
        @Override
        public HyperTextState nextState(char c) {
            if (c == '>')
                return BASE;
            else if (c == ' ')
                return STYLE_CLOSE_TAG8;
            else
                return ERROR;
        }
    },

    COMMENT_OR_DOCTYPE {
        @Override
        public HyperTextState nextState(char c) {
            if (c == '-')
                return COMMENT_OPEN_TAG4;
            else if (Character.isLetter(c))
                return OPEN_TAG;
            else
                return BASE;
        }
    },

    COMMENT_OPEN_TAG4 {
        @Override
        public HyperTextState nextState(char c) {
            if (c == '-')
                return COMMENT_CONTENT;
            else if (Character.isLetter(c))
                return WORD;
            else
                return BASE;
        }
    },
    COMMENT_CONTENT {
        @Override
        public HyperTextState nextState(char c) {
            if (c == '-')
                return COMMENT_CLOSE_TAG1;
            else
                return COMMENT_CONTENT;
        }
    },
    COMMENT_CLOSE_TAG1 {
        @Override
        public HyperTextState nextState(char c) {
            if (c == '-')
                return COMMENT_CLOSE_TAG2;
            else
                return COMMENT_CONTENT;
        }
    },
    COMMENT_CLOSE_TAG2 {
        @Override
        public HyperTextState nextState(char c) {
            if (c == '>')
                return BASE;
            else
                return COMMENT_CONTENT;
        }
    },

    OPEN_TAG {
        @Override
        public HyperTextState nextState(char c) {
            if (isLatinLetter(c) || isDigit(c))
                return OPEN_TAG;
            else if (c == '>')
                return BASE;
            else
                return OPEN_TAG_ATTRIBUTES;
        }
    },
    OPEN_TAG_ATTRIBUTES {
        @Override
        public HyperTextState nextState(char c) {
            if (c == '>')
                return BASE;
            else if (c != ETX)
                return OPEN_TAG_ATTRIBUTES;
            else
                return ERROR;
        }
    },

    CLOSE_TAG3 {
        @Override
        public HyperTextState nextState(char c) {
            if (isLatinLetter(c))
                return CLOSE_TAG4;
            else
                return BASE;
        }
    },

    CLOSE_TAG4 {
        @Override
        public HyperTextState nextState(char c) {
            if (isLatinLetter(c) || isDigit(c))
                return CLOSE_TAG4;
            else if (c == '>')
                return BASE;
            else if (c == ' ')
                return CLOSE_TAG_REST;
            else
                return ERROR;
        }
    },

    CLOSE_TAG_REST {
        @Override
        public HyperTextState nextState(char c) {
            if (isSpace(c))
                return CLOSE_TAG_REST;
            else if (c == '>')
                return BASE;
            else
                return ERROR;
        }
    },

    END {
        @Override
        public HyperTextState nextState(char c) {
            return this;
        }
    },

    ERROR {
        @Override
        public HyperTextState nextState(char c) {
            return this;
        }
    }
    ;

    abstract public HyperTextState nextState(char c);

    public static boolean isLatinLetter(char c) {
        return 'a' <= c && c <= 'z' || 'A' <= c && c <= 'Z';
    }

    public static boolean isDigit(char c) {
        return '0' <= c && c <= '9';
    }

    public static boolean isSpace(char c) {
        return c == ' ' || c == '\t' || c == '\n' || c == '\r';
    }

}
