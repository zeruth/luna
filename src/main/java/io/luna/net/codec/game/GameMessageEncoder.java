package io.luna.net.codec.game;

import io.luna.net.codec.IsaacCipher;
import io.luna.net.codec.MessageType;
import io.luna.net.msg.GameMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import rs.net.msg.out.game.ServerGameMessage;

/**
 * A {@link MessageToByteEncoder} implementation that encodes game messages.
 *
 * @author lare96
 */
@ChannelHandler.Sharable
public final class GameMessageEncoder extends MessageToByteEncoder<Object> {

    /**
     * The encryptor.
     */
    private final IsaacCipher encryptor;

    /**
     * Creates a new {@link GameMessageEncoder}.
     *
     * @param encryptor The encryptor.
     */
    public GameMessageEncoder(IsaacCipher encryptor) {
        this.encryptor = encryptor;
    }

    @Override
    public void encode(ChannelHandlerContext ctx, Object o, ByteBuf out) throws Exception {
        if (o instanceof ServerGameMessage gameMsg) {
            ServerGameMessage.Companion.write(gameMsg, out);
            return;
        }
        GameMessage msg = (GameMessage) o;
        try {
            out.writeByte(msg.getOpcode() + encryptor.nextInt());
            if (msg.getType() == MessageType.VAR) {
                out.writeByte(msg.getSize());
            } else if (msg.getType() == MessageType.VAR_SHORT) {
                out.writeShort(msg.getSize());
            }
            out.writeBytes(msg.getPayload().getBuffer());
        } finally {
            msg.getPayload().releaseAll();
        }
    }
}
