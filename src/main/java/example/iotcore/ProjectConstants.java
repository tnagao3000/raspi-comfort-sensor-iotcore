package example.iotcore;

/**
 * プロジェクト固有の設定値
 */
public class ProjectConstants {

	public static final String HOSTNAME = "mqtt.googleapis.com";
	public static final short PORT = 8883;

	// これ以降は、IoTCoreの設定値に合わせて修正します
	public static final String PROJECT = "[プロジェクトID]";
	public static final String REGION = "[IoTCoreのリージョン]"; 		// ex) us-central1
	public static final String REGISTRY = "[端末レジストリ]";
	public static final String KEYFILE = "rsa_private_pkcs8"; 		// 秘密鍵
}
