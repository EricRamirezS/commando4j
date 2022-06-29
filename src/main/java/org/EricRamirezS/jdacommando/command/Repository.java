package org.EricRamirezS.jdacommando.command;

import org.EricRamirezS.jdacommando.command.data.IRepository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Locale;

class Repository implements IRepository {

    private static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:JDAcommand");
        } catch (SQLException e) {
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
            String sql = """
                    CREATE TABLE IF NOT EXISTS prefixes(
                        guild_id VARCHAR2 PRIMARY KEY,
                        set_prefix VARCHAR2 NOT NULL
                    )""";
            stmt.executeUpdate(sql);
            stmt.close();
            conn.close();
        } catch (Exception e) {
            CommandEngine.getInstance().logError(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                CommandEngine.getInstance().logError(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
            }
        }
        try {
            conn = connect();
            stmt = conn.createStatement();
            String sql = """
                    CREATE TABLE IF NOT EXISTS languages(
                        guild_id VARCHAR2 PRIMARY KEY,
                        locale_code VARCHAR2 NOT NULL
                    )""";
            stmt.executeUpdate(sql);
            stmt.close();
            conn.close();
        } catch (Exception e) {
            CommandEngine.getInstance().logError(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                CommandEngine.getInstance().logError(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
            }
        }
    }

    public String getPrefix(String guildId) {
        PreparedStatement stmt;
        Connection conn = null;

        try {
            conn = connect();
            String sql = "SELECT set_prefix FROM prefixes WHERE guild_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, guildId);
            ResultSet r = stmt.executeQuery();
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
        } catch (Exception e) {
            CommandEngine.getInstance().logError(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                CommandEngine.getInstance().logError(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
            }
        }
        return CommandEngine.getInstance().getPrefix();
    }

    public String setPrefix(String guildId, String prefix) {
        PreparedStatement stmt = null;
        Connection conn = null;
        try {
            conn = connect();
            String sql = "INSERT INTO prefixes values (?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, guildId);
            stmt.setString(2, prefix);
            stmt.executeUpdate();
        } catch (Exception e) {
            CommandEngine.getInstance().logError(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                CommandEngine.getInstance().logError(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
            }
        }
        return prefix;
    }

    @Override
    public String getLanguage(String guildId) {
        PreparedStatement stmt;
        Connection conn = null;
        Locale locale = CommandEngine.getInstance().getLanguage();

        try {
            conn = connect();
            String sql = "SELECT locale_code FROM languages WHERE guild_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, guildId);
            ResultSet r = stmt.executeQuery();
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
        } catch (Exception e) {
            CommandEngine.getInstance().logError(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                CommandEngine.getInstance().logError(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
            }
        }
        return locale.toLanguageTag();
    }

    @Override
    public String setLanguage(String guildId, String locale) {
        PreparedStatement stmt = null;
        Connection conn = null;
        try {
            conn = connect();
            String sql = """
                    INSERT OR REPLACE INTO languages values (?, ?)
                    """;
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, guildId);
            stmt.setString(2, locale);

            stmt.executeUpdate();
        } catch (Exception e) {
            CommandEngine.getInstance().logError(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                CommandEngine.getInstance().logError(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
            }
        }
        return locale;
    }
}
