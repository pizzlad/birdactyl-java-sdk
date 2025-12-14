package io.birdactyl.sdk;

public final class MixinTargets {
    public static final String SERVER_CREATE = "server.create";
    public static final String SERVER_UPDATE = "server.update";
    public static final String SERVER_DELETE = "server.delete";
    public static final String SERVER_START = "server.start";
    public static final String SERVER_STOP = "server.stop";
    public static final String SERVER_RESTART = "server.restart";
    public static final String SERVER_KILL = "server.kill";
    public static final String SERVER_SUSPEND = "server.suspend";
    public static final String SERVER_UNSUSPEND = "server.unsuspend";
    public static final String SERVER_REINSTALL = "server.reinstall";
    public static final String SERVER_TRANSFER = "server.transfer";
    public static final String SERVER_LIST = "server.list";
    public static final String SERVER_GET = "server.get";

    public static final String USER_CREATE = "user.create";
    public static final String USER_UPDATE = "user.update";
    public static final String USER_DELETE = "user.delete";
    public static final String USER_AUTHENTICATE = "user.authenticate";
    public static final String USER_BAN = "user.ban";
    public static final String USER_UNBAN = "user.unban";
    public static final String USER_LIST = "user.list";
    public static final String USER_GET = "user.get";

    public static final String DATABASE_CREATE = "database.create";
    public static final String DATABASE_DELETE = "database.delete";
    public static final String DATABASE_LIST = "database.list";

    public static final String BACKUP_CREATE = "backup.create";
    public static final String BACKUP_DELETE = "backup.delete";
    public static final String BACKUP_LIST = "backup.list";

    public static final String FILE_READ = "file.read";
    public static final String FILE_WRITE = "file.write";
    public static final String FILE_DELETE = "file.delete";
    public static final String FILE_UPLOAD = "file.upload";
    public static final String FILE_MOVE = "file.move";
    public static final String FILE_COPY = "file.copy";
    public static final String FILE_COMPRESS = "file.compress";
    public static final String FILE_DECOMPRESS = "file.decompress";
    public static final String FILE_LIST = "file.list";

    public static final String NODE_CREATE = "node.create";
    public static final String NODE_DELETE = "node.delete";
    public static final String NODE_LIST = "node.list";
    public static final String NODE_GET = "node.get";

    public static final String PACKAGE_CREATE = "package.create";
    public static final String PACKAGE_UPDATE = "package.update";
    public static final String PACKAGE_DELETE = "package.delete";
    public static final String PACKAGE_LIST = "package.list";
    public static final String PACKAGE_GET = "package.get";

    public static final String SUBUSER_ADD = "subuser.add";
    public static final String SUBUSER_UPDATE = "subuser.update";
    public static final String SUBUSER_REMOVE = "subuser.remove";
    public static final String SUBUSER_LIST = "subuser.list";

    public static final String IPBAN_CREATE = "ipban.create";
    public static final String IPBAN_DELETE = "ipban.delete";
    public static final String IPBAN_LIST = "ipban.list";

    public static final String ALLOCATION_ADD = "allocation.add";
    public static final String ALLOCATION_DELETE = "allocation.delete";
    public static final String ALLOCATION_SET_PRIMARY = "allocation.set_primary";
    public static final String ALLOCATION_LIST = "allocation.list";

    public static final String DBHOST_CREATE = "dbhost.create";
    public static final String DBHOST_UPDATE = "dbhost.update";
    public static final String DBHOST_DELETE = "dbhost.delete";
    public static final String DBHOST_LIST = "dbhost.list";

    public static final String SETTINGS_UPDATE = "settings.update";
    public static final String SETTINGS_GET = "settings.get";

    public static final String ACTIVITYLOG_LIST = "activitylog.list";

    public static final String CONSOLE_COMMAND = "console.command";

    private MixinTargets() {}
}
