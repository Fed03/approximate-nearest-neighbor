package corpus_texmex_reader;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

abstract class ByteFileReader<T> implements AutoCloseable {
    private static final int STEP_SIZE = 4;
    private static final int BUFFER_SIZE = 1048576;
    int numberOfFields;
    private InputStream inputStream;

    ByteFileReader(String filename) {
        try {
            inputStream = new BufferedInputStream(new FileInputStream(filename), BUFFER_SIZE);
            inputStream.mark(2);
            numberOfFields = inputStream.read();
            inputStream.reset();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
    }

    int getNumberOfFields() {
        return numberOfFields;
    }

    byte[] nextRawLine() throws IOException {
        byte[] components = new byte[numberOfFields * STEP_SIZE];
        inputStream.skip(STEP_SIZE);
        if (inputStream.read(components) == -1) {
            throw new IOException("End of stream");
        }
        return components;
    }

    ByteBuffer createByteBuffer(byte[] components, int index) {
        return ByteBuffer.wrap(components, index * STEP_SIZE, STEP_SIZE).order(ByteOrder.LITTLE_ENDIAN);
    }

    protected abstract T getFieldValueByIndex(byte[] components, int index);

    private static String getFile(String filename) {
        return ClassLoader.getSystemClassLoader().getResource(filename).getFile();
    }
}
