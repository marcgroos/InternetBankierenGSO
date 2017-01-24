//package io;
//
//import calculate.Edge;
//import calculate.KochManager;
//import calculate.SavableEdges;
//import calculate.SerializableEdge;
//import io.transaction.PersistencyConstants;
//import io.transaction.PersistencyMediator;
//
//import java.io.*;
//import java.nio.MappedByteBuffer;
//import java.nio.channels.FileChannel;
//import java.bank.exceptions.ArrayList;
//import java.bank.exceptions.List;
//
///**
// * Created by guill on 27-12-2016.
// */
//public class MappedMediator implements PersistencyMediator {
//
//    private static final String FILE = PersistencyConstants.FILE_PATH + PersistencyConstants.FILE_NAME + ".txt";
//    private static final String TEMP_FILE = PersistencyConstants.FILE_PATH + PersistencyConstants.FILE_NAME + "temp.txt";
//
//    // Level 9 fits in this size. Level 10 is too big with a size of 1GB
//    private static final int NUMBER_OF_BYTES =50*1024*1024;
//
//    @Override
//    public KochManager load() {
//
//        // Initialize a Kochmanager and SavableEdges
//        KochManager km = new KochManager();
//        SavableEdges savableEdges = null;
//
//        // Create a byteArray with a size of 10MB
//        byte[] serializedEdges = new byte[NUMBER_OF_BYTES];
//
//        try{
//            // Retrieve bytes from file
//            File file = new File(FILE);
//            MappedByteBuffer in = new RandomAccessFile(file, "rw")
//                    .getChannel().map(FileChannel.MapMode.READ_WRITE, 0, NUMBER_OF_BYTES);
//            in.get(serializedEdges);
//
//            // Convert bytes into object
//            ByteArrayInputStream bis = new ByteArrayInputStream(serializedEdges);
//            ObjectInput input = new ObjectInputStream(bis);
//            savableEdges = (SavableEdges) input.readObject();
//
//            // Adding everything into KochManager
//            km.setLevel(savableEdges.getLevel());
//            List<Edge> edges = new ArrayList<>();
//            for(SerializableEdge se : savableEdges.getEdges()){
//                edges.add(new Edge(se));
//            }
//            km.getEdges().addAll(edges);
//
//            input.close();
//            bis.close();
//            System.gc();
//
//            // Return KochManager
//            return km;
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//
//        return null;
//    }
//
//    @Override
//    public boolean save(KochManager kochManager) {
//
//        // Convert edges into serializable object
//        List<SerializableEdge> edges = new ArrayList<>();
//        for(Edge e : kochManager.getEdges()){
//            edges.add(new SerializableEdge(e));
//        }
//        SavableEdges savableEdges = new SavableEdges(edges, kochManager.getLevel(), kochManager.getEdges().size());
//
//        try {
//            // Convert object into bytes
//            ByteArrayOutputStream bos = new ByteArrayOutputStream();
//            ObjectOutput out = new ObjectOutputStream(bos);
//            try{
//                out.writeObject(savableEdges);
//                byte[] yourBytes = bos.toByteArray();
//
//                // Creating file and write it
//                File file = new File(FILE);
//                if(file.exists()){
//                    file.delete();
//                }
//
//                //File tempfile = new File(TEMP_FILE);
//                MappedByteBuffer output = new RandomAccessFile(file, "rw")
//                        .getChannel().map(FileChannel.MapMode.READ_WRITE, 0, NUMBER_OF_BYTES);
//                output.put(yourBytes);
//
//                output.clear();
//                out.close();
//
//            } finally {
//                // Close byte converter
//                try {
//                    bos.close();
//                }catch (IOException ex){}
//            }
//            return true;
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//            return false;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
//}
