package discord;

import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.channel.MessageChannel;

import java.io.IOException;

public class DiscordBot {

    String discordBotToken;
    DiscordClient discordClient;

    public DiscordBot() {
        this.discordBotToken = System.getenv("discordBotToken");
        this.discordClient = DiscordClient.create(this.discordBotToken);
    }

    // Schedule this function to run every 10 seconds
    public void sendMessageToChannel(String message, String channelID) throws IOException {

        GatewayDiscordClient gateway = this.discordClient.login().block();

        // Retrieve the MessageChannel object for the specified channel ID
        MessageChannel channel = gateway.getChannelById(Snowflake.of(channelID))
                .cast(MessageChannel.class)
                .block();

        // Check if the channel exists and is a text channel
        if (channel != null) {
            // Send a message to the channel
            channel.createMessage(message).block();
        } else {
            System.out.println("Channel not found or is not a text channel.");
        }
        // Log out and disconnect from Discord
        gateway.logout().block();

    }
}