package org.EricRamirezS.jdacommando.command;

import net.dv8tion.jda.api.Permission;

import java.util.HashMap;
import java.util.Map;

public interface PermissionsName {

    Map<Permission, String> NAME = new HashMap<Permission, String>() {{
        put(Permission.CREATE_INSTANT_INVITE, "Crear invitación");
        put(Permission.KICK_MEMBERS, "Expulsar miembros");
        put(Permission.BAN_MEMBERS, "Banear miembros");
        put(Permission.ADMINISTRATOR, "Administrador");
        put(Permission.MANAGE_CHANNEL, "Gestionar canales");
        put(Permission.MANAGE_SERVER, "Gestionar servidor");
        put(Permission.MESSAGE_ADD_REACTION, "Añadir reacciones");
        put(Permission.VIEW_AUDIT_LOGS, "Ver registro de auditoría");
        put(Permission.PRIORITY_SPEAKER, "Prioridad de palabra");
        put(Permission.VIEW_GUILD_INSIGHTS, "Ver información del servidor");
        put(Permission.VIEW_CHANNEL, "Ver canales");
        put(Permission.MESSAGE_READ, "Ver canal");
        put(Permission.MESSAGE_WRITE, "Enviar mensajes");
        put(Permission.MESSAGE_TTS, "Enviar mensajes de texto a voz");
        put(Permission.MESSAGE_MANAGE, "Gestionar mensajes");
        put(Permission.MESSAGE_EMBED_LINKS, "Insertar enlaces");
        put(Permission.MESSAGE_ATTACH_FILES, "Adjuntar archivos");
        put(Permission.MESSAGE_HISTORY, "Leer el historial de mensajes");
        put(Permission.MESSAGE_MENTION_EVERYONE, "Mencionar @everyone, @here y todos los roles");
        put(Permission.MESSAGE_EXT_EMOJI, "Usar emojis externos");
        put(Permission.USE_SLASH_COMMANDS, "Use Application Commands");
        put(Permission.MANAGE_THREADS, "Gestionar hilos");
        put(Permission.USE_PUBLIC_THREADS, "Public Threads");
        put(Permission.USE_PRIVATE_THREADS, "Private Threads");
        put(Permission.VOICE_STREAM, "Video");
        put(Permission.VOICE_CONNECT, "Conectar");
        put(Permission.VOICE_SPEAK, "Hablar");
        put(Permission.VOICE_MUTE_OTHERS, "Silenciar miembros");
        put(Permission.VOICE_DEAF_OTHERS, "Ensordecer miembros");
        put(Permission.VOICE_MOVE_OTHERS, "Mover miembros");
        put(Permission.VOICE_USE_VAD, "Usar Actividad de voz");
        put(Permission.NICKNAME_CHANGE, "Cambiar apodo");
        put(Permission.NICKNAME_MANAGE, "Gestionar apodo");
        put(Permission.MANAGE_ROLES, "Gestionar roles");
        put(Permission.MANAGE_PERMISSIONS, "Gestionar permisos");
        put(Permission.MANAGE_WEBHOOKS, "Gestionar webhooks");
        put(Permission.MANAGE_EMOTES, "Gestionar emojis y pegatinas");
        put(Permission.REQUEST_TO_SPEAK, "Solicitar hablar");
        put(Permission.UNKNOWN, "Desconocido");
    }};
}
