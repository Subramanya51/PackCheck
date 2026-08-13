package com.hotel.packcheck.mqtt;
import com.hotel.packcheck.dto.CartWebSocketStatus;
import com.hotel.packcheck.enums.CartMode;
import tools.jackson.databind.ObjectMapper;
import com.hotel.packcheck.dto.CartStatusMessage;
import com.hotel.packcheck.service.FloorConfigurationService;
import com.hotel.packcheck.config.MqttConfig;
import jakarta.annotation.PostConstruct;
import com.hotel.packcheck.service.CartWebSocketService;
import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.springframework.stereotype.Service;
import com.hotel.packcheck.service.CartFloorStateService;
import java.nio.charset.StandardCharsets;
import com.hotel.packcheck.repository.CartRepository;
import com.hotel.packcheck.entity.Cart;
@Service
public class MqttService implements MqttCallback {

    private final MqttConfig mqttConfig;
    private final CartRepository cartRepository;

    private MqttClient mqttClient;
    private final FloorConfigurationService floorConfigurationService;
    private final ObjectMapper objectMapper;
    private final CartWebSocketService cartWebSocketService;
    private final CartFloorStateService cartFloorStateService;
    public MqttService(
            MqttConfig mqttConfig,
            FloorConfigurationService floorConfigurationService,
            ObjectMapper objectMapper,
            CartWebSocketService cartWebSocketService,
            CartFloorStateService cartFloorStateService, CartRepository cartRepository) {

        this.mqttConfig = mqttConfig;
        this.floorConfigurationService = floorConfigurationService;
        this.objectMapper = objectMapper;
        this.cartWebSocketService = cartWebSocketService;
        this.cartFloorStateService = cartFloorStateService;
        this.cartRepository = cartRepository;
    }

    @PostConstruct
    public void connect() {

        try {

            String serverUri =
                    "ssl://" +
                            mqttConfig.getBroker() +
                            ":" +
                            mqttConfig.getPort();

            mqttClient = new MqttClient(
                    serverUri,
                    mqttConfig.getClientId()
            );

            mqttClient.setCallback(this);

            MqttConnectionOptions options =
                    new MqttConnectionOptions();

            options.setUserName(
                    mqttConfig.getUsername()
            );

            options.setPassword(
                    mqttConfig.getPassword()
                            .getBytes(StandardCharsets.UTF_8)
            );

            options.setAutomaticReconnect(true);
            options.setCleanStart(true);

            mqttClient.connect(options);

            System.out.println(
                    "Connected to HiveMQ successfully."
            );

            subscribeToTopics();

        } catch (Exception e) {

            System.err.println(
                    "MQTT initialization failed: " +
                            e.getMessage()
            );

            throw new IllegalStateException(
                    "Unable to initialize MQTT client",
                    e
            );
        }
    }

    private void subscribeToTopics() throws MqttException {

        mqttClient.subscribe(
                mqttConfig.getTopics().getStatus(),
                1
        );

        mqttClient.subscribe(
                mqttConfig.getTopics().getTask(),
                1
        );

        mqttClient.subscribe(
                mqttConfig.getTopics().getFloor(),
                1
        );

        mqttClient.subscribe(
                mqttConfig.getTopics().getMode(),
                1
        );

        System.out.println(
                "Subscribed to PackCheck MQTT topics."
        );
    }


    @Override
    public void messageArrived(
            String topic,
            MqttMessage message) {

        String payload =
                new String(
                        message.getPayload(),
                        StandardCharsets.UTF_8
                );

        System.out.println("MQTT MESSAGE RECEIVED");
        System.out.println("Topic: " + topic);
        System.out.println("Payload: " + payload);

        if (!topic.matches(
                "packcheck/v1/cart/[^/]+/status")) {

            return;
        }

        try {

            String[] topicParts = topic.split("/");

            String cartId = topicParts[3];
            Cart cart = cartRepository.findByCartId(cartId)
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Cart not found: " + cartId
                            ));

            Long hotelId = cart.getHotel().getHotelId();

            CartStatusMessage status =
                    objectMapper.readValue(
                            payload,
                            CartStatusMessage.class
                    );

            Integer floor =
                    floorConfigurationService
                            .getFloorForBssid(
                                    hotelId,
                                    status.getBssid()
                            );
            if (cartFloorStateService.hasFloorChanged(
                    cartId,
                    floor)) {

                publishFloorUpdate(
                        cartId,
                        floor
                );
            }

            System.out.println(
                    "Cart ID: " + cartId
            );

            System.out.println(
                    "Resolved Floor: " + floor
            );

            System.out.println(
                    "Task Status: "
                            + status.getTaskStatus()
            );

