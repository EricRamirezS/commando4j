/*
 *
 *    Copyright 2022 Eric Bastian Ramírez Santis
 *
 *    Licensed under the Apache License,
 Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing,
 software
 *    distributed under the License is distributed on an "AS IS" BASIS,

 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.ericramirezs.commando4j.command.command;

import net.dv8tion.jda.api.Permission;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Map;

/**
 * Map with discord permissions and key for localized name.
 */
public interface PermissionsName {

    /**
     * Map with discord permissions and key for localized name.
     */
    @UnmodifiableView
    Map<Permission, String> NAME = Map.ofEntries(
            Map.entry(Permission.MANAGE_CHANNEL, "Permission_ManageChannel"),
            Map.entry(Permission.MANAGE_SERVER, "Permission_ManageServer"),
            Map.entry(Permission.VIEW_AUDIT_LOGS, "Permission_ViewAuditLogs"),
            Map.entry(Permission.VIEW_CHANNEL, "Permission_ViewChannel"),
            Map.entry(Permission.VIEW_GUILD_INSIGHTS, "Permission_ViewGuildInsights"),
            Map.entry(Permission.MANAGE_ROLES, "Permission_ManageRoles"),
            Map.entry(Permission.MANAGE_PERMISSIONS, "Permission_ManagePermissions"),
            Map.entry(Permission.MANAGE_WEBHOOKS, "Permission_ManageWebhooks"),
            Map.entry(Permission.MANAGE_EMOJIS_AND_STICKERS, "Permission_ManageEmojisAndStickers"),
            Map.entry(Permission.CREATE_INSTANT_INVITE, "Permission_CreateInstantInvite"),
            Map.entry(Permission.KICK_MEMBERS, "Permission_KickMembers"),
            Map.entry(Permission.BAN_MEMBERS, "Permission_BanMembers"),
            Map.entry(Permission.NICKNAME_CHANGE, "Permission_NicknameChange"),
            Map.entry(Permission.NICKNAME_MANAGE, "Permission_NicknameManage"),
            Map.entry(Permission.MODERATE_MEMBERS, "Permission_ModerateMembers"),
            Map.entry(Permission.MESSAGE_ADD_REACTION, "Permission_MessageAddReaction"),
            Map.entry(Permission.MESSAGE_SEND, "Permission_MessageSend"),
            Map.entry(Permission.MESSAGE_TTS, "Permission_MessageTts"),
            Map.entry(Permission.MESSAGE_MANAGE, "Permission_MessageManage"),
            Map.entry(Permission.MESSAGE_EMBED_LINKS, "Permission_MessageEmbedLinks"),
            Map.entry(Permission.MESSAGE_ATTACH_FILES, "Permission_MessageAttachFiles"),
            Map.entry(Permission.MESSAGE_HISTORY, "Permission_MessageHistory"),
            Map.entry(Permission.MESSAGE_MENTION_EVERYONE, "Permission_MessageMentionEveryone"),
            Map.entry(Permission.MESSAGE_EXT_EMOJI, "Permission_MessageExtEmoji"),
            Map.entry(Permission.USE_APPLICATION_COMMANDS, "Permission_UseApplicationCommands"),
            Map.entry(Permission.MESSAGE_EXT_STICKER, "Permission_MessageExtSticker"),
            Map.entry(Permission.MANAGE_THREADS, "Permission_ManageThreads"),
            Map.entry(Permission.CREATE_PUBLIC_THREADS, "Permission_CreatePublicThreads"),
            Map.entry(Permission.CREATE_PRIVATE_THREADS, "Permission_CreatePrivateThreads"),
            Map.entry(Permission.MESSAGE_SEND_IN_THREADS, "Permission_MessageSendInThreads"),
            Map.entry(Permission.PRIORITY_SPEAKER, "Permission_PrioritySpeaker"),
            Map.entry(Permission.VOICE_STREAM, "Permission_VoiceStream"),
            Map.entry(Permission.VOICE_CONNECT, "Permission_VoiceConnect"),
            Map.entry(Permission.VOICE_SPEAK, "Permission_VoiceSpeak"),
            Map.entry(Permission.VOICE_MUTE_OTHERS, "Permission_VoiceMuteOthers"),
            Map.entry(Permission.VOICE_DEAF_OTHERS, "Permission_VoiceDeafOthers"),
            Map.entry(Permission.VOICE_MOVE_OTHERS, "Permission_VoiceMoveOthers"),
            Map.entry(Permission.VOICE_USE_VAD, "Permission_VoiceUseVad"),
            Map.entry(Permission.VOICE_START_ACTIVITIES, "Permission_VoiceStartActivities"),
            Map.entry(Permission.REQUEST_TO_SPEAK, "Permission_RequestToSpeak"),
            Map.entry(Permission.ADMINISTRATOR, "Permission_Administrator"),
            Map.entry(Permission.UNKNOWN, "Permission_Unknown")
    );
}
