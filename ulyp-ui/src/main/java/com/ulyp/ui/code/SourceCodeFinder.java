package com.ulyp.ui.code;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class SourceCodeFinder {

    private final List<String> sourcesJar;
    private final List<String> classpath;

    public SourceCodeFinder(List<String> classpath) {

        List<String> sourcesJar = new ArrayList<>();

        for (String jar : classpath) {
            if (jar.contains(".gradle")) {
                Path path = Paths.get(jar);

                Path libFolder = path.getParent().getParent();

                for (File p : libFolder.toFile().listFiles()) {
                    if (p.isDirectory()) {
                        for (File jarFile : p.listFiles()) {
                            if (jarFile.getAbsolutePath().contains("-sources.jar")) {
                                sourcesJar.add(jarFile.getAbsolutePath());
                            }
                        }
                    }
                }
            }
        }

        this.sourcesJar = sourcesJar;
        this.classpath = classpath;
    }

    public SourceCode find(String javaClassName) {
        for (String sourceJar : this.sourcesJar) {
            try {
                ForSourceJarFile forSourceJarFile = new ForSourceJarFile(new JarFile(sourceJar));

                Resolution locate = forSourceJarFile.locate(javaClassName);
                if (locate.isResolved()) {
                    return new SourceCode(javaClassName, new String(locate.resolve()));
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return new SourceCode("", "");
    }

    interface ClassFileLocator {

        Resolution locate(String name) throws IOException;
    }

    interface Resolution {

        boolean isResolved();

        byte[] resolve();

        class Illegal implements Resolution {

            private final String typeName;

            public Illegal(String typeName) {
                this.typeName = typeName;
            }

            public boolean isResolved() {
                return false;
            }

            public byte[] resolve() {
                throw new IllegalStateException("Could not locate class file for " + typeName);
            }
        }

        class Explicit implements Resolution {

            private final byte[] binaryRepresentation;

            public Explicit(byte[] binaryRepresentation) {
                this.binaryRepresentation = binaryRepresentation;
            }

            public boolean isResolved() {
                return true;
            }

            public byte[] resolve() {
                return binaryRepresentation;
            }
        }
    }

    static class ForFolder implements ClassFileLocator {

        private final File folder;

        public ForFolder(File folder) {
            this.folder = folder;
        }

        public Resolution locate(String name) throws IOException {
            File file = new File(folder, name.replace('.', File.separatorChar) + CLASS_FILE_EXTENSION);
            if (file.exists()) {
                InputStream inputStream = new FileInputStream(file);
                try {
                    return new Resolution.Explicit(StreamDrainer.DEFAULT.drain(inputStream));
                } finally {
                    inputStream.close();
                }
            } else {
                return new Resolution.Illegal(name);
            }
        }

        public void close() {
            /* do nothing */
        }
    }

    private static final String CLASS_FILE_EXTENSION = ".class";
    private static final String CLASS_SOURCE_FILE_EXTENSION = ".java";

    static class ForSourceJarFile implements ClassFileLocator {

        private final JarFile jarFile;

        public ForSourceJarFile(JarFile jarFile) {
            this.jarFile = jarFile;
        }

        public Resolution locate(String name) throws IOException {
            ZipEntry zipEntry = jarFile.getEntry(name.replace('.', '/') + CLASS_SOURCE_FILE_EXTENSION);
            if (zipEntry == null) {
                return new Resolution.Illegal(name);
            } else {
                InputStream inputStream = jarFile.getInputStream(zipEntry);
                try {
                    return new Resolution.Explicit(StreamDrainer.DEFAULT.drain(inputStream));
                } finally {
                    inputStream.close();
                }
            }
        }

        public void close() throws IOException {
            jarFile.close();
        }
    }

    static class ForJarFile implements ClassFileLocator {

        private final JarFile jarFile;

        public ForJarFile(JarFile jarFile) {
            this.jarFile = jarFile;
        }

        public Resolution locate(String name) throws IOException {
            ZipEntry zipEntry = jarFile.getEntry(name.replace('.', '/') + CLASS_FILE_EXTENSION);
            if (zipEntry == null) {
                return new Resolution.Illegal(name);
            } else {
                InputStream inputStream = jarFile.getInputStream(zipEntry);
                try {
                    return new Resolution.Explicit(StreamDrainer.DEFAULT.drain(inputStream));
                } finally {
                    inputStream.close();
                }
            }
        }

        public void close() throws IOException {
            jarFile.close();
        }
    }

}
