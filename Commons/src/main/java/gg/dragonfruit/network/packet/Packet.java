package gg.dragonfruit.network.packet;

import gg.dragonfruit.network.connection.Connection;
import io.netty.buffer.ByteBuf;

public class Packet {

    public static interface OUTGOING {
        public void serialize(ByteBuf os);

        public int getId();
    }

    public static interface INCOMING {
        public void received(Connection connection);

        public void deserialize(ByteBuf is);
    }
}
