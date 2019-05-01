package open.bilibili.fuzzy;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Properties;

public class Decoder {

    public static void main(String[] args) throws Exception {

        System.out.println(Welcomes.speechText);
        Properties properties = System.getProperties();
        String fuzzyPath = properties.getProperty("fuzzy.path", "");

        if (args.length < 3) {
            System.err.println("[密钥] [输入目录] [输出目录]");
            System.exit(1);
        }

        String key = args[0];
        Path inputDir = Paths.get(args[1]);
        Path outputDir = Paths.get(args[2]);

        if (Files.exists(inputDir) && Files.isDirectory(inputDir) && Files.notExists(outputDir)) {
            if (!outputDir.toFile().mkdirs()) {
                throw new Exception("创建[输出目录]失败！");
            }
            ICipherAction fileCipher = new ICipherAction() {
                DESUtil desUtil = new DESUtil(key);
                @Override
                public byte[] cipher(byte[] data) throws Exception {
                    return desUtil.decrypt(StringUtil.hexToByte(new String(data, "UTF-8")));
                }
            };
            PathAction pathAction = new PathAction(inputDir.toFile(), outputDir.toFile(), fileCipher, !fuzzyPath.isEmpty());
            pathAction.setExcludeList(Arrays.asList(".git", "README.md"));
            pathAction.doAction();

        } else {
            System.err.println("[输入目录]必须存在，且[输出目录]必须不存在！");
        }

    }

}
