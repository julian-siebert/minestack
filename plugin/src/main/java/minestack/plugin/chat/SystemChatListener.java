package minestack.plugin.chat;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSystemChatMessage;
import org.jspecify.annotations.NonNull;

public final class SystemChatListener implements PacketListener {

    @Override
    public void onPacketSend(@NonNull PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.SYSTEM_CHAT_MESSAGE) {
            var packet = new WrapperPlayServerSystemChatMessage(event);

        }
    }
}
