package io.birdactyl.sdk;

import com.google.gson.Gson;
import com.google.protobuf.ByteString;
import io.birdactyl.sdk.proto.*;
import java.util.*;

public class PanelAPI {
    private static final Gson gson = new Gson();
    private final PanelServiceGrpc.PanelServiceBlockingStub stub;

    public PanelAPI(PanelServiceGrpc.PanelServiceBlockingStub stub) {
        this.stub = stub;
    }

    public void log(String level, String message) {
        stub.log(LogRequest.newBuilder().setLevel(level).setMessage(message).build());
    }

    public Server getServer(String id) {
        return new Server(stub.getServer(IDRequest.newBuilder().setId(id).build()));
    }

    public List<Server> listServers() {
        ListServersResponse resp = stub.listServers(ListServersRequest.getDefaultInstance());
        List<Server> out = new ArrayList<>();
        for (io.birdactyl.sdk.proto.Server s : resp.getServersList()) {
            out.add(new Server(s));
        }
        return out;
    }

    public List<Server> listServersByUser(String userId) {
        ListServersResponse resp = stub.listServers(ListServersRequest.newBuilder().setUserId(userId).build());
        List<Server> out = new ArrayList<>();
        for (io.birdactyl.sdk.proto.Server s : resp.getServersList()) {
            out.add(new Server(s));
        }
        return out;
    }

    public Server createServer(String name, String userId, String nodeId, String packageId, int memory, int cpu, int disk) {
        return new Server(stub.createServer(CreateServerRequest.newBuilder()
                .setName(name).setUserId(userId).setNodeId(nodeId).setPackageId(packageId)
                .setMemory(memory).setCpu(cpu).setDisk(disk).build()));
    }

    public void deleteServer(String id) { stub.deleteServer(IDRequest.newBuilder().setId(id).build()); }

    public Server updateServer(String id, String name, Integer memory, Integer cpu, Integer disk) {
        UpdateServerRequest.Builder req = UpdateServerRequest.newBuilder().setId(id);
        if (name != null) req.setName(name);
        if (memory != null) req.setMemory(memory);
        if (cpu != null) req.setCpu(cpu);
        if (disk != null) req.setDisk(disk);
        return new Server(stub.updateServer(req.build()));
    }
    public void startServer(String id) { stub.startServer(IDRequest.newBuilder().setId(id).build()); }
    public void stopServer(String id) { stub.stopServer(IDRequest.newBuilder().setId(id).build()); }
    public void restartServer(String id) { stub.restartServer(IDRequest.newBuilder().setId(id).build()); }
    public void killServer(String id) { stub.killServer(IDRequest.newBuilder().setId(id).build()); }
    public void suspendServer(String id) { stub.suspendServer(IDRequest.newBuilder().setId(id).build()); }
    public void unsuspendServer(String id) { stub.unsuspendServer(IDRequest.newBuilder().setId(id).build()); }
    public void reinstallServer(String id) { stub.reinstallServer(IDRequest.newBuilder().setId(id).build()); }

    public void transferServer(String serverId, String targetNodeId) {
        stub.transferServer(TransferServerRequest.newBuilder().setServerId(serverId).setTargetNodeId(targetNodeId).build());
    }

    public List<String> getConsoleLog(String serverId, int lines) {
        return stub.getConsoleLog(ConsoleLogRequest.newBuilder().setServerId(serverId).setLines(lines).build()).getLinesList();
    }

    public void sendCommand(String serverId, String command) {
        stub.sendCommand(SendCommandRequest.newBuilder().setServerId(serverId).setCommand(command).build());
    }

    public ServerStats getServerStats(String serverId) {
        io.birdactyl.sdk.proto.ServerStats s = stub.getServerStats(IDRequest.newBuilder().setId(serverId).build());
        return new ServerStats(s.getMemoryBytes(), s.getMemoryLimit(), s.getCpuPercent(), s.getDiskBytes(), s.getNetworkRx(), s.getNetworkTx(), s.getState());
    }

