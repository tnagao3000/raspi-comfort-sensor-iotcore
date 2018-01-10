package example.iotcore;

import java.util.HashMap;
import java.util.stream.Stream;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/**
 * 実行時引数をパースしてセンサー値を取得します
 */
public class SensorValues {

    // 取得対象センサー名
    private static final String[] SENSORS = new String[] {"temperature",        // 温度 (℃)
                                                          "pressure",           // 気圧 (hPa)
                                                          "humidity",           // 湿度 (%)
                                                          "illuminance",        // 照度 (lx)
                                                          "co2"                 // co2濃度 (ppm)
                                                         };

    /**
     * 実行時引数をパースしてセンサー値を取得します
     *
     * @param args 実行時引数
     * @return センサー値
     */
    public static HashMap<String, Float> getFromArgs(String[] args) {

        HashMap<String, Float> ret = new HashMap<>();
        Options cliOptions = new Options();

        // -------------------------
        // 引数を設定
        // -------------------------
        Stream.of(SENSORS).forEach(sensor -> {
            cliOptions.addOption(Option.builder().type(Float.class).longOpt(sensor).hasArg()
                    .desc("Measured decimal value.").build());
        });

        try {
            if (args.length == 0)
                throw new IllegalArgumentException();

            // -------------------------
            // 引数を取得
            // -------------------------
            CommandLine commandLine = new DefaultParser().parse(cliOptions, args);

            Stream.of(SENSORS).forEach(sensor -> {

                String value = commandLine.getOptionValue(sensor);
                value = value != null ? value + "f" : "0f";

                ret.put(sensor, Float.valueOf(value));
            });

        } catch (IllegalArgumentException e1) {
            (new HelpFormatter()).printHelp("java -jar raspi-comfort-sensor-iotcore-1.0.jar", cliOptions, true);
            return null;

        } catch (Exception e2) {
            (new HelpFormatter()).printHelp("java -jar raspi-comfort-sensor-iotcore-1.0.jar", cliOptions, true);
            System.err.println(e2.getMessage());
            return null;
        }

        return ret;
    }
}
