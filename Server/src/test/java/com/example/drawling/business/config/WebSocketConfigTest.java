package com.example.drawling.business.config;

import com.example.drawling.config.WebSocketConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.StompWebSocketEndpointRegistration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebSocketConfigTest {

    @InjectMocks
    private WebSocketConfig webSocketConfig;

    @Mock
    private MessageBrokerRegistry messageBrokerRegistry;

    @Mock
    private StompEndpointRegistry stompEndpointRegistry;

    @Mock
    private StompWebSocketEndpointRegistration stompWebSocketEndpointRegistration;

    @BeforeEach
    void setUp() {
        // mock
    }

    @Test
    void testConfigureMessageBroker() {
        // Act
        webSocketConfig.configureMessageBroker(messageBrokerRegistry);

        // Assert
        verify(messageBrokerRegistry, times(1)).enableSimpleBroker("/topic", "/queue", "/user");
        verify(messageBrokerRegistry, times(1)).setApplicationDestinationPrefixes("/app");
        verify(messageBrokerRegistry, times(1)).setUserDestinationPrefix("/user");
    }

    @Test
    void testRegisterStompEndpoints() {
        // Arrange
        when(stompEndpointRegistry.addEndpoint("/ws")).thenReturn(stompWebSocketEndpointRegistration);
        when(stompWebSocketEndpointRegistration.setAllowedOrigins(
                (new String[]{"http://localhost:4173", "http://localhost:5173"})))
                .thenReturn(stompWebSocketEndpointRegistration);

        // Act
        webSocketConfig.registerStompEndpoints(stompEndpointRegistry);

        // Assert
        verify(stompEndpointRegistry, times(1)).addEndpoint("/ws");
        verify(stompWebSocketEndpointRegistration, times(1))
                .setAllowedOrigins("http://localhost:4173", "http://localhost:5173");
    }

    @Test
    void testCorsConfigurer() {
        // Act
        WebMvcConfigurer configurer = webSocketConfig.corsConfigurer();

        // Assert
        assertNotNull(configurer, "WebMvcConfigurer bean should not be null.");
    }
}
