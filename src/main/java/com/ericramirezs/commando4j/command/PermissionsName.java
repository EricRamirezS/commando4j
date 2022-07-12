/*
 *
 *    Copyright 2022 Eric Bastian Ramírez Santis
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.ericramirezs.commando4j.command;

import net.dv8tion.jda.api.Permission;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.HashMap;
import java.util.Map;

/**
 * Map with discord permissions and key for localized name.
 */
public interface PermissionsName {

    /**
     * Map with discord permissions and key for localized name.
     */
    @UnmodifiableView
    Map<Permission, String> NAME =  new HashMap<Permission, String>() {{
        put(Permission.MANAGE_CHANNEL, "Permission_ManageChannel");
        put(Permission.MANAGE_SERVER, "Permission_ManageServer");
        put(Permission.VIEW_AUDIT_LOGS, "Permission_ViewAuditLogs");
        put(Permission.VIEW_CHANNEL, "Permission_ViewChannel");
        put(Permission.VIEW_GUILD_INSIGHTS, "Permission_ViewGuildInsights");
        put(Permission.MANAGE_ROLES, "Permission_ManageRoles");
        put(Permission.MANAGE_PERMISSIONS, "Permission_ManagePermissions");
        put(Permission.MANAGE_WEBHOOKS, "Permission_ManageWebhooks");
        put(Permission.MANAGE_EMOJIS_AND_STICKERS, "Permission_ManageEmojisAndStickers");
        put(Permission.CREATE_INSTANT_INVITE, "Permission_CreateInstantInvite");
        put(Permission.KICK_MEMBERS, "Permission_KickMembers");
        put(Permission.BAN_MEMBERS, "Permission_BanMembers");
        put(Permission.NICKNAME_CHANGE, "Permission_NicknameChange");
        put(Permission.NICKNAME_MANAGE, "Permission_NicknameManage");
        put(Permission.MODERATE_MEMBERS, "Permission_ModerateMembers");
        put(Permission.MESSAGE_ADD_REACTION, "Permission_MessageAddReaction");
        put(Permission.MESSAGE_SEND, "Permission_MessageSend");
        put(Permission.MESSAGE_TTS, "Permission_MessageTts");
        put(Permission.MESSAGE_MANAGE, "Permission_MessageManage");
        put(Permission.MESSAGE_EMBED_LINKS, "Permission_MessageEmbedLinks");
        put(Permission.MESSAGE_ATTACH_FILES, "Permission_MessageAttachFiles");
        put(Permission.MESSAGE_HISTORY, "Permission_MessageHistory");
        put(Permission.MESSAGE_MENTION_EVERYONE, "Permission_MessageMentionEveryone");
        put(Permission.MESSAGE_EXT_EMOJI, "Permission_MessageExtEmoji");
        put(Permission.USE_APPLICATION_COMMANDS, "Permission_UseApplicationCommands");
        put(Permission.MESSAGE_EXT_STICKER, "Permission_MessageExtSticker");
        put(Permission.MANAGE_THREADS, "Permission_ManageThreads");
        put(Permission.CREATE_PUBLIC_THREADS, "Permission_CreatePublicThreads");
        put(Permission.CREATE_PRIVATE_THREADS, "Permission_CreatePrivateThreads");
        put(Permission.MESSAGE_SEND_IN_THREADS, "Permission_MessageSendInThreads");
        put(Permission.PRIORITY_SPEAKER, "Permission_PrioritySpeaker");
        put(Permission.VOICE_STREAM, "Permission_VoiceStream");
        put(Permission.VOICE_CONNECT, "Permission_VoiceConnect");
        put(Permission.VOICE_SPEAK, "Permission_VoiceSpeak");
        put(Permission.VOICE_MUTE_OTHERS, "Permission_VoiceMuteOthers");
        put(Permission.VOICE_DEAF_OTHERS, "Permission_VoiceDeafOthers");
        put(Permission.VOICE_MOVE_OTHERS, "Permission_VoiceMoveOthers");
        put(Permission.VOICE_USE_VAD, "Permission_VoiceUseVad");
        put(Permission.VOICE_START_ACTIVITIES, "Permission_VoiceStartActivities");
        put(Permission.REQUEST_TO_SPEAK, "Permission_RequestToSpeak");
        put(Permission.ADMINISTRATOR, "Permission_Administrator");
        put(Permission.UNKNOWN, "Permission_Unknown");
    }};
}
