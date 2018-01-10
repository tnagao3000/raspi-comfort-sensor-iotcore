package example.iotcore;

import static example.iotcore.ProjectConstants.HOSTNAME;
import static example.iotcore.ProjectConstants.KEYFILE;
import static example.iotcore.ProjectConstants.PORT;
import static example.iotcore.ProjectConstants.PROJECT;
import static example.iotcore.ProjectConstants.REGION;
import static example.iotcore.ProjectConstants.REGISTRY;

import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.joda.time.DateTime;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * エントリポイント
 * {@link #main(String[])}
 */
public class Publish {

    private static final DateTimeFormatter BQ_TIME_FORMAT = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss");

    /**
     * センサー値を受け取り、Google IoTCoreに送信します。
     *
     * IotCoreとの通信に使用するため、次のファイルをカレントに設置しておいてください。
     * {@link ProjectConstants#KEYFILE}
     *
     * @param args センサー値たち
     * @throws MqttException
     */
    public static void main(String[] args) throws MqttException {

        // 引数からセンサー値を取得
        HashMap<String, Float> sensorValues = SensorValues.getFromArgs(args);

        if (sensorValues == null)
            System.exit(1);

        MqttClient client = null;

        try {
            // ホスト名を取得 （この値はIoTCoreの端末IDと一致させておく)
            final String device = InetAddress.getLocalHost().getHostName();

            // --------------------------------------------------
            // MQTT接続
            // --------------------------------------------------
            final String mqttServer = String.format("ssl://%s:%s", HOSTNAME, PORT);

            final String mqttClientId = String.format("projects/%s/locations/%s/registries/%s/devices/%s", PROJECT,
                    REGION, REGISTRY, device);

            MqttConnectOptions connectOptions = new MqttConnectOptions();
            connectOptions.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1_1);
            connectOptions.setUserName("unused");
            connectOptions.setPassword(createJwtRsa(PROJECT, KEYFILE).toCharArray());

            client = new MqttClient(mqttServer, mqttClientId, new MemoryPersistence());
            client.connect(connectOptions);

            // --------------------------------------------------
            // payloadを作成
            //
            // 次の項目を送る
            // (1) 計測日時(UTC)
            // (2) ホスト名(端末ID)
            // (3) センサー値たち
            // --------------------------------------------------
            HashMap<String, Object> payloadMap = new HashMap<>();

            // BigQueryは日時をUTCで保持するのが作法ぽい。
            payloadMap.put("time", LocalDateTime.now(ZoneOffset.UTC).format(BQ_TIME_FORMAT) + "Z");     // (1)
            payloadMap.put("device", device);                                                           // (2)
            payloadMap.putAll(sensorValues);                                                            // (3)

            // payloadはBigQueryにそのままinsert出来るようjsonにしておきます。
            String payload = (new ObjectMapper()).writeValueAsString(payloadMap);
            System.out.format("payload : '%s'\n", payload);

            // --------------------------------------------------
            // payloadを送信
            // --------------------------------------------------
            MqttMessage message = new MqttMessage(payload.toString().getBytes());
            message.setQos(1);

            client.publish(String.format("/devices/%s/events", device), message);
            System.out.println("Published.");

        } catch (Exception e) {
            System.out.println("Failed. : " + e.getMessage());

        } finally {
            if (client != null)
                client.disconnect();
        }
    }

    private static String createJwtRsa(String projectId, String privateKeyFile) throws Exception {

        DateTime now = new DateTime();
        JwtBuilder jwtBuilder = Jwts.builder().setIssuedAt(now.toDate()).setExpiration(now.plusMinutes(20).toDate())
                .setAudience(projectId);

        byte[] keyBytes = Files.readAllBytes(Paths.get(privateKeyFile));
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");

        return jwtBuilder.signWith(SignatureAlgorithm.RS256, kf.generatePrivate(spec)).compact();
    }
}
