package com.collapsar.springboot;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Set;

/**
 * Created by chenyong6 on 2017/9/30.
 */
public class NIOTest {

    public static void main(String args[]) throws Exception{

        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(false);

        Selector selector = Selector.open();
        channel.register(selector, SelectionKey.OP_CONNECT);

        InetSocketAddress socketAddress = new InetSocketAddress("192.168.56.1", 8888);
        channel.connect(socketAddress);

        while (true){
            selector.select();
            Set<SelectionKey> keySet = selector.selectedKeys();
            if(keySet == null || keySet.isEmpty()){
                System.out.println("selection_key is empty then sleep 1 second and try again.");
                Thread.sleep(1000);
                continue;
            }
            for(SelectionKey key : keySet){
                if(key.isConnectable()){
                    SocketChannel client = (SocketChannel) key.channel();
                    if(client.isConnectionPending()){
                        client.finishConnect();
                        System.out.println("connect successed.");
                        ByteBuffer wBuffer = ByteBuffer.allocate(1024);
                        wBuffer.clear();
                        wBuffer.put((new Date().toLocaleString() + " connected.").getBytes("utf-8"));
                        wBuffer.flip();
                        client.write(wBuffer);
                    }
                    client.register(selector, SelectionKey.OP_READ);
                }else if(key.isReadable()){
                    SocketChannel client = (SocketChannel) key.channel();
                    ByteBuffer rBuffer = ByteBuffer.allocate(1024);
                    rBuffer.clear();
                    int cnt = client.read(rBuffer);
                    if(cnt <= 0){
                        System.out.println("recv: data empty(data_size=0)");
                    }else {
                        String txt = new String(rBuffer.array(), 0, cnt, "utf-8");
                        System.out.println("recv: "+ txt);
                    }
//                    client.register(selector, SelectionKey.OP_READ);
                }
            }
            keySet.clear();
        }

    }
}
