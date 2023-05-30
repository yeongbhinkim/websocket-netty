package com.example.websocketnetty.netty;

import com.example.websocketnetty.websocket.WebSocketSendMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;

/**
 *  WebSocket에서 Netty Client를 참조하면서 동시에 특정 Bean을 하나 더 만드어서 참조하도록 했습니다.
 * 새로 만들어진 Bean에는 WebSocket에서 만들어진 정보와 메시지 발행에 대한 기능을 넘겨줍니다.
 * 그리고 Netty Client는 WebSocket Config Bean을 참조하지 않고 새로 만들어진 Bean을 통해 메시지 발행을 한다면
 * 결과적으로 순환 참조 해결을 할 수 있었습니다.
 * 새로 만들어진 Bean은 어떠한 Bean도 참조하지 않는다는 특징을 이용한 해결 방법입니다.(흥미롭네요)
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class NettyChattingClientHandler extends ChannelInboundHandlerAdapter {

    private final WebSocketSendMessage webSocketSendMessage;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("Netty Client Connected");
    }
    //Netty Client는 WebSocket을 통해 메시지를 발행합니다.
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String readMessage = ((ByteBuf) msg).toString(Charset.defaultCharset());
        log.info("[Receive From Server] ---- {}", readMessage);
        webSocketSendMessage.sendMessage(readMessage);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }
}

