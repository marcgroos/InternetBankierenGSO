//package io;
//
//import calculate.Edge;
//import calculate.KochManager;
//import calculate.SerializableEdge;
//import io.transaction.PersistencyConstants;
//import io.transaction.PersistencyMediator;
//
//import java.io.*;
//import java.nio.MappedByteBuffer;
//import java.nio.channels.FileChannel;
//import java.nio.channels.FileLock;
//import java.bank.exceptions.ArrayList;
//import java.bank.exceptions.List;
//import java.bank.exceptions.stream.Collectors;
//
///**
// * Created by Marc
// * <p>
// * SerializedEdges file layout:
// * <p>
// * LEVEL            - 4 bytes
// * TOTAL EDGES      - 4 bytes
// * WRITTEN EDGES    - 4 bytes
// * <p>
// * For each edge:
// * EDGE LENGTH IN BYTES - 4 bytes
// * EDGE DATA            - variable
// */
//public class LockingMediator implements PersistencyMediator {
//
//    private static final String FILE = PersistencyConstants.FILE_PATH + PersistencyConstants.FILE_NAME + ".bin";
//
//    private static final int MEMORY_BYTES = 1024; // 1 KB
//
//    @Override
//    public KochManager load() {
//
//        // Initialize a Kochmanager and SavableEdges
//        KochManager km = new KochManager();
//        List<Edge> edges = new ArrayList<>();
//
//        int bufferLimit = MEMORY_BYTES;
//        int byteCount = 12;
//        int totalEdges;
//        int writtenEdges;
//        int currentEdge;
//
//        RandomAccessFile randomAccessFile = null;
//        FileChannel fileChannel = null;
//        MappedByteBuffer statusBuffer = null;
//        MappedByteBuffer readBuffer = null;
//
//        try {
//            randomAccessFile = new RandomAccessFile(FILE, "rw");
//            fileChannel = randomAccessFile.getChannel();
//            statusBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 8, 4); // The written bytes status is between 8 and 12.
//            readBuffer = randomAccessFile.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, MEMORY_BYTES);
//
//            km.setLevel(readBuffer.getInt());
//            totalEdges = readBuffer.getInt();
//
//            readBuffer.position(12);
//
//            // Keep looping until all edges are read.
//            while ((currentEdge = edges.size()) < totalEdges) {
//
//                // Update written edges status
//                statusBuffer.position(0);
//                writtenEdges = statusBuffer.getInt();
//
//                // Load a new edge if possible.
//                if (currentEdge < writtenEdges) {
//
//                    int edgeByteSize = readBuffer.getInt();
//                    byte[] edgeBytes = new byte[edgeByteSize];
//
//                    // Check if the next edge fits within the buffer, otherwise create a new buffer.
//                    if (byteCount + edgeByteSize + 4 >= bufferLimit) {
//
//                        // Clear previous buffer
//                        readBuffer.clear();
//
//                        // Create a new read buffer.
//                        readBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, byteCount, MEMORY_BYTES);
//                        readBuffer.position(4);
//
//                        // Next buffer limit.
//                        bufferLimit = byteCount + MEMORY_BYTES;
//                    }
//
//                    // Lock
//                    FileLock fileLock = fileChannel.lock(byteCount, edgeByteSize, false); // 4 is the edgeByteSize value difference
//
//                    // Retrieve the edge bytes from the file.
//                    readBuffer.get(edgeBytes);
//
//                    // Convert the edge bytes to an edge object and add to the list.
//                    edges.add(bytesToEdge(edgeBytes));
//
//                    // Update byteCount
//                    byteCount += edgeByteSize + 4;
//
//                    // Release
//                    fileLock.release();
//
//                    System.out.println("Loaded edge: " + edges.size());
//                }
//            }
//
//            // Add edges to koch manager
//            km.getEdges().addAll(edges);
//
//            // Return KochManager
//            return km;
//
//        } catch (IOException | ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//
//        return null;
//    }
//
//    @Override
//    public boolean save(KochManager kochManager) {
//
//        int byteCount = 12; // The edge data starts at 12.
//        int bufferLimit = MEMORY_BYTES;
//
//        FileLock fileLock = null;
//        MappedByteBuffer statusBuffer = null;
//        MappedByteBuffer writeBuffer = null;
//
//        // Convert edges into serializable object
//        List<SerializableEdge> edges = kochManager.getEdges().stream().map(SerializableEdge::new).collect(Collectors.toList());
//
//        try {
//
//            emptyFileIfExists();
//
//            // Put bytes into file
//            RandomAccessFile raf = new RandomAccessFile(FILE, "rw");
//            FileChannel ch = raf.getChannel();
//
//            // Get the edge status buffer
//            statusBuffer = ch.map(FileChannel.MapMode.READ_WRITE, 8, 4); // The written bytes status is between 8 and 12.
//
//            // Init the file and get the buffer
//            writeBuffer = writeFileInfo(kochManager, ch);
//
//            // Write each edge seperately with a lock
//            for (SerializableEdge edge : edges) {
//
////                System.out.println("Current byte: " + byteCount);
////                System.out.println("Writing edge: " + edge.toString());
//
//                // Write the current edge
//                byte[] edgeBytes = edgeToBytes(edge);
//
//                // Get a new buffer when the byteCount becomes higher than the available memory.
//                if (byteCount + edgeBytes.length >= bufferLimit) {
//
//                    // Create mapped byte buffer to write the bytes to.
//                    writeBuffer = ch.map(FileChannel.MapMode.READ_WRITE, byteCount, MEMORY_BYTES);
//
//                    // Next buffer limit
//                    bufferLimit = byteCount + MEMORY_BYTES;
//                }
//
//                // Lock the file
//                fileLock = ch.lock(byteCount, MEMORY_BYTES, false);
//
//                // Write to buffer
//                writeBuffer.putInt(edgeBytes.length);
//                writeBuffer.put(edgeBytes);
//
//                // Update byteCount
//                byteCount += edgeBytes.length + 4;
//                System.out.println(byteCount);
//
//                // Release lock
//                fileLock.release();
//
//                // Update written edges status
//                statusBuffer.position(0);
//                statusBuffer.putInt(edges.indexOf(edge) + 1);
//
//                try {
//                    Thread.sleep(100);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
//
//    private void emptyFileIfExists() throws IOException {
//        File file = new File(FILE);
//
//        if (file.exists()) {
//            file.delete();
//            file.createNewFile();
//        }
//
//    }
//
//    private MappedByteBuffer writeFileInfo(KochManager km, FileChannel ch) throws IOException {
//
//        // Create mapped byte buffer to write the bytes to.
//        MappedByteBuffer buffer = ch.map(FileChannel.MapMode.READ_WRITE, 0, MEMORY_BYTES);
//        FileLock fileLock = ch.lock(0, 12, false);
//
//        // Write the level
//        buffer.putInt(km.getLevel());
//
//        // Write total edges
//        buffer.putInt(km.getEdges().size());
//
//        // Write written edges
//        buffer.putInt(0);
//
//        fileLock.release();
//        return buffer;
//    }
//
//    private byte[] edgeToBytes(SerializableEdge edge) throws IOException {
//
//        // Convert object into bytes
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        ObjectOutput out = new ObjectOutputStream(bos);
//
//        // Write the serialized object bytes to buffer.
//        out.writeObject(edge);
//        return bos.toByteArray();
//    }
//
//    private Edge bytesToEdge(byte[] edgeBytes) throws IOException, ClassNotFoundException {
//
//        // Convert bytes into object
//        ByteArrayInputStream bis = new ByteArrayInputStream(edgeBytes);
//        ObjectInput input = new ObjectInputStream(bis);
//        SerializableEdge edge = (SerializableEdge) input.readObject();
//
//        return new Edge(edge);
//    }
//}
