package okhttp3.internal.connection;

import java.io.IOException;
import java.lang.ref.Reference;
import java.net.ConnectException;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import okhttp3.Address;
import okhttp3.Call;
import okhttp3.Connection;
import okhttp3.ConnectionPool;
import okhttp3.EventListener;
import okhttp3.Handshake;
import okhttp3.HttpUrl;
import okhttp3.Interceptor.Chain;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.internal.Internal;
import okhttp3.internal.Util;
import okhttp3.internal.Version;
import okhttp3.internal.http.HttpCodec;
import okhttp3.internal.http.HttpHeaders;
import okhttp3.internal.http1.Http1Codec;
import okhttp3.internal.http2.ErrorCode;
import okhttp3.internal.http2.Http2Codec;
import okhttp3.internal.http2.Http2Connection;
import okhttp3.internal.http2.Http2Connection.Builder;
import okhttp3.internal.http2.Http2Connection.Listener;
import okhttp3.internal.http2.Http2Stream;
import okhttp3.internal.platform.Platform;
import okhttp3.internal.tls.OkHostnameVerifier;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import okio.Source;

public final class RealConnection extends Listener implements Connection {
    public int allocationLimit = 1;
    public final List<Reference<StreamAllocation>> allocations = new ArrayList();
    private final ConnectionPool connectionPool;
    private Handshake handshake;
    private Http2Connection http2Connection;
    public long idleAtNanos = Long.MAX_VALUE;
    public boolean noNewStreams;
    private Protocol protocol;
    private Socket rawSocket;
    private final Route route;
    private BufferedSink sink;
    private Socket socket;
    private BufferedSource source;
    public int successCount;

    public RealConnection(ConnectionPool connectionPool2, Route route2) {
        this.connectionPool = connectionPool2;
        this.route = route2;
    }

