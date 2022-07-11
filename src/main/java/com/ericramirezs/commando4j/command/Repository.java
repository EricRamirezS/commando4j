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

import com.ericramirezs.commando4j.command.data.IRepository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Locale;

/**
 * Default Implementation of IRepository, using sqlite.
 */
class Repository implements IRepository {

    private static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:commando4j");
        } catch (final SQLException e) {
            CommandEngine.getInstance().logError(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
        }
        return conn;
    }

    void createTables() {
        Statement stmt;
        Connection conn = null;
        try {
            conn = connect();
            stmt = conn.createStatement();
            final String sql = "CREATE TABLE IF NOT EXISTS prefixes(" +
                    " guild_id VARCHAR2 PRIMARY KEY," +
                    " set_prefix VARCHAR2 NOT NULL)";
            stmt.executeUpdate(sql);
            stmt.close();
            conn.close();
        } catch (final Exception e) {
            CommandEngine.getInstance().logError(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (final SQLException e) {
                CommandEngine.getInstance().logError(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
            }
        }
        try {
            conn = connect();
            stmt = conn.createStatement();
            final String sql = "CREATE TABLE IF NOT EXISTS languages(" +
                    "           guild_id VARCHAR2 PRIMARY KEY," +
                    "           locale_code VARCHAR2 NOT NULL)";
            stmt.executeUpdate(sql);
            stmt.close();
            conn.close();
        } catch (final Exception e) {
            CommandEngine.getInstance().logError(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (final SQLException e) {
                CommandEngine.getInstance().logError(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
            }
        }
    }

    @Override
    public String getPrefix(final String guildId) {
        final PreparedStatement stmt;
        Connection conn = null;

        try {
            conn = connect();
            final String sql = "SELECT set_prefix FROM prefixes WHERE guild_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, guildId);
            final ResultSet r = stmt.executeQuery();
            String prefix = null;
            while (r.next()) {
                prefix = r.getString("set_prefix");
            }
            stmt.close();
            conn.close();
            if (prefix == null) {
                return setPrefix(guildId, CommandEngine.getInstance().getPrefix());
            }
            return prefix;
        } catch (final Exception e) {
            CommandEngine.getInstance().logError(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (final SQLException e) {
                CommandEngine.getInstance().logError(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
            }
        }
        return CommandEngine.getInstance().getPrefix();
    }

    @Override
    public String setPrefix(final String guildId, final String prefix) {
        PreparedStatement stmt = null;
        Connection conn = null;
        try {
            conn = connect();
            final String sql = "INSERT OR REPLACE INTO prefixes values (?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, guildId);
            stmt.setString(2, prefix);
            stmt.executeUpdate();
        } catch (final Exception e) {
            CommandEngine.getInstance().logError(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
        } finally {
            closeConnections(stmt, conn);
        }
        return prefix;
    }

    private void closeConnections(final PreparedStatement stmt, final Connection conn) {
        try {
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (final SQLException e) {
            CommandEngine.getInstance().logError(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
        }
    }

    @Override
    public String getLanguage(final String guildId) {
        final PreparedStatement stmt;
        Connection conn = null;
        final Locale locale = CommandEngine.getInstance().getLanguage();

        try {
            conn = connect();
            final String sql = "SELECT locale_code FROM languages WHERE guild_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, guildId);
            final ResultSet r = stmt.executeQuery();
            String localeCode = null;
            while (r.next()) {
                localeCode = r.getString("locale_code");
            }
            stmt.close();
            conn.close();
            if (localeCode == null) {
                return setLanguage(guildId, locale.toLanguageTag());
            }
            return localeCode;
        } catch (final Exception e) {
            CommandEngine.getInstance().logError(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (final SQLException e) {
                CommandEngine.getInstance().logError(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
            }
        }
        return locale.toLanguageTag();
    }

    @Override
    public String setLanguage(final String guildId, final String locale) {
        PreparedStatement stmt = null;
        Connection conn = null;
        try {
            conn = connect();
            final String sql = "INSERT OR REPLACE INTO languages values (?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, guildId);
            stmt.setString(2, locale);

            stmt.executeUpdate();
        } catch (final Exception e) {
            CommandEngine.getInstance().logError(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
        } finally {
            closeConnections(stmt, conn);
        }
        return locale;
    }
}
