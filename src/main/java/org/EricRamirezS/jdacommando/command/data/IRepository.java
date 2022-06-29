package org.EricRamirezS.jdacommando.command.data;

public interface IRepository {

    String getPrefix(String guildId);

    String setPrefix(String guildId, String prefix);

    String getLanguage(String guildId);

    String setLanguage(String guildId, String locale);

}