    public void addAllocation(String serverId, int port) {
        stub.addAllocation(AllocationRequest.newBuilder().setServerId(serverId).setPort(port).build());
    }

    public void deleteAllocation(String serverId, int port) {
        stub.deleteAllocation(AllocationRequest.newBuilder().setServerId(serverId).setPort(port).build());
    }

    public void setPrimaryAllocation(String serverId, int port) {
        stub.setPrimaryAllocation(AllocationRequest.newBuilder().setServerId(serverId).setPort(port).build());
    }

    public void updateServerVariables(String serverId, Map<String, String> variables) {
        stub.updateServerVariables(UpdateVariablesRequest.newBuilder().setServerId(serverId).putAllVariables(variables).build());
    }

    public void compressFiles(String serverId, List<String> paths, String destination) {
        stub.compressFiles(CompressRequest.newBuilder().setServerId(serverId).addAllPaths(paths).setDestination(destination).build());
    }

    public void decompressFile(String serverId, String path) {
        stub.decompressFile(FilePathRequest.newBuilder().setServerId(serverId).setPath(path).build());
    }

    public User getUser(String id) {
        return new User(stub.getUser(IDRequest.newBuilder().setId(id).build()));
    }

    public User getUserByEmail(String email) {
        return new User(stub.getUserByEmail(EmailRequest.newBuilder().setEmail(email).build()));
    }

    public User getUserByUsername(String username) {
        return new User(stub.getUserByUsername(UsernameRequest.newBuilder().setUsername(username).build()));
    }

    public List<User> listUsers() {
        ListUsersResponse resp = stub.listUsers(ListUsersRequest.getDefaultInstance());
        List<User> out = new ArrayList<>();
        for (io.birdactyl.sdk.proto.User u : resp.getUsersList()) {
            out.add(new User(u));
        }
        return out;
    }

    public User createUser(String email, String username, String password) {
        return new User(stub.createUser(CreateUserRequest.newBuilder()
                .setEmail(email).setUsername(username).setPassword(password).build()));
    }

    public User updateUser(String id, String username, String email) {
        UpdateUserRequest.Builder req = UpdateUserRequest.newBuilder().setId(id);
        if (username != null) req.setUsername(username);
        if (email != null) req.setEmail(email);
        return new User(stub.updateUser(req.build()));
    }

    public void deleteUser(String id) { stub.deleteUser(IDRequest.newBuilder().setId(id).build()); }
    public void banUser(String id) { stub.banUser(IDRequest.newBuilder().setId(id).build()); }
    public void unbanUser(String id) { stub.unbanUser(IDRequest.newBuilder().setId(id).build()); }
    public void setAdmin(String id) { stub.setAdmin(IDRequest.newBuilder().setId(id).build()); }
    public void revokeAdmin(String id) { stub.revokeAdmin(IDRequest.newBuilder().setId(id).build()); }
    public void forcePasswordReset(String id) { stub.forcePasswordReset(IDRequest.newBuilder().setId(id).build()); }

    public void setUserResources(String userId, Integer ramLimit, Integer cpuLimit, Integer diskLimit, Integer serverLimit) {
        SetUserResourcesRequest.Builder req = SetUserResourcesRequest.newBuilder().setUserId(userId);
        if (ramLimit != null) req.setRamLimit(ramLimit);
        if (cpuLimit != null) req.setCpuLimit(cpuLimit);
        if (diskLimit != null) req.setDiskLimit(diskLimit);
        if (serverLimit != null) req.setServerLimit(serverLimit);
        stub.setUserResources(req.build());
    }

    public List<Node> listNodes() {
        ListNodesResponse resp = stub.listNodes(Empty.getDefaultInstance());
        List<Node> out = new ArrayList<>();
        for (io.birdactyl.sdk.proto.Node n : resp.getNodesList()) {
            out.add(new Node(n));
        }
        return out;
    }

