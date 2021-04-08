package kuzaev.webwordstat;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Processes a html-site read from a {@code Reader reader} by sequential parts (buffer).
 * A portions of chars of maximum size {@code DEFAULT_BUFFER_SIZE} will be sequentially loaded into {@code htmlBuffer}
 * from the source {@code Reader reader}, which will be then sent to Stream&lt;Character&gt; and transformed to Stream&lt;String&gt; by doing a lexical
 * analysis of html-tags by a simple DFA implemented in a class {@link HyperTextState}.
 */
public class HyperTextProcessor {

    public static final int DEFAULT_BUFFER_SIZE = 65535;

    /**
     * We use three "metasymbols":<br>
     * STX - denotes a begin mark of the text (will be used in generating a char stream as a start symbol)<br>
     * ETX - denotes an end mark of the text (will be used in streams by word extraction)<br>
     * EOT - denotes an end of the char stream (will be used in generating a char stream as a terminating symbol)
     */
    public static final char STX = 2; //ASCII code - start of text
    public static final char ETX = 3; //ASCII code - end of text
    public static final char EOT = 4; //ASCII code - end of transmission

    private final char[] htmlBuffer;
    private int index = 0; //current index in the html buffer
    private int bufferContentLength = 0; //current length of content in the buffer

    private final List<Runnable> bufferBeginListeners = new ArrayList<>();
    private final List<Runnable> bufferEndListeners = new ArrayList<>();

    private final Reader reader;

    public HyperTextProcessor(Reader reader) {
        this(reader, DEFAULT_BUFFER_SIZE);
    }

    private HyperTextProcessor(Reader reader, int bufferSize) {
        this.reader = reader;
        htmlBuffer = new char[bufferSize];
    }

    private void runListeners(List<Runnable> listeners) {
        for (Runnable listener : listeners) {
            listener.run();
        }
    }

    public void addBufferBeginListener(Runnable listener) {
        bufferBeginListeners.add(listener);
    }

    public void addBufferEndListener(Runnable listener) {
        bufferEndListeners.add(listener);
    }

    private void loadBuffer() {
        try {
            while ((bufferContentLength = reader.read(htmlBuffer)) == 0)
                ;
            index = 0;
            if (bufferContentLength > 0) {
                runListeners(bufferBeginListeners);
            }
//            System.out.println("\tloadBuffer(): bufferContentLength = " + bufferContentLength);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private char nextChar(char pc) {
        char c = peekChar(pc);
        if (c != ETX) { //switch to the next available char
            index++;
            if (index == bufferContentLength) {
                runListeners(bufferEndListeners); //call listeners for the previous buffer
                loadBuffer();
            }
        }
        return c;
    }

    private char peekChar(char pc) {
        /* take actions on begin and end of the char stream */
        switch (pc) {
            case STX:
                loadBuffer();
                break;
            case ETX:
                return EOT;
        }
        /*  if a valid char buffer is present, take a char from it, otherwise close the char sequence */
        return bufferContentLength > 0 ? htmlBuffer[index] : ETX;
    }

    public Stream<String> getWordsStream() {
        final Stream<Character> charsStream = Stream.iterate(STX, (c) -> c != EOT, this::nextChar);

        class CharWrapper {
            char c;
            HyperTextState state;
            StringBuilder sb;
            public CharWrapper set(char c, HyperTextState state, StringBuilder sb) {
                this.c = c;
                this.state = state;
                this.sb = sb;
                return this;
            }
            @Override
            public String toString() { //for debug purpose only
                return "CharWrapper: c = '" + c + "'(" + (int)c + "), state = " + state + ", sb = \"" + sb + "\"";
            }
        }

        class CharWrapperFactory implements Function<Character, CharWrapper> {
            HyperTextState prevState = null;
            HyperTextState currState = HyperTextState.BEGIN;
            final StringBuilder sb = new StringBuilder(SqlManager.WORD_MAX_LENGTH); //one instance for all words in order to save memory
            final CharWrapper cw = new CharWrapper(); //one instance for all chars in order to save memory
            @Override
            public CharWrapper apply(Character c) {
                prevState = currState;
                currState = currState.nextState(c);
                //Ideally ERROR state should not appear in a valid html code, but if it does then the HyperTextState class is to improve
                if (currState == HyperTextState.ERROR) {
                    System.err.println("Invalid html syntax: c = '" + c + "'(" + (int)c + "), state = " + currState + ", sb = " + sb + ".");
                }
                //catch a word's begin
                if (prevState != HyperTextState.WORD && currState == HyperTextState.WORD) {
                    this.sb.setLength(0); //clear the string builder for a new word
                }
                //catch a single letter in the word
                if (currState == HyperTextState.WORD) {
                    this.sb.append(c);
                }
                StringBuilder sb = null;
                //catch a word's end
                //the whole word will be associated with a char next AFTER the word!
                //(or with ETX symbol if there are no more chars in a stream)
                if (prevState == HyperTextState.WORD && currState != HyperTextState.WORD) {
                    sb = this.sb;
                }
                return cw.set(c, currState, sb);
            }
        }

        Stream<String> wordsStream = charsStream.sequential()
                                                .map(Character::toUpperCase)
                                                .map(new CharWrapperFactory())
//                                                .peek(System.out::println) //uncomment for debug
                                                .filter(cw -> cw.sb != null)
                                                .map(cw -> cw.sb.toString());
        return wordsStream;
    }

}
