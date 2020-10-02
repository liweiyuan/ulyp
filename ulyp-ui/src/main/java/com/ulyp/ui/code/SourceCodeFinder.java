package com.ulyp.ui.code;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SourceCodeFinder {

    private final List<com.ulyp.ui.code.JarFile> jars;

    public SourceCodeFinder(List<String> classpath) {

        this.jars = classpath.stream()
                .filter(x -> new File(x).exists())
                .filter(x -> !new File(x).isDirectory())
                .map(x -> new com.ulyp.ui.code.JarFile(Paths.get(x).toFile()))
                .collect(Collectors.toList());

        List<com.ulyp.ui.code.JarFile> sourcesJars = new ArrayList<>();

        for (com.ulyp.ui.code.JarFile jarFile : this.jars) {
            com.ulyp.ui.code.JarFile sourcesJar = jarFile.deriveSourcesJar();
            if (sourcesJar != null) {
                sourcesJars.add(sourcesJar);
            }
        }

        this.jars.addAll(0, sourcesJars);
    }

    public SourceCode find(String javaClassName) {
        Optional<SourceCode> decompiled = Optional.empty();

        for (com.ulyp.ui.code.JarFile jar : this.jars) {
            SourceCode sourceCode = jar.findSourceByClassName(javaClassName);

            if (sourceCode != null) {
                return sourceCode;
            }

            ByteCode byteCode = jar.findByteCodeByClassName(javaClassName);

            if (byteCode != null) {
                decompiled = Optional.of(byteCode.decompile());
            }
        }

        return decompiled.orElse(new SourceCode("", ""));
    }
//    static class ForFolder implements ClassFileLocator {
//
//        private final File folder;
//
//        public ForFolder(File folder) {
//            this.folder = folder;
//        }
//
//        public Resolution locate(String name) throws IOException {
//            File file = new File(folder, name.replace('.', File.separatorChar) + CLASS_FILE_EXTENSION);
//            if (file.exists()) {
//                InputStream inputStream = new FileInputStream(file);
//                try {
//                    return new Resolution.Explicit(StreamDrainer.DEFAULT.drain(inputStream));
//                } finally {
//                    inputStream.close();
//                }
//            } else {
//                return new Resolution.Illegal(name);
//            }
//        }
//
//        public void close() {
//            /* do nothing */
//        }
//    }
}
