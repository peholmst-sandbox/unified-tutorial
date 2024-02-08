package com.example.application.chat;

import com.example.application.security.Roles;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.test.context.support.WithMockUser;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings("ReactiveStreamsUnusedPublisher")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class ChatServiceTest {

    private String knownChannelId;

    @Autowired
    ChatService chatService;

    @Autowired
    ChannelRepository channelRepository;

    @PostConstruct
    public void setUpAll() {
        knownChannelId = channelRepository.generateId();
        channelRepository.save(new Channel(knownChannelId, "General"));
    }

    @Test
    public void all_methods_require_authentication() {
        assertThatThrownBy(() -> chatService.channels()).isInstanceOf(AuthenticationException.class);
        assertThatThrownBy(() -> chatService.createChannel("won't work")).isInstanceOf(AuthenticationException.class);
        assertThatThrownBy(() -> chatService.channel("won't work")).isInstanceOf(AuthenticationException.class);
        assertThatThrownBy(() -> chatService.liveMessages("won't work")).isInstanceOf(AuthenticationException.class);
        assertThatThrownBy(() -> chatService.messageHistory("won't work", 1)).isInstanceOf(AuthenticationException.class);
        assertThatThrownBy(() -> chatService.postMessage("won't work", "will never get published")).isInstanceOf(AuthenticationException.class);
    }

    @Test
    @WithMockUser(roles = {})
    public void all_methods_require_a_role() {
        assertThatThrownBy(() -> chatService.channels()).isInstanceOf(AccessDeniedException.class);
        assertThatThrownBy(() -> chatService.createChannel("won't work")).isInstanceOf(AccessDeniedException.class);
        assertThatThrownBy(() -> chatService.channel("won't work")).isInstanceOf(AccessDeniedException.class);
        assertThatThrownBy(() -> chatService.liveMessages("won't work")).isInstanceOf(AccessDeniedException.class);
        assertThatThrownBy(() -> chatService.messageHistory("won't work", 1)).isInstanceOf(AccessDeniedException.class);
        assertThatThrownBy(() -> chatService.postMessage("won't work", "will never get published")).isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithMockUser(roles = Roles.USER)
    public void users_can_retrieve_channels() {
        assertThat(chatService.channels()).isNotEmpty();
        assertThat(chatService.channel(knownChannelId)).isPresent();
    }

    @Test
    @WithMockUser(roles = {Roles.USER, Roles.ADMIN})
    public void admins_can_create_channels() {
        var channel = chatService.createChannel("My channel");
        assertThat(chatService.channel(channel.id())).contains(channel);
        assertThat(chatService.channels()).contains(channel);
    }

    @Test
    @WithMockUser(username = "joecool", roles = Roles.USER)
    public void users_can_post_and_receive_messages() {
        var liveMessages = chatService.liveMessages(knownChannelId);
        var verifier = StepVerifier
                .create(liveMessages)
                .expectNextMatches(messages -> {
                    if (messages.isEmpty()) {
                        return false;
                    }
                    var message = messages.getFirst();
                    return message.channelId().equals(knownChannelId)
                            && message.author().equals("joecool")
                            && message.message().equals("Hello, world!");
                })
                .thenCancel()
                .verifyLater();
        chatService.postMessage(knownChannelId, "Hello, world!");
        verifier.verify();
    }

    @Test
    @WithMockUser(roles = Roles.USER)
    public void simultaneously_posted_messages_are_grouped_together_to_save_bandwidth() {
        var liveMessages = chatService.liveMessages(knownChannelId);
        var verifier = StepVerifier
                .create(liveMessages)
                .expectNextMatches(messages -> messages.size() == 2)
                .thenCancel()
                .verifyLater();
        chatService.postMessage(knownChannelId, "message1");
        chatService.postMessage(knownChannelId, "message2");
        verifier.verify();
    }

    @Test
    @WithMockUser(roles = Roles.USER)
    public void users_can_fetch_message_history() {
        chatService.postMessage(knownChannelId, "message1");
        chatService.postMessage(knownChannelId, "message2");
        chatService.postMessage(knownChannelId, "message3");
        chatService.postMessage(knownChannelId, "message4");
        chatService.postMessage(knownChannelId, "message5");
        chatService.postMessage(knownChannelId, "message6");
        assertThat(chatService.messageHistory(knownChannelId, 5)).satisfies(messages -> {
            assertThat(messages).hasSize(5);
            assertThat(messages.getFirst().message()).isEqualTo("message2");
            assertThat(messages.getLast().message()).isEqualTo("message6");
        });
    }

    @Test
    @WithMockUser(roles = Roles.USER)
    public void posting_to_nonexistent_channel_throws_exception() {
        assertThatThrownBy(() -> chatService.postMessage("nonexistent", "will never get published")).isInstanceOf(InvalidChannelException.class);
    }

    @Test
    @WithMockUser(roles = Roles.USER)
    public void listening_to_nonexistent_channel_throws_exception() {
        assertThatThrownBy(() -> chatService.liveMessages("nonexistent")).isInstanceOf(InvalidChannelException.class);
    }

    @Test
    @WithMockUser(roles = Roles.USER)
    public void fetching_message_history_of_nonexistent_channel_returns_empty_list() {
        assertThat(chatService.messageHistory("nonexistent", 1)).isEmpty();
    }
}