    public Node getNode(String id) {
        return new Node(stub.getNode(IDRequest.newBuilder().setId(id).build()));
    }

    public NodeWithToken createNode(String name, String fqdn, int port) {
        io.birdactyl.sdk.proto.NodeWithToken resp = stub.createNode(CreateNodeRequest.newBuilder()
                .setName(name).setFqdn(fqdn).setPort(port).build());
        return new NodeWithToken(new Node(resp.getNode()), resp.getToken());
    }

    public void deleteNode(String id) { stub.deleteNode(IDRequest.newBuilder().setId(id).build()); }

    public String resetNodeToken(String id) {
        return stub.resetNodeToken(IDRequest.newBuilder().setId(id).build()).getToken();
    }

    public List<File> listFiles(String serverId, String path) {
        ListFilesResponse resp = stub.listFiles(FilePathRequest.newBuilder().setServerId(serverId).setPath(path).build());
        List<File> out = new ArrayList<>();
        for (FileInfo f : resp.getFilesList()) {
            out.add(new File(f));
        }
        return out;
    }

    public byte[] readFile(String serverId, String path) {
        return stub.readFile(FilePathRequest.newBuilder().setServerId(serverId).setPath(path).build()).getContent().toByteArray();
    }

    public void writeFile(String serverId, String path, byte[] content) {
        stub.writeFile(WriteFileRequest.newBuilder().setServerId(serverId).setPath(path).setContent(ByteString.copyFrom(content)).build());
    }

    public void deleteFile(String serverId, String path) {
        stub.deleteFile(FilePathRequest.newBuilder().setServerId(serverId).setPath(path).build());
    }

    public void createFolder(String serverId, String path) {
        stub.createFolder(FilePathRequest.newBuilder().setServerId(serverId).setPath(path).build());
    }

    public void moveFile(String serverId, String from, String to) {
        stub.moveFile(MoveFileRequest.newBuilder().setServerId(serverId).setFrom(from).setTo(to).build());
    }

    public void copyFile(String serverId, String from, String to) {
        stub.copyFile(MoveFileRequest.newBuilder().setServerId(serverId).setFrom(from).setTo(to).build());
    }

    public List<Database> listDatabases(String serverId) {
        ListDatabasesResponse resp = stub.listDatabases(IDRequest.newBuilder().setId(serverId).build());
        List<Database> out = new ArrayList<>();
        for (io.birdactyl.sdk.proto.Database d : resp.getDatabasesList()) {
            out.add(new Database(d));
        }
        return out;
    }

    public Database createDatabase(String serverId, String name) {
        return new Database(stub.createDatabase(CreateDatabaseRequest.newBuilder().setServerId(serverId).setName(name).build()));
    }

    public void deleteDatabase(String id) { stub.deleteDatabase(IDRequest.newBuilder().setId(id).build()); }

    public Database rotateDatabasePassword(String id) {
        return new Database(stub.rotateDatabasePassword(IDRequest.newBuilder().setId(id).build()));
    }

    public List<DatabaseHost> listDatabaseHosts() {
        ListDatabaseHostsResponse resp = stub.listDatabaseHosts(Empty.getDefaultInstance());
        List<DatabaseHost> out = new ArrayList<>();
        for (io.birdactyl.sdk.proto.DatabaseHost h : resp.getHostsList()) {
            out.add(new DatabaseHost(h));
        }
        return out;
    }

    public List<Backup> listBackups(String serverId) {
        ListBackupsResponse resp = stub.listBackups(IDRequest.newBuilder().setId(serverId).build());
        List<Backup> out = new ArrayList<>();
        for (io.birdactyl.sdk.proto.Backup b : resp.getBackupsList()) {
            out.add(new Backup(b));
        }
        return out;
    }

    public void createBackup(String serverId, String name) {
        stub.createBackup(CreateBackupRequest.newBuilder().setServerId(serverId).setName(name).build());
    }

