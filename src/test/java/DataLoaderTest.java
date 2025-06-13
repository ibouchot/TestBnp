import com.example.demo.dto.EventDto;
import com.example.demo.dto.TransactionDto;
import com.example.demo.services.load.DataLoader;
import com.example.demo.services.reader.JsonDataReader;
import com.example.demo.services.transaction.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataLoaderTest {

    @InjectMocks
    private DataLoader dataLoader;

    @Mock
    private JsonDataReader dataReader;

    @Mock
    private TransactionService transactionService;

    @Test
    void testDetermineCorrect_ValidAndMalformedDates() {
        assertTrue(dataLoader.determineCorrect("2025-06-10T12:00:00"));
        assertTrue(dataLoader.determineCorrect("12/31/2025 13:19:45"));
        assertFalse(dataLoader.determineCorrect("2025--9-83T12:00:00"));
        assertFalse(dataLoader.determineCorrect("2025-09-08T24:20"));
        assertFalse(dataLoader.determineCorrect("bad-format-date"));
    }

    @Test
    void testFixMalformedDate() {
        String malformed = "2025-15-32T25:00:00";
        String fixed = dataLoader.fixMalformedDate(malformed);
        assertDoesNotThrow(() -> java.time.LocalDateTime.parse(fixed));
        assertEquals("2026-04-02T01:00:00", fixed);
    }

    @Test
    void testDetermineEventRankOrdering() {

        EventDto eventC = new EventDto();
        eventC.setEventType("CBAcquired");
        EventDto eventB = new EventDto();
        eventB.setEventType("step2");
        EventDto eventA = new EventDto();
        eventA.setEventType("step3");

        // Build C --> B --> A
        TransactionDto transactionC = new TransactionDto(1,"C", "Z", eventC, "2025-06-01T13:00:00", "", true,"");
        TransactionDto transactionB = new TransactionDto(2,"B", "C", eventB, "2025-06-01T12:00:00", "", true,"");
        TransactionDto transactionA = new TransactionDto(3,"A", "B", eventA, "2025-06-01T11:00:00", "", true,"");

        List<TransactionDto> transactions = List.of(transactionA, transactionB, transactionC);
        Map<String, List<TransactionDto>> result = dataLoader.determineEventRank(transactions);
        System.out.println(result);
        assertEquals(1, result.size());
        List<TransactionDto> chain = result.values().iterator().next();
        assertEquals(List.of(transactionC, transactionB, transactionA), chain);
    }
}
