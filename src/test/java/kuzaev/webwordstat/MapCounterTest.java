package kuzaev.webwordstat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MapCounterTest {

    private final static String MOSCOW = "Moscow";
    private final static String KAZAN = "Kazan";
    private final static String OMSK = "Omsk";
    private final static String SAMARA = "Samara";
    private final static String NOVOSIBIRSK = "Novosibirsk";
    private final static String TULA = "Tula";
    private final static String VOLGOGRAD = "Volgograd";
    private final static String BOSTON = "Boston";
    private final static String WASHINGTON = "Washington";
    private final static String BERLIN = "Berlin";
    private final static String PARIS = "Paris";
    private final static String TOKIO = "Tokio";
    private final static String ROME = "Rome";
    private final static String FRANKFURT = "Frankfurt";

    private MapCounter<String> mapCounter;

    @BeforeEach
    void setUp() {
        mapCounter = new MapCounter<>();
        mapCounter.put(MOSCOW);
        mapCounter.put(KAZAN);
        mapCounter.put(OMSK);
        mapCounter.put(SAMARA);
        mapCounter.put(SAMARA);
        mapCounter.put(MOSCOW);
        mapCounter.put(MOSCOW);
        mapCounter.put(MOSCOW);
        mapCounter.put(NOVOSIBIRSK);
        mapCounter.put(TULA, 999L);
    }

    @Test
    void put1() {
        mapCounter.put(VOLGOGRAD);
        assertEquals(1, mapCounter.get(VOLGOGRAD));
        assertEquals(4, mapCounter.get(MOSCOW));
        assertEquals(1, mapCounter.get(KAZAN));
        assertEquals(1, mapCounter.get(OMSK));
        assertEquals(2, mapCounter.get(SAMARA));
        assertEquals(999, mapCounter.get(TULA));
        assertNull(mapCounter.get(BOSTON));
        assertNull(mapCounter.get(WASHINGTON));
    }

    @Test
    void put2() {
        mapCounter.put(BERLIN, Long.MAX_VALUE - 4L);
        mapCounter.put(BERLIN);
        mapCounter.put(BERLIN);
        mapCounter.put(BERLIN);
        mapCounter.put(BERLIN);
        assertEquals(Long.MAX_VALUE, mapCounter.get(BERLIN));
        assertThrows(IllegalStateException.class, () -> mapCounter.put(BERLIN));
        mapCounter.put(PARIS, Long.MAX_VALUE);
        assertThrows(IllegalStateException.class, () -> mapCounter.put(PARIS));
        mapCounter.put(TOKIO, 0L);
        assertEquals(0, mapCounter.get(TOKIO));
        mapCounter.put(TOKIO);
        assertEquals(1, mapCounter.get(TOKIO));
        assertThrows(IllegalArgumentException.class, () -> mapCounter.put(ROME, -1L));
        assertThrows(IllegalArgumentException.class, () -> mapCounter.put(FRANKFURT, Long.MIN_VALUE));
    }

}