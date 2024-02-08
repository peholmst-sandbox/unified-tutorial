package com.example.application.chat.inmemory;

import com.example.application.chat.Channel;
import com.example.application.chat.ChannelRepository;
import com.example.application.chat.Message;
import com.example.application.chat.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class InMemoryChannelRepositoryTest {

    private ChannelRepository repo;
    private MessageRepository messageRepoMock;

    @BeforeEach
    public void setUp() {
        messageRepoMock = Mockito.mock(MessageRepository.class);
        repo = new InMemoryChannelRepository(messageRepoMock);
    }

    @Test
    void repository_generates_unique_ids() {
        var id1 = repo.generateId();
        var id2 = repo.generateId();
        var id3 = repo.generateId();
        var id4 = repo.generateId();

        assertThat(List.of(id1, id2, id3, id4)).doesNotHaveDuplicates();
    }

    @Test
    void repository_is_empty_at_first() {
        assertThat(repo.findAll()).isEmpty();
        assertThat(repo.exists(repo.generateId())).isFalse();
        assertThat(repo.findById(repo.generateId())).isEmpty();
    }

    @Test
    void repository_can_save_and_retrieve_channels() {
        var channel1 = new Channel(repo.generateId(), "channel1");
        var channel2 = new Channel(repo.generateId(), "channel2");

        repo.save(channel1);
        repo.save(channel2);

        assertThat(repo.findAll()).containsExactly(channel1, channel2);
        assertThat(repo.exists(channel1.id())).isTrue();
        assertThat(repo.exists(channel2.id())).isTrue();
        assertThat(repo.findById(channel1.id())).contains(channel1);
        assertThat(repo.findById(channel2.id())).contains(channel2);
    }

    @Test
    void channels_are_sorted_by_name() {
        var channel1 = new Channel(repo.generateId(), "channel1");
        var channel2 = new Channel(repo.generateId(), "channel2");

        repo.save(channel2);
        repo.save(channel1);

        assertThat(repo.findAll()).containsExactly(channel1, channel2);
    }

    @Test
    void latest_message_is_included_when_retrieving_channels() {
        var channel1 = new Channel(repo.generateId(), "channel1", null);
        var message = new Message("messageId", channel1.id(), Instant.now(), "user", "message");
        repo.save(channel1);
        when(messageRepoMock.findLatest(channel1.id(), 1)).thenReturn(List.of(message));
        assertThat(repo.findById(channel1.id())).contains(new Channel(channel1.id(), channel1.name(), message));
    }
}