    public void deleteBackup(String serverId, String backupId) {
        stub.deleteBackup(DeleteBackupRequest.newBuilder().setServerId(serverId).setBackupId(backupId).build());
    }

    public List<Package> listPackages() {
        ListPackagesResponse resp = stub.listPackages(Empty.getDefaultInstance());
        List<Package> out = new ArrayList<>();
        for (io.birdactyl.sdk.proto.Package p : resp.getPackagesList()) {
            out.add(new Package(p));
        }
        return out;
    }

    public Package getPackage(String id) {
        return new Package(stub.getPackage(IDRequest.newBuilder().setId(id).build()));
    }

    public Package createPackage(String name, String description, String dockerImage, String startupCmd, String stopCmd, String configFiles, int memory, int cpu, int disk, boolean isPublic) {
        return new Package(stub.createPackage(CreatePackageRequest.newBuilder()
                .setName(name).setDescription(description).setDockerImage(dockerImage)
                .setStartupCommand(startupCmd).setStopCommand(stopCmd).setConfigFiles(configFiles)
                .setDefaultMemory(memory).setDefaultCpu(cpu).setDefaultDisk(disk).setIsPublic(isPublic).build()));
    }

    public Package updatePackage(String id, String name, String description, Integer memory, Integer cpu, Integer disk) {
        UpdatePackageRequest.Builder req = UpdatePackageRequest.newBuilder().setId(id);
        if (name != null) req.setName(name);
        if (description != null) req.setDescription(description);
        if (memory != null) req.setDefaultMemory(memory);
        if (cpu != null) req.setDefaultCpu(cpu);
        if (disk != null) req.setDefaultDisk(disk);
        return new Package(stub.updatePackage(req.build()));
    }

    public void deletePackage(String id) { stub.deletePackage(IDRequest.newBuilder().setId(id).build()); }

    public List<IPBan> listIPBans() {
        ListIPBansResponse resp = stub.listIPBans(Empty.getDefaultInstance());
        List<IPBan> out = new ArrayList<>();
        for (io.birdactyl.sdk.proto.IPBan b : resp.getBansList()) {
            out.add(new IPBan(b));
        }
        return out;
    }

    public IPBan createIPBan(String ip, String reason) {
        return new IPBan(stub.createIPBan(CreateIPBanRequest.newBuilder().setIp(ip).setReason(reason).build()));
    }

    public void deleteIPBan(String id) { stub.deleteIPBan(IDRequest.newBuilder().setId(id).build()); }

    public List<Subuser> listSubusers(String serverId) {
        ListSubusersResponse resp = stub.listSubusers(IDRequest.newBuilder().setId(serverId).build());
        List<Subuser> out = new ArrayList<>();
        for (io.birdactyl.sdk.proto.Subuser s : resp.getSubusersList()) {
            out.add(new Subuser(s));
        }
        return out;
    }

    public Subuser addSubuser(String serverId, String email, List<String> permissions) {
        return new Subuser(stub.addSubuser(AddSubuserRequest.newBuilder()
                .setServerId(serverId).setEmail(email).addAllPermissions(permissions).build()));
    }

    public void updateSubuser(String serverId, String subuserId, List<String> permissions) {
        stub.updateSubuser(UpdateSubuserRequest.newBuilder().setServerId(serverId).setSubuserId(subuserId).addAllPermissions(permissions).build());
    }

    public void removeSubuser(String serverId, String subuserId) {
        stub.removeSubuser(RemoveSubuserRequest.newBuilder().setServerId(serverId).setSubuserId(subuserId).build());
    }

    public Settings getSettings() {
        io.birdactyl.sdk.proto.Settings s = stub.getSettings(Empty.getDefaultInstance());
        return new Settings(s.getRegistrationEnabled(), s.getServerCreationEnabled());
    }

    public void setRegistrationEnabled(boolean enabled) {
        stub.setRegistrationEnabled(BoolRequest.newBuilder().setValue(enabled).build());
    }

