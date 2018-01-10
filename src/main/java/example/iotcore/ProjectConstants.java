package example.iotcore;

/**
 * プロジェクト固有の設定値
 */
public class ProjectConstants {

	public static final String HOSTNAME = "mqtt.googleapis.com";
	public static final short PORT = 8883;

	// これ以降は、IoTCoreの設定値に合わせて修正します
	public static final String PROJECT = "raspi-comfort-sensor"; 					// プロジェクトID
	public static final String REGION = "us-central1"; 								// IoTCoreのリージョン
	public static final String REGISTRY = "raspi-comfort-sensor-iotcore-registry"; 	// 端末レジストリ
	public static final String KEYFILE = "rsa_private_pkcs8"; 						// 秘密鍵
}
