# raspi-comfort-sensor-iotcore

JavaでCloud IoT Coreにデータ送信するサンプルです。  
Java8とMavenを使用します。

このサンプルは次の公式ソースを参考に作成しました。   
[Cloud IoT Core Java MQTT example](https://github.com/GoogleCloudPlatform/java-docs-samples/tree/master/iot/api-client/manager#cloud-iot-core-java-mqtt-example)

また通信暗号化に使用するキーペアは次を参考に作成しました。  
[Add a public key to the device](https://cloud.google.com/iot/docs/quickstart#add_a_public_key_to_the_device)  
[Cloud IoT Core Java Samples](https://github.com/GoogleCloudPlatform/java-docs-samples/blob/master/iot/api-client/README.md#quickstart)  

## Jarの作成

1. ソースを取得  
1. [ProjectConstants内の定数](https://github.com/tnagao3000/raspi-comfort-sensor-iotcore/blob/master/src/main/java/example/iotcore/ProjectConstants.java)を環境に合わせて修正  
1. `maven package`


## 実行環境の作成

次のファイルを適当な場所に設置します。  

- 作成したJar  
- rsa_private_pkcs8 (秘密鍵)  

鍵は次のコマンドで作成します。
```
openssl req -x509 -newkey rsa:2048 -keyout rsa_private.pem -nodes  -out rsa_cert.pem -subj "/CN=unused"
openssl pkcs8 -topk8 -inform PEM -outform DER -in rsa_private.pem -nocrypt > rsa_private_pkcs8
```
※ 公開鍵はIoTCoreに登録します。  

## 実行

引数でセンサー測定値を指定して実行します。  
成功するとPub/SubのTopicにパブリッシュされます。

```
pi@raspi-is:~/iotcore $ java -jar raspi-comfort-sensor-iotcore-1.0.jar
usage: java -jar raspi-comfort-sensor-iotcore-1.0.jar [--co2 <arg>]
       [--humidity <arg>] [--illuminance <arg>] [--pressure <arg>]
       [--temperature <arg>]
    --co2 <arg>           Measured decimal value.
    --humidity <arg>      Measured decimal value.
    --illuminance <arg>   Measured decimal value.
    --pressure <arg>      Measured decimal value.
    --temperature <arg>   Measured decimal value.
pi@raspi-is:~/iotcore $ 
pi@raspi-is:~/iotcore $ java -jar raspi-comfort-sensor-iotcore-1.0.jar --temperature 23.4 --illuminance 567
payload : '{"illuminance":567.0,"co2":0.0,"temperature":23.4,"humidity":0.0,"time":"2018-01-10 06:01:01Z","pressure":0.0,"device":"raspi-is"}'
Published.
```