    public void setServerCreationEnabled(boolean enabled) {
        stub.setServerCreationEnabled(BoolRequest.newBuilder().setValue(enabled).build());
    }

    public List<ActivityLog> getActivityLogs(int limit) {
        GetLogsResponse resp = stub.getActivityLogs(GetLogsRequest.newBuilder().setLimit(limit).build());
        List<ActivityLog> out = new ArrayList<>();
        for (io.birdactyl.sdk.proto.ActivityLog l : resp.getLogsList()) {
            out.add(new ActivityLog(l));
        }
        return out;
    }

    public String getKV(String key) {
        KVResponse resp = stub.getKV(KVRequest.newBuilder().setKey(key).build());
        return resp.getFound() ? resp.getValue() : null;
    }

    public void setKV(String key, String value) {
        stub.setKV(KVSetRequest.newBuilder().setKey(key).setValue(value).build());
    }

    public void deleteKV(String key) {
        stub.deleteKV(KVRequest.newBuilder().setKey(key).build());
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> queryDB(String query, String... args) {
        QueryDBResponse resp = stub.queryDB(QueryDBRequest.newBuilder().setQuery(query).addAllArgs(Arrays.asList(args)).build());
        List<Map<String, Object>> out = new ArrayList<>();
        for (ByteString row : resp.getRowsList()) {
            out.add(gson.fromJson(row.toStringUtf8(), Map.class));
        }
        return out;
    }

    public void broadcastEvent(String eventType, Map<String, String> data) {
        stub.broadcastEvent(BroadcastEventRequest.newBuilder().setEventType(eventType).putAllData(data).build());
    }

    public static class Server {
        public final String id, name, ownerId, nodeId, status, packageId, primaryAllocation;
        public final boolean suspended;
        public final int memory, cpu, disk;

        Server(io.birdactyl.sdk.proto.Server s) {
            this.id = s.getId(); this.name = s.getName(); this.ownerId = s.getUserId();
            this.nodeId = s.getNodeId(); this.status = s.getStatus(); this.suspended = s.getSuspended();
            this.memory = s.getMemory(); this.cpu = s.getCpu(); this.disk = s.getDisk();
            this.packageId = s.getPackageId(); this.primaryAllocation = s.getPrimaryAllocation();
        }
    }

    public static class User {
        public final String id, username, email, createdAt;
        public final boolean isAdmin, isBanned, forcePasswordReset;
        public final int ramLimit, cpuLimit, diskLimit, serverLimit;

        User(io.birdactyl.sdk.proto.User u) {
            this.id = u.getId(); this.username = u.getUsername(); this.email = u.getEmail();
            this.isAdmin = u.getIsAdmin(); this.isBanned = u.getIsBanned();
            this.ramLimit = u.getRamLimit(); this.cpuLimit = u.getCpuLimit();
            this.diskLimit = u.getDiskLimit(); this.serverLimit = u.getServerLimit();
            this.forcePasswordReset = u.getForcePasswordReset(); this.createdAt = u.getCreatedAt();
        }
    }

    public static class Node {
        public final String id, name, fqdn, lastHeartbeat;
        public final int port;
        public final boolean isOnline;

        Node(io.birdactyl.sdk.proto.Node n) {
            this.id = n.getId(); this.name = n.getName(); this.fqdn = n.getFqdn();
            this.port = n.getPort(); this.isOnline = n.getIsOnline(); this.lastHeartbeat = n.getLastHeartbeat();
        }
    }

    public static class NodeWithToken {
        public final Node node;
        public final String token;

        NodeWithToken(Node node, String token) {
            this.node = node; this.token = token;
        }
    }

    public static class File {
        public final String name, modTime, mime;
        public final long size;
        public final boolean isDir;

        File(FileInfo f) {
            this.name = f.getName(); this.size = f.getSize(); this.isDir = f.getIsDir();
            this.modTime = f.getModified(); this.mime = f.getMime();
        }
    }

    public static class Database {
        public final String id, name, username, password, host;
        public final int port;

        Database(io.birdactyl.sdk.proto.Database d) {
            this.id = d.getId(); this.name = d.getName(); this.username = d.getUsername();
            this.password = d.getPassword(); this.host = d.getHost(); this.port = d.getPort();
        }
    }

    public static class DatabaseHost {
        public final String id, name, host, username;
        public final int port, maxDatabases, databasesCount;

        DatabaseHost(io.birdactyl.sdk.proto.DatabaseHost h) {
            this.id = h.getId(); this.name = h.getName(); this.host = h.getHost();
            this.username = h.getUsername(); this.port = h.getPort();
            this.maxDatabases = h.getMaxDatabases(); this.databasesCount = h.getDatabasesCount();
        }
    }

    public static class Backup {
        public final String id, name, createdAt;
        public final long size;

        Backup(io.birdactyl.sdk.proto.Backup b) {
            this.id = b.getId(); this.name = b.getName(); this.size = b.getSize(); this.createdAt = b.getCreatedAt();
        }
    }

    public static class Package {
        public final String id, name, description, dockerImage, startupCommand, stopCommand, configFiles;
        public final int defaultMemory, defaultCpu, defaultDisk;
        public final boolean isPublic;

        Package(io.birdactyl.sdk.proto.Package p) {
            this.id = p.getId(); this.name = p.getName(); this.description = p.getDescription();
            this.dockerImage = p.getDockerImage(); this.startupCommand = p.getStartupCommand();
            this.stopCommand = p.getStopCommand(); this.configFiles = p.getConfigFiles();
            this.defaultMemory = p.getDefaultMemory(); this.defaultCpu = p.getDefaultCpu();
            this.defaultDisk = p.getDefaultDisk(); this.isPublic = p.getIsPublic();
        }
    }

    public static class IPBan {
        public final String id, ip, reason, createdAt;

        IPBan(io.birdactyl.sdk.proto.IPBan b) {
            this.id = b.getId(); this.ip = b.getIp(); this.reason = b.getReason(); this.createdAt = b.getCreatedAt();
        }
    }

    public static class Subuser {
        public final String id, userId, username, email;
        public final List<String> permissions;

        Subuser(io.birdactyl.sdk.proto.Subuser s) {
            this.id = s.getId(); this.userId = s.getUserId(); this.username = s.getUsername();
            this.email = s.getEmail(); this.permissions = s.getPermissionsList();
        }
    }

    public static class Settings {
        public final boolean registrationEnabled, serverCreationEnabled;

        Settings(boolean registrationEnabled, boolean serverCreationEnabled) {
            this.registrationEnabled = registrationEnabled; this.serverCreationEnabled = serverCreationEnabled;
        }
    }

    public static class ActivityLog {
        public final String id, userId, username, action, description, ip, createdAt;
        public final boolean isAdmin;

        ActivityLog(io.birdactyl.sdk.proto.ActivityLog l) {
            this.id = l.getId(); this.userId = l.getUserId(); this.username = l.getUsername();
            this.action = l.getAction(); this.description = l.getDescription(); this.ip = l.getIp();
            this.isAdmin = l.getIsAdmin(); this.createdAt = l.getCreatedAt();
        }
    }

    public static class ServerStats {
        public final long memoryBytes, memoryLimit, diskBytes, networkRx, networkTx;
        public final double cpuPercent;
        public final String state;

        ServerStats(long memoryBytes, long memoryLimit, double cpuPercent, long diskBytes, long networkRx, long networkTx, String state) {
            this.memoryBytes = memoryBytes; this.memoryLimit = memoryLimit; this.cpuPercent = cpuPercent;
            this.diskBytes = diskBytes; this.networkRx = networkRx; this.networkTx = networkTx; this.state = state;
        }
    }
}
