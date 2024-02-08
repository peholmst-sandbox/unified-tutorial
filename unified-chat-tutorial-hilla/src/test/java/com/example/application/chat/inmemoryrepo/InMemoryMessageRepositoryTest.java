package com.example.application.chat.inmemoryrepo;

import com.example.application.chat.service.Message;
import com.example.application.chat.service.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class InMemoryMessageRepositoryTest {

    private static final String CHANNEL1 = "channel1";
    private static final String CHANNEL2 = "channel2";
    private static final Instant TIMESTAMP1 = Instant.ofEpochMilli(1707380158462L);
    private static final Instant TIMESTAMP2 = TIMESTAMP1.plusSeconds(10);
    private static final Instant TIMESTAMP3 = TIMESTAMP2.plusSeconds(10);
    private MessageRepository repo;

    @BeforeEach
    void setup() {
        repo = new InMemoryMessageRepository();
    }

    @Test
    void repository_generates_unique_ids() {
        var id1 = repo.generateId(CHANNEL1);
        var id2 = repo.generateId(CHANNEL1);
        var id3 = repo.generateId(CHANNEL2);
        var id4 = repo.generateId(CHANNEL2);

        assertThat(List.of(id1, id2, id3, id4)).doesNotHaveDuplicates();
    }

    @Test
    void repository_is_empty_at_first() {
        assertThat(repo.findLatest(CHANNEL1, 10)).isEmpty();
        assertThat(repo.findLatest(CHANNEL2, 10)).isEmpty();
    }

    @Test
    void repository_can_save_and_retrieve_messages() {
        var message1 = new Message(repo.generateId(CHANNEL1), CHANNEL1, TIMESTAMP1, "user1", "message1");
        var message2 = new Message(repo.generateId(CHANNEL1), CHANNEL1, TIMESTAMP2, "user2", "message2");
        var message3 = new Message(repo.generateId(CHANNEL1), CHANNEL2, TIMESTAMP3, "user3", "message3");

        repo.save(message1);
        repo.save(message2);
        repo.save(message3);

        assertThat(repo.findLatest(CHANNEL1, 10)).containsExactly(message1, message2);
        assertThat(repo.findLatest(CHANNEL2, 10)).containsExactly(message3);
    }

    @Test
    void messages_are_sorted_by_timestamp() {
        var message1 = new Message(repo.generateId(CHANNEL1), CHANNEL1, TIMESTAMP1, "user1", "message1");
        var message2 = new Message(repo.generateId(CHANNEL1), CHANNEL1, TIMESTAMP2, "user2", "message2");
        var message3 = new Message(repo.generateId(CHANNEL1), CHANNEL1, TIMESTAMP3, "user3", "message3");

        repo.save(message2);
        repo.save(message3);
        repo.save(message1);

        assertThat(repo.findLatest(CHANNEL1, 2)).containsExactly(message2, message3);
    }
}