            System.out.println(
                    "Occupancy: "
                            + status.getOccupancy()
            );

            System.out.println(
                    "Motion: "
                            + status.getMotion()
            );

            System.out.println(
                    "Battery: "
                            + status.getBattery()
            );
            CartWebSocketStatus webSocketStatus =
                    new CartWebSocketStatus();

            webSocketStatus.setCartId(cartId);
            webSocketStatus.setFloor(floor);
            webSocketStatus.setTaskStatus(
                    status.getTaskStatus()
            );
            webSocketStatus.setOccupancy(
                    status.getOccupancy()
            );
            webSocketStatus.setMotion(
                    status.getMotion()
            );
            webSocketStatus.setBattery(
                    status.getBattery()
            );

            cartWebSocketService.publishCartStatus(
                    webSocketStatus
            );

        } catch (Exception e) {

            System.err.println(
                    "Failed to process cart status: "
                            + e.getMessage()
            );
        }
    }

    @Override
    public void disconnected(
            MqttDisconnectResponse disconnectResponse) {

        System.out.println(
                "MQTT disconnected."
        );
    }

    @Override
    public void mqttErrorOccurred(
            MqttException exception) {

        System.err.println(
                "MQTT error: " +
                        exception.getMessage()
        );
    }

    @Override
    public void deliveryComplete(
            IMqttToken token) {

        // No outgoing messages yet.
    }

    @Override
    public void connectComplete(
            boolean reconnect,
            String serverURI) {

        if (reconnect) {

            System.out.println(
                    "MQTT reconnected."
            );

        } else {

            System.out.println(
                    "MQTT connection established."
            );
        }
    }

    @Override
    public void authPacketArrived(
            int reasonCode,
            MqttProperties properties) {

        // Authentication packet handling
        // is not required for the current implementation.
    }
    public void publishTaskRequest(
            String cartId,
            int requestedFloor,
            String requestedRoom) {

        String topic =
                "packcheck/v1/cart/" + cartId + "/task";

        String payload = """
            {
                "requestedFloor": %d,
                "requestedRoom": "%s"
            }
            """.formatted(
                requestedFloor,
                requestedRoom
        );

        try {

            MqttMessage message =
                    new MqttMessage(
                            payload.getBytes(StandardCharsets.UTF_8)
                    );

            message.setQos(1);

            mqttClient.publish(
                    topic,
                    message
            );

            System.out.println(
                    "TASK REQUEST PUBLISHED"
            );

            System.out.println(
                    "Topic: " + topic
            );

            System.out.println(
                    "Payload: " + payload
            );

        } catch (MqttException e) {

            System.err.println(
                    "Failed to publish task request: "
                            + e.getMessage()
            );

            throw new IllegalStateException(
                    "Unable to send task request to cart",
                    e
            );
        }
    }
    public void publishFloorUpdate(
            String cartId,
            int floor) {

        String topic =
                "packcheck/v1/cart/" + cartId + "/floor";

        String payload =
                """
                {
                    "floor": %d
                }
                """.formatted(floor);

        try {

            MqttMessage message =
                    new MqttMessage(
                            payload.getBytes(
                                    StandardCharsets.UTF_8
                            )
                    );

            message.setQos(1);

            mqttClient.publish(
                    topic,
                    message
            );

            System.out.println(
                    "FLOOR UPDATE PUBLISHED"
            );

            System.out.println(
                    "Topic: " + topic
            );

            System.out.println(
                    "Payload: " + payload
            );

        } catch (MqttException e) {

            System.err.println(
                    "Failed to publish floor update: "
                            + e.getMessage()
            );

            throw new IllegalStateException(
                    "Unable to send floor update to cart",
                    e
            );
        }
    }
    public void publishModeUpdate(
            String cartId,
            CartMode mode) {

        String topic =
                "packcheck/v1/cart/" + cartId + "/mode";

        String payload =
                """
                {
                    "mode": "%s"
                }
                """.formatted(mode.name());

        try {

            MqttMessage message =
                    new MqttMessage(
                            payload.getBytes(
                                    StandardCharsets.UTF_8
                            )
                    );

            message.setQos(1);

            mqttClient.publish(
                    topic,
                    message
            );

            System.out.println(
                    "MODE UPDATE PUBLISHED"
            );

            System.out.println(
                    "Topic: " + topic
            );

            System.out.println(
                    "Payload: " + payload
            );

        } catch (MqttException e) {

            System.err.println(
                    "Failed to publish mode update: "
                            + e.getMessage()
            );

            throw new IllegalStateException(
                    "Unable to send mode update to cart",
                    e
            );
        }
    }
}