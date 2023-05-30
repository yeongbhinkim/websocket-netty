package com.example.websocketnetty.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

//  http://localhost:8080/master
//  http://localhost:8080/guest

/**
 * 반면에 Netty는 TCP 기반의 네트워크 프레임워크로, 네트워크 프로그래밍을 지원합니다.
 * 따라서, Netty를 사용하여 여러 개의 채팅방을 구현하려면, 서버 측에서 해당 기능을 구현해야 합니다.
 * 채팅방을 관리하는 객체를 만들고, 해당 객체가 채팅방의 입장, 퇴장, 메시지 전송 등을 처리하도록 구현하는 것이 일반적입니다.
 */
@Component
@RequiredArgsConstructor
public class NettyChattingClient {
    private Channel channel;
    private EventLoopGroup workerGroup = new NioEventLoopGroup();
    private final NettyChattingClientHandler nettyChattingClientHandler;


    //2. Netty Client 생성
    public void run() {
        //2.1. Netty Server TCP 연결
        SocketAddress address = new InetSocketAddress("127.0.0.1", 8888);

        try {
            Bootstrap bs = new Bootstrap();
            bs.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(nettyChattingClientHandler);
                        }
                    });

            channel = bs.connect(address).sync().channel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop(){
        workerGroup.shutdownGracefully();
    }

    //3. WebSocket을 통해 들어온 채팅 내용을 Netty Client를 통해 Netty Server로 데이터를 요청합니다.
    //(들어온 Channel을 구분하면 원하는 사람에게만 전달할 수도 있습니다.)
    public void sendToServer(String msg){
        ByteBuf messageBuffer = Unpooled.buffer();
        messageBuffer.writeBytes(msg.getBytes());
        channel.writeAndFlush(messageBuffer);
    }
}