    /* JADX WARNING: Removed duplicated region for block: B:35:0x00d5  */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x0121  */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x012a  */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x0131  */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x0137 A[EDGE_INSN: B:59:0x0137->B:57:0x0137 ?: BREAK  , SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:62:? A[ORIG_RETURN, RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void connect(int r18, int r19, int r20, int r21, boolean r22, okhttp3.Call r23, okhttp3.EventListener r24) {
        /*
            r17 = this;
            r7 = r17
            r8 = r23
            r9 = r24
            okhttp3.Protocol r1 = r7.protocol
            if (r1 == 0) goto L_0x0012
            java.lang.IllegalStateException r1 = new java.lang.IllegalStateException
            java.lang.String r2 = "already connected"
            r1.<init>(r2)
            throw r1
        L_0x0012:
            okhttp3.Route r1 = r7.route
            okhttp3.Address r1 = r1.address()
            java.util.List r1 = r1.connectionSpecs()
            okhttp3.internal.connection.ConnectionSpecSelector r10 = new okhttp3.internal.connection.ConnectionSpecSelector
            r10.<init>(r1)
            okhttp3.Route r2 = r7.route
            okhttp3.Address r2 = r2.address()
            javax.net.ssl.SSLSocketFactory r2 = r2.sslSocketFactory()
            if (r2 != 0) goto L_0x007b
            okhttp3.ConnectionSpec r2 = okhttp3.ConnectionSpec.CLEARTEXT
            boolean r1 = r1.contains(r2)
            if (r1 != 0) goto L_0x0042
            okhttp3.internal.connection.RouteException r1 = new okhttp3.internal.connection.RouteException
            java.net.UnknownServiceException r2 = new java.net.UnknownServiceException
            java.lang.String r3 = "CLEARTEXT communication not enabled for client"
            r2.<init>(r3)
            r1.<init>(r2)
            throw r1
        L_0x0042:
            okhttp3.Route r1 = r7.route
            okhttp3.Address r1 = r1.address()
            okhttp3.HttpUrl r1 = r1.url()
            java.lang.String r1 = r1.host()
            okhttp3.internal.platform.Platform r2 = okhttp3.internal.platform.Platform.get()
            boolean r2 = r2.isCleartextTrafficPermitted(r1)
            if (r2 != 0) goto L_0x007b
            okhttp3.internal.connection.RouteException r2 = new okhttp3.internal.connection.RouteException
            java.net.UnknownServiceException r3 = new java.net.UnknownServiceException
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "CLEARTEXT communication to "
            r4.append(r5)
            r4.append(r1)
            java.lang.String r1 = " not permitted by network security policy"
            r4.append(r1)
            java.lang.String r1 = r4.toString()
            r3.<init>(r1)
            r2.<init>(r3)
            throw r2
        L_0x007b:
            r11 = 0
            r12 = r11
        L_0x007d:
            okhttp3.Route r1 = r7.route     // Catch:{ IOException -> 0x00eb }
            boolean r1 = r1.requiresTunnel()     // Catch:{ IOException -> 0x00eb }
            if (r1 == 0) goto L_0x009b
            r1 = r7
            r2 = r18
            r3 = r19
            r4 = r20
            r5 = r8
            r6 = r9
            r1.connectTunnel(r2, r3, r4, r5, r6)     // Catch:{ IOException -> 0x00eb }
            java.net.Socket r1 = r7.rawSocket     // Catch:{ IOException -> 0x00eb }
            if (r1 != 0) goto L_0x0096
            goto L_0x00b8
        L_0x0096:
            r13 = r18
            r14 = r19
            goto L_0x00a2
        L_0x009b:
            r13 = r18
            r14 = r19
            r7.connectSocket(r13, r14, r8, r9)     // Catch:{ IOException -> 0x00e9 }
        L_0x00a2:
            r15 = r21
            r7.establishProtocol(r10, r15, r8, r9)     // Catch:{ IOException -> 0x00e7 }
            okhttp3.Route r1 = r7.route     // Catch:{ IOException -> 0x00e7 }
            java.net.InetSocketAddress r1 = r1.socketAddress()     // Catch:{ IOException -> 0x00e7 }
            okhttp3.Route r2 = r7.route     // Catch:{ IOException -> 0x00e7 }
            java.net.Proxy r2 = r2.proxy()     // Catch:{ IOException -> 0x00e7 }
            okhttp3.Protocol r3 = r7.protocol     // Catch:{ IOException -> 0x00e7 }
            r9.connectEnd(r8, r1, r2, r3)     // Catch:{ IOException -> 0x00e7 }
        L_0x00b8:
            okhttp3.Route r1 = r7.route
            boolean r1 = r1.requiresTunnel()
            if (r1 == 0) goto L_0x00d1
            java.net.Socket r1 = r7.rawSocket
            if (r1 != 0) goto L_0x00d1
            java.net.ProtocolException r1 = new java.net.ProtocolException
            java.lang.String r2 = "Too many tunnel connections attempted: 21"
            r1.<init>(r2)
            okhttp3.internal.connection.RouteException r2 = new okhttp3.internal.connection.RouteException
            r2.<init>(r1)
            throw r2
        L_0x00d1:
            okhttp3.internal.http2.Http2Connection r1 = r7.http2Connection
            if (r1 == 0) goto L_0x00e6
            okhttp3.ConnectionPool r1 = r7.connectionPool
            monitor-enter(r1)
            okhttp3.internal.http2.Http2Connection r2 = r7.http2Connection     // Catch:{ all -> 0x00e2 }
            int r2 = r2.maxConcurrentStreams()     // Catch:{ all -> 0x00e2 }
            r7.allocationLimit = r2     // Catch:{ all -> 0x00e2 }
            monitor-exit(r1)     // Catch:{ all -> 0x00e2 }
            goto L_0x00e6
        L_0x00e2:
            r0 = move-exception
            r2 = r0
            monitor-exit(r1)     // Catch:{ all -> 0x00e2 }
            throw r2
        L_0x00e6:
            return
        L_0x00e7:
            r0 = move-exception
            goto L_0x00f2
        L_0x00e9:
            r0 = move-exception
            goto L_0x00f0
        L_0x00eb:
            r0 = move-exception
            r13 = r18
            r14 = r19
        L_0x00f0:
            r15 = r21
        L_0x00f2:
            r6 = r0
            java.net.Socket r1 = r7.socket
            okhttp3.internal.Util.closeQuietly(r1)
            java.net.Socket r1 = r7.rawSocket
            okhttp3.internal.Util.closeQuietly(r1)
            r7.socket = r11
            r7.rawSocket = r11
            r7.source = r11
            r7.sink = r11
            r7.handshake = r11
            r7.protocol = r11
            r7.http2Connection = r11
            okhttp3.Route r1 = r7.route
            java.net.InetSocketAddress r3 = r1.socketAddress()
            okhttp3.Route r1 = r7.route
            java.net.Proxy r4 = r1.proxy()
            r5 = 0
            r1 = r9
            r2 = r8
            r16 = r6
            r1.connectFailed(r2, r3, r4, r5, r6)
            if (r12 != 0) goto L_0x012a
            okhttp3.internal.connection.RouteException r1 = new okhttp3.internal.connection.RouteException
            r2 = r16
            r1.<init>(r2)
            r12 = r1
            goto L_0x012f
        L_0x012a:
            r2 = r16
            r12.addConnectException(r2)
        L_0x012f:
            if (r22 == 0) goto L_0x0137
            boolean r2 = r10.connectionFailed(r2)
            if (r2 != 0) goto L_0x007d
        L_0x0137:
            throw r12
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.connection.RealConnection.connect(int, int, int, int, boolean, okhttp3.Call, okhttp3.EventListener):void");
    }

    private void connectTunnel(int i, int i2, int i3, Call call, EventListener eventListener) throws IOException {
        Request createTunnelRequest = createTunnelRequest();
        HttpUrl url = createTunnelRequest.url();
        int i4 = 0;
        while (i4 < 21) {
            connectSocket(i, i2, call, eventListener);
            createTunnelRequest = createTunnel(i2, i3, createTunnelRequest, url);
            if (createTunnelRequest != null) {
                Util.closeQuietly(this.rawSocket);
                this.rawSocket = null;
                this.sink = null;
                this.source = null;
                eventListener.connectEnd(call, this.route.socketAddress(), this.route.proxy(), null);
                i4++;
            } else {
                return;
            }
        }
    }

    private void connectSocket(int i, int i2, Call call, EventListener eventListener) throws IOException {
        Socket socket2;
        Proxy proxy = this.route.proxy();
        Address address = this.route.address();
        if (proxy.type() == Type.DIRECT || proxy.type() == Type.HTTP) {
            socket2 = address.socketFactory().createSocket();
        } else {
            socket2 = new Socket(proxy);
        }
        this.rawSocket = socket2;
        eventListener.connectStart(call, this.route.socketAddress(), proxy);
        this.rawSocket.setSoTimeout(i2);
        try {
            Platform.get().connectSocket(this.rawSocket, this.route.socketAddress(), i);
            try {
                this.source = Okio.buffer(Okio.source(this.rawSocket));
                this.sink = Okio.buffer(Okio.sink(this.rawSocket));
            } catch (NullPointerException e) {
                if ("throw with null exception".equals(e.getMessage())) {
                    throw new IOException(e);
                }
            }
        } catch (ConnectException e2) {
            StringBuilder sb = new StringBuilder();
            sb.append("Failed to connect to ");
            sb.append(this.route.socketAddress());
            ConnectException connectException = new ConnectException(sb.toString());
            connectException.initCause(e2);
            throw connectException;
        }
    }

    private void establishProtocol(ConnectionSpecSelector connectionSpecSelector, int i, Call call, EventListener eventListener) throws IOException {
        if (this.route.address().sslSocketFactory() == null) {
            this.protocol = Protocol.HTTP_1_1;
            this.socket = this.rawSocket;
            return;
        }
        eventListener.secureConnectStart(call);
        connectTls(connectionSpecSelector);
        eventListener.secureConnectEnd(call, this.handshake);
        if (this.protocol == Protocol.HTTP_2) {
            this.socket.setSoTimeout(0);
            this.http2Connection = new Builder(true).socket(this.socket, this.route.address().url().host(), this.source, this.sink).listener(this).pingIntervalMillis(i).build();
            this.http2Connection.start();
        }
    }

    /* JADX WARNING: type inference failed for: r1v1, types: [java.net.Socket, javax.net.ssl.SSLSocket] */
    /* JADX WARNING: type inference failed for: r2v1 */
    /* JADX WARNING: type inference failed for: r1v2 */
    /* JADX WARNING: type inference failed for: r2v2 */
    /* JADX WARNING: type inference failed for: r2v7 */
    /* JADX WARNING: type inference failed for: r1v5 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x011f A[Catch:{ all -> 0x0115 }] */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x0125 A[Catch:{ all -> 0x0115 }] */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x0128  */
    /* JADX WARNING: Unknown variable types count: 2 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void connectTls(okhttp3.internal.connection.ConnectionSpecSelector r8) throws java.io.IOException {
        /*
            r7 = this;
            okhttp3.Route r0 = r7.route
            okhttp3.Address r0 = r0.address()
            javax.net.ssl.SSLSocketFactory r1 = r0.sslSocketFactory()
            r2 = 0
            java.net.Socket r3 = r7.rawSocket     // Catch:{ AssertionError -> 0x0118 }
            okhttp3.HttpUrl r4 = r0.url()     // Catch:{ AssertionError -> 0x0118 }
            java.lang.String r4 = r4.host()     // Catch:{ AssertionError -> 0x0118 }
            okhttp3.HttpUrl r5 = r0.url()     // Catch:{ AssertionError -> 0x0118 }
            int r5 = r5.port()     // Catch:{ AssertionError -> 0x0118 }
            r6 = 1
            java.net.Socket r1 = r1.createSocket(r3, r4, r5, r6)     // Catch:{ AssertionError -> 0x0118 }
            javax.net.ssl.SSLSocket r1 = (javax.net.ssl.SSLSocket) r1     // Catch:{ AssertionError -> 0x0118 }
            okhttp3.ConnectionSpec r8 = r8.configureSecureSocket(r1)     // Catch:{ AssertionError -> 0x0112, all -> 0x0110 }
            boolean r3 = r8.supportsTlsExtensions()     // Catch:{ AssertionError -> 0x0112, all -> 0x0110 }
            if (r3 == 0) goto L_0x0041
            okhttp3.internal.platform.Platform r3 = okhttp3.internal.platform.Platform.get()     // Catch:{ AssertionError -> 0x0112, all -> 0x0110 }
            okhttp3.HttpUrl r4 = r0.url()     // Catch:{ AssertionError -> 0x0112, all -> 0x0110 }
            java.lang.String r4 = r4.host()     // Catch:{ AssertionError -> 0x0112, all -> 0x0110 }
            java.util.List r5 = r0.protocols()     // Catch:{ AssertionError -> 0x0112, all -> 0x0110 }
            r3.configureTlsExtensions(r1, r4, r5)     // Catch:{ AssertionError -> 0x0112, all -> 0x0110 }
        L_0x0041:
            r1.startHandshake()     // Catch:{ AssertionError -> 0x0112, all -> 0x0110 }
            javax.net.ssl.SSLSession r3 = r1.getSession()     // Catch:{ AssertionError -> 0x0112, all -> 0x0110 }
            boolean r4 = r7.isValid(r3)     // Catch:{ AssertionError -> 0x0112, all -> 0x0110 }
            if (r4 != 0) goto L_0x0056
            java.io.IOException r8 = new java.io.IOException     // Catch:{ AssertionError -> 0x0112, all -> 0x0110 }
            java.lang.String r0 = "a valid ssl session was not established"
            r8.<init>(r0)     // Catch:{ AssertionError -> 0x0112, all -> 0x0110 }
            throw r8     // Catch:{ AssertionError -> 0x0112, all -> 0x0110 }
        L_0x0056:
            okhttp3.Handshake r4 = okhttp3.Handshake.get(r3)     // Catch:{ AssertionError -> 0x0112, all -> 0x0110 }
            javax.net.ssl.HostnameVerifier r5 = r0.hostnameVerifier()     // Catch:{ AssertionError -> 0x0112, all -> 0x0110 }
            okhttp3.HttpUrl r6 = r0.url()     // Catch:{ AssertionError -> 0x0112, all -> 0x0110 }
            java.lang.String r6 = r6.host()     // Catch:{ AssertionError -> 0x0112, all -> 0x0110 }
            boolean r3 = r5.verify(r6, r3)     // Catch:{ AssertionError -> 0x0112, all -> 0x0110 }
            if (r3 != 0) goto L_0x00be
            java.util.List r8 = r4.peerCertificates()     // Catch:{ AssertionError -> 0x0112, all -> 0x0110 }
            r2 = 0
            java.lang.Object r8 = r8.get(r2)     // Catch:{ AssertionError -> 0x0112, all -> 0x0110 }
            java.security.cert.X509Certificate r8 = (java.security.cert.X509Certificate) r8     // Catch:{ AssertionError -> 0x0112, all -> 0x0110 }
            javax.net.ssl.SSLPeerUnverifiedException r2 = new javax.net.ssl.SSLPeerUnverifiedException     // Catch:{ AssertionError -> 0x0112, all -> 0x0110 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ AssertionError -> 0x0112, all -> 0x0110 }
            r3.<init>()     // Catch:{ AssertionError -> 0x0112, all -> 0x0110 }
            java.lang.String r4 = "Hostname "
            r3.append(r4)     // Catch:{ AssertionError -> 0x0112, all -> 0x0110 }
            okhttp3.HttpUrl r0 = r0.url()     // Catch:{ AssertionError -> 0x0112, all -> 0x0110 }
            java.lang.String r0 = r0.host()     // Catch:{ AssertionError -> 0x0112, all -> 0x0110 }
            r3.append(r0)     // Catch:{ AssertionError -> 0x0112, all -> 0x0110 }
            java.lang.String r0 = " not verified:\n    certificate: "
            r3.append(r0)     // Catch:{ AssertionError -> 0x0112, all -> 0x0110 }
            java.lang.String r0 = okhttp3.CertificatePinner.pin(r8)     // Catch:{ AssertionError -> 0x0112, all -> 0x0110 }
            r3.append(r0)     // Catch:{ AssertionError -> 0x0112, all -> 0x0110 }
            java.lang.String r0 = "\n    DN: "
            r3.append(r0)     // Catch:{ AssertionError -> 0x0112, all -> 0x0110 }
            java.security.Principal r0 = r8.getSubjectDN()     // Catch:{ AssertionError -> 0x0112, all -> 0x0110 }
            java.lang.String r0 = r0.getName()     // Catch:{ AssertionError -> 0x0112, all -> 0x0110 }
            r3.append(r0)     // Catch:{ AssertionError -> 0x0112, all -> 0x0110 }
            java.lang.String r0 = "\n    subjectAltNames: "
            r3.append(r0)     // Catch:{ AssertionError -> 0x0112, all -> 0x0110 }
            java.util.List r8 = okhttp3.internal.tls.OkHostnameVerifier.allSubjectAltNames(r8)     // Catch:{ AssertionError -> 0x0112, all -> 0x0110 }
            r3.append(r8)     // Catch:{ AssertionError -> 0x0112, all -> 0x0110 }
            java.lang.String r8 = r3.toString()     // Catch:{ AssertionError -> 0x0112, all -> 0x0110 }
            r2.<init>(r8)     // Catch:{ AssertionError -> 0x0112, all -> 0x0110 }
            throw r2     // Catch:{ AssertionError -> 0x0112, all -> 0x0110 }
        L_0x00be:
            okhttp3.CertificatePinner r3 = r0.certificatePinner()     // Catch:{ AssertionError -> 0x0112, all -> 0x0110 }
            okhttp3.HttpUrl r0 = r0.url()     // Catch:{ AssertionError -> 0x0112, all -> 0x0110 }
            java.lang.String r0 = r0.host()     // Catch:{ AssertionError -> 0x0112, all -> 0x0110 }
            java.util.List r5 = r4.peerCertificates()     // Catch:{ AssertionError -> 0x0112, all -> 0x0110 }
            r3.check(r0, r5)     // Catch:{ AssertionError -> 0x0112, all -> 0x0110 }
            boolean r8 = r8.supportsTlsExtensions()     // Catch:{ AssertionError -> 0x0112, all -> 0x0110 }
            if (r8 == 0) goto L_0x00df
            okhttp3.internal.platform.Platform r8 = okhttp3.internal.platform.Platform.get()     // Catch:{ AssertionError -> 0x0112, all -> 0x0110 }
            java.lang.String r2 = r8.getSelectedProtocol(r1)     // Catch:{ AssertionError -> 0x0112, all -> 0x0110 }
        L_0x00df:
            r7.socket = r1     // Catch:{ AssertionError -> 0x0112, all -> 0x0110 }
            java.net.Socket r8 = r7.socket     // Catch:{ AssertionError -> 0x0112, all -> 0x0110 }
            okio.Source r8 = okio.Okio.source(r8)     // Catch:{ AssertionError -> 0x0112, all -> 0x0110 }
            okio.BufferedSource r8 = okio.Okio.buffer(r8)     // Catch:{ AssertionError -> 0x0112, all -> 0x0110 }
            r7.source = r8     // Catch:{ AssertionError -> 0x0112, all -> 0x0110 }
            java.net.Socket r8 = r7.socket     // Catch:{ AssertionError -> 0x0112, all -> 0x0110 }
            okio.Sink r8 = okio.Okio.sink(r8)     // Catch:{ AssertionError -> 0x0112, all -> 0x0110 }
            okio.BufferedSink r8 = okio.Okio.buffer(r8)     // Catch:{ AssertionError -> 0x0112, all -> 0x0110 }
            r7.sink = r8     // Catch:{ AssertionError -> 0x0112, all -> 0x0110 }
            r7.handshake = r4     // Catch:{ AssertionError -> 0x0112, all -> 0x0110 }
            if (r2 == 0) goto L_0x0102
            okhttp3.Protocol r8 = okhttp3.Protocol.get(r2)     // Catch:{ AssertionError -> 0x0112, all -> 0x0110 }
            goto L_0x0104
        L_0x0102:
            okhttp3.Protocol r8 = okhttp3.Protocol.HTTP_1_1     // Catch:{ AssertionError -> 0x0112, all -> 0x0110 }
        L_0x0104:
            r7.protocol = r8     // Catch:{ AssertionError -> 0x0112, all -> 0x0110 }
            if (r1 == 0) goto L_0x010f
            okhttp3.internal.platform.Platform r8 = okhttp3.internal.platform.Platform.get()
            r8.afterHandshake(r1)
        L_0x010f:
            return
        L_0x0110:
            r8 = move-exception
            goto L_0x0126
        L_0x0112:
            r8 = move-exception
            r2 = r1
            goto L_0x0119
        L_0x0115:
            r8 = move-exception
            r1 = r2
            goto L_0x0126
        L_0x0118:
            r8 = move-exception
        L_0x0119:
            boolean r0 = okhttp3.internal.Util.isAndroidGetsocknameError(r8)     // Catch:{ all -> 0x0115 }
            if (r0 == 0) goto L_0x0125
            java.io.IOException r0 = new java.io.IOException     // Catch:{ all -> 0x0115 }
            r0.<init>(r8)     // Catch:{ all -> 0x0115 }
            throw r0     // Catch:{ all -> 0x0115 }
        L_0x0125:
            throw r8     // Catch:{ all -> 0x0115 }
        L_0x0126:
            if (r1 == 0) goto L_0x012f
            okhttp3.internal.platform.Platform r0 = okhttp3.internal.platform.Platform.get()
            r0.afterHandshake(r1)
        L_0x012f:
            okhttp3.internal.Util.closeQuietly(r1)
            throw r8
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.connection.RealConnection.connectTls(okhttp3.internal.connection.ConnectionSpecSelector):void");
    }

    private boolean isValid(SSLSession sSLSession) {
        return !"NONE".equals(sSLSession.getProtocol()) && !"SSL_NULL_WITH_NULL_NULL".equals(sSLSession.getCipherSuite());
    }

    private Request createTunnel(int i, int i2, Request request, HttpUrl httpUrl) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("CONNECT ");
        sb.append(Util.hostHeader(httpUrl, true));
        sb.append(" HTTP/1.1");
        String sb2 = sb.toString();
        while (true) {
            Http1Codec http1Codec = new Http1Codec(null, null, this.source, this.sink);
            this.source.timeout().timeout((long) i, TimeUnit.MILLISECONDS);
            this.sink.timeout().timeout((long) i2, TimeUnit.MILLISECONDS);
            http1Codec.writeRequest(request.headers(), sb2);
            http1Codec.finishRequest();
            Response build = http1Codec.readResponseHeaders(false).request(request).build();
            long contentLength = HttpHeaders.contentLength(build);
            if (contentLength == -1) {
                contentLength = 0;
            }
            Source newFixedLengthSource = http1Codec.newFixedLengthSource(contentLength);
            Util.skipAll(newFixedLengthSource, Integer.MAX_VALUE, TimeUnit.MILLISECONDS);
            newFixedLengthSource.close();
            int code = build.code();
            if (code != 200) {
                if (code != 407) {
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append("Unexpected response code for CONNECT: ");
                    sb3.append(build.code());
                    throw new IOException(sb3.toString());
                }
                Request authenticate = this.route.address().proxyAuthenticator().authenticate(this.route, build);
                if (authenticate == null) {
                    throw new IOException("Failed to authenticate with proxy");
                } else if ("close".equalsIgnoreCase(build.header("Connection"))) {
                    return authenticate;
                } else {
                    request = authenticate;
                }
            } else if (this.source.buffer().exhausted() && this.sink.buffer().exhausted()) {
                return null;
            } else {
                throw new IOException("TLS tunnel buffered too many bytes!");
            }
        }
    }

    private Request createTunnelRequest() {
        return new Request.Builder().url(this.route.address().url()).header("Host", Util.hostHeader(this.route.address().url(), true)).header("Proxy-Connection", "Keep-Alive").header("User-Agent", Version.userAgent()).build();
    }

    public boolean isEligible(Address address, Route route2) {
        if (this.allocations.size() >= this.allocationLimit || this.noNewStreams || !Internal.instance.equalsNonHost(this.route.address(), address)) {
            return false;
        }
        if (address.url().host().equals(route().address().url().host())) {
            return true;
        }
        if (this.http2Connection == null || route2 == null || route2.proxy().type() != Type.DIRECT || this.route.proxy().type() != Type.DIRECT || !this.route.socketAddress().equals(route2.socketAddress()) || route2.address().hostnameVerifier() != OkHostnameVerifier.INSTANCE || !supportsUrl(address.url())) {
            return false;
        }
        try {
            address.certificatePinner().check(address.url().host(), handshake().peerCertificates());
            return true;
        } catch (SSLPeerUnverifiedException unused) {
            return false;
        }
    }

    public boolean supportsUrl(HttpUrl httpUrl) {
        if (httpUrl.port() != this.route.address().url().port()) {
            return false;
        }
        boolean z = true;
        if (httpUrl.host().equals(this.route.address().url().host())) {
            return true;
        }
        if (this.handshake == null || !OkHostnameVerifier.INSTANCE.verify(httpUrl.host(), (X509Certificate) this.handshake.peerCertificates().get(0))) {
            z = false;
        }
        return z;
    }

    public HttpCodec newCodec(OkHttpClient okHttpClient, Chain chain, StreamAllocation streamAllocation) throws SocketException {
        if (this.http2Connection != null) {
            return new Http2Codec(okHttpClient, chain, streamAllocation, this.http2Connection);
        }
        this.socket.setSoTimeout(chain.readTimeoutMillis());
        this.source.timeout().timeout((long) chain.readTimeoutMillis(), TimeUnit.MILLISECONDS);
        this.sink.timeout().timeout((long) chain.writeTimeoutMillis(), TimeUnit.MILLISECONDS);
        return new Http1Codec(okHttpClient, streamAllocation, this.source, this.sink);
    }

    public Route route() {
        return this.route;
    }

    public void cancel() {
        Util.closeQuietly(this.rawSocket);
    }

    public Socket socket() {
        return this.socket;
    }

    public boolean isHealthy(boolean z) {
        int soTimeout;
        if (this.socket.isClosed() || this.socket.isInputShutdown() || this.socket.isOutputShutdown()) {
            return false;
        }
        if (this.http2Connection != null) {
            return !this.http2Connection.isShutdown();
        }
        if (z) {
            try {
                soTimeout = this.socket.getSoTimeout();
                this.socket.setSoTimeout(1);
                if (this.source.exhausted()) {
                    this.socket.setSoTimeout(soTimeout);
                    return false;
                }
                this.socket.setSoTimeout(soTimeout);
                return true;
            } catch (SocketTimeoutException unused) {
            } catch (IOException unused2) {
                return false;
            } catch (Throwable th) {
                this.socket.setSoTimeout(soTimeout);
                throw th;
            }
        }
        return true;
    }

    public void onStream(Http2Stream http2Stream) throws IOException {
        http2Stream.close(ErrorCode.REFUSED_STREAM);
    }

    public void onSettings(Http2Connection http2Connection2) {
        synchronized (this.connectionPool) {
            this.allocationLimit = http2Connection2.maxConcurrentStreams();
        }
    }

    public Handshake handshake() {
        return this.handshake;
    }

    public boolean isMultiplexed() {
        return this.http2Connection != null;
    }

    public Protocol protocol() {
        return this.protocol;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Connection{");
        sb.append(this.route.address().url().host());
        sb.append(":");
        sb.append(this.route.address().url().port());
        sb.append(", proxy=");
        sb.append(this.route.proxy());
        sb.append(" hostAddress=");
        sb.append(this.route.socketAddress());
        sb.append(" cipherSuite=");
        sb.append(this.handshake != null ? this.handshake.cipherSuite() : "none");
        sb.append(" protocol=");
        sb.append(this.protocol);
        sb.append('}');
        return sb.toString();
    }
}
