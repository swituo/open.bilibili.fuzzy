package open.bilibili.fuzzy;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class PathAction {

    private File inputDir;
    private File outputDir;
    private ICipherAction cipher;
    private boolean fuzzyPath;
    private final String separator = java.io.File.separator;
    private final Charset charset = Charset.defaultCharset();
    private List<String> excludeList = new ArrayList<>();
    private Map<String, String> fuzzyCache = new HashMap<>();

    public PathAction(File inputDir, File outputDir, ICipherAction cipher, boolean fuzzyPath) {
        this.inputDir = inputDir;
        this.outputDir = outputDir;
        this.cipher = cipher;
        this.fuzzyPath = fuzzyPath;
    }

    public PathAction(File inputDir, File outputDir, ICipherAction cipher) {
        this(inputDir, outputDir, cipher, false);
    }

    public void setExcludeList(List<String> excludeList) {
        this.excludeList = excludeList;
    }

    private String cipherFileName(String name) throws Exception {
        return new String(cipher.cipher(name.getBytes()), charset);
    }

    private File getOutputFile(File file) throws Exception {
        Path outputRelativePath = Paths.get(file.getAbsolutePath().replace(inputDir.getAbsolutePath(), ""));
        StringBuilder outputRelativeStr = new StringBuilder();
        for (int i=0; i<outputRelativePath.getNameCount(); i++) {
            String partPath = outputRelativePath.getName(i).toString();
            if (!fuzzyCache.containsKey(partPath)) {
                if (fuzzyPath) {
                    fuzzyCache.put(partPath, cipherFileName(partPath));
                } else {
                    fuzzyCache.put(partPath, partPath);
                }
            }
            outputRelativeStr.append(separator).append(fuzzyCache.get(partPath));
        }
        return new File(outputDir.getAbsolutePath() + separator + outputRelativeStr.toString());
    }

    public void dirAct(File dir) throws Exception {
        for (File file : dir.listFiles()) {
            if (excludeList.indexOf(file.getName()) > -1) {
                continue;
            }
            if (file.isFile()) {
                byte[] inputData = Files.readAllBytes(file.toPath());
                byte[] outputData = cipher.cipher(inputData);
                Files.write(getOutputFile(file).toPath(), outputData);
            } else if (file.isDirectory()) {
                getOutputFile(file).mkdir();
                dirAct(file);
            }
        }
    }

    public void doAction() throws Exception {
        dirAct(inputDir);
    }
}
